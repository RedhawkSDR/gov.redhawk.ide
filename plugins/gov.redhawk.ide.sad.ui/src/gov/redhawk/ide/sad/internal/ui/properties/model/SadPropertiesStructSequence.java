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

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;

import gov.redhawk.ide.sad.internal.ui.editor.XViewerCellEditor;
import gov.redhawk.ide.sad.internal.ui.editor.XViewerDialogCellEditor;
import gov.redhawk.model.sca.ScaFactory;
import gov.redhawk.model.sca.ScaStructSequenceProperty;
import gov.redhawk.sca.internal.ui.properties.SequencePropertyValueWizard;
import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.prf.Simple;
import mil.jpeojtrs.sca.prf.SimpleSequence;
import mil.jpeojtrs.sca.prf.StructSequence;
import mil.jpeojtrs.sca.prf.StructSequenceRef;

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
	public void setSadValue(Object value) {
		// TODO: Update values in SAD
	}

	@Override
	public Object getSadValue() {
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
					// TODO: Create value from editor
					return null;
				}
				return null;
			}
		};
	}
}
