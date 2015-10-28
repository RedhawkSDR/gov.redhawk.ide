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
package gov.redhawk.ide.swtbot.condition;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.ICondition;

/**
 * Waits until there is an active editor (any). If you need to wait for a specific editor, just use
 * {@link SWTWorkbenchBot#editorByTitle(String)}. It will wait for the editor to open.
 */
public class WaitForEditorCondition implements ICondition {

	private SWTWorkbenchBot bot;

	/**
	 * Use this constant with this class when calling {@link SWTBot#waitUntil(ICondition, long)}
	 */
	public static final long DEFAULT_WAIT_FOR_EDITOR_TIME = 30000;

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
