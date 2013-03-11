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
package gov.redhawk.ide.debug.impl.commands;

import gov.redhawk.ide.debug.ScaDebugFactory;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaWaveform;
import gov.redhawk.model.sca.commands.ScaWaveformMergeComponentsCommand;

import org.eclipse.core.runtime.IStatus;

import CF.ComponentType;

/**
 * 
 */
public class LocalScaWaveformMergeComponentsCommand extends ScaWaveformMergeComponentsCommand {

	@Deprecated
	public LocalScaWaveformMergeComponentsCommand(final ScaWaveform provider, final String assemblyControlerId, final ComponentType[] compTypes,
	        final IStatus componentStatus) {
		this(provider, compTypes, componentStatus);
	}

	public LocalScaWaveformMergeComponentsCommand(final ScaWaveform provider, final ComponentType[] compTypes, final IStatus componentStatus) {
		super(provider, compTypes, componentStatus);
	}

	@Override
	protected ScaComponent createComponent() {
		return ScaDebugFactory.eINSTANCE.createLocalScaComponent();
	}
}
