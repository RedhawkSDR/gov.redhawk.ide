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

import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.tb.ColorDecorator;
import org.eclipse.graphiti.tb.IColorDecorator;
import org.eclipse.graphiti.tb.IDecorator;
import org.eclipse.graphiti.util.IColorConstant;

import gov.redhawk.diagram.util.InterfacesUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import mil.jpeojtrs.sca.partitioning.ConnectInterface;
import mil.jpeojtrs.sca.partitioning.ConnectionTarget;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;

public class ConnectionValidationDecoratorProvider implements IDecoratorProvider, IToolTipDelegate {

	protected static final IDecorator[] NO_DECORATORS = new IDecorator[0];

	@Override
	public Object getToolTip(GraphicsAlgorithm ga) {
		PictogramElement pe = ga.getPictogramElement();
		if (pe instanceof Connection) {
			return validate((Connection) pe);
		}
		return null;
	}

	@Override
	public IDecorator[] getDecorators(PictogramElement pe) {
		if (pe instanceof Connection) {
			Connection connection = (Connection) pe;
			if (validate(connection) != null) {
				IColorDecorator decorator = new ColorDecorator(IColorConstant.RED, IColorConstant.RED);
				return new IDecorator[] { decorator };
			}
		}
		return NO_DECORATORS;
	}

	protected String validate(Connection connection) {
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
			return "Incompatible interface";
		}

		return null;
	}

}
