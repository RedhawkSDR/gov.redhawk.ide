/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.graphiti.dcd.ui.diagram.providers;

import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;

public class DCDDiagramTypeProvider extends AbstractDiagramTypeProvider {

	public static final String DIAGRAM_TYPE_ID = "DCDDiagram";
	public static final String DIAGRAM_EXT = ".dcd_GDiagram"; // TODO: Where is this used?
	public static final String PROVIDER_ID = "gov.redhawk.ide.graphiti.dcd.ui.FactoryProvider";
	
	private IToolBehaviorProvider[] toolBehaviorProviders;

	public DCDDiagramTypeProvider() {
		super();
		setFeatureProvider(new DCDDiagramFeatureProvider(this));
	}

	@Override
	public IToolBehaviorProvider[] getAvailableToolBehaviorProviders() {
		if (toolBehaviorProviders == null) {
			toolBehaviorProviders = new IToolBehaviorProvider[] { new GraphitiDCDToolBehaviorProvider(this) };
		}
		return toolBehaviorProviders;
	}
	
	/**
	 * On startup scan the model and update our diagram using the registered update feature providers
	 */
	@Override
	public boolean isAutoUpdateAtStartup() {
		return true;
	}

	@Override
	public boolean isAutoUpdateAtRuntime() {
		return true;
	}

	@Override
	public boolean isAutoUpdateAtReset() {
		return true;
	}
}
