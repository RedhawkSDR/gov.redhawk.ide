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

import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.sca.ui.editors.IScaContentDescriber;

import java.io.IOException;

import org.eclipse.ui.IEditorInput;

public class LocalScaChalkboardContentDescriber implements IScaContentDescriber {

	@Override
	public int describe(final Object contents) throws IOException {
		if (contents instanceof LocalScaWaveform) {
			final LocalScaWaveform waveform = (LocalScaWaveform) contents;
			if (waveform == ScaDebugPlugin.getInstance().getLocalSca().getSandboxWaveform()) {
				return IScaContentDescriber.VALID;
			}
		}
		return IScaContentDescriber.INVALID;
	}

	@Override
	public IEditorInput getEditorInput(final Object contents) {
		if (contents == ScaDebugPlugin.getInstance().getLocalSca().getSandboxWaveform()) {
			return LocalScaElementFactory.getLocalScaInput();
		}
		return null;
	}
}
