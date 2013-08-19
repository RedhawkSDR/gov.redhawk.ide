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
package gov.redhawk.ide.snapshot.datareceiver;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import BULKIO.StreamSRI;
import BULKIO.updateSRIOperations;

public interface IDataReceiver extends updateSRIOperations {

	/**
	 * @return files written to by the data receiver
	 */
	@NonNull public List<FilePair> getOutpuFileList();

	/**
	 * closes the receiver
	 * @throws IOException
	 */
	public void dispose() throws IOException;

	/**
	 * 
	 * @param monitor progress monitor
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public void processSamples(IProgressMonitor monitor) throws InterruptedException, IOException;

	//TODO add function to process samples indefinitely

	/**
	 * This function takes in an array of Object Data, determines which primitive data type the
	 * array corresponds to, and then writes the data along with the sri.
	 * @param data : data to be written to the file (Integer[],Double[],...)
	 * @param sri : The sri data of the data
	 * @throws IOException
	 */
	public void writeFile(Object[] data, StreamSRI sri) throws IOException;
	
	/** Immutable class to hold snapshot filename and an option metadata filename. */
	public static class FilePair {
		private final String file;
		private final String metadataFile;
		private final transient int count;
		public FilePair(@NonNull String file, @Nullable String metadataFile) {
			super();
			this.file = file;
			this.metadataFile = metadataFile;
			if (metadataFile != null) {
				count = 2;
			} else {
				count = 1;
			}
		}
		/**
		 * @return the file
		 */
		public String getFile() {
			return file;
		}
		/**
		 * @return the metadataFile
		 */
		public String getMetadataFile() {
			return metadataFile;
		}
		/** number of file, 2 if have metadata file, otherwise 1). */
		public int count() {
			return count;
		}
	}

}
