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
package gov.redhawk.ide.sad.graphiti.ui.runtime.local.tests;

import gov.redhawk.ide.sad.graphiti.ext.impl.ComponentShapeImpl;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.FindByUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.junit.Assert;
import org.junit.Test;

public class LocalWaveformRuntimeTest extends AbstractGraphitiLocalWaveformRuntimeTest {

	private SWTBotGefEditor editor;
	private static final String[] LOCAL_WAVEFORM_PARENT_PATH = {"Sandbox"};
	private static final String LOCAL_WAVEFORM = "ExampleWaveform01";
	private static final String HARD_LIMIT = "HardLimit";

	/**
	 * IDE-671
	 * Launch local waveform waveform diagram
	 * Verify existing component exists
	 * Add components to diagram from palette and TargetSDR
	 * Close diagram and re-open, verify newly added component exists
	 */
	@Test
	public void checkLocalWaveformComponents() {

		//Launch Local Waveform From Target SDR
		ScaExplorerTestUtils.launchWaveformFromTargetSDR(gefBot, LOCAL_WAVEFORM);
		
		//wait until local waveform appears in ScaExplorer Sandbox
		ScaExplorerTestUtils.waitUntilWaveformAppearsInScaExplorer(gefBot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM);
		
		// Open Local Waveform Diagram
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM);
		editor = gefBot.gefEditor(LOCAL_WAVEFORM);
		editor.setFocus();
		
		//verify existing component exists
		Assert.assertNotNull(editor.getEditPart("ExamplePythonComponent"));

		// Add component to diagram from palette
		DiagramTestUtils.dragFromPaletteToDiagram(editor, HARD_LIMIT, 0, 0);
		assertHardLimit(editor.getEditPart(HARD_LIMIT));
		DiagramTestUtils.deleteFromDiagram(editor, editor.getEditPart(HARD_LIMIT));

		// Add component to diagram from Target SDR
		DiagramTestUtils.dragFromTargetSDRToDiagram(gefBot, editor, HARD_LIMIT);
		assertHardLimit(editor.getEditPart(HARD_LIMIT));
		
		// Open the chalkboard with components already launched
		editor.close();
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM);
		editor = gefBot.gefEditor(LOCAL_WAVEFORM);
		Assert.assertNotNull(editor.getEditPart(HARD_LIMIT));
	}

	/**
	 * IDE-928
	 * Check to make sure FindBy elements do not appear in the RHToolBar when in the Graphiti sandbox
	 */
	@Test
	public void checkFindByNotInSandbox() {
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM);
		editor = gefBot.gefEditor(LOCAL_WAVEFORM);
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

		// HardLimit only has the two ports
		Assert.assertTrue(componentShape.getUsesPortStubs().size() == 1 && componentShape.getProvidesPortStubs().size() == 1);

		// Both ports are of type dataDouble
		Assert.assertEquals(componentShape.getUsesPortStubs().get(0).getUses().getInterface().getName(), "dataDouble");
		Assert.assertEquals(componentShape.getProvidesPortStubs().get(0).getProvides().getInterface().getName(), "dataDouble");
	}
}
