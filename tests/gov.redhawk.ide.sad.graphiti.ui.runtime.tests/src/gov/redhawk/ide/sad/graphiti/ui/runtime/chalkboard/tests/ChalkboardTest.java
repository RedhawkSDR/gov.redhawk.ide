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
package gov.redhawk.ide.sad.graphiti.ui.runtime.chalkboard.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.sad.ext.impl.ComponentShapeImpl;
import gov.redhawk.ide.swtbot.ComponentUtils;
import gov.redhawk.ide.swtbot.DeviceUtils;
import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.ServiceUtils;
import gov.redhawk.ide.swtbot.SoftpackageUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.FindByUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotLabel;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Assert;
import org.junit.Test;

public class ChalkboardTest extends AbstractGraphitiChalkboardTest {

	private static final String[] CHALKBOARD_PARENT_PATH = { "Sandbox" };
	private static final String CHALKBOARD = "Chalkboard";
	private static final String HARD_LIMIT = "HardLimit";
	private static final String HARD_LIMIT_1 = "HardLimit_1";
	private static final String SIGGEN = "SigGen";
	private static final String SIG_GEN_1 = "SigGen_1";
	private SWTBotGefEditor editor;

	/**
	 * IDE-884:
	 * Create the chalkboard waveform diagram.
	 * Add components to diagram from palette and TargetSDR,
	 * IDE-658:
	 * Open chalkboard with components already launched in the Sandbox,
	 * IDE-960:
	 * Show Console Feature
	 */
	@Test
	public void checkChalkboardComponents() {
		// Open Chalkboard Graphiti Diagram
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, CHALKBOARD_PARENT_PATH, CHALKBOARD, DiagramType.GRAPHITI_CHALKBOARD);
		editor = gefBot.gefEditor(CHALKBOARD);
		editor.setFocus();

		// Add component to diagram from palette
		DiagramTestUtils.dragFromPaletteToDiagram(editor, HARD_LIMIT, 0, 0);
		assertHardLimit(editor.getEditPart(HARD_LIMIT));
		DiagramTestUtils.releaseFromChalkboard(editor, editor.getEditPart(HARD_LIMIT));

		// Add component to diagram from Target SDR
		DiagramTestUtils.dragFromTargetSDRToDiagram(gefBot, editor, HARD_LIMIT);
		assertHardLimit(editor.getEditPart(HARD_LIMIT));

		// Open the chalkboard with components already launched
		editor.close();
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, CHALKBOARD_PARENT_PATH, CHALKBOARD, DiagramType.GRAPHITI_CHALKBOARD);
		editor = gefBot.gefEditor(CHALKBOARD);
		Assert.assertNotNull(editor.getEditPart(HARD_LIMIT));

		// Check 'Show Console' context menu option functionality
		DiagramTestUtils.dragFromPaletteToDiagram(editor, SIGGEN, 0, 0);
		String[] components = { HARD_LIMIT, SIGGEN };

		for (String component : components) {
			editor.getEditPart(component).click();
			editor.clickContextMenu("Show Console");
			bot.sleep(1000);
			final SWTBotView consoleView = gefBot.viewByPartName("Console");
			bot.waitUntil(new DefaultCondition() {

				@Override
				public boolean test() throws Exception {
					consoleView.bot().label(0);
					return true;
				}

				@Override
				public String getFailureMessage() {
					return "Console view label never loaded";
				}
			});
			SWTBotLabel consoleLabel = consoleView.bot().label(0);
			Assert.assertTrue("Console view for " + component + " did not display", consoleLabel.getText().matches(".*" + component + ".*"));
		}

	}

	/**
	 * IDE-928
	 * Check to make sure FindBy elements do not appear in the RHToolBar when in the Graphiti sandbox
	 */
	@Test
	public void checkFindByNotInSandbox() {
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, CHALKBOARD_PARENT_PATH, CHALKBOARD, DiagramType.GRAPHITI_CHALKBOARD);
		editor = gefBot.gefEditor(CHALKBOARD);
		editor.setFocus();
		String[] findByList = { FindByUtils.FIND_BY_NAME, FindByUtils.FIND_BY_DOMAIN_MANAGER, FindByUtils.FIND_BY_EVENT_CHANNEL,
			FindByUtils.FIND_BY_FILE_MANAGER, FindByUtils.FIND_BY_SERVICE };

		for (String findByType : findByList) {
			try {
				DiagramTestUtils.dragFromPaletteToDiagram(editor, findByType, 0, 0);
				Assert.fail(); // The only way to get here is if the FindBy type appears in the Palette
			} catch (WidgetNotFoundException e) {
				Assert.assertTrue(e.getMessage(), e.getMessage().matches(".*" + findByType + ".*"));
			}
		}
	}

	/**
	 * IDE-660 Chalkboard Palette contains Workspace Components
	 */
	@Test
	public void checkWorkspaceComponents() {
		// This must be done to ensure right shell is active for menu selection
		StandardTestActions.configurePyDev();
		bot.shell("SCA - REDHAWK IDE").activate();
		
		// create test Component in workspace
		final String wkspComponentName = "testComponentInWorkspace";
		ComponentUtils.createComponentProject(bot, wkspComponentName, "Python");

		// Open Chalkboard Graphiti Diagram
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, CHALKBOARD_PARENT_PATH, CHALKBOARD, DiagramType.GRAPHITI_CHALKBOARD);
		editor = gefBot.gefEditor(CHALKBOARD);
		editor.setFocus();

		// validate that workspace Component is in Chalkboard palette
		assertTrue("Workspace Component did not appear in Chalkboard Palette", isToolInPalette(editor, wkspComponentName));

		// cleanup
		editor.close();
		MenuUtils.deleteNodeInProjectExplorer(bot, wkspComponentName);
	}
	
	/**
	 * IDE-953
	 * Verifies that when the user drags a component to the diagram of a particular implementation
	 * that it in fact the correct implementation was added.
	 */
	@Test
	public void checkCorrectImplementationAddedToDiagram() {
		
		// Open Chalkboard Diagram
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, CHALKBOARD_PARENT_PATH, CHALKBOARD, DiagramType.GRAPHITI_CHALKBOARD);
		editor = gefBot.gefEditor(CHALKBOARD);
		editor.setFocus();

		// Add two components to diagram from palette
		final String sourceComponent = SIGGEN + " (python)";
		final String targetComponent = HARD_LIMIT + " (java)";
		DiagramTestUtils.dragFromPaletteToDiagram(editor, sourceComponent, 0, 0);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, targetComponent, 300, 0);

		//verify sigGen is python
		SWTBotGefEditPart sigGenEditPart = editor.getEditPart(SIGGEN);
		//get graphiti shape
		ComponentShapeImpl sigGenComponentShape = (ComponentShapeImpl) sigGenEditPart.part().getModel();
		// Grab the associated business object and confirm it is a SadComponentInstantiation
		SadComponentInstantiation sigGenSadComponentInstantiation = (SadComponentInstantiation) DUtil.getBusinessObject(sigGenComponentShape);
		Assert.assertEquals("SigGen implementation was not python", "python", sigGenSadComponentInstantiation.getImplID());
		
		//verify hardLimit is java
		SWTBotGefEditPart hardLimitEditPart = editor.getEditPart(HARD_LIMIT);
		//get graphiti shape
		ComponentShapeImpl hardLimitComponentShape = (ComponentShapeImpl) hardLimitEditPart.part().getModel();
		// Grab the associated business object and confirm it is a SadComponentInstantiation
		SadComponentInstantiation hardLimitSadComponentInstantiation = (SadComponentInstantiation) DUtil.getBusinessObject(hardLimitComponentShape);
		Assert.assertEquals("HardLimit implementation was not java", "java", hardLimitSadComponentInstantiation.getImplID());
		
	}

	/**
	 * Private helper method for {@link #checkComponentPictogramElements()} and
	 * {@link #checkComponentPictogramElementsWithAssemblyController()}.
	 * Asserts the given SWTBotGefEditPart is a HardLimit component and assembly controller
	 * @param gefEditPart
	 */
	private static void assertHardLimit(SWTBotGefEditPart gefEditPart) {
		Assert.assertNotNull(gefEditPart);
		// Drill down to graphiti component shape
		ComponentShapeImpl componentShape = (ComponentShapeImpl) gefEditPart.part().getModel();

		// Grab the associated business object and confirm it is a SadComponentInstantiation
		Object bo = DUtil.getBusinessObject(componentShape);
		Assert.assertTrue("business object should be of type SadComponentInstantiation", bo instanceof SadComponentInstantiation);
		SadComponentInstantiation ci = (SadComponentInstantiation) bo;

		// Run assertions on expected properties
		Assert.assertEquals("outer text should match component type", HARD_LIMIT, componentShape.getOuterText().getValue());
		Assert.assertEquals("inner text should match component usage name", ci.getUsageName(), componentShape.getInnerText().getValue());
		Assert.assertNotNull("component supported interface graphic should not be null", componentShape.getLollipop());
		Assert.assertNull("start order shape/text should be null", componentShape.getStartOrderText());
		// TODO
//		Assert.assertFalse("should not be assembly controller", ComponentUtils.isAssemblyController(componentShape));

		// HardLimit only has the two ports
		Assert.assertTrue(componentShape.getUsesPortStubs().size() == 1 && componentShape.getProvidesPortStubs().size() == 1);

		// Both ports are of type dataDouble
		Assert.assertEquals(componentShape.getUsesPortStubs().get(0).getUses().getInterface().getName(), "dataDouble");
		Assert.assertEquals(componentShape.getProvidesPortStubs().get(0).getProvides().getInterface().getName(), "dataDouble");
	}
	
	/**
	 * IDE-976
	 * Make sure devices, services, and softpackages are filtered out of Workspace compartment
	 */
	@Test
	public void checkNoWorkspaceDevicesServicesSoftpackages() {
		// This must be done to ensure right shell is active for menu selection
		StandardTestActions.configurePyDev();
		bot.shell("SCA - REDHAWK IDE").activate();

		// create test Service in workspace
		final String wkspServiceName = "testServiceInWorkspace";
		ServiceUtils.createServiceProject(bot, wkspServiceName, null, "Python");
		// create test Device in workspace
		final String wkspDeviceName = "testDeviceInWorkspace";
		DeviceUtils.createDeviceProject(bot, wkspDeviceName, "Python");
		// create test Softpackage in workspace
		final String wkspSftpkgName = "testSftpkgInWorkspace";
		SoftpackageUtils.createSoftpackageProject(bot, wkspSftpkgName, null);

		// Open Chalkboard Graphiti Diagram
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, CHALKBOARD_PARENT_PATH, CHALKBOARD, DiagramType.GRAPHITI_CHALKBOARD);
		editor = gefBot.gefEditor(CHALKBOARD);
		editor.setFocus();

		assertFalse("Workspace Service wrongly appeared in Chalkboard Palette", isToolInPalette(editor, wkspServiceName));
		assertFalse("Workspace Device wrongly appeared in Chalkboard Palette", isToolInPalette(editor, wkspDeviceName));
		assertFalse("Workspace Softpackage wrongly appeared in Chalkboard Palette", isToolInPalette(editor, wkspSftpkgName));

		// cleanup
		editor.close();
		MenuUtils.deleteNodeInProjectExplorer(bot, wkspServiceName);
		MenuUtils.deleteNodeInProjectExplorer(bot, wkspDeviceName);
		MenuUtils.deleteNodeInProjectExplorer(bot, wkspSftpkgName);
		
	}
	
	private boolean isToolInPalette(SWTBotGefEditor editor, String toolName) {
		try {
			editor.activateTool(toolName);
			return true;
		} catch (WidgetNotFoundException ex) {
			return false;
		}
		
	}
	
}
