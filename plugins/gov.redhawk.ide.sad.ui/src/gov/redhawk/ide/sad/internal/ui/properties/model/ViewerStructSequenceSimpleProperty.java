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
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.EList;

import mil.jpeojtrs.sca.prf.AbstractPropertyRef;
import mil.jpeojtrs.sca.prf.Simple;
import mil.jpeojtrs.sca.prf.SimpleRef;
import mil.jpeojtrs.sca.prf.StructSequence;
import mil.jpeojtrs.sca.prf.StructValue;

/**
 * 
 */
public class ViewerStructSequenceSimpleProperty extends ViewerProperty<Simple> {

	private List<String> values = Collections.emptyList();

	public ViewerStructSequenceSimpleProperty(Simple def, ViewerStructSequenceProperty parent) {
		super(def, parent);
	}
	
	@Override
	protected SimpleRef getRef() {
		return (SimpleRef) super.getRef();
	}

	@Override
	public ViewerStructSequenceProperty getParent() {
		return (ViewerStructSequenceProperty) super.getParent();
	}

	public List<String> getValues() {
		return values;
	}
	
	public void setValues(List<String> values) {
		this.values = values;
	}

	@Override
	public void setToDefault() {
		this.values = null;
	}

	@Override
	public Object getValue() {
		return getValues();
	}

	@Override
	public String getPrfValue() {
		StructSequence seq = getParent().getDefinition();
		EList<StructValue> value = seq.getStructValue();
		List<String> retVal = new ArrayList<String>(value.size());
		for (StructValue v : value) {
			SimpleRef ref = (SimpleRef) v.getRef(getDefinition().getId());
			retVal.add(ref.getValue());
		}
		return Arrays.toString(retVal.toArray());
	}

	@Override
	protected Collection< ? > getKindTypes() {
		return null;
	}
}
