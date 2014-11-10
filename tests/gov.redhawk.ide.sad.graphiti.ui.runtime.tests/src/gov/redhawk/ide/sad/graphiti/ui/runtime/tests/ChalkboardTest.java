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
package gov.redhawk.ide.sad.graphiti.ui.runtime.tests;

import gov.redhawk.ide.sad.graphiti.ext.impl.ComponentShapeImpl;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.FindByUtils;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class ChalkboardTest extends AbstractGraphitiRuntimeTest {

	private SWTBotGefEditor editor;
	private static final String CHALKBOARD = "Chalkboard";
	private static final String HARD_LIMIT = "HardLimit";

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
		System.out.println("STEP 1");
		DiagramTestUtils.openChalkboardFromSandbox(gefBot);
		System.out.println("STEP 2");
		editor = gefBot.gefEditor(CHALKBOARD);
		System.out.println("STEP 3");
		editor.setFocus();

		// Add component to diagram from palette
		System.out.println("STEP 4");
		DiagramTestUtils.dragFromPaletteToDiagram(editor, HARD_LIMIT, 0, 0);
		System.out.println("STEP 5");
		assertHardLimit(editor.getEditPart(HARD_LIMIT));
		System.out.println("STEP 6");
		DiagramTestUtils.deleteFromDiagram(editor, editor.getEditPart(HARD_LIMIT));

		// Add component to diagram from Target SDR
		System.out.println("STEP 7");
		DiagramTestUtils.dragFromTargetSDRToDiagram(gefBot, editor, HARD_LIMIT);
		System.out.println("STEP 8");
		assertHardLimit(editor.getEditPart(HARD_LIMIT));
		
		// Open the chalkboard with components already launched
		System.out.println("STEP 9");
		editor.close();
		System.out.println("STEP 10");
		SWTBotView scaExplorerView = bot.viewById("gov.redhawk.ui.sca_explorer");
		System.out.println("STEP 11");
		scaExplorerView.setFocus();
		System.out.println("STEP 12");
		scaExplorerView.bot().tree().expandNode("Sandbox", "Chalkboard", HARD_LIMIT + "_1");
		System.out.println("STEP 13");
		DiagramTestUtils.openChalkboardFromSandbox(gefBot);
		System.out.println("STEP 14");
		editor = gefBot.gefEditor(CHALKBOARD);
		System.out.println("STEP 15");
		Assert.assertNotNull(editor.getEditPart(HARD_LIMIT));
		System.out.println("STEP 16");
	}

	/**
	 * IDE-928
	 * Check to make sure FindBy elements do not appear in the RHToolBar when in the Graphiti sandbox
	 */
	@Ignore
	@Test
	public void checkFindByNotInSandbox() {
		DiagramTestUtils.openChalkboardFromSandbox(gefBot);
		editor = gefBot.gefEditor(CHALKBOARD);
		editor.setFocus();
		String[] findByList = { FindByUtils.FIND_BY_NAME, FindByUtils.FIND_BY_DOMAIN_MANAGER, FindByUtils.FIND_BY_EVENT_CHANNEL,
			FindByUtils.FIND_BY_FILE_MANAGER, FindByUtils.FIND_BY_SERVICE };
		
		for (String findByType : findByList) {
			try {
				DiagramTestUtils.dragFromPaletteToDiagram(editor, findByType, 0, 0);
				Assert.fail(); // The only way to get here is if the FindBy type appears in the Palette
			} catch (WidgetNotFoundException e) {
				Assert.assertTrue(e.getMessage(), findByType.matches(".*" + e.getMessage() + ".*"));
			}
		}
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
