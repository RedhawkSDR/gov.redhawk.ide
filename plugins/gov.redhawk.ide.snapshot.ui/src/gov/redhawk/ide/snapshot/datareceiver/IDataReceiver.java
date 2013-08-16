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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.annotation.NonNull;

import BULKIO.StreamSRI;
import BULKIO.updateSRIOperations;

public interface IDataReceiver extends updateSRIOperations {

	public static enum CaptureMethod {
		NUMBER("Number of Samples"), INDEFINITELY("Indefinitely"), CLOCK_TIME("Clock Time"), SAMPLE_TIME("Sample Time");

		/** the description of the enum, outputted by toString()*/
		private String description;

		private CaptureMethod(@NonNull String description) {
			this.description = description;
		}

		@Override
		public String toString() {
			return description;
		}

		/**
		 * @param arg0 the name or the description of the enum to be returned
		 * @return a CaptureMethod corresponding to the name or description provided
		 */
		public static CaptureMethod stringToValue(String arg0) {
			if (arg0.equals(NUMBER.name()) || arg0.equals(NUMBER.toString())) {
				return NUMBER;
			} else if (arg0.equals(INDEFINITELY.name()) || arg0.equals(INDEFINITELY.toString())) {
				return INDEFINITELY;
			} else if (arg0.equals(CLOCK_TIME.name()) || arg0.equals(CLOCK_TIME.toString())) {
				return CLOCK_TIME;
			} else if (arg0.equals(SAMPLE_TIME.name()) || arg0.equals(SAMPLE_TIME.toString())) {
				return SAMPLE_TIME;
			} else {
				return CaptureMethod.valueOf(arg0);
			}
		}
	} // end enum CaptureMethod

	/**
	 * 
	 * @return the files written to by the data receiver
	 */
	public String[][] getOutputFiles();

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

}
