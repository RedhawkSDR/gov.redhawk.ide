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
			return EMPTY_OBJECTS;
		}
		if (inputElement instanceof IWorkspaceRoot) {
			return new Object[] { activator.getTargetSdrRoot() };
		} else if (inputElement instanceof ScaDomainManagerRegistry) {
			return new Object[] { activator.getTargetSdrRoot() };
		} else {
			return EMPTY_OBJECTS;
		}
	}

	@Override
	public boolean hasChildren(final Object object) {
		if (object instanceof IWorkspaceRoot) {
			return true;
		} else {
			return super.hasChildren(object);
		}
	}

	public void init(final ICommonContentExtensionSite config) {
		// Nothing to do
	}

	public void restoreState(final IMemento memento) {
		// Nothing to do
	}

	public void saveState(final IMemento memento) {
		// Nothing to do
	}

}
