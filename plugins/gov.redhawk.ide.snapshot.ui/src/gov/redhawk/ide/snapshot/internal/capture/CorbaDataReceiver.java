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
package gov.redhawk.ide.snapshot.internal.capture;

import gov.redhawk.bulkio.util.AbstractUberBulkIOPort;
import gov.redhawk.bulkio.util.BulkIOType;
import gov.redhawk.bulkio.util.BulkIOUtilActivator;
import gov.redhawk.ide.snapshot.capture.IDataReceiver;
import gov.redhawk.ide.snapshot.ui.SnapshotUI;
import gov.redhawk.ide.snapshot.writer.IDataWriter;
import gov.redhawk.model.sca.ScaUsesPort;
import gov.redhawk.sca.util.SubMonitor;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;

import BULKIO.BitSequence;
import BULKIO.PrecisionUTCTime;
import BULKIO.StreamSRI;

public class CorbaDataReceiver extends AbstractUberBulkIOPort implements IDataReceiver {

	private IDataWriter writer;
	private IOException exception;
	private ScaUsesPort port;
	private String connectionID;
	private boolean processing;

	public void setPort(ScaUsesPort port) {
		setBulkIOType(BulkIOType.getType(port.getRepid()));
		this.port = port;
	}
	
	public ScaUsesPort getPort() {
		return port;
	}

	@Override
	public void setDataWriter(IDataWriter writer) {
		this.writer = writer;
	}

	@Override
	public IDataWriter getDataWriter() {
		return writer;
	}

	/**
	 * @since 1.1
	 */
	public String getConnectionID() {
		return connectionID;
	}

	/**
	 * @since 1.1
	 */
	public void setConnectionID(String connectionID) {
		this.connectionID = connectionID;
	}

	@Override
	protected void handleStreamSRIChanged(String streamID, StreamSRI oldSri, StreamSRI newSri) {
		try {
			writer.pushSRI(newSri);
		} catch (IOException e) {
			setException(e);
		}
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		SubMonitor subMonitor = SubMonitor.convert(monitor, "Taking Snapshot...", 100);
		SubMonitor child = subMonitor.newChild(5);
		try {
			connect(child);
		} catch (CoreException e) {
			return new Status(e.getStatus().getSeverity(), SnapshotUI.PLUGIN_ID, e.getLocalizedMessage(), e);
		}

		child = subMonitor.newChild(5);
		try {
			open(child);
			processSamples(subMonitor.newChild(80));
		} catch (IOException e) {
			return new Status(Status.ERROR, SnapshotUI.PLUGIN_ID, "Error during open", e);
		} finally {
			child = subMonitor.newChild(5);
			disconnect(child);
			child = subMonitor.newChild(5);
			try {
				close(child);
			} catch (IOException e) {
				return new Status(Status.ERROR, SnapshotUI.PLUGIN_ID, "Error during close", e);
			}
			subMonitor.done();
		}
		
		if (monitor.isCanceled()) {
			return Status.CANCEL_STATUS;
		}
		return Status.OK_STATUS;
	}

	protected void disconnect(SubMonitor child) {
		child.beginTask("Disconnecting port...", 1);
		try {
			BulkIOType type2 = getBulkIOType();
			String ior2 = port.getIor();
			if (type2 != null && ior2 != null) {
				BulkIOUtilActivator.getBulkIOPortConnectionManager().disconnect(ior2, type2, this, this.connectionID);
			}
		} finally {
			child.done();
		}
	}

	protected void close(SubMonitor child) throws IOException {
		child.beginTask("Closing output file...", 1);
		try {
			if (writer.isOpen()) {
				writer.close();
			}
		} finally {
			child.done();
		}
	}

	protected void open(SubMonitor child) throws IOException {
		child.beginTask("Opening output file...", 1);
		try {
			writer.open();
		} finally {
			child.done();
		}
	}

	protected void connect(SubMonitor child) throws CoreException {
		child.beginTask("Connecting to port...", 1);
		if (port == null) {
			throw new IllegalStateException("Port must not be null");
		}
		try {
			BulkIOType type2 = getBulkIOType();
			String ior2 = port.getIor();
			if (type2 != null && ior2 != null) {
				BulkIOUtilActivator.getBulkIOPortConnectionManager().connect(ior2, type2, this, this.connectionID);
			}
			while (getStreamSRI() == null) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// PASS
				}
				if (child.isCanceled()) {
					throw new OperationCanceledException();
				}
			}
		} finally {
			child.done();
		}
	}

	protected void processSamples(IProgressMonitor monitor) {
		SubMonitor subMonitor = SubMonitor.convert(monitor, "Capturing Samples...", IProgressMonitor.UNKNOWN);
		setProcessing(true);
		while (isProcessing() && !monitor.isCanceled()) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// PASS
			}
		}
		subMonitor.done();
	}

	protected boolean isProcessing() {
		return processing;
	}

	public synchronized void setProcessing(boolean processing) {
		this.processing = processing;
		notifyAll();
	}

	protected void setException(IOException e) {
		this.exception = e;
		setProcessing(false);
	}

	protected < T > T getSamplesToProcess(T data, Class<T> dataClass, int length, PrecisionUTCTime time) {
		return data;
	}

	@Override
	protected boolean pushPacket(int length, PrecisionUTCTime time, boolean endOfStream, String streamID) {
		boolean retVal = super.pushPacket(length, time, endOfStream, streamID);
		if (!retVal || super.getStreamSRI() == null || exception != null || !writer.isOpen() || !processing) {
			return false;
		}
		return true;
	}

	@Override
	public void pushPacket(BitSequence data, PrecisionUTCTime time, boolean eos, String streamID) {
		if (!pushPacket(data.bits, time, eos, streamID)) {
			return;
		}
		BitSequence dataToProcess = getSamplesToProcess(data, BitSequence.class, data.bits, time);
		try {
			writer.pushPacket(data, time, eos, streamID);
		} catch (IOException e) {
			setException(e);
		}
		postPushPacket(dataToProcess.bits, time);
	}

	@Override
	public void pushPacket(short[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		if (!pushPacket(data.length, time, eos, streamID)) {
			return;
		}
		short[] dataToProcess = getSamplesToProcess(data, short[].class, data.length, time);
		try {
			writer.pushPacket(dataToProcess, time, eos, streamID);
		} catch (IOException e) {
			setException(e);
		}
		postPushPacket(dataToProcess.length, time);
	}

	@Override
	public void pushPacket(char[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		if (!pushPacket(data.length, time, eos, streamID)) {
			return;
		}
		char[] dataToProcess = getSamplesToProcess(data, char[].class, data.length, time);
		try {
			writer.pushPacket(dataToProcess, time, eos, streamID);
		} catch (IOException e) {
			setException(e);
		}
		postPushPacket(dataToProcess.length, time);
	}

	@Override
	public void pushPacket(double[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		if (!pushPacket(data.length, time, eos, streamID)) {
			return;
		}
		double[] dataToProcess = getSamplesToProcess(data, double[].class, data.length, time);
		try {
			writer.pushPacket(dataToProcess, time, eos, streamID);
		} catch (IOException e) {
			setException(e);
		}
		postPushPacket(dataToProcess.length, time);
	}

	@Override
	public void pushPacket(float[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		if (!pushPacket(data.length, time, eos, streamID)) {
			return;
		}
		float[] dataToProcess = getSamplesToProcess(data, float[].class, data.length, time);
		try {
			writer.pushPacket(dataToProcess, time, eos, streamID);
		} catch (IOException e) {
			setException(e);
		}
		postPushPacket(dataToProcess.length, time);
	}

	@Override
	public void pushPacket(long[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		if (!pushPacket(data.length, time, eos, streamID)) {
			return;
		}
		long[] dataToProcess = getSamplesToProcess(data, long[].class, data.length, time);
		try {
			writer.pushPacket(dataToProcess, time, eos, streamID);
		} catch (IOException e) {
			setException(e);
		}
		postPushPacket(dataToProcess.length, time);
	}

	@Override
	public void pushPacket(int[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		if (!pushPacket(data.length, time, eos, streamID)) {
			return;
		}
		int[] dataToProcess = getSamplesToProcess(data, int[].class, data.length, time);
		try {
			writer.pushPacket(dataToProcess, time, eos, streamID);
		} catch (IOException e) {
			setException(e);
		}
		postPushPacket(dataToProcess.length, time);
	}

	@Override
	public void pushPacket(byte[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		if (!pushPacket(data.length, time, eos, streamID)) {
			return;
		}
		byte[] dataToProcess = getSamplesToProcess(data, byte[].class, data.length, time);
		try {
			writer.pushPacket(dataToProcess, time, eos, streamID);
		} catch (IOException e) {
			setException(e);
		}
		postPushPacket(dataToProcess.length, time);
	}

	protected void postPushPacket(int samplesProcessed, PrecisionUTCTime time) {
	}

}
