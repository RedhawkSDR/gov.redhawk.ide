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
package gov.redhawk.ide.datalist.ui.internal;

import BULKIO.PrecisionUTCTime;

public class Sample {
	private final PrecisionUTCTime time;
	private final int index;
	private final Object data;

	public Sample(final PrecisionUTCTime time, final int index, final Object data) {
		this.time = time;
		this.index = index;
		this.data = data;
	}

	public PrecisionUTCTime getTime() {
		return this.time;
	}

	public int getIndex() {
		return this.index;
	}

	public Object getData() {
		return this.data;
	}
}
