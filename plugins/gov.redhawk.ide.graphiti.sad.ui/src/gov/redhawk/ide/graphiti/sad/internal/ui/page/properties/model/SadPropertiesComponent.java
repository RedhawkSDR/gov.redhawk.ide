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
import java.util.Collections;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.UnexecutableCommand;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.provider.ViewerNotification;
import org.eclipse.emf.transaction.util.TransactionUtil;

import gov.redhawk.sca.util.PluginUtil;
import mil.jpeojtrs.sca.partitioning.ComponentFile;
import mil.jpeojtrs.sca.partitioning.ComponentProperties;
import mil.jpeojtrs.sca.partitioning.PartitioningFactory;
import mil.jpeojtrs.sca.partitioning.PartitioningPackage;
import mil.jpeojtrs.sca.prf.AbstractProperty;
import mil.jpeojtrs.sca.prf.AbstractPropertyRef;
import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.prf.Properties;
import mil.jpeojtrs.sca.prf.Simple;
import mil.jpeojtrs.sca.prf.SimpleSequence;
import mil.jpeojtrs.sca.prf.Struct;
import mil.jpeojtrs.sca.prf.StructSequence;
import mil.jpeojtrs.sca.sad.AssemblyController;
import mil.jpeojtrs.sca.sad.ExternalProperties;
import mil.jpeojtrs.sca.sad.ExternalProperty;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

public class SadPropertiesComponent extends ItemProviderAdapter implements ITreeItemContentProvider, NestedItemProvider {

	private static final EStructuralFeature[] PATH = new EStructuralFeature[] {
		SpdPackage.Literals.SOFT_PKG__PROPERTY_FILE,
		SpdPackage.Literals.PROPERTY_FILE__PROPERTIES
	};

	private SadComponentInstantiation compInst;

	public SadPropertiesComponent(AdapterFactory adapterFactory, SadComponentInstantiation compInst) {
		super(adapterFactory);
		this.compInst = compInst;
		propertiesAdded(compInst.getComponentProperties());
	}

	@Override
	public Object getParent(Object object) {
		return getSoftwareAssembly();
	}

	@Override
	protected Object getValue(EObject eObject, EStructuralFeature eStructuralFeature) {
		if (eStructuralFeature == PrfPackage.Literals.PROPERTIES__PROPERTIES) {
			ComponentFile theCompFile = ((SadComponentInstantiation) eObject).getPlacement().getComponentFileRef().getFile();

			// The component file can be null if the component file reference does not map back to any SPD (likely due
			// to a copy paste error on the users part)
			if (theCompFile != null && theCompFile.getSoftPkg() != null) {
				SoftPkg spd = theCompFile.getSoftPkg();
				Properties properties = ScaEcoreUtils.getFeature(spd, SadPropertiesComponent.PATH);
				if (properties != null) {
					return properties.getProperties();
				}
			}
			return Collections.EMPTY_LIST;
		}
		return super.getValue(eObject, eStructuralFeature);
	}

	@Override
	protected Collection< ? extends EStructuralFeature> getChildrenFeatures(Object object) {
		if (childrenFeatures == null) {
			childrenFeatures = new ArrayList<EStructuralFeature>();
			childrenFeatures.add(PrfPackage.Literals.PROPERTIES__PROPERTIES);
		}
		return childrenFeatures;
	}

	@Override
	protected Object createWrapper(EObject object, EStructuralFeature feature, Object value, int index) {
		if (feature == PrfPackage.Literals.PROPERTIES__PROPERTIES) {
			FeatureMap.Entry entry = (FeatureMap.Entry) value;
			SadPropertyImpl< ? > property = createViewerProperty((AbstractProperty) entry.getValue());
			ExternalProperty externalProperty = getExternalProperty(property.getID());
			property.setExternalProperty(externalProperty);
			return property;
		}
		return super.createWrapper(object, feature, value, index);
	}

	public SoftwareAssembly getSoftwareAssembly() {
		return ScaEcoreUtils.getEContainerOfType(compInst, SoftwareAssembly.class);
	}

	public SadComponentInstantiation getComponentInstantiation() {
		return compInst;
	}

	public boolean isAssemblyController() {
		SoftwareAssembly softwareAssembly = getSoftwareAssembly();
		if (softwareAssembly.getAssemblyController() != null) {
			AssemblyController assemblyController = softwareAssembly.getAssemblyController();
			if (assemblyController.getComponentInstantiationRef() != null) {
				if (PluginUtil.equals(compInst.getId(), assemblyController.getComponentInstantiationRef().getRefid())) {
					return true;
				}
			}
		}
		return false;
	}

	private SadPropertyImpl< ? > createViewerProperty(AbstractProperty def) {
		switch (def.eClass().getClassifierID()) {
		case PrfPackage.SIMPLE:
			return new SadPropertiesSimple(adapterFactory, (Simple) def, this);
		case PrfPackage.SIMPLE_SEQUENCE:
			return new SadPropertiesSimpleSequence(adapterFactory, (SimpleSequence) def, this);
		case PrfPackage.STRUCT:
			return new SadPropertiesStruct(adapterFactory, (Struct) def, this);
		case PrfPackage.STRUCT_SEQUENCE:
			return new SadPropertiesStructSequence(adapterFactory, (StructSequence) def, this);
		default:
			return null;
		}
	}

	private ExternalProperty getExternalProperty(final String refId) {
		final ExternalProperties properties = getSoftwareAssembly().getExternalProperties();
		if (properties != null) {
			for (final ExternalProperty property : properties.getProperties()) {
				if (property.getCompRefID().equals(compInst.getId()) && property.getPropID().equals(refId)) {
					return property;
				}
			}
		}
		return null;
	}

	@Override
	public EditingDomain getEditingDomain() {
		return TransactionUtil.getEditingDomain(getSoftwareAssembly());
	}

	@Override
	protected EStructuralFeature getChildFeature(Object object, Object child) {
		switch (((EObject) child).eClass().getClassifierID()) {
		case PrfPackage.SIMPLE_REF:
			return PartitioningPackage.Literals.COMPONENT_PROPERTIES__SIMPLE_REF;
		case PrfPackage.SIMPLE_SEQUENCE_REF:
			return PartitioningPackage.Literals.COMPONENT_PROPERTIES__SIMPLE_SEQUENCE_REF;
		case PrfPackage.STRUCT_REF:
			return PartitioningPackage.Literals.COMPONENT_PROPERTIES__STRUCT_REF;
		case PrfPackage.STRUCT_SEQUENCE_REF:
			return PartitioningPackage.Literals.COMPONENT_PROPERTIES__STRUCT_SEQUENCE_REF;
		default:
			return null;
		}
	}

	@Override
	public Command createAddChildCommand(EditingDomain domain, Object child, EStructuralFeature feature) {
		if (feature == SadPropertiesPackage.Literals.SAD_PROPERTY__VALUE) {
			SadComponentInstantiation instantiation = getComponentInstantiation();
			ComponentProperties properties = instantiation.getComponentProperties();
			if (properties == null) {
				properties = PartitioningFactory.eINSTANCE.createComponentProperties();
				properties.getProperties().add(getChildFeature(properties, child), child);
				return SetCommand.create(domain, instantiation, PartitioningPackage.Literals.COMPONENT_INSTANTIATION__COMPONENT_PROPERTIES, properties);
			} else {
				return AddCommand.create(domain, properties, getChildFeature(properties, child), child);
			}
		}
		return UnexecutableCommand.INSTANCE;
	}

	@Override
	public Command createRemoveChildCommand(EditingDomain domain, Object child, EStructuralFeature feature) {
		if (feature == SadPropertiesPackage.Literals.SAD_PROPERTY__VALUE) {
			ComponentProperties properties = getComponentInstantiation().getComponentProperties();
			if (properties.getProperties().size() == 1) {
				return RemoveCommand.create(domain, properties);
			} else {
				return RemoveCommand.create(domain, properties, getChildFeature(properties, child), child);
			}
		}
		return UnexecutableCommand.INSTANCE;
	}

	@Override
	public void notifyChanged(Notification msg) {
		final Object feature = msg.getFeature();
		if (feature == PartitioningPackage.Literals.COMPONENT_INSTANTIATION__COMPONENT_PROPERTIES) {
			if (msg.getEventType() == Notification.SET) {
				ComponentProperties properties = (ComponentProperties) msg.getNewValue();
				if (properties == null) {
					propertiesRemoved((ComponentProperties) msg.getOldValue());
				} else {
					propertiesAdded(properties);
				}
				fireNotifyChanged(msg);
			}
		} else if (feature == PartitioningPackage.Literals.COMPONENT_PROPERTIES__PROPERTIES) {
			if (msg.getEventType() == Notification.ADD) {
				propertyChanged(unwrapProperty(msg.getNewValue()), msg);
			} else if (msg.getEventType() == Notification.REMOVE) {
				propertyChanged(unwrapProperty(msg.getOldValue()), msg);
			}
		}
	}

	private AbstractPropertyRef< ? > unwrapProperty(Object value) {
		if (value != null) {
			FeatureMap.Entry entry = (FeatureMap.Entry) value;
			return (AbstractPropertyRef< ? >) entry.getValue();
		}
		return null;
	}

	@Override
	public boolean isAdapterForType(Object type) {
		if (type instanceof Class< ? >) {
			return ((Class< ? >) type).isInstance(this);
		}
		return super.isAdapterForType(type);
	}

	protected SadPropertyImpl< ? > getProperty(String identifier) {
		for (Object child : getChildren(compInst)) {
			SadPropertyImpl< ? > property = (SadPropertyImpl< ? >) child;
			if (property.getID().equals(identifier)) {
				return property;
			}
		}
		return null;
	}

	private void propertiesAdded(ComponentProperties properties) {
		if (properties != null) {
			properties.eAdapters().add(this);
			for (FeatureMap.Entry entry : properties.getProperties()) {
				AbstractPropertyRef< ? > ref = (AbstractPropertyRef< ? >) entry.getValue();
				SadPropertyImpl< ? > property = getProperty(ref.getRefID());
				if (property != null) {
					property.setReference(ref);
				}
			}
		}
	}

	private void propertiesRemoved(ComponentProperties properties) {
		if (properties != null) {
			properties.eAdapters().remove(this);
			for (FeatureMap.Entry entry : properties.getProperties()) {
				AbstractPropertyRef< ? > ref = (AbstractPropertyRef< ? >) entry.getValue();
				SadPropertyImpl< ? > property = getProperty(ref.getRefID());
				if (property != null) {
					property.setReference(null);
				}
			}
		}
	}

	private void propertyChanged(AbstractPropertyRef< ? > ref, Notification msg) {
		SadPropertyImpl< ? > property = getProperty(ref.getRefID());
		if (property != null) {
			property.setReference(unwrapProperty(msg.getNewValue()));
			boolean contentRefresh = property.hasChildren();
			fireNotifyChanged(new ViewerNotification(msg, property, contentRefresh, true));
		}
	}
}
