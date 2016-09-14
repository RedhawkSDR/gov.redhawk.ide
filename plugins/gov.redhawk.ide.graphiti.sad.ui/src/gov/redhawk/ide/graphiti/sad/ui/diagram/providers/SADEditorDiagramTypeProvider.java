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

import gov.redhawk.core.graphiti.sad.ui.diagram.providers.SADDiagramTypeProvider;

public class SADEditorDiagramTypeProvider extends SADDiagramTypeProvider {

	public static final String PROVIDER_ID = "gov.redhawk.ide.graphiti.sad.ui.SADEditorDiagramTypeProvider";

	private IToolBehaviorProvider[] toolBehaviorProviders;

	public SADEditorDiagramTypeProvider() {
		super();
		setFeatureProvider(new SADEditorFeatureProvider(this));
	}

	@Override
	public IToolBehaviorProvider[] getAvailableToolBehaviorProviders() {
		if (toolBehaviorProviders == null) {
			toolBehaviorProviders = new IToolBehaviorProvider[] { new SADEditorToolBehaviorProvider(this) };
		}
		return toolBehaviorProviders;
	}
}
