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
package gov.redhawk.ide.debug.ui.tabs;

import org.eclipse.core.runtime.IPath;

import gov.redhawk.ide.sdr.preferences.IdeSdrPreferences;
import mil.jpeojtrs.sca.sad.SadPackage;

/**
 * 
 */
public class LocalWaveformMainTab extends AbstractMainTab {

	@Override
	protected String getProfileExtension() {
		return SadPackage.FILE_EXTENSION;
	}

	@Override
	protected IPath getSdrDirectory() {
		IPath domPath = IdeSdrPreferences.getTargetSdrDomPath();
		return (domPath == null) ? null : domPath.append("waveforms");
	}

	/**
	 * @since 3.0
	 */
	@Override
	protected String getLocationLabel() {
		return "Location of Software Assembly File (sad.xml)";
	}

}
