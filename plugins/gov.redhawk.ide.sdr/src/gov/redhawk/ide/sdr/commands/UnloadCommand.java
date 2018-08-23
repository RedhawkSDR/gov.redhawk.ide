/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.sdr.commands;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.command.AbstractCommand;

import gov.redhawk.ide.sdr.SdrRoot;

/**
 * @deprecated This class is unused and there is no planned replacement
 * @since 8.0
 */
@Deprecated
public class UnloadCommand extends AbstractCommand {

	private SdrRoot sdrRoot;
	private IProgressMonitor monitor;

	public UnloadCommand(SdrRoot sdrRoot, IProgressMonitor monitor) {
		this.sdrRoot = sdrRoot;
		this.monitor = monitor;
	}

	@Override
	protected boolean prepare() {
		return this.sdrRoot != null;
	}

	@Override
	public boolean canUndo() {
		return false;
	}

	@Override
	public void execute() {
		this.sdrRoot.unload(monitor);
	}

	@Override
	public void redo() {
		// PASS
	}

}
