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
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import mil.jpeojtrs.sca.sad.HostCollocation;
import mil.jpeojtrs.sca.sad.SadComponentPlacement;

import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public class HostCollocationTests {

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
	 * IDE-746
	 * Create the pictogram shape in the waveform diagram that represents the Host Collocation object.
	 * This includes the ContainerShape for the component a label for the object name. 
	 */
	@Test
	public void checkHostCollocationPictogramElements() {
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
}
