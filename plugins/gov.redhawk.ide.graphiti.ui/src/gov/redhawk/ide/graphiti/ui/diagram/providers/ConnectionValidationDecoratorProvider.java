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

import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.tb.ColorDecorator;
import org.eclipse.graphiti.tb.IColorDecorator;
import org.eclipse.graphiti.tb.IDecorator;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.util.IColorConstant;
import org.eclipse.swt.graphics.Color;

import gov.redhawk.diagram.util.InterfacesUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import mil.jpeojtrs.sca.partitioning.ConnectInterface;
import mil.jpeojtrs.sca.partitioning.ConnectionTarget;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;

public class ConnectionValidationDecoratorProvider implements IDecoratorProvider, IToolTipDelegate {

	protected static final IDecorator[] NO_DECORATORS = new IDecorator[0];

	private final DiagramBehavior diagramBehavior;
	
	public ConnectionValidationDecoratorProvider(DiagramBehavior diagramBehavior) {
		this.diagramBehavior = diagramBehavior;
	}

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
				applyConnectionDecorators(connection, decorator);
				return new IDecorator[] { decorator };
			} else {
				applyConnectionDecorators(connection, null);				
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

	private void applyConnectionDecorators(Connection connection, IColorDecorator decorator) {
		GraphicalViewer viewer = diagramBehavior.getDiagramContainer().getGraphicalViewer();
		GraphicalEditPart part = (GraphicalEditPart) viewer.getEditPartRegistry().get(connection);

		Color foreground = null;
		Color background = null;
		if (decorator != null) {
			if (decorator.getForegroundColor() != null) {
				foreground = getSwtColor(decorator.getForegroundColor());
			}
			if (decorator.getBackgroundColor() != null) {
				background = getSwtColor(decorator.getBackgroundColor());
			}
		}

		// Assume that there's a 1:1 mapping between figure children and connection decorators, as there does not
		// appear to be any other way to reconcile the two
		List< ? > children = part.getFigure().getChildren();
		for (int index = 0; index < children.size(); index++) {
			ConnectionDecorator connectionDecorator = connection.getConnectionDecorators().get(index);
			if (!connectionDecorator.isActive()) {
				IFigure figure = (IFigure) children.get(index);
				refreshColors(figure, connectionDecorator.getGraphicsAlgorithm(), foreground, background);
			}
		}
	}

	private void refreshColors(IFigure figure, GraphicsAlgorithm ga, Color foreground, Color background) {
		if (foreground == null) {
			foreground = getSwtColor(Graphiti.getGaService().getForegroundColor(ga, true));
		}
		if (background == null) {
			background = getSwtColor(Graphiti.getGaService().getBackgroundColor(ga, true));
		}
		figure.setForegroundColor(foreground);
		figure.setBackgroundColor(background);
	}

	private Color getSwtColor(org.eclipse.graphiti.mm.algorithms.styles.Color color) {
		return new Color(null, color.getRed(), color.getGreen(), color.getBlue());
	}

	private Color getSwtColor(IColorConstant constant) {
		return new Color(null, constant.getRed(), constant.getGreen(), constant.getBlue());
	}
}
