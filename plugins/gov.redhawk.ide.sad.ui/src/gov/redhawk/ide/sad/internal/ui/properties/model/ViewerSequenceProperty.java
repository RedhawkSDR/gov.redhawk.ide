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

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;

import mil.jpeojtrs.sca.prf.PrfFactory;
import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.prf.SimpleSequence;
import mil.jpeojtrs.sca.prf.SimpleSequenceRef;
import mil.jpeojtrs.sca.prf.Values;

/**
 * 
 */
public class ViewerSequenceProperty extends ViewerProperty<SimpleSequence> {

	public ViewerSequenceProperty(SimpleSequence def, Object parent) {
		super(def, parent);
	}

	@Override
	public Object getValue() {
		SimpleSequenceRef ref = getValueRef();
		if (ref != null) {
			return ref.getValues().getValue();
		}
		return null;
	}

	@Override
	protected SimpleSequenceRef getValueRef() {
		return (SimpleSequenceRef) super.getValueRef();
	}

	@Override
	public String getPrfValue() {
		Values values = getDefinition().getValues();
		if (values != null) {
			return Arrays.toString(values.getValue().toArray());
		}
		return "";
	}

	@Override
	protected Collection< ? > getKindTypes() {
		return getDefinition().getKind();
	}

	public boolean checkValues(String... newValues) {
		if (newValues != null) {
			for (String s : newValues) {
				if (!def.getType().isValueOfType(s, def.isComplex())) {
					return false;
				}
			}
		}
		return true;
	}

	@Override
	protected Object createModelObject(EStructuralFeature feature, Object value) {
		if (feature == ViewerPackage.Literals.SAD_PROPERTY__VALUE) {
			SimpleSequenceRef ref = PrfFactory.eINSTANCE.createSimpleSequenceRef();
			ref.setRefID(getID());
			ref.setValues(PrfFactory.eINSTANCE.createValues());
			ref.getValues().eSet(PrfPackage.Literals.VALUES__VALUE, value);
			return ref;
		}
		return super.createModelObject(feature, value);
	}

	@Override
	public Command createSetCommand(EditingDomain domain, Object owner, EStructuralFeature feature, Object value) {
		if (feature == ViewerPackage.Literals.SAD_PROPERTY__VALUE) {
			return SetCommand.create(domain, ((SimpleSequenceRef)owner).getValues(), PrfPackage.Literals.VALUES__VALUE, value);
		}
		return super.createSetCommand(domain, owner, feature, value);
	}

}
