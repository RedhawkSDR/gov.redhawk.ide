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
package gov.redhawk.ide.debug.internal.ui;

import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaDevice;
import gov.redhawk.model.sca.ScaService;
import gov.redhawk.model.sca.ScaWaveform;

import java.text.Collator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * 
 */
public class DebugViewerSorter extends ViewerSorter {

	/**
	 * 
	 */
	public DebugViewerSorter() {

	}

	/**
	 * @param collator
	 */
	public DebugViewerSorter(final Collator collator) {
		super(collator);
	}

	@Override
	public int compare(final Viewer viewer, final Object e1, final Object e2) {
		if (e1 instanceof ScaService) {
			return compareService(viewer, (ScaService) e1, e2);
		} else if (e2 instanceof ScaService) {
			return -compareService(viewer, (ScaService) e2, e1);
		} else if (e1 instanceof ScaDevice< ? >) {
			return compareDevice(viewer, (ScaDevice< ? >) e1, e2);
		} else if (e2 instanceof ScaDevice< ? >) {
			return -compareDevice(viewer, (ScaDevice< ? >) e2, e1);
		} else if (e1 instanceof ScaComponent) {
			return compareComponent(viewer, (ScaComponent) e1, e2);
		} else if (e2 instanceof ScaComponent) {
			return -compareComponent(viewer, (ScaComponent) e2, e1);
		} else if (e1 instanceof ScaWaveform) {
			return compareWaveform(viewer, (ScaWaveform) e1, e2);
		} else if (e2 instanceof ScaWaveform) {
			return -compareWaveform(viewer, (ScaWaveform) e2, e1);
		}
		return super.compare(viewer, e1, e2);
	}

	private int compareService(final Viewer viewer, final ScaService e1, final Object e2) {
		if (e2 instanceof ScaService) {
			return super.compare(viewer, e1, e2);
		} else {
			return -1;
		}
	}

	private int compareWaveform(final Viewer viewer, final ScaWaveform e1, final Object e2) {
		if (e2 instanceof ScaWaveform) {
			return super.compare(viewer, e1, e2);
		} else {
			return -1;
		}
	}

	private int compareComponent(final Viewer viewer, final ScaComponent e1, final Object e2) {
		if (e2 instanceof ScaComponent) {
			return super.compare(viewer, e1, e2);
		} else {
			return -1;
		}
	}

	private int compareDevice(final Viewer viewer, final ScaDevice< ? > e1, final Object e2) {
		if (e2 instanceof ScaDevice< ? >) {
			return super.compare(viewer, e1, e2);
		} else {
			return -1;
		}
	}

}
