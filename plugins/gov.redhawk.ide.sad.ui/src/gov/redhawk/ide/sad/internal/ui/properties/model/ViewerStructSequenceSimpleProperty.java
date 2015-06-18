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

import java.util.Collections;
import java.util.List;

import mil.jpeojtrs.sca.prf.Simple;

/**
 * 
 */
public class ViewerStructSequenceSimpleProperty extends ViewerProperty<Simple> {

	private List<String> values = Collections.emptyList();

	public ViewerStructSequenceSimpleProperty(Simple def, ViewerStructSequenceProperty parent) {
		super(def, parent);
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
}
