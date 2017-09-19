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
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;

import gov.redhawk.ide.debug.LocalLaunch;
import gov.redhawk.ide.debug.SpdLauncherUtil;

/**
 * This feature gives the ability to terminate a locally-launched resource.
 * NOTE: The icon for this feature can be overridden in the associated ToolBehaviorProvider
 * @since 2.0
 */
public class TerminateFeature extends DefaultDeleteFeature {

	public TerminateFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return "Terminate";
	}

	@Override
	protected String getDeleteName(IDeleteContext context) {
		return "Terminate";
	}

	@Override
	public String getDescription() {
		return "Terminate the process(es) of the resource";
	}

	@Override
	public boolean canUndo(IContext context) {
		return false;
	}

	@Override
	public boolean canDelete(IDeleteContext context) {
		if (context.getPictogramElement() == null) {
			return false;
		}

		PictogramElement pe = context.getPictogramElement();
		LocalLaunch localLaunch = Platform.getAdapterManager().getAdapter(pe, LocalLaunch.class);
		if (localLaunch == null || localLaunch.getLaunch() == null) {
			return false;
		}
		return true;
	}

	@Override
	public void delete(IDeleteContext context) {
		PictogramElement pe = context.getPictogramElement();
		LocalLaunch localLaunch = Platform.getAdapterManager().getAdapter(pe, LocalLaunch.class);
		SpdLauncherUtil.terminate(localLaunch);
	}
}
