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
package gov.redhawk.ide.dcd.internal.ui;

import java.util.ArrayList;

import mil.jpeojtrs.sca.dcd.DcdComponentPlacement;
import mil.jpeojtrs.sca.dcd.DcdPartitioning;
import mil.jpeojtrs.sca.partitioning.ComponentPlacement;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ComponentPlacementContentProvider implements ITreeContentProvider {
	private DcdPartitioning elements = null;

	public ComponentPlacementContentProvider() {
		super();
	}

	public void dispose() {
	}

	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
		this.elements = (DcdPartitioning) newInput;
	}

	public Object getParent(final Object element) {
		if (element instanceof DcdComponentPlacement) {
			final DcdComponentPlacement node = (DcdComponentPlacement) element;

			for (final Object comp : this.elements.getComponentPlacement()) {
				if (node.getCompositePartOfDevice() != null) {
					if (((DcdComponentPlacement) comp).getComponentInstantiation().get(0).getId().equals(node.getCompositePartOfDevice().getRefID())) {
						return comp;
					}
				}
			}
		}
		return this.elements;
	}

	public Object[] getChildren(final Object parent) {
		Object[] element = new ComponentPlacement[0];

		// Check to see if we're updating something we know about
		if (parent instanceof DcdComponentPlacement) {
			final DcdComponentPlacement bindingNode = (DcdComponentPlacement) parent;
			final String id = bindingNode.getComponentInstantiation().get(0).getId();
			final ArrayList<DcdComponentPlacement> comps = new ArrayList<DcdComponentPlacement>();

			for (final Object comp : this.elements.getComponentPlacement()) {
				final DcdComponentPlacement place = (DcdComponentPlacement) comp;
				if ((place.getCompositePartOfDevice() != null) && place.getCompositePartOfDevice().getRefID().equals(id)) {
					comps.add(place);
				}
			}

			if (comps.size() > 0) {
				element = comps.toArray();
			}

		} else if (parent == this.elements) {
			final ArrayList<DcdComponentPlacement> comps = new ArrayList<DcdComponentPlacement>();

			for (final Object comp : this.elements.getComponentPlacement()) {
				final DcdComponentPlacement place = (DcdComponentPlacement) comp;
				if (place.getCompositePartOfDevice() == null) {
					comps.add(place);
				}
			}

			if (comps.size() > 0) {
				element = comps.toArray();
			}
		}

		return element;
	}

	public boolean hasChildren(final Object parent) {
		if (parent instanceof ComponentPlacement) {
			final DcdComponentPlacement bindingNode = (DcdComponentPlacement) parent;
			final String id = bindingNode.getComponentInstantiation().get(0).getId();
			final ArrayList<DcdComponentPlacement> comps = new ArrayList<DcdComponentPlacement>();

			for (final Object comp : this.elements.getComponentPlacement()) {
				final DcdComponentPlacement place = (DcdComponentPlacement) comp;
				if ((place.getCompositePartOfDevice() != null) && place.getCompositePartOfDevice().getRefID().equals(id)) {
					comps.add(place);
				}
			}

			return comps.size() > 0;

		} else if (parent == this.elements) {
			return this.elements.getComponentPlacement().size() > 0;
		}

		return false;
	}

	public Object[] getElements(final Object inputElement) {
		Object[] element = new ComponentPlacement[0];

		// Check to see if we're updating something we know about
		if (inputElement instanceof ComponentPlacement) {
			final DcdComponentPlacement bindingNode = (DcdComponentPlacement) inputElement;
			final String id = bindingNode.getComponentInstantiation().get(0).getId();
			final ArrayList<DcdComponentPlacement> comps = new ArrayList<DcdComponentPlacement>();

			for (final Object comp : this.elements.getComponentPlacement()) {
				final DcdComponentPlacement place = (DcdComponentPlacement) comp;
				if ((place.getCompositePartOfDevice() != null) && place.getCompositePartOfDevice().getRefID().equals(id)) {
					comps.add(place);
				}
			}

			if (comps.size() > 0) {
				element = comps.toArray();
			}

		} else if (inputElement == this.elements) {
			final ArrayList<DcdComponentPlacement> comps = new ArrayList<DcdComponentPlacement>();

			for (final Object comp : this.elements.getComponentPlacement()) {
				final DcdComponentPlacement place = (DcdComponentPlacement) comp;
				if (place.getCompositePartOfDevice() == null) {
					comps.add(place);
				}
			}

			if (comps.size() > 0) {
				element = comps.toArray();
			}
		}

		return element;
	}

}
