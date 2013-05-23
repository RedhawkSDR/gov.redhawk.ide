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
package gov.redhawk.ide.debug.internal.adapters;

import gov.redhawk.ide.debug.LocalLaunch;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.sca.sad.diagram.edit.parts.SadComponentInstantiationEditPart;
import gov.redhawk.sca.util.PluginUtil;

import org.eclipse.core.runtime.IAdapterFactory;

/**
 * 
 */
public class LocalSadComponentInstantiationEditPartAdapterFactory implements IAdapterFactory {

	private static final Class< ? >[] CLASS_LIST = new Class< ? >[] {
		LocalLaunch.class
	};

	/**
	 * {@inheritDoc}
	 */
	public Object getAdapter(final Object adaptableObject, @SuppressWarnings("rawtypes") final Class adapterType) {
		if (adapterType == LocalLaunch.class) {
			if (adaptableObject instanceof SadComponentInstantiationEditPart) {
				final ScaComponent comp = PluginUtil.adapt(ScaComponent.class, adaptableObject);
				if (comp instanceof LocalLaunch) {
					return comp;
				}
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Class < ? > [] getAdapterList() {
		return LocalSadComponentInstantiationEditPartAdapterFactory.CLASS_LIST;
	}

}
