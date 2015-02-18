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
package gov.redhawk.ide.graphiti.sad.ui.runtime.chalkboard.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.redhawk.ide.swtbot.ComponentUtils;
import gov.redhawk.ide.swtbot.DeviceUtils;
import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.ServiceUtils;
import gov.redhawk.ide.swtbot.SoftpackageUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.junit.BeforeClass;
import org.junit.Test;

public class ChalkboardWorkspaceTest extends AbstractGraphitiChalkboardTest {

	private static final String ROOT_SHELL_NAME = "SCA - Eclipse Platform";

	private SWTBotGefEditor editor;

	@BeforeClass
	public static void beforeClassSetup() {
		// PyDev needs to be configured before running New SCA * Project Wizards in some of the test cases
		StandardTestActions.configurePyDev();
	}

	/**
	 * IDE-660 Chalkboard Palette contains Workspace Components
	 */
	@Test
	public void checkHasWorkspaceComponents() {
		bot.shell(ROOT_SHELL_NAME).activate(); // This must be done to ensure right shell is active for menu selection
		
		// create test Component in workspace
		final String wkspComponentName = "testComponentInWorkspace";
		ComponentUtils.createComponentProject(bot, wkspComponentName, "Python");

		editor = openChalkboardDiagram(gefBot);

		// validate that workspace Component is in Chalkboard palette
		assertTrue("Workspace Component did not appear in Chalkboard Palette", isToolInPalette(editor, wkspComponentName));

		// cleanup
		editor.close();
		MenuUtils.deleteNodeInProjectExplorer(bot, wkspComponentName);
	}

	/**
	 * IDE-976 Make sure devices are filtered out of Palette's Workspace compartment
	 */
	@Test
	public void checkNoWorkspaceDevices() {
		bot.shell(ROOT_SHELL_NAME).activate(); // This must be done to ensure right shell is active for menu selection

		// create test Device in workspace
		final String wkspDeviceName = "testDeviceInWorkspace";
		DeviceUtils.createDeviceProject(bot, wkspDeviceName, "Python");

		editor = openChalkboardDiagram(gefBot);

		assertFalse("Workspace Device wrongly appeared in Chalkboard Palette", isToolInPalette(editor, wkspDeviceName));

		// cleanup
		editor.close();
		MenuUtils.deleteNodeInProjectExplorer(bot, wkspDeviceName);
	}

	/**
	 * IDE-976 Make sure services are filtered out of Palette's Workspace compartment
	 */
	@Test
	public void checkNoWorkspaceServices() {
		bot.shell(ROOT_SHELL_NAME).activate(); // This must be done to ensure right shell is active for menu selection

		// create test Service in workspace
		final String wkspServiceName = "testServiceInWorkspace";
		ServiceUtils.createServiceProject(bot, wkspServiceName, null, "Python");

		editor = openChalkboardDiagram(gefBot);

		assertFalse("Workspace Service wrongly appeared in Chalkboard Palette", isToolInPalette(editor, wkspServiceName));

		// cleanup
		editor.close();
		MenuUtils.deleteNodeInProjectExplorer(bot, wkspServiceName);
	}
	
	/**
	 * IDE-976 Make sure softpackages are filtered out of Palette's Workspace compartment
	 */
	@Test
	public void checkNoWorkspaceSoftpackages() {
		bot.shell(ROOT_SHELL_NAME).activate(); // This must be done to ensure right shell is active for menu selection

		// create test Softpackage in workspace
		final String wkspSftpkgName = "testSftpkgInWorkspace";
		SoftpackageUtils.createSoftpackageProject(bot, wkspSftpkgName, null);

		editor = openChalkboardDiagram(gefBot);

		assertFalse("Workspace Softpackage wrongly appeared in Chalkboard Palette", isToolInPalette(editor, wkspSftpkgName));

		// cleanup
		editor.close();
		MenuUtils.deleteNodeInProjectExplorer(bot, wkspSftpkgName);
	}

	private static boolean isToolInPalette(SWTBotGefEditor editor, String toolName) {
		try {
			editor.activateTool(toolName);
			return true;
		} catch (WidgetNotFoundException ex) {
			return false;
		}
	}

}
