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

import gov.redhawk.datalist.ui.DataListPlugin;
import gov.redhawk.datalist.ui.internal.OptionsComposite.CaptureMethod;
import gov.redhawk.model.sca.ScaUsesPort;
import gov.redhawk.sca.util.OrbSession;

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
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import BULKIO.PortStatistics;
import BULKIO.PortUsageType;
import BULKIO.PrecisionUTCTime;
import BULKIO.StreamSRI;
import CF.PortPackage.InvalidPort;
import CF.PortPackage.OccupiedPort;

public abstract class DataBuffer {

	private final List<Sample> dataBuffer = new CopyOnWriteArrayList<Sample>();
	private int dimension = 1;
	private StreamSRI sri;
	private ScaUsesPort port;
	private final OrbSession session = OrbSession.createSession();
	private org.omg.CORBA.Object ref;
	private String connectionId;
	private final List<IDataBufferListener> listeners = Collections.synchronizedList(new LinkedList<IDataBufferListener>());

	private List<Object> cached;

	private final Job disconnectJob = new Job("Disconnecting...") {

		@Override
		protected IStatus run(final IProgressMonitor monitor) {
			if (DataBuffer.this.connectionId != null) {
				try {
					DataBuffer.this.port.disconnectPort(DataBuffer.this.connectionId);
				} catch (final InvalidPort e) {
					e.fillInStackTrace();
					DataListPlugin.getDefault().getLog().log(new Status(
							Status.WARNING, 
							DataListPlugin.PLUGIN_ID, 
							"Invalid port.", e));
				}
			}
			DataBuffer.this.connectionId = null;
			return Status.OK_STATUS;
		}

	};
	private final Job connectJob = new Job("Connecting...") {

		@Override
		protected IStatus run(final IProgressMonitor monitor) {

			try {
				if (DataBuffer.this.ref == null) {
					DataBuffer.this.ref = createRef(DataBuffer.this.session.getPOA());
				}
				DataBuffer.this.connectionId = "dataList_" + System.currentTimeMillis();
				DataBuffer.this.port.connectPort(DataBuffer.this.ref, DataBuffer.this.connectionId);
				// converting to seconds (double)
				DataBuffer.this.initialTime = ((double) System.currentTimeMillis()) / 1000; 
			} catch (final InvalidPort e) {
				e.fillInStackTrace();
				DataListPlugin.getDefault().getLog().log(new Status(
						Status.WARNING, 
						DataListPlugin.PLUGIN_ID, 
						"Invalid port.", e));
			} catch (final OccupiedPort e) {
				e.fillInStackTrace();
				DataListPlugin.getDefault().getLog().log(new Status(
						Status.WARNING, 
						DataListPlugin.PLUGIN_ID, 
						"Occupied port.", e));
			} catch (final ServantNotActive e) {
				e.fillInStackTrace();
				DataListPlugin.getDefault().getLog().log(new Status(
						Status.WARNING, 
						DataListPlugin.PLUGIN_ID, 
						"Servant not active.", e));
			} catch (final WrongPolicy e) {
				e.fillInStackTrace();
				DataListPlugin.getDefault().getLog().log(new Status(
						Status.WARNING, 
						DataListPlugin.PLUGIN_ID, 
						"Wrong policy.", e));
			} catch (final CoreException e) {
				e.fillInStackTrace();
				DataListPlugin.getDefault().getLog().log(new Status(
						Status.WARNING, 
						DataListPlugin.PLUGIN_ID, 
						"Core exception.", e));
			}
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

	public void setPort(final ScaUsesPort port) {
		this.port = port;
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
		if (this.ref != null) {
			this.ref._release();
		}
		this.session.dispose();
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

	public PortUsageType state() {
		// TODO Auto-generated method stub
		return null;
	}

	public PortStatistics statistics() {
		// TODO Auto-generated method stub
		return null;
	}

	public StreamSRI[] activeSRIs() {
		// TODO Auto-generated method stub
		return null;
	}

	public StreamSRI getSri() {
		return this.sri;
	}

	public void pushSRI(final StreamSRI sri) {
		if (samples > index) {
			if (this.captureMethod == CaptureMethod.SAMPLE_TIME) {
				this.currentSampleDelta = (sri.xdelta != 0) ? sri.xdelta : 1;
				samples = ((int) (((this.totalTime - this.currentTimeDuration) 
						/ currentSampleDelta) + .5)) + this.index;
			}
			this.sri = sri;
		}
	}

	public void pushPacket(final Object data, final PrecisionUTCTime time, final boolean eos, final String streamID) {
		// converting from milliseconds to seconds (double)
		double t = ((double) System.currentTimeMillis()) / 1000;
		if (reachedLimit(t) || eos) {
			return;
		}

		final int length = Array.getLength(data); 

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
		switch(captureMethod) {
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

		switch(captureMethod) {
		case NUMBER:
			return settings;
		default:
			settings.setSamples((double) samples); 
			return settings;
		}
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
	public abstract org.omg.CORBA.Object createRef(POA poa) throws ServantNotActive, WrongPolicy;

}
