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
package gov.redhawk.datalist.ui;

import gov.redhawk.datalist.ui.views.OptionsComposite.CaptureMethod;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * @since 2.0
 */
public class DataCollectionSettings {
	/** number of samples to take */
	private double samples = 1024;
	/** How the samples are to be capture */
	private CaptureMethod captureType = CaptureMethod.NUMBER;
	/** The options of how to capture samples */
	private CaptureMethod[] captureTypes = CaptureMethod.values();
	private int dimensions = 1;

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	public int getDimensions() {
		return dimensions;
	}

	public void setDimensions(int dimensions) {
		int oldValue = this.dimensions;
		this.dimensions = dimensions;
		pcs.firePropertyChange("dimensions", oldValue, dimensions);
	}

	public double getSamples() {
		return samples;
	}

	public void setSamples(double samples) {
		double oldValue = this.samples;
		this.samples = samples;
		pcs.firePropertyChange("samples", oldValue, samples);
	}

	public CaptureMethod getProcessType() {
		return this.captureType;
	}

	public void setProcessType(CaptureMethod method) {
		CaptureMethod oldValue = this.captureType;
		this.captureType = method;
		pcs.firePropertyChange("processType", oldValue, captureType);
	}

	public CaptureMethod[] getProcessingTypes() {
		return captureTypes;
	}

	public void setProcessingTypes(CaptureMethod[] processingTypes) {
		CaptureMethod[] oldValue = this.captureTypes;
		this.captureTypes = processingTypes;
		setProcessType(this.captureTypes[0]);
		pcs.firePropertyChange("processingTypes", oldValue, processingTypes);
	}
}
