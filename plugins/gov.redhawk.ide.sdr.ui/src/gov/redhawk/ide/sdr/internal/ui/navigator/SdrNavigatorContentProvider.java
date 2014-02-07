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
package gov.redhawk.ide.sdr.internal.ui.navigator;

import gov.redhawk.eclipsecorba.library.IdlLibrary;
import gov.redhawk.ide.sdr.ComponentsContainer;
import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.WaveformsContainer;
import gov.redhawk.ide.sdr.ui.SdrContentProvider;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import gov.redhawk.model.sca.ScaDomainManagerRegistry;

import java.util.Collections;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.ICommonContentProvider;

public class SdrNavigatorContentProvider extends SdrContentProvider implements ICommonContentProvider {

	private static final Object[] EMPTY_OBJECTS = new Object[0];

	@Override
	public Object getParent(final Object object) {
		final Object retVal = super.getParent(object);
		if (!(retVal instanceof EObject)) {
			return null;
		}
		return null;
	}

	@Override
	public Object[] getChildren(final Object parentElement) {
		final Object element = AdapterFactoryEditingDomain.unwrap(parentElement);
		if (element instanceof EObject) {
			return super.getChildren(parentElement);
		} else {
			return Collections.EMPTY_LIST.toArray();
		}
	}

	@Override
	public Object[] getElements(final Object inputElement) {
		final SdrUiPlugin activator = SdrUiPlugin.getDefault();
		if (activator == null) {
			return SdrNavigatorContentProvider.EMPTY_OBJECTS;
		}
		if (inputElement instanceof IWorkspaceRoot) {
			return new Object[] { activator.getTargetSdrRoot() };
		} else if (inputElement instanceof ScaDomainManagerRegistry) {
			return new Object[] { activator.getTargetSdrRoot() };
		} else {
			return SdrNavigatorContentProvider.EMPTY_OBJECTS;
		}
	}

	@Override
	public boolean hasChildren(final Object object) {
		if (object instanceof IWorkspaceRoot) {
			return true;
		} else if (object instanceof SdrRoot) {
			return true;
		} else if (object instanceof IdlLibrary) {
			IdlLibrary library = (IdlLibrary) object;
			return !library.getDefinitions().isEmpty() || !library.getModuleDefinitions().isEmpty() || !library.getSpecifications().isEmpty();
		} else if (object instanceof ComponentsContainer) {
			ComponentsContainer cont = (ComponentsContainer) object;
			return !cont.getComponents().isEmpty();
		} else if (object instanceof WaveformsContainer) {
			WaveformsContainer container = (WaveformsContainer) object;
			return !container.getWaveforms().isEmpty();
		} else {
			return true;
		}
	}

	@Override
	public void init(final ICommonContentExtensionSite config) {
		// Nothing to do
	}

	@Override
	public void restoreState(final IMemento memento) {
		// Nothing to do
	}

	@Override
	public void saveState(final IMemento memento) {
		// Nothing to do
	}

}
