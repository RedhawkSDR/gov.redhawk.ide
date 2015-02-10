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

import gov.redhawk.ide.debug.LocalSca;
import gov.redhawk.ide.debug.LocalScaDeviceManager;
import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.debug.NotifyingNamingContext;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.internal.ScaDebugInstance;
import gov.redhawk.model.sca.CorbaObjWrapper;
import gov.redhawk.model.sca.services.AbstractScaObjectLocator;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * 
 */
@SuppressWarnings("restriction")
public class LocalScaObjectLocator extends AbstractScaObjectLocator {

	private Class< ? >[] findableClasses = new Class< ? >[] {LocalScaWaveform.class, LocalScaDeviceManager.class};
	
	@Override
	public < T extends CorbaObjWrapper< ? >> T findEObject(final Class<T> type, final String ior) {
		for (Class< ? > testClass: findableClasses) {
			if (type.isAssignableFrom(testClass) && ScaDebugInstance.SANDBOX_WF_REF.equals(ior)) {
				ScaDebugPlugin instance = ScaDebugPlugin.getInstance();
				if (instance != null) {
					LocalSca localsca = instance.getLocalSca();
					if (localsca != null) {
						if (type.isAssignableFrom(LocalScaWaveform.class)) {
							return type.cast(localsca.getSandboxWaveform());
						}
						if (type.isAssignableFrom(LocalScaDeviceManager.class)) {
							return type.cast(localsca.getSandboxDeviceManager());
						}
					}
				}
			}
		}
		return super.findEObject(type, ior);
	}

	@Override
	protected boolean shouldPrune(final EObject obj) {
		if (super.shouldPrune(obj) || obj instanceof NotifyingNamingContext) {
			return true;
		}
		return false;
	}

	@Override
	protected TreeIterator<EObject> getContentIterator(final Class< ? extends CorbaObjWrapper< ? >> type, final String ior) {
		final LocalSca localSca = ScaDebugPlugin.getInstance().getLocalSca();
		final TreeIterator<EObject> iterator = EcoreUtil.getAllContents(localSca, false);
		return iterator;
	}
}
