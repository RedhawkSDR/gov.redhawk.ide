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

import gov.redhawk.ide.graphiti.sad.ext.impl.ComponentShapeImpl;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.FindByUtils;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.junit.Assert;
import org.junit.Test;

public class ChalkboardTest extends AbstractGraphitiChalkboardTest {

	private SWTBotGefEditor editor;

	/**
	 * IDE-884 Create the chalkboard waveform diagram. Add components to diagram from palette and TargetSDR.
	 * IDE-658 Open chalkboard with components already launched in the Sandbox.
	 * IDE-960 Show Console Feature.
	 */
	@Test
	public void checkChalkboardComponents() {
		editor = openChalkboardDiagram(gefBot);

		// Add component to diagram from palette
		DiagramTestUtils.dragFromPaletteToDiagram(editor, HARD_LIMIT, 0, 0);
		assertHardLimit(editor.getEditPart(HARD_LIMIT));
		DiagramTestUtils.releaseFromDiagram(editor, editor.getEditPart(HARD_LIMIT));

		// IDE-984 Make sure device cannot be added from Target SDR
		DiagramTestUtils.dragDeviceFromTargetSDRToDiagram(gefBot, editor, "GPP");
		Assert.assertNull("Unexpected device found in diagram", editor.getEditPart("GPP"));

		// Add component to diagram from Target SDR
		DiagramTestUtils.dragComponentFromTargetSDRToDiagram(gefBot, editor, HARD_LIMIT);
		assertHardLimit(editor.getEditPart(HARD_LIMIT));
		
		// Open the chalkboard with components already launched
		editor.close();
		editor = openChalkboardDiagram(gefBot);
		Assert.assertNotNull(editor.getEditPart(HARD_LIMIT));

		// Check 'Show Console' context menu option functionality
		DiagramTestUtils.dragFromPaletteToDiagram(editor, SIGGEN, 0, 0);
		String[] components = { HARD_LIMIT, SIGGEN };

		for (final String component : components) {
			editor.getEditPart(component).select();
			editor.clickContextMenu("Show Console");
			final String[] consoleLabelTexts = { null };
			bot.waitUntil(new DefaultCondition() {

				@Override
				public boolean test() throws Exception {
					SWTBotView consoleView = ViewUtils.getConsoleView(gefBot);
					String text = consoleView.bot().label(0).getText();
					if (text.matches(".*" + component + ".*")) {
						consoleLabelTexts[0] = text;
						return true;
					}
					return false;
				}

				@Override
				public String getFailureMessage() {
					return "Console view label never loaded for " + component;
				}
			});
			String consoleLabelText = consoleLabelTexts[0];
			Assert.assertNotNull("console label text for " + component, consoleLabelText);
			Assert.assertTrue("Console view for " + component + " did not display", consoleLabelText.matches(".*" + component + ".*"));
		}
	}

	/**
	 * IDE-928 Check to make sure FindBy elements do not appear in the RHToolBar when in the Graphiti sandbox
	 * IDE-124 Check to make sure UsesDevice tool does not appear in the Palette when in the Graphiti sandbox
	 */
	@Test
	public void checkNotInSandbox() {
		
		// Check for Find Bys
		editor = openChalkboardDiagram(gefBot);
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
		
		// Check for Uses Devices
		String usesDevice = "Use FrontEnd Tuner Device";
		try {
			DiagramTestUtils.dragFromPaletteToDiagram(editor, usesDevice, 0, 0);
			Assert.fail(); // The only way to get here is if the FindBy type appears in the Palette
		} catch (WidgetNotFoundException e) {
			Assert.assertTrue(e.getMessage(), e.getMessage().matches(".*" + usesDevice + ".*"));
		}
	}

	/**
	 * IDE-953
	 * Verifies that when the user drags a component to the diagram of a particular implementation
	 * that it in fact the correct implementation was added.
	 */
	@Test
	public void checkCorrectImplementationAddedToDiagram() {
		editor = openChalkboardDiagram(gefBot);

		// Add two components to diagram from palette
		final String sourceComponent = SIGGEN + " (python)";
		final String targetComponent = HARD_LIMIT + " (java)";
		DiagramTestUtils.dragFromPaletteToDiagram(editor, sourceComponent, 0, 0);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, targetComponent, 300, 0);

		// verify sigGen is python
		SWTBotGefEditPart sigGenEditPart = editor.getEditPart(SIGGEN);
		// get graphiti shape
		ComponentShapeImpl sigGenComponentShape = (ComponentShapeImpl) sigGenEditPart.part().getModel();
		// Grab the associated business object and confirm it is a SadComponentInstantiation
		SadComponentInstantiation sigGenSadComponentInstantiation = (SadComponentInstantiation) DUtil.getBusinessObject(sigGenComponentShape);
		Assert.assertEquals("SigGen implementation was not python", "python", sigGenSadComponentInstantiation.getImplID());

		// verify hardLimit is java
		SWTBotGefEditPart hardLimitEditPart = editor.getEditPart(HARD_LIMIT);
		// get graphiti shape
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
}
