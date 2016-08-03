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

import gov.redhawk.ide.debug.internal.ScaDebugInstance;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

public class LocalScaElementFactory implements IElementFactory {

	public static final String ID = "gov.redhawk.ide.debug.ui.localSca.factory";
	private static final String EDITOR_TYPE = "editorType";
	private static final String LOCAL_WAVEFORM = "localWaveform";
	private static final String LOCAL_DEVICE_MANAGER = "localDeviceManager";

	public static IEditorInput getLocalScaInput() {
		return new URIEditorInput(ScaDebugInstance.getLocalSandboxWaveformURI()) {
			@Override
			public boolean exists() {
				return true;
			}

			@Override
			public IPersistableElement getPersistable() {
				return new IPersistableElement() {

					@Override
					public void saveState(final IMemento memento) {
						memento.putString(EDITOR_TYPE, LOCAL_WAVEFORM);
					}

					@Override
					public String getFactoryId() {
						return LocalScaElementFactory.ID;
					}
				};
			}

			@Override
			public String getToolTipText() {
				return "Sandbox chalkboard";
			}
		};
	}

	public static IEditorInput getLocalDeviceManagerInput() {
		return new URIEditorInput(ScaDebugInstance.getLocalSandboxDeviceManagerURI()) {
			@Override
			public boolean exists() {
				return true;
			}

			@Override
			public IPersistableElement getPersistable() {
				return new IPersistableElement() {

					@Override
					public void saveState(final IMemento memento) {
						memento.putString(EDITOR_TYPE, LOCAL_DEVICE_MANAGER);
					}

					@Override
					public String getFactoryId() {
						return LocalScaElementFactory.ID;
					}
				};
			}

			@Override
			public String getToolTipText() {
				return "Sandbox device manager";
			}
		};
	}

	@Override
	public IAdaptable createElement(final IMemento memento) {
		String editorType = memento.getString(EDITOR_TYPE);
		if (editorType.equals(LOCAL_WAVEFORM)) {
			return LocalScaElementFactory.getLocalScaInput();
		} else {
			return LocalScaElementFactory.getLocalDeviceManagerInput();
		}
	}

}
