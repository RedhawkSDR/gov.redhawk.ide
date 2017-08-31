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
package gov.redhawk.ide.graphiti.dcd.ui.project.wizards;

import gov.redhawk.ide.preferences.RedhawkIdePreferenceConstants;
import gov.redhawk.sca.util.PropertyChangeSupport;

import java.beans.PropertyChangeListener;

/**
 * @since 1.1
 */
public class DeviceProjectSettings {
	private String deviceType = RedhawkIdePreferenceConstants.DEVICE;
	private boolean aggregate = false;
	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

	/**
	 * @return the aggregate
	 */
	public String getDeviceType() {
		return deviceType;
	}

	/**
	 * @return the aggregate
	 */
	public boolean isAggregate() {
		return aggregate;
	}

	/**
	 * @param aggregate the aggregate to set
	 */
	public void setAggregate(boolean aggregate) {
		final boolean oldValue = this.aggregate;
		this.aggregate = aggregate;
		firePropertyChange("aggregate", oldValue, this.aggregate);
	}

	/**
	 * @param deviceType the deviceType to set
	 */
	public void setDeviceType(String deviceType) {
		final String oldValue = this.deviceType;
		this.deviceType = deviceType;
		firePropertyChange("deviceType", oldValue, this.deviceType);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(propertyName, listener);
	}

	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		changeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}
}
