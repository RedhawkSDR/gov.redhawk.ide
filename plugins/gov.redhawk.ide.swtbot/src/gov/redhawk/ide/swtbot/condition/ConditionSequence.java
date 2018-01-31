/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.swtbot.condition;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;

/**
 * This class can be used to efficiently handle a sequence of conditions, some of which are optional and may not occur.
 * The class reduces wait time because all conditions up to and including the next required condition are tested at the
 * same time, and if any condition tests true, all preceding optional conditions are discarded.
 * <p/>
 * Each condition has an associated timeout, and handler code which is run if the condition tests true. The order that
 * conditions are added to this class is the order that conditions are expected to occur.
 * <p/>
 * As an example, assume the following code:
 * <pre> {@code
 * seq = new ConditionSequence();
 * seq.addOptionalCondition(cond1, 1000, code1);
 * seq.addOptionalCondition(cond2, 2000, code2);
 * seq.addOptionalCondition(cond3, 3000, code3);
 * seq.addCondition(cond4, 5000, code4);
 * seq.run();
 * }</pre>
 * A valid sequence of events is as follows:
 * <ol>
 * <li>For the first 1000ms, all four conditions are tested</li>
 * <li>Since {@code cond1}'s time has elapsed (1000ms since start), it is discarded and will no longer be tested</li>
 * <li>500ms later, {@code cond2} tests true</li>
 * <li>{@code code2} is executed</li>
 * <li>For the next 3000ms, {@code cond3} and {@code cond4} are tested</li>
 * <li>Since {@code cond3}'s time has elapsed (3000ms since a previous condition tested true), it is discarded and will
 * no longer be tested</li>
 * <li>For the next 2000ms, {@code cond4} is tested. It will either test true, or a {@link TimeoutException} will be
 * thrown</li>
 * </ol>
 */
public class ConditionSequence {

	private class Condition {

		Condition(ICondition condition, boolean required, long timeout, Runnable code) {
			this.condition = condition;
			this.required = required;
			this.timeout = timeout;
			this.code = code;
		}

		private ICondition condition;
		private boolean required;
		private long timeout;
		private Runnable code;
		private boolean satisfied = false;
	}

	private List<Condition> conditions = new ArrayList<>();

	public ConditionSequence() {
	}

	/**
	 * Adds a condition to check. Conditions should be added in the order they are expected to occur. When a condition
	 * tests true, the associated code is executed.
	 * @param condition The condition to test
	 * @param timeout The maximum time in milliseconds to wait for this condition. Time is measured from the last
	 * condition that tested true (i.e. wait time resets with each condition that tests true)
	 * @param code The code to run after this condition tests true, or null for none
	 */
	public void addCondition(ICondition condition, long timeout, Runnable code) {
		conditions.add(new Condition(condition, true, timeout, code));
	}

	/**
	 * See {@link #addCondition(ICondition, long, Runnable)}.
	 * @param condition
	 * @param code
	 */
	public void addCondition(ICondition condition, Runnable code) {
		conditions.add(new Condition(condition, true, SWTBotPreferences.TIMEOUT, code));
	}

	/**
	 * Same as {@link #addCondition(ICondition, long, Runnable)}, but the condition does not have to test true. The
	 * condition will be skipped if a condition after it tests true first.
	 * @param condition
	 * @param timeout
	 * @param code
	 */
	public void addOptionalCondition(ICondition condition, long timeout, Runnable code) {
		conditions.add(new Condition(condition, false, timeout, code));
	}

	/**
	 * See {@link #addOptionalCondition(ICondition, long, Runnable)}.
	 * @param condition
	 * @param code
	 */
	public void addOptionalCondition(ICondition condition, Runnable code) {
		addOptionalCondition(condition, SWTBotPreferences.TIMEOUT, code);
	}

	public void run(SWTBot bot) throws TimeoutException {
		// Initialize conditions
		for (Condition condition : conditions) {
			condition.condition.init(bot);
		}

		// Starting state
		int conditionIndex = 0;
		long startTime = System.currentTimeMillis();
		long waitTime = conditions.get(conditionIndex).timeout;
		Boolean[] result = new Boolean[1];

		// Loop forever - we'll explicitly fail if we timeout
		while (true) {
			// While there's time left on the earliest possible condition
			while (System.currentTimeMillis() < (startTime + waitTime)) {
				// Test each condition in order. We skip earlier conditions that have expired / been satisfied.
				for (int index = conditionIndex; index < conditions.size(); index++) {
					final Condition condition = conditions.get(index);

					// Test the condition
					result[0] = Boolean.FALSE;
					SafeRunner.run(new SafeRunnable() {

						@Override
						public void run() throws Exception {
							result[0] = condition.condition.test();
						}

					});
					if (!result[0]) {
						// Only process further conditions if this one isn't required
						if (condition.required) {
							break;
						} else {
							continue;
						}
					}

					// Condition is true. Run its code (if any), discard earlier conditions.
					condition.satisfied = true;
					if (condition.code != null) {
						condition.code.run();
					}
					conditionIndex = index + 1;

					// If we're out of conditions, we're done
					if (conditionIndex == conditions.size()) {
						return;
					}

					// Restart the timer
					startTime = System.currentTimeMillis();
					waitTime = conditions.get(conditionIndex).timeout;
				}

				// Briefly sleep before the next iteration
				bot.sleep(SWTBotPreferences.DEFAULT_POLL_DELAY);
			}

			// The wait time on the earliest condition has expired. Prune expired conditions.
			while (conditionIndex < conditions.size()) {
				Condition condition = conditions.get(conditionIndex);
				if (condition.timeout <= waitTime) {
					// If it's required, we can't prune it. We have to fail.
					if (condition.required) {
						fail(conditionIndex);
					}
					conditionIndex++;
				} else {
					// This condition has a longer wait time
					waitTime = condition.timeout;
					break;
				}
			}

			// If all conditions are pruned, we're done
			if (conditionIndex == conditions.size()) {
				return;
			}
		}
	}

	private static final String FAIL_MSG_FORMAT = "Failed waiting for required condition %d (%s). Earlier conditions satisfied: [%s]";

	private void fail(int conditionIndex) {
		String earlierConditions = conditions.subList(0, conditionIndex) //
				.stream() //
				.map(condition -> String.valueOf(condition.satisfied)) //
				.collect(Collectors.joining(", "));
		String msg = String.format(FAIL_MSG_FORMAT, conditionIndex, conditions.get(conditionIndex).condition.getFailureMessage(), earlierConditions);
		throw new TimeoutException(msg);
	}
}
