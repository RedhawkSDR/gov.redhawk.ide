/*******************************************************************************
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package gov.redhawk.ide.swtbot;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.ICondition;

/**
 * 
 */
public class WaitForEditorCondition implements ICondition {

	private SWTWorkbenchBot bot;

	@Override
	public boolean test() throws Exception {
		bot.activeEditor();
		return true;
	}

	@Override
	public void init(SWTBot bot) {
		this.bot = (SWTWorkbenchBot) bot;
	}

	@Override
	public String getFailureMessage() {
		return "no editor available";
	}

}
