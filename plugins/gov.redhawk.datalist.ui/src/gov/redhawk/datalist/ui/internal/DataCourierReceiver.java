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

import java.io.IOException;
import java.lang.reflect.Array;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import BULKIO.PrecisionUTCTime;
import gov.redhawk.bulkio.util.BulkIOType;
import gov.redhawk.datalist.ui.DataListPlugin;
import gov.redhawk.datalist.ui.views.DataCourier;
import gov.redhawk.ide.snapshot.capture.IDataReceiver;
import gov.redhawk.ide.snapshot.writer.IDataWriter;
import gov.redhawk.ide.snapshot.writer.IDataWriterSettings;

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
		int dimensions = courier.getDimensions();
		BulkIOType type = courier.getType();
		IDataWriterSettings settings = writer.getSettings();
		settings.setType(type);
		writer.setSettings(settings);
		int length = dimensions * courier.getSize();
		Object data = Array.newInstance(type.getJavaType(), length);
		PrecisionUTCTime time = courier.getList().get(0).getTime();
		for (int i = 0; i < courier.getSize(); i++) {
			Sample s = courier.getList().get(i);
			if (dimensions == 1) {
				Array.set(data, i, s.getData());
			} else {
				System.arraycopy(s.getData(), 0, data, i * dimensions, dimensions);
			}
		}
		try {
			writer.open();
			try {
				switch (type) {
				case CHAR:
					writer.pushPacket((char[]) data, 0, length, time);
					break;
				case DOUBLE:
					writer.pushPacket((double[]) data, 0, length, time);
					break;
				case FLOAT:
					writer.pushPacket((float[]) data, 0, length, time);
					break;
				case LONG:
					writer.pushPacket((int[]) data, 0, length, time);
					break;
				case LONG_LONG:
					writer.pushPacket((long[]) data, 0, length, time);
					break;
				case OCTET:
					writer.pushPacket((byte[]) data, 0, length, time);
					break;
				case SHORT:
					writer.pushPacket((short[]) data, 0, length, time);
					break;
				case ULONG:
					writer.pushPacket((int[]) data, 0, length, time);
					break;
				case ULONG_LONG:
					writer.pushPacket((long[]) data, 0, length, time);
					break;
				case USHORT:
					writer.pushPacket((short[]) data, 0, length, time);
					break;
				default:
					return new Status(Status.ERROR, DataListPlugin.PLUGIN_ID, "Unknown data type: " + type, null);
				}
			} catch (IOException e) {
				return new Status(Status.ERROR, DataListPlugin.PLUGIN_ID, "Failed to write packet data", e);
			}
		} catch (IOException e) {
			return new Status(Status.ERROR, DataListPlugin.PLUGIN_ID, "Failed to open writer", e);
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				return new Status(Status.ERROR, DataListPlugin.PLUGIN_ID, "Failed to close writer", e);
			}
		}

		return Status.OK_STATUS;
	}
}
