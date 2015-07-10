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

import gov.redhawk.ide.sad.internal.ui.editor.XViewerCellEditor;
import gov.redhawk.ide.sad.internal.ui.properties.model.ViewerProperty;
import gov.redhawk.ide.sad.internal.ui.properties.model.ViewerStructSequenceProperty;
import gov.redhawk.model.sca.ScaFactory;
import gov.redhawk.model.sca.ScaSimpleProperty;
import gov.redhawk.model.sca.ScaStructProperty;
import gov.redhawk.model.sca.ScaStructSequenceProperty;
import gov.redhawk.sca.internal.ui.properties.SequencePropertyValueWizard;
import gov.redhawk.sca.sad.validation.DuplicateAssemblyExternalPropertyIDConstraint;
import gov.redhawk.sca.sad.validation.DuplicateExternalPropertyIDConstraint;
import mil.jpeojtrs.sca.prf.PrfFactory;
import mil.jpeojtrs.sca.prf.SimpleRef;
import mil.jpeojtrs.sca.prf.StructSequenceRef;
import mil.jpeojtrs.sca.prf.StructValue;
import mil.jpeojtrs.sca.sad.ExternalProperty;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.nebula.widgets.xviewer.edit.CellEditDescriptor;
import org.eclipse.nebula.widgets.xviewer.edit.XViewerConverter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;

/**
 * 
 */
public class PropertiesViewerConverter implements XViewerConverter {

	private PropertiesViewerLabelProvider labelProvider;

	public PropertiesViewerConverter(PropertiesViewerLabelProvider labelProvider) {
		this.labelProvider = labelProvider;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.xviewer.edit.XViewerConverter#setInput(org.eclipse.swt.widgets.Control, org.eclipse.nebula.widgets.xviewer.edit.CellEditDescriptor, java.lang.Object)
	 */
	@Override
	public void setInput(Control c, CellEditDescriptor ced, Object selObject) {
		if (ced.getInputField().equals(PropertiesViewerFactory.EXTERNAL.getId()) && selObject instanceof ViewerProperty< ? >) {
			ViewerProperty< ? > prop = ((ViewerProperty< ? >) selObject);
			String value = getUniqueValue(prop);
			((XViewerCellEditor) c).setValue(value);
		} else if (ced.getInputField().equals(PropertiesViewerFactory.SAD_VALUE.getId())) {
			if (c instanceof XViewerCellEditor) {
				Object value = ((ViewerProperty< ? >) selObject).getValue();
				((XViewerCellEditor) c).setValue(value);
			} else {
				String value = labelProvider.getSadValue(selObject);
				if (c instanceof Button) {
					final Button button = (Button) c;

					final ViewerStructSequenceProperty viewerProp = (ViewerStructSequenceProperty) selObject;
					button.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							ScaStructSequenceProperty property = ScaFactory.eINSTANCE.createScaStructSequenceProperty();
							StructSequenceRef ref = viewerProp.getValueRef();
							property.setDefinition(viewerProp.getDefinition());
							if (ref != null) {
								property.fromAny(ref.toAny());
							}
							// TODO: Get values from SAD file
							SequencePropertyValueWizard wizard = new SequencePropertyValueWizard(property);
							WizardDialog dialog = new WizardDialog(button.getShell(), wizard);
							if (dialog.open() == Window.OK) {
								viewerProp.setValue(createRef(property));
							}
						}
					});
				}
				setControlValue(c, value);
			}
		}
	}

	private String getUniqueValue(ViewerProperty< ? > viewerProp) {
		if (viewerProp.getExternalID() != null) {
			return viewerProp.getExternalID();
		}

		ExternalProperty prop = SadFactory.eINSTANCE.createExternalProperty();
		prop.setPropID(viewerProp.resolveExternalID());
		SoftwareAssembly sad = ScaEcoreUtils.getEContainerOfType(viewerProp.getComponentInstantiation(), SoftwareAssembly.class);
		for (int i = 1; !DuplicateAssemblyExternalPropertyIDConstraint.validateProperty(prop, sad); i++) {
			prop.setPropID(viewerProp.getID() + "_" + i);
		}
		for (int i = 1; !DuplicateExternalPropertyIDConstraint.validateProperty(prop, sad); i++) {
			prop.setPropID(viewerProp.getID() + "_" + i);
		}
		return prop.getPropID();
	}

	protected StructSequenceRef createRef(ScaStructSequenceProperty property) {
		if (property.isDefaultValue()) {
			return null;
		}
		StructSequenceRef retVal = PrfFactory.eINSTANCE.createStructSequenceRef();
		retVal.setRefID(property.getId());
		for (ScaStructProperty struct : property.getStructs()) {
			StructValue structRef = PrfFactory.eINSTANCE.createStructValue();
			for (ScaSimpleProperty simple : struct.getSimples()) {
				SimpleRef simpleRef = PrfFactory.eINSTANCE.createSimpleRef();
				simpleRef.setRefID(simple.getId());
				simpleRef.setValue(simple.getValue().toString());
				structRef.getSimpleRef().add(simpleRef);
			}
			retVal.getStructValue().add(structRef);
		}
		return retVal;
	}

	private void setControlValue(Control c, String value) {
		if (value == null) {
			value = "";
		}
		if (c instanceof Combo) {
			Combo combo = (Combo) c;
			combo.setText(value);
			combo.setSelection(new Point(0, combo.getText().length()));
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.xviewer.edit.XViewerConverter#getInput(org.eclipse.swt.widgets.Control, org.eclipse.nebula.widgets.xviewer.edit.CellEditDescriptor, java.lang.Object)
	 */
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

		if (selObject instanceof ViewerProperty< ? >) {
			ViewerProperty< ? > property = (ViewerProperty< ? >) selObject;
			property.setSadValue(newValue);
		}
		return selObject;
	}

	public static String[] split(String seqValue) {
		if (seqValue == null || seqValue.isEmpty()) {
			return null;
		}
		if (seqValue.charAt(0) == '[') {
			seqValue = seqValue.substring(1);
		}
		if (seqValue.charAt(seqValue.length() - 1) == ']') {
			seqValue = seqValue.substring(0, seqValue.length() - 1);
		}
		String[] newArray = seqValue.split(",");
		for (int i = 0; i < newArray.length; i++) {
			newArray[i] = newArray[i].trim();
		}
		return newArray;
	}

	protected Object setExternalID(Control c, Object selObject) {
		Object value = ((XViewerCellEditor) c).getValue();
		if (value == null) {
			return null;
		}
		((ViewerProperty< ? >) selObject).setExternalID((String) value);
		return selObject;
	}

	@Override
	public boolean isValid(CellEditDescriptor ced, Object selObject) {
		return true;
	}
}
