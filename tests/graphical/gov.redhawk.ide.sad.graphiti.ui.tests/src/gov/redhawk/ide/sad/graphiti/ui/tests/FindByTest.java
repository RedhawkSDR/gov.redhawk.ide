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
import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.FindByUtils;

import java.util.List;

import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;

import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.junit.Assert;
import org.junit.Test;

public class FindByTest extends AbstractGraphitiTest {
	private SWTBotGefEditor editor;
	private String waveformName;

	/**
	 * IDE-736 Create the pictogram shape in the waveform diagram that
	 * represents the FindBy business object. This includes the ContainerShape
	 * for the element, outer and inner text, port shapes and labels,
	 * and component supported interface.
	 * 
	 * IDE-737 Create wizards to get user input when adding FindBy Name, FindBy Service,
	 * and FindBy Event Channel elements to the SAD Diagram.
	 */
	@Test
	public void checkFindByPictogramElements() {
		waveformName = "FindBy_Pictogram";
		final String SIGGEN = "SigGen";
		final String findByName = "FindBy";
		final String[] provides = { "dataDouble_in" };
		final String[] uses = { "dataDouble_out" };

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName);
		editor = gefBot.gefEditor(waveformName);

		// Add component to the diagram
		DiagramTestUtils.dragFromPaletteToDiagram(editor, SIGGEN, 0, 0);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, FindByUtils.FIND_BY_NAME, 0, 150);
		FindByUtils.completeFindByWizard(gefBot, FindByUtils.FIND_BY_NAME, findByName, provides, uses);
		MenuUtils.save(editor);

		// Grab the associated business object and confirm it is a FindBy element
		SWTBotGefEditPart fbEditPart = editor.getEditPart(findByName);
		Assert.assertNotNull("FindBy Element not found", fbEditPart);
		RHContainerShapeImpl findByShape = (RHContainerShapeImpl) fbEditPart.part().getModel();
		Assert.assertTrue("Element is not a FindBy", DUtil.getBusinessObject(findByShape) instanceof FindByStub);
		FindByStub findByObject = (FindByStub) DUtil.getBusinessObject(findByShape);

		// Run assertions on expected properties: outer/inner text, lollipop, port number, type(provides-uses), name
		Assert.assertEquals("Outer Text does not match input", FindByUtils.FIND_BY_NAME, findByShape.getOuterText().getValue());
		Assert.assertEquals("Inner Text does not match input", findByName, findByShape.getInnerText().getValue());
		Assert.assertEquals("Diagram object and domain object names don't match", findByName, findByObject.getNamingService().getName());
		Assert.assertNotNull("component supported interface graphic should not be null", findByShape.getLollipop());

		Assert.assertTrue("Number of ports is incorrect", findByShape.getUsesPortStubs().size() == 1 && findByShape.getProvidesPortStubs().size() == 1);
		Assert.assertEquals("Uses port name is incorrect", uses[0], findByShape.getUsesPortStubs().get(0).getName());
		Assert.assertEquals("Diagram uses and domain uses don't match", uses[0], findByObject.getUses().get(0).getName());
		Assert.assertEquals("Provides port name is incorrect", provides[0], findByShape.getProvidesPortStubs().get(0).getName());
		Assert.assertEquals("Diagram provides and provides uses don't match", provides[0], findByObject.getProvides().get(0).getName());
	}

	/**
	 * IDE-669 Components are removed with the delete button (trashcan image)
	 * that appears when you select the component, but the delete context menu
	 * does not remove the component from the diagram. In most cases, the delete
	 * and remove context menu options are grayed out and not selectable.
	 * 
	 * IDE-737 Create wizards to get user input when adding FindBy Name, FindBy Service,
	 * and FindBy Event Channel elements to the SAD Diagram.
	 */
	@Test
	public void checkFindByContextMenuDelete() {
		waveformName = "FindBy_Delete";
		String[] findByList = { FindByUtils.FIND_BY_NAME, FindByUtils.FIND_BY_DOMAIN_MANAGER, FindByUtils.FIND_BY_EVENT_CHANNEL,
			FindByUtils.FIND_BY_FILE_MANAGER, FindByUtils.FIND_BY_SERVICE };

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName);
		editor = gefBot.gefEditor(waveformName);

		for (String s : findByList) {
			// Add component to diagram from palette
			DiagramTestUtils.dragFromPaletteToDiagram(editor, s, 0, 0);
			FindByUtils.completeFindByWizard(gefBot, s, null, new String[] { "p1", "p2" }, new String[] { "u1", "u2" });
		}

		for (String s : findByList) {
			// Drill down to graphiti component shape
			SWTBotGefEditPart gefEditPart = editor.getEditPart(FindByUtils.getFindByDefaultName(s));
			DiagramTestUtils.deleteFromDiagram(editor, gefEditPart);
			Assert.assertNull(editor.getEditPart(s));
		}
	}

	/**
	 * Ensure that deleting a FindBy that is part of a connection removes the connection from the sad.xml
	 */
	@Test
	public void deleteFindByTest() {
		waveformName = "Delete_FindBy";
		final String SIGGEN = "SigGen";
		final String FIND_BY_NAME = "FindByName";
		final String[] provides = { "data_in" };

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName);
		editor = gefBot.gefEditor(waveformName);

		// Add component to the diagram
		DiagramTestUtils.dragFromPaletteToDiagram(editor, SIGGEN, 0, 0);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, FindByUtils.FIND_BY_NAME, 0, 150);
		FindByUtils.completeFindByWizard(gefBot, FindByUtils.FIND_BY_NAME, FIND_BY_NAME, provides, null);
		MenuUtils.save(editor);

		// Create connection on diagram
		SWTBotGefEditPart sigGenUsesPart = DiagramTestUtils.getDiagramUsesPort(editor, SIGGEN);
		SWTBotGefEditPart findByProvidesPart = DiagramTestUtils.getDiagramProvidesPort(editor, FIND_BY_NAME);
		DiagramTestUtils.drawConnectionBetweenPorts(editor, sigGenUsesPart, findByProvidesPart);
		MenuUtils.save(editor);

		// Check sad.xml for connection
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();
		Assert.assertTrue("The sad.xml should include a new connection. Expected: <findby><namingservice name=\"" + FIND_BY_NAME + "\"/>",
			editorText.matches("(?s).*" + "<connectinterface.*<findby>.*<namingservice name=\"" + FIND_BY_NAME + "\"/>" + ".*"));

		// Delete Findby
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		DiagramTestUtils.deleteFromDiagram(editor, editor.getEditPart(FIND_BY_NAME));

		try {
			gefBot.shell("Confirm Delete").setFocus();
			Assert.fail("Deleting FindBy elements should not require a dialog");
		} catch (WidgetNotFoundException e) {
			Assert.assertTrue(e.getMessage().matches(".*" + "Confirm Delete" + ".*"));
		}

		editor = gefBot.gefEditor(waveformName);
		editor.setFocus();
		gefBot.menu("File").menu("Save").click();

		Assert.assertNull("FindBy shape was not removed", editor.getEditPart(FIND_BY_NAME));

		// Ensure Findby connection is removed from the XML
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		editorText = editor.toTextEditor().getText();
		Assert.assertTrue("FindBy Connection was not removed", editorText.matches("(?s).*" + "<connections/>" + ".*"));
	}

	/**
	 * IDE-652 & IDE-908
	 * Edit existing FindBy Elements
	 * Change names, add & remove ports
	 */
	@Test
	public void editFindByTest() {
		waveformName = "FindBy_Connection";
		final String SIGGEN = "SigGen";
		final String HARD_LIMIT = "HardLimit";
		final String FIND_BY_NAME = "FindBy";
		final String newFindByName = "NewFindByName";
		final String[] provides = { "data_in" };
		final String[] uses = { "data_out" };
		final String NEW_USES_PORT = "dataDouble_out";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName);
		editor = gefBot.gefEditor(waveformName);

		// Add component to the diagram
		DiagramTestUtils.dragFromPaletteToDiagram(editor, SIGGEN, 0, 0);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, HARD_LIMIT, 200, 20);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, FindByUtils.FIND_BY_NAME, 0, 150);
		FindByUtils.completeFindByWizard(gefBot, FindByUtils.FIND_BY_NAME, FIND_BY_NAME, provides, uses);
		MenuUtils.save(editor);

		// Create connection on diagram
		SWTBotGefEditPart sigGenUsesPart = DiagramTestUtils.getDiagramUsesPort(editor, SIGGEN);
		SWTBotGefEditPart findByProvidesPart = DiagramTestUtils.getDiagramProvidesPort(editor, FIND_BY_NAME);
		DiagramTestUtils.drawConnectionBetweenPorts(editor, sigGenUsesPart, findByProvidesPart);

		SWTBotGefEditPart findByUsesPart = DiagramTestUtils.getDiagramUsesPort(editor, FIND_BY_NAME);
		SWTBotGefEditPart hardLimitProvidesPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT);
		DiagramTestUtils.drawConnectionBetweenPorts(editor, findByUsesPart, hardLimitProvidesPart);

		MenuUtils.save(editor);

		// Open FindBy edit wizard and change name, remove existing port, and add a new one
		editor.getEditPart(FIND_BY_NAME).select();
		editor.clickContextMenu("Edit Find By");

		// Change Name
		gefBot.textWithLabel("Component Name:").setText(newFindByName);

		// Delete existing provides port
		gefBot.listInGroup("Port(s) to use for connections", 0).select(provides[0]);
		gefBot.button("Delete", 0).click();

		// Add new uses port
		gefBot.textInGroup("Port(s) to use for connections", 1).setText(NEW_USES_PORT);
		gefBot.button("Add Uses Port").click();

		gefBot.button("Finish").click();

		// Confirm that changes were made
		SWTBotGefEditPart fbEditPart = editor.getEditPart(newFindByName);
		Assert.assertNotNull("FindBy Element not found", fbEditPart);
		RHContainerShapeImpl findByShape = (RHContainerShapeImpl) fbEditPart.part().getModel();
		FindByStub findByObject = (FindByStub) DUtil.getBusinessObject(findByShape);

		Assert.assertEquals("Inner Text was not updated", newFindByName, findByShape.getInnerText().getValue());
		Assert.assertEquals("Diagram object and domain object names don't match", newFindByName, findByObject.getNamingService().getName());
		Assert.assertTrue("Number of ports is incorrect", findByShape.getUsesPortStubs().size() == 2 && findByShape.getProvidesPortStubs().size() == 0);
		Assert.assertEquals("Uses port name is incorrect", NEW_USES_PORT, findByShape.getUsesPortStubs().get(1).getName());
		Assert.assertEquals("Diagram uses and domain uses don't match", NEW_USES_PORT, findByObject.getUses().get(1).getName());

		// Confirm that connections properly updated
		sigGenUsesPart = DiagramTestUtils.getDiagramUsesPort(editor, SIGGEN);
		List<SWTBotGefConnectionEditPart> connections = DiagramTestUtils.getSourceConnectionsFromPort(editor, sigGenUsesPart);
		Assert.assertTrue("SigGen connection should have been removed", connections.size() == 0);

		hardLimitProvidesPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT);
		connections = DiagramTestUtils.getTargetConnectionsFromPort(editor, hardLimitProvidesPart);
		Assert.assertEquals("HardLimit should only have one incoming connection", 1, connections.size());

		SWTBotGefConnectionEditPart connectionPart = connections.get(0);
		Connection connection = (Connection) connectionPart.part().getModel();
		UsesPortStub connectionSource = (UsesPortStub) DUtil.getBusinessObject(connection.getStart());
		ProvidesPortStub connectionTarget = (ProvidesPortStub) DUtil.getBusinessObject(connection.getEnd());
		Assert.assertEquals("Connection source incorrect", uses[0], connectionSource.getName());
		Assert.assertEquals("Connection target incorrect", "dataDouble_in", connectionTarget.getName());
	}

	/**
	 * IDE-738
	 * Allow connections that include FindBy elements.
	 * Update the sad.xml to show the resultant connection details
	 */
	@Test
	public void findByConnectionTest() {
		waveformName = "FindBy_Connection";
		final String SIGGEN = "SigGen";
		final String HARDLIMIT = "HardLimit";
		final String findByName = "FindBy";
		final String[] provides = { "data_in" };
		final String[] uses = { "data_out" };

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName);
		editor = gefBot.gefEditor(waveformName);

		// Add component to the diagram
		DiagramTestUtils.dragFromPaletteToDiagram(editor, SIGGEN, 0, 0);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, FindByUtils.FIND_BY_NAME, 0, 150);
		FindByUtils.completeFindByWizard(gefBot, FindByUtils.FIND_BY_NAME, findByName, provides, uses);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, HARDLIMIT, 0, 300);
		MenuUtils.save(editor);

		// Create connection on diagram
		SWTBotGefEditPart sigGenUsesPart = DiagramTestUtils.getDiagramUsesPort(editor, SIGGEN);
		SWTBotGefEditPart findByProvidesPart = DiagramTestUtils.getDiagramProvidesPort(editor, findByName);
		DiagramTestUtils.drawConnectionBetweenPorts(editor, sigGenUsesPart, findByProvidesPart);
		SWTBotGefEditPart findByUsesPart = DiagramTestUtils.getDiagramUsesPort(editor, findByName);
		SWTBotGefEditPart hardLimitProvidesPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARDLIMIT);
		Assert.assertTrue("Failed to draw connection from FindBy uses port", 
			DiagramTestUtils.drawConnectionBetweenPorts(editor, findByUsesPart, hardLimitProvidesPart));
		MenuUtils.save(editor);

		// Check sad.xml for connection
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();
		Assert.assertTrue("The sad.xml should include a new connection. Expected: <findby><namingservice name=\"" + findByName + "\"/>",
			editorText.matches("(?s).*" + "<connectinterface.*<findby>.*<namingservice name=\"" + findByName + "\"/>" + ".*"));
	}
}
