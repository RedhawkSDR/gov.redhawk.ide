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
import gov.redhawk.ide.sdr.NodesContainer;
import gov.redhawk.ide.sdr.NodesSubContainer;
import gov.redhawk.ide.sdr.SdrFactory;
import gov.redhawk.ide.sdr.ServicesContainer;
import gov.redhawk.ide.sdr.SharedLibrariesContainer;
import gov.redhawk.ide.sdr.WaveformsContainer;
import gov.redhawk.ide.sdr.WaveformsSubContainer;
import gov.redhawk.ide.sdr.ui.SdrContentProvider;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import gov.redhawk.model.sca.ScaDomainManagerRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
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
		if (element instanceof ComponentsSubContainer || element instanceof WaveformsSubContainer || element instanceof NodesSubContainer) {
			List<Object> children = new ArrayList<Object>();

			// Add the appropriate sub-container children, which should already contain any spds, sads, or dcds.
			if (element instanceof ComponentsSubContainer) {
				children.addAll(((ComponentsSubContainer) element).getSubContainers());
			} else if (element instanceof WaveformsSubContainer) {
				children.addAll(((WaveformsSubContainer) element).getSubContainers());
			} else if (element instanceof NodesSubContainer) {
				children.addAll(((NodesSubContainer) element).getSubContainers());
			}

			children.addAll(Arrays.asList(super.getChildren(parentElement)));
			return children.toArray();
		} else if (element instanceof EObject) {
			List<Object> children = createNameSpaceStructure(element, Arrays.asList(super.getChildren(parentElement)));
			return children.toArray(new Object[0]);
		} else {
			return Collections.EMPTY_LIST.toArray();
		}
	}

	/**
	 * Searches the list of elements and properly nests any namespaced softpackages
	 * @param container
	 */
	private List<Object> createNameSpaceStructure(Object container, List<Object> children) {

		List<Object> retList = new ArrayList<Object>();

		if (container instanceof ComponentsContainer || container instanceof DevicesContainer || container instanceof ServicesContainer
			|| container instanceof SharedLibrariesContainer) {
			List<ComponentsSubContainer> nameSpacedContainers = new ArrayList<ComponentsSubContainer>();
			for (Object child : children) {
				if (child instanceof SoftPkg) {
					SoftPkg component = (SoftPkg) child;
					if (component.getName().contains(".")) {
						createSubContainers(component, nameSpacedContainers);
					} else {
						retList.add(child);
					}
				}
			}
			retList.addAll(nameSpacedContainers);
		} else if (container instanceof WaveformsContainer) {
			List<WaveformsSubContainer> nameSpacedContainers = new ArrayList<WaveformsSubContainer>();
			for (Object child : children) {
				if (child instanceof SoftwareAssembly) {
					SoftwareAssembly sad = (SoftwareAssembly) child;
					if (sad.getName().contains(".")) {
						createSubContainers(sad, nameSpacedContainers);
					} else {
						retList.add(child);
					}
				}
			}
			retList.addAll(nameSpacedContainers);
		} else if (container instanceof NodesContainer) {
			List<NodesSubContainer> nameSpacedContainers = new ArrayList<NodesSubContainer>();
			for (Object child : children) {
				if (child instanceof DeviceConfiguration) {
					DeviceConfiguration dcd = (DeviceConfiguration) child;
					if (dcd.getName().contains(".")) {
						createSubContainers(dcd, nameSpacedContainers);
					} else {
						retList.add(child);
					}
				}
			}
			retList.addAll(nameSpacedContainers);
		} else {
			return children;
		}

		return retList;
	}

	/**
	 * Utility method to place a component in the proper tree hierarchy based on its namespace. Creates any containers
	 * needed for the namespace.
	 * @param component The component to add
	 * @param topLevelContainers The list of top-level namespace containers
	 */
	private void createSubContainers(SoftPkg component, List<ComponentsSubContainer> topLevelContainers) {
		// Namespaces are all segments of the name except the last (the 'basename')
		String[] names = component.getName().split("\\.");
		int numContainers = names.length - 1;
		List<ComponentsSubContainer> containerList = new ArrayList<ComponentsSubContainer>();

		// Create containers for each namespace
		for (int i = 0; i < numContainers; i++) {
			ComponentsSubContainer container = null;

			if (i == 0) {
				// Top-level namespace -- check if a container already exists
				for (ComponentsSubContainer topLevelContainer : topLevelContainers) {
					if (names[i].equals(topLevelContainer.getContainerName())) {
						container = topLevelContainer;
						break;
					}
				}

				// Create a new container if we couldn't find an existing one
				if (container == null) {
					container = SdrFactory.eINSTANCE.createComponentsSubContainer();
					container.setContainerName(names[i]);
					topLevelContainers.add(container);
				}
			} else {
				// Namespace OTHER than top-level -- check if its parent already has a container for the namespace
				ComponentsSubContainer parent = containerList.get(i - 1);
				for (ComponentsSubContainer child : parent.getSubContainers()) {
					if (names[i].equals(child.getContainerName())) {
						container = child;
						break;
					}
				}

				// Create a new container if we couldn't find an existing one
				if (container == null) {
					container = SdrFactory.eINSTANCE.createComponentsSubContainer();
					container.setContainerName(names[i]);
					parent.getSubContainers().add(container);
				}
			}

			// Keep track of the containers for this component
			containerList.add(container);
		}

		// Add the component to the final container
		containerList.get(numContainers - 1).getComponents().add(component);
	}

	/**
	 * Utility method to place a waveform in the proper tree hierarchy based on its namespace. Creates any containers
	 * needed for the namespace.
	 * @param sad The waveform to add
	 * @param topLevelContainers The list of top-level namespace containers
	 */
	private void createSubContainers(SoftwareAssembly sad, List<WaveformsSubContainer> topLevelContainers) {
		// Namespaces are all segments of the name except the last (the 'basename')
		String[] names = sad.getName().split("\\.");
		int numContainers = names.length - 1;
		List<WaveformsSubContainer> containerList = new ArrayList<WaveformsSubContainer>();

		// Create containers for each namespace
		for (int i = 0; i < numContainers; i++) {
			WaveformsSubContainer container = null;

			if (i == 0) {
				// Top-level namespace -- check if a container already exists
				for (WaveformsSubContainer topLevelContainer : topLevelContainers) {
					if (names[i].equals(topLevelContainer.getContainerName())) {
						container = topLevelContainer;
						break;
					}
				}

				// Create a new container if we couldn't find an existing one
				if (container == null) {
					container = SdrFactory.eINSTANCE.createWaveformsSubContainer();
					container.setContainerName(names[i]);
					topLevelContainers.add(container);
				}
			} else {
				// Namespace OTHER than top-level -- check if its parent already has a container for the namespace
				WaveformsSubContainer parent = containerList.get(i - 1);
				for (WaveformsSubContainer child : parent.getSubContainers()) {
					if (names[i].equals(child.getContainerName())) {
						container = child;
						break;
					}
				}

				// Create a new container if we couldn't find an existing one
				if (container == null) {
					container = SdrFactory.eINSTANCE.createWaveformsSubContainer();
					container.setContainerName(names[i]);
					parent.getSubContainers().add(container);
				}
			}

			// Keep track of the containers for this waveform
			containerList.add(container);
		}

		// Add the waveform to the final container
		containerList.get(numContainers - 1).getWaveforms().add(sad);
	}

	/**
	 * Utility method to place a node in the proper tree hierarchy based on its namespace. Creates any containers
	 * needed for the namespace.
	 * @param dcd The node to add
	 * @param topLevelContainers The list of top-level namespace containers
	 */
	private void createSubContainers(DeviceConfiguration dcd, List<NodesSubContainer> topLevelContainers) {
		// Namespaces are all segments of the name except the last (the 'basename')
		String[] names = dcd.getName().split("\\.");
		int numContainers = names.length - 1;
		List<NodesSubContainer> containerList = new ArrayList<NodesSubContainer>();

		// Create containers for each namespace
		for (int i = 0; i < numContainers; i++) {
			NodesSubContainer container = null;

			if (i == 0) {
				// Top-level namespace -- check if a container already exists
				for (NodesSubContainer topLevelContainer : topLevelContainers) {
					if (names[i].equals(topLevelContainer.getContainerName())) {
						container = topLevelContainer;
						break;
					}
				}

				// Create a new container if we couldn't find an existing one
				if (container == null) {
					container = SdrFactory.eINSTANCE.createNodesSubContainer();
					container.setContainerName(names[i]);
					topLevelContainers.add(container);
				}
			} else {
				// Namespace OTHER than top-level -- check if its parent already has a container for the namespace
				NodesSubContainer parent = containerList.get(i - 1);
				for (NodesSubContainer child : parent.getSubContainers()) {
					if (names[i].equals(child.getContainerName())) {
						container = child;
						break;
					}
				}

				// Create a new container if we couldn't find an existing one
				if (container == null) {
					container = SdrFactory.eINSTANCE.createNodesSubContainer();
					container.setContainerName(names[i]);
					parent.getSubContainers().add(container);
				}
			}

			// Keep track of the containers for this node
			containerList.add(container);
		}

		// Add the node to the final container
		containerList.get(numContainers - 1).getNodes().add(dcd);
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
