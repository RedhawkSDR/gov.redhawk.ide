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

import mil.jpeojtrs.sca.prf.Simple;
import mil.jpeojtrs.sca.prf.SimpleRef;

public class ViewerSimpleProperty extends ViewerProperty<Simple> {
	
	private String value;

	public ViewerSimpleProperty(Simple def, Object parent) {
		super(def, parent);
	}
	
	public String getValue() {
		return value;
	}
	
	@Override
	public void setToDefault() {
		this.value = null;
	}
	
	public void setValue(SimpleRef value) {
		if (value != null) {
			this.value = value.getValue();
		} else {
			this.value = null;
		}
	}

}
