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

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.UnexecutableCommand;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap.ValueListIterator;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;

import gov.redhawk.sca.util.PluginUtil;

public class ViewerComponent extends ViewerItemProvider implements NestedPropertyItemProvider {
	
	private static final EStructuralFeature [] PATH = new EStructuralFeature [] {
		SpdPackage.Literals.SOFT_PKG__PROPERTY_FILE,
		SpdPackage.Literals.PROPERTY_FILE__PROPERTIES,
	};

	private SadComponentInstantiation compInst;
	private SoftwareAssembly sad;

	public ViewerComponent(SadComponentInstantiation compInst) {
		this.compInst = compInst;
		this.sad = ScaEcoreUtils.getEContainerOfType(compInst, SoftwareAssembly.class);
		
		ComponentFile theCompFile = compInst.getPlacement().getComponentFileRef().getFile();
		
		// The component file can be null if the component file reference does not map back to any SPD (likely due to a copy paste error on the users part)
		if (theCompFile == null || theCompFile.getSoftPkg() == null) {
			return;
		}
		SoftPkg spd = theCompFile.getSoftPkg();
		Properties prf = ScaEcoreUtils.getFeature(spd, PATH);
		if (prf != null) {
			for (ValueListIterator<Object> i = prf.getProperties().valueListIterator(); i.hasNext();) {
				final Object value = i.next();
				if (value instanceof AbstractProperty) {
					AbstractProperty def = (AbstractProperty) value;
					children.add(createViewerProperty(def));
				}
			}
		}
	}

	public SadComponentInstantiation getComponentInstantiation() {
		return compInst;
	}

	public boolean isAssemblyController() {
		if (sad.getAssemblyController() != null) {
			AssemblyController assemblyController = sad.getAssemblyController();
			if (assemblyController.getComponentInstantiationRef() != null) {
				if (PluginUtil.equals(compInst.getId(), assemblyController.getComponentInstantiationRef().getRefid())) {
					return true;
				}
			}
		}
		return false;
	}

	private ViewerProperty< ? > createViewerProperty(AbstractProperty def) {
		switch (def.eClass().getClassifierID()) {
		case PrfPackage.SIMPLE:
			return new ViewerSimpleProperty((Simple) def, this);
		case PrfPackage.SIMPLE_SEQUENCE:
			return new ViewerSequenceProperty((SimpleSequence) def, this);
		case PrfPackage.STRUCT:
			return new ViewerStructProperty((Struct) def, this);
		case PrfPackage.STRUCT_SEQUENCE:
			return new ViewerStructSequenceProperty((StructSequence) def, this);
		}
		return null;
	}

	protected AbstractPropertyRef< ? > getChildRef(final String refId) {
		if (compInst.getComponentProperties() != null) {
			for (FeatureMap.Entry entry : compInst.getComponentProperties().getProperties()) {
				AbstractPropertyRef< ? > ref = (AbstractPropertyRef< ? >) entry.getValue();
				if (ref.getRefID().equals(refId)) {
					return ref;
				}
			}
		}
		return null;
	}

	protected ExternalProperty getExternalProperty(final String refId) {
		final ExternalProperties properties = sad.getExternalProperties();
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
		return TransactionUtil.getEditingDomain(sad);
	}

	protected EStructuralFeature getChildFeature(Object object, Object child) {
		switch (((EObject)child).eClass().getClassifierID()) {
		case PrfPackage.SIMPLE_REF:
			return PartitioningPackage.Literals.COMPONENT_PROPERTIES__SIMPLE_REF;
		case PrfPackage.SIMPLE_SEQUENCE_REF:
			return PartitioningPackage.Literals.COMPONENT_PROPERTIES__SIMPLE_SEQUENCE_REF;
		case PrfPackage.STRUCT_REF:
			return PartitioningPackage.Literals.COMPONENT_PROPERTIES__STRUCT_REF;
		case PrfPackage.STRUCT_SEQUENCE_REF:
			return PartitioningPackage.Literals.COMPONENT_PROPERTIES__STRUCT_SEQUENCE_REF;
		}
		return null;
	}

	@Override
	public Command createAddChildCommand(EditingDomain domain, Object child, EStructuralFeature feature) {
		if (feature == ViewerPackage.Literals.SAD_PROPERTY__VALUE) {
			ComponentProperties properties = getComponentInstantiation().getComponentProperties();
			if (properties == null) {
				properties = PartitioningFactory.eINSTANCE.createComponentProperties();
				properties.getProperties().add(getChildFeature(properties, child), child);
				return SetCommand.create(domain, getComponentInstantiation(), PartitioningPackage.Literals.COMPONENT_INSTANTIATION__COMPONENT_PROPERTIES, properties);
			} else {
				return AddCommand.create(domain, properties, getChildFeature(properties, child), child);
			}
		}
		return UnexecutableCommand.INSTANCE;
	}

	@Override
	public Command createRemoveChildCommand(EditingDomain domain, Object child, EStructuralFeature feature) {
		if (feature == ViewerPackage.Literals.SAD_PROPERTY__VALUE) {
			ComponentProperties properties = getComponentInstantiation().getComponentProperties();
			if (properties.getProperties().size() == 1) {
				return RemoveCommand.create(domain, properties);
			} else {
				return RemoveCommand.create(domain, properties, getChildFeature(properties, child), child);
			}
		}
		return UnexecutableCommand.INSTANCE;
	}

}
