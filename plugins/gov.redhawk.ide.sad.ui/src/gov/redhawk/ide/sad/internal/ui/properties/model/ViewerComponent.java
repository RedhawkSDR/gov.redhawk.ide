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

import mil.jpeojtrs.sca.prf.AbstractProperty;
import mil.jpeojtrs.sca.prf.Simple;
import mil.jpeojtrs.sca.prf.SimpleRef;
import mil.jpeojtrs.sca.prf.SimpleSequence;
import mil.jpeojtrs.sca.prf.SimpleSequenceRef;
import mil.jpeojtrs.sca.prf.Struct;
import mil.jpeojtrs.sca.prf.StructRef;
import mil.jpeojtrs.sca.prf.StructSequence;
import mil.jpeojtrs.sca.prf.StructSequenceRef;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.emf.ecore.util.FeatureMap.ValueListIterator;

public class ViewerComponent {

	private List<ViewerProperty< ? >> properties = new ArrayList<ViewerProperty< ? >>();
	private SadComponentInstantiation compInst;
	private SoftPkg spd;

	public ViewerComponent(SadComponentInstantiation compInst) {
		this.compInst = compInst;
		spd = compInst.getPlacement().getComponentFileRef().getFile().getSoftPkg();
		for (ValueListIterator<Object> i = spd.getPropertyFile().getProperties().getProperties().valueListIterator(); i.hasNext();) {
			final Object value = i.next();
			if (value instanceof AbstractProperty) {
				AbstractProperty def = (AbstractProperty) value;
				properties.add(createViewerProperty(def));
			}
		}
	}
	
	public SadComponentInstantiation getComponentInstantiation() {
		return compInst;
	}
	
	public List<ViewerProperty< ? >> getProperties() {
		return properties;
	}

	private ViewerProperty< ? > createViewerProperty(AbstractProperty def) {
		if (def instanceof Simple) {
			return createViewerProperty((Simple) def);
		} else if (def instanceof SimpleSequence) {
			return createViewerProperty((SimpleSequence) def);
		} else if (def instanceof Struct) {
			return createViewerProperty((Struct) def);
		} else if (def instanceof StructSequence) {
			return createViewerProperty((StructSequence) def);
		}
		return null;
	}

	private ViewerSimpleProperty createViewerProperty(Simple def) {
		ViewerSimpleProperty retVal = new ViewerSimpleProperty(def, this);
		SimpleRef ref = getRef(def);
		retVal.setValue(ref);
		return retVal;
	}

	private ViewerSequenceProperty createViewerProperty(SimpleSequence def) {
		ViewerSequenceProperty retVal = new ViewerSequenceProperty(def, this);
		SimpleSequenceRef ref = getRef(def);
		retVal.setValues(ref.getValues());
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
