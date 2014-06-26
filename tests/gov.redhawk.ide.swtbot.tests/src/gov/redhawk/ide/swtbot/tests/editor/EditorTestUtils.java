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

import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;

import java.util.List;

import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;

import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

public class EditorTestUtils { // SUPPRESS CHECKSTYLE INLINE - this utility method is intended to be public

	/**
	 * Deletes the provided part from the diagram. Part must have a context menu option for "Delete"
	 * @param editor - SWTBotGefEditor
	 * @param part - part to be delete from diagram
	 */
	public static void deleteFromDiagram(SWTBotGefEditor editor, SWTBotGefEditPart part) {
		part.select();
		editor.clickContextMenu("Delete");
	}

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
	 * 
	 * @param editor - SWTBotGefEditor
	 * @param usesEditPart - SWTBotGefEditPart for the uses/source port
	 * @param providesEditPart - SWTBotGefEditPart for the provides/target port
	 */
	public static void drawConnectionBetweenPorts(SWTBotGefEditor editor, SWTBotGefEditPart usesEditPart, SWTBotGefEditPart providesEditPart) {
		editor.activateTool("Connection");
		getDiagramPortAnchor(usesEditPart).click();
		getDiagramPortAnchor(providesEditPart).click();
	}

	/**
	 * Drills down into the ports GEF children to return the anchor point
	 * Primarily uses for making connections. Makes assumptions as to how deep the anchor is.
	 * TODO: Figure out how to grab the anchor without assuming depth, maybe a recursive loop and type check?
	 * @param portEditPart - The SWTBotGefEditPart of the port you are trying to get the anchor for
	 * @return
	 */
	public static SWTBotGefEditPart getDiagramPortAnchor(SWTBotGefEditPart portEditPart) {
		return portEditPart.children().get(0).children().get(0).children().get(0);
	}

	/**
	 * 
	 * @param editor - SWTBotGefEditor
	 * @param componentName - Component being searched
	 * @return Returns SWTBotGefEditPart for the first provides port found in the component, or null if none found
	 */
	public static SWTBotGefEditPart getDiagramProvidesPort(SWTBotGefEditor editor, String componentName) {
		return getDiagramProvidesPort(editor, componentName, null);
	}

	/**
	 * 
	 * @param editor - SWTBotGefEditor
	 * @param componentName - Component being searched
	 * @param portName - Name of port being searched for
	 * @return Returns SWTBotGefEditPart for the specified provides port, or null if none found
	 */
	public static SWTBotGefEditPart getDiagramProvidesPort(SWTBotGefEditor editor, String componentName, String portName) {
		SWTBotGefEditPart componentEditPart = editor.getEditPart(componentName);

		for (SWTBotGefEditPart child : componentEditPart.children()) {
			ContainerShape containerShape = (ContainerShape) child.part().getModel();
			Object bo = DUtil.getBusinessObject(containerShape);

			// Only return objects of type ProvidesPortStub
			if (bo == null || !(bo instanceof ProvidesPortStub)) {
				continue;
			}

			ProvidesPortStub providesPort = (ProvidesPortStub) bo;
			// If a port name was supplied then use it to check for non-matching ports
			if (portName != null && !(portName.equals(providesPort.getName()))) {
				continue;
			}

			// If you get here, the object is a ProvidesPort, and matches the name supplied (or a name wasn't given)
			return child;
		}
		return null;
	}

	/**
	 * 
	 * @param editor - SWTBotGefEditor
	 * @param componentName - Component being searched
	 * @return Returns SWTBotGefEditPart for the first uses port found in the component, or null if none found
	 */
	public static SWTBotGefEditPart getDiagramUsesPort(SWTBotGefEditor editor, String componentName) {
		return getDiagramUsesPort(editor, componentName, null);
	}

	/**
	 * 
	 * @param editor - SWTBotGefEditor
	 * @param componentName - Component being searched
	 * @param portName - Name of port being searched for
	 * @return Returns SWTBotGefEditPart for the specified uses port, or null if none found
	 */
	public static SWTBotGefEditPart getDiagramUsesPort(SWTBotGefEditor editor, String componentName, String portName) {
		SWTBotGefEditPart componentEditPart = editor.getEditPart(componentName);

		for (SWTBotGefEditPart child : componentEditPart.children()) {
			ContainerShape containerShape = (ContainerShape) child.part().getModel();
			Object bo = DUtil.getBusinessObject(containerShape);

			// Only return objects of type UsesPortStub
			if (bo == null || !(bo instanceof UsesPortStub)) {
				continue;
			}

			UsesPortStub usesPort = (UsesPortStub) bo;
			// If a port name was supplied then use it to check for non-matching ports
			if (portName != null && !(portName.equals(usesPort.getName()))) {
				continue;
			}

			// If you get here, the object is a UsesPort, and matches the name supplied (or a name wasn't given)
			return child;
		}
		return null;
	}

	/**
	 * 
	 * @param editor - SWTBotGefEditor
	 * @param portEditPart - source port edit part
	 * @return - Returns of list of SWTBotGefConnectionEditParts that includes any connection where the provided port is the source
	 */
	public static List<SWTBotGefConnectionEditPart> getSourceConnectionsFromPort(SWTBotGefEditor editor, SWTBotGefEditPart portEditPart) {
		SWTBotGefEditPart anchor = getDiagramPortAnchor(portEditPart);
		return anchor.sourceConnections();
	}

	/**
	 * 
	 * @param editor - SWTBotGefEditor
	 * @param portEditPart - target port edit part
	 * @return - Returns of list of SWTBotGefConnectionEditParts that includes any connection where the provided port is the target
	 */
	public static List<SWTBotGefConnectionEditPart> getTargetConnectionsFromPort(SWTBotGefEditor editor, SWTBotGefEditPart portEditPart) {
		SWTBotGefEditPart anchor = getDiagramPortAnchor(portEditPart);
		return anchor.targetConnections();
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
