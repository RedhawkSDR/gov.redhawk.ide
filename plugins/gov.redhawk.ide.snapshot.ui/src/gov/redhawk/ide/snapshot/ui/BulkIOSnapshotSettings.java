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
package gov.redhawk.ide.snapshot.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.jdt.annotation.NonNull;


/**
 *
 */
public class BulkIOSnapshotSettings {
	/** @since 1.1 */
	public static final String PROP_CAPTURE_METHOD = "captureMethod";
	/** @since 1.1 */
	public static final String PROP_CONNECTION_ID  = "connectionID";
	/** @since 1.1 */
	public static final String PROP_SAMPLES        = "samples";
	
	/** number of samples to take/capture. */
	private double samples = 1024;
	/** How the samples are to be captured. */
	private CaptureMethod captureMethod = CaptureMethod.NUM_SAMPLES;
	/** custom connection ID to use (when not null). */
	private String connectionID;

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public double getSamples() {
		return samples;
	}

	public void setSamples(double samples) {
		if (this.samples != samples) {
			double oldValue = this.samples;
			this.samples = samples;
			pcs.firePropertyChange(PROP_SAMPLES, oldValue, samples);
		}
	}
	
	public CaptureMethod getCaptureMethod() {
		return captureMethod;
	}
	
	public void setCaptureMethod(@NonNull CaptureMethod captureMethod) {
		if (this.captureMethod != captureMethod) {
			CaptureMethod oldValue = this.captureMethod;
			this.captureMethod = captureMethod;
			pcs.firePropertyChange(PROP_CAPTURE_METHOD, oldValue, captureMethod);
		}
	}

	/**
	 * @since 1.1
	 */
	public String getConnectionID() {
		return connectionID;
	}

	/**
	 * @since 1.1
	 */
	public void setConnectionID(String connectionID) {
		if ((this.connectionID == null && connectionID != null) || !this.connectionID.equals(connectionID)) {
			String oldValue = this.connectionID;
			this.connectionID = connectionID;
			pcs.firePropertyChange(PROP_CONNECTION_ID, oldValue, connectionID);
		}
	}

	/**
	 * @param listener
	 * @see java.beans.PropertyChangeSupport#addPropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	/**
	 * @param listener
	 * @see java.beans.PropertyChangeSupport#removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

}
