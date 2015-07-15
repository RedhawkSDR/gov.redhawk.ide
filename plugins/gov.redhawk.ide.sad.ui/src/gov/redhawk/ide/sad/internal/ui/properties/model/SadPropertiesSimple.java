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
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import gov.redhawk.ide.sad.internal.ui.editor.XViewerCellEditor;
import gov.redhawk.ide.sad.internal.ui.editor.XViewerComboCellEditor;
import gov.redhawk.ide.sad.internal.ui.editor.XViewerTextCellEditor;
import mil.jpeojtrs.sca.prf.Enumeration;
import mil.jpeojtrs.sca.prf.PrfFactory;
import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.prf.PropertyValueType;
import mil.jpeojtrs.sca.prf.Simple;
import mil.jpeojtrs.sca.prf.SimpleRef;

public class SadPropertiesSimple extends SadPropertyImpl<Simple> {

	public SadPropertiesSimple(AdapterFactory adapterFactory, Simple def, Object parent) {
		super(adapterFactory, def, parent);
	}

	@Override
	public String getSadValue() {
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
	protected boolean isEmpty(Object value) {
		return ((String) value).isEmpty();
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

	@Override
	public XViewerCellEditor createCellEditor(Composite parent) {
		if (def.getType() == PropertyValueType.BOOLEAN) {
			String[] items = new String[] { "", "true", "false" };
			return new XViewerComboCellEditor(parent, items, SWT.BORDER | SWT.READ_ONLY);
		} else if (def.getEnumerations() != null) {
			List<String> values = new ArrayList<String>(def.getEnumerations().getEnumeration().size());
			values.add("");
			for (Enumeration v : def.getEnumerations().getEnumeration()) {
				values.add(v.getLabel());
			}
			String[] items = values.toArray(new String[values.size()]);
			return new XViewerComboCellEditor(parent, items, SWT.BORDER | SWT.READ_ONLY);
		} else {
			XViewerTextCellEditor editor = new XViewerTextCellEditor(parent, SWT.BORDER);
			editor.setValidator(new ICellEditorValidator() {

				@Override
				public String isValid(Object value) {
					if (checkValue((String) value)) {
						return null;
					} else {
						if (def.isComplex()) {
							return "Value must be of type complex " + def.getType();
						} else {
							return "Value must be of type " + def.getType();
						}
					}
				}
			});
			return editor;
		}
	}

}
