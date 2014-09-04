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

import gov.redhawk.ide.sad.graphiti.ext.impl.ComponentShapeImpl;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.HostCollocationPattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.ComponentUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import mil.jpeojtrs.sca.sad.HostCollocation;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadComponentPlacement;

import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public class WaveformComponentTests extends AbstractGraphitiTest {

	private SWTBotGefEditor editor;
	private String waveformName;
	private static final String HARD_LIMIT = "HardLimit";
	private static final String[] COMPONENTS = { "DataConverter", "HardLimit", "SigGen" };

	/**
	 * IDE-726
	 * Create the pictogram shape in the waveform diagram that represents the component business object.
	 * This includes the ContainerShape for the component, labels for Usage Name and ID, port shapes and labels,
	 * start order icon, and component supported interface.
	 */
	@Test
	public void checkComponentPictogramElements() {
		waveformName = "IDE-726-Test";

		// Create an empty waveform project
		WaveformUtils.createNewWaveform(gefBot, waveformName);
		editor = gefBot.gefEditor(waveformName);
		editor.setFocus();

		// Add component to diagram from palette
		DiagramTestUtils.dragFromPaletteToDiagram(editor, HARD_LIMIT, 0, 0);

		// Confirm created component truly is HardLimit
		assertHardLimit(editor.getEditPart(HARD_LIMIT));
		DiagramTestUtils.deleteFromDiagram(editor, editor.getEditPart(HARD_LIMIT));

		// Add component to diagram from Target SDR
		DiagramTestUtils.dragFromTargetSDRToDiagram(gefBot, editor, HARD_LIMIT);

		// Confirm created component truly is HardLimit
		assertHardLimit(editor.getEditPart(HARD_LIMIT));
	}

	/**
	 * IDE-669
	 * Components are removed with the delete button (trashcan image) that appears when you select the component,
	 * but the delete context menu does not remove the component from the diagram. In most cases, the delete and
	 * remove context menu options are grayed out and not selectable.
	 */
	@Test
	public void checkComponentContextMenuDelete() {
		waveformName = "IDE-669-Test";
		// Create an empty waveform project
		WaveformUtils.createNewWaveform(gefBot, waveformName);
		editor = gefBot.gefEditor(waveformName);

		for (String s : COMPONENTS) {
			// Add component to diagram from palette
			DiagramTestUtils.dragFromPaletteToDiagram(editor, s, 0, 0);
		}

		gefBot.menu("File").menu("Save").click();

		for (String s : COMPONENTS) {
			// Drill down to graphiti component shape
			SWTBotGefEditPart gefEditPart = editor.getEditPart(s);
			DiagramTestUtils.deleteFromDiagram(editor, gefEditPart);
			Assert.assertNull(editor.getEditPart(s));
		}
	}
	
	/**
	 * IDE-881
	 * Ensure components that are located within host collocation are deleted both in the diagram and model when
	 * clicking the delete action (trashcan)
	 */
	@Test
	public void checkComponentInHostCollocationContextMenuDelete() {
		waveformName = "HC_Pictogram";
		final String HOST_CO = "Host Collocation";
		final String HOST_CO_NAME = "HC1";
		
		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName);
		editor = gefBot.gefEditor(waveformName);
		
		// Add host collocation to the waveform
		DiagramTestUtils.dragFromPaletteToDiagram(editor, HOST_CO, 0, 0);
		SWTBotShell hostCoShell = gefBot.shell("New " + HOST_CO);
		hostCoShell.setFocus();
		SWTBotText hostCoName = gefBot.textWithLabel(HOST_CO + ":");
		hostCoName.setFocus();
		hostCoName.typeText(HOST_CO_NAME);
		gefBot.button("OK").click();
		
		// Add component to the host collocation
		editor.setFocus();
		DiagramTestUtils.dragFromPaletteToDiagram(editor, HARD_LIMIT, 20, 20);
		
		MenuUtils.save(gefBot);
		
		// Check pictogram elements
		SWTBotGefEditPart hostCoEditPart = editor.getEditPart(HOST_CO_NAME);
		Assert.assertNotNull(hostCoEditPart);
		ContainerShape hostCollocationContainerShape = (ContainerShape) hostCoEditPart.part().getModel();
		String shapeType = Graphiti.getPeService().getPropertyValue(hostCollocationContainerShape, DUtil.SHAPE_TYPE);
		Assert.assertTrue("Host Collocation property is missing or wrong", shapeType.equals(HostCollocationPattern.HOST_COLLOCATION_OUTER_CONTAINER_SHAPE));
		
		// Check model object values
		Object bo = DUtil.getBusinessObject(hostCollocationContainerShape);
		Assert.assertTrue("Business object should be instance of HostCollocation", bo instanceof HostCollocation);
		
		HostCollocation hostCo = (HostCollocation) bo;
		EList<SadComponentPlacement> components = hostCo.getComponentPlacement();
		Assert.assertEquals("Expected component \'" + HARD_LIMIT + "\' was not found", HARD_LIMIT + "_1",
			components.get(0).getComponentInstantiation().get(0).getId());
		
		//delete  component
		SWTBotGefEditPart gefEditPart = editor.getEditPart(HARD_LIMIT);
		DiagramTestUtils.deleteFromDiagram(editor, gefEditPart);
		//ensure component shape is deleted
		Assert.assertNull(editor.getEditPart(HARD_LIMIT));
		//ensure component business object is deleted
		Assert.assertTrue("Expected component \'" + HARD_LIMIT + "\' exists",
				hostCo.getComponentPlacement().size() < 1);

	}

	/**
	 * IDE-728
	 * Components selected in the diagram should have the properties of their corresponding
	 * model objects correctly exposed in the default Eclipse properties view.
	 */
	@Test
	public void checkChangesToPropertiesReflectedInSad() {
		waveformName = "IDE-728-Test";

		WaveformUtils.createNewWaveformWithAssemblyController(gefBot, waveformName, HARD_LIMIT);
		editor = gefBot.gefEditor(waveformName);
		editor.getEditPart(HARD_LIMIT).click();
		MenuUtils.showView(gefBot, "org.eclipse.ui.views.PropertySheet");
		String propertyname = gefBot.viewByTitle("Properties").bot().tree().cell(0, "Property").toString();
		String newValue = "0.0";
		for (SWTBotTreeItem item : gefBot.viewByTitle("Properties").bot().tree().getAllItems()) {
			if (item.getText().equals(propertyname)) {
				item.click(1).pressShortcut(Keystrokes.create('0')[0]);
				break;
			}
		}
		editor.getEditPart(HARD_LIMIT).click();
		gefBot.menu("File").menu("Save").click();
		String regex = DiagramTestUtils.regexStringForSadProperty((ComponentShapeImpl) editor.getEditPart(HARD_LIMIT).part().getModel(), propertyname, newValue);
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();
		Assert.assertTrue("The sad.xml should include HardLimit's changed property", editorText.matches(regex));
	}

	/**
	 * IDE-729
	 * New components should be added to sad.xml when the diagram is saved. All edits to components
	 * (such as changes to the usage name) should also be reflected in the sad.xml on save.
	 */
	@Test
	public void checkComponentsInSad() {
		waveformName = "IDE-729-Test";

		WaveformUtils.createNewWaveform(gefBot, waveformName);
		editor = gefBot.gefEditor(waveformName);

		// Add a SigGen component instantiation to the diagram and save
		DiagramTestUtils.dragFromPaletteToDiagram(editor, "SigGen", 0, 0);
		gefBot.menu("File").menu("Save").click();

		// Add a HardLimit component instantiation to the diagram
		DiagramTestUtils.dragFromPaletteToDiagram(editor, "HardLimit", 0, 0);

		// Find expected xml string for SigGen and HardLimit components
		final String sigGenSad = DiagramTestUtils.regexStringForSadComponent((ComponentShapeImpl) editor.getEditPart("SigGen").part().getModel());
		final String hardLimitSad = DiagramTestUtils.regexStringForSadComponent((ComponentShapeImpl) editor.getEditPart("HardLimit").part().getModel());

		// Check to see if SigGen is included in the sad.xml
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();
		Assert.assertTrue("The sad.xml should include SigGen's software assembly", editorText.matches(sigGenSad));
		Assert.assertFalse("The sad.xml should not yet include HardLimit's software assembly", editorText.matches(hardLimitSad));
		DiagramTestUtils.openTabInEditor(editor, "Diagram");

		// Save project and check to see if HardLimit is now in the sad.xml
		gefBot.menu("File").menu("Save").click();
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		editorText = editor.toTextEditor().getText();
		Assert.assertTrue("The sad.xml should include SigGen's software assembly", editorText.matches(sigGenSad));
		Assert.assertTrue("The sad.xml should include HardLimit's software assembly", editorText.matches(hardLimitSad));
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
		Assert.assertNotNull("start order shape/text should not be null", componentShape.getStartOrderText());
		Assert.assertTrue("should be assembly controller", ComponentUtils.isAssemblyController(componentShape));

		// HardLimit only has the two ports
		Assert.assertTrue(componentShape.getUsesPortStubs().size() == 1 && componentShape.getProvidesPortStubs().size() == 1);

		// Both ports are of type dataDouble
		Assert.assertEquals(componentShape.getUsesPortStubs().get(0).getUses().getInterface().getName(), "dataDouble");
		Assert.assertEquals(componentShape.getProvidesPortStubs().get(0).getProvides().getInterface().getName(), "dataDouble");
	}
}
