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

import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.prf.Simple;
import mil.jpeojtrs.sca.prf.SimpleRef;
import mil.jpeojtrs.sca.prf.SimpleSequence;
import mil.jpeojtrs.sca.prf.SimpleSequenceRef;
import mil.jpeojtrs.sca.prf.StructSequence;
import mil.jpeojtrs.sca.prf.StructSequenceRef;
import mil.jpeojtrs.sca.prf.StructValue;
import mil.jpeojtrs.sca.prf.Values;

/**
 * 
 */
public class ViewerStructSequenceProperty extends ViewerProperty<StructSequence> {
	private List<ViewerStructSequenceNestedProperty< ? >> fieldsArray = new ArrayList<ViewerStructSequenceNestedProperty< ? >>();

	public ViewerStructSequenceProperty(StructSequence def, Object parent) {
		super(def, parent);
		for (FeatureMap.Entry entry : def.getStruct().getFields()) {
			if (entry.getEStructuralFeature() == PrfPackage.Literals.STRUCT__SIMPLE) {
				Simple simple = (Simple) entry.getValue();
				fieldsArray.add(new ViewerStructSequenceSimpleProperty(simple, this));
			} else if (entry.getEStructuralFeature() == PrfPackage.Literals.STRUCT__SIMPLE_SEQUENCE) {
				SimpleSequence sequence = (SimpleSequence) entry.getValue();
				fieldsArray.add(new ViewerStructSequenceSequenceProperty(sequence, this));
			}
		}
		setToDefault();
	}

	@Override
	protected StructSequenceRef getValueRef() {
		return (StructSequenceRef) super.getValueRef();
	}

	@Override
	public void addPropertyChangeListener(IViewerPropertyChangeListener listener) {
		super.addPropertyChangeListener(listener);
		for (ViewerProperty< ? > p : fieldsArray) {
			p.addPropertyChangeListener(listener);
		}
	}

	@Override
	public void removePropertyChangeListener(IViewerPropertyChangeListener listener) {
		super.removePropertyChangeListener(listener);
		for (ViewerProperty< ? > p : fieldsArray) {
			p.removePropertyChangeListener(listener);
		}
	}

	@Override
	public void setToDefault() {
		for (ViewerProperty< ? > v : fieldsArray) {
			v.setToDefault();
		}
	}

	public List<ViewerStructSequenceSimpleProperty> getSimples() {
		List<ViewerStructSequenceSimpleProperty> simples = new ArrayList<ViewerStructSequenceSimpleProperty>();
		for (ViewerProperty< ? > property : fieldsArray) {
			if (property instanceof ViewerStructSequenceSimpleProperty) {
				simples.add((ViewerStructSequenceSimpleProperty) property);
			}
		}
		return simples;
	}

	public List<ViewerStructSequenceSequenceProperty> getSequences() {
		List<ViewerStructSequenceSequenceProperty> sequences = new ArrayList<ViewerStructSequenceSequenceProperty>();
		for (ViewerProperty< ? > property : fieldsArray) {
			if (property instanceof ViewerStructSequenceSequenceProperty) {
				sequences.add((ViewerStructSequenceSequenceProperty) property);
			}
		}
		return sequences;
	}

	public void setValue(StructSequenceRef value) {
		if (value == null) {
			setToDefault();
			firePropertyChangeEvent();
			return;
		}

		setToDefault();

		List<ViewerStructSequenceSimpleProperty> simplesArray = getSimples();
		for (ViewerStructSequenceSimpleProperty prop : simplesArray) {
			ArrayList<String> newValues = new ArrayList<String>(value.getStructValue().size());
			String simpleValue = prop.def.getValue();
			for (int i = 0; i < value.getStructValue().size(); i++) {
				newValues.add(simpleValue);
			}
			prop.setValues(newValues);
		}
		List<ViewerStructSequenceSequenceProperty> sequencesArray = getSequences();
		for (ViewerStructSequenceSequenceProperty prop : sequencesArray) {
			ArrayList<Values> newValues = new ArrayList<Values>(value.getStructValue().size());
			Values sequenceValue = ((SimpleSequence)prop.def).getValues();
			for (int i = 0; i < value.getStructValue().size(); i++) {
				newValues.add(sequenceValue);
			}
			prop.setValues(newValues);
		}

		for (int i = 0; i < value.getStructValue().size(); i++) {
			StructValue struct = value.getStructValue().get(i);
			for (int j = 0; j < struct.getSimpleRef().size(); j++) {
				SimpleRef simple = struct.getSimpleRef().get(j);
				simplesArray.get(j).getValues().set(i, simple.getValue());
			}
		}
		for (int i = 0; i < value.getStructValue().size(); i++) {
			StructValue struct = value.getStructValue().get(i);
			for (int j = 0; j < struct.getSimpleSequenceRef().size(); j++) {
				SimpleSequenceRef sequence = struct.getSimpleSequenceRef().get(j);
				sequencesArray.get(j).getValues().set(i, sequence.getValues().getValue());
			}
		}

		firePropertyChangeEvent();
	}

	@Override
	public Collection< ? > getChildren(Object object) {
		return fieldsArray;
	}

	@Override
	public Object getValue() {
		return null;
	}

	@Override
	public String getPrfValue() {
		return null;
	}

	@Override
	protected Collection< ? > getKindTypes() {
		return getDefinition().getConfigurationKind();
	}
}
