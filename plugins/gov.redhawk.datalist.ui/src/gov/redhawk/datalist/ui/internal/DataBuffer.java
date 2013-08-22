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

package gov.redhawk.datalist.ui.internal;

import gov.redhawk.bulkio.util.AbstractUberBulkIOPort;
import gov.redhawk.bulkio.util.BulkIOType;
import gov.redhawk.bulkio.util.BulkIOUtilActivator;
import gov.redhawk.datalist.ui.views.OptionsComposite.CaptureMethod;
import gov.redhawk.model.sca.ScaUsesPort;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import BULKIO.PrecisionUTCTime;
import BULKIO.StreamSRI;

public class DataBuffer extends AbstractUberBulkIOPort {

	private final List<Sample> dataBuffer = new CopyOnWriteArrayList<Sample>();
	private int dimension = 1;
	private final ScaUsesPort port;
	private final List<IDataBufferListener> listeners = Collections.synchronizedList(new LinkedList<IDataBufferListener>());

	private List<Object> cached;

	private final Job disconnectJob = new Job("Disconnecting...") {

		@Override
		protected IStatus run(final IProgressMonitor monitor) {
			BulkIOUtilActivator.getBulkIOPortConnectionManager().disconnect(port.getIor(), getBulkIOType(), DataBuffer.this);
			return Status.OK_STATUS;
		}

	};
	private final Job connectJob = new Job("Connecting...") {

		@Override
		protected IStatus run(final IProgressMonitor monitor) {

			try {
				BulkIOUtilActivator.getBulkIOPortConnectionManager().connect(port.getIor(), getBulkIOType(), DataBuffer.this);
			} catch (CoreException e) {
				return e.getStatus();
			}

			// converting to seconds (double)
			DataBuffer.this.initialTime = ((double) System.currentTimeMillis()) / 1000;

			return Status.OK_STATUS;
		}

	};
	private int samples;
	private int index;
	private double initialTime;
	private double currentTimeDuration;
	private double currentSampleDelta;
	private double totalTime;
	private CaptureMethod captureMethod;
	private DataCollectionSettings settings;

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
		for (final IDataBufferListener listener : this.listeners) {
			listener.dataBufferChanged(this);
		}
	}

	public ScaUsesPort getPort() {
		return this.port;
	}

	public void acquire(final int samples) {
		clear();
		this.samples = samples;
		this.connectJob.schedule();
	}

	public void acquire(final DataCollectionSettings settings) {
		clear();
		this.setDimension(settings.getDimensions());
		CaptureMethod method = CaptureMethod.stringToValue(settings.getProcessType());
		this.currentSampleDelta = 1;
		switch (method) {
		case NUMBER:
			this.samples = (int) settings.getSamples();
			this.totalTime = samples * this.currentSampleDelta;
			break;
		case SAMPLE_TIME:
			this.totalTime = settings.getSamples();
			this.samples = (int) (this.totalTime / this.currentSampleDelta + .5);
			break;
		case INDEFINITELY:
			this.totalTime = 0;
			this.samples = 0;
			break;
		case CLOCK_TIME:
			this.totalTime = settings.getSamples();
			this.samples = 0;
			break;
		default:
			throw new IllegalArgumentException("Unsupported Capture Type");
		}
		this.captureMethod = method;
		this.settings = settings;
		this.connectJob.schedule();

	}

	public void dispose() {
		disconnect();
		this.listeners.clear();
	}

	public List<Sample> getDataBuffer() {
		return this.dataBuffer;
	}

	public void clear() {
		this.index = 0;
		this.currentTimeDuration = 0;
		this.totalTime = 0;
		this.dataBuffer.clear();
		if (this.cached != null) {
			this.cached.clear();
			this.cached = null;
		}
		fireDataBufferChanged();
	}

	public void setDimension(final int dimension) {
		this.dimension = dimension;
	}

	public void pushSRI(final StreamSRI sri) {
		super.pushSRI(sri);
		if (samples > index) {
			if (this.captureMethod == CaptureMethod.SAMPLE_TIME) {
				this.currentSampleDelta = (sri.xdelta != 0) ? sri.xdelta : 1;
				samples = ((int) (((this.totalTime - this.currentTimeDuration) / currentSampleDelta) + .5)) + this.index;
			}
		}
	}

	public void pushPacket(final Object data, final PrecisionUTCTime time, final boolean eos, final String streamID) {
		final int length = Array.getLength(data);

		super.pushPacket(length, time, eos, streamID);

		// converting from milliseconds to seconds (double)
		double t = ((double) System.currentTimeMillis()) / 1000;
		if (reachedLimit(t) || eos) {
			return;
		}

		for (int i = 0; i < length; i++) {
			if (this.dimension == 1) {
				this.dataBuffer.add(new Sample(time, this.index++, Array.get(data, i)));
			} else {
				List<Object> sampleList = new ArrayList<Object>();

				if (cached != null) {
					sampleList.addAll(cached);
					cached.clear();
					cached = null;
				}

				while (i < length && sampleList.size() < this.dimension) {
					if (Array.get(data, i) != null) {
						sampleList.add(Array.get(data, i));
					}
					i++;
				}

				if (sampleList.size() == this.dimension) {
					this.dataBuffer.add(new Sample(time, this.index++, sampleList.toArray()));
				} else {
					cached = sampleList;
				}
			}
		}
		fireDataBufferChanged();

		if (reachedLimit(t)) {
			disconnect();
		}
	}

	private boolean reachedLimit(double currentTime) {
		switch (captureMethod) {
		case NUMBER:
			return (this.index >= this.samples);
		case SAMPLE_TIME:
			return (this.index >= this.samples);
		case CLOCK_TIME:
			currentTimeDuration = currentTime - initialTime;
			return (this.currentTimeDuration >= this.totalTime);
		case INDEFINITELY:
			return false;
		default:
			return false;
		}

	}

	public DataCollectionSettings saveSettings() {
		if (captureMethod != null) {
			switch (captureMethod) {
			case NUMBER:
				return settings;
			default:
				settings.setSamples((double) samples);
				return settings;
			}
		}
		return null;
	}

	public List<Object> listCopy() {
		List<Object> s = new ArrayList<Object>();
		for (Sample smpl : dataBuffer) {
			s.add(smpl.getIndex(), smpl.getData());
		}
		return s;
	}

	public List<Sample> samplesCopy() {
		List<Sample> s = new ArrayList<Sample>();
		for (Sample smpl : dataBuffer) {
			s.add(smpl.getIndex(), smpl);
		}
		return s;
	}

	public void disconnect() {
		saveSettings();
		this.disconnectJob.schedule();

	}

	public List<Sample> getList() {
		return dataBuffer;
	}

	@Override
	public void pushPacket(short[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		pushPacket((Object) data, time, eos, streamID);
	}

	@Override
	public void pushPacket(byte[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		pushPacket((Object) data, time, eos, streamID);
	}

	@Override
	public void pushPacket(int[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		pushPacket((Object) data, time, eos, streamID);
	}

	@Override
	public void pushPacket(char[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		pushPacket((Object) data, time, eos, streamID);
	}

	@Override
	public void pushPacket(long[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		pushPacket((Object) data, time, eos, streamID);
	}

	@Override
	public void pushPacket(float[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		pushPacket((Object) data, time, eos, streamID);
	}

	@Override
	public void pushPacket(double[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		pushPacket((Object) data, time, eos, streamID);
	}

}
