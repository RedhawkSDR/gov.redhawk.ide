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

import gov.redhawk.ide.graphiti.sad.internal.ui.page.properties.model.SadProperty;
import gov.redhawk.sca.sad.validation.DuplicateAssemblyExternalPropertyIDConstraint;
import gov.redhawk.sca.sad.validation.DuplicateExternalPropertyIDConstraint;
import mil.jpeojtrs.sca.prf.util.PropertiesUtil;
import mil.jpeojtrs.sca.sad.ExternalProperty;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.edit.CellEditDescriptor;
import org.eclipse.nebula.widgets.xviewer.edit.XViewerControlFactory;
import org.eclipse.nebula.widgets.xviewer.edit.XViewerConverter;
import org.eclipse.swt.widgets.Control;

public class PropertiesViewerEditingSupport implements XViewerControlFactory, XViewerConverter {

	@Override
	public Control createControl(CellEditDescriptor ced, final XViewer xv) {
		IStructuredSelection ss = (IStructuredSelection) xv.getSelection();
		final SadProperty prop = (SadProperty) ss.getFirstElement();
		if (ced.getInputField().equals(PropertiesViewerFactory.EXTERNAL.getId())) {
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
		} else if (ced.getInputField().equals(PropertiesViewerFactory.SAD_VALUE.getId())) {
			if (PropertiesUtil.canOverride(prop.getDefinition())) {
				return prop.createCellEditor(xv.getTree());
			}
		}
		return null;
	}

	protected boolean isUniqueProperty(String text, SoftwareAssembly sad) {
		ExternalProperty prop = SadFactory.eINSTANCE.createExternalProperty();
		prop.setPropID(text);
		return DuplicateExternalPropertyIDConstraint.validateProperty(prop, sad);
	}


	@Override
	public void setInput(Control c, CellEditDescriptor ced, Object selObject) {
		if (ced.getInputField().equals(PropertiesViewerFactory.EXTERNAL.getId())) {
			SadProperty prop = ((SadProperty) selObject);
			String value = getUniqueValue(prop);
			((XViewerCellEditor) c).setValue(value);
		} else if (ced.getInputField().equals(PropertiesViewerFactory.SAD_VALUE.getId())) {
			if (c instanceof XViewerCellEditor) {
				Object value = ((SadProperty) selObject).getSadValue();
				((XViewerCellEditor) c).setValue(value);
			}
		}
	}

	private String getUniqueValue(SadProperty viewerProp) {
		if (viewerProp.getExternalID() != null) {
			return viewerProp.getExternalID();
		}

		ExternalProperty prop = SadFactory.eINSTANCE.createExternalProperty();
		prop.setPropID(viewerProp.getID());
		SoftwareAssembly sad = ScaEcoreUtils.getEContainerOfType(viewerProp.getComponentInstantiation(), SoftwareAssembly.class);
		for (int i = 1; !DuplicateAssemblyExternalPropertyIDConstraint.validateProperty(prop, sad); i++) {
			prop.setPropID(viewerProp.getID() + "_" + i);
		}
		for (int i = 1; !DuplicateExternalPropertyIDConstraint.validateProperty(prop, sad); i++) {
			prop.setPropID(viewerProp.getID() + "_" + i);
		}
		return prop.getPropID();
	}

	@Override
	public Object getInput(Control c, CellEditDescriptor ced, Object selObject) {
		if (ced.getInputField().equals(PropertiesViewerFactory.EXTERNAL.getId())) {
			return setExternalID(c, selObject);
		} else if (ced.getInputField().equals(PropertiesViewerFactory.SAD_VALUE.getId())) {
			return setSadValue(c, selObject);
		}
		return null;
	}

	protected Object setSadValue(Control c, Object selObject) {
		Object newValue = null;
		if (c instanceof XViewerCellEditor) {
			newValue = ((XViewerCellEditor) c).getValue();
			if (newValue == null) {
				return null;
			}
		} else {
			return null;
		}

		if (selObject instanceof SadProperty) {
			SadProperty property = (SadProperty) selObject;
			property.setSadValue(newValue);
		}
		return selObject;
	}

	protected Object setExternalID(Control c, Object selObject) {
		Object value = ((XViewerCellEditor) c).getValue();
		if (value == null) {
			return null;
		}
		((SadProperty) selObject).setExternalID((String) value);
		return selObject;
	}

	@Override
	public boolean isValid(CellEditDescriptor ced, Object selObject) {
		if (ced.getInputField().equals(PropertiesViewerFactory.EXTERNAL.getId())) {
			return ((SadProperty) selObject).canSetExternalId();
		}
		return true;
	}
}
