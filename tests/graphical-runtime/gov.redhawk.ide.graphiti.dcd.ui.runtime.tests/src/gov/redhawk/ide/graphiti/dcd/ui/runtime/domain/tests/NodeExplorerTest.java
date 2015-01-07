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

import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
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
	 * IDE-998
	 * Opens Graphiti Node Explorer diagram
	 * This editor is "look but don't touch". All design functionality should be disabled.
	 * Runtime functionality (start/stop, plot, etc) should still work.
	 */
	@Test
	public void nodeExplorerTest() {
		// Need to make a temp editor since the default launch page (Overview) is a SWTBotEditor and not a
		// SWTBotGefEditor
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

		DiagramTestUtils.startComponentFromDiagram(editor, gppFullName);
		ScaExplorerTestUtils.waitUntilComponentAppearsStartedInScaExplorer(bot, DOMAIN_NODE_PARENT_PATH, getNodeFullName(), gppFullName);

		// check that device is removed from editor when released in the Sca Explorer
		String[] GPP_PARENT_PATH = { DOMAIN_NODE_PARENT_PATH[0], DOMAIN_NODE_PARENT_PATH[1], getNodeFullName() };
		ScaExplorerTestUtils.releaseFromScaExplorer(bot, GPP_PARENT_PATH, gppFullName);
	}
}
