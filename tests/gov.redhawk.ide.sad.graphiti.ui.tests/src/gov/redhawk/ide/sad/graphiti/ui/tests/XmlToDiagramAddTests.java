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

import java.math.BigInteger;
import java.util.List;

import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class that deals with adding elements to the sad.xml and making sure they appear correctly in the diagram
 */
@RunWith(SWTBotJunit4ClassRunner.class) 
public class XmlToDiagramAddTests {

	private SWTGefBot gefBot;
	private SWTBotGefEditor editor;
	private String waveformName;

	@BeforeClass
	public static void beforeClass() throws Exception {
		StandardTestActions.beforeClass();
	}
	
	@Before
	public void beforeTest() throws Exception {
		gefBot = new SWTGefBot();
		StandardTestActions.beforeTest(gefBot);
	}

	@After
	public void afterTest() throws Exception {
		if (waveformName != null) {
			MenuUtils.closeAndDelete(gefBot, waveformName);
		}
		StandardTestActions.afterTest(gefBot);
	}
	
	@AfterClass
	public static void afterClass() throws Exception {
		StandardTestActions.afterClass();
	}

	/**
	 * IDE-847
	 * Add a component to the diagram via the sad.xml
	 */
	@Test
	public void addComponentInXmlTest() {
		waveformName = "Add_Component_Xml";
		final String componentOne = "SigGen";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName);
		editor = gefBot.gefEditor(waveformName);

		// Add component to the diagram
		DiagramTestUtils.dragFromPaletteToDiagram(editor, componentOne, 0, 0);
		MenuUtils.save(gefBot);

		// Edit content of sad.xml
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();

		String newComponentFile = "</componentfile> <componentfile id=\"HardLimit_304be23f-b97f-4371-b8bd-2d1922baf555\" type=\"SPD\"> "
			+ "<localfile name=\"/components/HardLimit/HardLimit.spd.xml\"/> </componentfile>";
		editorText = editorText.replace("</componentfile>", newComponentFile);

		String newComponentPlacement = "</componentplacement> <componentplacement> "
			+ "<componentfileref refid=\"HardLimit_304be23f-b97f-4371-b8bd-2d1922baf555\"/> "
			+ "<componentinstantiation id=\"HardLimit_1\" startorder=\"1\"> <usagename>HardLimit_1</usagename> "
			+ "<findcomponent> <namingservice name=\"HardLimit_1\"/> </findcomponent> </componentinstantiation> </componentplacement>";
		editorText = editorText.replace("</componentplacement>", newComponentPlacement);
		editor.toTextEditor().setText(editorText);
		gefBot.sleep(1000);
		MenuUtils.save(gefBot);
		
		// Confirm edits appear in the diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		gefBot.sleep(2000); // It can take a few seconds for the diagram to redraw
		SadComponentInstantiation componentObj = DiagramTestUtils.getComponentObject(editor, "HardLimit");
		Assert.assertEquals("Usage Name did not create correctly", "HardLimit_1", componentObj.getUsageName());
		Assert.assertEquals("Component ID did not create correctly", "HardLimit_1", componentObj.getId());
		Assert.assertEquals("Naming Service did not create correctly", "HardLimit_1", componentObj.getFindComponent().getNamingService().getName());
		Assert.assertEquals("Start Order did not create correctly", BigInteger.valueOf(1), componentObj.getStartOrder());
	}
	
	/**
	 * IDE-848
	 * Add a connection to the diagram via the sad.xml
	 */
	@Test
	public void addConnectionInXmlTest() {
		waveformName = "Add_Connection_Xml";
		final String SIGGEN = "SigGen";
		final String HARDLIMIT = "HardLimit";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName);
		editor = gefBot.gefEditor(waveformName);

		// Add component to the diagram
		DiagramTestUtils.dragFromPaletteToDiagram(editor, SIGGEN, 0, 0);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, HARDLIMIT, 200, 0);
		MenuUtils.save(gefBot);

		// Get component edit parts and container shapes
		SWTBotGefEditPart componentEditPart = editor.getEditPart(SIGGEN);
		ContainerShape containerShape = (ContainerShape) componentEditPart.part().getModel();
		
		// Confirm that no connections currently exist
		SWTBotGefEditPart sigGenUsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, SIGGEN);
		List<SWTBotGefConnectionEditPart> sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, sigGenUsesEditPart);
		Assert.assertTrue("No connections should exist", sourceConnections.isEmpty());
		
		// Edit content of sad.xml
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();
		
		String newConnection = "</assemblycontroller> <connections> <connectinterface id=\"connection_1\"> "
			+ "<usesport> <usesidentifier>out</usesidentifier> <componentinstantiationref refid=\"SigGen_1\"/>"
			+ "</usesport> <providesport> <providesidentifier>dataDouble_in</providesidentifier> "
			+ "<componentinstantiationref refid=\"HardLimit_1\"/> </providesport> </connectinterface> </connections>";
		editorText = editorText.replace("</assemblycontroller>", newConnection);
		editor.toTextEditor().setText(editorText);
		MenuUtils.save(gefBot);
		
		// Confirm edits appear in the diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		sigGenUsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, SIGGEN);
		sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, sigGenUsesEditPart);
		Assert.assertFalse("Connection should exist", sourceConnections.isEmpty());
		
		// Get port edit parts
		SWTBotGefEditPart usesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, SIGGEN);
		SWTBotGefEditPart providesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARDLIMIT);
		Connection connection = (Connection) sourceConnections.get(0).part().getModel();
		
		UsesPortStub usesPort = (UsesPortStub) DUtil.getBusinessObject(connection.getStart());
		Assert.assertEquals("Connection uses port not correct", usesPort, DUtil.getBusinessObject((ContainerShape) usesEditPart.part().getModel()));

		ProvidesPortStub providesPort = (ProvidesPortStub) DUtil.getBusinessObject(connection.getEnd());
		Assert.assertEquals("Connect provides port not correct", providesPort, DUtil.getBusinessObject((ContainerShape) providesEditPart.part().getModel()));

		Assert.assertTrue("Only arrowhead decorator should be present", connection.getConnectionDecorators().size() == 1);
		for (ConnectionDecorator decorator : connection.getConnectionDecorators()) {
			Assert.assertTrue("Only arrowhead decorator should be present", decorator.getGraphicsAlgorithm() instanceof Polyline);
		}
	}
}
