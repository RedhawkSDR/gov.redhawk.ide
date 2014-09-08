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

import gov.redhawk.ide.sad.graphiti.ext.ComponentShape;
import gov.redhawk.ide.sad.graphiti.ext.impl.ComponentShapeImpl;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;

import java.math.BigInteger;
import java.util.List;

import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class that deals with editing elements to the sad.xml and making sure they appear correctly in the diagram
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class XmlToDiagramEditTests extends AbstractGraphitiTest {

	private SWTBotGefEditor editor;
	private String waveformName;

	/**
	 * IDE-853
	 * Test editing component properties in the sad.xml
	 * Ensure that edits are reflected to the diagram upon save
	 */
	@Test
	public void editComponentInXmlTest() {
		waveformName = "Edit_Component_Xml";
		final String componentOne = "SigGen";
		final String componentTwo = "HardLimit";
		final String componentThree = "SigGen";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName);
		editor = gefBot.gefEditor(waveformName);

		// Add component to the diagram
		DiagramTestUtils.dragFromPaletteToDiagram(editor, componentOne, 0, 0);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, componentTwo, 200, 0);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, componentThree, 0, 200);
		MenuUtils.save(gefBot);

		// Edit content of sad.xml
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();
		editorText = editorText.replace(componentTwo + "_1", componentTwo + "_2");
		editorText = editorText.replace("startorder=\"1\"", "startorder=\"3\"");
		editorText = editorText.replace("startorder=\"2\"", "startorder=\"1\"");
		editorText = editorText.replace("startorder=\"3\"", "startorder=\"2\"");
		editor.toTextEditor().setText(editorText);
		MenuUtils.save(gefBot);

		// Confirm edits appear in the diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		gefBot.sleep(2000); // Sometimes diagram takes a few seconds to update
		SadComponentInstantiation componentObj = DiagramTestUtils.getComponentObject(editor, componentTwo);
		Assert.assertEquals("Usage Name did not update correctly", componentTwo + "_2", componentObj.getUsageName());
		Assert.assertEquals("Component ID did not update correctly", componentTwo + "_2", componentObj.getId());
		Assert.assertEquals("Naming Service did not update correctly", componentTwo + "_2", componentObj.getFindComponent().getNamingService().getName());
		Assert.assertEquals("Start Order did not update correctly", BigInteger.valueOf(2), componentObj.getStartOrder());

	}

	/**
	 * IDE-854
	 * Test editing connection properties in the sad.xml
	 * Ensure that edits are reflected to the diagram upon save
	 */
	@Test
	public void editConnectionInXmlTest() {
		waveformName = "Edit_Connection_Xml";
		final String SIGGEN = "SigGen";
		final String HARDLIMIT = "HardLimit";
		final String DATA_CONVERTER = "DataConverter";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName);
		editor = gefBot.gefEditor(waveformName);

		// Add components to the diagram
		DiagramTestUtils.dragFromPaletteToDiagram(editor, SIGGEN, 0, 0);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, HARDLIMIT, 200, 0);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, DATA_CONVERTER, 0, 100);

		// Get port edit parts
		SWTBotGefEditPart sigGenUsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, SIGGEN);
		SWTBotGefEditPart hardLimitProvidesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARDLIMIT);
		DiagramTestUtils.drawConnectionBetweenPorts(editor, sigGenUsesEditPart, hardLimitProvidesEditPart);
		MenuUtils.save(gefBot);

		// Edit content of sad.xml
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();
		editorText = editorText.replace("<providesidentifier>dataDouble_in</providesidentifier>", "<providesidentifier>dataDouble</providesidentifier>");
		editorText = editorText.replace("<componentinstantiationref refid=\"HardLimit_1\"/>", "<componentinstantiationref refid=\"DataConverter_1\"/>");
		editor.toTextEditor().setText(editorText);
		MenuUtils.save(gefBot);

		// Confirm edits appear in the diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		gefBot.sleep(2000); // Sometimes diagram takes a few seconds to update
		sigGenUsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, SIGGEN);
		hardLimitProvidesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARDLIMIT);

		// Check that connection data has changed
		List<SWTBotGefConnectionEditPart> sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, sigGenUsesEditPart);
		Assert.assertTrue("Only one connection should exist", sourceConnections.size() == 1);
		Connection connection = (Connection) sourceConnections.get(0).part().getModel();

		UsesPortStub usesPort = (UsesPortStub) DUtil.getBusinessObject(connection.getStart());
		Assert.assertEquals("Connection uses port not correct", usesPort, DUtil.getBusinessObject((ContainerShape) sigGenUsesEditPart.part().getModel()));

		ProvidesPortStub providesPort = (ProvidesPortStub) DUtil.getBusinessObject(connection.getEnd());
		SWTBotGefEditPart dataConverterProvidesPort = DiagramTestUtils.getDiagramProvidesPort(editor, DATA_CONVERTER, "dataDouble");
		Assert.assertEquals("Connect provides port not correct", providesPort,
			DUtil.getBusinessObject((ContainerShape) dataConverterProvidesPort.part().getModel()));

		// Check that HardLimit is no longer a part of a connection
		List<SWTBotGefConnectionEditPart> providesConnections = DiagramTestUtils.getTargetConnectionsFromPort(editor, hardLimitProvidesEditPart);
		Assert.assertTrue("HardLimit should not be the target of a connection", providesConnections.isEmpty());
	}

	/**
	 * IDE-855
	 * Test editing assembly controller refid in sad.xml
	 * Ensure that edits are reflected to the diagram upon save
	 */
	@Test
	public void editAssemblyControllerInXmlTest() {
		waveformName = "Edit_Assembly_Controller_Xml";
		final String componentOne = "SigGen";
		final String componentTwo = "HardLimit";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName);
		editor = gefBot.gefEditor(waveformName);

		// Add component to the diagram
		DiagramTestUtils.dragFromPaletteToDiagram(editor, componentOne, 0, 0);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, componentTwo, 200, 0);
		MenuUtils.save(gefBot);

		// Verify componentOne is set as assembly Controller
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		ComponentShapeImpl componentShapeOne = DiagramTestUtils.getComponentShape(editor, componentOne);
		Assert.assertEquals("Setup for test is flawed, componentOne is not the assembly controller", componentShapeOne.getStartOrderText().getValue(), "0");
		ComponentShapeImpl componentShapeTwo = DiagramTestUtils.getComponentShape(editor, componentTwo);
		Assert.assertEquals("Setup for test is flawed, componentTwo is the assembly controller", componentShapeTwo.getStartOrderText().getValue(), "1");

		// Edit content of sad.xml, change assembly controller from componentOne to componentTwo
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();
		editorText = editorText.replace("<componentinstantiationref refid=\"SigGen_1\"/>", "<componentinstantiationref refid=\"HardLimit_1\"/>");
		editor.toTextEditor().setText(editorText);
		MenuUtils.save(gefBot);

		// Confirm edits reflect that componentTwo is now assembly controller
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		gefBot.sleep(2000); // Sometimes diagram takes a few seconds to update
		componentShapeOne = DiagramTestUtils.getComponentShape(editor, componentOne);
		Assert.assertEquals("Diagram does not represent newly changed assembly controller", componentShapeOne.getStartOrderText().getValue(), "1");
		componentShapeTwo = DiagramTestUtils.getComponentShape(editor, componentTwo);
		Assert.assertEquals("Diagram does not represent newly changed assembly controller", componentShapeTwo.getStartOrderText().getValue(), "0");
	}

	/**
	 * IDE-856
	 * User should be able to edit the contents of a host collocation object in the sad.xml
	 * and have the graphical representation update accordingly the next time they view the diagram.
	 */
	@Test
	public void editHostCoInXmlTest() {
		waveformName = "Edit_Host_Co_XML";
		final String HOST_CO_NAME = "HC";
		final String SIGGEN = "SigGen";
		final String HARD_LIMIT = "HardLimit";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName);
		editor = gefBot.gefEditor(waveformName);

		// Add host collocation to the waveform
		DiagramTestUtils.dragHostCollocationToDiagram(gefBot, editor, HOST_CO_NAME);

		// Add component to the diagram
		DiagramTestUtils.dragFromPaletteToDiagram(editor, SIGGEN, 20, 20);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, HARD_LIMIT, 20, 150);
		MenuUtils.save(gefBot);

		// Verify that the host collocation has two components
		ContainerShape hostCoShape = DiagramTestUtils.getHostCollocationShape(editor, HOST_CO_NAME);
		Assert.assertEquals("Two components are expected", 2, hostCoShape.getChildren().size());

		// Edit sad.xml to remove SigGen component placement
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();
		int begin = editorText.indexOf("<componentfile id=\"SigGen");
		int end = editorText.indexOf("</componentfile>", begin) + "</componentfile>".length();
		String sigGenCfText = editorText.substring(begin, end);
		begin = editorText.indexOf("<componentplacement>");
		end = editorText.indexOf("</componentplacement>", begin) + "</componentplacement>".length();
		String sigGenCpText = editorText.substring(begin, end); // save this
		editorText = editorText.substring(0, begin) + editorText.substring(end);
		editor.toTextEditor().setText(editorText);
		MenuUtils.save(gefBot);

		// Confirm SigGen component was removed from Host Collocation
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		gefBot.sleep(2000); // Sometimes diagram takes a few seconds to update
		editor.drag(HOST_CO_NAME, 20, 20); // Need to do a save to prevent override dialog from showing on next edit
		MenuUtils.save(gefBot);
		hostCoShape = DiagramTestUtils.getHostCollocationShape(editor, HOST_CO_NAME);
		Assert.assertEquals("Only one component expected", 1, hostCoShape.getChildren().size());
		ComponentShape componentShape = (ComponentShape) hostCoShape.getChildren().get(0);
		SadComponentInstantiation ci = (SadComponentInstantiation) DUtil.getBusinessObject(componentShape);
		Assert.assertEquals("HardLimit component expected", HARD_LIMIT + "_1", ci.getId());

		// Edit sad.xml to replace SigGen component
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		editorText = editor.toTextEditor().getText();
		editorText = editorText.replace("<componentfile id", sigGenCfText + " \n <componentfile id");
		editorText = editorText.replace("<componentplacement>", sigGenCpText + " \n <componentplacement>");
		editor.toTextEditor().setText(editorText);
		MenuUtils.save(gefBot);

		// Confirm SigGen component was added back to Host Collocation
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		gefBot.sleep(2000); // Sometimes diagram takes a few seconds to update
		editor.drag(HOST_CO_NAME, 20, 20); // Need to do a save to prevent override dialog from showing on next edit
		MenuUtils.save(gefBot);
		hostCoShape = DiagramTestUtils.getHostCollocationShape(editor, HOST_CO_NAME);
		Assert.assertEquals("Two components expected", 2, hostCoShape.getChildren().size());
		componentShape = (ComponentShape) hostCoShape.getChildren().get(0);
		ci = (SadComponentInstantiation) DUtil.getBusinessObject(componentShape);
		Assert.assertEquals("SigGen component expected", SIGGEN + "_1", ci.getId());

		// Edit sad.xml to change Host Collocation name
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		editorText = editor.toTextEditor().getText();
		editorText = editorText.replace("<hostcollocation name=\"" + HOST_CO_NAME + "\">", "<hostcollocation name=\"" + HOST_CO_NAME + "_1\">");
		editor.toTextEditor().setText(editorText);
		MenuUtils.save(gefBot);

		// Confirm that Host Collocation name updated in diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		gefBot.sleep(2000); // Sometimes diagram takes a few seconds to update
		SWTBotGefEditPart hostCoPart = editor.getEditPart(HOST_CO_NAME + "_1");
		Assert.assertNotNull("Host Collocatoin " + HOST_CO_NAME + "_1" + "does not exist", hostCoPart);
	}
}
