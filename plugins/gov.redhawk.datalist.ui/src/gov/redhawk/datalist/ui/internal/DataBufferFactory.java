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
package gov.redhawk.datalist.ui.internal;

import gov.redhawk.datalist.ui.internal.ports.DataBufferChar;
import gov.redhawk.datalist.ui.internal.ports.DataBufferDouble;
import gov.redhawk.datalist.ui.internal.ports.DataBufferFloat;
import gov.redhawk.datalist.ui.internal.ports.DataBufferLong;
import gov.redhawk.datalist.ui.internal.ports.DataBufferLongLong;
import gov.redhawk.datalist.ui.internal.ports.DataBufferOctet;
import gov.redhawk.datalist.ui.internal.ports.DataBufferShort;
import gov.redhawk.datalist.ui.internal.ports.DataBufferULong;
import gov.redhawk.datalist.ui.internal.ports.DataBufferULongLong;
import gov.redhawk.datalist.ui.internal.ports.DataBufferUShort;
import gov.redhawk.model.sca.ScaUsesPort;
import BULKIO.dataCharHelper;
import BULKIO.dataDoubleHelper;
import BULKIO.dataFloatHelper;
import BULKIO.dataLongHelper;
import BULKIO.dataLongLongHelper;
import BULKIO.dataOctetHelper;
import BULKIO.dataShortHelper;
import BULKIO.dataUlongHelper;
import BULKIO.dataUlongLongHelper;
import BULKIO.dataUshortHelper;

public class DataBufferFactory {
	protected DataBufferFactory() {
		
	}
	public static DataBuffer createBuffer(final ScaUsesPort port) throws Exception {
		DataBuffer retVal = null;
		final String repID = port.getRepid();
		if (dataCharHelper.id().equals(repID)) {
			retVal = new DataBufferChar();
		} else if (dataDoubleHelper.id().equals(repID)) {
			retVal = new DataBufferDouble();
		} else if (dataFloatHelper.id().equals(repID)) {
			retVal = new DataBufferFloat();
		} else if (dataLongHelper.id().equals(repID)) {
			retVal = new DataBufferLong();
		} else if (dataLongLongHelper.id().equals(repID)) {
			retVal = new DataBufferLongLong();
		} else if (dataOctetHelper.id().equals(repID)) {
			retVal = new DataBufferOctet();
		} else if (dataShortHelper.id().equals(repID)) {
			retVal = new DataBufferShort();
		} else if (dataUlongHelper.id().equals(repID)) {
			retVal = new DataBufferULong();
		} else if (dataUlongLongHelper.id().equals(repID)) {
			retVal = new DataBufferULongLong();
		} else if (dataUshortHelper.id().equals(repID)) {
			retVal = new DataBufferUShort();
		} else {
			throw new Exception(port.getRepid() + " is an unsupported type.");
		}
		retVal.setPort(port);
		return retVal;
	}
}
