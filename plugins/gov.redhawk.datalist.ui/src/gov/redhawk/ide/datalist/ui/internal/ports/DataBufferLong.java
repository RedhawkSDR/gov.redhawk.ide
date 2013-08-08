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
package gov.redhawk.ide.datalist.ui.internal.ports;

import gov.redhawk.ide.datalist.ui.internal.DataBuffer;

import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import BULKIO.PrecisionUTCTime;
import BULKIO.dataLongOperations;
import BULKIO.dataLongPOATie;

public class DataBufferLong extends DataBuffer implements dataLongOperations {

	@Override
	public org.omg.CORBA.Object createRef(final POA poa) throws ServantNotActive, WrongPolicy {
		return poa.servant_to_reference(new dataLongPOATie(this));
	}

	@Override
	public void pushPacket(final int[] data, final PrecisionUTCTime time, final boolean eos, final String streamID) {
		super.pushPacket(data, time, eos, streamID);
	}

}
