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
package gov.redhawk.ide.graphiti.ui.diagram.util;

import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;

import gov.redhawk.model.sca.commands.NonDirtyingCommand;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.Port;

public class PortStyleUtil {

	public enum PortStyle {
		NORMAL,
		EXTERNAL,
		COMPATIBLE_PORT,
		STAT_OK,
		STAT_WARN1,
		STAT_WARN2,
		STAT_WARN3,
		STAT_WARN4,
	}

	private PortStyleUtil() {
	}

	/**
	 * Resets all ports' styling. See {@link #resetPortStyling(Diagram, EditingDomain, ContainerShape)} for details.
	 * @param diagram The diagram
	 * @param editingDomain The diagram's editing domain
	 */
	public static void resetAllPortStyling(Diagram diagram, EditingDomain editingDomain) {
		List<ContainerShape> allPorts = DUtil.getDiagramPorts(diagram);
		for (ContainerShape port : allPorts) {
			resetPortStyling(diagram, editingDomain, port);
		}
	}

	/**
	 * Resets a port's styling. Takes into account:
	 * <ul>
	 * <li>Port direction</li>
	 * <li>External ports</li>
	 * </ul>
	 * Does not take into account:
	 * <ul>
	 * <li>Port statistics</li>
	 * <li>When a connection is in progress</li>
	 * </ul>
	 * @param diagram The diagram
	 * @param editingDomain The diagram's editing domain
	 * @param portShape The shape for the port
	 */
	public static void resetPortStyling(Diagram diagram, EditingDomain editingDomain, ContainerShape portShape) {
		Anchor anchor = portShape.getChildren().get(0).getAnchors().get(0);

		// Check if this is an external port (the anchor will be linked to the external port in the SAD
		for (EObject object : anchor.getLink().getBusinessObjects()) {
			if (object instanceof Port) {
				updatePortStyle(diagram, editingDomain, portShape, PortStyle.EXTERNAL);
				return;
			}
		}

		// Standard port
		updatePortStyle(diagram, editingDomain, portShape, PortStyle.NORMAL);
	}

	/**
	 * Updates all ports' styling to show the compatible ports that can complete a connection.
	 * @param diagram The diagram
	 * @param editingDomain The diagram's editing domain
	 * @param compatiblePortShapes The port shapes to be highlighted as compatible to complete the connection
	 */
	public static void highlightCompatiblePorts(Diagram diagram, EditingDomain editingDomain, Set<ContainerShape> compatiblePortShapes) {
		List<ContainerShape> allPortShapes = DUtil.getDiagramPorts(diagram);
		for (ContainerShape portShape : allPortShapes) {
			if (compatiblePortShapes.contains(portShape)) {
				updatePortStyle(diagram, editingDomain, portShape, PortStyle.COMPATIBLE_PORT);
			} else {
				updatePortStyle(diagram, editingDomain, portShape, PortStyle.NORMAL);
			}
		}
	}

	/**
	 * Update a port's style.
	 * @param diagram The diagram
	 * @param editingDomain The diagram's editing domain
	 * @param portShape The shape for the port
	 * @param portStyle The new style for the port
	 */
	public static void updatePortStyle(final Diagram diagram, final EditingDomain editingDomain, final ContainerShape portShape, final PortStyle portStyle) {
		final Anchor anchor = portShape.getChildren().get(0).getAnchors().get(0);
		final GraphicsAlgorithm ga = anchor.getGraphicsAlgorithm();
		final EObject portObj = DUtil.getBusinessObject(portShape);

		// don't change style color for super ports
		final ContainerShape portContainer = (ContainerShape) ga.eContainer().eContainer();
		if (DUtil.isSuperPort(portContainer)) {
			return;
		}

		final CommandStack stack = editingDomain.getCommandStack();
		stack.execute(new NonDirtyingCommand() {
			@Override
			public void execute() {
				String styleId = null;
				switch (portStyle) {
				case NORMAL:
					if (portObj instanceof ProvidesPortStub) {
						styleId = StyleUtil.PROVIDES_PORT;
					} else if (portObj instanceof UsesPortStub) {
						styleId = StyleUtil.USES_PORT;
					}
					break;
				case EXTERNAL:
					if (portObj instanceof ProvidesPortStub) {
						styleId = StyleUtil.EXTERNAL_PROVIDES_PORT;
					} else if (portObj instanceof UsesPortStub) {
						styleId = StyleUtil.EXTERNAL_USES_PORT;
					}
					break;
				case COMPATIBLE_PORT:
					styleId = StyleUtil.PORT_STYLE_COMPATIBLE;
					break;
				case STAT_OK:
					styleId = StyleUtil.PORT_STYLE_OK;
					break;
				case STAT_WARN1:
					styleId = StyleUtil.PORT_STYLE_WARN1;
					break;
				case STAT_WARN2:
					styleId = StyleUtil.PORT_STYLE_WARN2;
					break;
				case STAT_WARN3:
					styleId = StyleUtil.PORT_STYLE_WARN3;
					break;
				case STAT_WARN4:
					styleId = StyleUtil.PORT_STYLE_WARN4;
					break;
				default:
					break;
				}

				if (styleId != null) {
					StyleUtil.setStyle(ga, styleId);
				}
			}
		});
	}
}
