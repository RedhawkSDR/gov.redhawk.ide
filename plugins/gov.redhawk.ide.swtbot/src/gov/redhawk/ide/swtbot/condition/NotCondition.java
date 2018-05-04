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

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.waits.ICondition;

/**
 * Inverts a condition's test
 */
public class NotCondition extends DefaultCondition {

	private ICondition invertCondition;

	public NotCondition(ICondition invertCondition) {
		this.invertCondition = invertCondition;
	}

	@Override
	public boolean test() throws Exception {
		return !invertCondition.test();
	}

	@Override
	public void init(SWTBot bot) {
		super.init(bot);
		invertCondition.init(bot);
	}

	@Override
	public String getFailureMessage() {
		return "NOT (" + invertCondition.getFailureMessage() + ")";
	}

	public static NotCondition not(ICondition invertCondition) {
		return new NotCondition(invertCondition);
	}

}
