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

import gov.redhawk.ide.sad.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.swtbot.MenuUtils;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.diagram.FindByUtils;
import mil.jpeojtrs.sca.partitioning.FindByStub;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
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
		DiagramTestUtils.dragFromPaletteToDiagram(editor, FindByUtils.FIND_BY_CORBA_NAME, 0, 150);
		FindByUtils.completeFindByWizard(gefBot, FindByUtils.FIND_BY_CORBA_NAME, findByName, provides, uses);
		MenuUtils.save(gefBot);

		// Grab the associated business object and confirm it is a FindBy element
		SWTBotGefEditPart fbEditPart = editor.getEditPart(findByName);
		Assert.assertNotNull("FindBy Element not found", fbEditPart);
		RHContainerShapeImpl findByShape = (RHContainerShapeImpl) fbEditPart.part().getModel();
		Assert.assertTrue("Element is not a FindBy", DUtil.getBusinessObject(findByShape) instanceof FindByStub);
		FindByStub findByObject = (FindByStub) DUtil.getBusinessObject(findByShape);

		// Run assertions on expected properties: outer/inner text, lollipop, port number, type(provides-uses), name
		Assert.assertEquals("Outer Text does not match input", FindByUtils.FIND_BY_CORBA_NAME, findByShape.getOuterText().getValue());
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
		String[] findByList = { FindByUtils.FIND_BY_CORBA_NAME, FindByUtils.FIND_BY_DOMAIN_MANAGER, FindByUtils.FIND_BY_EVENT_CHANNEL,
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
			gefBot.button("Yes").click(); // are you sure you want to delete this element?
			Assert.assertNull(editor.getEditPart(s));
		}
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
		final String findByName = "FindBy";
		final String[] provides = { "data_in" };

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName);
		editor = gefBot.gefEditor(waveformName);

		// Add component to the diagram
		DiagramTestUtils.dragFromPaletteToDiagram(editor, SIGGEN, 0, 0);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, FindByUtils.FIND_BY_CORBA_NAME, 0, 150);
		FindByUtils.completeFindByWizard(gefBot, FindByUtils.FIND_BY_CORBA_NAME, findByName, provides, null);
		MenuUtils.save(gefBot);

		// Create connection on diagram
		SWTBotGefEditPart sigGenUsesPart = DiagramTestUtils.getDiagramUsesPort(editor, SIGGEN);
		SWTBotGefEditPart findByProvidesPart = DiagramTestUtils.getDiagramProvidesPort(editor, findByName);
		DiagramTestUtils.drawConnectionBetweenPorts(editor, sigGenUsesPart, findByProvidesPart);
		MenuUtils.save(gefBot);

		// Check sad.xml for connection
		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
		String editorText = editor.toTextEditor().getText();
		Assert.assertTrue("The sad.xml should include a new connection. Expected: <findby><namingservice name=\"" + findByName + "\"/>",
			editorText.matches("(?s).*" + "<connectinterface.*<findby>.*<namingservice name=\"" + findByName + "\"/>" + ".*"));

	}
}
