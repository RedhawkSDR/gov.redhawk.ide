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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.statushandlers.StatusManager;

import gov.redhawk.ide.graphiti.dcd.ui.DCDUIGraphitiPlugin;

public class DCDDiagramTypeProvider extends AbstractDiagramTypeProvider {

	public static final String DIAGRAM_TYPE_ID = "DCDDiagram";
	public static final String DIAGRAM_EXT = ".dcd_GDiagram"; // TODO: Where is this used?
	public static final String PROVIDER_ID = "gov.redhawk.ide.graphiti.dcd.ui.FactoryProvider";

	private static final String PROPERTY_VIEW_ID = "org.eclipse.ui.views.PropertySheet";

	private IToolBehaviorProvider[] toolBehaviorProviders;

	public DCDDiagramTypeProvider() {
		super();
		setFeatureProvider(new DCDDiagramFeatureProvider(this));

		// Open properties view
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(PROPERTY_VIEW_ID);
		} catch (PartInitException e) {
			StatusManager.getManager().handle(new Status(IStatus.WARNING, DCDUIGraphitiPlugin.PLUGIN_ID, "Unable to open property view", e));
		}
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
		return false;
	}

	@Override
	public boolean isAutoUpdateAtReset() {
		return true;
	}

}
