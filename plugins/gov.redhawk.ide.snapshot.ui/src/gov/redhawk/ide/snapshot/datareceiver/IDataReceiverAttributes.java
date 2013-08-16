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

import gov.redhawk.bulkio.util.BulkIOType;

import java.io.File;
import java.io.IOException;

public interface IDataReceiverAttributes {

	/**
	 * @return the printable name of the file type this receiver prints to
	 */
	public abstract String getReceiverName();

	/**
	 * The first index is main filename extension, the subsequent extensions are for metadata filename extensions. 
	 * @return the filename extensions (should be prepended with a dot) this data receiver uses.
	 */
	public abstract String[] getReceiverFilenameExtensions();

	/**
	 * 
	 * @param file : The file to start saving with
	 * @param samples : The number of samples to save (used by NUMBER)
	 * @param time : The duration of the sample capture (used by SAMPLE_TIME or CAPTURE_TIME)
	 * @param type : The BulkIOType of the port
	 * @param upcastUnsigned : whether or not unsigned data should be upcast
	 * @param method : method used to process samples, 
	 * 			NUMBER to process a certain number of samples,
	 * 			INDEFINATELY to process till an end of stream occurs,
	 * 			CLOCK_TIME to process for a certain amount of time in real time,
	 * 			SAMPLE_TIME to process for a certain amount of time in sample time
	 * @return an IDataReceiver
	 * @throws IOException
	 */
	public abstract IDataReceiver newInstance(File file, long samples, double time, BulkIOType type, boolean upcastUnsigned, IDataReceiver.CaptureMethod method)
		throws IOException;

}
