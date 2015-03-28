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

import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import gov.redhawk.ide.swtbot.ComponentUtils;
import gov.redhawk.ide.swtbot.DeviceUtils;
import gov.redhawk.ide.swtbot.NodeUtils;
import gov.redhawk.ide.swtbot.ProjectExplorerUtils;
import gov.redhawk.ide.swtbot.ServiceUtils;
import gov.redhawk.ide.swtbot.SharedLibraryUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.condition.WaitForBuild;
import gov.redhawk.ide.swtbot.condition.WaitForLaunchTermination;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class ScaNamespaceTest extends UIRuntimeTest {

	private static final String PREFIX_DOTS = "runtime.test.";

	SWTBotEditor editor;

	/**
	 * IDE-1122, IDE-1182, IDE-1183, IDE-1185
	 * Check that name-spaced component projects can be created, generated and exported.
	 * They should also be represented in the SCA Explorer.
	 */
	@Test
	public void namespaceBehaviorComponents() {
		final String componentBaseName = "component";

		String projectName = PREFIX_DOTS + "cpp." + componentBaseName;
		ComponentUtils.createComponentProject(bot, projectName, "C++");
		generateProject(projectName, componentBaseName + ".cpp");
		bot.waitUntil(new WaitForBuild(), 30000);

		projectName = PREFIX_DOTS + "java." + componentBaseName;
		ComponentUtils.createComponentProject(bot, projectName, "Java");
		generateProject(projectName, componentBaseName + ".java");
		bot.waitUntil(new WaitForBuild(), 30000);

		projectName = PREFIX_DOTS + "python." + componentBaseName;
		ComponentUtils.createComponentProject(bot, projectName, "Python");
		generateProject(projectName, componentBaseName);
		bot.waitUntil(new WaitForBuild(), 30000);

		exportProject(PREFIX_DOTS + "cpp." + componentBaseName);
		exportProject(PREFIX_DOTS + "java." + componentBaseName);
		exportProject(PREFIX_DOTS + "python." + componentBaseName);
		bot.waitUntil(new WaitForLaunchTermination(), 30000);

		checkExistsInScaAndRemove(new String[] { "Target SDR", "Components", "runtime", "test", "cpp" }, componentBaseName);
		checkExistsInScaAndRemove(new String[] { "Target SDR", "Components", "runtime", "test", "java" }, componentBaseName);
		checkExistsInScaAndRemove(new String[] { "Target SDR", "Components", "runtime", "test", "python" }, componentBaseName);
	}

	/**
	 * IDE-1122, IDE-1182, IDE-1183, IDE-1185
	 * Check that name-spaced device projects can be created, generated and exported.
	 * They should also be represented in the SCA Explorer.
	 * @throws InterruptedException
	 * @throws OperationCanceledException
	 */
	@Test
	public void namespaceBehaviorDevices() throws OperationCanceledException, InterruptedException {
		final String deviceBaseName = "dev";

		String projectName = PREFIX_DOTS + "cpp." + deviceBaseName;
		DeviceUtils.createDeviceProject(bot, projectName, "C++");
		generateProject(projectName, deviceBaseName + ".cpp");
		bot.waitUntil(new WaitForBuild(), 30000);

		projectName = PREFIX_DOTS + "java." + deviceBaseName;
		DeviceUtils.createDeviceProject(bot, projectName, "Java");
		generateProject(projectName, deviceBaseName + ".java");
		bot.waitUntil(new WaitForBuild(), 30000);

		projectName = PREFIX_DOTS + "python." + deviceBaseName;
		DeviceUtils.createDeviceProject(bot, projectName, "Python");
		generateProject(projectName, deviceBaseName);
		bot.waitUntil(new WaitForBuild(), 30000);

		exportProject(PREFIX_DOTS + "cpp." + deviceBaseName);
		exportProject(PREFIX_DOTS + "java." + deviceBaseName);
		exportProject(PREFIX_DOTS + "python." + deviceBaseName);
		bot.waitUntil(new WaitForLaunchTermination(), 30000);

		checkExistsInScaAndRemove(new String[] { "Target SDR", "Devices", "runtime", "test", "cpp" }, deviceBaseName);
		checkExistsInScaAndRemove(new String[] { "Target SDR", "Devices", "runtime", "test", "java" }, deviceBaseName);
		checkExistsInScaAndRemove(new String[] { "Target SDR", "Devices", "runtime", "test", "python" }, deviceBaseName);
	}

	/**
	 * IDE-1122, IDE-1182, IDE-1183, IDE-1185
	 * Check that a name-spaced service project can be created, generated and exported.
	 * It should also be represented in the SCA Explorer.
	 */
	@Test
	public void namespaceBehaviorServices() {
		final String serviceBaseName = "service";
		final String serviceInterface = "IDL:BULKIO/dataDouble:1.0";

		String projectName = PREFIX_DOTS + "cpp." + serviceBaseName;
		ServiceUtils.createServiceProject(bot, projectName, serviceInterface, "C++");
		generateProject(projectName, serviceBaseName + ".cpp");
		bot.waitUntil(new WaitForBuild(), 30000);

		projectName = PREFIX_DOTS + "java." + serviceBaseName;
		ServiceUtils.createServiceProject(bot, projectName, serviceInterface, "Java");
		generateProject(projectName, serviceBaseName + ".java");
		bot.waitUntil(new WaitForBuild(), 30000);

		projectName = PREFIX_DOTS + "python." + serviceBaseName;
		ServiceUtils.createServiceProject(bot, projectName, serviceInterface, "Python");
		generateProject(projectName, serviceBaseName);
		bot.waitUntil(new WaitForBuild(), 30000);

		exportProject(PREFIX_DOTS + "cpp." + serviceBaseName);
		exportProject(PREFIX_DOTS + "java." + serviceBaseName);
		exportProject(PREFIX_DOTS + "python." + serviceBaseName);
		bot.waitUntil(new WaitForLaunchTermination(), 30000);

		checkExistsInScaAndRemove(new String[] { "Target SDR", "Services", "runtime", "test", "cpp" }, serviceBaseName);
		checkExistsInScaAndRemove(new String[] { "Target SDR", "Services", "runtime", "test", "java" }, serviceBaseName);
		checkExistsInScaAndRemove(new String[] { "Target SDR", "Services", "runtime", "test", "python" }, serviceBaseName);
	}

	/**
	 * IDE-1122, IDE-1128
	 * Check that a name-spaced waveform project can be created and exported.
	 * It should install to the correct location (we install it), and also be represented in the SCA Explorer.
	 */
	@Test
	public void namespaceBehaviorWaveforms() {
		final String waveformBaseName = "waveform";

		WaveformUtils.createNewWaveform(bot, PREFIX_DOTS + waveformBaseName);
		exportProject(PREFIX_DOTS + waveformBaseName);

		String[] scaPath = { "Target SDR", "Waveforms", "runtime", "test" };
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, scaPath, waveformBaseName);

		// Check that the directory and XML file exist in the appropriate location in the SDRROOT
		IPath waveformDir = SdrUiPlugin.getDefault().getTargetSdrDomPath().append("waveforms");
		for (String segment : PREFIX_DOTS.split("\\.")) {
			waveformDir = waveformDir.append(segment);
		}
		waveformDir = waveformDir.append(waveformBaseName);
		Assert.assertTrue("Directory for waveform doesn't exist in SDRROOT", waveformDir.toFile().exists());
		Assert.assertTrue("SAD XML for waveform doesn't exist in SDRROOT", waveformDir.append(waveformBaseName + ".sad.xml").toFile().exists());

		checkExistsInScaAndRemove(scaPath, waveformBaseName);
	}

	/**
	 * IDE-1122, IDE-1128
	 * Check that a name-spaced service project can be created and exported.
	 * It should install to the correct location (we install it), and also be represented in the SCA Explorer.
	 */
	@Test
	public void namespaceBehaviorNodes() {
		final String nodeBaseName = "node";
		final String nodeDomain = "REDHAWK_DEV";

		NodeUtils.createNewNodeProject(bot, PREFIX_DOTS + nodeBaseName, nodeDomain);
		exportProject(PREFIX_DOTS + nodeBaseName);

		String[] scaPath = { "Target SDR", "Nodes", "runtime", "test" };
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, scaPath, nodeBaseName);

		// Check that the directory and XML file exist in the appropriate location in the SDRROOT
		IPath nodeDir = SdrUiPlugin.getDefault().getTargetSdrDevPath().append("nodes");
		for (String segment : PREFIX_DOTS.split("\\.")) {
			if (segment.trim().length() > 0) {
				nodeDir = nodeDir.append(segment);
			}
		}
		nodeDir = nodeDir.append(nodeBaseName);
		Assert.assertTrue("Directory for node doesn't exist in SDRROOT", nodeDir.toFile().exists());
		Assert.assertTrue("DCD XML for node doesn't exist in SDRROOT", nodeDir.append("DeviceManager.dcd.xml").toFile().exists());

		checkExistsInScaAndRemove(scaPath, nodeBaseName);
	}

	/**
	 * IDE-1122, IDE-1182, IDE-1185
	 * Check that a name-spaced shared library can be created, generated and exported.
	 * It should also be represented in the SCA Explorer.
	 */
	@Ignore
	@Test
	public void namespaceBehaviorSharedLib() {
		final String sharedLibraryBaseName = "sharedLibrary";
		final String sharedLibraryType = "C++ Library";

		SharedLibraryUtils.createSharedLibraryProject(bot, PREFIX_DOTS + sharedLibraryBaseName, sharedLibraryType);
		generateProject(PREFIX_DOTS + sharedLibraryBaseName, sharedLibraryBaseName + ".cpp");
		bot.waitUntil(new WaitForBuild(), 30000);

		exportProject(PREFIX_DOTS + sharedLibraryBaseName);
		bot.waitUntil(new WaitForLaunchTermination(), 30000);

		checkExistsInScaAndRemove(new String[] { "Target SDR", "Shared Libraries", "runtime", "test" }, sharedLibraryBaseName);
	}

	private void generateProject(String projectName, String editorTabName) {
		// Generate
		editor = bot.editorByTitle(projectName);
		StandardTestActions.generateProject(bot, editor);

		// Default file editor should open
		bot.editorByTitle(editorTabName);

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
