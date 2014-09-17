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
package gov.redhawk.ide.debug.internal.ui.diagram;

import gov.redhawk.ide.debug.ui.ScaDebugUiPlugin;
import gov.redhawk.model.sca.ScaWaveform;
import gov.redhawk.sca.ui.ScaFileStoreEditorInput;

import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorMatchingStrategy;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;

public class ScaChalkboardMatchingStrategy implements IEditorMatchingStrategy {

	@Override
	public boolean matches(final IEditorReference editorRef, final IEditorInput input) {
		if (input instanceof ScaFileStoreEditorInput) {
			final ScaFileStoreEditorInput inp1 = (ScaFileStoreEditorInput) input;
			try {
				final IEditorInput inp = editorRef.getEditorInput();
				if (inp instanceof ScaFileStoreEditorInput) {
					final ScaFileStoreEditorInput inp2 = (ScaFileStoreEditorInput) inp;
					final ScaWaveform sca1 = getWaveformFromEditorInput(inp1);
					final ScaWaveform sca2 = getWaveformFromEditorInput(inp2);
					return sca1 != null && sca1 == sca2;
				}
			} catch (final PartInitException e) {
				return false;
			}
		} else if (input instanceof URIEditorInput) {
			//only one instance of Chalkboard should be opened
			final URIEditorInput inp1 = (URIEditorInput) input;
			try {
				if (input.equals(editorRef.getEditorInput())) {
					return true;
				}
			} catch (PartInitException e1) {
				return false;
			}
			if (("/plugin" +  ScaDebugUiPlugin.CHALKBOARD_EDITOR_URI_PATH).equals(inp1.getURI().path())) {
				try {
					if (editorRef.getEditorInput() instanceof URIEditorInput) {
						return true;
					}
				} catch (PartInitException e) {
					return false;
				}
			}
		}
		return false;
	}

	private ScaWaveform getWaveformFromEditorInput(final ScaFileStoreEditorInput input) {
		if (input.getScaObject() instanceof ScaWaveform) {
			return (ScaWaveform) input.getScaObject();
		}
		return null;
	}
}
