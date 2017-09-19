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
package gov.redhawk.ide.graphiti.dcd.internal.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;
import mil.jpeojtrs.sca.dcd.DcdComponentPlacement;
import mil.jpeojtrs.sca.dcd.DcdPartitioning;

/**
 * @since 1.1
 */
public class DcdComponentContentProvider implements ITreeContentProvider {
	private DcdPartitioning dcdPartitioning = null;

	public DcdComponentContentProvider() {
		super();
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
		this.dcdPartitioning = (DcdPartitioning) newInput;
	}

	@Override
	public Object getParent(final Object element) {
		if (element instanceof DcdComponentInstantiation) {
			DcdComponentInstantiation comp = (DcdComponentInstantiation) element;
			DcdComponentPlacement elementPlacement = (DcdComponentPlacement) comp.getPlacement();

			if (elementPlacement == null || elementPlacement.getCompositePartOfDevice() == null) {
				return this.dcdPartitioning;
			}

			for (final DcdComponentPlacement placement : this.dcdPartitioning.getComponentPlacement()) {
				for (DcdComponentInstantiation ci : placement.getComponentInstantiation()) {
					if (ci.getId().equals(elementPlacement.getCompositePartOfDevice().getRefID())) {
						return ci;
					}
				}
			}
		}
		return this.dcdPartitioning;
	}

	@Override
	public Object[] getChildren(final Object parent) {
		List<Object> elements = new ArrayList<Object>();

		if (parent instanceof DcdComponentInstantiation) {
			DcdComponentInstantiation parentComp = (DcdComponentInstantiation) parent;
			String parentId = parentComp.getId();

			for (final DcdComponentPlacement placement : this.dcdPartitioning.getComponentPlacement()) {
				if (placement.getCompositePartOfDevice() != null && placement.getCompositePartOfDevice().getRefID().equals(parentId)) {
					for (DcdComponentInstantiation ci : placement.getComponentInstantiation()) {
						elements.add(ci);
					}
				}
			}
		} else if (parent == this.dcdPartitioning) {
			for (final DcdComponentPlacement placement : this.dcdPartitioning.getComponentPlacement()) {
				if (placement.getCompositePartOfDevice() == null) {
					for (DcdComponentInstantiation ci : placement.getComponentInstantiation()) {
						elements.add(ci);
					}
				}
			}
		}

		return elements.toArray(new Object[0]);
	}

	@Override
	public boolean hasChildren(final Object parent) {
		if (parent instanceof DcdComponentInstantiation) {
			final DcdComponentInstantiation parentComp = (DcdComponentInstantiation) parent;
			final String parentId = parentComp.getId();

			for (DcdComponentPlacement placement : this.dcdPartitioning.getComponentPlacement()) {
				if (placement.getCompositePartOfDevice() != null && parentId.equals(placement.getCompositePartOfDevice().getRefID())) {
					return true;
				}
			}
		} else if (parent == this.dcdPartitioning) {
			return this.dcdPartitioning.getComponentPlacement().size() > 0;
		}
		return false;
	}

	@Override
	public Object[] getElements(final Object inputElement) {
		List<Object> elements = new ArrayList<Object>();

		if (inputElement instanceof DcdComponentInstantiation) {
			final DcdComponentInstantiation parentCI = (DcdComponentInstantiation) inputElement;
			final String parentId = parentCI.getId();
			for (final DcdComponentPlacement placement : this.dcdPartitioning.getComponentPlacement()) {
				if ((placement.getCompositePartOfDevice() != null) && placement.getCompositePartOfDevice().getRefID().equals(parentId)) {
					for (DcdComponentInstantiation comp : placement.getComponentInstantiation()) {
						elements.add(comp);
					}
				}
			}
		} else if (inputElement == this.dcdPartitioning) {
			for (final DcdComponentPlacement placement : this.dcdPartitioning.getComponentPlacement()) {
				if (placement.getCompositePartOfDevice() == null) {
					for (DcdComponentInstantiation comp : placement.getComponentInstantiation()) {
						elements.add(comp);
					}
				}
			}
		}

		return elements.toArray(new Object[0]);
	}

}
