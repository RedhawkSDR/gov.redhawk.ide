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
package gov.redhawk.ide.sad.graphiti.ui.tests;

import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import mil.jpeojtrs.sca.partitioning.UsesDeviceStub;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.Assert;
import org.junit.Test;

public class UsesDeviceTest extends AbstractGraphitiTest {

	private SWTBotGefEditor editor;
	private String waveformName;
	private static final String USE_FRONTEND_TUNER_DEVICE = "Use FrontEnd Tuner Device";

	/**
	 * IDE-726
	 * Create the pictogram shape in the waveform diagram that represents the component business object.
	 * This includes the ContainerShape for the component, labels for Usage Name and ID, port shapes and labels,
	 * start order icon, and component supported interface.
	 */
	@Test
	public void checkUsesFrontEndTunerPictogramElements() {
		waveformName = "IDE-124-Test";

		// Create an empty waveform project
		WaveformUtils.createNewWaveform(gefBot, waveformName);
		editor = gefBot.gefEditor(waveformName);
		editor.setFocus();

		// Add component to diagram from palette
		DiagramTestUtils.dragUseFrontEndTunerDeviceToDiagram(gefBot, editor);
		
		SWTBotShell allocateTunerShell = gefBot.shell("Allocate Tuner");
		allocateTunerShell.setFocus();
		//click next, Generic FrontEnd Device already selected
		gefBot.button("Next").click();
		//stick with the default values
		gefBot.button("Next").click();
		//switch to Listen by id
		SWTBotCombo comboField = gefBot.comboBox("Allocation:");
		comboField.setFocus();
		comboField.setSelection("Listen to Existing Tuner by Id");
		//provide existing tuner allocation id
		SWTBotText existingTunerAllocationIdText = gefBot.textWithLabel("Existing Tuner Allocation ID");
		existingTunerAllocationIdText.setFocus();
		existingTunerAllocationIdText.typeText("12345");
		gefBot.button("Next").click();
		//add two uses and two provides ports
		//provides
		gefBot.textInGroup("Port(s) to use for connections", 0).setText("dataDouble");
		gefBot.button(0).click(); //add provides port
		gefBot.textInGroup("Port(s) to use for connections", 0).setText("dataFloat");
		gefBot.button(0).click(); //add provides port
		//uses
		gefBot.textInGroup("Port(s) to use for connections", 1).setText("dataDouble_out");
		gefBot.button(2).click(); //add uses port
		gefBot.textInGroup("Port(s) to use for connections", 1).setText("dataFloat_out");
		gefBot.button(2).click(); //add uses port
		//finish
		gefBot.button("Finish").click();
		
		editor.setFocus();

		// Confirm created component truly is Generic FrontEnd Tuner
		SWTBotGefEditPart frontEndTunerGefEditPart = editor.getEditPart(USE_FRONTEND_TUNER_DEVICE);
		assertFrontEndTuner(frontEndTunerGefEditPart);
		
		RHContainerShapeImpl rhContainerShape = (RHContainerShapeImpl) frontEndTunerGefEditPart.part().getModel();

		// two provides ports, two uses ports
		Assert.assertTrue(rhContainerShape.getUsesPortStubs().size() == 2 && rhContainerShape.getProvidesPortStubs().size() == 2);

		// Both ports are of type dataDouble
		Assert.assertEquals(rhContainerShape.getUsesPortStubs().get(0).getUses().getInterface().getName(), "dataDouble");
		Assert.assertEquals(rhContainerShape.getUsesPortStubs().get(1).getUses().getInterface().getName(), "dataFloat");
		Assert.assertEquals(rhContainerShape.getProvidesPortStubs().get(0).getProvides().getInterface().getName(), "dataDouble_out");
		Assert.assertEquals(rhContainerShape.getProvidesPortStubs().get(1).getProvides().getInterface().getName(), "dataFloat_out");
		
		// Check to see if SigGen is included in the sad.xml
		final String usesDeviceXML = DiagramTestUtils.regexStringForUseDevice((RHContainerShapeImpl) frontEndTunerGefEditPart.part().getModel());
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();
		Assert.assertTrue("The sad.xml should include UsesDevice", editorText.matches(usesDeviceXML));
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		
		//delete
		DiagramTestUtils.deleteFromDiagram(editor, editor.getEditPart(USE_FRONTEND_TUNER_DEVICE));
		
		//verify deleted
		Assert.assertNull(editor.getEditPart(USE_FRONTEND_TUNER_DEVICE));
	}


	/**
	 * Assert FrontEnd Tuner
	 * @param gefEditPart
	 */
	private static void assertFrontEndTuner(SWTBotGefEditPart gefEditPart) {
		Assert.assertNotNull(gefEditPart);
		// Drill down to graphiti component shape
		RHContainerShapeImpl rhContainerShape = (RHContainerShapeImpl) gefEditPart.part().getModel();

		// Grab the associated business object and confirm it is a UsesDeviceStub
		Object bo = DUtil.getBusinessObject(rhContainerShape);
		Assert.assertTrue("business object should be of type UsesDeviceStub", bo instanceof UsesDeviceStub);
		UsesDeviceStub usesDeviceStub = (UsesDeviceStub) bo;

		// Run assertions on expected properties
		Assert.assertEquals("outer text should match component type", USE_FRONTEND_TUNER_DEVICE, rhContainerShape.getOuterText().getValue());
		Assert.assertEquals("inner text should match component usage name", usesDeviceStub.getUsesDevice().getId(), rhContainerShape.getInnerText().getValue());
		Assert.assertNotNull("component supported interface graphic should not be null", rhContainerShape.getLollipop());

	}

}
