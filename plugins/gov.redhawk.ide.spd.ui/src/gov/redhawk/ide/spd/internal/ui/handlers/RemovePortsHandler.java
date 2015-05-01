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
package gov.redhawk.ide.spd.internal.ui.handlers;

import gov.redhawk.ide.spd.internal.ui.editor.ComponentEditor;
import gov.redhawk.model.sca.util.ModelUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mil.jpeojtrs.sca.scd.AbstractPort;
import mil.jpeojtrs.sca.scd.InheritsInterface;
import mil.jpeojtrs.sca.scd.Interface;
import mil.jpeojtrs.sca.scd.Ports;
import mil.jpeojtrs.sca.scd.Provides;
import mil.jpeojtrs.sca.scd.ScdPackage;
import mil.jpeojtrs.sca.scd.SupportsInterface;
import mil.jpeojtrs.sca.scd.Uses;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * An implementation of {@link AbstractHandler} for removing ports from a SoftwareComponent.
 * @since 6.0
 */
public class RemovePortsHandler extends AbstractHandler {

	private EditingDomain editingDomain;
	private Resource resource;
	private SoftPkg softPkg;
	private Map<String, Interface> interfaceMap;

	/**
	 * Default Constructor for instantiation by framework.
	 */
	public RemovePortsHandler() {
		// DefaultConstructor
	}

	/**
	 * Constructor for use within the package so this handler can be used by other handlers.
	 * @param editor
	 * 
	 * @param editingDomain
	 * @param resource
	 * @param softPkg
	 */
	public RemovePortsHandler(final EditingDomain editingDomain, final Resource resource, final SoftPkg softPkg) {
		this.editingDomain = editingDomain;
		this.resource = resource;
		this.softPkg = softPkg;
		this.interfaceMap = PortsHandlerUtil.getInterfaceMap(this.softPkg);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getSelectionService().getSelection();
		final ComponentEditor editor = (ComponentEditor) HandlerUtil.getActiveEditor(event);
		this.editingDomain = editor.getEditingDomain();
		this.resource = editor.getMainResource();
		this.softPkg = ModelUtil.getSoftPkg(this.resource);
		this.interfaceMap = PortsHandlerUtil.getInterfaceMap(this.softPkg);
		final List<Object> ports = Arrays.asList(((IStructuredSelection) selection).toArray());

		Command command = this.createRemovePortCommand(ports, new HashSet<String>());
		this.editingDomain.getCommandStack().execute(command);

		return null;
	}

	/**
	 * Creates and returns a command to remove the specified ports.
	 * 
	 * @param ports the ports to remove from the {@link mil.jpeojtrs.sca.scd.SoftwareComponent}
	 * @param ignoreIds ignore the specified repIds when considering which interfaces to remove
	 * @return the {@link Command} to remove the port and all associated {@link Interface}
	 */
	public Command createRemovePortCommand(final Collection<Object> ports, final Set<String> ignoreIds) {
		final Set<String> repIds = new HashSet<String>();
		final List<EObject> removePorts = new ArrayList<EObject>();
		final CompoundCommand command = new CompoundCommand("Remove Ports");
		for (final Object obj : ports) {
			final EObject port = (EObject) AdapterFactoryEditingDomain.unwrap(obj);
			if (port instanceof AbstractPort) {
				for (AbstractPort p : getPorts().getAllPorts()) {
					if (p.equals(port)) {
						final AbstractPort sibling = p.getSibling();
						if (sibling == null || !(p instanceof Uses)) {
							repIds.add(p.getRepID());
							command.append(RemoveCommand.create(this.editingDomain, getPorts(), p.eContainingFeature(), p));
							if (sibling != null) {
								command.append(RemoveCommand.create(this.editingDomain, getPorts(), sibling.eContainingFeature(), sibling));
							}
							removePorts.add(p);
						}
					}
				}
			}
		}

		for (final String repId : repIds) {
			if (this.interfaceMap.containsKey(repId) && !ignoreIds.contains(repId)) {
				final Command interfaceCommand = this.createRemoveInterfaceCommand(removePorts, ignoreIds, this.interfaceMap.get(repId));
				if (interfaceCommand != null) {
					command.append(interfaceCommand);
					this.interfaceMap.remove(repId);
				}
			}
		}
		return command;
	}

	/**
	 * Creates and returns a command to remove the interface and associated inherited interfaces if they are not
	 * referenced by other ports.
	 * 
	 * @param i the {@link Interface} to remove
	 * @param removePorts the {@link Uses} or {@link Provides} to ignore when considering which interfaces to remove
	 * @param removeInterfaces the {@link Set} of repIds already scheduled for removal
	 * @return a {@link Command} to remove the specified {@link Interface} and {@link InheritsInterface} if they can be
	 * removed
	 */
	public Command createRemoveInterfaceCommand(final Collection<EObject> removePorts, final Set<String> removeInterfaces, final Interface i) {
		if (!containsRepId(i.getRepid(), removePorts)) {
			final CompoundCommand command = new CompoundCommand("Remove Interfaces");
			for (final InheritsInterface inherited : i.getInheritsInterfaces()) {
				if (!containsRepId(inherited.getRepid(), removePorts)) {
					if (!removeInterfaces.contains(inherited.getRepid())) {
						// If the inherited interface isn't referenced by another port and isn't already scheduled for
						// removal, make a recursive call to remove it
						if (inherited.getInterface() != null) {
							command.append(this.createRemoveInterfaceCommand(removePorts, removeInterfaces, inherited.getInterface()));
						}
					}
				}
			}
			// If the interface isn't already scheduled for removal, create a command to remove it
			if (removeInterfaces.add(i.getRepid())) {
				command.append(RemoveCommand.create(this.editingDomain, PortsHandlerUtil.getInterfaces(this.softPkg),
					ScdPackage.Literals.INTERFACES__INTERFACE, i));
			}
			return command;
		}
		return null;
	}

	/**
	 * Searches the software component for the specified repID.
	 *
	 * @param repID the repID to search for
	 * @param removePorts the {@link Uses} or {@link Provides} ports being removed, these are disregarded
	 * @param repIds the {@link Set} of repIds belonging to the {@link mil.jpeojtrs.sca.scd.SoftwareComponent}
	 * @return <code> true </code> if the repID is found; <code> false </code> otherwise
	 */
	private boolean containsRepId(final String repID, final Collection<EObject> removePorts) {
		// If it's a supported interface, return true;
		if (getSupportsInterfaceIds().contains(repID)) {
			return true;
		} else {
			// Go through the ports and try to match
			for (final FeatureMap.Entry entry : getPorts().getGroup()) {
				if (!(entry.getValue() instanceof AbstractPort)) {
					continue; // unexpected
				}
				final AbstractPort port = (AbstractPort) entry.getValue();
				// Ignore ports being removed
				if (!removePorts.contains(port) && !removePorts.contains(port.getSibling())) {
					if (port instanceof Provides) {
						if (!repID.equals(((Provides) port).getRepID())) {
							for (final InheritsInterface inherits : ((Provides) port).getInterface().getInheritsInterfaces()) {
								if (repID.equals(inherits.getRepid())) {
									return true;
								}
							}
						} else {
							return true;
						}
					} else { // Uses
						if (!repID.equals(((Uses) port).getRepID())) {
							for (final InheritsInterface inherits : ((Uses) port).getInterface().getInheritsInterfaces()) {
								if (repID.equals(inherits.getRepid())) {
									return true;
								}
							}
						} else {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Gets the ports via the {@link SoftPkg}
	 * 
	 * @return the {@link Ports} associated with the {@link mil.jpeojtrs.sca.scd.SoftwareComponent}
	 */
	private Ports getPorts() {
		return this.softPkg.getDescriptor().getComponent().getComponentFeatures().getPorts();
	}

	/**
	 * Gets a set of {@link SupportsInterface} repIds.
	 * 
	 * @return the set of repIds supported by the SoftwareComponent
	 */
	private Set<String> getSupportsInterfaceIds() {
		final Set<String> ids = new HashSet<String>();
		for (final SupportsInterface supports : this.softPkg.getDescriptor().getComponent().getComponentFeatures().getSupportsInterface()) {
			ids.add(supports.getRepId());
		}
		return ids;
	}

}
