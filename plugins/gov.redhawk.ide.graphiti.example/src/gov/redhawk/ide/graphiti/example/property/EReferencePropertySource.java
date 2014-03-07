/*******************************************************************************
 * <copyright>
 *
 * Copyright (c) 2012, 2012 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP AG - initial API, implementation and documentation
 *
 * </copyright>
 *
 *******************************************************************************/
package gov.redhawk.ide.graphiti.example.property;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * Defines the properties that shall be displayed for {@link EReference}s
 * selected in the {@link DiagramEditor}.
 */
public class EReferencePropertySource implements IPropertySource {

	private static final String NAME_ID = "name";
	private static final PropertyDescriptor NAME_PROP_DESC = new PropertyDescriptor(NAME_ID, "Name");
	private static final IPropertyDescriptor[] DESCRIPTORS = { NAME_PROP_DESC };

	private EReference reference;

	public EReferencePropertySource(EReference reference) {
		super();
		this.reference = reference;
	}

	public Object getEditableValue() {
		return null;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return DESCRIPTORS;
	}

	public Object getPropertyValue(Object id) {
		if (NAME_ID.equals(id)) {
			return reference.getName();
		}
		return null;
	}

	public boolean isPropertySet(Object id) {
		return false;
	}

	public void resetPropertyValue(Object id) {
	}

	public void setPropertyValue(Object id, Object value) {
	}
}
