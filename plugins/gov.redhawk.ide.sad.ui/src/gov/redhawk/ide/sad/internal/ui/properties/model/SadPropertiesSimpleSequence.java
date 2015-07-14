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
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;

import gov.redhawk.ide.sad.internal.ui.editor.XViewerCellEditor;
import gov.redhawk.ide.sad.internal.ui.editor.XViewerDialogCellEditor;
import gov.redhawk.sca.internal.ui.wizards.ValuesWizard;
import mil.jpeojtrs.sca.prf.PrfFactory;
import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.prf.SimpleSequence;
import mil.jpeojtrs.sca.prf.SimpleSequenceRef;
import mil.jpeojtrs.sca.prf.Values;

/**
 * 
 */
public class SadPropertiesSimpleSequence extends SadPropertyImpl<SimpleSequence> {

	public SadPropertiesSimpleSequence(AdapterFactory adapterFactory, SimpleSequence def, Object parent) {
		super(adapterFactory, def, parent);
	}

	@Override
	public Object getSadValue() {
		SimpleSequenceRef ref = getValueRef();
		if (ref != null) {
			return ref.getValues().getValue();
		}
		return null;
	}

	@Override
	public void setSadValue(Object value) {
		if (value != null) {
			Collection< ? > values = (Collection< ? >) value;
			if (values.isEmpty()) {
				value = null;
			}
		}
		super.setSadValue(value);
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

	@Override
	protected Object createModelObject(EStructuralFeature feature, Object value) {
		if (feature == SadPropertiesPackage.Literals.SAD_PROPERTY__VALUE) {
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
		if (feature == SadPropertiesPackage.Literals.SAD_PROPERTY__VALUE) {
			return SetCommand.create(domain, ((SimpleSequenceRef)owner).getValues(), PrfPackage.Literals.VALUES__VALUE, value);
		}
		return super.createSetCommand(domain, owner, feature, value);
	}

	@Override
	public XViewerCellEditor createCellEditor(Composite parent) {
		return new XViewerDialogCellEditor(parent) {

			@SuppressWarnings("restriction")
			@Override
			protected Object openDialogBox() {
				ValuesWizard wizard = new ValuesWizard(def.getType(), def.isComplex());
				List<String> values = new ArrayList<String>();
				if (value != null) {
					for (Object item : (Collection< ? >) value) {
						values.add((String) item);
					}
				}
				wizard.setInput(values.toArray(new String[values.size()]));
				WizardDialog dialog = new WizardDialog(getShell(), wizard);
				if (dialog.open() == Window.OK) {
					return Arrays.asList(wizard.getValues());
				}
				return null;
			}

		};
	}

}
