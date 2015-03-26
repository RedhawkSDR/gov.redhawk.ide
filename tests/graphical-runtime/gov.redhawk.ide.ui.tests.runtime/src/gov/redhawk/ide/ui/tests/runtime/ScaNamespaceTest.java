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

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Test;

public class ScaNamespaceTest extends UIRuntimeTest {

	private final String prefix = "name.space.";

	SWTBotEditor editor;

	/**
	 * IDE-1122, IDE-1182
	 * Check that name-spaced component projects can be created, generated and exported.
	 * They should also be represented in the SCA Explorer.
	 */
	@Test
	public void namespaceBehaviorComponents() {
		final String componentBaseName = "component";

		String projectName = prefix + "cpp." + componentBaseName;
		ComponentUtils.createComponentProject(bot, projectName, "C++");
		generateProject(projectName, componentBaseName + ".spd.xml");

		projectName = prefix + "java." + componentBaseName;
		ComponentUtils.createComponentProject(bot, projectName, "Java");
		generateProject(projectName, componentBaseName + ".spd.xml");

		projectName = prefix + "python." + componentBaseName;
		ComponentUtils.createComponentProject(bot, projectName, "Python");
		generateProject(projectName, componentBaseName + ".spd.xml");

		exportProject(prefix + "cpp." + componentBaseName);
		exportProject(prefix + "java." + componentBaseName);
		exportProject(prefix + "python." + componentBaseName);

		checkExistsInScaAndRemove(new String[] { "Target SDR", "Components", "name", "space", "cpp" }, componentBaseName);
		checkExistsInScaAndRemove(new String[] { "Target SDR", "Components", "name", "space", "java" }, componentBaseName);
		checkExistsInScaAndRemove(new String[] { "Target SDR", "Components", "name", "space", "python" }, componentBaseName);
	}

	/**
	 * IDE-1122
	 * Check that name-spaced device projects can be created, generated and exported.
	 * They should also be represented in the SCA Explorer.
	 */
	@Test
	public void namespaceBehaviorDevices() {
		final String deviceBaseName = "device";

		String projectName = prefix + "cpp." + deviceBaseName;
		DeviceUtils.createDeviceProject(bot, projectName, "C++");
		generateProject(projectName, deviceBaseName + ".spd.xml");

		projectName = prefix + "java." + deviceBaseName;
		DeviceUtils.createDeviceProject(bot, projectName, "Java");
		generateProject(projectName, deviceBaseName + ".spd.xml");

		projectName = prefix + "python." + deviceBaseName;
		DeviceUtils.createDeviceProject(bot, projectName, "Python");
		generateProject(projectName, deviceBaseName + ".spd.xml");

		exportProject(prefix + "cpp." + deviceBaseName);
		exportProject(prefix + "java." + deviceBaseName);
		exportProject(prefix + "python." + deviceBaseName);

		checkExistsInScaAndRemove(new String[] { "Target SDR", "Devices", "name", "space", "cpp" }, deviceBaseName);
		checkExistsInScaAndRemove(new String[] { "Target SDR", "Devices", "name", "space", "java" }, deviceBaseName);
		checkExistsInScaAndRemove(new String[] { "Target SDR", "Devices", "name", "space", "python" }, deviceBaseName);
	}

	/**
	 * IDE-1122
	 * Check that a name-spaced service project can be created, generated and exported.
	 * It should also be represented in the SCA Explorer.
	 */
	@Test
	public void namespaceBehaviorServices() {
		final String serviceBaseName = "service";
		final String serviceInterface = "IDL:BULKIO/dataDouble:1.0";

		String projectName = prefix + "cpp." + serviceBaseName;
		ServiceUtils.createServiceProject(bot, projectName, serviceInterface, "C++");
		generateProject(projectName, serviceBaseName + ".spd.xml");

		projectName = prefix + "java." + serviceBaseName;
		ServiceUtils.createServiceProject(bot, projectName, serviceInterface, "Java");
		generateProject(projectName, serviceBaseName + ".spd.xml");

		projectName = prefix + "python." + serviceBaseName;
		ServiceUtils.createServiceProject(bot, projectName, serviceInterface, "Python");
		generateProject(projectName, serviceBaseName + ".spd.xml");

		exportProject(prefix + "cpp." + serviceBaseName);
		exportProject(prefix + "java." + serviceBaseName);
		exportProject(prefix + "python." + serviceBaseName);

		checkExistsInScaAndRemove(new String[] { "Target SDR", "Services", "name", "space", "cpp" }, serviceBaseName);
		checkExistsInScaAndRemove(new String[] { "Target SDR", "Services", "name", "space", "java" }, serviceBaseName);
		checkExistsInScaAndRemove(new String[] { "Target SDR", "Services", "name", "space", "python" }, serviceBaseName);
	}

	/**
	 * IDE-1122
	 * Check that a name-spaced waveform project can be created and exported.
	 * It should also be represented in the SCA Explorer.
	 */
	@Test
	public void namespaceBehaviorWaveforms() {
		final String waveformBaseName = "waveform";

		WaveformUtils.createNewWaveform(bot, prefix + waveformBaseName);
		exportProject(prefix + waveformBaseName);
		checkExistsInScaAndRemove(new String[] { "Target SDR", "Waveforms", "name", "space" }, waveformBaseName);
	}

	/**
	 * IDE-1122
	 * Check that a name-spaced service project can be created and exported.
	 * It should also be represented in the SCA Explorer.
	 */
	@Test
	public void namespaceBehaviorNodes() {
		final String nodeBaseName = "node";
		final String nodeDomain = "REDHAWK_DEV";

		NodeUtils.createNewNodeProject(bot, prefix + nodeBaseName, nodeDomain);
		exportProject(prefix + nodeBaseName);
		checkExistsInScaAndRemove(new String[] { "Target SDR", "Nodes", "name", "space" }, nodeBaseName);
	}

	/**
	 * IDE-1122
	 * Check that a name-spaced shared library can be created, generated and exported.
	 * It should also be represented in the SCA Explorer.
	 */
	@Test
	public void namespaceBehaviorSharedLib() {
		final String sharedLibraryBaseName = "sharedLibrary";
		final String sharedLibraryType = "C++ Library";

		SharedLibraryUtils.createSharedLibraryProject(bot, prefix + sharedLibraryBaseName, sharedLibraryType);
		generateProject(prefix + sharedLibraryBaseName, sharedLibraryBaseName + ".spd.xml");
		exportProject(prefix + sharedLibraryBaseName);
		checkExistsInScaAndRemove(new String[] { "Target SDR", "Shared Libraries", "name", "space" }, sharedLibraryBaseName);
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
		ScaExplorerTestUtils.waitUntilNodeRemovedFromScaExplorer(bot, scaPath, projectName);
	}
}
