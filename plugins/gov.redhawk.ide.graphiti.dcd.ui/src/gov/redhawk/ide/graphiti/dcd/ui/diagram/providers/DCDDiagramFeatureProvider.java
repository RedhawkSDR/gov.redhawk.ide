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
package gov.redhawk.ide.graphiti.dcd.ui.diagram.providers;

import gov.redhawk.ide.graphiti.dcd.ui.diagram.patterns.DevicePattern;
import gov.redhawk.ide.graphiti.dcd.ui.diagram.patterns.ServicePattern;
import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.ui.diagram.providers.AbstractGraphitiFeatureProvider;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;
import org.eclipse.graphiti.features.impl.DefaultRemoveFeature;
import org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature;
import org.eclipse.graphiti.features.impl.UpdateNoBoFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;

public class DCDDiagramFeatureProvider extends AbstractGraphitiFeatureProvider {

	public DCDDiagramFeatureProvider(IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);

		addPattern(new DevicePattern());
		addPattern(new ServicePattern());
	}
	
	@Override
	public IMoveShapeFeature getMoveShapeFeature(IMoveShapeContext context) {
		// Search for shapes that we don't want the user to have move capability
		if (DUtil.doesPictogramContainProperty(context, new String[] { RHContainerShapeImpl.SHAPE_USES_PORT_RECTANGLE,
			RHContainerShapeImpl.SHAPE_PROVIDES_PORT_RECTANGLE, RHContainerShapeImpl.SHAPE_INTERFACE_CONTAINER })) {
			return new DefaultMoveShapeFeature(this) {
				public boolean canMove(IContext context) {
					return false;
				}
			};
		}

		return super.getMoveShapeFeature(context);
	}
	
	@Override
	public IUpdateFeature getUpdateFeature(IUpdateContext context) {
		// hide update icon for some pictogram elements
		if (DUtil.doesPictogramContainProperty(context, new String[] { RHContainerShapeImpl.SHAPE_PROVIDES_PORTS_CONTAINER,
			RHContainerShapeImpl.SHAPE_USES_PORTS_CONTAINER, RHContainerShapeImpl.SHAPE_PROVIDES_PORT_CONTAINER,
			RHContainerShapeImpl.SHAPE_USES_PORT_CONTAINER, RHContainerShapeImpl.SHAPE_PROVIDES_PORT_RECTANGLE, RHContainerShapeImpl.SHAPE_USES_PORT_RECTANGLE,
			RHContainerShapeImpl.SHAPE_INTERFACE_CONTAINER, RHContainerShapeImpl.SHAPE_INTERFACE_ELLIPSE })) {
			return new UpdateNoBoFeature(this) {
				public boolean isAvailable(IContext context) {
					return false;
				}
			};
		}

		return super.getUpdateFeature(context);
	}
	
	@Override
	public IDeleteFeature getDeleteFeature(IDeleteContext context) {
		// Search for shapes for which we don't want the user to have the delete capability, including the diagram as a whole
		if (context.getPictogramElement() instanceof Diagram
			|| context.getPictogramElement() instanceof FixPointAnchor
			|| DUtil.doesPictogramContainProperty(context, new String[] { RHContainerShapeImpl.SHAPE_PROVIDES_PORTS_CONTAINER,
				RHContainerShapeImpl.SHAPE_USES_PORTS_CONTAINER, RHContainerShapeImpl.SHAPE_PROVIDES_PORT_CONTAINER,
				RHContainerShapeImpl.SHAPE_USES_PORT_CONTAINER, RHContainerShapeImpl.SHAPE_PROVIDES_PORT_RECTANGLE,
				RHContainerShapeImpl.SHAPE_USES_PORT_RECTANGLE, RHContainerShapeImpl.SHAPE_INTERFACE_CONTAINER, RHContainerShapeImpl.SHAPE_INTERFACE_ELLIPSE })) {
			return new DefaultDeleteFeature(this) {
				@Override
				public boolean canDelete(IDeleteContext context) {
					return false;
				}

				@Override
				public boolean isAvailable(IContext context) {
					return false;
				}
			};
		}
		
		return super.getDeleteFeature(context);
	}
	
	@Override
	public IResizeShapeFeature getResizeShapeFeature(IResizeShapeContext context) {
		// Search for shapes that we don't want the user to have resize capability
		if (context.getPictogramElement() instanceof FixPointAnchor
				|| DUtil.doesPictogramContainProperty(context, new String[] { RHContainerShapeImpl.SHAPE_PROVIDES_PORTS_CONTAINER,
					RHContainerShapeImpl.SHAPE_USES_PORTS_CONTAINER, RHContainerShapeImpl.SHAPE_PROVIDES_PORT_CONTAINER,
					RHContainerShapeImpl.SHAPE_USES_PORT_CONTAINER, RHContainerShapeImpl.SHAPE_PROVIDES_PORT_RECTANGLE,
					RHContainerShapeImpl.SHAPE_USES_PORT_RECTANGLE, RHContainerShapeImpl.SHAPE_INTERFACE_CONTAINER, RHContainerShapeImpl.SHAPE_INTERFACE_ELLIPSE })) {
			return new DefaultResizeShapeFeature(this) {
				public boolean canResizeShape(IResizeShapeContext context) {
					return false;
				}
			};
		}

		return super.getResizeShapeFeature(context);
	}

	@Override
	public IRemoveFeature getRemoveFeature(IRemoveContext context) {
		return new DefaultRemoveFeature(this) {
			public boolean isAvailable(IContext context) {
				return false;
			}
		};
	}
}
