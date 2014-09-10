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

import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.HostCollocationPattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import mil.jpeojtrs.sca.sad.HostCollocation;
import mil.jpeojtrs.sca.sad.SadComponentPlacement;

import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public class HostCollocationTests extends AbstractGraphitiTest {

	private SWTBotGefEditor editor;
	private String waveformName;

	/**
	 * IDE-746
	 * Create the pictogram shape in the waveform diagram that represents the Host Collocation object.
	 * This includes the ContainerShape for the component a label for the object name.
	 */
	@Test
	public void checkHostCollocationPictogramElements() {
		waveformName = "HC_Pictogram";
		final String HARD_LIMIT = "HardLimit";
		final String HOST_CO_NAME = "HC1";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName);
		editor = gefBot.gefEditor(waveformName);

		// Add host collocation to the waveform
		DiagramTestUtils.dragHostCollocationToDiagram(gefBot, editor, HOST_CO_NAME);

		// Add component to the host collocation
		DiagramTestUtils.dragFromPaletteToDiagram(editor, HARD_LIMIT, 20, 20);

		MenuUtils.save(gefBot);

		// Check pictogram elements
		SWTBotGefEditPart hostCoEditPart = editor.getEditPart(HOST_CO_NAME);
		Assert.assertNotNull(hostCoEditPart);
		ContainerShape containerShape = (ContainerShape) hostCoEditPart.part().getModel();
		String shapeType = Graphiti.getPeService().getPropertyValue(containerShape, DUtil.SHAPE_TYPE);
		Assert.assertTrue("Host Collocation property is missing or wrong", shapeType.equals(HostCollocationPattern.HOST_COLLOCATION_OUTER_CONTAINER_SHAPE));

		// Check model object values
		Object bo = DUtil.getBusinessObject(containerShape);
		Assert.assertTrue("Business object should be instance of HostCollocation", bo instanceof HostCollocation);

		HostCollocation hostCo = (HostCollocation) bo;
		Assert.assertEquals("Name value does not match expected value: " + HOST_CO_NAME, HOST_CO_NAME, hostCo.getName());
		EList<SadComponentPlacement> components = hostCo.getComponentPlacement();
		Assert.assertEquals("Expected component \'" + HARD_LIMIT + "\' was not found", HARD_LIMIT + "_1",
			components.get(0).getComponentInstantiation().get(0).getId());
	}

	/**
	 * IDE-749
	 * Elements contained within a Host Collocation container should should stay in their relative locations when the
	 * parent container is moved.
	 */
	@Test
	public void hostCollocationRelativePosition() {
		waveformName = "HC_Component_Position";
		final String HARD_LIMIT = "HardLimit";
		final String HOST_CO_NAME = "HC1";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName);
		editor = gefBot.gefEditor(waveformName);

		// Add host collocation to the waveform
		DiagramTestUtils.dragHostCollocationToDiagram(gefBot, editor, HOST_CO_NAME);

		// Add component to the host collocation
		DiagramTestUtils.dragFromPaletteToDiagram(editor, HARD_LIMIT, 20, 20);
		MenuUtils.save(gefBot);

		// Store host collocation and component relative location
		ContainerShape hostCoShape = DiagramTestUtils.getHostCollocationShape(editor, HOST_CO_NAME);
		int hostCoX = hostCoShape.getGraphicsAlgorithm().getX();
		int hostCoY = hostCoShape.getGraphicsAlgorithm().getY();
		Shape child = hostCoShape.getChildren().get(0);
		int childX = child.getGraphicsAlgorithm().getX();
		int childY = child.getGraphicsAlgorithm().getY();
		
		// Drag host collocation
		editor.drag(HOST_CO_NAME, 50, 50);
		
		// Check that host collocation has moved, but component relative location is the same
		hostCoShape = DiagramTestUtils.getHostCollocationShape(editor, HOST_CO_NAME);
		Assert.assertNotEquals("Host Collocation x-coord did not change, and it should have", hostCoX, hostCoShape.getGraphicsAlgorithm().getX());
		Assert.assertNotEquals("Host Collocation y-coord did not change, and it should have", hostCoY, hostCoShape.getGraphicsAlgorithm().getY());
		child = hostCoShape.getChildren().get(0);
		Assert.assertEquals("Child component relative x-coord should not have changed", childX, child.getGraphicsAlgorithm().getX());
		Assert.assertEquals("Child component relative y-coord should not have changed", childY, child.getGraphicsAlgorithm().getY());
	}

	/**
	 * IDE-747
	 * User should be able to add and remove components to a Host Collocation container by dragging them in and out of
	 * the pictogram element.
	 */
	@Test
	public void hostCollocationDnDComponents() {
		waveformName = "HC_DragAndDrop";
		final String HARD_LIMIT = "HardLimit";
		final String SIGGEN = "SigGen";
		final String HOST_CO_NAME = "HC1";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName);
		editor = gefBot.gefEditor(waveformName);

		// Add host collocation to the waveform
		DiagramTestUtils.dragHostCollocationToDiagram(gefBot, editor, HOST_CO_NAME);

		// Add component to the host collocation
		DiagramTestUtils.dragFromPaletteToDiagram(editor, HARD_LIMIT, 20, 20);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, SIGGEN, 20, 150);

		MenuUtils.save(gefBot);

		// Check that component was added
		HostCollocation hostCo = DiagramTestUtils.getHostCollocationObject(editor, HOST_CO_NAME);
		Assert.assertFalse("Component was not added to the Host Collocation", hostCo.getComponentPlacement().isEmpty());
		Assert.assertTrue("Number of components should be 2, instead there are " + hostCo.getComponentPlacement().size(),
			hostCo.getComponentPlacement().size() == 2);
		Assert.assertEquals("Expected component \'" + HARD_LIMIT + "\' was not found", HARD_LIMIT + "_1",
			hostCo.getComponentPlacement().get(0).getComponentInstantiation().get(0).getId());

		// Drag component outside of host collocation and confirm that it was removed
		editor.drag(editor.getEditPart(HARD_LIMIT), 350, 0);
		MenuUtils.save(gefBot);
		hostCo = DiagramTestUtils.getHostCollocationObject(editor, HOST_CO_NAME);
		Assert.assertTrue("Number of components should be 1, instead there are " + hostCo.getComponentPlacement().size(),
			hostCo.getComponentPlacement().size() == 1);
		Assert.assertEquals("Expected component \'" + SIGGEN + "\' was not found", SIGGEN + "_1",
			hostCo.getComponentPlacement().get(0).getComponentInstantiation().get(0).getId());
	}

	/**
	 * IDE-696
	 * Ensure deletion of host collocation does not remove contained components and instead leaves
	 * them in the diagram.
	 */
	@Test
	public void hostCollocationContextMenuDelete() {
		waveformName = "HC_Pictogram";
		final String HARD_LIMIT = "HardLimit";
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

		// delete host collocation
		SWTBotGefEditPart gefEditPart = editor.getEditPart(HOST_CO_NAME);
		DiagramTestUtils.deleteFromDiagram(editor, gefEditPart);
		// ensure host collocation is deleted
		Assert.assertNull(editor.getEditPart(HOST_CO_NAME));
		// ensure component still exists
		Assert.assertNotNull(editor.getEditPart(HARD_LIMIT));
	}
}
