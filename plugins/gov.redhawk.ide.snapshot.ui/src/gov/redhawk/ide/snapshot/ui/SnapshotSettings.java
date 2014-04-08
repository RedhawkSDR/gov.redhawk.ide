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
import gov.redhawk.sca.util.PropertyChangeSupport;

import java.beans.PropertyChangeListener;
import java.io.File;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;

public class SnapshotSettings {
	/** The fileName of the output file relative to the workspace or the absolute path. */
	private String fileName;
	/** relative file path form the workspace, only to be used if saveToWorkspace=true. */
	private String path;
	/**
	 * 
	 */
	private IResource resource;
	/** whether or not to save to the workspace. */
	private boolean saveToWorkspace = false;
	/** The type of file to save to port data snapshot to. */
	private IDataWriterDesc dataWriter;
	/** Confirm with user if overwrite existing file(s). */
	private boolean confirmOverwrite = true;

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

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

	public File getDestinationFile() {
		return new File(this.fileName);
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		String oldValue = this.fileName;
		this.fileName = fileName;
		pcs.firePropertyChange("fileName", oldValue, fileName);
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		String oldValue = this.path;
		this.path = path;
		pcs.firePropertyChange("path", oldValue, path);
	}

	public IResource getResource() {
		return resource;
	}

	public void setResource(IResource resource) {
		IResource oldValue = this.resource;
		this.resource = resource;
		pcs.firePropertyChange("resource", oldValue, resource);
		if (resource instanceof IFile) {
			setPath(resource.getName());
		}
	}

	public boolean isSaveToWorkspace() {
		return saveToWorkspace;
	}

	public void setSaveToWorkspace(boolean saveToWorkspace) {
		boolean oldValue = this.saveToWorkspace;
		this.saveToWorkspace = saveToWorkspace;
		pcs.firePropertyChange("saveToWorkspace", oldValue, saveToWorkspace);
	}

	public boolean isConfirmOverwrite() {
		return confirmOverwrite;
	}

	public void setConfirmOverwrite(boolean confirmOverwrite) {
		boolean oldValue = this.confirmOverwrite;
		this.confirmOverwrite = confirmOverwrite;
		pcs.firePropertyChange("confirmOverwrite", oldValue, confirmOverwrite);
	}

	public IDataWriterDesc getDataWriter() {
		return dataWriter;
	}

	public void setDataWriter(IDataWriterDesc dataWriter) {
		IDataWriterDesc oldValue = this.dataWriter;
		this.dataWriter = dataWriter;
		pcs.firePropertyChange("dataWriter", oldValue, dataWriter);
	}

	public IFile getIFile() {
		if (resource instanceof IFile) {
			return (IFile) resource;
		} else {
			return ((IContainer) resource).getFile(new Path(path));
		}
	}

}
