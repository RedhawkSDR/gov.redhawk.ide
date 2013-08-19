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

import org.eclipse.jdt.annotation.NonNull;

public enum CaptureMethod {
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