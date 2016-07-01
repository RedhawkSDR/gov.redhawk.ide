/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package gov.redhawk.ide.swtbot.condition;

import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;

/**
 * Used to wait for the UI thread to finish executing an {@link org.eclipse.jface.operation.IRunnableWithProgress
 * IRunnableWithProgress} in the SWT {@link ModalContext} class. This happens when UI code invokes a long-running
 * operation via {@link IRunnableContext#run(boolean, boolean, IRunnableWithProgress)} on a
 * {@link org.eclipse.jface.wizard.WizardDialog WizardDialog}, etc.
 * <p/>
 * Because conditions poll, it's not possible to know if the modal context hasn't started yet, or has already finished.
 * This code waits at least 2 seconds to see the modal context before it assumes it must already have run to
 * completion.
 */
public class WaitForModalContext extends DefaultCondition {

	private static final long MINIMUM_SEARCH_TIME = 2000;
	private long startTime = 0;
	private boolean found = false;

	@Override
	public void init(SWTBot bot) {
		super.init(bot);
		startTime = System.currentTimeMillis();
	}

	@Override
	public boolean test() throws Exception {
		StackTraceElement[] elements = bot.getDisplay().getThread().getStackTrace();
		for (StackTraceElement element : elements) {
			if (element.getClassName().startsWith("org.eclipse.jface.operation.ModalContext")) {
				found = true;
				return false;
			}
		}
		return found || (System.currentTimeMillis() - startTime > MINIMUM_SEARCH_TIME);
	}

	@Override
	public String getFailureMessage() {
		return "Modal context was still running";
	}
}
