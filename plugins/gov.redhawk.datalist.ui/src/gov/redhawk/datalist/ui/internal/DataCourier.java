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

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.statushandlers.StatusManager;

import BULKIO.StreamSRI;
import gov.redhawk.bulkio.util.BulkIOType;
import gov.redhawk.datalist.ui.DataCollectionSettings;
import gov.redhawk.datalist.ui.DataListPlugin;
import gov.redhawk.model.sca.ScaUsesPort;
import gov.redhawk.statistics.ui.views.StatisticsView;

/**
 * @since 1.1
 */
public class DataCourier {

	public static final int REAL = 0, IMAGINARY = 1, ALL = -1;

	private DataBuffer dataBuffer;
	private ScaUsesPort source;
	private BulkIOType type;
	private ListenerList<IDataCourierListener> listeners = new ListenerList<IDataCourierListener>();

	public void addListener(IDataCourierListener listener) {
		listeners.add(listener);
	}

	public void removeListener(IDataCourierListener listener) {
		listeners.remove(listener);
	}

	public void openStatisticsView(String secondaryID) {
		try {
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			StatisticsView statsView = (StatisticsView) window.getActivePage().showView(StatisticsView.ID, secondaryID, IWorkbenchPage.VIEW_ACTIVATE);
			statsView.setInput(convertToDataSet(dataBuffer));
		} catch (PartInitException e) {
			StatusManager.getManager().handle(new Status(Status.WARNING, DataListPlugin.PLUGIN_ID, "Problem opening Stats View.", e),
				StatusManager.SHOW | StatusManager.LOG);
		}
	}

	/**
	 * TODO: There's no need to reformat the data. The stats code could directly use the buffer, which would save time
	 * and memory.
	 * @param buffer
	 * @return
	 */
	private Number[][] convertToDataSet(DataBuffer buffer) {
		int dimensions = buffer.getDimension();
		int totalSamples = buffer.size();

		// Create 2D array
		Number[][] retVal = new Number[dimensions][];
		for (int dimension = 0; dimension < dimensions; dimension++) {
			retVal[dimension] = new Number[totalSamples];
		}

		// Populate with sample data
		for (int sampleIndex = 0; sampleIndex < totalSamples; sampleIndex++) {
			Object[] sample = buffer.getSample(sampleIndex);
			for (int dimension = 0; dimension < dimensions; dimension++) {
				retVal[dimension][sampleIndex] = (Number) sample[dimension];
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

	public DataBuffer getBuffer() {
		return dataBuffer;
	}

	public int getSize() {
		return dataBuffer.size();
	}

	public StreamSRI getStreamSRI() {
		return dataBuffer.getStreamSRI();
	}

	public void acquire(DataCollectionSettings settings, String connectionId) {
		dataBuffer.acquire(settings, connectionId);
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
