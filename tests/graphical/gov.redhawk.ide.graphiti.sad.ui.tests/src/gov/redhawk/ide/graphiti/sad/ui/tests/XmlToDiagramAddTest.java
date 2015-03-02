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

import gov.redhawk.ide.graphiti.sad.ext.ComponentShape;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;

import java.math.BigInteger;
import java.util.List;

import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.HostCollocation;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class that deals with adding elements to the sad.xml and making sure they appear correctly in the diagram
 */
public class XmlToDiagramAddTest extends AbstractGraphitiTest {

	private SWTBotGefEditor editor;
	private String waveformName;

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
		DiagramTestUtils.addFromPaletteToDiagram(editor, componentOne, 0, 0);
		MenuUtils.save(editor);

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
		MenuUtils.save(editor);

		// Confirm edits appear in the diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");

		SadComponentInstantiation componentObj = DiagramTestUtils.getComponentObject(editor, "HardLimit");
		Assert.assertNotNull(componentObj);
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
		DiagramTestUtils.addFromPaletteToDiagram(editor, SIGGEN, 0, 0);
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARDLIMIT, 200, 0);
		MenuUtils.save(editor);

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
		MenuUtils.save(editor);

		// Confirm edits appear in the diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");

		sigGenUsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, SIGGEN);
		SWTBotGefEditPart hardLimitProvidesEditPart = DiagramTestUtils.getDiagramProvidesPort(editor, HARDLIMIT);

		sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, sigGenUsesEditPart);
		Assert.assertFalse("Connection should exist", sourceConnections.isEmpty());

		Connection connection = (Connection) sourceConnections.get(0).part().getModel();
		UsesPortStub usesPort = (UsesPortStub) DUtil.getBusinessObject(connection.getStart());
		Assert.assertEquals("Connection uses port not correct", usesPort, DUtil.getBusinessObject((ContainerShape) sigGenUsesEditPart.part().getModel()));

		ProvidesPortStub providesPort = (ProvidesPortStub) DUtil.getBusinessObject(connection.getEnd());
		Assert.assertEquals("Connect provides port not correct", providesPort,
			DUtil.getBusinessObject((ContainerShape) hardLimitProvidesEditPart.part().getModel()));

		Assert.assertTrue("Only arrowhead decorator should be present", connection.getConnectionDecorators().size() == 1);
		for (ConnectionDecorator decorator : connection.getConnectionDecorators()) {
			Assert.assertTrue("Only arrowhead decorator should be present", decorator.getGraphicsAlgorithm() instanceof Polyline);
		}
	}

	/**
	 * IDE-849
	 * Add a host collocation to the diagram via the sad.xml
	 */
	@Test
	public void addHostCollocationInXmlTest() {
		waveformName = "Add_Host_Collocation_Xml";
		final String HOSTCOLLOCATION_INSTANCE_NAME = "Host A";
		final String HARDLIMIT = "HardLimit";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName);
		editor = gefBot.gefEditor(waveformName);
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARDLIMIT, 400, 0);
		MenuUtils.save(editor);

		// Edit content of sad.xml
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();

		// add host collocation
		editorText = editorText.replace("<componentplacement>", "<hostcollocation name=\"" + HOSTCOLLOCATION_INSTANCE_NAME + "\"><componentplacement>");

		editorText = editorText.replace("</partitioning>", "</hostcollocation></partitioning>");

		editor.toTextEditor().setText(editorText);
		MenuUtils.save(editor);

		// Confirm edits appear in the diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");

		// check the shapes are drawing properly
		ContainerShape hostCollocationShape = DiagramTestUtils.getHostCollocationShape(editor, HOSTCOLLOCATION_INSTANCE_NAME);
		Assert.assertTrue(HOSTCOLLOCATION_INSTANCE_NAME + " host collocation shape does not exist", hostCollocationShape != null);
		ComponentShape componentShape = DiagramTestUtils.getComponentShape(editor, HARDLIMIT);
		Assert.assertTrue(HARDLIMIT + " component shape does not exist", componentShape != null);
		Assert.assertTrue(HARDLIMIT + " component shape does not exist within " + HOSTCOLLOCATION_INSTANCE_NAME,
			DiagramTestUtils.childShapeExists(hostCollocationShape, componentShape));

		// verify component exists within host collocation
		HostCollocation hostCollocation = DiagramTestUtils.getHostCollocationObject(editor, HOSTCOLLOCATION_INSTANCE_NAME);
		Assert.assertTrue(HOSTCOLLOCATION_INSTANCE_NAME + " host collocation object does not exist", hostCollocation != null);
		Assert.assertTrue(HARDLIMIT + " component object does not exist within " + HOSTCOLLOCATION_INSTANCE_NAME,
			hostCollocation.getComponentPlacement().size() == 1 && hostCollocation.getComponentPlacement().get(0).getComponentInstantiation().size() == 1);
	}

	/**
	 * IDE-847
	 * Add a component to the diagram via the sad.xml
	 */
	@Test
	public void addFindByInXmlTest() {
		waveformName = "Add_FindBy_Xml";
		final String SIGGEN = "SigGen";
		final String SIGGEN_PORT = "out";
		final String FIND_BY_NAME = "FindByName";
		final String NAME_PORT = "NamePort";
		final String FIND_BY_SERVICE = "FindByService";
		final String SERVICE_PORT = "ServicePort";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName);
		editor = gefBot.gefEditor(waveformName);

		// Add component to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, SIGGEN, 0, 0);
		MenuUtils.save(editor);

		// Edit content of sad.xml
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();

		String newFindByConnections = "</assemblycontroller> <connections> <connectinterface id=\"connection_1\"> <usesport> "
			+ "<usesidentifier>out</usesidentifier> <componentinstantiationref refid=\"SigGen_1\"/> " + "</usesport> <providesport> <providesidentifier>"
			+ NAME_PORT + "</providesidentifier> <findby> " + "<namingservice name=\"" + FIND_BY_NAME + "\"/> </findby> </providesport> </connectinterface> "
			+ "<connectinterface id=\"connection_2\"> <usesport> <usesidentifier>out</usesidentifier> "
			+ "<componentinstantiationref refid=\"SigGen_1\"/> </usesport> <providesport> " + "<providesidentifier>" + SERVICE_PORT
			+ "</providesidentifier> <findby> " + "<domainfinder name=\"" + FIND_BY_SERVICE + "\" type=\"servicename\"/> </findby> </providesport> "
			+ "</connectinterface> </connections>";

		editorText = editorText.replace("</assemblycontroller>", newFindByConnections);
		editor.toTextEditor().setText(editorText);
		// Need to allow the editor to get the changes
		MenuUtils.save(editor);

		// Confirm edits appear in the diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");
		FindByStub findByNameObject = DiagramTestUtils.getFindByObject(editor, FIND_BY_NAME);
		FindByStub findByServiceObject = DiagramTestUtils.getFindByObject(editor, FIND_BY_SERVICE);

		// Check find by object names
		Assert.assertNotNull(findByNameObject);
		Assert.assertNotNull(findByNameObject.getNamingService());
		Assert.assertEquals("Naming Service did not create correctly", FIND_BY_NAME, findByNameObject.getNamingService().getName());
		Assert.assertEquals("Domain Finder did not create correctly", FIND_BY_SERVICE, findByServiceObject.getDomainFinder().getName());

		// Check port names
		Assert.assertEquals("FindBy Name provides port did not create as expected", NAME_PORT, findByNameObject.getProvides().get(0).getName());
		Assert.assertEquals("FindBy Service provides port did not create as expected", SERVICE_PORT, findByServiceObject.getProvides().get(0).getName());

		// Check that connections were made
		SWTBotGefEditPart usesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, SIGGEN);
		List<SWTBotGefConnectionEditPart> connections = DiagramTestUtils.getSourceConnectionsFromPort(editor, usesEditPart);

		SWTBotGefConnectionEditPart nameConnectionPart = connections.get(0);
		Connection nameConnection = (Connection) nameConnectionPart.part().getModel();
		UsesPortStub nameConnectionSource = (UsesPortStub) DUtil.getBusinessObject(nameConnection.getStart());
		ProvidesPortStub nameConnectionTarget = (ProvidesPortStub) DUtil.getBusinessObject(nameConnection.getEnd());
		Assert.assertEquals("FindByName connection source incorrect", SIGGEN_PORT, nameConnectionSource.getName());
		Assert.assertEquals("FindByName connection target incorrect", NAME_PORT, nameConnectionTarget.getName());

		SWTBotGefConnectionEditPart serviceConnectionPart = connections.get(1);
		Connection serviceConnection = (Connection) serviceConnectionPart.part().getModel();
		UsesPortStub serviceConnectionSource = (UsesPortStub) DUtil.getBusinessObject(serviceConnection.getStart());
		ProvidesPortStub serviceConnectionTarget = (ProvidesPortStub) DUtil.getBusinessObject(serviceConnection.getEnd());
		Assert.assertEquals("FindByService connection source incorrect", SIGGEN_PORT, serviceConnectionSource.getName());
		Assert.assertEquals("FindByService connection target incorrect", SERVICE_PORT, serviceConnectionTarget.getName());
	}
	
	

	/**
	 * IDE-978, IDE-965
	 * Add an external port to the diagram via the sad.xml
	 */
	@Test
	public void addExternalPortsInXmlTest() {
		waveformName = "Add_ExternalPort_Xml";
		final String HARDLIMIT = "HardLimit";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName);
		editor = gefBot.gefEditor(waveformName);

		// Add component to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, HARDLIMIT, 200, 0);
		MenuUtils.save(editor);

		// Confirm that no external ports exist in diagram
		SWTBotGefEditPart hardLimitUsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, HARDLIMIT);
		DiagramTestUtils.assertExternalPort(hardLimitUsesEditPart, false);
		
		//switch to overview tab and verify there are no external ports
		DiagramTestUtils.openTabInEditor(editor, "Overview");
		Assert.assertEquals("There are external ports", 0, bot.table(0).rowCount());
		
		// Edit content of sad.xml
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();

		String externalports = "</assemblycontroller> <externalports><port>"
			+ "<usesidentifier>dataDouble_out</usesidentifier>"
			+ "<componentinstantiationref refid=\"HardLimit_1\"/>"
			+ "</port> </externalports>";
		editorText = editorText.replace("</assemblycontroller>", externalports);
		editor.toTextEditor().setText(editorText);
		MenuUtils.save(editor);

		// Confirm edits appear in the diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");

		//assert port set to external in diagram
		hardLimitUsesEditPart = DiagramTestUtils.getDiagramUsesPort(editor, HARDLIMIT);
		DiagramTestUtils.assertExternalPort(hardLimitUsesEditPart, true);
		
		//switch to overview tab and verify there are external ports
		DiagramTestUtils.openTabInEditor(editor, "Overview");
		Assert.assertEquals("There are no external ports", 1, bot.table(0).rowCount());
	}
	
	/**
	 * IDE-124
	 * Add use device to the diagram via the sad.xml
	 */
	@Test
	public void addUseDeviceInXmlTest() {
		waveformName = "Add_UseDevice_Xml";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName);
		editor = gefBot.gefEditor(waveformName);

		// Edit content of sad.xml
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();

		String usesDevice = "<assemblycontroller/><usesdevicedependencies><usesdevice id=\"FrontEndTuner_1\"></usesdevicedependencies>";
		editorText = editorText.replace("<assemblycontroller/>", usesDevice);
		editor.toTextEditor().setText(editorText);

		// Confirm edits appear in the diagram
		DiagramTestUtils.openTabInEditor(editor, "Diagram");

		SWTBotGefEditPart useDeviceEditPart = editor.getEditPart(UsesDeviceTest.USE_DEVICE);
		UsesDeviceTest.assertUsesDevice(useDeviceEditPart);
	}

}
