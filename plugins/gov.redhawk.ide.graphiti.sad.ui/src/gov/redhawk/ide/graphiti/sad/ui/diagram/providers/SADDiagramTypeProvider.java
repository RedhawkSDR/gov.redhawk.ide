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
package gov.redhawk.ide.graphiti.sad.ui.diagram.providers;

import gov.redhawk.ide.graphiti.sad.ui.SADUIGraphitiPlugin;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.statushandlers.StatusManager;

public class SADDiagramTypeProvider extends AbstractDiagramTypeProvider {

	public static final String DIAGRAM_TYPE_ID = "SADDiagram";
	public static final String DIAGRAM_EXT = ".sad_GDiagram";
	public static final String PROVIDER_ID = "gov.redhawk.ide.graphiti.sad.ui.FactoryProvider";

	private static final String PROPERTY_VIEW_ID = "org.eclipse.ui.views.PropertySheet";

	private IToolBehaviorProvider[] toolBehaviorProviders;

	public SADDiagramTypeProvider() {
		super();
		setFeatureProvider(new SADGraphitiFeatureProvider(this));

		// Open properties view
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(PROPERTY_VIEW_ID);
		} catch (PartInitException e) {
			StatusManager.getManager().handle(new Status(IStatus.WARNING, SADUIGraphitiPlugin.PLUGIN_ID, "Unable to open property view", e));
		}
	}

	/**
	 * Provide a custom Behavior Provider
	 */
	@Override
	public IToolBehaviorProvider[] getAvailableToolBehaviorProviders() {
		if (toolBehaviorProviders == null) {
			toolBehaviorProviders = new IToolBehaviorProvider[] { new GraphitiSADToolBehaviorProvider(this) };
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
