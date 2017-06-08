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
package gov.redhawk.ide.graphiti.sad.internal.ui.editor.pages;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import mil.jpeojtrs.sca.sad.HostCollocation;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadComponentPlacement;
import mil.jpeojtrs.sca.sad.SadPartitioning;

public class SadComponentContentProvider implements ITreeContentProvider {
	private SadPartitioning elements = null;

	public SadComponentContentProvider() {
		super();
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
		this.elements = (SadPartitioning) newInput;
	}

	@Override
	public Object getParent(final Object element) {
		return this.elements;
	}

	@Override
	public Object[] getChildren(final Object parent) {
		return new Object[0];
	}

	@Override
	public boolean hasChildren(final Object parent) {
		return false;
	}

	@Override
	public Object[] getElements(final Object inputElement) {
		if (inputElement == this.elements) {
			final ArrayList<SadComponentInstantiation> comps = new ArrayList<>();

			for (final SadComponentPlacement placement : this.elements.getComponentPlacement()) {
				for (SadComponentInstantiation ci : placement.getComponentInstantiation()) {
					comps.add(ci);
				}
			}
			for (HostCollocation hc : this.elements.getHostCollocation()) {
				for (SadComponentPlacement placement : hc.getComponentPlacement()) {
					for (SadComponentInstantiation ci : placement.getComponentInstantiation()) {
						comps.add(ci);
					}
				}
			}

			if (comps.size() > 0) {
				return comps.toArray();
			}
		}

		return new Object[0];
	}

}
