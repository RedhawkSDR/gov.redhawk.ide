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
package gov.redhawk.ide.graphiti.sad.internal.ui.page.properties;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.edit.CellEditDescriptor;
import org.eclipse.nebula.widgets.xviewer.edit.XViewerControlFactory;
import org.eclipse.nebula.widgets.xviewer.edit.XViewerConverter;
import org.eclipse.swt.widgets.Control;

import gov.redhawk.ide.graphiti.sad.internal.ui.page.properties.model.SadProperty;
import gov.redhawk.sca.sad.validation.DuplicateAssemblyExternalPropertyIDConstraint;
import gov.redhawk.sca.sad.validation.DuplicateExternalPropertyIDConstraint;
import mil.jpeojtrs.sca.prf.util.PropertiesUtil;
import mil.jpeojtrs.sca.sad.ExternalProperty;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

public class PropertiesViewerEditingSupport implements XViewerControlFactory, XViewerConverter {

	@Override
	public Control createControl(CellEditDescriptor ced, final XViewer xv) {
		IStructuredSelection ss = (IStructuredSelection) xv.getSelection();
		final SadProperty prop = (SadProperty) ss.getFirstElement();
		if (ced.getInputField().equals(PropertiesViewerFactory.EXTERNAL.getId())) {
			final SoftwareAssembly sad = ScaEcoreUtils.getEContainerOfType(prop.getComponentInstantiation(), SoftwareAssembly.class);
			XViewerTextCellEditor editor = new XViewerTextCellEditor(xv.getTree(), ced.getSwtStyle());
			editor.setValidator(value -> {
				if (value == null || value.equals(prop.getExternalID())) {
					return null;
				} else if (!isUniqueExternalProperty((String) value, sad)) {
					return "Duplicate external property ID";
				}
				return null;
			});
			return editor;
		} else if (ced.getInputField().equals(PropertiesViewerFactory.SAD_VALUE.getId())) {
			if (PropertiesUtil.canOverride(prop.getDefinition())) {
				return prop.createCellEditor(xv.getTree());
			}
		}
		return null;
	}

	private boolean isUniqueExternalProperty(String text, SoftwareAssembly sad) {
		ExternalProperty prop = SadFactory.eINSTANCE.createExternalProperty();
		prop.setPropID(text);
		return DuplicateAssemblyExternalPropertyIDConstraint.validateProperty(prop, sad) && DuplicateExternalPropertyIDConstraint.validateProperty(prop, sad);
	}

	@Override
	public void setInput(Control c, CellEditDescriptor ced, Object selObject) {
		if (ced.getInputField().equals(PropertiesViewerFactory.EXTERNAL.getId())) {
			String value = getUniqueExternalID(((SadProperty) selObject));
			((XViewerCellEditor) c).setValue(value);
		} else if (ced.getInputField().equals(PropertiesViewerFactory.SAD_VALUE.getId())) {
			Object value = ((SadProperty) selObject).getSadValue();
			((XViewerCellEditor) c).setValue(value);
		}
	}

	private String getUniqueExternalID(SadProperty viewerProp) {
		// Return the existing external ID (if any)
		if (viewerProp.getExternalID() != null) {
			return viewerProp.getExternalID();
		}

		// Create a new external ID that doesn't clash with the assembly controller's properties or other external properties
		ExternalProperty prop = SadFactory.eINSTANCE.createExternalProperty();
		prop.setPropID(viewerProp.getID());
		SoftwareAssembly sad = ScaEcoreUtils.getEContainerOfType(viewerProp.getComponentInstantiation(), SoftwareAssembly.class);
		int i = 1;
		while (!DuplicateAssemblyExternalPropertyIDConstraint.validateProperty(prop, sad)
			|| !DuplicateExternalPropertyIDConstraint.validateProperty(prop, sad)) {
			prop.setPropID(viewerProp.getID() + "_" + i++);
		}
		return prop.getPropID();
	}

	@Override
	public Object getInput(Control c, CellEditDescriptor ced, Object selObject) {
		if (ced.getInputField().equals(PropertiesViewerFactory.EXTERNAL.getId())) {
			XViewerCellEditor cellEditor = ((XViewerCellEditor) c);
			if (cellEditor.isValueValid()) {
				((SadProperty) selObject).setExternalID((String) cellEditor.getValue());
			}
			return selObject;
		} else if (ced.getInputField().equals(PropertiesViewerFactory.SAD_VALUE.getId())) {
			XViewerCellEditor cellEditor = ((XViewerCellEditor) c);
			if (cellEditor.isValueValid()) {
				((SadProperty) selObject).setSadValue(cellEditor.getValue());
			}
			return selObject;
		}
		return null;
	}

	@Override
	public boolean isValid(CellEditDescriptor ced, Object selObject) {
		if (ced.getInputField().equals(PropertiesViewerFactory.EXTERNAL.getId())) {
			return ((SadProperty) selObject).canSetExternalId();
		}
		return true;
	}
}
