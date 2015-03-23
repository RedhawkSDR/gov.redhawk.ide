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
package gov.redhawk.ide.graphiti.dcd.ui.tests;

import gov.redhawk.ide.graphiti.dcd.ext.impl.DeviceShapeImpl;
import gov.redhawk.ide.graphiti.dcd.ext.impl.ServiceShapeImpl;
import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.NodeUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.RHTestBotEditor;

import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;

import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

public class NodeComponentTest extends AbstractGraphitiTest {

	private SWTBotGefEditor editor;
	private String projectName;
	private static final String DOMAIN_NAME = "REDHAWK_DEV";
	private static final String GPP = "GPP";
	private static final String SERVICE_STUB = "ServiceStub";

	/**
	 * IDE-988
	 * Create the pictogram shape in the node diagram that represents device/service business objects.
	 * This includes ContainerShape, usage name, ID, port shapes and labels, and component supported interface.
	 */
	@Test
	public void checkNodePictogramElements() {
		projectName = "PictogramShapesNode";

		// Create an empty node project
		NodeUtils.createNewNodeProject(gefBot, projectName, DOMAIN_NAME);
		editor = gefBot.gefEditor(projectName);
		editor.setFocus();

		// Add to diagram from palette
		DiagramTestUtils.addFromPaletteToDiagram(editor, GPP, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, SERVICE_STUB, 300, 200);

		// Confirm created object is as expected
		assertGPP(editor.getEditPart(GPP));
		DiagramTestUtils.deleteFromDiagram(editor, editor.getEditPart(GPP));
		assertServiceStub(editor.getEditPart(SERVICE_STUB));
		DiagramTestUtils.deleteFromDiagram(editor, editor.getEditPart(SERVICE_STUB));

		// Add to diagram from Target SDR
		DiagramTestUtils.dragDeviceFromTargetSDRToDiagram(gefBot, editor, GPP);
		editor.drag(editor.getEditPart(GPP), 10, 50);
		assertGPP(editor.getEditPart(GPP));

		DiagramTestUtils.dragServiceFromTargetSDRToDiagram(gefBot, editor, SERVICE_STUB);
		editor.select(editor.getEditPart(SERVICE_STUB));
		assertServiceStub(editor.getEditPart(SERVICE_STUB));
	}
	
	/**
	 * IDE-1131
	 * Name-spaced devices should have their component file id set to basename_UUID, not the fully qualified name
	 */
	@Test
	public void checkNameSpacedDeviceInDcd() {
		projectName = "NameSpacedDeviceTest";
		String deviceName = "name.space.device";
		String deviceBaseName = "device"; 

		NodeUtils.createNewNodeProject(bot, projectName, DOMAIN_NAME);
		editor = gefBot.gefEditor(projectName);

		ToolEntry deviceToolEntry = getToolEntry((RHTestBotEditor) editor, deviceName);
		DiagramTestUtils.addFromPaletteToDiagramWithNameSpace((RHTestBotEditor) editor, deviceToolEntry, 0, 0);
		MenuUtils.save(editor);

		// Build expected xml string for device
		final String componentFileString = "(?s).*<componentfile id=\"" + deviceBaseName + ".*";
		final String deviceXmlString = DiagramTestUtils.regexStringForDevice((RHContainerShapeImpl) editor.getEditPart(deviceName).part().getModel());

		// Check dcd.xml for string
		DiagramTestUtils.openTabInEditor(editor, "DeviceManager.dcd.xml");
		String editorText = editor.toTextEditor().getText();
		Assert.assertTrue("The componentfile should only include the basename_UUID", editorText.matches(componentFileString));
		Assert.assertTrue("The dcd.xml should include " + deviceName + "'s device configuration", editorText.matches(deviceXmlString));
	}

	/**
	 * Use context menu to delete a device
	 */
	@Test
	public void checkDeviceContextMenuDelete() {
		projectName = "Context-Delete";

		// Create an empty node project
		NodeUtils.createNewNodeProject(gefBot, projectName, DOMAIN_NAME);
		editor = gefBot.gefEditor(projectName);
		editor.setFocus();

		DiagramTestUtils.addFromPaletteToDiagram(editor, GPP, 0, 0);
		SWTBotGefEditPart gefEditPart = editor.getEditPart(GPP);
		DiagramTestUtils.deleteFromDiagram(editor, gefEditPart);
		Assert.assertNull(editor.getEditPart(GPP));
	}

	/**
	 * Devices selected in the diagram should have the properties of their corresponding
	 * model objects correctly exposed in the default Eclipse properties view.
	 */
	@Test
	public void checkChangesToPropertiesReflectedInDcd() {
		projectName = "ReflectProperties";

		// Create an empty node project
		NodeUtils.createNewNodeProject(gefBot, projectName, DOMAIN_NAME);
		editor = gefBot.gefEditor(projectName);
		editor.setFocus();

		SWTBotView view = MenuUtils.showView(gefBot, "org.eclipse.ui.views.PropertySheet");
		DiagramTestUtils.addFromPaletteToDiagram(editor, GPP, 0, 0);
		editor.getEditPart(GPP).click();
		view.setFocus();
		String propertyname = gefBot.viewByTitle("Properties").bot().tree().cell(0, "Property").toString();
		String newValue = "log";
		for (SWTBotTreeItem item : gefBot.viewByTitle("Properties").bot().tree().getAllItems()) {
			if (item.getText().equals(propertyname)) {
				KeyStroke[] keystrokes = { Keystrokes.create('l')[0], Keystrokes.create('o')[0], Keystrokes.create('g')[0] };
				item.click(1).pressShortcut(keystrokes);
				break;
			}
		}

		editor.getEditPart(GPP).click();
		MenuUtils.save(editor);
		String regex = DiagramTestUtils.regexStringForProperty(propertyname, newValue);
		DiagramTestUtils.openTabInEditor(editor, "DeviceManager.dcd.xml");
		String editorText = editor.toTextEditor().getText();
		Assert.assertTrue("The dcd.xml should include GPP's changed property", editorText.matches(regex));
	}

	/**
	 * The delete context menu should not appear when ports are selected
	 */
	@Test
	public void doNotDeletePortsTest() {
		projectName = "No-Delete-Port-Test";
		// Create an empty node project
		NodeUtils.createNewNodeProject(gefBot, projectName, DOMAIN_NAME);
		editor = gefBot.gefEditor(projectName);
		editor.setFocus();

		DiagramTestUtils.addFromPaletteToDiagram(editor, GPP, 0, 0);
		SWTBotGefEditPart uses = DiagramTestUtils.getDiagramUsesPort(editor, GPP);

		List<SWTBotGefEditPart> anchors = new ArrayList<SWTBotGefEditPart>();
		anchors.add(DiagramTestUtils.getDiagramPortAnchor(uses));

		for (SWTBotGefEditPart anchor : anchors) {
			try {
				anchor.select();
				editor.clickContextMenu("Delete");
				Assert.fail();
			} catch (WidgetNotFoundException e) {
				Assert.assertEquals(e.getMessage(), "Delete", e.getMessage());
			}
		}
	}

	/**
	 * Private helper method for {@link #checkNodePictogramElements()} Asserts the given SWTBotGefEditPart is a GPP
	 * device and is drawn correctly
	 * @param gefEditPart
	 */
	private static void assertGPP(SWTBotGefEditPart gefEditPart) {
		Assert.assertNotNull(gefEditPart);
		// Drill down to graphiti device shape
		DeviceShapeImpl deviceShape = (DeviceShapeImpl) gefEditPart.part().getModel();

		// Grab the associated business object and confirm it is a DcdComponentInstantiation
		Object bo = DUtil.getBusinessObject(deviceShape);
		Assert.assertTrue("business object should be of type DcdComponentInstantiation", bo instanceof DcdComponentInstantiation);
		DcdComponentInstantiation ci = (DcdComponentInstantiation) bo;

		// Run assertions on expected properties
		Assert.assertEquals("outer text should match component type", GPP, deviceShape.getOuterText().getValue());
		Assert.assertEquals("inner text should match component usage name", ci.getUsageName(), deviceShape.getInnerText().getValue());
		Assert.assertNotNull("component supported interface graphic should not be null", deviceShape.getLollipop());

		// GPP only has the one port
		Assert.assertTrue(deviceShape.getUsesPortStubs().size() == 1 && deviceShape.getProvidesPortStubs().size() == 0);

		// Port is of type propEvent
		Assert.assertEquals("propEvent", deviceShape.getUsesPortStubs().get(0).getUses().getName());
	}

	/**
	 * Private helper method for {@link #checkNodePictogramElements()} Asserts the given SWTBotGefEditPart is a
	 * ServiceStub service and is drawn correctly
	 * @param gefEditPart
	 */
	private static void assertServiceStub(SWTBotGefEditPart gefEditPart) {
		Assert.assertNotNull(gefEditPart);
		// Drill down to graphiti service shape
		ServiceShapeImpl serviceShape = (ServiceShapeImpl) gefEditPart.part().getModel();

		// Grab the associated business object and confirm it is a DcdComponentInstantiation
		Object bo = DUtil.getBusinessObject(serviceShape);
		Assert.assertTrue("business object should be of type DcdComponentInstantiation", bo instanceof DcdComponentInstantiation);
		DcdComponentInstantiation ci = (DcdComponentInstantiation) bo;

		// Run assertions on expected properties
		Assert.assertEquals("outer text should match component type", SERVICE_STUB, serviceShape.getOuterText().getValue());
		Assert.assertEquals("inner text should match component usage name", ci.getUsageName(), serviceShape.getInnerText().getValue());
		Assert.assertNotNull("component supported interface graphic should not be null", serviceShape.getLollipop());

		// SERVICE_STUB should not have a port
		Assert.assertTrue(serviceShape.getUsesPortStubs().size() == 0 && serviceShape.getProvidesPortStubs().size() == 0);
	}
}
