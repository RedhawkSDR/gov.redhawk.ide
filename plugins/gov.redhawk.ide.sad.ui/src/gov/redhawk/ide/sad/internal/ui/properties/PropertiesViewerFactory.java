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

import gov.redhawk.ide.sad.internal.ui.editor.SadEditor;
import gov.redhawk.ide.sad.internal.ui.properties.model.ViewerProperty;
import gov.redhawk.ide.sad.internal.ui.properties.model.SadPropertiesSimpleSequence;
import gov.redhawk.ide.sad.internal.ui.properties.model.SadPropertiesSimple;
import gov.redhawk.ide.sad.internal.ui.properties.model.SadPropertiesStruct;
import gov.redhawk.ide.sad.internal.ui.properties.model.SadPropertiesStructSequence;

import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.nebula.widgets.xviewer.XViewerFactory;
import org.eclipse.nebula.widgets.xviewer.customize.IXViewerCustomizations;
import org.eclipse.nebula.widgets.xviewer.edit.CellEditDescriptor;
import org.eclipse.nebula.widgets.xviewer.edit.ExtendedViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;

/**
 * 
 */
public class PropertiesViewerFactory extends XViewerFactory {

	public static final String NAMESPACE = SadEditor.ID + ".propertiesViewer";

	public static final XViewerColumn NAME = new XViewerColumn(NAMESPACE + ".name", "Name", 140, SWT.LEFT, false, SortDataType.String, false,
		"Name of the property or component");
	public static final XViewerColumn ID = new XViewerColumn(NAMESPACE + ".id", "ID", 140, SWT.LEFT, true, SortDataType.String, false,
		"ID of the property or component");
	public static final ExtendedViewerColumn EXTERNAL = new ExtendedViewerColumn(NAMESPACE + ".external", "External ID", 140, SWT.LEFT, true, SortDataType.String,
		false, "External name of the property");
	public static final XViewerColumn PRF_VALUE = new XViewerColumn(NAMESPACE + ".prfValue", "PRF Value", 140, SWT.LEFT, true, SortDataType.String, false,
		"Value of the property within the PRF");
	public static final ExtendedViewerColumn SAD_VALUE = new ExtendedViewerColumn(NAMESPACE + ".sadValue", "SAD Value", 140, SWT.LEFT, true,
		SortDataType.String, false, "Value of the property within the SAD");
	public static final XViewerColumn TYPE = new XViewerColumn(NAMESPACE + ".type", "Type", 100, SWT.LEFT, false, SortDataType.String, false,
		"Type of the property");
	public static final XViewerColumn KIND = new XViewerColumn(NAMESPACE + ".kind", "Kind", 100, SWT.LEFT, false, SortDataType.String, false,
		"Kind of the property");
	public static final XViewerColumn MODE = new XViewerColumn(NAMESPACE + ".mode", "Mode", 100, SWT.LEFT, false, SortDataType.String, false,
		"Mode of the property");
	public static final XViewerColumn UNITS = new XViewerColumn(NAMESPACE + ".units", "Units", 100, SWT.LEFT, false, SortDataType.String, false,
			"Units of the property");
	public static final XViewerColumn ACTION = new XViewerColumn(NAMESPACE + ".action", "Action", 100, SWT.LEFT, false, SortDataType.String, false,
			"Action of the property");
	public static final XViewerColumn ENUMERATIONS = new XViewerColumn(NAMESPACE + ".enums", "Enumerations", 100, SWT.LEFT, false, SortDataType.String, false,
			"Enumerations of the property");
	public static final XViewerColumn RANGE = new XViewerColumn(NAMESPACE + ".range", "Range", 100, SWT.LEFT, false, SortDataType.String, false,
			"Range of the property");
	public static final XViewerColumn DESCRIPTION = new XViewerColumn(NAMESPACE + ".description", "Description", 100, SWT.LEFT, false,
		SortDataType.String_MultiLine, false, "Description of the property");
	
	static {
		// We handle the creation of cell editors ourselves to behave in a more EMF-like manner; however, to work
		// within XViewer, it is still necessary to register a CellEditDescriptor for each item class. There are only
		// two fields that are used: "control" and "inputField". The former must be non-null, or XViewerEditAdapter
		// will not call the createControl() method, but is otherwise ignored. The latter is the column ID and is used
		// to distinguish what value we are editing.
		
		// All top-level property types can potentially set an external ID; at edit time, we check whether the current
		// selection is actually a top-level property (i.e., is not part of a struct).
		CellEditDescriptor externalDescriptor = new CellEditDescriptor(Control.class, SWT.NONE, EXTERNAL.getId(), ViewerProperty.class);
		EXTERNAL.addMapEntry(SadPropertiesSimple.class, externalDescriptor);
		EXTERNAL.addMapEntry(SadPropertiesSimpleSequence.class, externalDescriptor);
		EXTERNAL.addMapEntry(SadPropertiesStruct.class, externalDescriptor);
		EXTERNAL.addMapEntry(SadPropertiesStructSequence.class, externalDescriptor);

		// SAD values can only be set via the simple, simple sequence and struct sequence items. Struct properties do
		// not provide an editor themselves, but rather rely on the child items. The children of struct sequences are
		// used strictly for display and cannot be directly edited.
		CellEditDescriptor sadValueDescriptor = new CellEditDescriptor(Control.class, SWT.NONE, SAD_VALUE.getId(), ViewerProperty.class);
		SAD_VALUE.addMapEntry(SadPropertiesSimple.class, sadValueDescriptor);
		SAD_VALUE.addMapEntry(SadPropertiesSimpleSequence.class, sadValueDescriptor);
		SAD_VALUE.addMapEntry(SadPropertiesStructSequence.class, sadValueDescriptor);
	}

	/**
	 * @param namespace
	 */
	public PropertiesViewerFactory() {
		super(NAMESPACE);
		registerColumns(ID, NAME, EXTERNAL, PRF_VALUE, SAD_VALUE, TYPE, KIND, MODE, UNITS, ACTION, ENUMERATIONS, DESCRIPTION);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.xviewer.IXViewerFactory#isAdmin()
	 */
	@Override
	public boolean isAdmin() {
		return false;
	}

	@Override
	public boolean isSearhTop() {
		return false;
	}
	
	@Override
	public IXViewerCustomizations getXViewerCustomizations() {
		return new PropertiesViewerCustomizations();
	}

}
