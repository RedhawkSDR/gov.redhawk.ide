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

import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.FindByEditFeature;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.AbstractFindByPattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByCORBANamePattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByDomainManagerPattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByEventChannelPattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByFileManagerPattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByServicePattern;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.palette.RHGraphitiPaletteFilter;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.palette.impl.ObjectCreationToolEntry;
import org.eclipse.graphiti.palette.impl.PaletteCompartmentEntry;
import org.eclipse.graphiti.pattern.CreateFeatureForPattern;
import org.eclipse.graphiti.tb.DefaultToolBehaviorProvider;

public abstract class AbstractGraphitiToolBehaviorProvider extends DefaultToolBehaviorProvider {

	private RHGraphitiPaletteFilter paletteFilter;
	private PaletteCompartmentEntry findByCompartmentEntry;
	
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

	protected PaletteCompartmentEntry initializeCompartment(PaletteCompartmentEntry existing, String label) {
		if (existing != null) {
			existing.getToolEntries().clear();
			return existing;
		}
		return new PaletteCompartmentEntry(label, null);
	}
	
	private PaletteCompartmentEntry getFindByCompartmentEntry() {

		findByCompartmentEntry = initializeCompartment(findByCompartmentEntry, "Find By");
		final PaletteCompartmentEntry compartmentEntry = findByCompartmentEntry;

		IFeatureProvider featureProvider = getFeatureProvider();
		ICreateFeature[] createFeatures = featureProvider.getCreateFeatures();
		for (ICreateFeature cf : createFeatures) {
			// IDE-1020: instanceof conditions added to exclude similarly-named non-findby create features
			if ((cf instanceof CreateFeatureForPattern && ((CreateFeatureForPattern) cf).getPattern() instanceof AbstractFindByPattern) 
					&& (FindByCORBANamePattern.NAME.equals(cf.getCreateName()) || FindByEventChannelPattern.NAME.equals(cf.getCreateName())
				|| FindByServicePattern.NAME.equals(cf.getCreateName()) || FindByFileManagerPattern.NAME.equals(cf.getCreateName())
				|| FindByDomainManagerPattern.NAME.equals(cf.getCreateName()))) {
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

	/**
	 * Disable selection for PictogramElements that contain certain property values
	 */
	@Override
	public PictogramElement getSelection(PictogramElement originalPe, PictogramElement[] oldSelection) {

		if (originalPe instanceof FixPointAnchor
			|| DUtil.doesPictogramContainProperty(originalPe, new String[] { RHContainerShapeImpl.SHAPE_USES_PORT_RECTANGLE,
				RHContainerShapeImpl.SHAPE_PROVIDES_PORT_RECTANGLE, })) {
			return null;
		}

		// Always select outerContainershape instead of its contents
		if (DUtil.doesPictogramContainProperty(originalPe, new String[] { RHContainerShapeImpl.SHAPE_PROVIDES_PORT_CONTAINER,
			RHContainerShapeImpl.SHAPE_USES_PORT_CONTAINER, RHContainerShapeImpl.SHAPE_PROVIDES_PORTS_CONTAINER,
			RHContainerShapeImpl.SHAPE_USES_PORTS_CONTAINER, RHContainerShapeImpl.SHAPE_PROVIDES_PORT_CONTAINER,
			RHContainerShapeImpl.SHAPE_USES_PORT_CONTAINER, RHContainerShapeImpl.SHAPE_INTERFACE_CONTAINER, RHContainerShapeImpl.SHAPE_INTERFACE_ELLIPSE,
			RHContainerShapeImpl.SHAPE_INNER_CONTAINER })) {
			ContainerShape outerContainerShape = DUtil.findContainerShapeParentWithProperty(originalPe, RHContainerShapeImpl.SHAPE_OUTER_CONTAINER);
			return outerContainerShape;
		}
		return null;
	}
	
	public boolean passesFilter(String id) {
		if (paletteFilter == null || paletteFilter.getFilter().isEmpty() || paletteFilter.matches(id)) {
			return true;
		}
		return false;
	}

	public void setFilter(RHGraphitiPaletteFilter filter) {
		paletteFilter = filter;
	}

}
