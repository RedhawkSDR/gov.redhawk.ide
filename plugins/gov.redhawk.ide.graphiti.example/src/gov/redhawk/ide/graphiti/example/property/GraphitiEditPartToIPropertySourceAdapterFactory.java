/*******************************************************************************
 * <copyright>
 *
 * Copyright (c) 2012, 2012 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP AG - initial API, implementation and documentation
 *
 * </copyright>
 *
 *******************************************************************************/
package gov.redhawk.ide.graphiti.example.property;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.platform.GraphitiConnectionEditPart;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * Registered to adapt an {@link EditPart} that gets selected in the
 * {@link DiagramEditor} to A {@link IPropertySource} object that is used as
 * input for the properties view.
 */
public class GraphitiEditPartToIPropertySourceAdapterFactory implements IAdapterFactory {

	public GraphitiEditPartToIPropertySourceAdapterFactory() {
		super();
	}

	public Object getAdapter(Object adaptableObject, @SuppressWarnings("rawtypes") Class adapterType) {
		if (IPropertySource.class.equals(adapterType)) {
			if (adaptableObject instanceof GraphitiConnectionEditPart) {
				GraphitiConnectionEditPart editPart = (GraphitiConnectionEditPart) adaptableObject;
				PictogramElement pictogramElement = editPart.getPictogramElement();
				Object object = editPart.getFeatureProvider().getBusinessObjectForPictogramElement(pictogramElement);
				if (object instanceof EReference) {
					return new EReferencePropertySource((EReference) object);
				}
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getAdapterList() {
		return new Class[] { IPropertySource.class };
	}
}
