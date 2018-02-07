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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import BULKIO.BitSequence;
import BULKIO.PrecisionUTCTime;
import BULKIO.StreamSRI;
import gov.redhawk.bulkio.util.AbstractUberBulkIOPort;
import gov.redhawk.bulkio.util.BufferCopy;
import gov.redhawk.bulkio.util.BulkIOType;
import gov.redhawk.bulkio.util.BulkIOUtilActivator;
import gov.redhawk.datalist.ui.DataCollectionSettings;
import gov.redhawk.datalist.ui.DataListPlugin;
import gov.redhawk.datalist.ui.views.OptionsComposite.CaptureMethod;
import gov.redhawk.model.sca.ScaUsesPort;

/**
 * Handles directly receiving port data and storing it, deciding when acquisition is complete, and accessing the data
 * afterwards.
 */
public class DataBuffer extends AbstractUberBulkIOPort {

	private final List<BulkioPush> dataPushes = new ArrayList<>();
	private final List<Integer> dataLengths = new ArrayList<>();

	private final ScaUsesPort port;
	private String connectionId;
	private final ListenerList<IDataBufferListener> listeners = new ListenerList<IDataBufferListener>();

	/**
	 * The wall clock time that collection started (in milliseconds since the epoch).
	 */
	private long startTime;

	/**
	 * How long (in wall clock time) to acquire data (in milliseconds).
	 */
	private double timeToAcquire;

	/**
	 * The current number of received 'atoms' (the smallest unit of data from a push). For example, with complex data,
	 * two atoms make up a sample.
	 */
	private int receivedAtoms;

	/**
	 * Number of samples to acquire.
	 */
	private int samplesToAcquire;

	private CaptureMethod captureMethod;

	private DataCollectionSettings settings;

	private int dimension = 1;

	private boolean connected = false;

	private final Job disconnectJob = new Job("Disconnecting...") {

		@Override
		protected IStatus run(final IProgressMonitor monitor) {
			BulkIOType type2 = getBulkIOType();
			String ior2 = port.getIor();
			if (type2 != null && ior2 != null) {
				BulkIOUtilActivator.getBulkIOPortConnectionManager().disconnect(ior2, type2, DataBuffer.this, DataBuffer.this.connectionId);
			}
			return Status.OK_STATUS;
		}

	};
	private final Job connectJob = new Job("Connecting...") {

		@Override
		protected IStatus run(final IProgressMonitor monitor) {

			try {
				BulkIOType type2 = getBulkIOType();
				String ior2 = port.getIor();
				if (type2 != null && ior2 != null) {
					BulkIOUtilActivator.getBulkIOPortConnectionManager().connect(ior2, type2, DataBuffer.this, DataBuffer.this.connectionId);
				}
			} catch (CoreException e) {
				return new Status(e.getStatus().getSeverity(), DataListPlugin.PLUGIN_ID, "Failed to connect port.", e);
			}

			startTime = System.currentTimeMillis();

			return Status.OK_STATUS;
		}

	};

	public DataBuffer(ScaUsesPort port, BulkIOType type) {
		super(type);
		this.port = port;
	}

	public void addDataBufferListener(final IDataBufferListener listener) {
		this.listeners.add(listener);
	}

	public void removeDataBufferListener(final IDataBufferListener listener) {
		this.listeners.remove(listener);
	}

	protected void fireDataBufferChanged() {
		for (final Object listener : listeners.getListeners()) {
			((IDataBufferListener) listener).dataBufferChanged(this);
		}
	}

	protected void fireDataBufferComplete() {
		for (final Object listener : listeners.getListeners()) {
			((IDataBufferListener) listener).dataBufferComplete(this);
		}
	}

	public ScaUsesPort getPort() {
		return this.port;
	}

	public void acquire(final DataCollectionSettings settings, String connectionId) {
		if (connected) {
			return;
		}
		this.connectionId = connectionId;

		connected = true;
		clear();
		this.setDimension(settings.getDimensions());
		CaptureMethod method = settings.getProcessType();
		switch (method) {
		case NUMBER:
			this.samplesToAcquire = (int) settings.getSamples();
			this.timeToAcquire = 0;
			break;
		case SAMPLE_TIME:
			throw new IllegalArgumentException("Sample time not implemented");
		case INDEFINITELY:
			this.samplesToAcquire = 0;
			this.timeToAcquire = 0;
			break;
		case CLOCK_TIME:
			this.samplesToAcquire = 0;
			this.timeToAcquire = settings.getSamples();
			break;
		default:
			throw new IllegalArgumentException("Unsupported capture type");
		}
		this.captureMethod = method;
		this.settings = settings;

		this.connectJob.schedule();
	}

	public void dispose() {
		disconnect();
		this.listeners.clear();
	}

	public void clear() {
		this.receivedAtoms = 0;
		this.timeToAcquire = 0;
		this.dataPushes.clear();
		this.dataLengths.clear();
		fireDataBufferChanged();
	}

	public void setDimension(final int dimension) {
		this.dimension = dimension;
	}

	private void handlePacket(final Object data, int length, final PrecisionUTCTime time, final boolean eos, final String streamID) {
		if (!connected) {
			return;
		}

		StreamSRI sri = getSri(streamID);
		super.pushPacket(length, time, eos, streamID);

		// Handle ending capture based on method
		switch (captureMethod) {
		case NUMBER:
			if (this.receivedAtoms + length >= this.samplesToAcquire * dimension) {
				int newLength = this.samplesToAcquire * dimension - this.receivedAtoms;
				Object truncatedData = BufferCopy.copyOf(data, newLength);
				dataPushes.add(new BulkioPush(sri, truncatedData, newLength, time, eos, streamID));
				dataLengths.add(newLength);
				this.receivedAtoms += newLength;
				disconnect();
				return;
			}
			break;
		case CLOCK_TIME:
			if (timeToAcquire > System.currentTimeMillis() - startTime) {
				disconnect();
				return;
			}
			break;
		default:
			break;
		}

		// Add all data
		dataPushes.add(new BulkioPush(sri, data, length, time, eos, streamID));
		dataLengths.add(length);
		this.receivedAtoms += length;

		if (eos) {
			disconnect();
			return;
		}

		fireDataBufferChanged();
	}

	private DataCollectionSettings saveSettings() {
		if (captureMethod != null) {
			switch (captureMethod) {
			case NUMBER:
				return settings;
			default:
				settings.setSamples((double) samplesToAcquire);
				return settings;
			}
		}
		return null;
	}

	public void disconnect() {
		if (!connected) {
			return;
		}
		connected = false;
		saveSettings();
		this.disconnectJob.schedule();
		fireDataBufferComplete();
	}

	public List<BulkioPush> getBuffers() {
		return Collections.unmodifiableList(dataPushes);
	}

	/**
	 * Retrieves a sample by index. To retrieve data in bulk, use {@link #getBuffers()}.
	 * @param index
	 * @return
	 */
	public Object[] getSample(int index) {
		// Compute offset
		int offset = index * dimension;

		// Iterate buffers until we find the offset
		int subsample = 0;
		Object[] sample = new Object[dimension];
		for (int bufferIndex = 0; bufferIndex < dataPushes.size(); bufferIndex++) {
			int bufferLength = dataLengths.get(bufferIndex);
			while (subsample < dimension && offset < bufferLength) {
				BulkioPush push = dataPushes.get(bufferIndex);
				if (push.getData() instanceof BitSequence) {
					sample[subsample] = ((BitSequence) push.getData()).data[offset / 8] >> (7 - offset % 8) & 0x1;
				} else {
					sample[subsample] = Array.get(push.getData(), offset);
				}
				subsample++;
				offset++;
			}
			if (subsample == dimension) {
				return sample;
			}
			offset -= bufferLength;
		}

		throw new ArrayIndexOutOfBoundsException(index);
	}

	/**
	 * Returns the last {@link StreamSRI} pushed before the packet containing the sample.
	 * @param index
	 * @return
	 */
	public StreamSRI getSampleSRI(int index) {
		int offset = index * dimension;
		for (int bufferIndex = 0; bufferIndex < dataPushes.size(); bufferIndex++) {
			int bufferLength = dataLengths.get(bufferIndex);
			if (offset < bufferLength) {
				return dataPushes.get(bufferIndex).getSRI();
			}
			offset -= bufferLength;
		}

		throw new ArrayIndexOutOfBoundsException(index);
	}

	/**
	 * Gets the exact time of the sample
	 * @param index
	 * @return The sample time, or null if it can't be calculated
	 */
	public PrecisionUTCTime getSampleTime(int index) {
		StreamSRI sri = getSampleSRI(index);
		if (sri == null || sri.xunits != BULKIO.UNITS_TIME.value) {
			return null;
		}

		int offset = index * dimension;
		for (int bufferIndex = 0; bufferIndex < dataPushes.size(); bufferIndex++) {
			int bufferLength = dataLengths.get(bufferIndex);
			if (offset < bufferLength) {
				PrecisionUTCTime pushTime = dataPushes.get(bufferIndex).getTime();
				double timeOffset = sri.xdelta * offset;
				PrecisionUTCTime sampleTime = new PrecisionUTCTime(pushTime.tcmode, pushTime.tcstatus, pushTime.toff, pushTime.twsec, pushTime.tfsec);
				sampleTime.twsec += Math.floor(timeOffset);
				sampleTime.tfsec += (timeOffset - Math.floor(timeOffset));
				if (sampleTime.tfsec >= 1.0) {
					sampleTime.twsec += 1.0;
					sampleTime.tfsec -= 1.0;
				}
				return sampleTime;
			}
			offset -= bufferLength;
		}

		throw new ArrayIndexOutOfBoundsException(index);
	}

	@Override
	public void pushPacket(BitSequence data, PrecisionUTCTime time, boolean eos, String streamID) {
		handlePacket(data, data.bits, time, eos, streamID);
	}

	@Override
	public void pushPacket(short[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		handlePacket(data, data.length, time, eos, streamID);
	}

	@Override
	public void pushPacket(byte[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		handlePacket(data, data.length, time, eos, streamID);
	}

	@Override
	public void pushPacket(int[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		handlePacket(data, data.length, time, eos, streamID);
	}

	@Override
	public void pushPacket(char[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		handlePacket(data, data.length, time, eos, streamID);
	}

	@Override
	public void pushPacket(long[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		handlePacket(data, data.length, time, eos, streamID);
	}

	@Override
	public void pushPacket(float[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		handlePacket(data, data.length, time, eos, streamID);
	}

	@Override
	public void pushPacket(double[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		handlePacket(data, data.length, time, eos, streamID);
	}

	public int getDimension() {
		return dimension;
	}

	public int size() {
		// Sum the data lengths
		return dataLengths.stream().reduce(0, Integer::sum) / dimension;
	}
}
