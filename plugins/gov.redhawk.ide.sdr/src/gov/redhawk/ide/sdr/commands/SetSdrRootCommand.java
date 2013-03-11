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
package gov.redhawk.ide.sdr.commands;

import gov.redhawk.ide.sdr.SdrRoot;

import org.eclipse.emf.common.command.AbstractCommand;
import org.eclipse.emf.common.util.URI;

/**
 * @since 8.0
 */
public class SetSdrRootCommand extends AbstractCommand {

	
	private final SdrRoot sdrRoot;
	private final URI uriRoot;
	private final String domPath;
	private final String devPath;

	public SetSdrRootCommand(SdrRoot sdrRoot, URI uriRoot, String domPath, String devPath) {
	    super();
	    this.sdrRoot = sdrRoot;
	    this.uriRoot = uriRoot;
	    this.domPath = domPath;
	    this.devPath = devPath;
    }
	

	@Override
	protected boolean prepare() {
	    return this.sdrRoot != null && uriRoot != null && domPath != null && devPath != null;
	}
	
	@Override
	public boolean canUndo() {
	    return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public void execute() {
		this.sdrRoot.setSdrRoot(uriRoot, domPath, devPath);
	}

	/**
	 * {@inheritDoc}
	 */
	public void redo() {
		// TODO Auto-generated method stub

	}
}
