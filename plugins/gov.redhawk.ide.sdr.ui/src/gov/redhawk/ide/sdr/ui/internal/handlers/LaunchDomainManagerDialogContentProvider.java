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
package gov.redhawk.ide.sdr.ui.internal.handlers;

import gov.redhawk.ide.sdr.SdrRoot;

import java.util.Collections;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * 
 */
public class LaunchDomainManagerDialogContentProvider implements ITreeContentProvider {

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public Object[] getElements(final Object inputElement) {
		if (inputElement instanceof SdrRoot) {
			return ((SdrRoot) inputElement).getNodesContainer().getNodes().toArray();
		}
		return Collections.emptyList().toArray();
	}

	@Override
	public boolean hasChildren(final Object element) {
		return false;
	}

	@Override
	public Object getParent(final Object element) {
		return null;
	}

	@Override
	public Object[] getChildren(final Object parentElement) {
		return Collections.emptyList().toArray();
	}
}
