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

import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.model.sca.ScaDomainManagerRegistry;
import gov.redhawk.model.sca.provider.ScaItemProviderAdapterFactory;
import gov.redhawk.sca.ui.ScaContentProvider;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;

/**
 * 
 */
public class ScaDebugContentProvider extends ScaContentProvider {
	private static final Object[] EMPTY_OBJECTS = new Object[0];
	
	public ScaDebugContentProvider() {
		super(ScaDebugContentProvider.createAdapterFactory());
	}

	protected static AdapterFactory createAdapterFactory() {
		final ComposedAdapterFactory factory = new ComposedAdapterFactory();
		factory.addAdapterFactory(new ScaItemProviderAdapterFactory());
		factory.addAdapterFactory(ScaContentProvider.createAdapterFactory());
		return factory;
	}

	@Override
	public Object[] getElements(final Object object) {
		final ScaDebugPlugin activator = ScaDebugPlugin.getInstance();
		if (activator == null) {
			return EMPTY_OBJECTS;
		}
		if (object instanceof IWorkspaceRoot) {
			return new Object[] { activator.getLocalSca() };
		} else if (object instanceof ScaDomainManagerRegistry) {
			return new Object[] { activator.getLocalSca() };
		} else {
			return EMPTY_OBJECTS;
		}
	}
}
