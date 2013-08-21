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

import org.eclipse.jdt.annotation.NonNull;

public enum CaptureMethod {
	/**
	 * to process a certain number of samples,
	 */
	NUM_SAMPLES("Number of Samples"),
	/**
	 * INDEFINATELY to process till an end of stream occurs,
	 */
	INDEFINITELY("Indefinitely"),
	/**
	 * to process for a certain amount of time in real time,
	 */
	CLOCK_TIME("Clock Time"), 
	/**
	 * to process for a certain amount of time in sample time
	 */
	SAMPLE_TIME("Sample Time");

	/** the description of the enum, outputted by toString()*/
	private String description;

	private CaptureMethod(@NonNull String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return description;
	}

} // end enum CaptureMethod