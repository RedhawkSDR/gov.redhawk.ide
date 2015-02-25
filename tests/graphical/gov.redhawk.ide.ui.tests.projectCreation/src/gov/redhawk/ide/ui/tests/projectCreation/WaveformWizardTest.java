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

import gov.redhawk.ide.swtbot.WaitForEditorCondition;
import gov.redhawk.model.sca.util.ModelUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.junit.Assert;
import org.junit.Test;

public class WaveformWizardTest extends AbstractCreationWizardTest {

	@Override
	protected String getProjectType() {
		return "SCA Waveform Project";
	}

	@Test
	public void testBasicCreate() {
		testBasicCreate("WaveformProj01");
	}
	
	protected void testBasicCreate(String projectName) {
		bot.textWithLabel("&Project name:").setText(projectName);
		bot.button("Finish").click();

		String baseFilename = getBaseFilename(projectName);
		
		// Ensure SAD file was created
		SWTBotView view = bot.viewById("org.eclipse.ui.navigator.ProjectExplorer");
		view.show();
		view.bot().tree().setFocus();
		view.bot().tree().getTreeItem(projectName).select();
		view.bot().tree().getTreeItem(projectName).expand();
		view.bot().tree().getTreeItem(projectName).getNode(baseFilename + ".sad.xml");

		bot.waitUntil(new WaitForEditorCondition(), 30000, 500);

		SWTBotEditor editorBot = bot.activeEditor();
		editorBot.bot().cTabItem("Overview").activate();

		Assert.assertEquals(projectName, editorBot.bot().textWithLabel("Name:").getText());
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
	public void testSelectAssemblyController() throws IOException {
		bot.textWithLabel("&Project name:").setText("WaveformProj01");
		bot.button("Next >").click();

		bot.activeShell().bot().table().select("SigGen (/components/SigGen/)");
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

		URI uri = URI.createPlatformResourceURI("/WaveformProj01/WaveformProj01.sad.xml", true);
		SoftwareAssembly sad = ModelUtil.loadSoftwareAssembly(uri);
		Assert.assertEquals(1, sad.getPartitioning().getComponentPlacement().size());
		Assert.assertEquals(1, sad.getPartitioning().getComponentPlacement().get(0).getComponentInstantiation().size());
		Assert.assertEquals("SigGen_1", sad.getPartitioning().getComponentPlacement().get(0).getComponentInstantiation().get(0).getUsageName());
		Assert.assertEquals(sad.getPartitioning().getComponentPlacement().get(0).getComponentInstantiation().get(0),
			sad.getAssemblyController().getComponentInstantiationRef().getInstantiation());
	}

	/**
	 * IDE-1111: Test creation of waveform with dots in the name
	 */
	@Test
	public void testNamespacedWaveformCreation() {
		testBasicCreate("namespaced.waveform.IDE1111");
	}
	
}
