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


/**
 * 
 */
public class BulkIOSnapshotSettings {
	/** number of samples to take/capture. */
	private double samples = 1024;
	/** How the samples are to be captured. */
	private CaptureMethod captureMethod = CaptureMethod.NUM_SAMPLES;
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public double getSamples() {
		return samples;
	}

	public void setSamples(double samples) {
		double oldValue = this.samples;
		this.samples = samples;
		pcs.firePropertyChange("samples", oldValue, samples);
	}
	
	public CaptureMethod getCaptureMethod() {
		return captureMethod;
	}
	
	public void setCaptureMethod(CaptureMethod captureMethod) {
		CaptureMethod oldValue = this.captureMethod;
		this.captureMethod = captureMethod;
		pcs.firePropertyChange("captureMethod", oldValue, captureMethod);
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
