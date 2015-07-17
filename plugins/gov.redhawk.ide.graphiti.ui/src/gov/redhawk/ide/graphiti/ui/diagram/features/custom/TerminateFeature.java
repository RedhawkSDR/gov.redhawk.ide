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
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import gov.redhawk.ide.debug.LocalLaunch;
import gov.redhawk.ide.debug.SpdLauncherUtil;

/**
 * This feature gives the ability to terminate a locally-launched resource.
 */
public class TerminateFeature extends NonUndoableCustomFeature {

	public TerminateFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return "Terminate";
	}

	@Override
	public String getDescription() {
		return "Terminate the process(es) of the resource";
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

		// Selected objects must be have an ILaunch or we can't terminate them
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
			SpdLauncherUtil.terminate(localLaunch);
		}
	}

	@Override
	public String getImageId() {
		// Don't include in hover pad
		// return gov.redhawk.ide.graphiti.ui.diagram.providers.ImageProvider.IMG_TERMINATE;
		return null;
	}

}
