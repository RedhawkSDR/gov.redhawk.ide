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

import org.eclipse.core.runtime.IStatus;

import CF.ComponentType;
import gov.redhawk.ide.debug.LocalScaComponent;
import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.debug.ScaDebugFactory;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.commands.ScaWaveformMergeComponentsCommand;

/**
 * Used when updating the components belonging to a sandbox waveform.
 * @since 2.0
 */
public class LocalScaWaveformMergeComponentsCommand extends ScaWaveformMergeComponentsCommand {

	public LocalScaWaveformMergeComponentsCommand(final LocalScaWaveform provider, final ComponentType[] compTypes, final IStatus componentStatus) {
		super(provider, compTypes, componentStatus);
	}

	/**
	 * @deprecated Use {@link #createComponent(String, String, org.omg.CORBA.Object)}
	 */
	@Deprecated
	@Override
	protected ScaComponent createComponent() {
		return ScaDebugFactory.eINSTANCE.createLocalScaComponent();
	}

	protected ScaComponent createComponent(String identifier, String softwareProfile, org.omg.CORBA.Object componentObject) {
		LocalScaComponent component = ScaDebugFactory.eINSTANCE.createLocalScaComponent();
		setAttributes(component, identifier, softwareProfile, componentObject);
		return component;
	}
}
