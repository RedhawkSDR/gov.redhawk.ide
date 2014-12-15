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

import java.util.ArrayList;
import java.util.List;

import gov.redhawk.ide.graphiti.dcd.ext.impl.DeviceShapeImpl;
import gov.redhawk.ide.graphiti.dcd.ext.impl.ServiceShapeImpl;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.swtbot.NodeUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class NodeComponentTest extends AbstractGraphitiTest {

	private SWTBotGefEditor editor;
	private String projectName;
	private String domainName = "REDHAWK_DEV";
	private static final String GPP = "GPP";
	private static final String SERVICE_STUB = "ServiceStub";

	/**
	 * IDE-988
	 * Create the pictogram shape in the node diagram that represents device/service business objects.
	 * This includes ContainerShape, usage name, ID, port shapes and labels, and component supported interface.
	 */
	@Ignore
	@Test
	public void checkNodePictogramElements() {
		projectName = "PictogramShapesNode";

		// Create an empty node project
		NodeUtils.createNewNodeProject(gefBot, projectName, domainName);

		// Need to make a temp editor since the default launch page (Overview) is a SWTBotEditor and not a
		// SWTBotGefEditor
		SWTBotEditor nodeEditor = gefBot.editorByTitle(projectName);
		nodeEditor.setFocus();
		nodeEditor.bot().cTabItem("Diagram").activate();
		editor = gefBot.gefEditor(projectName);
		editor.setFocus();

		// Add to diagram from palette
		DiagramTestUtils.dragFromPaletteToDiagram(editor, GPP, 0, 0);
		DiagramTestUtils.dragFromPaletteToDiagram(editor, SERVICE_STUB, 300, 200);

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

//	/**
//	 * IDE-669
//	 * Components are removed with the delete button (trashcan image) that appears when you select the component,
//	 * but the delete context menu does not remove the component from the diagram. In most cases, the delete and
//	 * remove context menu options are grayed out and not selectable.
//	 */
//	@Test
//	public void checkComponentContextMenuDelete() {
//		waveformName = "IDE-669-Test";
//		// Create an empty waveform project
//		WaveformUtils.createNewWaveform(gefBot, waveformName);
//		editor = gefBot.gefEditor(waveformName);
//
//		for (String s : COMPONENTS) {
//			// Add component to diagram from palette
//			DiagramTestUtils.dragFromPaletteToDiagram(editor, s, 0, 0);
//		}
//
//		for (String s : COMPONENTS) {
//			// Drill down to graphiti component shape
//			SWTBotGefEditPart gefEditPart = editor.getEditPart(s);
//			DiagramTestUtils.deleteFromDiagram(editor, gefEditPart);
//			Assert.assertNull(editor.getEditPart(s));
//		}
//	}
//
//	/**
//	 * IDE-728
//	 * Components selected in the diagram should have the properties of their corresponding
//	 * model objects correctly exposed in the default Eclipse properties view.
//	 */
//	@Test
//	public void checkChangesToPropertiesReflectedInSad() {
//		waveformName = "IDE-728-Test";
//
//		WaveformUtils.createNewWaveformWithAssemblyController(gefBot, waveformName, HARD_LIMIT);
//		editor = gefBot.gefEditor(waveformName);
//		editor.getEditPart(HARD_LIMIT).click();
//		MenuUtils.showView(gefBot, "org.eclipse.ui.views.PropertySheet");
//		String propertyname = gefBot.viewByTitle("Properties").bot().tree().cell(0, "Property").toString();
//		String newValue = "0.0";
//		for (SWTBotTreeItem item : gefBot.viewByTitle("Properties").bot().tree().getAllItems()) {
//			if (item.getText().equals(propertyname)) {
//				item.click(1).pressShortcut(Keystrokes.create('0')[0]);
//				break;
//			}
//		}
//		editor.getEditPart(HARD_LIMIT).click();
//		MenuUtils.save(editor);
//		String regex = DiagramTestUtils.regexStringForSadProperty((ComponentShapeImpl) editor.getEditPart(HARD_LIMIT).part().getModel(), propertyname, newValue);
//		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
//		String editorText = editor.toTextEditor().getText();
//		Assert.assertTrue("The sad.xml should include HardLimit's changed property", editorText.matches(regex));
//	}
//
//	/**
//	 * IDE-729
//	 * New components should be added to sad.xml when the diagram is saved. All edits to components
//	 * (such as changes to the usage name) should also be reflected in the sad.xml on save.
//	 */
//	@Test
//	public void checkComponentsInSad() {
//		waveformName = "IDE-729-Test";
//
//		WaveformUtils.createNewWaveform(gefBot, waveformName);
//		editor = gefBot.gefEditor(waveformName);
//
//		// Add a SigGen component instantiation to the diagram and save
//		DiagramTestUtils.dragFromPaletteToDiagram(editor, "SigGen", 0, 0);
//		MenuUtils.save(editor);
//
//		// Add a HardLimit component instantiation to the diagram
//		DiagramTestUtils.dragFromPaletteToDiagram(editor, "HardLimit", 0, 0);
//
//		// Find expected xml string for SigGen and HardLimit components
//		final String sigGenSad = DiagramTestUtils.regexStringForSadComponent((ComponentShapeImpl) editor.getEditPart("SigGen").part().getModel());
//		final String hardLimitSad = DiagramTestUtils.regexStringForSadComponent((ComponentShapeImpl) editor.getEditPart("HardLimit").part().getModel());
//
//		// Check to see if SigGen is included in the sad.xml
//		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
//		String editorText = editor.toTextEditor().getText();
//		Assert.assertTrue("The sad.xml should include SigGen's software assembly", editorText.matches(sigGenSad));
//		Assert.assertFalse("The sad.xml should not yet include HardLimit's software assembly", editorText.matches(hardLimitSad));
//		DiagramTestUtils.openTabInEditor(editor, "Diagram");
//
//		// Save project and check to see if HardLimit is now in the sad.xml
//		MenuUtils.save(editor);
//		DiagramTestUtils.openTabInEditor(editor, waveformName + ".sad.xml");
//		editorText = editor.toTextEditor().getText();
//		Assert.assertTrue("The sad.xml should include SigGen's software assembly", editorText.matches(sigGenSad));
//		Assert.assertTrue("The sad.xml should include HardLimit's software assembly", editorText.matches(hardLimitSad));
//	}
//
//	/**
//	 * IDE-741
//	 * Palette has all Components in Target SDR.
//	 */
//	@Test
//	public void checkTargetSDRComponentsInPalete() {
//		waveformName = "IDE-741-Test";
//		// Create an empty waveform project
//		WaveformUtils.createNewWaveform(gefBot, waveformName);
//		editor = gefBot.gefEditor(waveformName);
//		editor.setFocus();
//
//		List<String> sdrComponents = WaveformComponentTest.getTargetSdrComponents(gefBot);
//		LinkedList<String> paletteComponents = new LinkedList<String>();
//		LinkedList<String> missingSdrComponentSet = new LinkedList<String>();
//		for (String compName : sdrComponents) {
//			try {
//				if (componentIsPresent(editor, compName)) {
//					paletteComponents.add(compName);
//				}
//			} catch (WidgetNotFoundException ex) {
//				missingSdrComponentSet.add(compName);
//			}
//		}
//
//		assertEquals("Missing Target SDR Components from Palette: " + missingSdrComponentSet, 0, missingSdrComponentSet.size());
//		assertEquals("Palette contains all Target SDR Components - size", sdrComponents.size(), paletteComponents.size());
//		assertEquals("Palette contains all Target SDR Components - contents", sdrComponents, paletteComponents);
//	}
//
//	private boolean componentIsPresent(SWTBotGefEditor editor, final String compName) {
//		String[] impls = { "", " (cpp)", " (java)", " (python)" };
//		for (int i = 0; i < impls.length; i++) {
//			try {
//				editor.activateTool(compName + impls[i]);
//				return true;
//			} catch (WidgetNotFoundException e) {
//				if (i == impls.length - 1) {
//					throw e;
//				} else {
//					continue;
//				}
//			}
//		}
//		return false;
//	}
//	
//
//	static List<String> getTargetSdrComponents(final SWTWorkbenchBot bot) {
//		LinkedList<String> list = new LinkedList<String>();
//
//		SWTBotView scaExplorerView = bot.viewByTitle("SCA Explorer");
//		SWTBotTree scaTree = scaExplorerView.bot().tree();
//		SWTBotTreeItem componentsItem = scaTree.expandNode("Target SDR", "Components");
//		SWTBotTreeItem[] sdrComponents = componentsItem.getItems();
//
//		for (SWTBotTreeItem item : sdrComponents) {
//			final String compName = item.getText();
//			list.add(compName);
//		}
//
//		return Collections.unmodifiableList(list);
//	}
	/**
	 * The delete context menu should not appear when ports are selected
	 */
	@Test
	public void doNotDeletePortsTest() {
		projectName = "No-Delete-Port-Test";
		// Create an empty node project
		NodeUtils.createNewNodeProject(gefBot, projectName, domainName);

		// Need to make a temp editor since the default launch page (Overview) is a SWTBotEditor and not a
		// SWTBotGefEditor
		SWTBotEditor nodeEditor = gefBot.editorByTitle(projectName);
		nodeEditor.setFocus();
		nodeEditor.bot().cTabItem("Diagram").activate();
		editor = gefBot.gefEditor(projectName);
		editor.setFocus();

		DiagramTestUtils.dragFromPaletteToDiagram(editor, GPP, 0, 0);
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
