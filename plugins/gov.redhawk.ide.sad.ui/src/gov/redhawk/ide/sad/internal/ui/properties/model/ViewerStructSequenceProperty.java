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
	private List<ViewerStructSequenceSimpleProperty> simplesArray = new ArrayList<ViewerStructSequenceSimpleProperty>();
	private List<ViewerStructSequenceSequenceProperty> sequencesArray = new ArrayList<ViewerStructSequenceSequenceProperty>();

	public ViewerStructSequenceProperty(StructSequence def, Object parent) {
		super(def, parent);
		for (Simple simple : def.getStruct().getSimple()) {
			simplesArray.add(new ViewerStructSequenceSimpleProperty(simple, this));
		}
		for (SimpleSequence sequence : def.getStruct().getSimpleSequence()) {
			sequencesArray.add(new ViewerStructSequenceSequenceProperty(sequence, this));
		}
		setToDefault();
	}

	@Override
	public void addPropertyChangeListener(IViewerPropertyChangeListener listener) {
		super.addPropertyChangeListener(listener);
		for (ViewerStructSequenceSimpleProperty p : simplesArray) {
			p.addPropertyChangeListener(listener);
		}
		for (ViewerStructSequenceSequenceProperty p : sequencesArray) {
			p.addPropertyChangeListener(listener);
		}
	}

	@Override
	public void removePropertyChangeListener(IViewerPropertyChangeListener listener) {
		super.removePropertyChangeListener(listener);
		for (ViewerStructSequenceSimpleProperty p : simplesArray) {
			p.removePropertyChangeListener(listener);
		}
		for (ViewerStructSequenceSequenceProperty p : sequencesArray) {
			p.removePropertyChangeListener(listener);
		}
	}

	@Override
	public void setToDefault() {
		for (ViewerStructSequenceSimpleProperty v : simplesArray) {
			v.setToDefault();
		}
		for (ViewerStructSequenceSequenceProperty v : sequencesArray) {
			v.setToDefault();
		}
	}

	public List<ViewerStructSequenceSimpleProperty> getSimples() {
		return simplesArray;
	}

	public List<ViewerStructSequenceSequenceProperty> getSequences() {
		return sequencesArray;
	}

	public void setValue(StructSequenceRef value) {
		if (value == null) {
			setToDefault();
			firePropertyChangeEvent();
			return;
		}

		setToDefault();

		for (ViewerStructSequenceSimpleProperty prop : simplesArray) {
			ArrayList<String> newValues = new ArrayList<String>(value.getStructValue().size());
			String simpleValue = prop.def.getValue();
			for (int i = 0; i < value.getStructValue().size(); i++) {
				newValues.add(simpleValue);
			}
			prop.setValues(newValues);
		}
		for (ViewerStructSequenceSequenceProperty prop : sequencesArray) {
			ArrayList<Values> newValues = new ArrayList<Values>(value.getStructValue().size());
			Values sequenceValue = prop.def.getValues();
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

	public Collection< ? > getChildren(Object object) {
		return simplesArray;
	}
}
