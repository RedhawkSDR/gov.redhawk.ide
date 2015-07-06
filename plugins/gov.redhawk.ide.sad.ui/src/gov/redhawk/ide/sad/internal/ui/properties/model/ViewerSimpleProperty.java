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

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;

import mil.jpeojtrs.sca.prf.PrfFactory;
import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.prf.Simple;
import mil.jpeojtrs.sca.prf.SimpleRef;

public class ViewerSimpleProperty extends ViewerProperty<Simple> {

	public ViewerSimpleProperty(Simple def, Object parent) {
		super(def, parent);
	}

	@Override
	public String getValue() {
		SimpleRef simpleRef = getValueRef();
		if (simpleRef != null) {
			return simpleRef.getValue();
		}
		return null;
	}

	@Override
	protected SimpleRef getValueRef() {
		return (SimpleRef) super.getValueRef();
	}

	@Override
	public String getPrfValue() {
		return getDefinition().getValue();
	}

	@Override
	public void setSadValue(Object value) {
		String stringValue = (String) value;
		if (stringValue != null) {
			stringValue = stringValue.trim();
			if (stringValue.isEmpty()) {
				stringValue = null;
			}
		}
		super.setSadValue(stringValue);
	}

	@Override
	protected Collection< ? > getKindTypes() {
		return getDefinition().getKind();
	}

	@Override
	protected Object createModelObject(EStructuralFeature feature, Object value) {
		if (feature == SadPropertiesPackage.Literals.SAD_PROPERTY__VALUE) {
			SimpleRef ref = PrfFactory.eINSTANCE.createSimpleRef();
			ref.setRefID(getID());
			ref.setValue((String) value);
			return ref;
		}
		return super.createModelObject(feature, value);
	}

	@Override
	protected Command createSetCommand(EditingDomain domain, Object owner, EStructuralFeature feature, Object value) {
		if (feature == SadPropertiesPackage.Literals.SAD_PROPERTY__VALUE) {
			return SetCommand.create(domain, owner, PrfPackage.Literals.SIMPLE_REF__VALUE, value);
		}
		return super.createSetCommand(domain, owner, feature, value);
	}

	public boolean checkValue(String text) {
		if (text == null || text.isEmpty() || def.getType().isValueOfType(text, def.isComplex())) {
			return true;
		}
		return false;
	}

}
