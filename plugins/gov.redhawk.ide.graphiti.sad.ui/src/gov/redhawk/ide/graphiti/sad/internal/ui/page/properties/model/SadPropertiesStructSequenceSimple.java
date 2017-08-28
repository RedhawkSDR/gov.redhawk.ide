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
package gov.redhawk.ide.graphiti.sad.internal.ui.page.properties.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;

import mil.jpeojtrs.sca.prf.Simple;
import mil.jpeojtrs.sca.prf.SimpleRef;
import mil.jpeojtrs.sca.prf.StructValue;

/**
 *
 */
public class SadPropertiesStructSequenceSimple extends SadPropertiesStructSequenceNestedProperty {

	public SadPropertiesStructSequenceSimple(AdapterFactory adapterFactory, Simple def, SadPropertiesStructSequence parent) {
		super(adapterFactory, def, parent);
	}

	@Override
	protected List<String> getRefValues(List<StructValue> structValues) {
		List<String> refValues = new ArrayList<String>(structValues.size());
		for (StructValue structVal : structValues) {
			SimpleRef ref = (SimpleRef) structVal.getRef(getID());
			if (ref != null) {
				refValues.add(ref.getValue());
			}
		}
		return refValues;
	}
}
