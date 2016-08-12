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
package gov.redhawk.ide.graphiti.sad.ui.diagram.providers;

import org.eclipse.graphiti.tb.IToolBehaviorProvider;

public class WaveformSandboxTypeProvider extends SADDiagramTypeProvider {

	public static final String PROVIDER_ID = "gov.redhawk.ide.graphiti.sad.ui.WaveformSandboxTypeProvider";

	private IToolBehaviorProvider[] toolBehaviorProviders;

	public WaveformSandboxTypeProvider() {
		super();
		setFeatureProvider(new WaveformSandboxFeatureProvider(this));
	}

	@Override
	public IToolBehaviorProvider[] getAvailableToolBehaviorProviders() {
		if (toolBehaviorProviders == null) {
			toolBehaviorProviders = new IToolBehaviorProvider[] { new WaveformSandboxToolBehaviorProvider(this) };
		}
		return toolBehaviorProviders;
	}
}
