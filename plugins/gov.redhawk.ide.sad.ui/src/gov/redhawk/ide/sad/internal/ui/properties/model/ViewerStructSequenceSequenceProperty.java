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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.util.EList;

import mil.jpeojtrs.sca.prf.SimpleSequence;
import mil.jpeojtrs.sca.prf.SimpleSequenceRef;
import mil.jpeojtrs.sca.prf.StructSequence;
import mil.jpeojtrs.sca.prf.StructValue;
import mil.jpeojtrs.sca.prf.Values;

/**
 * 
 */
public class ViewerStructSequenceSequenceProperty extends ViewerProperty<SimpleSequence> {

	private List<List<String>> values = null;

	public ViewerStructSequenceSequenceProperty(SimpleSequence def, ViewerStructSequenceProperty parent) {
		super(def, parent);
	}

	@Override
	public void setToDefault() {
		values = null;
	}

	public void setValues(List<List<String>> values) {
		this.values = values;
	}
	
	public List<List<String>> getValues() {
		return values;
	}

	public void setValues(ArrayList<Values> newValues) {
		List<List<String>> newStrings = new ArrayList<List<String>>();
		for (Values v: newValues) {
			newStrings.add(v.getValue());
		}
		setValues(newStrings);
	}
	
	@Override
	public ViewerStructSequenceProperty getParent() {
		return (ViewerStructSequenceProperty) super.getParent();
	}

	@Override
	public Object getValue() {
		return getValues();
	}

	@Override
	public String getPrfValue() {
		StructSequence seq = getParent().getDefinition();
		EList<StructValue> value = seq.getStructValue();
		List<List<String>> retVal = new ArrayList<List<String>>(value.size());
		for (StructValue v : value) {
			SimpleSequenceRef ref = (SimpleSequenceRef) v.getRef(getDefinition().getId());
			retVal.add(ref.getValues().getValue());
		}
		return Arrays.toString(retVal.toArray());
	}

	@Override
	protected Collection< ? > getKindTypes() {
		return null;
	}

}
