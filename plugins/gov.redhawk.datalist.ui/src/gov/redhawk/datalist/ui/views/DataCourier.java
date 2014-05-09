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
package gov.redhawk.datalist.ui.views;

import gov.redhawk.bulkio.util.BulkIOType;
import gov.redhawk.datalist.ui.DataListPlugin;
import gov.redhawk.datalist.ui.Sample;
import gov.redhawk.statistics.ui.views.StatisticsView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

/**
 * @since 1.1
 */
public class DataCourier {

	private int dimensions;
	private List<Sample> datalist = new ArrayList<Sample>();

	private List<IFullListener> listeners = Collections.synchronizedList(new ArrayList<IFullListener>());

	public static final int REAL = 0, IMAGINARY = 1, ALL = -1;

	private StatisticsView sView;
	private int numSamples = 0;
	private int currentIndex = 0;
	private BulkIOType type;
	private DisposeListener disposeListener = new DisposeListener() {

		@Override
		public void widgetDisposed(DisposeEvent e) {
			sView = null;
		}

	};

	public DataCourier() {
	}

	public void setType(BulkIOType type) {
		this.type = type;
	}

	public BulkIOType getType() {
		return type;
	}

	public void addFullListener(IFullListener listener) {
		listeners.add(listener);
	}

	public void removeFullListener(IFullListener listener) {
		listeners.remove(listener);
	}

	public void fireListIsFull() {
		for (IFullListener listener : listeners) {
			listener.fireIsFull(this);
		}
	}

	public void addList(List<Sample> elements) {
		for (Sample obj : elements) {
			addToList(obj);
		}
	}

	public void addToList(Sample element) {
		addOrSet(datalist, numSamples, element.getIndex(), element);
		if (numSamples == datalist.size()) {
			fireListIsFull();
			UIJob uiJob = new UIJob("Update Statistics View") {

				@Override
				public IStatus runInUIThread(IProgressMonitor monitor) {
					updateStatisticsView();
					return Status.OK_STATUS;
				}
			};
			uiJob.schedule();
		}
	}

	private static List<Object> toObjectList(List<Sample> list) {
		List<Object> objList = new ArrayList<Object>(list.size());
		for (Sample s : list) {
			objList.add(s.getIndex(), s.getData());
		}
		return objList;
	}

	public List<Object> getObjectData() {
		return toObjectList(datalist);
	}

	public void openStatisticsView(String secondaryID) {
		try {
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			sView = (StatisticsView) window.getActivePage().showView(StatisticsView.ID, secondaryID, IWorkbenchPage.VIEW_ACTIVATE);
			sView.addDisposeListener(disposeListener);
			updateStatisticsView();
		} catch (PartInitException e) {
			e.fillInStackTrace();
			DataListPlugin.getDefault().getLog().log(new Status(Status.WARNING, DataListPlugin.PLUGIN_ID, "Problem initializing part.", e));
		}
	}

	public void setList(List<Sample> newList, int dim, int samples) {
		if (datalist != null) {
			datalist.clear();
			datalist = null;
		}
		datalist = new ArrayList<Sample>();
		datalist.addAll(newList);
		dimensions = dim;
		numSamples = samples;
		updateStatisticsView();

	}

	private void updateStatisticsView() {
		if (sView != null) {
			sView.setInput(toObjectList(datalist), dimensions, type);
			Display.getCurrent().update();
		}
	}

	public void clear() {
		dimensions = 0;
		numSamples = 0;
		currentIndex = 0;
		if (datalist != null) {
			datalist.clear();
			datalist = null;
		}
		datalist = new ArrayList<Sample>();
	}

	public int getDimensions() {
		return dimensions;
	}

	public List<Sample> getList() {
		return datalist;
	}

	public void setProperties(int dimensions, int samples) {
		setDimensions(dimensions);
		setSize(samples);
	}

	public void setDimensions(int dimensions) {
		this.dimensions = dimensions;
	}

	public int getSize() {
		return numSamples;
	}

	public void setSize(int i) {
		this.numSamples = i;
		if (numSamples == datalist.size()) {
			fireListIsFull();
		}
	}

	public void setSelectedIndex(int i) {
		if (i < dimensions) {
			currentIndex = i;
			sView.setIndex(currentIndex);
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	private static < T > void addOrSet(List<T> list, int max, int i, T e) {
		if (list.size() > i) {
			list.set(i, e);
		} else if (max < 1 || list.size() < max) {
			list.add(i, e);
		}
	}

	/**
	 * Returns a
	 * @return
	 */
	public int getSelectedIndex() {
		return currentIndex;
	}
}
