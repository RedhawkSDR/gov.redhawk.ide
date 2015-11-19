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

import java.util.Arrays;
import java.util.List;

import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;

/**
 * 
 */
public final class PropertiesDefaultCustomizations {
	private PropertiesDefaultCustomizations() {

	}

	private static final List<XViewerColumn> DEFAULT_COLUMNS = Arrays.asList(new XViewerColumn[] { PropertiesViewerFactory.NAME, PropertiesViewerFactory.EXTERNAL,
		PropertiesViewerFactory.PRF_VALUE, PropertiesViewerFactory.SAD_VALUE });

	public static CustomizeData getConfigurableProperitesCustomization() {
		CustomizeData data = new CustomizeData();
		data.setName("Configurable Properties");
		data.setGuid(PropertiesViewerFactory.NAMESPACE + ".configurableProperties");
		data.setNameSpace(PropertiesViewerFactory.NAMESPACE);

		// Columns must be copied cause they each store their own manipulation data and can be used
		// across multiple customizations.
		XViewerColumn kindColumn = PropertiesViewerFactory.KIND.copy();
		kindColumn.setShow(true);
		
		XViewerColumn modeColumn = PropertiesViewerFactory.MODE.copy();
		modeColumn.setShow(true);
		
		data.getColumnData().getColumns().addAll(DEFAULT_COLUMNS);
		data.getColumnData().getColumns().add(kindColumn);
		data.getColumnData().getColumns().add(modeColumn);

		data.getColumnFilterData().setFilterText(kindColumn.getId(), "configure");
		data.getColumnFilterData().setFilterText(modeColumn.getId(), "write");

		return data;
	}
	
	public static CustomizeData getExecParamsCustomization() {
		CustomizeData data = new CustomizeData();
		data.setName("Exec Params");
		data.setGuid(PropertiesViewerFactory.NAMESPACE + ".execParams");
		data.setNameSpace(PropertiesViewerFactory.NAMESPACE);

		// Columns must be copied cause they each store their own manipulation data and can be used
		// across multiple customizations.
		XViewerColumn kindColumn = PropertiesViewerFactory.KIND.copy();
		kindColumn.setShow(true);
		
		XViewerColumn modeColumn = PropertiesViewerFactory.MODE.copy();
		modeColumn.setShow(true);
		
		data.getColumnData().getColumns().addAll(DEFAULT_COLUMNS);
		data.getColumnData().getColumns().add(kindColumn);
		data.getColumnData().getColumns().add(modeColumn);

		data.getColumnFilterData().setFilterText(kindColumn.getId(), "execparam");
		data.getColumnFilterData().setFilterText(modeColumn.getId(), "write");

		return data;
	}
}
