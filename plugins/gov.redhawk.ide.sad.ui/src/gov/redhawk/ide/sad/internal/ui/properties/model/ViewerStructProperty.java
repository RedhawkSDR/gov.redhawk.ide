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

import org.eclipse.emf.ecore.util.FeatureMap;

import mil.jpeojtrs.sca.prf.AbstractProperty;
import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.prf.Simple;
import mil.jpeojtrs.sca.prf.SimpleRef;
import mil.jpeojtrs.sca.prf.SimpleSequence;
import mil.jpeojtrs.sca.prf.SimpleSequenceRef;
import mil.jpeojtrs.sca.prf.Struct;
import mil.jpeojtrs.sca.prf.StructRef;
import mil.jpeojtrs.sca.prf.StructValue;

/**
 * 
 */
public class ViewerStructProperty extends ViewerProperty<Struct> {

	private List<ViewerProperty< ? >> fields = new ArrayList<ViewerProperty< ? >>();

	public ViewerStructProperty(Struct def, Object parent) {
		super(def, parent);
		setToDefault();
	}

	public List<ViewerProperty< ? >> getFields() {
		return fields;
	}

	private ViewerProperty< ? > getField(final String identifier) {
		for (ViewerProperty< ? > property : fields) {
			if (((AbstractProperty)property.getDefinition()).getId().equals(identifier)) {
				return property;
			}
		}
		return null;
	}

	@Override
	public void setToDefault() {
		fields.clear();
		for (FeatureMap.Entry field : def.getFields()) {
			if (field.getEStructuralFeature() == PrfPackage.Literals.STRUCT__SIMPLE) {
				fields.add(new ViewerSimpleProperty((Simple) field.getValue(), this));
			} else if (field.getEStructuralFeature() == PrfPackage.Literals.STRUCT__SIMPLE_SEQUENCE) {
				fields.add(new ViewerSequenceProperty((SimpleSequence) field.getValue(), this));				
			}
		}
	}

	private void clear() {
		for (ViewerProperty< ? > property : fields) {
			if (property instanceof ViewerSimpleProperty) {
				((ViewerSimpleProperty) property).setValue((SimpleRef) null);
			} else if (property instanceof ViewerSequenceProperty) {
				((ViewerSequenceProperty) property).setValues((SimpleSequenceRef) null);
			}
		}
	}

	private void setValue(FeatureMap refs) {
		for (FeatureMap.Entry entry : refs) {
			if (entry.getValue() instanceof SimpleRef) {
				SimpleRef ref = (SimpleRef)entry.getValue();
				ViewerProperty< ? > property = getField(ref.getRefID());
				if (property instanceof ViewerSimpleProperty) {
					((ViewerSimpleProperty) property).setValue(ref);
				}
			} else if (entry.getValue() instanceof SimpleSequenceRef) {
				SimpleSequenceRef ref = (SimpleSequenceRef)entry.getValue();
				ViewerProperty< ? > property = getField(ref.getRefID());
				if (property instanceof ViewerSequenceProperty) {
					((ViewerSequenceProperty) property).setValues(ref);
				}
			}
		}
	}

	public void setValue(StructRef value) {
		if (value != null) {
			setValue(value.getRefs());
		} else {
			clear();
		}
	}
	
	@Override
	public void addPropertyChangeListener(IViewerPropertyChangeListener listener) {
		super.addPropertyChangeListener(listener);
		for (ViewerProperty< ? > p : fields) {
			p.addPropertyChangeListener(listener);
		}
	}
	
	@Override
	public void removePropertyChangeListener(IViewerPropertyChangeListener listener) {
		super.removePropertyChangeListener(listener);
		for (ViewerProperty< ? > p : fields) {
			p.removePropertyChangeListener(listener);
		}
	}

	public void setValue(StructValue value) {
		if (value != null) {
			setValue(value.getRefs());
		} else {
			clear();
		}
	}

	@Override
	public Collection< ? > getChildren(Object object) {
		return getFields();
	}
}
