/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.spd.internal.ui.editor;

import gov.redhawk.eclipsecorba.idl.IdlInterfaceDcl;
import gov.redhawk.eclipsecorba.library.IdlLibrary;
import gov.redhawk.ide.spd.internal.ui.handlers.PortsHandlerUtil;
import gov.redhawk.sca.util.PropertyChangeSupport;
import gov.redhawk.ui.editor.SCAFormEditor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mil.jpeojtrs.sca.scd.AbstractPort;
import mil.jpeojtrs.sca.scd.ComponentFeatures;
import mil.jpeojtrs.sca.scd.InheritsInterface;
import mil.jpeojtrs.sca.scd.Interface;
import mil.jpeojtrs.sca.scd.PortType;
import mil.jpeojtrs.sca.scd.PortTypeContainer;
import mil.jpeojtrs.sca.scd.Ports;
import mil.jpeojtrs.sca.scd.Provides;
import mil.jpeojtrs.sca.scd.ScdFactory;
import mil.jpeojtrs.sca.scd.ScdPackage;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.scd.SupportsInterface;
import mil.jpeojtrs.sca.scd.Uses;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

public class PortsPageModel {

	public static final String PROP_TYPE = "type";
	public static final String PROP_REP_ID = "repId";
	public static final String PROP_PORT_TYPES = "portTypes";
	public static final String PROP_PORT_DIRECTION = "portDirection";

	private SoftPkg softPkg;
	private AbstractPort port = null;
	private Ports ports;
	private final Set<PortType> portTypes = new HashSet<PortType>();

	private PortDirection portDirection = PortDirection.PROVIDES;

	private SCAFormEditor editor;
	private String repId = null;

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	public PortsPageModel(SCAFormEditor editor) {
		this.editor = editor;
	}

	public void addPropertyChangeListener(final PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, final PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(final PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(String propertyName, final PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(propertyName, listener);
	}

	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		this.pcs.firePropertyChange(propertyName, oldValue, newValue);
	}

	public AbstractPort getPort() {
		return this.port;
	}

	public void setPort(final AbstractPort port) {
		if (port.getRepID().isEmpty() || "".equals(port.getRepID())) {
			return;
		}

		this.port = port;
		setRepId((this.port.getRepID()));

		// Don't need to set provides, it is default
		if (port.isBiDirectional()) {
			setPortDirection(PortDirection.BIDIR);
		} else if (port instanceof Uses) {
			setPortDirection(PortDirection.USES);
		} else if (port instanceof Provides) {
			setPortDirection(PortDirection.PROVIDES);
		}

		for (final PortTypeContainer ptc : this.port.getPortType()) {
			this.portTypes.add(ptc.getType());
		}
	}

	public Ports getPorts() {
		return this.ports;
	}

	public void setPorts(final Ports ports) {
		this.ports = ports;
	}

	public String getRepId() {
		return this.repId;
	}

	public void setRepId(final String repId) {
		updateModelRepId(repId);

		final String oldValue = this.repId;
		this.repId = repId;
		this.pcs.firePropertyChange(new PropertyChangeEvent(this, PortsPageModel.PROP_REP_ID, oldValue, repId));
	}

	public Set<PortType> getPortTypes() {
		return this.portTypes;
	}

	public void setPortTypes(final EList<PortTypeContainer> newTypes) {
		this.portTypes.clear();
		for (PortTypeContainer portType : newTypes) {
			this.portTypes.add(portType.getType());
		}
		this.pcs.firePropertyChange(new PropertyChangeEvent(this, PortsPageModel.PROP_PORT_TYPES, null, this.portTypes));
	}

	public PortDirection getPortDirection() {
		return this.portDirection;
	}

	public void setPortDirection(final PortDirection portDirection) {
		final PortDirection oldValue = this.portDirection;
		this.portDirection = portDirection;

		updateModelPortDirection(portDirection);

		this.pcs.firePropertyChange(new PropertyChangeEvent(this, PortsPageModel.PROP_PORT_DIRECTION, oldValue, portDirection));
	}

	public void setSoftPkg(SoftPkg softPkg) {
		this.softPkg = softPkg;
	}

	public SoftPkg getSoftPkg() {
		return this.softPkg;
	}

	private void updateModelRepId(final String newRepId) {
		// Check if user clicked on a container rather than an IDL interface
		if ("".equals(newRepId) || newRepId.equals(this.port.getRepID())) {
			return;
		}

		TransactionalEditingDomain editingDomain = (TransactionalEditingDomain) AdapterFactoryEditingDomain.getEditingDomainFor(port);

		if (editingDomain != null) {
			TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();

			// Remove all port interfaces from the scd, we are starting fresh
			final CompoundCommand removeCommand = new CompoundCommand("Remove Port Interfaces Command");
			Set<String> removeInterfaces = new HashSet<String>();
			removeInterfaces.addAll(getComponentInterfaces());
			for (AbstractPort p : ports.getAllPorts()) {
				if (!removeInterfaces.contains(p.getRepID())) {
					removeCommand.append(createRemoveInterfaceCommand(editingDomain, p.getInterface(), removeInterfaces));
				}
			}

			// Update the repId of the affected port
			RecordingCommand updateCommand = new RecordingCommand(editingDomain) {

				@Override
				protected void doExecute() {
					port.setRepID(newRepId);
				}
			};

			// Add in all expected port interfaces
			final CompoundCommand addCommand = new CompoundCommand("Add Port Interfaces Command");
			Set<String> addInterfaces = new HashSet<String>();
			addInterfaces.addAll(getComponentInterfaces());

			// Create an add command for the new port
			addCommand.append(createAddInterfaceCommand(editingDomain, editor.getIdlLibrary(), newRepId, addInterfaces));

			for (AbstractPort p : ports.getAllPorts()) {
				// Ignore the existing port, since it was added above (and it hasn't actually been updated yet, so
				// you'll get stale information)
				if (p.equals(port)) {
					continue;
				}

				// Make sure a valid IDL Interface is being added, and only add it once
				if ((!"".equals(p.getRepID())) && !addInterfaces.contains(p.getRepID())) {
					addCommand.append(createAddInterfaceCommand(editingDomain, editor.getIdlLibrary(), p.getRepID(), addInterfaces));
				}
			}

			CompoundCommand editRepIdCommand = new CompoundCommand();
			editRepIdCommand.append(removeCommand);
			editRepIdCommand.append(updateCommand);
			editRepIdCommand.append(addCommand);

			stack.execute(editRepIdCommand);
		}
	}

	private Set<String> getComponentInterfaces() {
		Set<String> componentInterfaces = new HashSet<String>();
		
		SoftwareComponent scd = (SoftwareComponent) port.eResource().getContents().get(0);
		ComponentFeatures cf = scd.getComponentFeatures();
		EList<SupportsInterface> si = cf.getSupportsInterface();
		for (SupportsInterface s : si) {
			componentInterfaces.add(s.getRepId());
		}

		return componentInterfaces;
	}

	private Command createRemoveInterfaceCommand(TransactionalEditingDomain editingDomain, Interface portInterface, final Set<String> removeInterfaces) {
		final CompoundCommand command = new CompoundCommand("Remove Interfaces");

		// Remove all inherited interfaces
		if (portInterface == null) {
			return command;
		}
		
		for (final InheritsInterface inherited : portInterface.getInheritsInterfaces()) {
			if (inherited.getInterface() != null && !removeInterfaces.contains(inherited.getRepid())) {
				command.append(createRemoveInterfaceCommand(editingDomain, inherited.getInterface(), removeInterfaces));
			}
		}

		// If the interface isn't already scheduled for removal, create a command to remove it
		if (removeInterfaces.add(portInterface.getRepid())) {
			command.append(RemoveCommand.create(editingDomain, PortsHandlerUtil.getInterfaces(this.softPkg), ScdPackage.Literals.INTERFACES__INTERFACE,
				portInterface));
		}

		return command;
	}

	private Command createAddInterfaceCommand(TransactionalEditingDomain editingDomain, final IdlLibrary library, String repId, final Set<String> addInterfaces) {

		final Interface newInterface = ScdFactory.eINSTANCE.createInterface();
		final IdlInterfaceDcl idlInter = (IdlInterfaceDcl) library.find(repId);

		// If the interface isn't present in the IdlLibrary, there's nothing to do
		if (idlInter != null) {
			final CompoundCommand command = new CompoundCommand("Add Interfaces");
			newInterface.setName(idlInter.getName());
			newInterface.setRepid(repId);

			// Add all the inherited interfaces first.
			for (final IdlInterfaceDcl inherited : idlInter.getInheritedInterfaces()) {
				final InheritsInterface iface = ScdFactory.eINSTANCE.createInheritsInterface();
				iface.setRepid(inherited.getRepId());
				newInterface.getInheritsInterfaces().add(iface);

				// If the inherited interface isn't already present, make a recursive call to add it.
				if (!addInterfaces.contains(inherited.getRepId())) {
					command.append(createAddInterfaceCommand(editingDomain, library, inherited.getRepId(), addInterfaces));
				}
			}

			// If the interface isn't already present, create a command to add it
			if (addInterfaces.add(newInterface.getRepid())) {
				command.append(AddCommand.create(editingDomain, PortsHandlerUtil.getInterfaces(this.softPkg), ScdPackage.Literals.INTERFACES__INTERFACE,
					newInterface));
			}
			return command;
		}
		return null;
	}

	public void updateModelPortTypes() {
		final List<AbstractPort> portsToUpdate = new ArrayList<AbstractPort>();
		portsToUpdate.add(port);
		if (port.isBiDirectional()) {
			portsToUpdate.add(port.getSibling());
		}

		TransactionalEditingDomain editingDomain = (TransactionalEditingDomain) AdapterFactoryEditingDomain.getEditingDomainFor(ports);
		if (editingDomain != null) {
			TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
			stack.execute(new RecordingCommand(editingDomain) {
				@Override
				protected void doExecute() {
					for (AbstractPort p : portsToUpdate) {
						p.getPortType().clear();
						if (getPortTypes().isEmpty()) {
							p.getPortType().add(ScdFactory.eINSTANCE.createPortTypeContainer(PortType.CONTROL));
						} else {
							for (PortType portType : getPortTypes()) {
								p.getPortType().add(ScdFactory.eINSTANCE.createPortTypeContainer(portType));
							}
						}
					}
				}
			});
		}
	}

	public void updateModelPortDirection(PortDirection newValue) {
		TransactionalEditingDomain editingDomain = (TransactionalEditingDomain) AdapterFactoryEditingDomain.getEditingDomainFor(ports);
		if (editingDomain != null) {
			TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();

			final CompoundCommand updateTypeCommand = new CompoundCommand("Change Port Type");

			if (PortDirection.USES.equals(newValue)) {

				if (port.isBiDirectional()) {
					// If bi-directional, delete the provides, leaving only the uses copy
					if (port instanceof Provides) {
						updateTypeCommand.append(RemoveCommand.create(editingDomain, PortsHandlerUtil.getPorts(this.softPkg),
							ScdPackage.Literals.PORTS__PROVIDES, port));
					} else {
						updateTypeCommand.append(RemoveCommand.create(editingDomain, PortsHandlerUtil.getPorts(this.softPkg),
							ScdPackage.Literals.PORTS__PROVIDES, port.getSibling()));
					}
				} else if (port instanceof Provides) {
					// Make a new uses port, and delete the old provides port
					Uses newUses = ScdFactory.eINSTANCE.createUses();
					Provides oldProvides = (Provides) port;
					newUses.setName(oldProvides.getName());
					newUses.setInterface(oldProvides.getInterface());
					newUses.setRepID(oldProvides.getRepID());
					for (PortTypeContainer ptc : oldProvides.getPortType()) {
						PortType pt = ptc.getType();
						newUses.getPortType().add(ScdFactory.eINSTANCE.createPortTypeContainer(pt));
					}

					updateTypeCommand.append(RemoveCommand.create(editingDomain, PortsHandlerUtil.getPorts(this.softPkg), ScdPackage.Literals.PORTS__PROVIDES,
						oldProvides));
					updateTypeCommand.append(AddCommand.create(editingDomain, PortsHandlerUtil.getPorts(this.softPkg), ScdPackage.Literals.PORTS__USES, newUses));
				}

			} else if (PortDirection.PROVIDES.equals(newValue)) {

				if (port.isBiDirectional()) {
					// If bi-directional, delete the provides, leaving only the uses copy
					if (port instanceof Uses) {
						updateTypeCommand.append(RemoveCommand.create(editingDomain, PortsHandlerUtil.getPorts(this.softPkg), ScdPackage.Literals.PORTS__USES,
							port));
					} else {
						updateTypeCommand.append(RemoveCommand.create(editingDomain, PortsHandlerUtil.getPorts(this.softPkg), ScdPackage.Literals.PORTS__USES,
							port.getSibling()));
					}
				} else if (port instanceof Uses) {

					// User changed port to provides. Make a new provides port, and delete the old uses port
					Provides newProvides = ScdFactory.eINSTANCE.createProvides();
					Uses oldUses = (Uses) port;
					newProvides.setName(oldUses.getName());
					newProvides.setInterface(oldUses.getInterface());
					newProvides.setRepID(oldUses.getRepID());
					for (PortTypeContainer ptc : oldUses.getPortType()) {
						PortType pt = ptc.getType();
						newProvides.getPortType().add(ScdFactory.eINSTANCE.createPortTypeContainer(pt));

					}
					updateTypeCommand.append(RemoveCommand.create(editingDomain, PortsHandlerUtil.getPorts(this.softPkg), ScdPackage.Literals.PORTS__USES,
						oldUses));
					updateTypeCommand.append(AddCommand.create(editingDomain, PortsHandlerUtil.getPorts(this.softPkg), ScdPackage.Literals.PORTS__PROVIDES,
						newProvides));
				}
			} else if (PortDirection.BIDIR.equals(newValue)) {
				if (port.getSibling() != null) {
					// The sibling port already exists, no need to make it again
					return;
				}
				if (port instanceof Provides) {
					// We already have the provides port, so make a new uses
					Uses newUses = ScdFactory.eINSTANCE.createUses();
					Provides originalPort = (Provides) port;
					newUses.setName(originalPort.getName());
					newUses.setInterface(originalPort.getInterface());
					newUses.setRepID(originalPort.getRepID());
					for (PortTypeContainer ptc : originalPort.getPortType()) {
						PortType pt = ptc.getType();
						newUses.getPortType().add(ScdFactory.eINSTANCE.createPortTypeContainer(pt));
					}

					updateTypeCommand.append(AddCommand.create(editingDomain, PortsHandlerUtil.getPorts(this.softPkg), ScdPackage.Literals.PORTS__USES, newUses));
				} else {
					// We already have the uses port, so make a new provides
					Provides newProvides = ScdFactory.eINSTANCE.createProvides();
					Uses originalPort = (Uses) port;
					newProvides.setName(originalPort.getName());
					newProvides.setInterface(originalPort.getInterface());
					newProvides.setRepID(originalPort.getRepID());
					for (PortTypeContainer ptc : originalPort.getPortType()) {
						PortType pt = ptc.getType();
						newProvides.getPortType().add(ScdFactory.eINSTANCE.createPortTypeContainer(pt));
					}

					updateTypeCommand.append(AddCommand.create(editingDomain, PortsHandlerUtil.getPorts(this.softPkg), ScdPackage.Literals.PORTS__PROVIDES,
						newProvides));
				}
			}
			stack.execute(updateTypeCommand);
		}
	}
}
