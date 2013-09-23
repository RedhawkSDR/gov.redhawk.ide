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
package gov.redhawk.ide.sdr.internal.ui.commands;

import gov.redhawk.eclipsecorba.library.IdlLibrary;
import gov.redhawk.ide.preferences.RedhawkIdePreferenceInitializer;

import org.eclipse.emf.common.command.AbstractCommand;

/**
 * @since 6.0
 */
public class InitIdlLibraryCommand extends AbstractCommand {

	private final IdlLibrary library;

	public InitIdlLibraryCommand(final IdlLibrary library) {
		this.library = library;
	}

	@Override
	protected boolean prepare() {
		return this.library != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		RedhawkIdePreferenceInitializer.initializeIdlLibraryToDefaults(this.library);
	}

	@Override
	public boolean canUndo() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void redo() {
		// TODO Auto-generated method stub

	}

}
