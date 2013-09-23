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
package gov.redhawk.ide.internal.ui.event;

import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerFactory;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.swt.SWT;

/**
 * 
 */
public class EventViewerFactory extends XViewerFactory {

	public static final String NAMESPACE = EventView.ID + ".viewer";

	static final XViewerColumn TIME_COL = new XViewerColumn(NAMESPACE + ".time", "Time (HH:MM::ms)", 140, SWT.LEFT, true, SortDataType.Date, false,
		"Timestamp of the event");
	static final XViewerColumn TYPE_COL = new XViewerColumn(NAMESPACE + ".type", "Type", 140, SWT.LEFT, true, SortDataType.String, false,
		"Type of event");
	static final XViewerColumn CHANNEL_COL = new XViewerColumn(NAMESPACE + ".channel", "Channel", 110, SWT.LEFT, true, SortDataType.String, false,
		"Channel event was received from.");
	static final XViewerColumn SOURCE_ID_COL = new XViewerColumn(NAMESPACE + ".sourceID", "Source ID", 80, SWT.LEFT, true, SortDataType.String, false,
		"ID of the source");
	static final XViewerColumn SOURCE_NAME_COL = new XViewerColumn(NAMESPACE + ".sourceName", "Source Name", 80, SWT.LEFT, true, SortDataType.String,
		false, "Name of the source");
	static final XViewerColumn SOURCE_IOR_COL = new XViewerColumn(NAMESPACE + ".sourceIOR", "Source IOR", 80, SWT.LEFT, false, SortDataType.String,
		false, "IOR of the source");
	static final XViewerColumn SOURCE_CATEGORY_COL = new XViewerColumn(NAMESPACE + ".sourceCategory", "Source Category", 80, SWT.LEFT, false,
		SortDataType.String, false, "Category of the source");
	static final XViewerColumn PRODUCER_ID_COL = new XViewerColumn(NAMESPACE + ".producerID", "Producer ID", 80, SWT.LEFT, false, SortDataType.String,
		false, "ID of the producer");
	static final XViewerColumn STATE_CHANGE_CATEGORY_COL = new XViewerColumn(NAMESPACE + ".stateChangeCategory", "State", 80, SWT.LEFT, false,
		SortDataType.String, false, "Category of the state change");
	static final XViewerColumn STATE_FROM_TYPE_COL = new XViewerColumn(NAMESPACE + ".stateFromType", "From State", 80, SWT.LEFT, false,
		SortDataType.String, false, "Previous state value");
	static final XViewerColumn STATE_TO_TYPE_COL = new XViewerColumn(NAMESPACE + ".stateToType", "To State", 80, SWT.LEFT, false, SortDataType.String,
		false, "New state value");
	static final XViewerColumn PROPERTIES_COL = new XViewerColumn(NAMESPACE + ".properties", "Properties", 80, SWT.LEFT, false, SortDataType.String_MultiLine,
		false, "New property values");

	/**
	 * @param namespace
	 */
	public EventViewerFactory() {
		super(NAMESPACE);
		registerColumns(TIME_COL, TYPE_COL, CHANNEL_COL, SOURCE_ID_COL, SOURCE_NAME_COL, SOURCE_IOR_COL, SOURCE_CATEGORY_COL, PRODUCER_ID_COL,
			STATE_CHANGE_CATEGORY_COL, STATE_FROM_TYPE_COL, STATE_TO_TYPE_COL, PROPERTIES_COL);
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

}
