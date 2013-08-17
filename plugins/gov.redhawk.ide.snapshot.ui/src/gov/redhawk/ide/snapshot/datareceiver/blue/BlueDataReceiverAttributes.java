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
package gov.redhawk.ide.snapshot.datareceiver.blue;

import java.io.File;
import java.io.IOException;

import gov.redhawk.bulkio.util.BulkIOType;
import gov.redhawk.ide.snapshot.datareceiver.AbstractDataReceiverAttributes;
import gov.redhawk.ide.snapshot.datareceiver.IDataReceiver;
import gov.redhawk.ide.snapshot.datareceiver.IDataReceiver.CaptureMethod;

//TODO: DELETE ME
@Deprecated
public class BlueDataReceiverAttributes extends AbstractDataReceiverAttributes {

	private final String receiverName = "Midas BLUE file (.tmp)";
	private final String[] receiverExtensions = { ".tmp" };

	@Override
	public String getReceiverName() {
		return receiverName;
	}

	@Override
	public String[] getReceiverFilenameExtensions() {
		return receiverExtensions;
	}

	@Override
	public IDataReceiver newInstance(File file, long samples, double time, BulkIOType type, boolean upcastUnsigned, IDataReceiver.CaptureMethod method)
		throws IOException {
		if (type == BulkIOType.OCTET || type == BulkIOType.ULONG || type == BulkIOType.ULONG_LONG || type == BulkIOType.USHORT) {
			if (method == CaptureMethod.NUMBER || method == CaptureMethod.SAMPLE_TIME) {
				return new UBLUEDataReceiver(file, samples, time, type, method);
			} else {
				return new UBLUEDataReceiverClock(file, time, type, method);
			}
		} else {
			if (method == CaptureMethod.NUMBER || method == CaptureMethod.SAMPLE_TIME) {
				return new BLUEDataReceiver(file, samples, time, type, method);
			} else {
				return new BLUEDataReceiverClock(file, time, type, method);
			}
		}
	}

}
