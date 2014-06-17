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

/**
 * 
 */
public class LocalScaElementFactory implements IElementFactory {

	public static final String ID = "gov.redhawk.ide.debug.ui.localSca.factory";
	
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

					}

					@Override
					public String getFactoryId() {
						return LocalScaElementFactory.ID;
					}
				};
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IAdaptable createElement(final IMemento memento) {
		return LocalScaElementFactory.getLocalScaInput();
	}

}
