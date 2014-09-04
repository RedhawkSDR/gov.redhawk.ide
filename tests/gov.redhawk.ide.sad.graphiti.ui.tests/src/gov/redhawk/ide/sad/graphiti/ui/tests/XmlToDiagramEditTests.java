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
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;

import java.math.BigInteger;
import java.util.List;

import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

import org.eclipse.graphiti.mm.pictograms.Connection;
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
 * Test class that deals with editing elements to the sad.xml and making sure they appear correctly in the diagram
 */
@RunWith(SWTBotJunit4ClassRunner.class) 
public class XmlToDiagramEditTests {

	private SWTGefBot gefBot;
	private SWTBotGefEditor editor;
	private String waveformName;

	@BeforeClass
	public static void beforeClass() throws Exception {
		StandardTestActions.beforeClass();
		StandardTestActions.setTargetSdr(GraphitiTestsActivator.PLUGIN_ID);
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
		gefBot.sleep(10000); // Sometimes diagram takes a few seconds to update
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
		Assert.assertEquals("Connect provides port not correct", providesPort, DUtil.getBusinessObject((ContainerShape) dataConverterProvidesPort.part().getModel()));
		
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
}
