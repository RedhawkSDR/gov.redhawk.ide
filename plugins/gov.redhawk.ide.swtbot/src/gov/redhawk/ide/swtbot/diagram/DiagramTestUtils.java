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
package gov.redhawk.ide.swtbot.diagram;

import java.lang.reflect.Field;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.platform.GraphitiShapeEditPart;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefConnectionEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefFigureCanvas;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefViewer;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotLabel;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Assert;

import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.sad.ext.ComponentShape;
import gov.redhawk.ide.graphiti.sad.internal.ui.editor.GraphitiWaveformMultiPageEditor;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.StyleUtil;
import gov.redhawk.logging.ui.LogLevels;
import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;
import mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub;
import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.HostCollocation;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

public class DiagramTestUtils extends AbstractGraphitiTest {

	/** hide constructor, since all functions are static. */
	private DiagramTestUtils() {
	}

	public static final String OVERVIEW_TAB = "Overview";
	public static final String PROPERTIES_TAB = "Properties";
	public static final String PORTS_TAB = "Ports";
	public static final String IMPLEMENTATIONS = "Implementations";
	public static final String DIAGRAM_TAB = "Diagram";
	public static final String XML_TAB = ".sad.xml";

	static final long DELAY_MS = 100;

	/**
	 * Deletes the provided part from the diagram. Part must have a context menu option for "Delete"
	 * @param editor - SWTBotGefEditor
	 * @param part - part to be delete from diagram
	 */
	public static void deleteFromDiagram(SWTBotGefEditor editor, SWTBotGefEditPart part) {
		part.select();
		editor.clickContextMenu("Delete");
		SWTUtils.sleep(DELAY_MS);
	}

	public static void releaseFromDiagram(SWTBotGefEditor editor, SWTBotGefEditPart part) {
		part.select();
		editor.clickContextMenu("Release");
		SWTUtils.sleep(DELAY_MS);
	}

	public static void terminateFromDiagram(SWTBotGefEditor editor, SWTBotGefEditPart part) {
		part.select();
		editor.clickContextMenu("Terminate");
		SWTUtils.sleep(DELAY_MS);
	}

	/**
	 * Adds a component onto the SAD diagram editor from the palette
	 * @param editor
	 * @param componentName - Component to grab from palette (e.g. 'foo' or 'a.b.foo')
	 * @param xTargetPosition - x coordinate for drop location
	 * @param yTargetPosition - y coordinate for drop location
	 */
	public static void addFromPaletteToDiagram(final RHBotGefEditor editor, String componentName, int xTargetPosition, int yTargetPosition) {
		editor.activateNamespacedTool(componentName.split("\\."));
		editor.click(xTargetPosition, yTargetPosition);
	}

	private static void dragFromTargetSDRToDiagram(SWTGefBot gefBot, SWTBotGefEditor editor, String componentName, String sdrLocation) {
		SWTBotView scaExplorerView = gefBot.viewByTitle("REDHAWK Explorer");
		SWTBotTree scaTree = scaExplorerView.bot().tree();
		SWTBotTreeItem componentTreeItem = scaTree.expandNode("Target SDR", sdrLocation).expandNode(componentName.split("\\."));

		SWTBotGefViewer viewer = editor.getSWTBotGefViewer();
		SWTBotGefFigureCanvas canvas = null;

		for (Field f : viewer.getClass().getDeclaredFields()) {
			if ("canvas".equals(f.getName())) {
				f.setAccessible(true);
				try {
					canvas = (SWTBotGefFigureCanvas) f.get(viewer);
				} catch (IllegalArgumentException e) {
					throw new IllegalStateException(e);
				} catch (IllegalAccessException e) {
					throw new IllegalStateException(e);
				}
			}
		}

		Assert.assertNotNull(canvas);
		componentTreeItem.dragAndDrop(canvas);
	}

	/**
	 * Drag a component onto the SAD diagram editor from the Target SDR.
	 * @param gefBot
	 * @param editor
	 * @param componentName
	 */
	public static void dragComponentFromTargetSDRToDiagram(SWTGefBot gefBot, SWTBotGefEditor editor, String componentName) {
		dragFromTargetSDRToDiagram(gefBot, editor, componentName, "Components");
	}

	/**
	 * Drag a device onto the SAD diagram editor from the Target SDR.
	 * @param gefBot
	 * @param editor
	 * @param deviceName
	 */
	public static void dragDeviceFromTargetSDRToDiagram(SWTGefBot gefBot, SWTBotGefEditor editor, String deviceName) {
		dragFromTargetSDRToDiagram(gefBot, editor, deviceName, "Devices");
	}

	/**
	 * Drag a service onto the SAD diagram editor from the Target SDR.
	 * @param gefBot
	 * @param editor
	 * @param deviceName
	 */
	public static void dragServiceFromTargetSDRToDiagram(SWTGefBot gefBot, SWTBotGefEditor editor, String deviceName) {
		dragFromTargetSDRToDiagram(gefBot, editor, deviceName, "Services");
	}

	/**
	 * Add a HostCollocation to the SAD diagram editor
	 */
	public static void addHostCollocationToDiagram(SWTGefBot gefBot, RHBotGefEditor editor, String hostCoName) {
		addFromPaletteToDiagram(editor, "Host Collocation", 0, 0);

		SWTBotGefEditPart editPart = editor.getEditPart("collocation_1");
		editPart.select();

		// Set the DiagramBehavior's mouse position to point to the collocation name, otherwise activating direct
		// editing will fail to activate.
		ContainerShape shape = (ContainerShape) editPart.part().getModel();
		Text name = getHostCollocationText(shape);
		final int textX = name.getX() + name.getWidth() / 2;
		final int textY = name.getY() + name.getHeight() / 2;
		GraphitiWaveformMultiPageEditor diagramEditor = (GraphitiWaveformMultiPageEditor) editor.getReference().getEditor(false);
		final DiagramBehavior diagramBehavior = diagramEditor.getDiagramEditor().getDiagramBehavior();
		diagramBehavior.getMouseLocation().setLocation(textX, textY);

		editPart.activateDirectEdit();
		editor.directEditType(hostCoName);
	}

	private static Text getHostCollocationText(ContainerShape shape) {
		for (GraphicsAlgorithm ga : shape.getGraphicsAlgorithm().getGraphicsAlgorithmChildren()) {
			if (ga instanceof Text) {
				return (Text) ga;
			}
		}
		return null;
	}

	/**
	 * Add a Use FrontEnd Tuner Device to the SAD diagram editor
	 */
	public static void addUseFrontEndTunerDeviceToDiagram(SWTGefBot gefBot, RHBotGefEditor editor) {
		addUseFrontEndTunerDeviceToDiagram(gefBot, editor, 0, 0);
	}

	/**
	 * Add a Use FrontEnd Tuner Device to the SAD diagram editor at the specified coordinates
	 */
	public static void addUseFrontEndTunerDeviceToDiagram(SWTGefBot gefBot, RHBotGefEditor editor, int xPosition, int yPosition) {
		addFromPaletteToDiagram(editor, "Use FrontEnd Tuner Device", xPosition, yPosition);
	}

	/**
	 * Draws a connection between two ports using drag-and-drop on the editor.
	 * The editor should be created from an RHTestBot.
	 *
	 * @param editor - SWTBotGefEditor
	 * @param sourceEditPart - SWTBotGefEditPart for the port to start the connection at
	 * @param targetEditPart - SWTBotGefEditPart for the port to end the connection at
	 */
	public static boolean drawConnectionBetweenPorts(SWTBotGefEditor editor, SWTBotGefEditPart sourceEditPart, SWTBotGefEditPart targetEditPart) {
		final SWTBotGefEditPart sourceAnchor = getDiagramPortAnchor(sourceEditPart);
		final SWTBotGefEditPart targetAnchor = getDiagramPortAnchor(targetEditPart);

		// Count original number of connections on each port for comparison
		final int numSourceConnections = sourceAnchor.sourceConnections().size();
		final int numTargetConnections = targetAnchor.targetConnections().size();

		final Point sourcePos = getDiagramRelativeCenter(sourceAnchor);
		final Point targetPos = getDiagramRelativeCenter(targetAnchor);
		editor.drag(sourcePos.x, sourcePos.y, targetPos.x, targetPos.y);

		// Wait to see if new connection appears for both ports
		try {
			editor.bot().waitWhile(new ICondition() {
				@Override
				public boolean test() throws Exception {
					return targetAnchor.targetConnections().size() <= numTargetConnections || sourceAnchor.sourceConnections().size() <= numSourceConnections;
				}

				@Override
				public void init(SWTBot bot) {
				}

				@Override
				public String getFailureMessage() {
					return "Failed to create connection";
				}
			}, 2000, 500);
		} catch (TimeoutException e) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the center of the edit part, relative to the diagram
	 * @param part The edit part for which to find the center
	 * @return The center of the part
	 */
	public static Point getDiagramRelativeCenter(SWTBotGefEditPart part) {
		final IFigure figure = ((GraphicalEditPart) part.part()).getFigure();
		final Rectangle bounds = figure.getBounds().getCopy();
		figure.translateToAbsolute(bounds);
		int posX = bounds.x + bounds.width / 2;
		int posY = bounds.y + bounds.height / 2;
		return new Point(posX, posY);
	}

	/**
	 * Utility method to extract business object from a component in the Graphiti diagram.
	 * Returns null if object not found
	 * @param editor
	 * @param componentName
	 * @return
	 */
	public static SadComponentInstantiation getComponentObject(SWTBotGefEditor editor, String componentName) {
		ComponentShape componentShape = getComponentShape(editor, componentName);
		if (componentShape == null) {
			return null;
		}
		return DUtil.getBusinessObject(componentShape, SadComponentInstantiation.class);
	}

	/**
	 * Utility method to extract ComponentShape from the Graphiti diagram with the provided componentName.
	 * Returns null if object not found
	 * @param editor
	 * @param componentName
	 * @return
	 */
	public static ComponentShape getComponentShape(SWTBotGefEditor editor, String componentName) {
		return (ComponentShape) getRHContainerShape(editor, componentName);
	}
	
	/**
	 * 
	 * @param editor
	 * @param shapeName
	 * @return the edit part for the shapes component supported interface, or null if not found
	 */
	public static SWTBotGefEditPart getComponentSupportedInterface(SWTBotGefEditor editor, String shapeName) {
		// Find the component first
		SWTBotGefEditPart shapeEditPart = editor.getEditPart(shapeName);
		Assert.assertNotNull(shapeEditPart);

		// Find the child part whose business object is the CSI
		List<SWTBotGefEditPart> csiEditParts = shapeEditPart.descendants(new BaseMatcher<EditPart>() {

			@Override
			public boolean matches(Object item) {
				if (!(item instanceof GraphitiShapeEditPart)) {
					return false;
				}
				GraphitiShapeEditPart editPart = (GraphitiShapeEditPart) item;
				Object businessObject = DUtil.getBusinessObject(editPart.getPictogramElement());
				return businessObject instanceof ComponentSupportedInterfaceStub;
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("is component supported interface edit part");
			}
		});
		Assert.assertEquals(1, csiEditParts.size());

		// The anchor is the only child of the part we found
		return csiEditParts.get(0);
	}

	/**
	 * Utility method to get {@link Diagram} from the GEF Editor.
	 * @return the {@link Diagram} from the main editor's part's model or null if not found
	 */
	public static Diagram getDiagram(SWTBotGefEditor editor) {
		Object model = editor.mainEditPart().part().getModel();
		if (model instanceof Diagram) {
			return (Diagram) model;
		}
		return null;
	}

	/**
	 * Utility method to extract business object from a device in the Graphiti diagram.
	 * Returns null if object not found
	 * @param editor
	 * @param deviceName
	 * @return
	 */
	public static DcdComponentInstantiation getDeviceObject(SWTBotGefEditor editor, String deviceName) {
		RHContainerShape deviceShape = getRHContainerShape(editor, deviceName);
		if (deviceShape == null) {
			return null;
		}
		DcdComponentInstantiation businessObject = (DcdComponentInstantiation) DUtil.getBusinessObject(deviceShape);
		return businessObject;
	}

	/**
	 * Utility method to extract business object from a findby in the Graphiti diagram.
	 * Returns null if object not found
	 * @param editor
	 * @param findByName
	 * @return
	 */
	public static FindByStub getFindByObject(SWTBotGefEditor editor, String findByName) {

		RHContainerShape findByShape = getRHContainerShape(editor, findByName);
		if (findByShape == null) {
			return null;
		}
		FindByStub businessObject = (FindByStub) DUtil.getBusinessObject(findByShape);
		return businessObject;
	}

	/**
	 * Utility method to extract business object from a host collocation in the Graphiti diagram.
	 * Returns null if object not found
	 * @param editor
	 * @param name
	 * @return
	 */
	public static HostCollocation getHostCollocationObject(SWTBotGefEditor editor, String name) {

		ContainerShape containerShape = getHostCollocationShape(editor, name);
		if (containerShape == null) {
			return null;
		}
		HostCollocation businessObject = (HostCollocation) DUtil.getBusinessObject(containerShape);
		return businessObject;
	}

	/**
	 * Utility method to extract HostCollocationShape from the Graphiti diagram with the provided name.
	 * Returns null if object not found
	 * @param editor
	 * @param name
	 * @return
	 */
	public static ContainerShape getHostCollocationShape(SWTBotGefEditor editor, String name) {

		SWTBotGefEditPart swtBotGefEditPart = editor.getEditPart(name);
		if (swtBotGefEditPart == null) {
			return null;
		}
		return (ContainerShape) swtBotGefEditPart.part().getModel();
	}

	/**
	 * Utility method to extract RHContainerShape from the Graphiti diagram with the provided objectName.
	 * Returns null if object not found
	 * @param editor
	 * @param findByName
	 * @return
	 */
	public static RHContainerShape getRHContainerShape(SWTBotGefEditor editor, String objectName) {
		SWTBotGefEditPart swtBotGefEditPart = editor.getEditPart(objectName);
		if (swtBotGefEditPart == null) {
			return null;
		}
		return (RHContainerShape) swtBotGefEditPart.part().getModel();
	}

	/**
	 * Return true if child shape exists within ContainerShape. Grandchildren, Great grandchildren included..
	 * @param containerShape
	 * @param childShape
	 * @return
	 */
	public static boolean childShapeExists(ContainerShape containerShape, Shape childShape) {
		List<Shape> shapes = DUtil.collectShapeChildren(containerShape);
		for (Shape s : shapes) {
			if (childShape.equals(s)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Drills down into the ports GEF children to return the anchor point
	 * Primarily uses for making connections. Does depth-first search to arbitrary depth.
	 * @param portEditPart - The SWTBotGefEditPart of the port you are trying to get the anchor for
	 * @return
	 */
	public static SWTBotGefEditPart getDiagramPortAnchor(@NonNull SWTBotGefEditPart portEditPart) {
		Assert.assertNotNull(portEditPart);
		EditPart part = portEditPart.part();
		if (part != null && part.getModel() instanceof Anchor) {
			return portEditPart;
		}
		for (SWTBotGefEditPart child : portEditPart.children()) {
			SWTBotGefEditPart anchor = getDiagramPortAnchor(child);
			if (anchor != null) {
				return anchor;
			}
		}
		return null;
	}

	/**
	 * Plot port data on provided Port Anchor
	 * If there is only one port you leave portName null
	 * @param portEditPart - The SWTBotGefEditPart of the port you are trying to get the anchor for
	 * @return
	 */
	public static void plotPortDataOnComponentPort(SWTBotGefEditor editor, String componentName, String portName) {
		final SWTBotGefEditPart usesPort = getDiagramUsesPort(editor, componentName, portName);
		final SWTBotGefEditPart usesPortAnchor = getDiagramPortAnchor(usesPort);
		usesPortAnchor.select();
		editor.clickContextMenu("Plot Port Data");
	}

	/**
	 * Play port data on provided Port Anchor
	 * If there is only one port you leave portName null
	 * @param portEditPart - The SWTBotGefEditPart of the port you are trying to get the anchor for
	 * @return
	 */
	public static void playPortDataOnComponentPort(SWTBotGefEditor editor, String componentName, String portName) {
		final SWTBotGefEditPart usesPort = getDiagramUsesPort(editor, componentName, portName);
		final SWTBotGefEditPart usesPortAnchor = getDiagramPortAnchor(usesPort);
		usesPortAnchor.select();
		editor.clickContextMenu("Play Port");
	}

	/**
	 * Display SRI data on provided Port Anchor
	 * If there is only one port you leave portName null
	 * @param portEditPart - The SWTBotGefEditPart of the port you are trying to get the anchor for
	 * @return
	 */
	public static void displaySRIDataOnComponentPort(SWTBotGefEditor editor, String componentName, String portName) {
		final SWTBotGefEditPart usesPort = getDiagramUsesPort(editor, componentName, portName);
		final SWTBotGefEditPart usesPortAnchor = getDiagramPortAnchor(usesPort);
		usesPortAnchor.select();
		editor.clickContextMenu("Display SRI");
	}

	/**
	 * Display DataList View on provided Port Anchor
	 * If there is only one port you leave portName null
	 * @param portEditPart - The SWTBotGefEditPart of the port you are trying to get the anchor for
	 * @return
	 */
	public static void displayDataListViewOnComponentPort(SWTBotGefEditor editor, String componentName, String portName) {
		final SWTBotGefEditPart usesPort = getDiagramUsesPort(editor, componentName, portName);
		final SWTBotGefEditPart usesPortAnchor = getDiagramPortAnchor(usesPort);
		usesPortAnchor.select();
		editor.clickContextMenu("Data List");
	}

	/**
	 * Display Snapshot View on provided Port Anchor
	 * If there is only one port you leave portName null
	 * @param portEditPart - The SWTBotGefEditPart of the port you are trying to get the anchor for
	 * @return
	 */
	public static void displaySnapshotDialogOnComponentPort(SWTBotGefEditor editor, String componentName, String portName) {
		final SWTBotGefEditPart usesPort = getDiagramUsesPort(editor, componentName, portName);
		final SWTBotGefEditPart usesPortAnchor = getDiagramPortAnchor(usesPort);
		usesPortAnchor.select();
		editor.clickContextMenu("Snapshot");
	}

	/**
	 * Display Port Monitor View on provided Port Anchor
	 * If there is only one port you leave portName null
	 * @param portEditPart - The SWTBotGefEditPart of the port you are trying to get the anchor for
	 * @return
	 */
	public static void displayPortMonitorViewOnComponentPort(SWTBotGefEditor editor, String componentName, String portName) {
		final SWTBotGefEditPart usesPort = getDiagramUsesPort(editor, componentName, portName);
		final SWTBotGefEditPart usesPortAnchor = getDiagramPortAnchor(usesPort);
		usesPortAnchor.select();
		editor.clickContextMenu("Monitor Ports");
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

			List<SWTBotGefEditPart> providesPortsEditParts = child.children();
			for (SWTBotGefEditPart portEditPart : providesPortsEditParts) {
				// If no port name was supplied, return edit part for first provides port
				if (portName == null) {
					return portEditPart;
				}

				// Other wise, check for a matching port name
				if (portEditPart.part().getModel() instanceof ContainerShape) {
					Object businessObject = DUtil.getBusinessObject((ContainerShape) portEditPart.part().getModel());
					if (businessObject instanceof ProvidesPortStub) {
						ProvidesPortStub portStub = (ProvidesPortStub) businessObject;
						if (portName != null && portName.equals(portStub.getName())) {
							return portEditPart;
						}
					}
				}
			}
		}
		// If you get here, no matching provides port was found
		return null;
	}

	/**
	 * 
	 * @param editor - SWTBotGefEditor
	 * @param componentName - Component being searched
	 * @return Returns SWTBotGefEditPart for the specified provides super port, or null if none found
	 */
	public static SWTBotGefEditPart getDiagramProvidesSuperPort(SWTBotGefEditor editor, String componentName) {
		SWTBotGefEditPart componentEditPart = editor.getEditPart(componentName);

		for (SWTBotGefEditPart child : componentEditPart.children()) {
			ContainerShape containerShape = (ContainerShape) child.part().getModel();
			Object bo = DUtil.getBusinessObject(containerShape);

			// Only return objects of type ProvidesPortStub
			if (bo == null || !(bo instanceof ProvidesPortStub || bo instanceof ComponentSupportedInterfaceStub)) {
				continue;
			}

			List<SWTBotGefEditPart> providesPortsEditParts = child.children();
			if (providesPortsEditParts != null && providesPortsEditParts.size() > 0
				&& providesPortsEditParts.get(0).part().getModel() instanceof FixPointAnchor) {
				return providesPortsEditParts.get(0);
			}
		}
		// If you get here, no matching provides port was found
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
		Assert.assertNotNull(componentEditPart);
		for (SWTBotGefEditPart child : componentEditPart.children()) {
			ContainerShape containerShape = (ContainerShape) child.part().getModel();
			Object bo = DUtil.getBusinessObject(containerShape);

			// Only return objects of type UsesPortStub
			if (bo == null || !(bo instanceof UsesPortStub)) {
				continue;
			}

			List<SWTBotGefEditPart> usesPortsEditParts = child.children();
			for (SWTBotGefEditPart portEditPart : usesPortsEditParts) {
				// If no port name was supplied, return edit part for first provides port
				if (portName == null) {
					return portEditPart;
				}

				// Other wise, check for a matching port name
				if (portEditPart.part().getModel() instanceof ContainerShape) {
					Object businessObject = DUtil.getBusinessObject((ContainerShape) portEditPart.part().getModel());
					if (businessObject instanceof UsesPortStub) {
						UsesPortStub portStub = (UsesPortStub) businessObject;
						if (portName != null && portName.equals(portStub.getName())) {
							return portEditPart;
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @param editor - SWTBotGefEditor
	 * @param componentName - Component being searched
	 * @return Returns SWTBotGefEditPart for the specified uses super port, or null if none found
	 */
	public static SWTBotGefEditPart getDiagramUsesSuperPort(SWTBotGefEditor editor, String componentName) {
		SWTBotGefEditPart componentEditPart = editor.getEditPart(componentName);
		Assert.assertNotNull(componentEditPart);
		for (SWTBotGefEditPart child : componentEditPart.children()) {
			ContainerShape containerShape = (ContainerShape) child.part().getModel();
			Object bo = DUtil.getBusinessObject(containerShape);

			// Only return objects of type UsesPortStub
			if (bo == null || !(bo instanceof UsesPortStub)) {
				continue;
			}

			List<SWTBotGefEditPart> usesPortsEditParts = child.children();
			if (usesPortsEditParts != null && usesPortsEditParts.size() > 0 && usesPortsEditParts.get(0).part().getModel() instanceof FixPointAnchor) {
				return usesPortsEditParts.get(0);
			}
		}
		return null;
	}

	/**
	 * 
	 * @param editor - SWTBotGefEditor
	 * @param portEditPart - source port edit part
	 * @return - Returns of list of SWTBotGefConnectionEditParts that includes any connection where the provided port is
	 * the source
	 */
	public static List<SWTBotGefConnectionEditPart> getSourceConnectionsFromPort(SWTBotGefEditor editor, SWTBotGefEditPart portEditPart) {
		SWTBotGefEditPart anchor = getDiagramPortAnchor(portEditPart);
		return anchor.sourceConnections();
	}

	/**
	 * 
	 * @param editor - SWTBotGefEditor
	 * @param portEditPart - target port edit part
	 * @return - Returns of list of SWTBotGefConnectionEditParts that includes any connection where the provided port is
	 * the target
	 */
	public static List<SWTBotGefConnectionEditPart> getTargetConnectionsFromPort(SWTBotGefEditor editor, SWTBotGefEditPart portEditPart) {
		SWTBotGefEditPart anchor = getDiagramPortAnchor(portEditPart);
		return anchor.targetConnections();
	}

	/**
	 * Opens the given tab with the given name within the waveform editor.
	 * @param editor - the editor within which to open the tab
	 * @param tabName - name of the tab to be opened; several helpful constants are defined including
	 * {@link #OVERVIEW_TAB}, {@link #PROPERTIES_TAB}, {@link #IMPLEMENTATIONS} and {@link #DIAGRAM_TAB}
	 */
	public static void openTabInEditor(SWTBotEditor editor, String tabName) {
		editor.bot().cTabItem(tabName).activate();
	}

	/**
	 * Checks sad.xml for component instantiation code
	 * @param componentShape
	 * @return
	 */
	public static String regexStringForComponent(ComponentShape componentShape) {
		Object bo = DUtil.getBusinessObject(componentShape);
		SadComponentInstantiation ci = (SadComponentInstantiation) bo;
		String componentinstantiation = "<componentinstantiation id=\"" + ci.getUsageName() + "\""
			+ ((ci.getStartOrder() != null) ? " startorder=\"" + ci.getStartOrder() + "\">" : ">");
		String usagename = "<usagename>" + ci.getUsageName() + "</usagename>";
		String namingservice = "<namingservice name=\"" + ci.getUsageName() + "\"/>";

		return "(?s).*" + componentinstantiation + ".*" + usagename + ".*" + namingservice + ".*";

	}

	/**
	 * Checks dcd.xml for device instantiation code
	 * @param deviceShape
	 * @return
	 */
	public static String regexStringForDevice(RHContainerShape deviceShape) {
		Object bo = DUtil.getBusinessObject(deviceShape);
		DcdComponentInstantiation ci = (DcdComponentInstantiation) bo;
		String componentinstantiation = "<componentinstantiation id=\"" + ci.getId() + "\">";
		String usagename = "<usagename>" + ci.getUsageName() + "</usagename>";

		return "(?s).*" + componentinstantiation + ".*" + usagename + ".*";
	}

	/**
	 * Checks sad.xml for component property code
	 * @param componentShape
	 * @param propertyname
	 * @param value
	 * @return
	 */
	public static String regexStringForProperty(String propertyname, String value) {
		return "(?s).*<componentproperties>.*<simpleref refid=\"" + propertyname + "\" value=\"" + value + "\"/>.*</componentproperties>.*";
	}

	/**
	 * Maximizes active window
	 */
	public static void maximizeActiveWindow(SWTGefBot gefBot) {
		gefBot.menu().menu("Window", "Navigation", "Maximize Active View or Editor").click();
	}

	/**
	 * Return true if Shape's x/y coordinates match arguments
	 * @param shape
	 * @param x
	 * @param y
	 * @return
	 */
	public static boolean verifyShapeLocation(Shape shape, int x, int y) {
		if (shape.getGraphicsAlgorithm().getX() == x && shape.getGraphicsAlgorithm().getY() == y) {
			return true;
		}
		return false;
	}

	/**
	 * Waits until Connection displays in Chalkboard Diagram
	 * @param componentName
	 */
	public static void waitUntilConnectionDisplaysInDiagram(SWTWorkbenchBot bot, SWTBotGefEditor editor, final String targetComponentName) {
		SWTBotGefEditPart targetComponentEditPart = editor.getEditPart(targetComponentName);
		final ContainerShape targetContainerShape = (ContainerShape) targetComponentEditPart.part().getModel();

		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return targetComponentName + " Target Component's connection did not load into Chalkboard Diagram";
			}

			@Override
			public boolean test() throws Exception {
				if (DUtil.getIncomingConnectionsContainedInContainerShape(targetContainerShape).size() > 0) {
					return true;
				}
				return false;
			}
		});
	}

	/**
	 * Waits until Connection disappears in Chalkboard Diagram
	 * @param componentName
	 */
	public static void waitUntilConnectionDisappearsInDiagram(SWTWorkbenchBot bot, SWTBotGefEditor editor, final String targetComponentName) {

		SWTBotGefEditPart targetComponentEditPart = editor.getEditPart(targetComponentName);
		final ContainerShape targetContainerShape = (ContainerShape) targetComponentEditPart.part().getModel();

		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return targetComponentName + " Target Component's connection did not disappear diagram";
			}

			@Override
			public boolean test() throws Exception {
				if (DUtil.getIncomingConnectionsContainedInContainerShape(targetContainerShape).size() < 1) {
					return true;
				}
				return false;
			}
		});
	}

	/**
	 * Start component from Chalkboard Diagram
	 * @param componentName
	 */
	public static void startComponentFromDiagram(SWTBotGefEditor editor, String componentName) {
		editor.setFocus();
		SWTBotGefEditPart componentPart = editor.getEditPart(componentName);
		componentPart.select();
		editor.clickContextMenu("Start");
	}

	/**
	 * Stop component from Chalkboard Diagram
	 * @param componentName
	 */
	public static void stopComponentFromDiagram(SWTBotGefEditor editor, String componentName) {
		editor.setFocus();
		SWTBotGefEditPart componentPart = editor.getEditPart(componentName);
		componentPart.select();
		editor.clickContextMenu("Stop");
	}

	/**
	 * Change the component log level from the Chalkboard Diagram
	 * @param componentName
	 * @param logLevel
	 */
	public static void changeLogLevelFromDiagram(SWTBotGefEditor editor, String componentName, LogLevels logLevel) {
		editor.setFocus();
		SWTBotGefEditPart componentPart = editor.getEditPart(componentName);
		componentPart.select();
		//editor.clickContextMenu("Logging");
		editor.clickContextMenu("Log Level");

		final SWTBot editorBot = editor.bot();
		SWTBotShell shell = editorBot.shell("Set Debug Level");
		shell.setFocus();

		SWTBot dialogBot = shell.bot();
		dialogBot.comboBox().setSelection(logLevel.getLabel());
		dialogBot.button("OK").click();
		editorBot.waitUntil(Conditions.shellCloses(shell));
	}

	/**
	 * Verify the component's current log level from the Chalkboard Diagram
	 * @param componentName
	 * @param logLevel
	 */
	public static void confirmLogLevelFromDiagram(SWTBotGefEditor editor, String componentName, LogLevels logLevel) {
		editor.setFocus();
		SWTBotGefEditPart componentPart = editor.getEditPart(componentName);
		componentPart.select();
		//editor.clickContextMenu("Logging");
		editor.clickContextMenu("Log Level");

		final SWTBot editorBot = editor.bot();
		SWTBotShell shell = editorBot.shell("Set Debug Level");
		shell.setFocus();

		SWTBot dialogBot = shell.bot();
		SWTBotLabel currentLogLevelLabel = dialogBot.label(2);
		Assert.assertTrue("Current Log Level is not the expected value: " + logLevel.getLabel(), logLevel.getLabel().equals(currentLogLevelLabel.getText()));
		dialogBot.button("Cancel").click();
		editorBot.waitUntil(Conditions.shellCloses(shell));
	}

	public static void waitUntilComponentDisappearsInDiagram(SWTBot bot, final SWTBotGefEditor editor, final String componentName) {
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return componentName + " Component did not disappear from Chalkboard Diagram";
			}

			@Override
			public boolean test() throws Exception {
				return editor.getEditPart(componentName) == null;
			}
		});
	}

	/**
	 * Waits until Component appears started in ChalkboardDiagram
	 * @param componentName
	 * @deprecated Use {@link #waitForComponentState(SWTBot, SWTBotGefEditor, String, ComponentState)}
	 */
	@Deprecated
	public static void waitUntilComponentAppearsStartedInDiagram(SWTWorkbenchBot bot, final SWTBotGefEditor editor, final String componentName) {
		waitForComponentState(bot, editor, componentName, ComponentState.STARTED);
	}

	/**
	 * Waits until Component appears stopped in ChalkboardDiagram
	 * @param componentName
	 * @deprecated Use {@link #waitForComponentState(SWTBot, SWTBotGefEditor, String, ComponentState)}
	 */
	@Deprecated
	public static void waitUntilComponentAppearsStoppedInDiagram(SWTWorkbenchBot bot, final SWTBotGefEditor editor, final String componentName) {
		waitForComponentState(bot, editor, componentName, ComponentState.STOPPED);
	}

	public enum ComponentState {
		LAUNCHING,
		STARTED,
		STOPPED,
		ERROR;

		public String getStyleId() {
			switch (this) {
			case LAUNCHING:
				return StyleUtil.COMPONENT_INNER_DISABLED;
			case STARTED:
				return StyleUtil.COMPONENT_INNER_STARTED;
			case STOPPED:
				return StyleUtil.COMPONENT_INNER;
			case ERROR:
				return StyleUtil.COMPONENT_INNER_ERROR;
			default:
				return null;
			}
		}

		public static ComponentState getStateFromStyle(String styleId) {
			for (ComponentState state : ComponentState.values()) {
				if (state.getStyleId().equals(styleId)) {
					return state;
				}
			}
			return null;
		}
	}

	/**
	 * See {@link #waitForComponentState(SWTBot, SWTBotGefEditor, String, ComponentState, long)}
	 */
	public static void waitForComponentState(SWTBot bot, SWTBotGefEditor editor, String componentName, ComponentState state) {
		waitForComponentState(bot, editor, componentName, state, 10000);
	}

	public static void waitForComponentState(SWTBot bot, final SWTBotGefEditor editor, final String componentName, final ComponentState state, long timeout) {
		waitUntilComponentDisplaysInDiagram(bot, editor, componentName, timeout);
		bot.waitUntil(new DefaultCondition() {

			private String lastStyle = null;

			@Override
			public boolean test() throws Exception {
				RHContainerShapeImpl componentShape = (RHContainerShapeImpl) editor.getEditPart(componentName).part().getModel();
				GraphicsAlgorithm ga = componentShape.getInnerContainerShape().getGraphicsAlgorithm();
				lastStyle = ga.getStyle().getId();
				return state.getStyleId().equals(lastStyle);
			}

			@Override
			public String getFailureMessage() {
				String styleDesc = (ComponentState.getStateFromStyle(lastStyle) != null) ? ComponentState.getStateFromStyle(lastStyle).toString() : "id: " + lastStyle;
				return String.format("Resource did not change to state '%s'. Style was '%s'.", state.toString(), styleDesc);
			}
		}, timeout);
	}

	public static void waitUntilComponentDisplaysInDiagram(SWTBot bot, final SWTBotGefEditor editor, final String componentName) {
		waitUntilComponentDisplaysInDiagram(bot, editor, componentName, SWTBotPreferences.TIMEOUT);
	}

	public static void waitUntilComponentDisplaysInDiagram(SWTBot bot, final SWTBotGefEditor editor, final String componentName, long timeout) {
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return String.format("Resource %s did not appear in the diagram", componentName);
			}

			@Override
			public boolean test() throws Exception {
				return editor.getEditPart(componentName) != null;
			}
		}, timeout);
	}

	/**
	 * Asserts external port
	 * @param external
	 * @return
	 */
	public static void assertExternalPort(SWTBotGefEditPart portEditPart, boolean external) {
		// Get the edit part for the visible port rectangle, then get its graphics algorithm and check that its style
		// reflects the expected external state
		SWTBotGefEditPart portRectPart = portEditPart.children().get(0);
		GraphicsAlgorithm portRect = ((Shape) portRectPart.part().getModel()).getGraphicsAlgorithm();
		if (external) {
			Assert.assertTrue("Port style is not external", StyleUtil.isStyleSet(portRect, StyleUtil.EXTERNAL_PROVIDES_PORT,  StyleUtil.EXTERNAL_USES_PORT));
		} else {
			Assert.assertFalse("Port style is external", StyleUtil.isStyleSet(portRect, StyleUtil.EXTERNAL_PROVIDES_PORT,  StyleUtil.EXTERNAL_USES_PORT));
		}
	}

	/** NOTE: Unfortunately, if the context menu item exists, it will be clicked */
	public static boolean hasContentMenuItem(SWTBotGefEditor editor, String componentName, String menuItem) {
		editor.setFocus();
		SWTBotGefEditPart componentPart = editor.getEditPart(componentName);
		componentPart.select();
		boolean foundMenuItem;
		try {
			editor.clickContextMenu(menuItem);
			foundMenuItem = true;
		} catch (WidgetNotFoundException e) {
			foundMenuItem = false;
		}
		return foundMenuItem;
	}
}
