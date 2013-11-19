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
package gov.redhawk.ide.snapshot.writer;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;

import BULKIO.StreamSRI;

public abstract class BaseDataWriter implements IDataWriter {
	private IDataWriterSettings settings;

	private boolean open;

	private StreamSRI sri;

	@Override
	public void setSettings(IDataWriterSettings settings) {
		this.settings = settings;
	}

	@Override
	public IDataWriterSettings getSettings() {
		return settings;
	}

	protected void setOpen(boolean open) {
		this.open = open;
	}

	@Override
	public boolean isOpen() {
		return this.open;
	}

	@Override
	public void pushSRI(StreamSRI sri) throws IOException {
		this.sri = sri;
	}

	public StreamSRI getSRI() {
		return sri;
	}

	public boolean isUnsignedData() {
		return settings.getType().isUnsigned();
	}

	@Override
	public List<File> getOutputFileList() {
		return Arrays.asList(new File[] { getFileDestination() });
	}

	protected File getFileDestination() {
		Object destination = getSettings().getDestination();
		if (destination instanceof IFile) {
			IFile iFile = (IFile) destination;
			return iFile.getLocation().toFile();
		} else if (destination instanceof File) {
			return (File) destination;
		} else if (destination != null) {
			throw new IllegalStateException("Can not convert destination of type " + destination.getClass() + " to file");
		}
		return null;
	}

}
