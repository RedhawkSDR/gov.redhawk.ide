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

public class DataCollectionSettings {
	/** number of samples to take */
	private double samples = 1024;
	/** How the samples are to be capture */
	private CaptureMethod captureType = CaptureMethod.NUMBER;
	/** The options of how to capture samples */
	private CaptureMethod[] captureTypes = CaptureMethod.values();
	private int dimensions = 1;

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	/**
	 * @since 2.1
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	/**
	 * @since 2.1
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	/**
	 * @since 1.1
	 */
	public int getDimensions() {
		return dimensions;
	}

	/**
	 * @since 1.1
	 */
	public void setDimensions(int dimensions) {
		int oldValue = this.dimensions;
		this.dimensions = dimensions;
		pcs.firePropertyChange("dimensions", oldValue, dimensions);
	}

	/**
	 * @since 1.1
	 */
	public double getSamples() {
		return samples;
	}

	/**
	 * @since 1.1
	 */
	public void setSamples(double samples) {
		double oldValue = this.samples;
		this.samples = samples;
		pcs.firePropertyChange("samples", oldValue, samples);
	}

	/**
	 * @since 2.1
	 */
	public CaptureMethod getProcessType() {
		return this.captureType;
	}

	/**
	 * @since 2.1
	 */
	public void setProcessType(CaptureMethod method) {
		CaptureMethod oldValue = this.captureType;
		this.captureType = method;
		pcs.firePropertyChange("processType", oldValue, captureType);
	}

	/**
	 * @since 2.1
	 */
	public CaptureMethod[] getProcessingTypes() {
		return captureTypes;
	}

	/**
	 * @since 2.1
	 */
	public void setProcessingTypes(CaptureMethod[] processingTypes) {
		CaptureMethod[] oldValue = this.captureTypes;
		this.captureTypes = processingTypes;
		setProcessType(this.captureTypes[0]);
		pcs.firePropertyChange("processingTypes", oldValue, processingTypes);
	}
}
