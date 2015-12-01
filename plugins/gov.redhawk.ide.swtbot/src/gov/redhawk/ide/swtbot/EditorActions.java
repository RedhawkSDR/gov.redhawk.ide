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

import java.util.Arrays;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.junit.Assert;

public class EditorActions {

	private EditorActions() {
	}

	public static final String DCD_EDITOR_OVERVIEW_TAB_ID = "nodeOverview";
	public static final String SAD_EDITOR_OVERVIEW_TAB_ID = "sadEditorOverviewPage";
	public static final String SPD_EDITOR_OVERVIEW_TAB_ID = "componentOverview";
	public static final String SPD_EDITOR_PROPERTIES_TAB_ID = "properties";
	public static final String SPD_EDITOR_PORTS_TAB_ID = "ports";

	/**
	 * Asserts that a tab in a form editor does not have validation errors (warnings are okay)
	 * @param editorBot
	 * @param tabId See constants in {@link EditorActions}
	 */
	public static void assertEditorTabValid(SWTBotEditor editorBot, String tabId) {
		try {
			waitForValidationState(editorBot, tabId, IMessageProvider.NONE, IMessageProvider.INFORMATION, IMessageProvider.WARNING);
		} catch (TimeoutException e) {
			Assert.fail("Form should be valid: " + e.toString());
		}
	}

	/**
	 * Asserts that a tab in a form editor has validation errors
	 * @param editorBot
	 * @param tabId See constants in {@link EditorActions}
	 */
	public static void assertEditorTabInvalid(SWTBotEditor editorBot, String tabId) {
		try {
			waitForValidationState(editorBot, tabId, IMessageProvider.ERROR);
		} catch (TimeoutException e) {
			Assert.fail("Form should be invalid: " + e.toString());
		}
	}

	private static void waitForValidationState(SWTBotEditor editorBot, String tabId, final int... states) {
		FormEditor formEditor = (FormEditor) editorBot.getReference().getEditor(false);
		IFormPage formPage = formEditor.findPage(tabId);
		Assert.assertNotNull(String.format("Editor tab with ID '%s' not found", tabId), formPage);
		final IManagedForm managedForm = formPage.getManagedForm();
		Assert.assertNotNull("Managed form for page is null. The page has never been activated, and is likely not the active page.", managedForm);

		editorBot.bot().waitUntil(new DefaultCondition() {
			@Override
			public boolean test() throws Exception {
				int messageType = managedForm.getForm().getMessageType();
				for (int state : states) {
					if (state == messageType) {
						return true;
					}
				}
				return false;
			}

			@Override
			public String getFailureMessage() {
				return "Failed waiting for validation state to change to: " + Arrays.toString(states);
			}

		});
	}

}
