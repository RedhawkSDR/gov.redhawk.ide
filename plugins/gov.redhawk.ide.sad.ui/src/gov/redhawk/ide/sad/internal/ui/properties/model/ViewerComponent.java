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
import java.util.List;

import mil.jpeojtrs.sca.partitioning.ComponentFile;
import mil.jpeojtrs.sca.prf.AbstractProperty;
import mil.jpeojtrs.sca.prf.Properties;
import mil.jpeojtrs.sca.prf.Simple;
import mil.jpeojtrs.sca.prf.SimpleRef;
import mil.jpeojtrs.sca.prf.SimpleSequence;
import mil.jpeojtrs.sca.prf.SimpleSequenceRef;
import mil.jpeojtrs.sca.prf.Struct;
import mil.jpeojtrs.sca.prf.StructRef;
import mil.jpeojtrs.sca.prf.StructSequence;
import mil.jpeojtrs.sca.prf.StructSequenceRef;
import mil.jpeojtrs.sca.sad.ExternalProperties;
import mil.jpeojtrs.sca.sad.ExternalProperty;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap.ValueListIterator;

public class ViewerComponent {
	
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
		ExternalProperty externalProp = getExternalProp(def);
		if (retVal != null && externalProp != null) {
			if (externalProp.getExternalPropID() != null) {
				retVal.setExternalID(externalProp.getExternalPropID());
			} else {
				retVal.setExternalID(externalProp.getPropID());
			}
		}
		return retVal;
	}

	private ViewerSimpleProperty createViewerProperty(Simple def) {
		ViewerSimpleProperty retVal = new ViewerSimpleProperty(def, this);
		SimpleRef ref = getRef(def);
		retVal.setValue(ref);
		return retVal;
	}

	private ExternalProperty getExternalProp(AbstractProperty prop) {
		ExternalProperties externalProperties = sad.getExternalProperties();
		if (externalProperties != null) {
			for (ExternalProperty p : externalProperties.getProperties()) {
				if (p.getCompRefID().equals(compInst.getId()) && p.getPropID().equals(prop.getId())) {
					return p;
				}
			}
		}
		return null;
	}

	private ViewerSequenceProperty createViewerProperty(SimpleSequence def) {
		ViewerSequenceProperty retVal = new ViewerSequenceProperty(def, this);
		SimpleSequenceRef ref = getRef(def);
		retVal.setValues(ref);
		return retVal;
	}

	private ViewerStructProperty createViewerProperty(Struct def) {
		ViewerStructProperty retVal = new ViewerStructProperty(def, this);
		StructRef ref = getRef(def);
		retVal.setValue(ref);
		return retVal;
	}

	private ViewerStructSequenceProperty createViewerProperty(StructSequence def) {
		ViewerStructSequenceProperty retVal = new ViewerStructSequenceProperty(def, this);
		StructSequenceRef ref = getRef(def);
		retVal.setValue(ref);
		return retVal;
	}

	private SimpleRef getRef(Simple prop) {
		if (compInst.getComponentProperties() != null) {
			for (SimpleRef ref : compInst.getComponentProperties().getSimpleRef()) {
				if (ref.getRefID().equals(prop.getId())) {
					return ref;
				}
			}
		}
		return null;
	}

	private SimpleSequenceRef getRef(SimpleSequence prop) {
		if (compInst.getComponentProperties() != null) {
			for (SimpleSequenceRef ref : compInst.getComponentProperties().getSimpleSequenceRef()) {
				if (ref.getRefID().equals(prop.getId())) {
					return ref;
				}
			}
		}
		return null;
	}

	private StructRef getRef(Struct prop) {
		if (compInst.getComponentProperties() != null) {
			for (StructRef ref : compInst.getComponentProperties().getStructRef()) {
				if (ref.getRefID().equals(prop.getId())) {
					return ref;
				}
			}
		}
		return null;
	}

	private StructSequenceRef getRef(StructSequence prop) {
		if (compInst.getComponentProperties() != null) {
			for (StructSequenceRef ref : compInst.getComponentProperties().getStructSequenceRef()) {
				if (ref.getRefID().equals(prop.getId())) {
					return ref;
				}
			}
		}
		return null;
	}

}