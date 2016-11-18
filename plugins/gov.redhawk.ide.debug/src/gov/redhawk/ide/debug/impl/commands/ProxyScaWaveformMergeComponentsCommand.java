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
package gov.redhawk.ide.debug.impl.commands;

import org.eclipse.core.runtime.IStatus;

import CF.ComponentType;
import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.debug.impl.ProxyScaComponentImpl;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaWaveform;

/**
 * Used when updating the components belonging to a sandbox waveform which has been proxied from a domain waveform.
 * @since 8.3
 */
public class ProxyScaWaveformMergeComponentsCommand extends LocalScaWaveformMergeComponentsCommand {

	public ProxyScaWaveformMergeComponentsCommand(LocalScaWaveform provider, ComponentType[] compTypes, IStatus componentStatus) {
		super(provider, compTypes, componentStatus);
	}

	@Override
	protected ScaComponent createComponent(String identifier, String softwareProfile, org.omg.CORBA.Object componentObject) {
		// Find the domain component and proxy it
		ScaWaveform domainWaveform = ((LocalScaWaveform) provider).getDomainWaveform();
		if (domainWaveform != null) {
			for (ScaComponent domainComponent : domainWaveform.getComponentsCopy()) {
				if (identifier.equals(domainComponent.getIdentifier())) {
					ScaComponent component = new ProxyScaComponentImpl(domainComponent);
					setAttributes(component, identifier, softwareProfile, componentObject);
					return component;
				}
			}
		}

		// Fallback to a unique component instance since we can't find a domain model component to proxy
		return super.createComponent(identifier, softwareProfile, componentObject);
	}
}
