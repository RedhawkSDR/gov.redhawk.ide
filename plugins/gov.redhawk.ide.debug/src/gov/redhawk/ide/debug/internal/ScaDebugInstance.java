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
package gov.redhawk.ide.debug.internal;

import gov.redhawk.ide.debug.LocalSca;
import gov.redhawk.ide.debug.ScaDebugFactory;
import gov.redhawk.ide.debug.internal.cf.extended.impl.SandboxImpl;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.sca.ScaPlugin;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

/**
 * 
 */
public enum ScaDebugInstance {
	INSTANCE;
	private LocalSca localSca;
	private TransactionalEditingDomain editingDomain;

	private ScaDebugInstance() {
		this.editingDomain = TransactionalEditingDomain.Registry.INSTANCE.getEditingDomain(ScaPlugin.EDITING_DOMAIN_ID);
		final Resource resource = editingDomain.getResourceSet().createResource(URI.createURI("virtual://localSca.scaDebug"));
		this.localSca = ScaDebugFactory.eINSTANCE.createLocalSca();
		final SandboxImpl impl = new SandboxImpl(localSca);
		editingDomain.getCommandStack().execute(new ScaModelCommand() {

			public void execute() {
				resource.getContents().add(ScaDebugInstance.this.localSca);
				localSca.setSandbox(impl);
			}
		});
	}

	public LocalSca getLocalSca() {
		return this.localSca;
	}

}
