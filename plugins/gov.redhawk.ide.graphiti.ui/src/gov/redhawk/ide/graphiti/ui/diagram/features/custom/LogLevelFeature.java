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

import CF.LoggingOperations;
import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.logging.ui.handlers.SetLoggingLevel;
import mil.jpeojtrs.sca.partitioning.ComponentInstantiation;

/**
 * This feature gives access to the UI that lets the user view/change the logging level of the resource.
 */
public class LogLevelFeature extends NonUndoableCustomFeature {

	public LogLevelFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return "Log Level";
	}

	@Override
	public String getDescription() {
		return "Show/set the resource's logging level";
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		// It only makes sense to allow the user to do this with one selected resource
		if (context.getPictogramElements().length != 1) {
			return false;
		}

		RHContainerShape componentShape = (RHContainerShape) context.getPictogramElements()[0];
		Object object = DUtil.getBusinessObject(componentShape);
		return (object instanceof ComponentInstantiation) && componentShape.isEnabled();
	}

	@Override
	public void execute(ICustomContext context) {
		PictogramElement pe = context.getPictogramElements()[0];
		LoggingOperations loggingOperations = Platform.getAdapterManager().getAdapter(pe, LoggingOperations.class);
		if (loggingOperations != null) {
			SetLoggingLevel setLoggingLevelHandler = new SetLoggingLevel();
			setLoggingLevelHandler.handleSetLoggingLevel(loggingOperations, Display.getCurrent().getActiveShell());
		}
	}
}
