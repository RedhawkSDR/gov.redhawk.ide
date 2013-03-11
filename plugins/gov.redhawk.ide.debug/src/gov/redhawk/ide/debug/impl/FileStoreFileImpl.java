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
package gov.redhawk.ide.debug.impl;

import java.io.InputStream;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;

import CF.ErrorNumberType;
import CF.FileException;
import CF.FilePOA;
import CF.OctetSequenceHolder;
import CF.FilePackage.IOException;
import CF.FilePackage.InvalidFilePointer;

/**
 * 
 */
public class FileStoreFileImpl extends FilePOA {

	private final IFileStore store;
	private final InputStream inputStream;
	private int pointer;

	public FileStoreFileImpl(final IFileStore store) throws CoreException {
		this.store = store;
		this.inputStream = store.openInputStream(0, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public String fileName() {
		return this.store.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	public int filePointer() {
		return this.pointer;
	}

	/**
	 * {@inheritDoc}
	 */
	public void read(final OctetSequenceHolder data, final int length) throws IOException {
		final byte[] buffer = new byte[length];
		try {
			final int read = this.inputStream.read(buffer);
			if (read == -1) {
				data.value = new byte[0];
			} else if (read != length) {
				final byte[] subBuffer = new byte[read];
				System.arraycopy(buffer, 0, subBuffer, 0, subBuffer.length);
				data.value = subBuffer;
			} else {
				data.value = buffer;
			}
			this.pointer += length;
		} catch (final java.io.IOException e) {
			throw new IOException(ErrorNumberType.CF_EIO, e.getMessage());
		}

	}

	/**
	 * {@inheritDoc}
	 */
	public void write(final byte[] data) throws IOException {
		throw new IOException(ErrorNumberType.CF_ENOTSUP, "Does not support write operations");
	}

	/**
	 * {@inheritDoc}
	 */
	public int sizeOf() throws FileException {
		return (int) this.store.fetchInfo().getLength();
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() throws FileException {
		try {
			this.inputStream.close();
			this.pointer = -1;
		} catch (final java.io.IOException e) {
			throw new FileException(ErrorNumberType.CF_EIO, e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setFilePointer(final int filePointer) throws InvalidFilePointer, FileException {
		try {
			this.inputStream.skip(filePointer);
			this.pointer += filePointer;
		} catch (final java.io.IOException e) {
			throw new FileException(ErrorNumberType.CF_EIO, e.getMessage());
		}

	}

}
