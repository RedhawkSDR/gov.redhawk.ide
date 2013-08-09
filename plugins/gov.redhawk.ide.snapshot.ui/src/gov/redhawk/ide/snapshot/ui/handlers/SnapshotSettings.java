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
package gov.redhawk.ide.snapshot.ui.handlers;

public class SnapshotSettings {
	/**The fileName of the output file relative to the workspace or the absolute path*/
	private String fileName = "";
	/**relative file path form the workspace, only to be used if saveToWorkspace=true*/
	private String filePath = "";
	/**whether or not to save to the workspace*/
	private boolean saveToWorkspace = false;
	/** number of samples to take*/
	private double samples = 1024;
	/** the types supported by the snapshot, index 0 is the default*/
	private String[] supportedTypes = { ".bin", ".BLUE" };
	/**The type of file to save to Blue file, bin*/
	private String fileType = supportedTypes[0];
	/**How the samples are to be capture*/
	private String captureType = "";
	/**The options of how to capture samples*/
	private String[] captureTypes;

	public double getSamples() {
		return samples;
	}

	public void setSamples(double samples) {
		this.samples = samples;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFilePath() {
		return filePath;
	}

	public String[] getSupportedTypes() {
		return supportedTypes;
	}

	public void setSupportedTypes(String[] types) {
		this.supportedTypes = types;
		this.fileType = supportedTypes[0];
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public boolean getSaveToWorkspace() {
		return saveToWorkspace;
	}

	public void setSaveToWorkspace(boolean saveToWorkspace) {
		this.saveToWorkspace = saveToWorkspace;
	}

	public String getCaptureType() {
		return this.captureType;
	}

	public void setCaptureType(String method) {
		this.captureType = method;
	}

	public String[] getCaptureTypes() {
		return captureTypes;
	}

	public void setCaptureTypes(String[] processingTypes) {
		this.captureTypes = processingTypes;
		this.captureType = this.captureTypes[0];
	}
}
