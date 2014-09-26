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
import gov.redhawk.prf.ui.editor.page.PropertiesFormPage;

import java.util.Arrays;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;

@SuppressWarnings("restriction")
@RunWith(SWTBotJunit4ClassRunner.class)
public abstract class AbstractBasicTest extends UITest {
	
	protected SWTBotEditor editor;


	@Before
	public void before() throws Exception {
		super.before();
		bot = new SWTWorkbenchBot();
		StandardTestActions.beforeTest(bot);
		
		StandardTestActions.clearWorkspace();
		StandardTestActions.importProject(PropertiesUITestsActivator.getInstance().getBundle(), new Path("workspace/PropTest_Comp"), null);
		bot.tree().getTreeItem("PropTest_Comp").select();
		bot.tree().getTreeItem("PropTest_Comp").expand();
		bot.tree().getTreeItem("PropTest_Comp").getNode("PropTest_Comp.spd.xml").doubleClick();
		
		bot.waitUntil(new ICondition() {

			@Override
			public boolean test() throws Exception {
				bot.activeEditor();
				return true;
			}

			@Override
			public void init(SWTBot bot) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public String getFailureMessage() {
				return "no editor available";
			}
			
		}, 30000);
		
		editor = bot.activeEditor();
		editor.setFocus();
		SWTBot editorBot = editor.bot();
		editorBot.cTabItem("Properties").activate();
	}
	
	@After
	public void after() throws Exception {
		editor = null;
		bot.sleep(500);
		super.afterTest();
	}

	protected void assertFormValid() {
		try {
			waitForValidationState(IMessageProvider.NONE, IMessageProvider.INFORMATION, IMessageProvider.WARNING);
		} catch (TimeoutException e) {
			Assert.fail("Form should be valid");
		}
	}

	protected int getValidationState() {
		ComponentEditor spdEditor = (ComponentEditor) editor.getReference().getEditor(false);

		PropertiesFormPage propertiesPage = spdEditor.getPropertiesPage();
		int messageType = propertiesPage.getManagedForm().getForm().getMessageType();
		return messageType;
	}
	
	protected void waitForValidationState(final int ... states) {
		bot.waitUntil(new ICondition() {

			@Override
			public boolean test() throws Exception {
				int current = getValidationState();
				for (int i : states) {
					if (i == current) {
						return true;
					}
				}
				return false;
			}

			@Override
			public void init(SWTBot bot) {
				
			}

			@Override
			public String getFailureMessage() {
				return "Failed waiting for validation state to change to: " + Arrays.toString(states);
			}
			
		}, 5000, 200);
	}
	
	protected void assertFormInvalid() {
		try {
			waitForValidationState(IMessageProvider.ERROR);
		} catch (TimeoutException e) {
			Assert.fail("Form should be valid");
		}
	}

}
