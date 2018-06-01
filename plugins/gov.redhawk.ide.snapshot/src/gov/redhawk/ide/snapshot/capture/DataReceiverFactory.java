/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.snapshot.capture;

import gov.redhawk.ide.snapshot.internal.capture.CorbaDataReceiver;
import gov.redhawk.ide.snapshot.internal.capture.CorbaNumSamplesReceiver;
import gov.redhawk.ide.snapshot.internal.capture.CorbaSignalTimeReceiver;
import gov.redhawk.ide.snapshot.internal.capture.CorbaSystemTimeReceiver;

public class DataReceiverFactory {

	private DataReceiverFactory() {
	}

	/**
	 * @return A {@link IDataReceiver} that will indefinitely process data.
	 */
	public static IScaPortReceiver createReceiver() {
		return new CorbaDataReceiver();
	}

	/**
	 * @return A {@link IDataReceiver} that will process data for the specified amount of wall-clock time.
	 */
	public static IScaPortReceiver createWallClockTimeReceiver(double timeInSeconds) {
		CorbaSystemTimeReceiver receiver = new CorbaSystemTimeReceiver();
		receiver.setTimeInSeconds(timeInSeconds);
		return receiver;
	}

	/**
	 * @return A {@link IDataReceiver} that will process the specified number of samples.
	 */
	public static IScaPortReceiver createSamplesReceiver(long samples) {
		CorbaNumSamplesReceiver receiver = new CorbaNumSamplesReceiver();
		receiver.setSamples(samples);
		return receiver;
	}

	/**
	 * @return A {@link IDataReceiver} that will process data for the specified amount of time per the data's
	 * timestamps.
	 */
	public static IScaPortReceiver createSampleTimeReceiver(double timeInSeconds) {
		CorbaSignalTimeReceiver receiver = new CorbaSignalTimeReceiver();
		receiver.setTimeInSeconds(timeInSeconds);
		return receiver;
	}

}
