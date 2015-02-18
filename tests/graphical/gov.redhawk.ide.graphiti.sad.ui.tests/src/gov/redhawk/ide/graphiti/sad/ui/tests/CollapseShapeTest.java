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

import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;

import java.util.List;

import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.junit.Assert;
import org.junit.Test;

public class CollapseShapeTest extends AbstractGraphitiTest {

	private SWTBotGefEditor editor;
	private String waveformName;
	
	/**
	 * IDE-1026
	 * Exercise creating several components.  Collapse all components and create connections via super ports
	 * Verify connections created, collapse and expand shapes and verify appropriate shapes are displayed and connections
	 * are tied to appropriate shapes.
	 * Verify deletion of collapsed component removes all associated connections
	 */
	@Test
	public void checkCollapseExpandComponents() {
		waveformName = "IDE-1026-checkCollapseExpandComponents";

		final String DATA_READER = "DataReader";
		final String SIGGEN = "SigGen";
		final String DATA_CONVERTER = "DataConverter";
		final String DATA_WRITER = "DataWriter";
		final String HARD_LIMIT = "HardLimit";

		// Create an empty waveform project
		WaveformUtils.createNewWaveform(gefBot, waveformName);

		// Add components to diagram from palette
		editor = gefBot.gefEditor(waveformName);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, DATA_READER, 0, 0);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, SIGGEN, 0, 150);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, DATA_CONVERTER, 300, 150);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, DATA_WRITER, 500, 0);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, HARD_LIMIT, 500, 150);

		// Get gefEditParts for port shapes
		//SWTBotGefEditPart dataReaderDataFloatOut = DiagramTestUtils.getDiagramUsesPort(editor, DATA_READER, "dataFloatOut");
		//SWTBotGefEditPart sigGenOut = DiagramTestUtils.getDiagramUsesPort(editor, SIGGEN, "out");
		SWTBotGefEditPart dataConverterDataFloat = DiagramTestUtils.getDiagramProvidesPort(editor, DATA_CONVERTER, "dataFloat");
		SWTBotGefEditPart dataConverterDataDouble = DiagramTestUtils.getDiagramProvidesPort(editor, DATA_CONVERTER, "dataDouble");
		SWTBotGefEditPart dataConverterDataFloatOut = DiagramTestUtils.getDiagramUsesPort(editor, DATA_CONVERTER, "dataFloat_out");
		SWTBotGefEditPart dataConverterDataDoubleOut = DiagramTestUtils.getDiagramUsesPort(editor, DATA_CONVERTER, "dataDouble_out");
		//SWTBotGefEditPart dataWriterDataFloat = DiagramTestUtils.getDiagramProvidesPort(editor, DATA_WRITER, "dataFloat");
		SWTBotGefEditPart hardLimitDataDoubleIn = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT, "dataDouble_in");

		//for simplicity just verify a couple port shapes
		Assert.assertTrue("DataConverter DataDouble port shape does not exist", dataConverterDataDouble != null);
		Assert.assertTrue("DataConverter DataDouble_out port shape does not exist", dataConverterDataDoubleOut != null);
		Assert.assertTrue("HardLimit DataDouble_in port shape does not exist", hardLimitDataDoubleIn != null);
				
		//collapse all shapes
		editor.setFocus();
		editor.click(300, 0);
		editor.clickContextMenu("Collapse All Shapes");
		
		//verify some port shapes were hidden
		dataConverterDataDouble = DiagramTestUtils.getDiagramUsesPort(editor, DATA_CONVERTER, "dataDouble");
		dataConverterDataDoubleOut = DiagramTestUtils.getDiagramUsesPort(editor, DATA_CONVERTER, "dataDouble_out");
		hardLimitDataDoubleIn = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT, "dataDouble_in");
		Assert.assertTrue("DataConverter DataDouble port shape exists", dataConverterDataDouble == null);
		Assert.assertTrue("DataConverter DataDouble_out port shape exists", dataConverterDataDoubleOut == null);
		Assert.assertTrue("HardLimit DataDouble_in port shape exists", hardLimitDataDoubleIn == null);
		
		//super ports
		SWTBotGefEditPart dataReaderSuperUses = DiagramTestUtils.getDiagramUsesSuperPort(editor, DATA_READER);
		SWTBotGefEditPart sigGenSuperUses = DiagramTestUtils.getDiagramUsesSuperPort(editor, SIGGEN);
		SWTBotGefEditPart dataConverterSuperProvides = DiagramTestUtils.getDiagramProvidesSuperPort(editor, DATA_CONVERTER);
		SWTBotGefEditPart dataConverterSuperUses = DiagramTestUtils.getDiagramUsesSuperPort(editor, DATA_CONVERTER);
		SWTBotGefEditPart dataWriterSuperProvides = DiagramTestUtils.getDiagramProvidesSuperPort(editor, DATA_WRITER);
		SWTBotGefEditPart hardLimitSuperProvides = DiagramTestUtils.getDiagramProvidesSuperPort(editor, HARD_LIMIT);
		
		//verify super ports exist (check a few)
		Assert.assertTrue("DataConverter Super Provides shape does not exist", dataConverterSuperProvides != null);
		Assert.assertTrue("DataConverter Super Uses port shape does not exist", dataConverterSuperUses != null);
		Assert.assertTrue("HardLimit Super Provides port shape does not exist", hardLimitSuperProvides != null);
		
		//create connections via super ports and verify
		Assert.assertTrue("Connection DataReader -> DataConverter via super ports failed", DiagramTestUtils.drawConnectionBetweenPorts(editor, dataReaderSuperUses, dataConverterSuperProvides));
		Assert.assertTrue("Connection SigGen -> DataConverter via super ports failed", DiagramTestUtils.drawConnectionBetweenPorts(editor, sigGenSuperUses, dataConverterSuperProvides));
		Assert.assertTrue("Connection DataConverter -> DataWriter via super ports failed", DiagramTestUtils.drawConnectionBetweenPorts(editor, dataConverterSuperUses, dataWriterSuperProvides));
		Assert.assertTrue("Connection DataConverter -> HardLimit via super ports failed", DiagramTestUtils.drawConnectionBetweenPorts(editor, dataConverterSuperUses, hardLimitSuperProvides));
		
		//expand data converter only
		SWTBotGefEditPart dataConverterGefEditPart = editor.getEditPart(DATA_CONVERTER);
		dataConverterGefEditPart.select();
		editor.clickContextMenu("Expand Shape");
		
		//verify data converter super ports are gone
		dataConverterSuperProvides = DiagramTestUtils.getDiagramProvidesSuperPort(editor, DATA_CONVERTER);
		dataConverterSuperUses = DiagramTestUtils.getDiagramUsesSuperPort(editor, DATA_CONVERTER);
		Assert.assertTrue("DataConverter Super Provides shape exists", dataConverterSuperProvides == null);
		Assert.assertTrue("DataConverter Super Uses port shape exists", dataConverterSuperUses == null);
		
		//verify data convert individual port shapes exist
		dataConverterDataDouble = DiagramTestUtils.getDiagramProvidesPort(editor, DATA_CONVERTER, "dataDouble");
		dataConverterDataDoubleOut = DiagramTestUtils.getDiagramUsesPort(editor, DATA_CONVERTER, "dataDouble_out");
		dataConverterDataFloatOut = DiagramTestUtils.getDiagramUsesPort(editor, DATA_CONVERTER, "dataFloat_out");
		Assert.assertTrue("DataConverter DataDouble port shape do not exist", dataConverterDataDouble != null);
		Assert.assertTrue("DataConverter DataDouble_out port shape do not exist", dataConverterDataDoubleOut != null);
		Assert.assertTrue("DataConverter DataFloat_out port shape do not exist", dataConverterDataFloatOut != null);
		
		//verify connections exist on individual ports
		//source connections
		List<SWTBotGefConnectionEditPart> sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, dataConverterDataDoubleOut);
		Assert.assertTrue("Data Converter DataDouble_out doesn't have a connection", sourceConnections.size() == 1);
		sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, dataConverterDataFloatOut);
		Assert.assertTrue("Data Converter DataFloat_out doesn't have a connection", sourceConnections.size() == 1);
		//target connections
		List<SWTBotGefConnectionEditPart> targetConnections = DiagramTestUtils.getTargetConnectionsFromPort(editor, dataConverterDataDouble);
		Assert.assertTrue("Data Converter DataDouble doesn't have a connection", targetConnections.size() == 1);
		targetConnections = DiagramTestUtils.getTargetConnectionsFromPort(editor, dataConverterDataFloat);
		Assert.assertTrue("Data Converter DataFloat doesn't have a connection", targetConnections.size() == 1);
		
		//collapse data converter
		dataConverterGefEditPart = editor.getEditPart(DATA_CONVERTER);
		dataConverterGefEditPart.select();
		editor.clickContextMenu("Collapse Shape");
		
		//verify super ports exist
		dataConverterSuperProvides = DiagramTestUtils.getDiagramProvidesSuperPort(editor, DATA_CONVERTER);
		dataConverterSuperUses = DiagramTestUtils.getDiagramUsesSuperPort(editor, DATA_CONVERTER);
		Assert.assertTrue("DataConverter Super Provides shape do not exist", dataConverterSuperProvides != null);
		Assert.assertTrue("DataConverter Super Uses port shape do not exist", dataConverterSuperUses != null);
		
		//verify connections exist on super ports
		//source connections
		sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, dataConverterSuperUses);
		Assert.assertTrue("Data Converter Super Usess Port doesn't have a 2 connections", sourceConnections.size() == 2);
		//target connections
		targetConnections = DiagramTestUtils.getTargetConnectionsFromPort(editor, dataConverterSuperProvides);
		Assert.assertTrue("Data Converter Super Provides Port doesn't have a 2 connections", targetConnections.size() == 2);
		
		//verify individual port shapes hidden
		dataConverterDataDouble = DiagramTestUtils.getDiagramProvidesPort(editor, DATA_CONVERTER, "dataDouble");
		dataConverterDataDoubleOut = DiagramTestUtils.getDiagramUsesPort(editor, DATA_CONVERTER, "dataDouble_out");
		Assert.assertTrue("DataConverter DataDouble port shape exists", dataConverterDataDouble == null);
		Assert.assertTrue("DataConverter DataDouble_out port shape exists", dataConverterDataDoubleOut == null);
		
		//expand all shapes
		editor.setFocus();
		editor.click(300, 0);
		editor.clickContextMenu("Expand All Shapes");
		
		//verify some individual port shapes exist
		dataConverterDataDouble = DiagramTestUtils.getDiagramProvidesPort(editor, DATA_CONVERTER, "dataDouble");
		dataConverterDataDoubleOut = DiagramTestUtils.getDiagramUsesPort(editor, DATA_CONVERTER, "dataDouble_out");
		hardLimitDataDoubleIn = DiagramTestUtils.getDiagramProvidesPort(editor, HARD_LIMIT, "dataDouble_in");
		Assert.assertTrue("DataConverter DataDouble port shape does not exist", dataConverterDataDouble != null);
		Assert.assertTrue("DataConverter DataDouble_out port shape does not exist", dataConverterDataDoubleOut != null);
		Assert.assertTrue("HardLimit DataDouble_in port shape does not exist", hardLimitDataDoubleIn != null);
		
		//verify connections exist on individual port shapes
		sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, dataConverterDataDoubleOut);
		Assert.assertTrue("Data Converter DataDouble_out doesn't have a connection", sourceConnections.size() == 1);
		sourceConnections = DiagramTestUtils.getSourceConnectionsFromPort(editor, dataConverterDataFloatOut);
		Assert.assertTrue("Data Converter DataFloat_out doesn't have a connection", sourceConnections.size() == 1);
		//target connections
		targetConnections = DiagramTestUtils.getTargetConnectionsFromPort(editor, dataConverterDataDouble);
		Assert.assertTrue("Data Converter DataDouble doesn't have a connection", targetConnections.size() == 1);
		targetConnections = DiagramTestUtils.getTargetConnectionsFromPort(editor, dataConverterDataFloat);
		Assert.assertTrue("Data Converter DataFloat doesn't have a connection", targetConnections.size() == 1);
		
		//collapse DataConvert Shape
		dataConverterGefEditPart = editor.getEditPart(DATA_CONVERTER);
		dataConverterGefEditPart.select();
		editor.clickContextMenu("Collapse Shape");
		
		//delete DataConverter
		dataConverterGefEditPart = editor.getEditPart(DATA_CONVERTER);
		dataConverterGefEditPart.select();
		editor.clickContextMenu("Delete");
		
		//verify no connections in diagram
		Diagram diagram = DUtil.findDiagram((ContainerShape) editor.getEditPart(HARD_LIMIT).part().getModel());
		Assert.assertTrue("No connections should exist", diagram.getConnections().isEmpty());
		
	}
}
