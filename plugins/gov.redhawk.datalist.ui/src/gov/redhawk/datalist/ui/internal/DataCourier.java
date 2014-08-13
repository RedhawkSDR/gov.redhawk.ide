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

import gov.redhawk.bulkio.util.BulkIOType;
import gov.redhawk.datalist.ui.DataCollectionSettings;
import gov.redhawk.datalist.ui.DataListPlugin;
import gov.redhawk.datalist.ui.Sample;
import gov.redhawk.model.sca.ScaUsesPort;
import gov.redhawk.statistics.ui.views.StatisticsView;

import java.util.Arrays;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.statushandlers.StatusManager;

import BULKIO.StreamSRI;

/**
 * @since 1.1
 */
public class DataCourier {

	private DataBuffer dataBuffer;

	public static final int REAL = 0, IMAGINARY = 1, ALL = -1;

	private StatisticsView sView;
	private ScaUsesPort source;

	private DisposeListener disposeListener = new DisposeListener() {

		@Override
		public void widgetDisposed(DisposeEvent e) {
			sView = null;
		}

	};

	private BulkIOType type;
	private ListenerList listeners = new ListenerList();

	public void addListener(IDataCourierListener listener) {
		listeners.add(listener);
	}

	public void removeListener(IDataCourierListener listener) {
		listeners.remove(listener);
	}

	public void openStatisticsView(String secondaryID) {
		try {
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			sView = (StatisticsView) window.getActivePage().showView(StatisticsView.ID, secondaryID, IWorkbenchPage.VIEW_ACTIVATE);
			sView.addDisposeListener(disposeListener);
			updateStatisticsView();
		} catch (PartInitException e) {
			StatusManager.getManager().handle(new Status(Status.WARNING, DataListPlugin.PLUGIN_ID, "Problem opening Stats View.", e),
				StatusManager.SHOW | StatusManager.LOG);
		}
	}

	private void updateStatisticsView() {
		if (sView != null) {
			sView.setInput(Arrays.asList(dataBuffer.getBuffer()), dataBuffer.getDimension(), type);
		}
	}

	public void clear() {
		dataBuffer.clear();
	}

	public int getDimensions() {
		return dataBuffer.getDimension();
	}

	public Sample[] getBuffer() {
		if (dataBuffer == null) {
			return new Sample[0];
		}
		return dataBuffer.getBuffer();
	}

	public int getSize() {
		return dataBuffer.size();
	}

	public StreamSRI getStreamSRI() {
		return dataBuffer.getStreamSRI();
	}

	public void acquire(DataCollectionSettings settings) {
		dataBuffer.acquire(settings);
	}

	public void stop() {
		dataBuffer.disconnect();
	}

	public void dispose() {
		if (dataBuffer != null) {
			dataBuffer.dispose();
			dataBuffer = null;
		}

	}

	public void setSource(ScaUsesPort port) {
		this.source = port;
		type = BulkIOType.getType(port.getRepid());

		this.dataBuffer = new DataBuffer(port, type);
		this.dataBuffer.addDataBufferListener(new IDataBufferListener() {

			@Override
			public void dataBufferComplete(DataBuffer d) {
				fireComplete();
			}

			@Override
			public void dataBufferChanged(DataBuffer d) {
				fireChanged();
			}
		});
	}

	protected void fireChanged() {
		for (Object obj : listeners.getListeners()) {
			((IDataCourierListener) obj).dataChanged();
		}
	}

	protected void fireComplete() {
		for (Object obj : listeners.getListeners()) {
			((IDataCourierListener) obj).complete();
		}
	}

	public ScaUsesPort getSource() {
		return source;
	}

	public BulkIOType getType() {
		return type;
	}
}
