/******************************************************************************
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package gov.redhawk.datalist.ui.internal.ports;

import gov.redhawk.datalist.ui.internal.DataBuffer;

import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import BULKIO.PrecisionUTCTime;
import BULKIO.dataCharOperations;
import BULKIO.dataCharPOATie;

public class DataBufferChar extends DataBuffer implements dataCharOperations {

	@Override
	public void pushPacket(final char[] data, final PrecisionUTCTime time, final boolean eos, final String streamID) {
		super.pushPacket(data, time, eos, streamID);
	}

	@Override
	public org.omg.CORBA.Object createRef(final POA poa) throws ServantNotActive, WrongPolicy {
		return poa.servant_to_reference(new dataCharPOATie(this));
	}
}
