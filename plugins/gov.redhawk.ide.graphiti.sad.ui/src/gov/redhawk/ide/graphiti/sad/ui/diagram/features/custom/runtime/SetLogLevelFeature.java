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
package gov.redhawk.ide.graphiti.sad.ui.diagram.features.custom.runtime;

import gov.redhawk.ide.graphiti.sad.ext.ComponentShape;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.NonUndoableCustomFeature;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.logging.ui.handlers.SetLoggingLevel;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

import org.eclipse.core.runtime.Platform;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.internal.parts.ContainerShapeEditPart;
import org.eclipse.swt.widgets.Display;

import CF.LoggingOperations;

@SuppressWarnings("restriction")
public class SetLogLevelFeature extends NonUndoableCustomFeature {

	public SetLogLevelFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getDescription() {
		return "Show/Set Log Level";
	}

	@Override
	public String getName() {
		return "Log Level";
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		if (context.getPictogramElements().length == 1) {
			ComponentShape componentShape = (ComponentShape) context.getPictogramElements()[0];
			Object object = DUtil.getBusinessObject(componentShape);
			Diagram diagram = DUtil.findDiagram((ContainerShape) componentShape);
			if (object instanceof SadComponentInstantiation
					&& (DUtil.isDiagramRuntime(diagram) || DUtil.isDiagramTargetSdr(diagram))) {
				return true;
			}
		}
		return super.canExecute(context);
	}

	@Override
	public void execute(ICustomContext context) {
		Object[] selection = DUtil.getSelectedEditParts();
		for (Object obj : selection) {
			if (obj instanceof ContainerShapeEditPart) {
				ContainerShapeEditPart csep = (ContainerShapeEditPart) obj;
				Object resourceObject = Platform.getAdapterManager().getAdapter(csep, LoggingOperations.class);
				if (resourceObject != null) {
					LoggingOperations loggingResource = (LoggingOperations) resourceObject;
					SetLoggingLevel setLoggingLevelHandler = new SetLoggingLevel();
					setLoggingLevelHandler.handleSetLoggingLevel(loggingResource, Display.getCurrent().getActiveShell());
				}
			}
		}
	}
}
