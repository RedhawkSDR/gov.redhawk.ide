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

import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import mil.jpeojtrs.sca.partitioning.ComponentInstantiation;
import mil.jpeojtrs.sca.profile.provider.ProfileItemProviderAdapterFactory;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.platform.GraphitiShapeEditPart;
import org.eclipse.ui.views.properties.IPropertySource;

public class GraphitiEditPartToIPropertySourceAdapterFactory implements IAdapterFactory {

	public GraphitiEditPartToIPropertySourceAdapterFactory() {
		super();
	}

	public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
		if (IPropertySource.class.equals(adapterType)) {
			if (adaptableObject instanceof GraphitiShapeEditPart) {
				GraphitiShapeEditPart editPart = (GraphitiShapeEditPart) adaptableObject;
				PictogramElement pictogramElement = editPart.getPictogramElement();
				ContainerShape containerShape = (ContainerShape) DUtil.findContainerShapeParentWithProperty(pictogramElement,
					RHContainerShapeImpl.SHAPE_OUTER_CONTAINER);
				Object obj = DUtil.getBusinessObject(containerShape);
				if (containerShape != null && obj != null && obj instanceof ComponentInstantiation) {

					// get sca property source
					final ProfileItemProviderAdapterFactory factory = new ProfileItemProviderAdapterFactory();
					IItemPropertySource obj2 = (IItemPropertySource) factory.adapt(obj, IItemPropertySource.class);
					return adapterType.cast(new gov.redhawk.sca.ui.RedhawkUiAdapterFactory.ScaPropertySource(obj, obj2));
				}
			}
		}
		return null;
	}

	public Class<?>[] getAdapterList() {
		return new Class[] { IPropertySource.class };
	}
}
