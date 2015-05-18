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
package gov.redhawk.ide.graphiti.sad.ui.tests;

import gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.HostCollocationPattern;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.FindByUtils;
import gov.redhawk.ide.swtbot.diagram.UsesDeviceTestUtils;
import mil.jpeojtrs.sca.sad.HostCollocation;
import mil.jpeojtrs.sca.sad.SadComponentPlacement;

import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.Assert;
import org.junit.Test;

public class HostCollocationTest extends AbstractGraphitiTest {

	private SWTBotGefEditor editor;
	private String waveformName;
	static final String UNEXPECTED_NUM_COMPONENTS = "Incorrect number of components in Host Collocation";
	static final String UNEXPECTED_SHAPE_LOCATION = "Shape location unexpected";

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
		DiagramTestUtils.addHostCollocationToDiagram(gefBot, editor, HOST_CO_NAME);

		// Add component to the host collocation
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 20, 20);

		MenuUtils.save(editor);

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
		DiagramTestUtils.addHostCollocationToDiagram(gefBot, editor, HOST_CO_NAME);

		// Add component to the host collocation
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 20, 20);
		MenuUtils.save(editor);

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
		DiagramTestUtils.addHostCollocationToDiagram(gefBot, editor, HOST_CO_NAME);

		// Add component to the host collocation
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 20, 20);
		DiagramTestUtils.addFromPaletteToDiagram(editor, SIGGEN, 20, 150);

		MenuUtils.save(editor);

		// Check that component was added
		HostCollocation hostCo = DiagramTestUtils.getHostCollocationObject(editor, HOST_CO_NAME);
		Assert.assertNotNull("Host collocation must not be null", hostCo);
		Assert.assertFalse("Component was not added to the Host Collocation", hostCo.getComponentPlacement().isEmpty());
		Assert.assertTrue("Number of components should be 2, instead there are " + hostCo.getComponentPlacement().size(),
			hostCo.getComponentPlacement().size() == 2);
		Assert.assertEquals("Expected component \'" + HARD_LIMIT + "\' was not found", HARD_LIMIT + "_1",
			hostCo.getComponentPlacement().get(0).getComponentInstantiation().get(0).getId());

		// Drag component outside of host collocation and confirm that it was removed
		editor.drag(editor.getEditPart(HARD_LIMIT), 350, 0);
		MenuUtils.save(editor);
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
		DiagramTestUtils.addFromPaletteToDiagram(editor, HOST_CO, 0, 0);
		SWTBotShell hostCoShell = gefBot.shell("New " + HOST_CO);
		hostCoShell.setFocus();
		SWTBotText hostCoName = gefBot.textWithLabel("Name:");
		hostCoName.setFocus();
		hostCoName.typeText(HOST_CO_NAME);
		gefBot.button("OK").click();

		// Add component to the host collocation
		editor.setFocus();
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 20, 20);

		MenuUtils.save(editor);

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

	/**
	 * IDE-748
	 * Upon resizing Host Collocation components should be added/removed to/from Host Collocation
	 * if they are contained within the boundaries of the new host collocation shape. Most of the location
	 * checking appears to be the same which is what we expect. There is code in place that maintains the absolute
	 * position
	 * of components within the graph as they are transitioned to the hostCollocation and we don't want them shifting.
	 */
	@Test
	public void hostCollocationResize() {
		waveformName = "HC_Resize";
		final String HARD_LIMIT = "HardLimit";
		final String SIGGEN = "SigGen";
		final String HOST_CO_NAME = "HC1";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName);
		editor = gefBot.gefEditor(waveformName);

		// Maximize window
		DiagramTestUtils.maximizeActiveWindow(gefBot);

		// Add host collocation to the waveform
		DiagramTestUtils.addHostCollocationToDiagram(gefBot, editor, HOST_CO_NAME);

		// Add component to the host collocation
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARD_LIMIT, 20, 20);
		DiagramTestUtils.addFromPaletteToDiagram(editor, SIGGEN, 20, 150);

		MenuUtils.save(editor);

		// Check that component was added
		HostCollocation hostCo = DiagramTestUtils.getHostCollocationObject(editor, HOST_CO_NAME);
		Assert.assertFalse("Component was not added to the Host Collocation", hostCo.getComponentPlacement().isEmpty());
		Assert.assertTrue("Number of components should be 2, instead there are " + hostCo.getComponentPlacement().size(),
			hostCo.getComponentPlacement().size() == 2);
		Assert.assertEquals("Expected component \'" + HARD_LIMIT + "\' was not found", HARD_LIMIT + "_1",
			hostCo.getComponentPlacement().get(0).getComponentInstantiation().get(0).getId());

		// ** Get hold of the shapes involved **//
		// HostCOllocation
		SWTBotGefEditPart hostCollocationEditPart = editor.getEditPart(HOST_CO_NAME);
		GraphicsAlgorithm hostCollocationGa = DiagramTestUtils.getHostCollocationShape(editor, HOST_CO_NAME).getGraphicsAlgorithm();
		// HardLimit
		ContainerShape hardLimitShape = (ContainerShape) editor.getEditPart(HARD_LIMIT).part().getModel();
		// SigGen
		ContainerShape sigGenShape = (ContainerShape) editor.getEditPart(SIGGEN).part().getModel();

		// Expand Host Collocation, verify components are still part of Host Collocation and
		// absolute position of components (in relation to diagram) has not changed
		hostCollocationEditPart.click();
		gefBot.sleep(1000);
		int oldX = hostCollocationGa.getX() + hostCollocationGa.getWidth();
		int oldY = hostCollocationGa.getY() + hostCollocationGa.getHeight();
		int newX = oldX + 500;
		int newY = oldY + 200;
		editor.drag(oldX + 5, oldY + 5, newX, newY);
		assertShapeLocationAndNumber(hardLimitShape, 10, 10, sigGenShape, 10, 140, hostCo, HOST_CO_NAME, 2);

		// Contract Host Collocation from the left thereby excluding the two components
		oldX = hostCollocationGa.getX();
		oldY = hostCollocationGa.getY() / 2;
		newX = oldX + 500;
		newY = oldY;
		editor.drag(oldX + 2, oldY + 2, newX, newY);
		assertShapeLocationAndNumber(hardLimitShape, 10, 10, sigGenShape, 10, 140, hostCo, HOST_CO_NAME, 0);

		// Expand Host Collocation from the left thereby including the two components
		hostCollocationEditPart.click();
		gefBot.sleep(1000);
		oldX = hostCollocationGa.getX();
		oldY = hostCollocationGa.getY() / 2;
		newX = 0;
		newY = oldY;
		editor.drag(oldX - 1, oldY + 2, newX, newY);
		assertShapeLocationAndNumber(hardLimitShape, 10, 10, sigGenShape, 10, 140, hostCo, HOST_CO_NAME, 2);

		// Contract Host Collocation from the top-left coming down and to the right thereby including the two components
		oldX = hostCollocationGa.getX();
		oldY = hostCollocationGa.getY();
		newX = oldX + 500;
		newY = oldY + 200;
		editor.drag(oldX, oldY + 2, newX, newY);
		assertShapeLocationAndNumber(hardLimitShape, 10, 10, sigGenShape, 10, 140, hostCo, HOST_CO_NAME, 0);

		// Expand Host Collocation from the top-left thereby including the two components
		oldX = hostCollocationGa.getX();
		oldY = hostCollocationGa.getY();
		newX = 0;
		newY = 0;
		editor.drag(oldX, oldY, newX, newY);
		assertShapeLocationAndNumber(hardLimitShape, 10, 10, sigGenShape, 10, 140, hostCo, HOST_CO_NAME, 2);

		// Contract Host Collocation from the bottom-left to the right and up thereby excluding the two components
		oldX = hostCollocationGa.getX();
		oldY = hostCollocationGa.getY() + hostCollocationGa.getHeight();
		newX = 500;
		newY = oldY / 2;
		editor.drag(oldX, oldY, newX, newY);
		assertShapeLocationAndNumber(hardLimitShape, 10, 10, sigGenShape, 10, 140, hostCo, HOST_CO_NAME, 0);

		// Expand Host Collocation from the bottom-left to the left and down thereby including the two components
		oldX = hostCollocationGa.getX();
		oldY = hostCollocationGa.getY() + hostCollocationGa.getHeight();
		newX = 0;
		newY = oldY * 2;
		editor.drag(oldX - 2, oldY - 1, newX, newY);
		assertShapeLocationAndNumber(hardLimitShape, 10, 10, sigGenShape, 10, 140, hostCo, HOST_CO_NAME, 2);

		// Collapse Host Collocation from the top-right downwards thereby excluding the two components
		oldX = hostCollocationGa.getX() + hostCollocationGa.getWidth();
		oldY = 0;
		newX = oldX;
		newY = hostCollocationGa.getHeight() / 2;
		editor.drag(oldX, oldY, newX, newY);
		assertShapeLocationAndNumber(hardLimitShape, 10, 10, sigGenShape, 10, 140, hostCo, HOST_CO_NAME, 0);

		// Expand Host Collocation from the top-right upwards thereby including the two components
		oldX = hostCollocationGa.getX() + hostCollocationGa.getWidth();
		oldY = hostCollocationGa.getY();
		newX = oldX;
		newY = 0;
		editor.drag(oldX, oldY, newX, newY);
		assertShapeLocationAndNumber(hardLimitShape, 10, 10, sigGenShape, 10, 140, hostCo, HOST_CO_NAME, 2);

		// Collapse Host Collocation from the bottom-right upwards thereby including the one component (topmost)
		oldX = hostCollocationGa.getX() + hostCollocationGa.getWidth();
		oldY = hostCollocationGa.getY() + hostCollocationGa.getHeight();
		newX = oldX;
		newY = 130;
		editor.drag(oldX, oldY, newX, newY);
		assertShapeLocationAndNumber(hardLimitShape, 10, 10, sigGenShape, 10, 140, hostCo, HOST_CO_NAME, 1);

		// Drag HardLimit to right
		editor.drag(editor.getEditPart(HARD_LIMIT), 350, 15);

		// Collapse Host Collocation from the right towards the left thereby excluding the all components
		hostCollocationEditPart.click();
		oldX = hostCollocationGa.getX() + hostCollocationGa.getWidth();
		oldY = (hostCollocationGa.getY() + hostCollocationGa.getHeight()) / 2;
		newX = 300;
		newY = oldY;
		editor.drag(oldX + 5, oldY + 2, newX, newY);
		assertShapeLocationAndNumber(hardLimitShape, 350, 20, sigGenShape, 10, 140, hostCo, HOST_CO_NAME, 0);

		// Expand Host Collocation from the right towards the right thereby including one component
		hostCollocationEditPart.click();
		oldX = hostCollocationGa.getX() + hostCollocationGa.getWidth();
		oldY = (hostCollocationGa.getY() + hostCollocationGa.getHeight()) / 2;
		newX = 750;
		newY = oldY;
		editor.drag(oldX + 5, oldY + 2, newX, newY);
		assertShapeLocationAndNumber(hardLimitShape, 350, 20, sigGenShape, 10, 140, hostCo, HOST_CO_NAME, 1);

		// Expand Host Collocation from the bottom downward thereby adding one more component
		oldX = (hostCollocationGa.getX() + hostCollocationGa.getWidth()) / 2;
		oldY = hostCollocationGa.getY() + hostCollocationGa.getHeight();
		newX = oldX;
		newY = 400;
		editor.drag(oldX + 5, oldY + 2, newX, newY);
		assertShapeLocationAndNumber(hardLimitShape, 350, 20, sigGenShape, 10, 140, hostCo, HOST_CO_NAME, 2);

		// Collapse Host Collocation from the bottom upward thereby removing one component
		oldX = (hostCollocationGa.getX() + hostCollocationGa.getWidth()) / 2;
		oldY = hostCollocationGa.getY() + hostCollocationGa.getHeight();
		newX = oldX;
		newY = 130;
		editor.drag(oldX + 5, oldY + 2, newX, newY);
		assertShapeLocationAndNumber(hardLimitShape, 350, 20, sigGenShape, 10, 140, hostCo, HOST_CO_NAME, 1);

		// Drag Host Collocation Down below SigGenComponent
		editor.drag(hostCollocationEditPart, 0, 300);

		// Expand Host Collocation from the top upwards thereby adding one more component
		hostCollocationGa = DiagramTestUtils.getHostCollocationShape(editor, HOST_CO_NAME).getGraphicsAlgorithm();
		oldX = (hostCollocationGa.getX() + hostCollocationGa.getWidth()) / 2;
		oldY = hostCollocationGa.getY();
		newX = oldX;
		newY = 0;
		editor.drag(oldX + 5, oldY, newX, newY);
		assertShapeLocationAndNumber(hardLimitShape, 350, 320, sigGenShape, 10, 140, hostCo, HOST_CO_NAME, 2);

		// Collapse Host Collocation from the top downwards thereby removing one component
		oldX = (hostCollocationGa.getX() + hostCollocationGa.getWidth()) / 2;
		oldY = hostCollocationGa.getY();
		newX = oldX;
		newY = 300;
		editor.drag(oldX + 5, oldY, newX, newY);
		assertShapeLocationAndNumber(hardLimitShape, 350, 20, sigGenShape, 10, 140, hostCo, HOST_CO_NAME, 1);
	}

	private void assertShapeLocationAndNumber(ContainerShape shape1, int shape1X, // SUPPRESS CHECKSTYLE INLINE
		int shape1Y, ContainerShape shape2, int shape2X, int shape2Y, HostCollocation hostCo, String hostCoName, int numContainedShapes) {

		MenuUtils.save(editor);
		hostCo = DiagramTestUtils.getHostCollocationObject(editor, hostCoName);

		gefBot.sleep(1000);

		// Test Position of component shapes
		Assert.assertTrue(UNEXPECTED_SHAPE_LOCATION, DiagramTestUtils.verifyShapeLocation(shape1, shape1X, shape1Y));
		Assert.assertTrue(UNEXPECTED_SHAPE_LOCATION, DiagramTestUtils.verifyShapeLocation(shape2, shape2X, shape2Y));

		// Test how many components are contained in the host collocation bounds
		Assert.assertTrue(UNEXPECTED_NUM_COMPONENTS, hostCo.getComponentPlacement().size() == numContainedShapes);
	}
	
	/**
	 * IDE-698
	 * Host Collocation resize should not execute if a Find By object would end up inside the contained
	 */
	@Test
	public void hostCollocationResizeOverFindBy() {
		waveformName = "HC_Resize_FindBy";
		final String SIGGEN = "SigGen";
		final String HOST_CO_NAME = "HC1";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName);
		editor = gefBot.gefEditor(waveformName);

		// maximize window
		DiagramTestUtils.maximizeActiveWindow(gefBot);

		// Add host collocation to the waveform
		DiagramTestUtils.addHostCollocationToDiagram(gefBot, editor, HOST_CO_NAME);

		// Add component/findby to the host collocation
		DiagramTestUtils.addFromPaletteToDiagram(editor, SIGGEN, 20, 150);
		DiagramTestUtils.addFromPaletteToDiagram(editor, FindByUtils.FIND_BY_DOMAIN_MANAGER, 450, 150);
		MenuUtils.save(editor);

		// HostCOllocation objects
		HostCollocation hostCo = DiagramTestUtils.getHostCollocationObject(editor, HOST_CO_NAME);
		GraphicsAlgorithm hostCollocationGa = DiagramTestUtils.getHostCollocationShape(editor, HOST_CO_NAME).getGraphicsAlgorithm();

		// Attempt to expand host collocation right to cover the FindBy object
		editor.getEditPart(HOST_CO_NAME).click();
		int oldX = hostCollocationGa.getX() + hostCollocationGa.getWidth();
		int oldY = (hostCollocationGa.getY() + hostCollocationGa.getHeight()) / 2;
		int newX = oldX + 1000;
		int newY = oldY;
		editor.drag(oldX + 5, oldY + 2, newX, newY);

		// Assert that the host collocation resize was rejected
		Assert.assertFalse(editor.isDirty());
		Assert.assertEquals("Host collocation width not have changed size", oldX, hostCollocationGa.getX() + hostCollocationGa.getWidth());

		// Assert that FindBy Element is not contained within host collocation
		Assert.assertTrue("Number of components should be 1, instead there are " + hostCo.getComponentPlacement().size(),
			hostCo.getComponentPlacement().size() == 1);
		Assert.assertEquals("Expected component \'" + SIGGEN + "\' was not found", SIGGEN + "_1",
			hostCo.getComponentPlacement().get(0).getComponentInstantiation().get(0).getId());
	}

	/**
	 * IDE-698
	 * Host Collocation resize should not execute if a Find By object would end up inside the contained
	 */
	@Test
	public void hostCollocationResizeOverUsesDevice() {
		waveformName = "HC_Resize_FindBy";
		final String SIGGEN = "SigGen";
		final String HOST_CO_NAME = "HC1";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName);
		editor = gefBot.gefEditor(waveformName);

		// maximize window
		DiagramTestUtils.maximizeActiveWindow(gefBot);

		// Add host collocation to the waveform
		DiagramTestUtils.addHostCollocationToDiagram(gefBot, editor, HOST_CO_NAME);

		// Add component/findby to the host collocation
		DiagramTestUtils.addFromPaletteToDiagram(editor, SIGGEN, 20, 150);
		DiagramTestUtils.addUseFrontEndTunerDeviceToDiagram(gefBot, editor, 450, 150);
		UsesDeviceTestUtils.completeUsesFEDeviceWizard(gefBot, "existingAllocId", "newAllocId", new String[] { "provides" }, new String[] { "uses" });
		MenuUtils.save(editor);

		// HostCOllocation objects
		HostCollocation hostCo = DiagramTestUtils.getHostCollocationObject(editor, HOST_CO_NAME);
		GraphicsAlgorithm hostCollocationGa = DiagramTestUtils.getHostCollocationShape(editor, HOST_CO_NAME).getGraphicsAlgorithm();

		// Attempt to expand host collocation right to cover the FindBy object
		editor.getEditPart(HOST_CO_NAME).click();
		int oldX = hostCollocationGa.getX() + hostCollocationGa.getWidth();
		int oldY = (hostCollocationGa.getY() + hostCollocationGa.getHeight()) / 2;
		int newX = oldX + 1000;
		int newY = oldY;
		editor.drag(oldX + 5, oldY + 2, newX, newY);

		// Assert that the host collocation resize was rejected
		Assert.assertFalse(editor.isDirty());
		Assert.assertEquals("Host collocation width not have changed size", oldX, hostCollocationGa.getX() + hostCollocationGa.getWidth());

		// Assert that Uses Device Element is not contained within host collocation
		Assert.assertTrue("Number of components should be 1, instead there are " + hostCo.getComponentPlacement().size(),
			hostCo.getComponentPlacement().size() == 1);
		Assert.assertEquals("Expected component \'" + SIGGEN + "\' was not found", SIGGEN + "_1",
			hostCo.getComponentPlacement().get(0).getComponentInstantiation().get(0).getId());
	}
}
