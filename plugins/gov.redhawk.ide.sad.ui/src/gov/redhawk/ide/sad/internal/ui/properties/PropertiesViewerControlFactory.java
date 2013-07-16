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

import gov.redhawk.ide.sad.internal.ui.properties.model.ViewerComponent;
import gov.redhawk.ide.sad.internal.ui.properties.model.ViewerProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mil.jpeojtrs.sca.prf.Enumeration;
import mil.jpeojtrs.sca.prf.PropertyValueType;
import mil.jpeojtrs.sca.prf.Simple;
import mil.jpeojtrs.sca.prf.SimpleSequence;
import mil.jpeojtrs.sca.prf.StructSequence;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.edit.CellEditDescriptor;
import org.eclipse.nebula.widgets.xviewer.edit.DefaultXViewerControlFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * 
 */
public class PropertiesViewerControlFactory extends DefaultXViewerControlFactory {

	@Override
	public Control createControl(CellEditDescriptor ced, XViewer xv) {
		IStructuredSelection ss = (IStructuredSelection) xv.getSelection();
		Object editElement = ss.getFirstElement();
		if (ced.getInputField().equals(PropertiesViewerFactory.EXTERNAL.getId())) {
			if (editElement instanceof ViewerProperty< ? >) {
				ViewerProperty< ? > prop = (ViewerProperty< ? >) editElement;
				if (prop.getParent() instanceof ViewerComponent) {
					Combo combo = new Combo(xv.getTree(), ced.getSwtStyle());
					combo.setItems(new String[] { "", prop.getDefinition().getId() });
					return combo;
				}
			}
			return null;
		} else if (ced.getInputField().equals(PropertiesViewerFactory.SAD_VALUE.getId())) {
			if (editElement instanceof ViewerProperty< ? >) {
				ViewerProperty< ? > prop = (ViewerProperty< ? >) editElement;
				if (prop.getDefinition() instanceof Simple) {
					Simple simple = (Simple) prop.getDefinition();
					if (simple.getType() == PropertyValueType.BOOLEAN) {
						Combo combo = new Combo(xv.getTree(), ced.getSwtStyle() | SWT.READ_ONLY);
						combo.setItems(new String[] { "", "true", "false" });
						return combo;
					} else if (simple.getEnumerations() != null) {
						Combo combo = new Combo(xv.getTree(), ced.getSwtStyle() | SWT.READ_ONLY);
						List<String> values = new ArrayList<String>(simple.getEnumerations().getEnumeration().size());
						for (Enumeration v : simple.getEnumerations().getEnumeration()) {
							values.add(v.getLabel());
						}
						Collections.sort(values);
						combo.setItems(values.toArray(new String[values.size()]));
						return combo;
					} else {
						return new Text(xv.getTree(), ced.getSwtStyle());
					}
				} else if (prop.getDefinition() instanceof SimpleSequence) {
					return new Text(xv.getTree(), ced.getSwtStyle());
				} else if (prop.getDefinition() instanceof StructSequence) {
					Button button = new Button(xv.getTree(), SWT.PUSH);
					button.setText("Edit");
					return button;
				}
			}
		}
		return super.createControl(ced, xv);
	}
}
