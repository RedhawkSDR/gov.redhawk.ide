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
package gov.redhawk.ide.swtbot.tests.editor;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

public class EditorUtils { // SUPPRESS CHECKSTYLE INLINE - this utility method is intended to be public


	/**
	 * Drag a component onto the SAD diagram editor from the Palette.
	 * Position is determined relative to the last item dropped on the diagram.
	 * @param editor - SWTBotGefEditor
	 * @param componentName - Component to grab from palette
	 * @param xTargetPosition - x coordinate for drop location
	 * @param yTargetPosition - y coordinate for drop location
	 */
	public static void dragFromPaletteToDiagram(SWTBotGefEditor editor, String componentName, int xTargetPosition, int yTargetPosition) {
		editor.activateTool(componentName);
		editor.drag(xTargetPosition, yTargetPosition, xTargetPosition, yTargetPosition);
	}
	
	/**
	 * Open the diagram editor from an existing sad.xml in the Project Explorer
	 * @param waveformName - name of waveform that will be opened
	 */
	public static void openSadDiagram(SWTWorkbenchBot wbBot, String waveformName) {
		// TODO develop static method to open SAD editor from sad.xml

		// Expand tree view to sad.xml
		SWTBotView explorerView = wbBot.viewByTitle("Project Explorer");
		SWTBotTreeItem[] projects = explorerView.bot().tree().getAllItems();
		for (SWTBotTreeItem project : projects) {
			if (waveformName.equals(project.getText())) {
				for (SWTBotTreeItem childElement : project.expand().getItems()) {
					// PASS
					// System.out.println("child: " + childElement.getText());
					// TODO: Select the node for the sad.xml and double-click
					// TODO: Make sure the diagram tab in the SAD editor is selected
				}
			}
		}
	}
}
