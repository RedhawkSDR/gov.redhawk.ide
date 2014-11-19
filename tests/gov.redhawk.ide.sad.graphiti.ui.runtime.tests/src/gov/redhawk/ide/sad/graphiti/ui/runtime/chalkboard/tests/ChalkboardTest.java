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

import static org.junit.Assert.assertTrue;
import gov.redhawk.ide.sad.graphiti.ext.impl.ComponentShapeImpl;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.FindByUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Assert;
import org.junit.Test;

public class ChalkboardTest extends AbstractGraphitiChalkboardTest {

	private static final String[] CHALKBOARD_PARENT_PATH = {"Sandbox"};
	private static final String CHALKBOARD = "Chalkboard";
	private static final String HARD_LIMIT = "HardLimit";
	private SWTBotGefEditor editor;

	/**
	 * IDE-884
	 * Create the chalkboard waveform diagram.
	 * Add components to diagram from palette and TargetSDR
	 * IDE-658
	 * Open chalkboard with components already launched in the Sandbox
	 */
	@Test
	public void checkChalkboardComponents() {

		// Open Chalkboard Graphiti Diagram
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, CHALKBOARD_PARENT_PATH, CHALKBOARD);
		editor = gefBot.gefEditor(CHALKBOARD);
		editor.setFocus();

		// Add component to diagram from palette
		DiagramTestUtils.dragFromPaletteToDiagram(editor, HARD_LIMIT, 0, 0);
		assertHardLimit(editor.getEditPart(HARD_LIMIT));
		DiagramTestUtils.deleteFromDiagram(editor, editor.getEditPart(HARD_LIMIT));

		// Add component to diagram from Target SDR
		DiagramTestUtils.dragFromTargetSDRToDiagram(gefBot, editor, HARD_LIMIT);
		assertHardLimit(editor.getEditPart(HARD_LIMIT));

		// Open the chalkboard with components already launched
		editor.close();
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, CHALKBOARD_PARENT_PATH, CHALKBOARD);
		editor = gefBot.gefEditor(CHALKBOARD);
		Assert.assertNotNull(editor.getEditPart(HARD_LIMIT));
	}

	/**
	 * IDE-928
	 * Check to make sure FindBy elements do not appear in the RHToolBar when in the Graphiti sandbox
	 */
	@Test
	public void checkFindByNotInSandbox() {
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, CHALKBOARD_PARENT_PATH, CHALKBOARD);
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
		// create test Component in workspace
		final String wkspComponentName = "testComponentInWorkspace";
		bot.menu("File").menu("New").menu("SCA Component Project").click();

		SWTBotShell wizardShell = bot.shell("New Component Project");
		SWTBot wizardBot = wizardShell.activate().bot();
		wizardBot.textWithLabel("Project name:").setText(wkspComponentName);
		wizardBot.button("Next >").click();

		wizardBot.comboBoxWithLabel("Prog. Lang:").setSelection("Java");
		wizardBot.button("Next >").click();
		wizardBot.button("Finish").click();

		// Open Chalkboard Graphiti Diagram
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, CHALKBOARD_PARENT_PATH, CHALKBOARD);
		editor = gefBot.gefEditor(CHALKBOARD);
		editor.setFocus();

		// validate that workspace Component is in Chalkboard palette
		boolean isWkspCompInPalette;
		try {
			editor.activateTool(wkspComponentName);
			isWkspCompInPalette = true;
		} catch (WidgetNotFoundException ex) {
			isWkspCompInPalette = false;
		}
		assertTrue("found workspace Component in Chalkboard Palette", isWkspCompInPalette);

		// cleanup
		editor.close();
		MenuUtils.deleteNodeInProjectExplorer(bot, wkspComponentName);
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
}
