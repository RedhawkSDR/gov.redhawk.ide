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

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import gov.redhawk.ide.internal.ui.event.model.Event;
import mil.jpeojtrs.sca.util.AnyUtils;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.swt.graphics.Image;

import CF.DataType;
import ExtendedEvent.PropertySetChangeEventType;
import ExtendedEvent.PropertySetChangeEventTypeHelper;
import ExtendedEvent.ResourceStateChangeEventType;
import ExtendedEvent.ResourceStateChangeEventTypeHelper;
import ExtendedEvent.ResourceStateChangeType;
import StandardEvent.DomainManagementObjectAddedEventType;
import StandardEvent.DomainManagementObjectAddedEventTypeHelper;
import StandardEvent.DomainManagementObjectRemovedEventType;
import StandardEvent.DomainManagementObjectRemovedEventTypeHelper;
import StandardEvent.SourceCategoryType;
import StandardEvent.StateChangeCategoryType;
import StandardEvent.StateChangeEventType;
import StandardEvent.StateChangeEventTypeHelper;
import StandardEvent.StateChangeType;

/**
 * 
 */
public class EventViewerLabelProvider extends XViewerLabelProvider {
	
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm::SSSS");

	/**
	 * @param viewer
	 */
	public EventViewerLabelProvider(XViewer viewer) {
		super(viewer);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		// do nothing
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider#getColumnImage(java.lang.Object, org.eclipse.nebula.widgets.xviewer.XViewerColumn, int)
	 */
	@Override
	public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) throws Exception {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider#getColumnText(java.lang.Object, org.eclipse.nebula.widgets.xviewer.XViewerColumn, int)
	 */
	@Override
	public String getColumnText(Object element, XViewerColumn xCol, int columnIndex) throws Exception {
		if (element instanceof Event) {
			Event event = (Event) element;
			if (xCol.equals(EventViewerFactory.CHANNEL_COL)) {
				return event.getChannel();
			} else if (xCol.equals(EventViewerFactory.PRODUCER_ID_COL)) {
				if (event.valueIsType(DomainManagementObjectAddedEventTypeHelper.type())) {
					DomainManagementObjectAddedEventType value = DomainManagementObjectAddedEventTypeHelper.extract(event.getValue());
					return value.producerId;
				} else if (event.valueIsType(DomainManagementObjectRemovedEventTypeHelper.type())) {
					DomainManagementObjectRemovedEventType value = DomainManagementObjectRemovedEventTypeHelper.extract(event.getValue());
					return value.producerId;
				} else if (event.valueIsType(StateChangeEventTypeHelper.type())) {
					StateChangeEventType value = StateChangeEventTypeHelper.extract(event.getValue());
					return value.producerId;
				} else {
					return "";
				}
			} else if (xCol.equals(EventViewerFactory.PROPERTIES_COL)) {
				if (event.valueIsType(PropertySetChangeEventTypeHelper.type())) {
					PropertySetChangeEventType value = PropertySetChangeEventTypeHelper.extract(event.getValue());
					return toString(value.properties);
				} else {
					return "";
				}
			} else if (xCol.equals(EventViewerFactory.SOURCE_CATEGORY_COL)) {
				if (event.valueIsType(DomainManagementObjectAddedEventTypeHelper.type())) {
					DomainManagementObjectAddedEventType value = DomainManagementObjectAddedEventTypeHelper.extract(event.getValue());
					return toString(value.sourceCategory);
				} else if (event.valueIsType(DomainManagementObjectRemovedEventTypeHelper.type())) {
					DomainManagementObjectRemovedEventType value = DomainManagementObjectRemovedEventTypeHelper.extract(event.getValue());
					return toString(value.sourceCategory);
				} else {
					return "";
				}
			} else if (xCol.equals(EventViewerFactory.SOURCE_ID_COL)) {
				if (event.valueIsType(DomainManagementObjectAddedEventTypeHelper.type())) {
					DomainManagementObjectAddedEventType value = DomainManagementObjectAddedEventTypeHelper.extract(event.getValue());
					return value.sourceId;
				} else if (event.valueIsType(DomainManagementObjectRemovedEventTypeHelper.type())) {
					DomainManagementObjectRemovedEventType value = DomainManagementObjectRemovedEventTypeHelper.extract(event.getValue());
					return value.sourceId;
				} else if (event.valueIsType(StateChangeEventTypeHelper.type())) {
					StateChangeEventType value = StateChangeEventTypeHelper.extract(event.getValue());
					return value.sourceId;
				} else if (event.valueIsType(PropertySetChangeEventTypeHelper.type())) {
					PropertySetChangeEventType value = PropertySetChangeEventTypeHelper.extract(event.getValue());
					return value.sourceId;
				} else if (event.valueIsType(ResourceStateChangeEventTypeHelper.type())) {
					ResourceStateChangeEventType value = ResourceStateChangeEventTypeHelper.extract(event.getValue());
					return value.sourceId;
				} else {
					return "";
				}
			} else if (xCol.equals(EventViewerFactory.SOURCE_IOR_COL)) {
				if (event.valueIsType(DomainManagementObjectAddedEventTypeHelper.type())) {
					DomainManagementObjectAddedEventType value = DomainManagementObjectAddedEventTypeHelper.extract(event.getValue());
					return String.valueOf(value.sourceIOR);
				} else {
					return "";
				}
			} else if (xCol.equals(EventViewerFactory.SOURCE_NAME_COL)) {
				if (event.valueIsType(DomainManagementObjectAddedEventTypeHelper.type())) {
					DomainManagementObjectAddedEventType value = DomainManagementObjectAddedEventTypeHelper.extract(event.getValue());
					return value.sourceName;
				} else if (event.valueIsType(DomainManagementObjectRemovedEventTypeHelper.type())) {
					DomainManagementObjectRemovedEventType value = DomainManagementObjectRemovedEventTypeHelper.extract(event.getValue());
					return value.sourceName;
				} else if (event.valueIsType(PropertySetChangeEventTypeHelper.type())) {
					PropertySetChangeEventType value = PropertySetChangeEventTypeHelper.extract(event.getValue());
					return value.sourceName;
				} else if (event.valueIsType(ResourceStateChangeEventTypeHelper.type())) {
					ResourceStateChangeEventType value = ResourceStateChangeEventTypeHelper.extract(event.getValue());
					return value.sourceName;
				} else {
					return "";
				}
			} else if (xCol.equals(EventViewerFactory.STATE_CHANGE_CATEGORY_COL)) {
				if (event.valueIsType(StateChangeEventTypeHelper.type())) {
					StateChangeEventType value = StateChangeEventTypeHelper.extract(event.getValue());
					return toString(value.stateChangeCategory);
				} else {
					return "";
				}
			} else if (xCol.equals(EventViewerFactory.STATE_FROM_TYPE_COL)) {
				if (event.valueIsType(StateChangeEventTypeHelper.type())) {
					StateChangeEventType value = StateChangeEventTypeHelper.extract(event.getValue());
					return toString(value.stateChangeFrom);
				} else if (event.valueIsType(ResourceStateChangeEventTypeHelper.type())) {
					ResourceStateChangeEventType value = ResourceStateChangeEventTypeHelper.extract(event.getValue());
					return toString(value.stateChangeFrom);
				} else {
					return "";
				}
			} else if (xCol.equals(EventViewerFactory.STATE_TO_TYPE_COL)) {
				if (event.valueIsType(StateChangeEventTypeHelper.type())) {
					StateChangeEventType value = StateChangeEventTypeHelper.extract(event.getValue());
					return toString(value.stateChangeTo);
				} else if (event.valueIsType(ResourceStateChangeEventTypeHelper.type())) {
					ResourceStateChangeEventType value = ResourceStateChangeEventTypeHelper.extract(event.getValue());
					return toString(value.stateChangeTo);
				} else {
					return "";
				}
			} else if (xCol.equals(EventViewerFactory.TIME_COL)) {
				return DATE_FORMAT.format(event.getTimestamp());
			} else if (xCol.equals(EventViewerFactory.TYPE_COL)) {
				if (event.valueIsType(DomainManagementObjectAddedEventTypeHelper.type())) {
					return DomainManagementObjectAddedEventTypeHelper.id();
				} else if (event.valueIsType(DomainManagementObjectRemovedEventTypeHelper.type())) {
					return DomainManagementObjectRemovedEventTypeHelper.id();
				} else if (event.valueIsType(StateChangeEventTypeHelper.type())) {
					return StateChangeEventTypeHelper.id();
				} else if (event.valueIsType(PropertySetChangeEventTypeHelper.type())) {
					return PropertySetChangeEventTypeHelper.id();
				} else if (event.valueIsType(ResourceStateChangeEventTypeHelper.type())) {
					return ResourceStateChangeEventTypeHelper.id();
				} else {
					return "UNKNOWN";
				}
			}
		}
		return "";
	}

	private String toString(ResourceStateChangeType stateChangeFrom) {
		switch(stateChangeFrom.value()) {
		case ResourceStateChangeType._STARTED:
			return "STARTED";
		case ResourceStateChangeType._STOPPED:
			return "STOPPED";
		default:
			return "";
		}
	}

	private String toString(StateChangeType stateChangeFrom) {
		switch(stateChangeFrom.value()) {
		case StateChangeType._ACTIVE:
			return "ACTIVE";
		case StateChangeType._BUSY:
			return "BUSY";
		case StateChangeType._DISABLED:
			return "DISABLED";
		case StateChangeType._ENABLED:
			return "ENABLED";
		case StateChangeType._IDLE:
			return "IDLE";
		case StateChangeType._LOCKED:
			return "LOCKED";
		case StateChangeType._SHUTTING_DOWN:
			return "SHUTTING_DOWN";
		case StateChangeType._UNLOCKED:
			return "UNLOCKED";
		default:
			return "";
		}
	}

	private String toString(StateChangeCategoryType stateChangeCategory) {
		switch(stateChangeCategory.value()) {
		case StateChangeCategoryType._ADMINISTRATIVE_STATE_EVENT:
			return "ADMINISTRATIVE_STATE_EVENT"; 
		case StateChangeCategoryType._OPERATIONAL_STATE_EVENT:
			return "OPERATIONAL_STATE_EVENT";
		case StateChangeCategoryType._USAGE_STATE_EVENT:
			return "USAGE_STATE_EVENT";
		default:
			return "";
		}
	}

	private String toString(SourceCategoryType sourceCategory) {
		switch(sourceCategory.value()) {
		case SourceCategoryType._APPLICATION:
			return "APPLICATION";
		case SourceCategoryType._APPLICATION_FACTORY:
			return "APPLICATION_FACTORY";
		case SourceCategoryType._DEVICE:
			return "DEVICE";
		case SourceCategoryType._DEVICE_MANAGER:
			return "DEVICE_MANAGER";
		case SourceCategoryType._SERVICE:
			return "SERVICE";
		default:
			return "";
		}
	}

	private String toString(DataType[] properties) {
		StringBuilder retVal = new StringBuilder();
		for (DataType t : properties) {
			retVal.append(t.id);
			retVal.append(" = ");
			Object value = AnyUtils.convertAny(t.value);
			if (value instanceof DataType[]) {
				retVal.append("{\n");
				retVal.append(toString((DataType[]) value));
				retVal.append("}");
			} else if (value.getClass().isArray()) {
				retVal.append(ArrayUtils.toString(value));
			} else {
				retVal.append(value);
			}
			retVal.append("\n");
		}
		return retVal.toString();
	}

}
