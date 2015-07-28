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

import gov.redhawk.eclipsecorba.idl.IdlInterfaceDcl;
import gov.redhawk.eclipsecorba.library.IdlLibrary;
import gov.redhawk.ide.spd.internal.ui.editor.ComponentEditor;
import gov.redhawk.model.sca.util.ModelUtil;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import mil.jpeojtrs.sca.scd.AbstractPort;
import mil.jpeojtrs.sca.scd.InheritsInterface;
import mil.jpeojtrs.sca.scd.Interface;
import mil.jpeojtrs.sca.scd.PortType;
import mil.jpeojtrs.sca.scd.Ports;
import mil.jpeojtrs.sca.scd.Provides;
import mil.jpeojtrs.sca.scd.ScdFactory;
import mil.jpeojtrs.sca.scd.ScdPackage;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * An implementation of {@link AbstractHandler} for adding a port to a SoftwareComponent.
 * @since 6.0
 */
public class AddPortHandler extends AbstractHandler {

	private static final String DEFAULT_PORT_REP_ID = "IDL:BULKIO/dataFloat:1.0";
	private static final String DEFAULT_PORT_NAME = "dataFloat";
	
	private EditingDomain editingDomain;
	private Resource resource;
	private SoftPkg softPkg;
	private ComponentEditor editor;
	private Map<String, Interface> interfaceMap;

	public AddPortHandler() {
	}

	/**
	 * Constructor for use within the package so this handler can be used by other handlers.
	 * 
	 * @param editingDomain
	 * @param resource
	 * @param softPkg
	 */
	public AddPortHandler(final EditingDomain editingDomain, final Resource resource, final SoftPkg softPkg) {
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
		this.editor = (ComponentEditor) HandlerUtil.getActiveEditor(event);
		this.editingDomain = this.editor.getEditingDomain();
		this.resource = this.editor.getMainResource();
		this.softPkg = ModelUtil.getSoftPkg(this.resource);
		this.interfaceMap = PortsHandlerUtil.getInterfaceMap(this.softPkg);

		// Creates a new port with default settings
		Command defaultPortCommand = createDefaultAddPortCommand(editor.getIdlLibrary());
		this.editingDomain.getCommandStack().execute(defaultPortCommand);

		return null;
	}
	
	/**
	 * Adds the port and any associated interfaces.
	 */
	public Command createDefaultAddPortCommand(final IdlLibrary library) {
		String defaultRepID = DEFAULT_PORT_REP_ID; 
		Provides provides = ScdFactory.eINSTANCE.createProvides();

		provides.setName(getDefaultName(getPorts()));
		provides.setRepID(defaultRepID);
		provides.getPortType().add(ScdFactory.eINSTANCE.createPortTypeContainer(PortType.DATA));

		final CompoundCommand command = new CompoundCommand("Add Default Port Command");
		command.append(AddCommand.create(this.editingDomain, PortsHandlerUtil.getPorts(this.softPkg), ScdPackage.Literals.PORTS__PROVIDES, provides));

		if (!this.interfaceMap.containsKey(defaultRepID)) {
			command.append(createAddInterfaceCommand(library, defaultRepID, this.interfaceMap));
		}

		return command;
	}

	private String getDefaultName(Ports ports) {
		String defaultName = DEFAULT_PORT_NAME;

		Set<String> portNameList = new HashSet<String>();
		for (AbstractPort port : ports.getAllPorts()) {
			portNameList.add(port.getName());
		}

		int nameIncrement = 1;
		while (portNameList.contains(defaultName)) {
			defaultName = DEFAULT_PORT_NAME + "_" + nameIncrement++;
		}

		return (defaultName.toString());
	}

	/**
	 * Creates a command to add the interface and all inherited interfaces provided they're not already in the
	 * interfaceMap.
	 * 
	 * @param repId the {@link String} repId of the {@link Interface} to add
	 * @param interfaceMap the {@link Map} of current interfaces
	 * @return the {@link Command} to add the specified {@link Interface} and all {@link InheritsInterface} if they
	 * don't already exist and are present in the {@link IdlLibrary}; <code> null </code> otherwise
	 */
	public Command createAddInterfaceCommand(final IdlLibrary library, final String repId, final Map<String, Interface> interfaceMap) {
		final Interface i = ScdFactory.eINSTANCE.createInterface();
		final IdlInterfaceDcl idlInter = (IdlInterfaceDcl) library.find(repId);

		// If the interface isn't present in the IdlLibrary, there's nothing to do
		if (idlInter != null) {
			final CompoundCommand command = new CompoundCommand("Add Interfaces");
			i.setName(idlInter.getName());
			i.setRepid(repId);

			// Add all the inherited interfaces first.
			for (final IdlInterfaceDcl inherited : idlInter.getInheritedInterfaces()) {
				final InheritsInterface iface = ScdFactory.eINSTANCE.createInheritsInterface();
				iface.setRepid(inherited.getRepId());
				i.getInheritsInterfaces().add(iface);

				// If the inherited interface isn't already present, make a recursive call to add it.
				if (!interfaceMap.containsKey(inherited.getRepId())) {
					command.append(createAddInterfaceCommand(library, inherited.getRepId(), interfaceMap));
				}
			}

			// If the interface isn't already present
			if (!interfaceMap.containsKey(i.getRepid())) {
				interfaceMap.put(i.getRepid(), i);
				command.append(AddCommand.create(this.editingDomain, PortsHandlerUtil.getInterfaces(this.softPkg), ScdPackage.Literals.INTERFACES__INTERFACE, i));
			}
			return command;
		}
		return null;
	}

	private Ports getPorts() {
		return PortsHandlerUtil.getPorts(this.softPkg);
	}
}
