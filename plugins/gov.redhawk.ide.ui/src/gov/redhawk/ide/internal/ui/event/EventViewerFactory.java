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

import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerFactory;
import org.eclipse.nebula.widgets.xviewer.XViewerSorter;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.customize.IXViewerCustomizations;

public class EventViewerFactory extends XViewerFactory {

	public static final String NAMESPACE = EventView.ID + ".viewer";

	static final XViewerColumn TIME_COL_MS = new XViewerColumn(EventViewerFactory.NAMESPACE + ".time_ms", "Time (HH:MM::ms)", 140, XViewerAlign.Left, false,
		SortDataType.Date, false, "Timestamp of the event");
	static final XViewerColumn TIME_COL_SS = new XViewerColumn(EventViewerFactory.NAMESPACE + ".time_ss", "Time (HH:MM::ss)", 140, XViewerAlign.Left, true,
		SortDataType.Date, false, "Timestamp of the event");
	static final XViewerColumn TYPE_COL = new XViewerColumn(EventViewerFactory.NAMESPACE + ".type", "Type", 140, XViewerAlign.Left, true, SortDataType.String,
		false, "Type of event");
	static final XViewerColumn CHANNEL_COL = new XViewerColumn(EventViewerFactory.NAMESPACE + ".channel", "Channel", 110, XViewerAlign.Left, true,
		SortDataType.String, false, "Channel event was received from.");
	static final XViewerColumn SOURCE_ID_COL = new XViewerColumn(EventViewerFactory.NAMESPACE + ".sourceID", "Source ID", 80, XViewerAlign.Left, true,
		SortDataType.String, false, "ID of the source");
	static final XViewerColumn SOURCE_NAME_COL = new XViewerColumn(EventViewerFactory.NAMESPACE + ".sourceName", "Source Name", 80, XViewerAlign.Left, true,
		SortDataType.String, false, "Name of the source");
	static final XViewerColumn SOURCE_IOR_COL = new XViewerColumn(EventViewerFactory.NAMESPACE + ".sourceIOR", "Source IOR", 80, XViewerAlign.Left, false,
		SortDataType.String, false, "IOR of the source");
	static final XViewerColumn SOURCE_CATEGORY_COL = new XViewerColumn(EventViewerFactory.NAMESPACE + ".sourceCategory", "Source Category", 80,
		XViewerAlign.Left, false, SortDataType.String, false, "Category of the source");
	static final XViewerColumn PRODUCER_ID_COL = new XViewerColumn(EventViewerFactory.NAMESPACE + ".producerID", "Producer ID", 80, XViewerAlign.Left, false,
		SortDataType.String, false, "ID of the producer");
	static final XViewerColumn STATE_CHANGE_CATEGORY_COL = new XViewerColumn(EventViewerFactory.NAMESPACE + ".stateChangeCategory", "State", 80,
		XViewerAlign.Left, false, SortDataType.String, false, "Category of the state change");
	static final XViewerColumn STATE_FROM_TYPE_COL = new XViewerColumn(EventViewerFactory.NAMESPACE + ".stateFromType", "From State", 80, XViewerAlign.Left,
		false, SortDataType.String, false, "Previous state value");
	static final XViewerColumn STATE_TO_TYPE_COL = new XViewerColumn(EventViewerFactory.NAMESPACE + ".stateToType", "To State", 80, XViewerAlign.Left, false,
		SortDataType.String, false, "New state value");
	static final XViewerColumn PROPERTIES_COL = new XViewerColumn(EventViewerFactory.NAMESPACE + ".properties", "Properties", 80, XViewerAlign.Left, false,
		SortDataType.String_MultiLine, false, "New property values");

	/**
	 * @param namespace
	 */
	public EventViewerFactory() {
		super(EventViewerFactory.NAMESPACE);
		registerColumns(EventViewerFactory.TIME_COL_SS, EventViewerFactory.TIME_COL_MS, EventViewerFactory.TYPE_COL, EventViewerFactory.CHANNEL_COL,
			EventViewerFactory.SOURCE_ID_COL, EventViewerFactory.SOURCE_NAME_COL, EventViewerFactory.SOURCE_IOR_COL, EventViewerFactory.SOURCE_CATEGORY_COL,
			EventViewerFactory.PRODUCER_ID_COL, EventViewerFactory.STATE_CHANGE_CATEGORY_COL, EventViewerFactory.STATE_FROM_TYPE_COL,
			EventViewerFactory.STATE_TO_TYPE_COL, EventViewerFactory.PROPERTIES_COL);

	}

	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.xviewer.IXViewerFactory#isAdmin()
	 */
	@Override
	public boolean isAdmin() {
		return false;
	}

	@Override
	public XViewerSorter createNewXSorter(XViewer xViewer) {
		return new XViewerSorter(xViewer) {

		};
	}

	@Override
	public IXViewerCustomizations getXViewerCustomizations() {
		return super.getXViewerCustomizations();
	}

	@Override
	public boolean isSearhTop() {
		return false;
	}

	@Override
	public XViewerColumn getDefaultXViewerColumn(String id) {
		return EventViewerFactory.TIME_COL_SS;
	}

	@Override
	public CustomizeData getDefaultTableCustomizeData() {
		CustomizeData retVal = super.getDefaultTableCustomizeData();
		XViewerColumn col = retVal.getColumnData().getXColumn(EventViewerFactory.TIME_COL_SS.getId());
		col.setSortForward(false);
		retVal.getSortingData().addSortingName(EventViewerFactory.TIME_COL_SS.getId());
		return retVal;
	}

}
