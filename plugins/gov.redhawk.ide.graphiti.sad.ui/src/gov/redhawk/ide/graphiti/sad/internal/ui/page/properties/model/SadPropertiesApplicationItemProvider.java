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
package gov.redhawk.ide.graphiti.sad.internal.ui.page.properties.model;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.provider.ViewerNotification;

import mil.jpeojtrs.sca.partitioning.PartitioningPackage;
import mil.jpeojtrs.sca.sad.ExternalProperties;
import mil.jpeojtrs.sca.sad.ExternalProperty;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

public class SadPropertiesApplicationItemProvider extends ItemProviderAdapter implements ITreeItemContentProvider {

	public SadPropertiesApplicationItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	@Override
	protected Object getValue(EObject eObject, EStructuralFeature eStructuralFeature) {
		if (eStructuralFeature == PartitioningPackage.Literals.COMPONENT_PLACEMENT__COMPONENT_INSTANTIATION) {
			return ((SoftwareAssembly) eObject).getAllComponentInstantiations();
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

	@Override
	public void setTarget(Notifier target) {
		super.setTarget(target);
		if (target instanceof SoftwareAssembly) {
			SoftwareAssembly softwareAssembly = (SoftwareAssembly) target;
			externalPropertiesAdded(softwareAssembly, softwareAssembly.getExternalProperties());
		}
	}

	@Override
	public void notifyChanged(Notification msg) {
		final Object feature = msg.getFeature();
		if (feature == SadPackage.Literals.SOFTWARE_ASSEMBLY__EXTERNAL_PROPERTIES) {
			if (msg.getEventType() == Notification.SET) {
				SoftwareAssembly softwareAssembly = (SoftwareAssembly) msg.getNotifier();
				ExternalProperties properties = (ExternalProperties) msg.getNewValue();
				if (properties != null) {
					externalPropertiesAdded(softwareAssembly, properties);
				} else {
					properties = (ExternalProperties) msg.getOldValue();
					externalPropertiesRemoved(softwareAssembly, properties);
				}
				fireNotifyChanged(msg);
			}
		} else if (feature == SadPackage.Literals.EXTERNAL_PROPERTIES__PROPERTIES) {
			if (msg.getEventType() == Notification.ADD) {
				externalPropertyChanged(msg, (ExternalProperty) msg.getNewValue());
			} else if (msg.getEventType() == Notification.REMOVE) {
				externalPropertyChanged(msg, (ExternalProperty) msg.getOldValue());
			}
		}
	}

	@Override
	public boolean isAdapterForType(Object type) {
		if (type instanceof Class< ? >) {
			return ((Class< ? >) type).isInstance(this);
		}
		return super.isAdapterForType(type);
	}

	private void externalPropertiesAdded(SoftwareAssembly softwareAssembly, ExternalProperties properties) {
		if (properties != null) {
			properties.eAdapters().add(this);
			for (ExternalProperty externalProperty : properties.getProperties()) {
				SadPropertyImpl< ? > viewerProperty = getComponentProperty(softwareAssembly, externalProperty);
				if (viewerProperty != null) {
					viewerProperty.setExternalProperty(externalProperty);
				}
			}
		}
	}

	private void externalPropertiesRemoved(SoftwareAssembly softwareAssembly, ExternalProperties properties) {
		if (properties != null) {
			properties.eAdapters().remove(this);
			for (ExternalProperty externalProperty : properties.getProperties()) {
				SadPropertyImpl< ? > viewerProperty = getComponentProperty(softwareAssembly, externalProperty);
				if (viewerProperty != null) {
					viewerProperty.setExternalProperty(null);
				}
			}
		}
	}

	private void externalPropertyChanged(Notification msg, ExternalProperty externalProperty) {
		ExternalProperties properties = (ExternalProperties) msg.getNotifier();
		SoftwareAssembly softwareAssembly = (SoftwareAssembly) properties.eContainer();
		SadPropertyImpl< ? > viewerProperty = getComponentProperty(softwareAssembly, externalProperty);
		viewerProperty.setExternalProperty((ExternalProperty) msg.getNewValue());
		fireNotifyChanged(new ViewerNotification(msg, viewerProperty, true, true));
	}

	private SadComponentInstantiation getComponentInstantiation(SoftwareAssembly softwareAssembly, String componentId) {
		for (SadComponentInstantiation instantiation : softwareAssembly.getAllComponentInstantiations()) {
			if (instantiation.getId().equals(componentId)) {
				return instantiation;
			}
		}
		return null;
	}

	private SadPropertyImpl< ? > getComponentProperty(SoftwareAssembly softwareAssembly, ExternalProperty externalProperty) {
		final String componentId = externalProperty.getCompRefID();
		SadComponentInstantiation instantiation = getComponentInstantiation(softwareAssembly, componentId);
		if (instantiation != null) {
			SadPropertiesComponent component = (SadPropertiesComponent) adapterFactory.adapt(instantiation, SadPropertiesComponent.class);
			if (component != null) {
				final String propertyId = externalProperty.getPropID();
				for (Object child : component.getChildren(instantiation)) {
					SadPropertyImpl< ? > viewerProperty = (SadPropertyImpl< ? >) child;
					if (viewerProperty.getID().equals(propertyId)) {
						return viewerProperty;
					}
				}
			}
		}
		return null;
	}

}
