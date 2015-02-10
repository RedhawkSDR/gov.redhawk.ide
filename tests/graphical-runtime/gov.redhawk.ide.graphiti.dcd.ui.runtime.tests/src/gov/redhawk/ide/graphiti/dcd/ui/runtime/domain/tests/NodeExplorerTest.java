/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.graphiti.dcd.ui.runtime.domain.tests;

import static org.junit.Assert.assertEquals;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
//import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.junit.Assert;
import org.junit.Test;

public class NodeExplorerTest extends AbstractGraphitiDomainNodeRuntimeTest {

	private SWTBotGefEditor editor;
	private static final String GPP = "GPP";
	private static final String DEVICE_STUB = "DeviceStub";

	/**
	 * IDE-998 Opens Graphiti Node Explorer diagram.
	 * This editor is "look but don't touch". All design functionality should be disabled.
	 * Runtime functionality (start/stop, plot, etc) should still work.
	 * IDE-1001 Hide grid on runtime diagram.
	 * IDE-1038, IDE-1064 No Undo Start|Stop|Terminate|Do Command
	 */
	@Test
	public void nodeExplorerTest() {
		// IDE-1089 test
		SWTBotEditor nodeEditor = gefBot.editorByTitle(getNodeFullName());
		nodeEditor.setFocus();
		nodeEditor.bot().cTabItem("Diagram").activate();
		editor = gefBot.gefEditor(getNodeFullName());
		editor.setFocus();

		// check for devices
		String hostName = getNodeFullName().substring(getNodeFullName().indexOf("_"));
		String gppFullName = GPP + hostName;
		SWTBotGefEditPart gpp = editor.getEditPart(gppFullName);
		Assert.assertNotNull(gppFullName + " device not found in diagram", gpp);

		// check that delete option does not appear
		gpp.select();
		String[] removedContextOptions = { "Delete", "Release", "Terminate" };
		for (String contextOption : removedContextOptions) {
			try {
				editor.clickContextMenu(contextOption);
				Assert.fail(); // The only way to get here is if the undesired context menu option appears
			} catch (WidgetNotFoundException e) {
				Assert.assertEquals(e.getMessage(), contextOption, e.getMessage());
			}
		}

		// check that start/stop works
		DiagramTestUtils.stopComponentFromDiagram(editor, gppFullName);
		ScaExplorerTestUtils.waitUntilComponentAppearsStoppedInScaExplorer(bot, DOMAIN_NODE_PARENT_PATH, getNodeFullName(), gppFullName);
		Assert.assertFalse("IDE-1038 No Undo Stop Command context menu item", DiagramTestUtils.hasContentMenuItem(editor, gppFullName, "Undo Stop Command"));
		Assert.assertFalse("IDE-1065 No Undo Do Command context menu item", DiagramTestUtils.hasContentMenuItem(editor, gppFullName, "Undo Do Command"));

		DiagramTestUtils.startComponentFromDiagram(editor, gppFullName);
		ScaExplorerTestUtils.waitUntilComponentAppearsStartedInScaExplorer(bot, DOMAIN_NODE_PARENT_PATH, getNodeFullName(), gppFullName);
		Assert.assertFalse("IDE-1038 No Undo Start Command context menu item", DiagramTestUtils.hasContentMenuItem(editor, gppFullName, "Undo Start Command"));
		Assert.assertFalse("IDE-1065 No Undo Do Command context menu item", DiagramTestUtils.hasContentMenuItem(editor, gppFullName, "Undo Do Command"));

		// check that device is removed from editor when released in the Sca Explorer
		String[] GPP_PARENT_PATH = { DOMAIN_NODE_PARENT_PATH[0], DOMAIN_NODE_PARENT_PATH[1], getNodeFullName() };
		ScaExplorerTestUtils.releaseFromScaExplorer(bot, GPP_PARENT_PATH, gppFullName);

		// IDE-1001 check that grid is hidden on runtime diagram
		Diagram diagram = DiagramTestUtils.getDiagram(editor);
		Assert.assertNotNull("Found in Diagram (model object) on editor", diagram);
		int gridUnit = diagram.getGridUnit();
		assertEquals("Grid is hidden on runtime Node/DeviceMgr diagram", -1, gridUnit); // -1 means it is hidden

		// Confirm that .dcd.xml is visible
		DiagramTestUtils.openTabInEditor(editor, "DeviceManager.dcd.xml");
	}
}
