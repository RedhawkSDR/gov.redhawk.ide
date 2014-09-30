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
package gov.redhawk.ide.ui.tests.projectcreation;

import gov.redhawk.ide.spd.internal.ui.editor.ComponentEditor;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.WaitForEditorCondition;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class ComponentWizardTest extends UITest{
	private SWTBotShell wizardShell;
	private SWTBot wizardBot;

	private void testProjectCreation(String name, String lang, String generator, String template) {
		wizardBot.textWithLabel("&Project name:").setText(name);
		wizardBot.button("Next >").click();
		
		wizardBot.comboBoxWithLabel("Prog. Lang:").setSelection(lang);
		wizardBot.comboBoxWithLabel("Code Generator:").setSelection(generator);
		Assert.assertFalse(wizardBot.textWithLabel("ID:").getText().isEmpty());
		wizardBot.textWithLabel("ID:").setText("customImplID");
		Assert.assertFalse(wizardBot.textWithLabel("Description:").getText().isEmpty());
		wizardBot.textWithLabel("Description:").setText("custom description");
		wizardBot.button("Next >").click();
		
		wizardBot.comboBoxWithLabel("Template:").setSelection(template);
		Assert.assertFalse(wizardBot.textWithLabel("Output Directory:").getText().isEmpty());
		wizardBot.textWithLabel("Output Directory:").setText("customOutput");
		wizardBot.button("Finish").click();
		
		// Ensure SPD file was created
		SWTBotView view = bot.viewById("org.eclipse.ui.navigator.ProjectExplorer");
		view.show();
		view.bot().tree().setFocus();
		view.bot().tree().getTreeItem(name).select();
		view.bot().tree().getTreeItem(name).expand();
		view.bot().tree().getTreeItem(name).getNode(name + ".spd.xml");
		
		bot.waitUntil(new WaitForEditorCondition(), 30000, 500);
		
		// Ensure SPD Editor is open and has correct content
		SWTBotEditor editorBot = bot.activeEditor();
		Assert.assertTrue(editorBot.getReference().getEditor(true) instanceof ComponentEditor);
		
		Assert.assertEquals(name, editorBot.bot().textWithLabel("Name*:").getText());
		editorBot.bot().cTabItem("Implementations").activate();
		
		SWTBotTreeItem[] items = editorBot.bot().tree().getAllItems();
		Assert.assertEquals(1, editorBot.bot().tree().selectionCount());
		Assert.assertEquals(1, items.length);
		Assert.assertTrue(items[0].getText().matches("customImplID.*"));
		Assert.assertEquals("customImplID", editorBot.bot().textWithLabel("ID*:").getText());
		Assert.assertEquals(lang, editorBot.bot().textWithLabel("Prog. Lang:").getText());
		
		Assert.assertEquals("custom description", editorBot.bot().textWithLabel("Description:").getText());
		
		Assert.assertEquals("customOutput", editorBot.bot().textWithLabel("Output Dir:").getText());
		
	}
	
	@Test
	public void testPythonCreation() {
		testProjectCreation("ComponentWizardTest01", "Python", "Python Code Generator", "Pull Port Data");
	}
	
	@Test
	public void testCppCreation() {
		testProjectCreation("ComponentWizardTest01", "C++", "C++ Code Generator", "Pull Port Data");
	}
	
	@Test
	public void testJavaCreation() {
		testProjectCreation("ComponentWizardTest01", "Java", "Java Code Generator", "Pull Port Data (Base/Derived)");
	}
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		StandardTestActions.beforeClass();
		
		configurePyDev();
	}
	
	@Before
	@Override
	public void before() throws Exception {
		super.before();
		
		bot.menu("File").menu("New").menu("SCA Component Project").click();
		wizardShell = bot.activeShell();
		wizardBot = wizardShell.bot();
	}
	
	@After
	@Override
	public void afterTest() throws Exception {
		if (wizardShell != null) {
			wizardShell.close();
			wizardShell = null;
		}
		super.afterTest();
	}
	
	
	@Test
	public void testBackNext() {
		wizardBot.textWithLabel("&Project name:").setText("ComponentWizardTest01");
		wizardBot.button("Next >").click();
		wizardBot.comboBoxWithLabel("Prog. Lang:").setSelection("Python");
		wizardBot.comboBoxWithLabel("Prog. Lang:").setSelection("Java");
		wizardBot.comboBoxWithLabel("Prog. Lang:").setSelection("C++");
		
		wizardBot.comboBoxWithLabel("Prog. Lang:").setSelection("Python");
		wizardBot.comboBoxWithLabel("Code Generator:").setSelection("Python Code Generator");
		wizardBot.button("Next >").click();
		Assert.assertTrue(wizardBot.button("Finish").isEnabled());
		wizardBot.button("< Back").click();
		
		wizardBot.comboBoxWithLabel("Prog. Lang:").setSelection("Java");
		wizardBot.comboBoxWithLabel("Code Generator:").setSelection("Java Code Generator");
		wizardBot.button("Next >").click();
		Assert.assertTrue(wizardBot.button("Finish").isEnabled());
		wizardBot.button("< Back").click();
		
		wizardBot.comboBoxWithLabel("Prog. Lang:").setSelection("C++");
		wizardBot.comboBoxWithLabel("Code Generator:").setSelection("C++ Code Generator");
		wizardBot.button("Next >").click();
		Assert.assertTrue(wizardBot.button("Finish").isEnabled());
		wizardBot.button("< Back").click();
		
		wizardBot.comboBoxWithLabel("Prog. Lang:").setSelection("Python");
		wizardBot.comboBoxWithLabel("Code Generator:").setSelection("Python Code Generator");
		wizardBot.button("Next >").click();
		Assert.assertTrue(wizardBot.button("Finish").isEnabled());
	}
	
	@Test
	public void testUUID() {
		wizardBot.textWithLabel("&Project name:").setText("WizardTest02");
		Assert.assertTrue(wizardBot.button("Next >").isEnabled());
		
		wizardBot.radio("Provide an ID").click();
		Assert.assertFalse(wizardBot.button("Next >").isEnabled());
		
		wizardBot.textWithLabel("DCE UUID:").setText("187ca38e-ef38-487f-8f9b-935dca8595da");
		Assert.assertFalse(wizardBot.button("Next >").isEnabled());
		
		wizardBot.textWithLabel("DCE UUID:").setText("DCE");
		Assert.assertFalse(wizardBot.button("Next >").isEnabled());
		
		wizardBot.textWithLabel("DCE UUID:").setText("DCE:187ca38e-ef38-487f-8f9b-935dca8595dz");
		Assert.assertFalse(wizardBot.button("Next >").isEnabled());
		
		wizardBot.textWithLabel("DCE UUID:").setText("DCE");
		Assert.assertFalse(wizardBot.button("Next >").isEnabled());
		
		wizardBot.radio("Generate an ID").click();
		Assert.assertTrue(wizardBot.button("Next >").isEnabled());
		
		wizardBot.radio("Provide an ID").click();
		Assert.assertFalse(wizardBot.button("Next >").isEnabled());
		wizardBot.textWithLabel("DCE UUID:").setText("DCE:187ca38e-ef38-487f-8f9b-935dca8595da");
		Assert.assertTrue(wizardBot.button("Next >").isEnabled());
	}
	
	@Test
	public void testJavaPackage() {
		wizardBot.textWithLabel("&Project name:").setText("WizardTest03");
		wizardBot.button("Next >").click();
		wizardBot.comboBox().setSelection("Java");
		wizardBot.button("Next >").click();
		Assert.assertFalse(wizardBot.textWithLabel("Package:").getText().isEmpty());
		wizardBot.textWithLabel("Package:").setText("customPackageName");
	}

}
