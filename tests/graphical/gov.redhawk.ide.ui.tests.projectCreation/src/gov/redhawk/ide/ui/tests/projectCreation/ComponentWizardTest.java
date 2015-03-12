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
package gov.redhawk.ide.ui.tests.projectCreation;

import java.io.File;
import java.io.IOException;

import gov.redhawk.ide.spd.internal.ui.editor.ComponentEditor;
import gov.redhawk.ide.swtbot.WaitForEditorCondition;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 */
public class ComponentWizardTest extends AbstractCreationWizardTest {

	@Override
	protected String getProjectType() {
		return "SCA Component Project";
	}

	@Test
	@Override
	public void testNonDefaultLocation() throws IOException {
		wizardBot.textWithLabel("&Project name:").setText("ProjectName");
		wizardBot.checkBox("Use default location").click();

		wizardBot.textWithLabel("&Location:").setText("Bad location");
		Assert.assertFalse(bot.button("Finish").isEnabled());

		File createdFolder = folder.newFolder("ProjectName");
		wizardBot.textWithLabel("&Location:").setText(createdFolder.getAbsolutePath());
		wizardBot.button("Next >").click();

		wizardBot.comboBoxWithLabel("Prog. Lang:").setSelection("Python");
		wizardBot.comboBoxWithLabel("Code Generator:").setSelection(0);
		wizardBot.button("Next >").click();

		SWTBotCombo templateCombo = wizardBot.comboBoxWithLabel("Template:");
		for (int i = 0; i < templateCombo.itemCount(); i++) {
			wizardBot.comboBoxWithLabel("Template:").setSelection(i);
			if (wizardBot.button("Finish").isEnabled()) {
				break;
			}
		}
		wizardBot.button("Finish").click();

		bot.waitUntil(new WaitForEditorCondition(), 30000, 500);

		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("ProjectName");
		IPath location = project.getLocation();
		Assert.assertEquals(createdFolder.getAbsolutePath(), location.toOSString());
	}

	protected void testProjectCreation(String name, String lang, String generator, String template) {
		wizardBot.textWithLabel("&Project name:").setText(name);
		wizardBot.button("Next >").click();

		wizardBot.comboBoxWithLabel("Prog. Lang:").setSelection(lang);
		if (generator != null) {
			wizardBot.comboBoxWithLabel("Code Generator:").setSelection(generator);
		}
		Assert.assertFalse(wizardBot.textWithLabel("ID:").getText().isEmpty());
		wizardBot.textWithLabel("ID:").setText("customImplID");
		Assert.assertFalse(wizardBot.textWithLabel("Description:").getText().isEmpty());
		wizardBot.textWithLabel("Description:").setText("custom description");
		wizardBot.button("Next >").click();

		if (template != null) {
			wizardBot.comboBoxWithLabel("Template:").setSelection(template);
		}
		Assert.assertFalse(wizardBot.textWithLabel("Output Directory:").getText().isEmpty());
		wizardBot.textWithLabel("Output Directory:").setText("customOutput");
		wizardBot.button("Finish").click();

		String baseFilename = getBaseFilename(name);
		// Ensure SPD file was created
		SWTBotView view = bot.viewById("org.eclipse.ui.navigator.ProjectExplorer");
		view.show();
		view.bot().tree().setFocus();
		view.bot().tree().getTreeItem(name).select();
		view.bot().tree().getTreeItem(name).expand();
		view.bot().tree().getTreeItem(name).getNode(baseFilename + ".spd.xml");

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
	public void testStubPythonCreation() {
		testProjectCreation("ComponentWizardTest01", "Python", "Stub Python Code Generator", "Pull Port Data");
	}

	@Test
	public void testStubCppCreation() {
		testProjectCreation("ComponentWizardTest01", "C++", "Stub C++ Code Generator", "Pull Port Data");
	}

	@Test
	public void testStubJavaCreation() {
		testProjectCreation("ComponentWizardTest01", "Java", "Stub Java Code Generator", "Pull Port Data (Base/Derived)");
	}

	@Test
	public void testBackNext() {
		wizardBot.textWithLabel("&Project name:").setText("ComponentWizardTest01");
		wizardBot.button("Next >").click();
		wizardBot.comboBoxWithLabel("Prog. Lang:").setSelection("Python");
		wizardBot.comboBoxWithLabel("Prog. Lang:").setSelection("Java");
		wizardBot.comboBoxWithLabel("Prog. Lang:").setSelection("C++");

		wizardBot.comboBoxWithLabel("Prog. Lang:").setSelection("Python");
		wizardBot.comboBoxWithLabel("Code Generator:").setSelection("Stub Python Code Generator");
		wizardBot.button("Next >").click();
		Assert.assertTrue(wizardBot.button("Finish").isEnabled());
		wizardBot.button("< Back").click();

		wizardBot.comboBoxWithLabel("Prog. Lang:").setSelection("Java");
		wizardBot.comboBoxWithLabel("Code Generator:").setSelection("Stub Java Code Generator");
		wizardBot.button("Next >").click();
		Assert.assertTrue(wizardBot.button("Finish").isEnabled());
		wizardBot.button("< Back").click();

		wizardBot.comboBoxWithLabel("Prog. Lang:").setSelection("C++");
		wizardBot.comboBoxWithLabel("Code Generator:").setSelection("Stub C++ Code Generator");
		wizardBot.button("Next >").click();
		Assert.assertTrue(wizardBot.button("Finish").isEnabled());
		wizardBot.button("< Back").click();

		wizardBot.comboBoxWithLabel("Prog. Lang:").setSelection("Python");
		wizardBot.comboBoxWithLabel("Code Generator:").setSelection("Stub Python Code Generator");
		wizardBot.button("Next >").click();
		Assert.assertTrue(wizardBot.button("Finish").isEnabled());

		wizardShell.close();
	}

	@Test
	public void testContributedPropertiesUI() {
		wizardBot.textWithLabel("&Project name:").setText("WizardTest03");
		wizardBot.button("Next >").click();
		wizardBot.comboBox().setSelection("Java");
		wizardBot.button("Next >").click();
		Assert.assertFalse(wizardBot.textWithLabel("Package:").getText().isEmpty());
		wizardBot.textWithLabel("Package:").setText("customPackageName");

		wizardShell.close();
	}

	/**
	 * IDE-1111: test creation of component with dots in project name
	 */
	@Test
	public void testNamespacedObjectCreation() {
		testProjectCreation("namespaced.component.IDE1111", "Python", null, null);
		Assert.assertEquals("customOutput/IDE1111.py", bot.activeEditor().bot().textWithLabel("Entry Point:").getText());
		verifyEditorTabPresent("IDE1111.prf.xml");
		verifyEditorTabPresent("IDE1111.scd.xml");
	}
	
	protected void verifyEditorTabPresent(String tabName) {
		Assert.assertNotNull(tabName + " editor tab is missing", bot.activeEditor().bot().cTabItem(tabName));
	}
	
}
