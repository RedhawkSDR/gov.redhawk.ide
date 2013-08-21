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
package gov.redhawk.ide.snapshot.capture;

import gov.redhawk.ide.snapshot.ui.SnapshotActivator;
import gov.redhawk.ide.snapshot.writer.IDataWriter;

import java.io.IOException;
import java.lang.reflect.Array;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import BULKIO.PrecisionUTCTime;
import BULKIO.StreamSRI;

/**
 * 
 */
public class RawDataReceiver implements IDataReceiver {

	private IDataWriter writer;
	private Object array;
	private PrecisionUTCTime time;
	private StreamSRI sri;

	public RawDataReceiver(Object array, PrecisionUTCTime time, StreamSRI sri) {
		this.array = array;
		this.time = time;
		this.sri = sri;
	}

	/* (non-Javadoc)
	 * @see gov.redhawk.ide.snapshot.capture.IDataReceiver#setDataWriter(gov.redhawk.ide.snapshot.writer.IDataWriter)
	 */
	@Override
	public void setDataWriter(IDataWriter writer) {
		this.writer = writer;
	}

	/* (non-Javadoc)
	 * @see gov.redhawk.ide.snapshot.capture.IDataReceiver#getDataWriter()
	 */
	@Override
	public IDataWriter getDataWriter() {
		return this.writer;
	}

	/* (non-Javadoc)
	 * @see gov.redhawk.ide.snapshot.capture.IDataReceiver#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IStatus run(IProgressMonitor monitor) {
		try {
			writer.pushSRI(sri);
			writer.open();
			try {
				if (array instanceof byte[]) {
					writer.pushPacket((byte[]) array, 0, Array.getLength(array), time);
				} else if (array instanceof float[]) {
					writer.pushPacket((float[]) array, 0, Array.getLength(array), time);
				} else if (array instanceof double[]) {
					writer.pushPacket((double[]) array, 0, Array.getLength(array), time);
				} else if (array instanceof long[]) {
					writer.pushPacket((long[]) array, 0, Array.getLength(array), time);
				} else if (array instanceof int[]) {
					writer.pushPacket((int[]) array, 0, Array.getLength(array), time);
				} else if (array instanceof short[]) {
					writer.pushPacket((short[]) array, 0, Array.getLength(array), time);
				} else if (array instanceof char[]) {
					writer.pushPacket((char[]) array, 0, Array.getLength(array), time);
				} else {
					return new Status(Status.ERROR, SnapshotActivator.PLUGIN_ID, "Unknown buffer type: " + array.getClass(), null);
				}
			} catch (IOException e) {
				return new Status(Status.ERROR, SnapshotActivator.PLUGIN_ID, "Error during write", e);
			}
		} catch (IOException e) {
			return new Status(Status.ERROR, SnapshotActivator.PLUGIN_ID, "Error during open" , e);
		} finally {
			if (writer.isOpen()) {
				try {
					writer.close();
				} catch (IOException e) {
					return new Status(Status.ERROR, SnapshotActivator.PLUGIN_ID, "Error during close" , e);
				}
			}
		}
		return Status.OK_STATUS;
	}

}
