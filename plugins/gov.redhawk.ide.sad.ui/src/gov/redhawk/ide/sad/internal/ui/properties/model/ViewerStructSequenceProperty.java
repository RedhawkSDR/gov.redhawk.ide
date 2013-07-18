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
import mil.jpeojtrs.sca.prf.StructSequence;
import mil.jpeojtrs.sca.prf.StructSequenceRef;
import mil.jpeojtrs.sca.prf.StructValue;

/**
 * 
 */
public class ViewerStructSequenceProperty extends ViewerProperty<StructSequence> {
	private List<ViewerStructSequenceSimpleProperty> structs = new ArrayList<ViewerStructSequenceSimpleProperty>();

	public ViewerStructSequenceProperty(StructSequence def, Object parent) {
		super(def, parent);
		for (Simple simple : def.getStruct().getSimple()) {
			structs.add(new ViewerStructSequenceSimpleProperty(simple, this));
		}
		setToDefault();
	}
	
	@Override
	public void addPropertyChangeListener(IViewerPropertyChangeListener listener) {
		super.addPropertyChangeListener(listener);
		for (ViewerStructSequenceSimpleProperty p : structs) {
			p.addPropertyChangeListener(listener);
		}
	}
	
	@Override
	public void removePropertyChangeListener(IViewerPropertyChangeListener listener) {
		super.removePropertyChangeListener(listener);
		for (ViewerStructSequenceSimpleProperty p : structs) {
			p.removePropertyChangeListener(listener);
		}
	}

	@Override
	public void setToDefault() {
		for (ViewerStructSequenceSimpleProperty v : structs) {
			v.setToDefault();
		}
	}

	public List<ViewerStructSequenceSimpleProperty> getStructs() {
		return structs;
	}

	public void setValue(StructSequenceRef value) {
		if (value == null) {
			setToDefault();
			return;
		}
		for (ViewerStructSequenceSimpleProperty p : structs) {
			p.setValues(new ArrayList<String>(value.getStructValue().size()));
		}
		for (StructValue v : value.getStructValue()) {
			for (SimpleRef ref : v.getSimpleRef()) {
				for (ViewerStructSequenceSimpleProperty p : structs) {
					if (ref.getRefID().equals(p.getDefinition().getId())) {
						p.getValues().add(ref.getValue());
						break;
					}
				}
			}
		}
	}

}
