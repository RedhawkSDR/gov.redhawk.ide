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
package gov.redhawk.ide.ui.tests.properties;

import gov.redhawk.ide.spd.internal.ui.editor.ComponentEditor;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.WaitForEditorCondition;
import gov.redhawk.prf.ui.editor.page.PropertiesFormPage;

import org.eclipse.core.runtime.Path;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.junit.After;
import org.junit.Before;

@SuppressWarnings("restriction")
public abstract class AbstractBasicTest extends UITest {
	
	protected SWTBotEditor editor;


	@Before
	public void before() throws Exception {
		super.before();
		
		StandardTestActions.importProject(PropertiesUITestsActivator.getInstance().getBundle(), new Path("workspace/PropTest_Comp"), null);
		bot.tree().getTreeItem("PropTest_Comp").select();
		bot.tree().getTreeItem("PropTest_Comp").expand();
		bot.tree().getTreeItem("PropTest_Comp").getNode("PropTest_Comp.spd.xml").doubleClick();
		
		bot.waitUntil(new WaitForEditorCondition(), 30000);
		
		editor = bot.activeEditor();
		editor.setFocus();
		SWTBot editorBot = editor.bot();
		editorBot.cTabItem("Properties").activate();
	}
	
	@After
	public void after() throws Exception {
		editor = null;
		bot.sleep(500);
		super.after();
	}

	protected void assertFormValid() {		
		ComponentEditor spdEditor = (ComponentEditor) editor.getReference().getEditor(false);

		PropertiesFormPage propertiesPage = spdEditor.getPropertiesPage();
		StandardTestActions.assertFormValid(bot, propertiesPage);
	}

	protected int getValidationState() {
		ComponentEditor spdEditor = (ComponentEditor) editor.getReference().getEditor(false);

		PropertiesFormPage propertiesPage = spdEditor.getPropertiesPage();
		return StandardTestActions.getValidationState(propertiesPage);
	}
	
	protected void waitForValidationState(final int ... states) {
		ComponentEditor spdEditor = (ComponentEditor) editor.getReference().getEditor(false);

		PropertiesFormPage propertiesPage = spdEditor.getPropertiesPage();
		StandardTestActions.waitForValidationState(bot, propertiesPage, states);
	}
	
	protected void assertFormInvalid() {
		ComponentEditor spdEditor = (ComponentEditor) editor.getReference().getEditor(false);

		PropertiesFormPage propertiesPage = spdEditor.getPropertiesPage();
		StandardTestActions.assertFormInvalid(bot, propertiesPage);
	}

}
