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
package gov.redhawk.ide.graphiti.ui.diagram.features.custom;

import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;

import gov.redhawk.ide.debug.LocalLaunch;

/**
 * This feature allows showing the console view for a component/device/etc running locally.
 */
@SuppressWarnings("restriction")
public class ShowConsoleFeature extends AbstractCustomFeature {

	public ShowConsoleFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return "Show Console";
	}

	@Override
	public String getDescription() {
		return "Display the local console output for this resource";
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		return true;
	}

	@Override
	public boolean isAvailable(IContext context) {
		if (!(context instanceof ICustomContext)) {
			return false;
		}
		ICustomContext customContext = (ICustomContext) context;

		// Selected objects must be have an ILaunch or we can't show a console
		for (PictogramElement pe : customContext.getPictogramElements()) {
			LocalLaunch localLaunch = Platform.getAdapterManager().getAdapter(pe, LocalLaunch.class);
			if (localLaunch == null || localLaunch.getLaunch() == null) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void execute(ICustomContext context) {
		for (PictogramElement pe : context.getPictogramElements()) {
			LocalLaunch localLaunch = Platform.getAdapterManager().getAdapter(pe, LocalLaunch.class);
			if (localLaunch != null && localLaunch.getLaunch() != null && localLaunch.getLaunch().getProcesses().length > 0) {
				final IConsole console = DebugUIPlugin.getDefault().getProcessConsoleManager().getConsole(localLaunch.getLaunch().getProcesses()[0]);
				final IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();
				consoleManager.showConsoleView(console);
			}
		}
	}

	@Override
	public String getImageId() {
		return gov.redhawk.ide.graphiti.ui.diagram.providers.ImageProvider.IMG_CONSOLE_VIEW;
	}

}
