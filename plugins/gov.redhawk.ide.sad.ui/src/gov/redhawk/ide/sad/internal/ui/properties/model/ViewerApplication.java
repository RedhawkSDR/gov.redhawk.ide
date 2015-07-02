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
package gov.redhawk.ide.sad.internal.ui.properties.model;

import gov.redhawk.sca.util.PluginUtil;

import java.util.ArrayList;
import java.util.Collection;

import mil.jpeojtrs.sca.partitioning.ComponentProperties;
import mil.jpeojtrs.sca.partitioning.PartitioningPackage;
import mil.jpeojtrs.sca.prf.AbstractPropertyRef;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap.ValueListIterator;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;

public class ViewerApplication extends ItemProviderAdapter implements ITreeItemContentProvider {

	public ViewerApplication(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	public static AbstractPropertyRef< ? > getRef(SadComponentInstantiation inst, ViewerProperty< ? > p) {
		ComponentProperties properties = inst.getComponentProperties();
		if (properties != null) {
			for (ValueListIterator<Object> i = properties.getProperties().valueListIterator(); i.hasNext();) {
				Object obj = i.next();
				if (obj instanceof AbstractPropertyRef< ? >) {
					AbstractPropertyRef< ? > propRef = (AbstractPropertyRef< ? >) obj;
					if (PluginUtil.equals(propRef.getRefID(), p.getID())) {
						return propRef;
					}
				}
			}
		}
		return null;
	}

	@Override
	protected Object getValue(EObject eObject, EStructuralFeature eStructuralFeature) {
		if (eStructuralFeature == PartitioningPackage.Literals.COMPONENT_PLACEMENT__COMPONENT_INSTANTIATION) {
			return ((SoftwareAssembly)eObject).getAllComponentInstantiations();
		}
		return super.getValue(eObject, eStructuralFeature);
	}

	@Override
	protected Collection< ? extends EStructuralFeature> getChildrenFeatures(Object object) {
		if (childrenFeatures == null) {
			childrenFeatures = new ArrayList<EStructuralFeature>();
			childrenFeatures.add(PartitioningPackage.Literals.COMPONENT_PLACEMENT__COMPONENT_INSTANTIATION);
		}
		return childrenFeatures;
	}

}
