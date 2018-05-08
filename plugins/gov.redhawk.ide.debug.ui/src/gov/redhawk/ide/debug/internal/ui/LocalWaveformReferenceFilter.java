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

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.debug.ScaDebugPlugin;

/**
 * A viewer filter that excludes {@link LocalScaWaveform} without a launch (that are proxying a remote domain
 * waveform).
 */
public class LocalWaveformReferenceFilter extends ViewerFilter {

	public LocalWaveformReferenceFilter() {
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof LocalScaWaveform) {
			return ((LocalScaWaveform) element).getLaunch() != null || element == ScaDebugPlugin.getInstance().getLocalSca().getSandboxWaveform();
		}
		return true;
	}

}
