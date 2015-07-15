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
package gov.redhawk.ide.sad.internal.ui.handler;

import mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.ExternalPorts;
import mil.jpeojtrs.sca.sad.Port;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadComponentInstantiationRef;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.action.Action;

import CF.ResourceHelper;

/**
 * 
 */
public class AddExternalPortAction extends Action {

	private SadComponentInstantiation componentInstantiation;
	private EObject componentPort;
	private SoftwareAssembly softwareAssembly;
	private String portDescription;
	private EditingDomain editingDomain;

	public void setEditingDomain(final EditingDomain editingDomain) {
		this.editingDomain = editingDomain;
	}

	private EditingDomain getEditingDomain() {
		if (this.editingDomain == null) {
			return TransactionUtil.getEditingDomain(this.softwareAssembly);
		}
		return this.editingDomain;
	}

	public void setComponentInstantiation(final SadComponentInstantiation componentInstantiation) {
		this.componentInstantiation = componentInstantiation;
	}

	public void setComponentPort(final EObject componentPort) {
		this.componentPort = componentPort;
	}

	public void setSoftwareAssembly(final SoftwareAssembly softwareAssembly) {
		this.softwareAssembly = softwareAssembly;
	}

	public void setPortDescription(final String portDescription) {
		this.portDescription = portDescription;
	}

	@Override
	public void run() {
		addPort();
	}

	private void addPort() {
		final EditingDomain localEditingDomain = getEditingDomain();
		Command command = null;
		final Port port = createPort();

		if (this.softwareAssembly.getExternalPorts() == null) {
			final ExternalPorts externalPorts = SadFactory.eINSTANCE.createExternalPorts();
			externalPorts.getPort().add(port);
			command = SetCommand.create(localEditingDomain, this.softwareAssembly, SadPackage.Literals.SOFTWARE_ASSEMBLY__EXTERNAL_PORTS, externalPorts);
		} else {
			command = AddCommand.create(localEditingDomain, this.softwareAssembly.getExternalPorts(), SadPackage.Literals.EXTERNAL_PORTS__PORT, port);
		}

		localEditingDomain.getCommandStack().execute(command);
	}

	private Port createPort() {
		final Port port = SadFactory.eINSTANCE.createPort();
		final SadComponentInstantiationRef ref = SadFactory.eINSTANCE.createSadComponentInstantiationRef();
		ref.setInstantiation(this.componentInstantiation);
		port.setComponentInstantiationRef(ref);
		if (this.componentPort instanceof ProvidesPortStub) {
			final ProvidesPortStub providesPort = (ProvidesPortStub) this.componentPort;
			port.setProvidesIdentifier(providesPort.getProvides().getProvidesName());
		} else if (this.componentPort instanceof UsesPortStub) {
			final UsesPortStub usesPort = (UsesPortStub) this.componentPort;
			port.setUsesIdentifier(usesPort.getUses().getUsesName());
		} else if (this.componentPort instanceof ComponentSupportedInterfaceStub) {
			final ComponentSupportedInterfaceStub compPort = (ComponentSupportedInterfaceStub) this.componentPort;
			// FIXME:  I don't understand why we even need the specific interface here?
			port.setSupportedIdentifier(ResourceHelper.id());
		}
		port.setDescription(this.portDescription);
		return port;
	}
}
