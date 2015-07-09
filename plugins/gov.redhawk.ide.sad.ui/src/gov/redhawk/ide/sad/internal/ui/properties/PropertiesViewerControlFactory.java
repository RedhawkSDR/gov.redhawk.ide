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
package gov.redhawk.ide.sad.internal.ui.properties;

import gov.redhawk.ide.sad.internal.ui.editor.XViewerComboCellEditor;
import gov.redhawk.ide.sad.internal.ui.editor.XViewerDialogCellEditor;
import gov.redhawk.ide.sad.internal.ui.editor.XViewerTextCellEditor;
import gov.redhawk.ide.sad.internal.ui.properties.model.ViewerComponent;
import gov.redhawk.ide.sad.internal.ui.properties.model.ViewerProperty;
import gov.redhawk.ide.sad.internal.ui.properties.model.ViewerSequenceProperty;
import gov.redhawk.ide.sad.internal.ui.properties.model.ViewerSimpleProperty;
import gov.redhawk.ide.sad.internal.ui.properties.model.ViewerStructSequenceProperty;
import gov.redhawk.ide.sad.internal.ui.properties.model.ViewerStructSequenceSimpleProperty;
import gov.redhawk.sca.sad.validation.DuplicateExternalPropertyIDConstraint;

import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.prf.Enumeration;
import mil.jpeojtrs.sca.prf.PropertyValueType;
import mil.jpeojtrs.sca.prf.Simple;
import mil.jpeojtrs.sca.sad.ExternalProperty;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.edit.CellEditDescriptor;
import org.eclipse.nebula.widgets.xviewer.edit.DefaultXViewerControlFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;

/**
 * 
 */
public class PropertiesViewerControlFactory extends DefaultXViewerControlFactory {

	@Override
	public Control createControl(CellEditDescriptor ced, final XViewer xv) {
		IStructuredSelection ss = (IStructuredSelection) xv.getSelection();
		Object editElement = ss.getFirstElement();
		if (ced.getInputField().equals(PropertiesViewerFactory.EXTERNAL.getId())) {
			if (editElement instanceof ViewerProperty< ? >) {
				final ViewerProperty< ? > prop = (ViewerProperty< ? >) editElement;
				if (prop.getParent() instanceof ViewerComponent) {

					String[] items = new String[] { "", prop.getDefinition().getId() };
					XViewerComboCellEditor editor = new XViewerComboCellEditor(xv.getTree(), items, ced.getSwtStyle());

					final SoftwareAssembly sad = ScaEcoreUtils.getEContainerOfType(prop.getComponentInstantiation(), SoftwareAssembly.class);
					editor.setValidator(new ICellEditorValidator() {

						@Override
						public String isValid(Object value) {
							if (value == null || value.equals(prop.getExternalID())) {
								return null;
							} else if (!isUniqueProperty((String) value, sad)) {
								return "Duplicate external property ID";
							}
							return null;
						}
					});

					return editor;
				}
			}
			return null;
		} else if (ced.getInputField().equals(PropertiesViewerFactory.SAD_VALUE.getId())) {
			if (editElement instanceof ViewerSimpleProperty) {
				final ViewerSimpleProperty simpleProp = (ViewerSimpleProperty) editElement;
				final Simple simple = simpleProp.getDefinition();
				if (simple.getType() == PropertyValueType.BOOLEAN) {
					String[] items = new String[] { "", "true", "false" };
					XViewerComboCellEditor editor = new XViewerComboCellEditor(xv.getTree(), items, ced.getSwtStyle() | SWT.READ_ONLY);
					return editor;
				} else if (simple.getEnumerations() != null) {
					List<String> values = new ArrayList<String>(simple.getEnumerations().getEnumeration().size());
					values.add("");
					for (Enumeration v : simple.getEnumerations().getEnumeration()) {
						values.add(v.getLabel());
					}
					String[] items = values.toArray(new String[values.size()]);
					XViewerComboCellEditor editor = new XViewerComboCellEditor(xv.getTree(), items, ced.getSwtStyle() | SWT.READ_ONLY);
					return editor;
				} else {
					XViewerTextCellEditor editor = new XViewerTextCellEditor(xv.getTree(), ced.getSwtStyle());
					editor.setValidator(new ICellEditorValidator() {

						@Override
						public String isValid(Object value) {
							if (simpleProp.checkValue((String) value)) {
								return null;
							} else {
								if (simple.isComplex()) {
									return "Value must be of type complex " + simple.getType();
								} else {
									return "Value must be of type " + simple.getType();
								}
							}
						}
					});
					return editor;
				}
			} else if (editElement instanceof ViewerSequenceProperty) {
				return new XViewerDialogCellEditor(xv.getTree(), ced.getSwtStyle());
			} else if (editElement instanceof ViewerStructSequenceProperty) {
				final Button button = new Button(xv.getTree(), SWT.PUSH);
				button.setText("Edit");
				return button;
			} else if (editElement instanceof ViewerStructSequenceSimpleProperty) {
				// TODO
				return null;
			}
		}
		return super.createControl(ced, xv);
	}

	protected boolean isUniqueProperty(String text, SoftwareAssembly sad) {
		ExternalProperty prop = SadFactory.eINSTANCE.createExternalProperty();
		prop.setPropID(text);
		return DuplicateExternalPropertyIDConstraint.validateProperty(prop, sad);
	}

}
