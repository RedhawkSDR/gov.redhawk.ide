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
package gov.redhawk.ide.snapshot.writer.internal;

import gov.redhawk.ide.snapshot.writer.DefaultDataWriterSettings;

import java.nio.ByteOrder;

/**
 * 
 */
public class BinDataWriterSettings extends DefaultDataWriterSettings {
	static final ByteOrder DEFAULT_BYTE_ORDER = ByteOrder.nativeOrder(); // default to machine's native byte-order for best performance
	private ByteOrder byteOrder = DEFAULT_BYTE_ORDER; 

	public ByteOrder getByteOrder() {
		return byteOrder;
	}

	public void setByteOrder(ByteOrder byteOrder) {
		this.byteOrder = byteOrder;
	}
}
