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
import gov.redhawk.ide.sad.internal.ui.properties.model.ViewerProperty;
import gov.redhawk.sca.sad.validation.DuplicateExternalPropertyIDConstraint;

import mil.jpeojtrs.sca.sad.ExternalProperty;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.edit.CellEditDescriptor;
import org.eclipse.nebula.widgets.xviewer.edit.DefaultXViewerControlFactory;
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
				if (prop.canSetExternalId()) {

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
			if (editElement instanceof ViewerProperty<?>) {
				return ((ViewerProperty< ? >) editElement).createCellEditor(xv.getTree());
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
