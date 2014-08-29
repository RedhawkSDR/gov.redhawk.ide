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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import gov.redhawk.bulkio.util.BulkIOType;
import gov.redhawk.datalist.ui.DataCollectionSettings;
import gov.redhawk.datalist.ui.DataListPlugin;
import gov.redhawk.datalist.ui.Sample;
import gov.redhawk.model.sca.ScaUsesPort;
import gov.redhawk.statistics.ui.views.StatisticsView;

import org.apache.commons.lang.ArrayUtils;
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
			sView.setInput(convertToDataSet(dataBuffer.getBuffer()));
		}
	}

	private Number[][] convertToDataSet(List<Sample> buffer) {
		Number[][] dataSet = new Number[buffer.size()][];
		Iterator<Sample> iterator = buffer.iterator();
		for (int i = 0; i < dataSet.length; i++) {
			Object data = iterator.next().getData();
			if (data instanceof Number[]) {
				dataSet[i] = (Number[]) data;
			} else if (data instanceof Number) {
				dataSet[i] = new Number[] { (Number) data };
			} else if (data instanceof double[]) {
				dataSet[i] = ArrayUtils.toObject((double[]) data);
			} else if (data instanceof float[]) {
				dataSet[i] = ArrayUtils.toObject((float[]) data);
			} else if (data instanceof long[]) {
				dataSet[i] = ArrayUtils.toObject((long[]) data);
			} else if (data instanceof int[]) {
				dataSet[i] = ArrayUtils.toObject((int[]) data);
			} else if (data instanceof short[]) {
				dataSet[i] = ArrayUtils.toObject((short[]) data);
			} else if (data instanceof byte[]) {
				dataSet[i] = ArrayUtils.toObject((byte[]) data);
			} else if (data instanceof Object[]) {
				Object[] objArray = (Object[]) data;
				dataSet[i] = new Number[objArray.length];
				for (int j = 0; j < objArray.length; j++) {
					dataSet[i][j] = (Number) objArray[j];
				}
			} else {
				throw new IllegalStateException("Unsupported type: " + data.getClass());
			}

		}

		// Transpose the result
		Number[][] retVal = new Number[dataSet[0].length][dataSet.length];
		for (int i = 0; i < retVal.length; i++) {
			for (int j = 0; j < retVal[i].length; j++) {
				retVal[i][j] = dataSet[j][i];
			}
		}

		return retVal;
	}

	public void clear() {
		dataBuffer.clear();
	}

	public int getDimensions() {
		return dataBuffer.getDimension();
	}

	public List<Sample> getBuffer() {
		if (dataBuffer == null) {
			return Collections.emptyList();
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
