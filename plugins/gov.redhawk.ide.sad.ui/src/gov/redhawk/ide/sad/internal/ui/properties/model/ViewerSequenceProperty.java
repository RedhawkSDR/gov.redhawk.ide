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
import java.util.List;

import mil.jpeojtrs.sca.prf.SimpleSequence;
import mil.jpeojtrs.sca.prf.Values;

/**
 * 
 */
public class ViewerSequenceProperty extends ViewerProperty<SimpleSequence> {
	
	private List<String> values = new ArrayList<String>();

	public ViewerSequenceProperty(SimpleSequence def, Object parent) {
		super(def, parent);
	}
	
	@Override
	public void setToDefault() {
		values.clear();
	}

	public List<String> getValues() {
		return values;
	}
	
	public void setValues(String ... values) {
		this.values = new ArrayList<String>(Arrays.asList(values));
	}

	public void setValues(Values newValue) {
		if (newValue != null) {
			setValues(newValue.getValue().toArray(new String[newValue.getValue().size()]));
		} else {
			setValues();
		}
	}
}
