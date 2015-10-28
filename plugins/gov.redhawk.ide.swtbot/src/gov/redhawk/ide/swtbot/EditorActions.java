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
package gov.redhawk.ide.swtbot;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;

public class EditorActions {

	private EditorActions() {
	}

	public static final String SPD_EDITOR_PROPERTIES_TAB_ID = "properties";

	/**
	 * Asserts that a tab in a form editor does not have validation errors (warnings are okay)
	 * @param editorBot
	 * @param tabId See constants in {@link EditorActions}
	 */
	@SuppressWarnings("deprecation")
	public static void assertEditorTabValid(SWTBotEditor editorBot, String tabId) {
		FormEditor formEditor = (FormEditor) editorBot.getReference().getEditor(false);
		IFormPage formPage = formEditor.findPage(tabId);
		StandardTestActions.assertFormValid(editorBot.bot(), formPage);
	}

	/**
	 * Asserts that a tab in a form editor has validation errors
	 * @param editorBot
	 * @param tabId See constants in {@link EditorActions}
	 */
	@SuppressWarnings("deprecation")
	public static void assertEditorTabInvalid(SWTBotEditor editorBot, String tabId) {
		FormEditor formEditor = (FormEditor) editorBot.getReference().getEditor(false);
		IFormPage formPage = formEditor.findPage(tabId);
		StandardTestActions.assertFormInvalid(editorBot.bot(), formPage);
	}

}
