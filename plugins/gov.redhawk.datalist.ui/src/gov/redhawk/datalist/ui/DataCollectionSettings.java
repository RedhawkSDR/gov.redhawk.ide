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


/**
 * @since 1.1
 */
public class DataCollectionSettings {
	/** number of samples to take*/
    private double samples = 1024;
    /**How the samples are to be capture*/
    private String captureType = "";
    /**The options of how to capture samples*/
    private String [] captureTypes;
    private int dimensions = 1;

    public int getDimensions() {
    	return dimensions;
    }
    
    public void setDimensions(int dimensions) {
    	this.dimensions = dimensions;
    }
    
    public double getSamples() {
        return samples;
    }

    public void setSamples(double samples) {
        this.samples = samples;
    }
    
    public String getProcessType() {
    	return this.captureType;
    }
    
    public void setProcessType(String method) {
    	this.captureType = method;
    }
    
    public String [] getProcessingTypes() {
    	return captureTypes;
    }
    public void setProcessingTypes(String [] processingTypes) {
    	this.captureTypes = processingTypes;
    	this.captureType = this.captureTypes[0];
    }
}
