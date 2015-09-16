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
package gov.redhawk.ide.graphiti.ui.diagram.patterns;

import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.graphiti.features.context.IConnectionContext;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.pattern.AbstractConnectionPattern;
import org.eclipse.graphiti.services.Graphiti;

import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.model.sca.commands.NonDirtyingCommand;
import mil.jpeojtrs.sca.partitioning.ConnectionTarget;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

public class AbstractConnectInterfacePattern extends AbstractConnectionPattern {

	private UsesPortStub sourcePort;
	private ConnectionTarget targetPort;

	/**
	 * Returns the source port a connection is being initiated from. Only available after {@link #startConnecting()}.
	 * @return The source port or null if none
	 */
	protected UsesPortStub getSourcePort() {
		return sourcePort;
	}

	/**
	 * Returns the connection target a connection is being initiated from. Only available after
	 * {@link #startConnecting()}.
	 * @return The target connection or null if none
	 */
	protected ConnectionTarget getTargetPort() {
		return targetPort;
	}

	@Override
	public boolean canStartConnection(ICreateConnectionContext context) {
		// Not allowed in the explorer or if read-only
		Diagram diagram = getDiagram();
		if (DUtil.isDiagramExplorer(diagram) || DUtil.isDiagramReadOnly(diagram)) {
			return false;
		}

		// Check if the source anchor belongs to a component, and disallow the connection if it's disabled
		RHContainerShape component = ScaEcoreUtils.getEContainerOfType(context.getSourceAnchor(), RHContainerShape.class);
		if (component != null && !component.isEnabled()) {
			return false;
		}

		// We must be able to find a UsesPortStub or ConnectionTarget
		UsesPortStub source = getUsesPortStub(context);
		if (source != null) {
			this.sourcePort = source;
			return true;
		}
		ConnectionTarget target = getConnectionTarget(context);
		if (target != null) {
			this.targetPort = target;
			return true;
		}

		return false;
	}

	@Override
	public void startConnecting() {
		super.startConnecting();

		// Set a property on the diagram so it's easy to tell a connection is in progress
		CommandStack stack = getDiagramBehavior().getEditingDomain().getCommandStack();
		stack.execute(new NonDirtyingCommand() {
			@Override
			public void execute() {
				Graphiti.getPeService().setPropertyValue(getDiagram(), DUtil.DIAGRAM_CONNECTION_IN_PROGRESS, "true");
			}
		});
	}

	@Override
	public boolean canCreate(ICreateConnectionContext context) {
		// Check if the target anchor belongs to a component, and disallow the connection if it's disabled; the source
		// was already checked in canStart().
		RHContainerShape component = ScaEcoreUtils.getEContainerOfType(context.getTargetAnchor(), RHContainerShape.class);
		if (component != null && !component.isEnabled()) {
			return false;
		}

		// determine source
		UsesPortStub source = getUsesPortStub(context);
		if (source == null) {
			return false;
		}

		// determine destination
		// getConnectionTarget handles connecting to ports on components, not ports or interfaces on FindBy Shapes
		ConnectionTarget target = getConnectionTarget(context);
		if (target == null) {
			// PASS
			// TODO: check if interface on findBy Shape
			// TODO: check if provides port on findBy...not sure how were doing all this??
		}

		return true;
	}

	@Override
	public void endConnecting() {
		this.sourcePort = null;
		this.targetPort = null;

		CommandStack stack = getDiagramBehavior().getEditingDomain().getCommandStack();
		stack.execute(new NonDirtyingCommand() {
			@Override
			public void execute() {
				Graphiti.getPeService().removeProperty(getDiagram(), DUtil.DIAGRAM_CONNECTION_IN_PROGRESS);
			}
		});

		super.endConnecting();
	}

	/**
	 * Return UsesPortStub from either the source or target anchor. Depends on how user drew connection.
	 * @param context
	 * @return
	 */
	protected UsesPortStub getUsesPortStub(IConnectionContext context) {
		UsesPortStub source = getUsesPortStub(context.getSourceAnchor());
		if (source != null) {
			return source;
		}
		source = getUsesPortStub(context.getTargetAnchor());
		return source;
	}

	protected UsesPortStub getUsesPortStub(Anchor anchor) {
		if (anchor != null) {
			Object object = getBusinessObjectForPictogramElement(anchor.getParent());
			if (object instanceof UsesPortStub) {
				return (UsesPortStub) object;
			}
		}
		return null;
	}

	/**
	 * Return ConnectionTarget from either the source or target anchor. Depends on how user drew connection.
	 * @param context
	 * @return
	 */
	protected ConnectionTarget getConnectionTarget(IConnectionContext context) {
		ConnectionTarget connectionTarget = getConnectionTarget(context.getSourceAnchor());
		if (connectionTarget != null) {
			return connectionTarget;
		}
		connectionTarget = getConnectionTarget(context.getTargetAnchor());
		return connectionTarget;
	}

	protected ConnectionTarget getConnectionTarget(Anchor anchor) {
		if (anchor != null) {
			Object object = getBusinessObjectForPictogramElement(anchor.getParent());
			if (object instanceof ConnectionTarget) {
				return (ConnectionTarget) object;
			}
		}
		return null;
	}

}
