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
import gov.redhawk.ide.debug.NotifyingNamingContext;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.model.sca.CorbaObjWrapper;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaPackage;
import gov.redhawk.model.sca.ScaWaveform;
import gov.redhawk.model.sca.impl.ScaWaveformImpl;
import gov.redhawk.model.sca.services.AbstractScaObjectLocator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreEList;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * 
 */
public class LocalScaObjectLocator extends AbstractScaObjectLocator {

	private class LocalWaveform extends ScaWaveformImpl {

		@Override
		public EList<ScaComponent> getComponents() {
			final List<ScaComponent> allDevices = new ArrayList<ScaComponent>();
			ScaDebugPlugin instance = ScaDebugPlugin.getInstance();
			if (instance == null) {
				return ECollections.emptyEList();
			}
			LocalSca localsca = ScaDebugPlugin.getInstance().getLocalSca();
			if (localsca != null && localsca.getSandboxWaveform() != null) {
				allDevices.addAll(localsca.getSandboxWaveform().getComponents());
			}
			return new EcoreEList.UnmodifiableEList<ScaComponent>(this, ScaPackage.eINSTANCE.getScaWaveform_Components(), allDevices.size(),
				allDevices.toArray());
		}
	};

	private final ScaWaveform localStub = new LocalWaveform();

	@Override
	public < T extends CorbaObjWrapper< ? >> T findEObject(final Class<T> type, final String ior) {
		if (type == ScaWaveform.class && ior == null) {
			return type.cast(this.localStub);
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
