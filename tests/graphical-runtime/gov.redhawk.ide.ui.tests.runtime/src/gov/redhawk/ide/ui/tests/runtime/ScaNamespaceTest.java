/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.ui.tests.runtime;

import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Test;

import gov.redhawk.ide.swtbot.ComponentUtils;
import gov.redhawk.ide.swtbot.DeviceUtils;
import gov.redhawk.ide.swtbot.NodeUtils;
import gov.redhawk.ide.swtbot.ProjectExplorerUtils;
import gov.redhawk.ide.swtbot.ServiceUtils;
import gov.redhawk.ide.swtbot.SharedLibraryUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

public class ScaNamespaceTest extends UIRuntimeTest{

	SWTBotEditor editor;

	/**
	 * IDE-1122
	 * Check that name-spaced objects are properly represented in the SCA Explorer
	 * Includes: components, devices, services, nodes, and waveforms
	 */
	@Test
	public void namespaceBehaviorTest() {
		final String prefix = "name.space.";
		final String language = "Python";
		
		final String componentBaseName = "component";
		final String deviceBaseName = "device";
		final String serviceBaseName = "service";
		final String serviceInterface = "IDL:BULKIO/dataDouble:1.0"; 
		final String sharedLibraryBaseName = "sharedLibrary";
		final String sharedLibraryType = "C++ Library";
		final String waveformBaseName = "waveform";
		final String nodeBaseName = "node";
		final String nodeDomain = "REDHAWK_DEV";
		
		
		// Create the different project types
		ComponentUtils.createComponentProject(bot, prefix + componentBaseName, language);
		generateProject(prefix + componentBaseName, componentBaseName + ".spd.xml");
		
		DeviceUtils.createDeviceProject(bot, prefix + deviceBaseName, language);
		generateProject(prefix + deviceBaseName, deviceBaseName + ".spd.xml");
		
		ServiceUtils.createServiceProject(bot, prefix + serviceBaseName, serviceInterface, language);
		generateProject(prefix + serviceBaseName, serviceBaseName + ".spd.xml");
		
		SharedLibraryUtils.createSharedLibraryProject(bot, prefix + sharedLibraryBaseName, sharedLibraryType);
		generateProject(prefix + sharedLibraryBaseName, sharedLibraryBaseName + ".spd.xml");
		
		WaveformUtils.createNewWaveform(bot, prefix + waveformBaseName);
		
		NodeUtils.createNewNodeProject(bot, prefix + nodeBaseName, nodeDomain);
		
		// Export all of the projects to the SCA Explorer
		exportProject(prefix + componentBaseName);
		exportProject(prefix + deviceBaseName);
		exportProject(prefix + serviceBaseName);
		exportProject(prefix + sharedLibraryBaseName);
		exportProject(prefix + waveformBaseName);
		exportProject(prefix + nodeBaseName);
		
		// Check that each project exists, as well as all containers
		checkExistsInScaAndRemove(new String[]{"Target SDR", "Components", "name", "space"}, componentBaseName);
		checkExistsInScaAndRemove(new String[]{"Target SDR", "Devices", "name", "space"}, deviceBaseName);
		checkExistsInScaAndRemove(new String[]{"Target SDR", "Services", "name", "space"}, serviceBaseName);
		checkExistsInScaAndRemove(new String[]{"Target SDR", "Shared Libraries", "name", "space"}, sharedLibraryBaseName);
		checkExistsInScaAndRemove(new String[]{"Target SDR", "Waveforms", "name", "space"}, waveformBaseName);
		checkExistsInScaAndRemove(new String[]{"Target SDR", "Nodes", "name", "space"}, nodeBaseName);
	}
	
	private void generateProject(String projectName, String fileName) {
		ProjectExplorerUtils.openProjectInEditor(bot, new String[] { projectName, fileName });
		editor = bot.editorByTitle(projectName);
		StandardTestActions.generateProject(bot, editor);
		bot.closeAllEditors();
	}
	
	private void exportProject(String projectName) {
		SWTBotTreeItem projectNode = ProjectExplorerUtils.selectNode(bot, projectName);
		projectNode.contextMenu("Export to SDR").click();
	}
	
	private void checkExistsInScaAndRemove(String[] scaPath, String projectName) {
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, scaPath, projectName);
		SWTBotTreeItem scaNode = ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, scaPath, projectName);
		SWTBotMenu deleteContext = scaNode.contextMenu("Delete");
		deleteContext.click();
		bot.button("Yes").click();
		SWTBotTreeItem amIHere;
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, scaPath, projectName);
	}
	
	/**
	 * IDE-1122
	 * Check that name-spaced components with similar but not identical paths display
	 * properly in the SCA Explorer
	 */
	@Test
	public void similarNamespaceTest() {
		
		// TODO: Create three components 
			// name.space.comp
			// name.space.deep.dive.comp
			// name.space.comp.realComp
		
		// TODO: Generate and export each component
		
		// TODO: Make sure components sure up in expected location
		
		// TODO: Delete each component, make sure it is removed, and make sure the other components are not affected
	}
}
