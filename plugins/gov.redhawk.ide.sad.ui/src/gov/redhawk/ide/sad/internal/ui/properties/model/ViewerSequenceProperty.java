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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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

	private List<String> values = null;

	public ViewerSequenceProperty(SimpleSequence def, Object parent) {
		super(def, parent);
	}

	@Override
	public void setToDefault() {
		setValues((String[]) null);
	}

	@Override
	public Object getValue() {
		SimpleSequenceRef ref = getRef();
		if (ref != null) {
			return ref.getValues().getValue();
		}
		return null;
	}

	@Override
	protected SimpleSequenceRef getRef() {
		return (SimpleSequenceRef) super.getRef();
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

	public List<String> getValues() {
		return values;
	}

	public void setValues(String... newValues) {
		List<String> oldValue = this.values;
		if (newValues == null || newValues.length == 0) {
			this.values = null;
		} else {
			this.values = new ArrayList<String>(Arrays.asList(newValues));
		}

		if (!PluginUtil.equals(oldValue, this.values)) {
			if (getParent() instanceof ViewerComponent) {
				((ViewerComponent) getParent()).setRef(this, this.values);
			}
			firePropertyChangeEvent();
		}
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

	private void setValues(Values newValue) {
		if (newValue != null) {
			setValues(newValue.getValue().toArray(new String[newValue.getValue().size()]));
		} else {
			setToDefault();
		}
	}

	public void setValues(SimpleSequenceRef propRef) {
		if (propRef != null) {
			setValues(propRef.getValues());
		} else {
			setToDefault();
		}
	}

	@Override
	protected Object createPeer(Object feature, Object value) {
		final String stringFeature = (String) feature;
		if (stringFeature.equals("value")) {
			SimpleSequenceRef ref = PrfFactory.eINSTANCE.createSimpleSequenceRef();
			ref.setRefID(getID());
			ref.setValues(PrfFactory.eINSTANCE.createValues());
			ref.getValues().eSet(PrfPackage.Literals.VALUES__VALUE, value);
			return ref;
		}
		return super.createPeer(feature, value);
	}

	@Override
	protected EStructuralFeature getChildFeature(Object object, Object child) {
		return PrfPackage.Literals.VALUES__VALUE;
	}

	@Override
	public Command createSetCommand(EditingDomain editingDomain, Object owner, Object value) {
		return SetCommand.create(editingDomain, ((SimpleSequenceRef)owner).getValues(), PrfPackage.Literals.VALUES__VALUE, value);
	}

}
