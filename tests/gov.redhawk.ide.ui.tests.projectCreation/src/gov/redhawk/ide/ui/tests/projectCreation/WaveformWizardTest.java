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
import java.net.URL;

import gov.redhawk.ide.swtbot.WaitForEditorCondition;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class WaveformWizardTest extends AbstractCreationWizardTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	public WaveformWizardTest() {
		super("SCA Waveform Project");
	}

	@Test
	public void testBasicCreate() {
		bot.textWithLabel("&Project name:").setText("WaveformProj01");
		bot.button("Finish").click();

		// Ensure SAD file was created
		SWTBotView view = bot.viewById("org.eclipse.ui.navigator.ProjectExplorer");
		view.show();
		view.bot().tree().setFocus();
		view.bot().tree().getTreeItem("WaveformProj01").select();
		view.bot().tree().getTreeItem("WaveformProj01").expand();
		view.bot().tree().getTreeItem("WaveformProj01").getNode("WaveformProj01.sad.xml");

		bot.waitUntil(new WaitForEditorCondition(), 30000, 500);

		SWTBotEditor editorBot = bot.activeEditor();
		editorBot.bot().cTabItem("Overview").activate();

		Assert.assertEquals("WaveformProj01", editorBot.bot().textWithLabel("Name:").getText());
	}

	@Test
	public void testCreateFromTemplate() throws IOException {
		bot.textWithLabel("&Project name:").setText("WaveformProj01");
		bot.radio("Use existing waveform as a template").click();
		bot.textWithLabel("SAD File:").setText("Bad location");
		Assert.assertFalse(bot.button("Finish").isEnabled());

		URL fileUrl = FileLocator.toFileURL(FileLocator.find(ProjectCreationActivator.getInstance().getBundle(), new Path(
			"sdr/dom/waveforms/ExampleWaveform01/ExampleWaveform01.sad.xml"), null));
		File file = new File(fileUrl.getPath());
		bot.textWithLabel("SAD File:").setText(file.getAbsolutePath());
		bot.button("Finish").click();

		// Ensure SAD file was created
		SWTBotView view = bot.viewById("org.eclipse.ui.navigator.ProjectExplorer");
		view.show();
		view.bot().tree().setFocus();
		view.bot().tree().getTreeItem("WaveformProj01").select();
		view.bot().tree().getTreeItem("WaveformProj01").expand();
		view.bot().tree().getTreeItem("WaveformProj01").getNode("WaveformProj01.sad.xml");

		bot.waitUntil(new WaitForEditorCondition(), 30000, 500);

		SWTBotEditor editorBot = bot.activeEditor();
		editorBot.bot().cTabItem("Overview").activate();

		Assert.assertEquals("WaveformProj01", editorBot.bot().textWithLabel("Name:").getText());
		Assert.assertNotEquals("DCE:64a7d543-7055-494d-936f-30225b3b283e", editorBot.bot().textWithLabel("ID:").getText());
	}

	@Test
	public void testNonDefaultLocation() throws IOException {
		bot.textWithLabel("&Project name:").setText("WaveformProj01");
		bot.checkBox("Use default location").click();

		bot.textWithLabel("&Location:").setText("Bad location");
		Assert.assertFalse(bot.button("Finish").isEnabled());

		File createdFolder = folder.newFolder("WaveformProj01");
		bot.textWithLabel("&Location:").setText(createdFolder.getAbsolutePath());
		bot.button("Finish").click();
		
		// Ensure SAD file was created
		SWTBotView view = bot.viewById("org.eclipse.ui.navigator.ProjectExplorer");
		view.show();
		view.bot().tree().setFocus();
		view.bot().tree().getTreeItem("WaveformProj01").select();
		view.bot().tree().getTreeItem("WaveformProj01").expand();
		view.bot().tree().getTreeItem("WaveformProj01").getNode("WaveformProj01.sad.xml");

		bot.waitUntil(new WaitForEditorCondition(), 30000, 500);
		
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("WaveformProj01");
		IPath location = project.getLocation();
		Assert.assertEquals(createdFolder.getAbsolutePath(), location.toOSString());
	}

}
