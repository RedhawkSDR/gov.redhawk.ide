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

import gov.redhawk.sca.util.PluginUtil;
import mil.jpeojtrs.sca.prf.Simple;
import mil.jpeojtrs.sca.prf.SimpleRef;

public class ViewerSimpleProperty extends ViewerProperty<Simple> {

	private String value;

	public ViewerSimpleProperty(Simple def, Object parent) {
		super(def, parent);
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public String getPrfValue() {
		return getDefinition().getValue();
	}

	@Override
	public void setToDefault() {
		setValue((String) null);
	}

	public void setValue(SimpleRef value) {
		if (value != null) {
			setValue(value.getValue());
		} else {
			setToDefault();
		}
	}

	public void setValue(String newValue) {
		if (newValue != null) {
			newValue = newValue.trim();
			if (newValue.isEmpty()) {
				newValue = null;
			}
		}
		String oldValue = this.value;
		this.value = newValue;
		if (!PluginUtil.equals(oldValue, value)) {
			firePropertyChangeEvent();
		}
	}


	public boolean checkValue(String text) {
		if (text == null || text.isEmpty() || def.getType().isValueOfType(text, def.isComplex())) {
			return true;
		}
		return false;
	}

}
