/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.datalist.ui.internal;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import BULKIO.BitSequence;
import BULKIO.StreamSRI;
import gov.redhawk.bulkio.util.BulkIOType;
import gov.redhawk.bulkio.util.StreamSRIUtil;
import gov.redhawk.datalist.ui.DataListPlugin;
import gov.redhawk.ide.snapshot.capture.IDataReceiver;
import gov.redhawk.ide.snapshot.writer.IDataWriter;

/**
 * Handles taking the data from a {@link DataCourier} (which holds the data from a data list operation) and passing it
 * to an {@link IDataReceiver}, which is how the snapshot functionality receives data.
 */
public class DataCourierReceiver implements IDataReceiver {

	private DataCourier courier;

	public DataCourierReceiver(DataCourier courier) {
		this.courier = courier;
	}

	private IDataWriter writer;

	@Override
	public void setDataWriter(IDataWriter writer) {
		this.writer = writer;
	}

	@Override
	public IDataWriter getDataWriter() {
		return writer;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		List<BulkioPush> buffers = courier.getBuffer().getBuffers();
		if (buffers.size() == 0) {
			return new Status(IStatus.ERROR, DataListPlugin.PLUGIN_ID, "No data to write");
		}
		BulkIOType type = courier.getType();
		writer.getSettings().setType(type);

		try {
			StreamSRI lastSRIPushed = buffers.get(0).getSRI();
			writer.pushSRI(lastSRIPushed);
			writer.open();

			for (BulkioPush buffer : buffers) {
				// Push SRI if it has changed
				if (!StreamSRIUtil.equals(lastSRIPushed, buffer.getSRI())) {
					writer.pushSRI(buffer.getSRI());
					lastSRIPushed = buffer.getSRI();
				}

				// Push packet
				switch (type) {
				case BIT:
					writer.pushPacket((BitSequence) buffer.getData(), buffer.getTime(), buffer.getEOS(), buffer.getStreamID());
					break;
				case CHAR:
					writer.pushPacket((char[]) buffer.getData(), buffer.getTime(), buffer.getEOS(), buffer.getStreamID());
					break;
				case DOUBLE:
					writer.pushPacket((double[]) buffer.getData(), buffer.getTime(), buffer.getEOS(), buffer.getStreamID());
					break;
				case FLOAT:
					writer.pushPacket((float[]) buffer.getData(), buffer.getTime(), buffer.getEOS(), buffer.getStreamID());
					break;
				case LONG:
				case ULONG:
					writer.pushPacket((int[]) buffer.getData(), buffer.getTime(), buffer.getEOS(), buffer.getStreamID());
					break;
				case LONG_LONG:
				case ULONG_LONG:
					writer.pushPacket((long[]) buffer.getData(), buffer.getTime(), buffer.getEOS(), buffer.getStreamID());
					break;
				case OCTET:
					writer.pushPacket((byte[]) buffer.getData(), buffer.getTime(), buffer.getEOS(), buffer.getStreamID());
					break;
				case SHORT:
				case USHORT:
					writer.pushPacket((short[]) buffer.getData(), buffer.getTime(), buffer.getEOS(), buffer.getStreamID());
					break;
				default:
				}
			}
		} catch (IOException e) {
			return new Status(Status.ERROR, DataListPlugin.PLUGIN_ID, "Failed to write data", e);
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				// PASS
			}
		}

		return Status.OK_STATUS;
	}
}
