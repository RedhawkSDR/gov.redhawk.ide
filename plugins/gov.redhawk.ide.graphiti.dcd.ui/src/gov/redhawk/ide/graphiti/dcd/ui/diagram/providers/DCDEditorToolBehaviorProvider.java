/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.graphiti.dcd.ui.diagram.providers;

import java.util.List;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;

import gov.redhawk.ide.graphiti.ui.diagram.features.custom.IDialogEditingFeature;
import gov.redhawk.ide.graphiti.ui.diagram.providers.ConnectionValidationDecoratorProvider;

public class DCDEditorToolBehaviorProvider extends DCDPaletteToolBehaviorProvider {

	public DCDEditorToolBehaviorProvider(IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);

		ConnectionValidationDecoratorProvider validator = new ConnectionValidationDecoratorProvider();
		addDecoratorProvider(validator);
		addToolTipDelegate(validator);
	}

	@Override
	public ICustomFeature getDoubleClickFeature(IDoubleClickContext context) {
		// Check for a feature that supports dialog editing
		for (ICustomFeature feature : getFeatureProvider().getCustomFeatures(context)) {
			if (feature instanceof IDialogEditingFeature && feature.canExecute(context)) {
				return feature;
			}
		}

		return super.getDoubleClickFeature(context);
	}

	@Override
	protected void addPaletteCompartments(List<IPaletteCompartmentEntry> compartments) {
		super.addPaletteCompartments(compartments);

		compartments.add(createFindByCompartmentEntry());
	}
}
