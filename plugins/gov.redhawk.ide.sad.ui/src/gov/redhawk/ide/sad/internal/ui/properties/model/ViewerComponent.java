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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;

public class ViewerComponent extends ViewerItemProvider {
	
	private static final EStructuralFeature [] PATH = new EStructuralFeature [] {
		SpdPackage.Literals.SOFT_PKG__PROPERTY_FILE,
		SpdPackage.Literals.PROPERTY_FILE__PROPERTIES,
	};

	private List<ViewerProperty< ? >> properties = new ArrayList<ViewerProperty< ? >>();
	private SadComponentInstantiation compInst;
	private SoftPkg spd;
	private SoftwareAssembly sad;

	public ViewerComponent(SadComponentInstantiation compInst) {
		this.compInst = compInst;
		this.sad = ScaEcoreUtils.getEContainerOfType(compInst, SoftwareAssembly.class);
		
		ComponentFile theCompFile = compInst.getPlacement().getComponentFileRef().getFile();
		
		// The component file can be null if the component file reference does not map back to any SPD (likely due to a copy paste error on the users part)
		if (theCompFile == null || theCompFile.getSoftPkg() == null) {
			return;
		} else {
			spd = theCompFile.getSoftPkg();
		}
		Properties prf = ScaEcoreUtils.getFeature(spd, PATH);
		if (prf != null) {
			for (ValueListIterator<Object> i = prf.getProperties().valueListIterator(); i.hasNext();) {
				final Object value = i.next();
				if (value instanceof AbstractProperty) {
					AbstractProperty def = (AbstractProperty) value;
					properties.add(createViewerProperty(def));
				}
			}
		}
	}

	public void addPropertyChangeListener(IViewerPropertyChangeListener listener) {
		for (ViewerProperty< ? > p : properties) {
			p.addPropertyChangeListener(listener);
		}
	}

	public void removePropertyChangeListener(IViewerPropertyChangeListener listener) {
		for (ViewerProperty< ? > p : properties) {
			p.removePropertyChangeListener(listener);
		}
	}

	public SadComponentInstantiation getComponentInstantiation() {
		return compInst;
	}

	public List<ViewerProperty< ? >> getProperties() {
		return properties;
	}

	private ViewerProperty< ? > createViewerProperty(AbstractProperty def) {
		ViewerProperty< ? > retVal = null;
		if (def instanceof Simple) {
			retVal = createViewerProperty((Simple) def);
		} else if (def instanceof SimpleSequence) {
			retVal = createViewerProperty((SimpleSequence) def);
		} else if (def instanceof Struct) {
			retVal = createViewerProperty((Struct) def);
		} else if (def instanceof StructSequence) {
			retVal = createViewerProperty((StructSequence) def);
		}
		return retVal;
	}

	private ViewerSimpleProperty createViewerProperty(Simple def) {
		return new ViewerSimpleProperty(def, this);
	}

	private ViewerSequenceProperty createViewerProperty(SimpleSequence def) {
		return new ViewerSequenceProperty(def, this);
	}

	private ViewerStructProperty createViewerProperty(Struct def) {
		return new ViewerStructProperty(def, this);
	}

	private ViewerStructSequenceProperty createViewerProperty(StructSequence def) {
		return new ViewerStructSequenceProperty(def, this);
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
	public Collection< ? > getChildren(Object element) {
		return getProperties();
	}

	@Override
	public EditingDomain getEditingDomain() {
		return TransactionUtil.getEditingDomain(sad);
	}

	@Override
	public Command createParentCommand(EditingDomain domain, Object feature, Object value) {
		final String stringFeature = (String) feature;
		if (stringFeature.equals("value")) {
			return SetCommand.create(domain, getComponentInstantiation(), PartitioningPackage.Literals.COMPONENT_INSTANTIATION__COMPONENT_PROPERTIES, value);
		}
		return UnexecutableCommand.INSTANCE;
	}

	@Override
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
		return super.getChildFeature(object, child);
	}

	@Override
	protected Object getContainer(Object feature) {
		final String stringFeature = (String)feature;
		if (stringFeature.equals("value")) {
			return getComponentInstantiation().getComponentProperties();
		}
		return null;
	}

	@Override
	protected Object createContainer(Object feature, Object value) {
		final String stringFeature = (String)feature;
		if (stringFeature.equals("value")) {
			ComponentProperties properties = PartitioningFactory.eINSTANCE.createComponentProperties();
			properties.getProperties().add(getChildFeature(properties, value), value);
			return properties;
		}
		return null;
	}

}
