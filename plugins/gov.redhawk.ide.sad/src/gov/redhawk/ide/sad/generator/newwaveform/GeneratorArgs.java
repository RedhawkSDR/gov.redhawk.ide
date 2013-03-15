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
package gov.redhawk.ide.sad.generator.newwaveform;

import gov.redhawk.ide.codegen.args.GeneratorArgsBase;
import mil.jpeojtrs.sca.spd.SoftPkg;

/**
 * The properties that can be set for the New Waveform generator.
 */
public class GeneratorArgs extends GeneratorArgsBase {
	private SoftPkg assemblyController;
	private String waveformName;
	private String waveformId;

	public void setAssemblyConroller(final SoftPkg assemblyController) {
		this.assemblyController = assemblyController;
	}

	public SoftPkg getAssemblyController() {
		return this.assemblyController;
	}

	public String getWaveformName() {
	    return waveformName;
    }

	public void setWaveformName(String waveformName) {
	    this.waveformName = waveformName;
    }

	public String getWaveformId() {
	    return waveformId;
    }

	public void setWaveformId(String waveformId) {
	    this.waveformId = waveformId;
    }
}
