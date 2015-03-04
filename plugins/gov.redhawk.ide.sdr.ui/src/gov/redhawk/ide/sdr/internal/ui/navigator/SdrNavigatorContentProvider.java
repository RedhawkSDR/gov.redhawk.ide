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

import gov.redhawk.ide.sdr.ComponentsContainer;
import gov.redhawk.ide.sdr.ComponentsSubContainer;
import gov.redhawk.ide.sdr.DevicesContainer;
import gov.redhawk.ide.sdr.SdrFactory;
import gov.redhawk.ide.sdr.ServicesContainer;
import gov.redhawk.ide.sdr.ui.SdrContentProvider;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import gov.redhawk.model.sca.ScaDomainManagerRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import mil.jpeojtrs.sca.spd.SoftPkg;

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
		if (element instanceof ComponentsContainer || element instanceof DevicesContainer || element instanceof ServicesContainer) {
			List<Object> children = createNameSpaceStructure(Arrays.asList(super.getChildren(parentElement)));
			return children.toArray(new Object[0]);
		} else if (element instanceof ComponentsSubContainer) {
			ComponentsSubContainer container = (ComponentsSubContainer) element;
			List<Object> children = new ArrayList<Object>();
			children.addAll(Arrays.asList(super.getChildren(parentElement)));
			children.addAll(container.getSubContainers());
			return children.toArray();
		} else if (element instanceof EObject) {
			return super.getChildren(parentElement);
		} else {
			return Collections.EMPTY_LIST.toArray();
		}
	}

	/**
	 * Searches the list of elements and properly nests any namespaced softpackages
	 */
	private List<Object> createNameSpaceStructure(List<Object> children) {
		List<Object> retList = new ArrayList<Object>();
		List<ComponentsSubContainer> nameSpacedContainers = new ArrayList<ComponentsSubContainer>();

		for (Object child : children) {
			if (child instanceof SoftPkg) {
				SoftPkg component = (SoftPkg) child;
				if (component.getName().contains(".")) {
					ComponentsSubContainer newSubContainer = createSubContainers(component, nameSpacedContainers);
					if (newSubContainer != null) {
						nameSpacedContainers.add(newSubContainer);
					}
				} else {
					retList.add(child);
				}
			}
		}

		retList.addAll(nameSpacedContainers);

		return retList;
	}

	/**
	 * Utility method used for namespace structure, including reusing existing componentSubContainers
	 */
	private ComponentsSubContainer createSubContainers(SoftPkg component, List<ComponentsSubContainer> nameSpacedContainers) {
		List<ComponentsSubContainer> containerList = new ArrayList<ComponentsSubContainer>();
		boolean alreadyExists = false;
		String[] names = component.getName().split("\\.");
		int numOfContainers = names.length - 1;

		// Create a subContainer for all names but the last one (which is used for the component itself)
		for (int i = 0; i < (numOfContainers); i++) {
			ComponentsSubContainer container = SdrFactory.eINSTANCE.createComponentsSubContainer();
			container.setContainerName(names[i]);

			// For the top level name, check and see if that subContainer already exists
			if (i == 0) {
				for (ComponentsSubContainer c : nameSpacedContainers) {
					if (names[i].equals(c.getContainerName())) {
						container = c;
						alreadyExists = true;
						break;
					}

				}
			}

			// Make sure all containers beyond the first are properly nested
			if (i != 0) {
				ComponentsSubContainer parent = containerList.get(i - 1);

				// If there are no other subContainers, then add this one
				if (parent.getSubContainers().isEmpty()) {
					parent.getSubContainers().add(container);
				}

				// If there are already subContainers, see if the one we want already exists
				for (ComponentsSubContainer c : parent.getSubContainers()) {
					if (names[i].equals(c.getContainerName())) {
						container = c;
						break;
					}

					parent.getSubContainers().add(container);
				}

				// Add the component to the final subContainer
				if (i == (numOfContainers - 1)) {
					container.getComponents().add(component);
				}
			}

			containerList.add(container);
		}

		if (!alreadyExists) {
			return containerList.get(0);
		} else {
			return null;
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
