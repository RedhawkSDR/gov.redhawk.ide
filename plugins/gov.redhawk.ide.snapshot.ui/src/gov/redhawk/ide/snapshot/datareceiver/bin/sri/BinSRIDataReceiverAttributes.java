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
package gov.redhawk.ide.snapshot.datareceiver.bin.sri;

import java.io.File;
import java.io.IOException;

import gov.redhawk.bulkio.util.BulkIOType;
import gov.redhawk.ide.snapshot.datareceiver.AbstractDataReceiverAttributes;
import gov.redhawk.ide.snapshot.datareceiver.CaptureMethod;
import gov.redhawk.ide.snapshot.datareceiver.IDataReceiver;

//TODO: DELETE ME
@Deprecated
public final class BinSRIDataReceiverAttributes extends AbstractDataReceiverAttributes {
	private final String receiverName = "Binary files (.bin & .sri)";
	private final String[] recevierExtensions = { ".bin", ".sri" };

	@Override
	public String getReceiverName() {
		return receiverName;
	}

	@Override
	public String[] getReceiverFilenameExtensions() {
		return recevierExtensions;
	}

	@Override
	public IDataReceiver newInstance(File file, long samples, double time, BulkIOType type, boolean upcastUnsigned, CaptureMethod method)
		throws IOException {
		if (!upcastUnsigned) {
			if (method == CaptureMethod.NUMBER || method == CaptureMethod.SAMPLE_TIME) {
				return new DataReceiver(file, samples, time, type, method);
			} else {
				return new BinDataReceiverClock(file, time, type, method);
			}
		} else {
			if (method == CaptureMethod.NUMBER || method == CaptureMethod.SAMPLE_TIME) {
				return new UBinDataReceiver(file, samples, time, type, method);
			} else {
				return new UBinDataReceiverClock(file, time, type, method);
			}
		}
	}

}
