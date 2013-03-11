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

import mil.jpeojtrs.sca.sad.ExternalPorts;
import mil.jpeojtrs.sca.sad.Port;
import mil.jpeojtrs.sca.sad.SadPackage;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.action.Action;

/**
 * 
 */
public class RemoveExternalPortAction extends Action {

	private Port port;
	private EditingDomain editingDomain;

	public void setPort(final Port port) {
		this.port = port;
	}

	public void setEditingDomain(final EditingDomain editingDomain) {
		this.editingDomain = editingDomain;
	}

	private void removePort() {
		final EditingDomain localEditingDomain = getEditingDomain();
		final ExternalPorts ports = (ExternalPorts) this.port.eContainer();
		Command command = null;
		if (ports.getPort().size() == 1) {
			command = SetCommand.create(localEditingDomain, ports.eContainer(), SadPackage.Literals.SOFTWARE_ASSEMBLY__EXTERNAL_PORTS, null);
		} else {
			command = RemoveCommand.create(localEditingDomain, ports, SadPackage.Literals.EXTERNAL_PORTS__PORT, this.port);
		}

		localEditingDomain.getCommandStack().execute(command);
	}

	private EditingDomain getEditingDomain() {
		if (this.editingDomain == null) {
			return TransactionUtil.getEditingDomain(this.port);
		}
		return this.editingDomain;
	}

	@Override
	public void run() {
		removePort();
	}
}
