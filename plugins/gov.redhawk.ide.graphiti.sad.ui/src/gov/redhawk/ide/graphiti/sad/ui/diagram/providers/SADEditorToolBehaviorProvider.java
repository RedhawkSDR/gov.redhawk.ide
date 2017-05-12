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
package gov.redhawk.ide.graphiti.sad.ui.diagram.providers;

import java.util.List;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.palette.impl.ObjectCreationToolEntry;
import org.eclipse.graphiti.palette.impl.PaletteCompartmentEntry;

import gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.HostCollocationPattern;
import gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.UsesDeviceFrontEndTunerPattern;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.IDialogEditingFeature;
import gov.redhawk.ide.graphiti.ui.diagram.providers.ConnectionValidationDecoratorProvider;

public class SADEditorToolBehaviorProvider extends SADPaletteToolBehaviorProvider {

	public SADEditorToolBehaviorProvider(IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);

		ConnectionValidationDecoratorProvider connValidator = new ConnectionValidationDecoratorProvider();
		addDecoratorProvider(connValidator);
		addToolTipDelegate(connValidator);
		
		HostCollocationDecoratorProvider hcDecorator = new HostCollocationDecoratorProvider();
		addDecoratorProvider(hcDecorator);
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
		compartments.add(getAdvancedCompartmentEntry());
	}

	/**
	 * Returns a populated CompartmentEntry containing all the Base Types and Uses Device tools
	 */
	private PaletteCompartmentEntry getAdvancedCompartmentEntry() {
		PaletteCompartmentEntry compartmentEntry = new PaletteCompartmentEntry("Advanced", null);
		compartmentEntry.setInitiallyOpen(false);

		for (ICreateFeature cf : getFeatureProvider().getCreateFeatures()) {
			if (HostCollocationPattern.NAME.equals(cf.getCreateName()) || UsesDeviceFrontEndTunerPattern.NAME.equals(cf.getCreateName())) {
				ObjectCreationToolEntry objectCreationToolEntry = new ObjectCreationToolEntry(cf.getCreateName(), cf.getCreateDescription(),
					cf.getCreateImageId(), cf.getCreateLargeImageId(), cf);

				compartmentEntry.addToolEntry(objectCreationToolEntry);
			}
		}

		return compartmentEntry;
	}
}
