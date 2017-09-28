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
package gov.redhawk.ide.graphiti.sad.internal.ui.page.properties.model;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.ViewerNotification;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;

import gov.redhawk.ide.graphiti.sad.internal.ui.page.properties.XViewerCellEditor;
import gov.redhawk.ide.graphiti.sad.internal.ui.page.properties.XViewerDialogCellEditor;
import gov.redhawk.model.sca.ScaFactory;
import gov.redhawk.model.sca.ScaStructSequenceProperty;
import gov.redhawk.sca.internal.ui.properties.SequencePropertyValueWizard;
import mil.jpeojtrs.sca.prf.PrfFactory;
import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.prf.Simple;
import mil.jpeojtrs.sca.prf.SimpleSequence;
import mil.jpeojtrs.sca.prf.StructSequence;
import mil.jpeojtrs.sca.prf.StructSequenceRef;
import mil.jpeojtrs.sca.prf.StructValue;

@SuppressWarnings("restriction")
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
		if (getRef() != null) {
			return ((StructSequenceRef) getRef()).getStructValue();
		}
		return null;
	}

	@Override
	protected boolean isEmpty(Object value) {
		return ((Collection< ? >) value).isEmpty();
	}

	@Override
	public Object getPrfValue() {
		return getDef().getStructValue();
	}

	@Override
	protected ILabelProvider createLabelProvider() {
		return new LabelProvider() {

			@Override
			public String getText(Object element) {
				if (element != null) {
					int items = ((List< ? >) element).size();
					if (items > 0) {
						return "[" + items + "]";
					}
				}
				return "";
			}

		};
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

			List< ? > list = (List< ? >) value;
			for (int i = 0; i < list.size(); i++) {
				structSequenceRef.getStructValue().add((StructValue) list.get(i));
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
	protected void notifyChanged(Notification msg) {
		if (msg.getFeature() == PrfPackage.Literals.STRUCT_SEQUENCE_REF__STRUCT_VALUE) {
			// When the value changes, it affects all of the children (because their values cut across all of the
			// struct values), so refresh both content and labels to ensure they are updated
			fireNotifyChanged(new ViewerNotification(msg, this, true, true));
			return;
		}
		super.notifyChanged(msg);
	}

	@Override
	public XViewerCellEditor createCellEditor(Composite parent) {
		XViewerDialogCellEditor editor = new XViewerDialogCellEditor(parent) {

			@Override
			protected Object openDialogBox() {
				ScaStructSequenceProperty property = toScaStructSequenceProperty();
				SequencePropertyValueWizard wizard = new SequencePropertyValueWizard(property);
				WizardDialog dialog = new WizardDialog(getShell(), wizard);
				if (dialog.open() == Window.OK) {
					property = (ScaStructSequenceProperty) wizard.getProperty();
					return property.createPropertyRef().getStructValue();
				}
				return null;
			}
		};

		// Use the same label provider to ensure that the value is displayed as "[N]" instead of the using toString()
		editor.setLabelProvider(getLabelProvider());
		return editor;
	}

	private ScaStructSequenceProperty toScaStructSequenceProperty() {
		ScaStructSequenceProperty property = ScaFactory.eINSTANCE.createScaStructSequenceProperty();
		StructSequenceRef ref = getValueRef();
		property.setDefinition(getDefinition());
		if (ref != null) {
			property.setValueFromRef(ref);
		}
		return property;
	}
}
