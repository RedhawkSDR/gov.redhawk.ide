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
package gov.redhawk.ide.graphiti.ui.diagram.features.custom;

import org.eclipse.core.runtime.Platform;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.ui.PlatformUI;

import CF.LogConfigurationOperations;
import gov.redhawk.logging.ui.handlers.EditLogConfig;

public class EditLogConfigFeature extends AbstractLoggingFeature {

	public EditLogConfigFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return "Edit Log Config";
	}

	@Override
	public String getDescription() {
		return "Edit the resource's logging configuration file";
	}

	@Override
	public void execute(ICustomContext context) {
		PictogramElement pe = context.getPictogramElements()[0];
		LogConfigurationOperations resource = Platform.getAdapterManager().getAdapter(pe, LogConfigurationOperations.class);
		if (resource != null) {
			EditLogConfig editLogConfig = new EditLogConfig();
			editLogConfig.handleEditLogConfiguration(resource, PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage());
		}
	}

}
