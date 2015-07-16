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

import org.eclipse.emf.common.notify.AdapterFactory;

import mil.jpeojtrs.sca.prf.SimpleSequence;
import mil.jpeojtrs.sca.prf.SimpleSequenceRef;
import mil.jpeojtrs.sca.prf.StructValue;

/**
 *
 */
public class SadPropertiesStructSequenceSimpleSequence extends SadPropertiesStructSequenceNestedProperty {

	public SadPropertiesStructSequenceSimpleSequence(AdapterFactory adapterFactory, SimpleSequence def, SadPropertiesStructSequence parent) {
		super(adapterFactory, def, parent);
	}

	@Override
	protected List<List<String>> getRefValues(List<StructValue> structValues) {
		List<List<String>> refValues = new ArrayList<List<String>>(structValues.size());
		for (StructValue structVal : structValues) {
			SimpleSequenceRef ref = (SimpleSequenceRef) structVal.getRef(getID());
			if (ref != null) {
				List<String> values = new ArrayList<String>();
				values.addAll(ref.getValues().getValue());
				refValues.add(values);
			}
		}
		return refValues;
	}

}
