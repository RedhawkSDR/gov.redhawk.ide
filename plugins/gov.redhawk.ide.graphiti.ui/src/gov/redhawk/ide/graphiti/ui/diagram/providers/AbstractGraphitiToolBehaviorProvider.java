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
package gov.redhawk.ide.graphiti.ui.diagram.providers;

import java.util.ArrayList;
import java.util.List;

import gov.redhawk.ide.graphiti.ui.diagram.feature.custom.FindByEditFeature;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByCORBANamePattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByDomainManagerPattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByEventChannelPattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByFileManagerPattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByServicePattern;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.palette.impl.ObjectCreationToolEntry;
import org.eclipse.graphiti.palette.impl.PaletteCompartmentEntry;
import org.eclipse.graphiti.tb.DefaultToolBehaviorProvider;

public abstract class AbstractGraphitiToolBehaviorProvider extends DefaultToolBehaviorProvider {

	// TODO: Make abstract
	
	/**
	 * @param diagramTypeProvider
	 */
	public AbstractGraphitiToolBehaviorProvider(IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);
	}
	
	/**
	 * Provides compartment entries that can be used extending ToolBehavoirProvider classes
	 */
	@Override
	public IPaletteCompartmentEntry[] getPalette() {
		List<IPaletteCompartmentEntry> compartments = new ArrayList<IPaletteCompartmentEntry>();
		
		// FINDBY Compartment
		if (!DUtil.isDiagramLocal(getDiagramTypeProvider().getDiagram())) {
			PaletteCompartmentEntry findByCompartmentEntry = getFindByCompartmentEntry();
			compartments.add(findByCompartmentEntry);
		}
		
		return compartments.toArray(new IPaletteCompartmentEntry[compartments.size()]);
	}

	private PaletteCompartmentEntry getFindByCompartmentEntry() {

		final PaletteCompartmentEntry compartmentEntry = new PaletteCompartmentEntry("Find By", null);

		IFeatureProvider featureProvider = getFeatureProvider();
		ICreateFeature[] createFeatures = featureProvider.getCreateFeatures();
		for (ICreateFeature cf : createFeatures) {
			if (FindByCORBANamePattern.NAME.equals(cf.getCreateName()) || FindByEventChannelPattern.NAME.equals(cf.getCreateName())
				|| FindByServicePattern.NAME.equals(cf.getCreateName()) || FindByFileManagerPattern.NAME.equals(cf.getCreateName())
				|| FindByDomainManagerPattern.NAME.equals(cf.getCreateName())) {
				ObjectCreationToolEntry objectCreationToolEntry = new ObjectCreationToolEntry(cf.getCreateName(), cf.getCreateDescription(),
					cf.getCreateImageId(), cf.getCreateLargeImageId(), cf);

				compartmentEntry.addToolEntry(objectCreationToolEntry);
			}
		}

		return compartmentEntry;
	}
	
	@Override
	public ICustomFeature getDoubleClickFeature(IDoubleClickContext context) {
		ICustomFeature customFeature = new FindByEditFeature(getFeatureProvider());
		if (customFeature.canExecute(context)) {
			return customFeature;
		}

		return super.getDoubleClickFeature(context);
	}

}
