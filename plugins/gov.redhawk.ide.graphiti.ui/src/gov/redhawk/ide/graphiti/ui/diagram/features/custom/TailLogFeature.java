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
import org.eclipse.swt.widgets.Display;

import CF.LogConfigurationOperations;
import gov.redhawk.logging.ui.handlers.TailLog;
import gov.redhawk.model.sca.CorbaObjWrapper;
import gov.redhawk.model.sca.ScaDomainManager;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

/**
 * This feature gives access to the UI that lets the user "tail" the log of the resource.
 */
public class TailLogFeature extends AbstractLoggingFeature {

	public TailLogFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return "Tail Log";
	}

	@Override
	public String getDescription() {
		return "View the resource's log";
	}

	@Override
	public void execute(ICustomContext context) {
		PictogramElement pe = context.getPictogramElements()[0];

		CorbaObjWrapper< ? > wrapper = Platform.getAdapterManager().getAdapter(pe, CorbaObjWrapper.class);
		if (wrapper != null && (wrapper instanceof LogConfigurationOperations)) {
			ScaDomainManager domMgr = ScaEcoreUtils.getEContainerOfType(wrapper, ScaDomainManager.class);
			if (domMgr != null) {
				TailLog tailLogHandler = new TailLog();
				tailLogHandler.handleTailLog(Display.getCurrent().getActiveShell(), wrapper);
			}
		}
	}
}
