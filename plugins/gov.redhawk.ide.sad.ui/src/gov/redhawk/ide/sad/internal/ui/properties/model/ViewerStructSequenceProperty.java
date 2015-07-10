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

import java.util.Collection;

import org.eclipse.emf.ecore.util.FeatureMap;

import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.prf.Simple;
import mil.jpeojtrs.sca.prf.SimpleSequence;
import mil.jpeojtrs.sca.prf.StructSequence;
import mil.jpeojtrs.sca.prf.StructSequenceRef;

/**
 * 
 */
public class ViewerStructSequenceProperty extends ViewerProperty<StructSequence> {

	public ViewerStructSequenceProperty(StructSequence def, Object parent) {
		super(def, parent);
		for (FeatureMap.Entry entry : def.getStruct().getFields()) {
			if (entry.getEStructuralFeature() == PrfPackage.Literals.STRUCT__SIMPLE) {
				Simple simple = (Simple) entry.getValue();
				getChildren().add(new ViewerStructSequenceSimpleProperty(simple, this));
			} else if (entry.getEStructuralFeature() == PrfPackage.Literals.STRUCT__SIMPLE_SEQUENCE) {
				SimpleSequence sequence = (SimpleSequence) entry.getValue();
				getChildren().add(new ViewerStructSequenceSequenceProperty(sequence, this));
			}
		}
	}

	@Override
	public StructSequenceRef getValueRef() {
		return (StructSequenceRef) super.getValueRef();
	}

	public void setValue(StructSequenceRef value) {
		// TODO: Update values in SAD
	}

	@Override
	public Object getValue() {
		return null;
	}

	@Override
	public String getPrfValue() {
		return null;
	}

	@Override
	protected Collection< ? > getKindTypes() {
		return getDefinition().getConfigurationKind();
	}
}
