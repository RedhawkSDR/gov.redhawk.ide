/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.debug.internal.ui;

import gov.redhawk.ide.debug.LocalScaDeviceManager;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.sca.ui.editors.IScaContentDescriber;

import java.io.IOException;

import org.eclipse.ui.IEditorInput;

public class LocalScaDeviceManagerContentDescriber implements IScaContentDescriber {

	@Override
	public int describe(Object contents) throws IOException {
		if (contents instanceof LocalScaDeviceManager) {
			return IScaContentDescriber.VALID;
		}
		return IScaContentDescriber.INVALID;
	}

	@Override
	public IEditorInput getEditorInput(Object contents) {
		if (contents == ScaDebugPlugin.getInstance().getLocalSca().getSandboxDeviceManager()) {
			return LocalScaElementFactory.getLocalDeviceManagerInput();
		} 
		
		return null;
	}

}
