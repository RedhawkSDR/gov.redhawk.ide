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

import mil.jpeojtrs.sca.prf.Simple;
import mil.jpeojtrs.sca.prf.SimpleRef;
import mil.jpeojtrs.sca.prf.Struct;
import mil.jpeojtrs.sca.prf.StructRef;
import mil.jpeojtrs.sca.prf.StructValue;

/**
 * 
 */
public class ViewerStructProperty extends ViewerProperty<Struct> {

	private List<ViewerSimpleProperty> simples = new ArrayList<ViewerSimpleProperty>();

	public ViewerStructProperty(Struct def, Object parent) {
		super(def, parent);
		setToDefault();
	}

	public List<ViewerSimpleProperty> getSimples() {
		return simples;
	}

	@Override
	public void setToDefault() {
		simples.clear();
		for (Simple s : def.getSimple()) {
			simples.add(new ViewerSimpleProperty(s, this));
		}
	}

	public void setValue(StructRef value) {
		if (value != null) {
			for (SimpleRef ref : value.getSimpleRef()) {
				for (ViewerSimpleProperty prop : simples) {
					if (prop.getDefinition().getId().equals(ref.getRefID())) {
						prop.setValue(ref);
					}
				}
			}
		} else {
			for (ViewerSimpleProperty prop : simples) {
				prop.setValue((SimpleRef) null);
			}
		}
	}
	
	@Override
	public void addPropertyChangeListener(IViewerPropertyChangeListener listener) {
		super.addPropertyChangeListener(listener);
		for (ViewerSimpleProperty p : simples) {
			p.addPropertyChangeListener(listener);
		}
	}
	
	@Override
	public void removePropertyChangeListener(IViewerPropertyChangeListener listener) {
		super.removePropertyChangeListener(listener);
		for (ViewerSimpleProperty p : simples) {
			p.removePropertyChangeListener(listener);
		}
	}

	public void setValue(StructValue value) {
		if (value != null) {
			for (SimpleRef ref : value.getSimpleRef()) {
				for (ViewerSimpleProperty prop : simples) {
					if (prop.getDefinition().getId().equals(ref.getRefID())) {
						prop.setValue(ref);
					}
				}
			}
		} else {
			for (ViewerSimpleProperty prop : simples) {
				prop.setValue((SimpleRef) null);
			}
		}
	}

}
