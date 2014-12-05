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
package gov.redhawk.ide.sad.graphiti.ui.diagram.providers;

import gov.redhawk.ide.sad.graphiti.ui.SADUIGraphitiPlugin;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;
import org.eclipse.ui.PlatformUI;

public class SADDiagramTypeProvider extends AbstractDiagramTypeProvider implements IDiagramTypeProvider {

	public static final String DIAGRAM_TYPE_ID = "SADDiagram";
	public static final String DIAGRAM_EXT = ".sad_GDiagram";
	public static final String PROVIDER_ID = "gov.redhawk.ide.sad.graphiti.ui.FactoryProvider";

	private IToolBehaviorProvider[] toolBehaviorProviders;

	public SADDiagramTypeProvider() {
		super();
		setFeatureProvider(new SADDiagramFeatureProvider(this));

		// open properties view
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.eclipse.ui.views.PropertySheet");
		} catch (Exception e) { // SUPPRESS CHECKSTYLE catching Exception allowed here
			IStatus status = new Status(IStatus.WARNING, SADUIGraphitiPlugin.PLUGIN_ID, e.getMessage(), e);
		}
	}

	/**
	 * Provide a custom Behavior Provider
	 */
	@Override
	public IToolBehaviorProvider[] getAvailableToolBehaviorProviders() {
		if (toolBehaviorProviders == null) {
			toolBehaviorProviders = new IToolBehaviorProvider[] { new RHToolBehaviorProvider(this) };
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
