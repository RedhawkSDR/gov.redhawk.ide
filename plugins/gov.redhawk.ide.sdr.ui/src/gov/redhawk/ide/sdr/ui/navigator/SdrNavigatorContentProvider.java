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
package gov.redhawk.ide.sdr.ui.navigator;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.ICommonContentProvider;

import gov.redhawk.ide.sdr.ui.SdrContentProvider;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import gov.redhawk.model.sca.ScaDomainManagerRegistry;
import mil.jpeojtrs.sca.spd.SoftPkg;

/**
 * @since 4.2
 */
public class SdrNavigatorContentProvider extends SdrContentProvider implements ICommonContentProvider {

	private static final Object[] EMPTY_OBJECTS = new Object[0];

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
			return this.getChildren(inputElement);
		}
	}

	@Override
	public boolean hasChildren(final Object object) {
		if (object instanceof SoftPkg) {
			return false;
		}
		return true;
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
