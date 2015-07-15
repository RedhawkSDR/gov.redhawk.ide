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
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;

import gov.redhawk.ide.sad.internal.ui.editor.XViewerCellEditor;
import gov.redhawk.ide.sad.internal.ui.editor.XViewerDialogCellEditor;
import gov.redhawk.model.sca.ScaFactory;
import gov.redhawk.model.sca.ScaSimpleProperty;
import gov.redhawk.model.sca.ScaSimpleSequenceProperty;
import gov.redhawk.model.sca.ScaStructProperty;
import gov.redhawk.model.sca.ScaStructSequenceProperty;
import gov.redhawk.sca.internal.ui.properties.SequencePropertyValueWizard;
import mil.jpeojtrs.sca.prf.PrfFactory;
import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.prf.Simple;
import mil.jpeojtrs.sca.prf.SimpleRef;
import mil.jpeojtrs.sca.prf.SimpleSequence;
import mil.jpeojtrs.sca.prf.SimpleSequenceRef;
import mil.jpeojtrs.sca.prf.StructSequence;
import mil.jpeojtrs.sca.prf.StructSequenceRef;
import mil.jpeojtrs.sca.prf.StructValue;
import mil.jpeojtrs.sca.prf.Values;

/**
 * 
 */
public class SadPropertiesStructSequence extends SadPropertyImpl<StructSequence> {

	public SadPropertiesStructSequence(AdapterFactory adapterFactory, StructSequence def, Object parent) {
		super(adapterFactory, def, parent);
		for (FeatureMap.Entry entry : def.getStruct().getFields()) {
			if (entry.getEStructuralFeature() == PrfPackage.Literals.STRUCT__SIMPLE) {
				Simple simple = (Simple) entry.getValue();
				getChildren().add(new SadPropertiesStructSequenceSimple(adapterFactory, simple, this));
			} else if (entry.getEStructuralFeature() == PrfPackage.Literals.STRUCT__SIMPLE_SEQUENCE) {
				SimpleSequence sequence = (SimpleSequence) entry.getValue();
				getChildren().add(new SadPropertiesStructSequenceSimpleSequence(adapterFactory, sequence, this));
			}
		}
	}

	@Override
	public StructSequenceRef getValueRef() {
		return (StructSequenceRef) super.getValueRef();
	}

	@Override
	public Object getSadValue() {
		if (ref != null) {
			return formatListSize(((StructSequenceRef) ref).getStructValue());
		}
		return "";
	}

	@Override
	protected boolean isEmpty(Object value) {
		return ((Collection< ? >) value).isEmpty();
	}

	@Override
	public String getPrfValue() {
		return formatListSize(def.getStructValue());
	}

	private String formatListSize(List< ? > value) {
		if (value != null) {
			int items = value.size();
			return "[" + items + "]";
		}
		return "";
	}

	@Override
	protected Collection< ? > getKindTypes() {
		return getDefinition().getConfigurationKind();
	}

	@Override
	protected Object createModelObject(EStructuralFeature feature, Object value) {
		if (feature == SadPropertiesPackage.Literals.SAD_PROPERTY__VALUE) {
			StructSequenceRef structSequenceRef = PrfFactory.eINSTANCE.createStructSequenceRef();
			structSequenceRef.setRefID(getID());
			for (Object item : (Collection< ? >) value) {
				structSequenceRef.getStructValue().add((StructValue) item);
			}
			return structSequenceRef;
		}
		return super.createModelObject(feature, value);
	}

	@Override
	protected Command createSetCommand(EditingDomain domain, Object owner, EStructuralFeature feature, Object value) {
		if (feature == SadPropertiesPackage.Literals.SAD_PROPERTY__VALUE) {
			return SetCommand.create(domain, owner, PrfPackage.Literals.STRUCT_SEQUENCE_REF__STRUCT_VALUE, value);
		}
		return super.createSetCommand(domain, owner, feature, value);
	}

	@Override
	public XViewerCellEditor createCellEditor(Composite parent) {
		return new XViewerDialogCellEditor(parent) {

			@Override
			protected Object openDialogBox() {
				ScaStructSequenceProperty property = ScaFactory.eINSTANCE.createScaStructSequenceProperty();
				StructSequenceRef ref = getValueRef();
				property.setDefinition(getDefinition());
				if (ref != null) {
					property.fromAny(ref.toAny());
				}
				SequencePropertyValueWizard wizard = new SequencePropertyValueWizard(property);
				WizardDialog dialog = new WizardDialog(getShell(), wizard);
				if (dialog.open() == Window.OK) {
					property = (ScaStructSequenceProperty) wizard.getProperty();
					return toStructValues(property);
				}
				return null;
			}
		};
	}

	private Collection< ? > toStructValues(ScaStructSequenceProperty property) {
		List<StructValue> result = new ArrayList<StructValue>();
		for (ScaStructProperty structProperty : property.getStructs()) {
			StructValue structValue = PrfFactory.eINSTANCE.createStructValue();
			for (ScaSimpleProperty simple : structProperty.getSimples()) {
				SimpleRef simpleRef = PrfFactory.eINSTANCE.createSimpleRef();
				simpleRef.setRefID(simple.getId());
				simpleRef.setValue(simple.getValue().toString());
				structValue.getSimpleRef().add(simpleRef);
			}
			for (ScaSimpleSequenceProperty simpleSequence : structProperty.getSequences()) {
				SimpleSequenceRef simpleSequenceRef = PrfFactory.eINSTANCE.createSimpleSequenceRef();
				simpleSequenceRef.setRefID(simpleSequence.getId());
				Values values = PrfFactory.eINSTANCE.createValues();
				for (Object value : simpleSequence.getValues()) {
					values.getValue().add(value.toString());
				}
				simpleSequenceRef.setValues(values);
				structValue.getSimpleSequenceRef().add(simpleSequenceRef);
			}
			result.add(structValue);
		}
		return result;
	}

}
