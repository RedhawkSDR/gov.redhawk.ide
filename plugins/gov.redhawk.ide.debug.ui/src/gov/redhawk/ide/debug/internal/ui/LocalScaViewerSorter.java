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

import gov.redhawk.ide.debug.LocalFileManager;
import gov.redhawk.ide.debug.LocalSca;
import gov.redhawk.ide.debug.LocalScaDeviceManager;
import gov.redhawk.ide.debug.LocalScaWaveform;

import java.text.Collator;

import mil.jpeojtrs.sca.util.ScaEcoreUtils;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * This class still extends {@link ViewerSorter} because of https://bugs.eclipse.org/bugs/show_bug.cgi?id=484248.
 */
@SuppressWarnings("deprecation")
public class LocalScaViewerSorter extends ViewerSorter {

	public LocalScaViewerSorter() {
	}

	/**
	 * @param collator
	 */
	public LocalScaViewerSorter(Collator collator) {
		super(collator);
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if (e1 instanceof LocalScaWaveform) {
			if (e2 instanceof LocalScaWaveform) {
				if (e1 == e2) {
					return 0;
				}
				LocalScaWaveform l1 = (LocalScaWaveform) e1;
				LocalScaWaveform l2 = (LocalScaWaveform) e2;
				LocalSca localSca = ScaEcoreUtils.getEContainerOfType(l1, LocalSca.class);
				if (localSca == null) {
					localSca = ScaEcoreUtils.getEContainerOfType(l2, LocalSca.class);
				}
				if (localSca != null) {
					if (l1 == localSca.getSandboxWaveform()) {
						return -1;
					} else if (l2 == localSca.getSandboxWaveform()) {
						return 1;
					}
				}
			} else if (e2 instanceof LocalScaDeviceManager) {
				return -1;
			} else if (e2 instanceof LocalFileManager) {
				return -1;
			}
		} else if (e1 instanceof LocalScaDeviceManager) {
			if (e2 instanceof LocalScaWaveform) {
				return 1;
			} else if (e2 instanceof LocalFileManager) {
				return 1;
			}
		} else if (e1 instanceof LocalFileManager) {
			if (e2 instanceof LocalScaWaveform) {
				return 1;
			} else if (e2 instanceof LocalScaDeviceManager) {
				return -1;
			}
		}
		return super.compare(viewer, e1, e2);
	}

}
