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
package gov.redhawk.ide.graphiti.ui.diagram.providers;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.tb.ColorDecorator;
import org.eclipse.graphiti.tb.IColorDecorator;
import org.eclipse.graphiti.tb.IDecorator;
import org.eclipse.graphiti.util.IColorConstant;

import gov.redhawk.core.graphiti.ui.util.StyleUtil;
import gov.redhawk.diagram.util.InterfacesUtil;
import gov.redhawk.ide.graphiti.ui.GraphitiUIPlugin;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import mil.jpeojtrs.sca.partitioning.ConnectInterface;
import mil.jpeojtrs.sca.partitioning.ConnectionTarget;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;

public class ConnectionValidationDecoratorProvider implements IDecoratorProvider, IToolTipDelegate {

	public static final IColorConstant COLOR_ERROR = IColorConstant.RED;
	public static final IColorConstant COLOR_WARNING = StyleUtil.GOLD;

	protected static final IDecorator[] NO_DECORATORS = new IDecorator[0];

	@Override
	public Object getToolTip(GraphicsAlgorithm ga) {
		PictogramElement pe = ga.getPictogramElement();
		if (pe instanceof Connection) {
			IStatus status = validate((Connection) pe);
			if (!status.isOK()) {
				return status.getMessage();
			}
		}
		return null;
	}

	@Override
	public IDecorator[] getDecorators(PictogramElement pe) {
		if (pe instanceof Connection) {
			IStatus status = validate((Connection) pe);
			if (!status.isOK()) {
				IColorConstant color = getDecoratorColor(status.getSeverity());
				IColorDecorator decorator = new ColorDecorator(color, color);
				return new IDecorator[] { decorator };
			}
		}
		return NO_DECORATORS;
	}

	protected IColorConstant getDecoratorColor(int severity) {
		switch (severity) {
		case IStatus.ERROR:
			return ConnectionValidationDecoratorProvider.COLOR_ERROR;
		case IStatus.WARNING:
			return ConnectionValidationDecoratorProvider.COLOR_WARNING;
		default:
			return null;
		}
	}

	protected IStatus validate(Connection connection) {
		ConnectInterface< ? , ? , ? > connectInterface = (ConnectInterface< ? , ? , ? >) DUtil.getBusinessObject(connection);

		//establish source/target for connection
		UsesPortStub source = connectInterface.getSource();
		ConnectionTarget target = connectInterface.getTarget();

		//source and target will be null if findBy or usesDevice is used, in this case pull stubs from diagram
		if (source == null) {
			source = DUtil.getBusinessObject(connection.getStart(), UsesPortStub.class);
		}
		if (target == null) {
			target = DUtil.getBusinessObject(connection.getEnd(), ConnectionTarget.class);
		}

		if (!InterfacesUtil.areCompatible(source, target)) {
			return new Status(IStatus.ERROR, GraphitiUIPlugin.PLUGIN_ID, "Incompatible interface");
		} else if (isDuplicate(connection)) {
			return new Status(IStatus.WARNING, GraphitiUIPlugin.PLUGIN_ID, "Duplicate connection");
		}
		return Status.OK_STATUS;
	}

	protected boolean isDuplicate(Connection connection) {
		// Check all of the connections originating at the start anchor to verify that none have the same end anchor.
		// We intentionally create connections to have the uses port as the start, regardless of which direction the
		// user drew the connection in, so it should be sufficient to only check outgoing connections.
		Anchor start = connection.getStart();
		Anchor end = connection.getEnd();
		for (Connection outgoing : start.getOutgoingConnections()) {
			if (outgoing != connection && outgoing.getEnd() == end) {
				return true;
			}
		}
		return false;
	}
}
