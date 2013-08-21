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

import gov.redhawk.ide.snapshot.writer.IDataWriterDesc;

import java.io.File;

import org.eclipse.core.resources.IContainer;

public class SnapshotSettings {
	/** The fileName of the output file relative to the workspace or the absolute path. */
	private File destinationFile;
	/** relative file path form the workspace, only to be used if saveToWorkspace=true. */
	private String path;
	/**
	 * 
	 */
	private IContainer container;
	/** whether or not to save to the workspace. */
	private boolean saveToWorkspace = false;
	/** The type of file to save to port data snapshot to. */
	private IDataWriterDesc dataWriter;
	/** Confirm with user if overwrite existing file(s). */
	private boolean confirmOverwrite = true;
	

	public File getDestinationFile() {
		return destinationFile;
	}
	
	public void setDestinationFile(File destinationFile) {
		this.destinationFile = destinationFile;
	}

	public String getFileName() {
		if (destinationFile != null) {
			return destinationFile.getAbsolutePath();
		} else {
			return "";
		}
	}

	public void setFileName(String fileName) {
		setDestinationFile(new File(fileName));
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public IContainer getContainer() {
		return container;
	}
	
	public void setContainer(IContainer container) {
		this.container = container;
	}

	public boolean isSaveToWorkspace() {
		return saveToWorkspace;
	}

	public void setSaveToWorkspace(boolean saveToWorkspace) {
		this.saveToWorkspace = saveToWorkspace;
	}

	public boolean isConfirmOverwrite() {
		return confirmOverwrite;
	}

	public void setConfirmOverwrite(boolean confirmOverwrite) {
		this.confirmOverwrite = confirmOverwrite;
	}
	
	public IDataWriterDesc getDataWriter() {
		return dataWriter;
	}
	
	public void setDataWriter(IDataWriterDesc dataWriter) {
		this.dataWriter = dataWriter;
	}

}
