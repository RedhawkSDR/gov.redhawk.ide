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
package gov.redhawk.ide.sad.graphiti.ui.runtime.tests;

import gov.redhawk.ide.sad.graphiti.ext.impl.ComponentShapeImpl;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class StartOrderRuntimeTest extends AbstractGraphitiRuntimeTest {

	private SWTBotGefEditor editor;
	private static final String CHALKBOARD = "Chalkboard";
	private static final String SIGGEN = "SigGen";

	/**
	 * IDE-326
	 * Test to make sure the Start Order ellipse doesn't not get drawn in the sandbox
	 */
	@Ignore
	@Test
	public void removeStartOrderIconTest() {
		// Prepare Graphiti diagram
		SWTBotView scaExplorerView = bot.viewById("gov.redhawk.ui.sca_explorer");
		DiagramTestUtils.openChalkboardFromSandbox(gefBot);
		editor = gefBot.gefEditor(CHALKBOARD);
		editor.setFocus();

		DiagramTestUtils.dragFromPaletteToDiagram(editor, SIGGEN, 0, 0);
		final SWTBotTreeItem chalkboard = scaExplorerView.bot().tree().expandNode("Sandbox", "Chalkboard");
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return SIGGEN + " Component did not load into sandbox";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotTreeItem[] items = chalkboard.getItems();
				for (SWTBotTreeItem item : items) {
					if (item.getText().equals(SIGGEN + "_1")) {
						return true;
					}
				}
				return false;
			}
		});

		SWTBotGefEditPart sigGenEditPart = editor.getEditPart(SIGGEN);
		ComponentShapeImpl componentShape = (ComponentShapeImpl) sigGenEditPart.part().getModel();
		Assert.assertNull("Start Order ellipse should not be created during runtime", componentShape.getStartOrderEllipseShape());
	}
}
