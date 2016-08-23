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
package gov.redhawk.ide.graphiti.ui.adapters;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.GraphitiShapeEditPart;

import gov.redhawk.core.graphiti.ui.ext.RHContainerShape;
import gov.redhawk.ide.debug.LocalLaunch;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.model.sca.ScaAbstractComponent;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaDevice;
import mil.jpeojtrs.sca.partitioning.ComponentInstantiation;

/**
 * Can adapt either:
 * <ul>
 * <li>{@link GraphitiShapeEditPart} (Graphiti UI part)</li>
 * <li>{@link RHContainerShape} (our Graphiti model object)</li>
 * </ul>
 * from the diagrams to the following types (and a few of their super types):
 * <ul>
 * <li>{@link ScaComponent}</li>
 * <li>{@link ScaDevice}</li>
 * <li>{@link LocalLaunch}</li>
 * </ul>
 */
public class ContainerShapeAdapterFactory implements IAdapterFactory {

	private static final Class< ? >[] ADAPTER_TYPES = new Class< ? >[] { ScaComponent.class, ScaDevice.class, LocalLaunch.class };

	@Override
	public < T > T getAdapter(Object adaptableObject, Class<T> adapterType) {
		// We convert the Graphiti UI part -> Graphiti pictogram element, if not already done for us
		if (adaptableObject instanceof GraphitiShapeEditPart) {
			adaptableObject = ((GraphitiShapeEditPart) adaptableObject).getPictogramElement();
		}
		if (!(adaptableObject instanceof RHContainerShape)) {
			return null;
		}
		RHContainerShape containerShape = (RHContainerShape) adaptableObject;

		// Go from the Graphiti pictogram element to the Redhawk model object
		// The object must be a ComponentInstantiation (super-type of instantiations in both SAD & DCD)
		ComponentInstantiation instantiation = DUtil.getBusinessObject(containerShape, ComponentInstantiation.class);
		if (instantiation == null) {
			return null;
		}
		Diagram diagram = Graphiti.getPeService().getDiagramForShape(containerShape);
		if (diagram == null) {
			return null;
		}

		ScaAbstractComponent< ? > component = GraphitiAdapterUtil.getScaModelObject(diagram, instantiation);
		if (adapterType.isInstance(component)) {
			return adapterType.cast(component);
		} else {
			return null;
		}
	}

	@Override
	public Class< ? >[] getAdapterList() {
		return ADAPTER_TYPES;
	}

}
