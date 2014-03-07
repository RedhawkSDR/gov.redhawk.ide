/*******************************************************************************
 * <copyright>
 *
 * Copyright (c) 2005, 2010 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP AG - initial API, implementation and documentation
 *
 * </copyright>
 *
 *******************************************************************************/
package gov.redhawk.ide.graphiti.example.diagram;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import gov.redhawk.ide.graphiti.example.TutorialImageProvider;
import gov.redhawk.ide.graphiti.example.features.TutorialCollapseDummyFeature;
import gov.redhawk.ide.graphiti.example.features.TutorialRenameEClassFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.impl.CreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.palette.impl.ConnectionCreationToolEntry;
import org.eclipse.graphiti.palette.impl.ObjectCreationToolEntry;
import org.eclipse.graphiti.palette.impl.PaletteCompartmentEntry;
import org.eclipse.graphiti.palette.impl.StackEntry;
import org.eclipse.graphiti.platform.IPlatformImageConstants;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.tb.ContextButtonEntry;
import org.eclipse.graphiti.tb.ContextEntryHelper;
import org.eclipse.graphiti.tb.ContextMenuEntry;
import org.eclipse.graphiti.tb.DefaultToolBehaviorProvider;
import org.eclipse.graphiti.tb.IContextButtonEntry;
import org.eclipse.graphiti.tb.IContextButtonPadData;
import org.eclipse.graphiti.tb.IContextMenuEntry;
import org.eclipse.graphiti.tb.IDecorator;
import org.eclipse.graphiti.tb.ImageDecorator;

public class TutorialToolBehaviorProvider extends DefaultToolBehaviorProvider {

	public TutorialToolBehaviorProvider(IDiagramTypeProvider dtp) {
		super(dtp);
	}

	@Override
	public IContextButtonPadData getContextButtonPad(IPictogramElementContext context) {
		IContextButtonPadData data = super.getContextButtonPad(context);
		PictogramElement pe = context.getPictogramElement();

		// 1. set the generic context buttons
		// note, that we do not add 'remove' (just as an example)
		setGenericContextButtons(data, pe, CONTEXT_BUTTON_DELETE | CONTEXT_BUTTON_UPDATE);

		// 2. set the collapse button
		// simply use a dummy custom feature (senseless example)
		CustomContext cc = new CustomContext(new PictogramElement[] { pe });
		ICustomFeature[] cf = getFeatureProvider().getCustomFeatures(cc);
		for (int i = 0; i < cf.length; i++) {
			ICustomFeature iCustomFeature = cf[i];
			if (iCustomFeature instanceof TutorialCollapseDummyFeature) {
				IContextButtonEntry collapseButton = ContextEntryHelper.createCollapseContextButton(true, iCustomFeature, cc);
				data.setCollapseContextButton(collapseButton);
				break;
			}
		}

		// 3. add one domain specific context-button, which offers all
		// available connection-features as drag&drop features...

		// 3.a. create new CreateConnectionContext
		CreateConnectionContext ccc = new CreateConnectionContext();
		ccc.setSourcePictogramElement(pe);
		Anchor anchor = null;
		if (pe instanceof Anchor) {
			anchor = (Anchor) pe;
		} else if (pe instanceof AnchorContainer) {
			// assume, that our shapes always have chopbox anchors
			anchor = Graphiti.getPeService().getChopboxAnchor((AnchorContainer) pe);
		}
		ccc.setSourceAnchor(anchor);

		// 3.b. create context button and add all applicable features
		ContextButtonEntry button = new ContextButtonEntry(null, context);
		button.setText("Create connection"); //$NON-NLS-1$
		button.setIconId(TutorialImageProvider.IMG_EREFERENCE);
		ICreateConnectionFeature[] features = getFeatureProvider().getCreateConnectionFeatures();
		for (ICreateConnectionFeature feature : features) {
			if (feature.isAvailable(ccc) && feature.canStartConnection(ccc))
				button.addDragAndDropFeature(feature);
		}

		// 3.c. add context button, if it contains at least one feature
		if (button.getDragAndDropFeatures().size() > 0) {
			data.getDomainSpecificContextButtons().add(button);
		}

		return data;
	}

	@Override
	public IContextMenuEntry[] getContextMenu(ICustomContext context) {
		// create a sub-menu for all custom features
		ContextMenuEntry subMenu = new ContextMenuEntry(null, context);
		subMenu.setText("Cu&stom"); //$NON-NLS-1$
		subMenu.setDescription("Custom features submenu"); //$NON-NLS-1$
		// display sub-menu hierarchical or flat
		subMenu.setSubmenu(true);

		// create a menu-entry in the sub-menu for each custom feature
		ICustomFeature[] customFeatures = getFeatureProvider().getCustomFeatures(context);
		for (int i = 0; i < customFeatures.length; i++) {
			ICustomFeature customFeature = customFeatures[i];
			if (customFeature.isAvailable(context)) {
				ContextMenuEntry menuEntry = new ContextMenuEntry(customFeature, context);
				subMenu.add(menuEntry);
			}
		}

		IContextMenuEntry ret[] = new IContextMenuEntry[] { subMenu };
		return ret;
	}

	@Override
	public IPaletteCompartmentEntry[] getPalette() {
		List<IPaletteCompartmentEntry> ret = new ArrayList<IPaletteCompartmentEntry>();

		// add compartments from super class
		IPaletteCompartmentEntry[] superCompartments = super.getPalette();
		for (int i = 0; i < superCompartments.length; i++)
			ret.add(superCompartments[i]);

		// add new compartment at the end of the existing compartments
		PaletteCompartmentEntry compartmentEntry = new PaletteCompartmentEntry("Stacked", null); //$NON-NLS-1$
		ret.add(compartmentEntry);

		// add new stack entry to new compartment
		StackEntry stackEntry = new StackEntry("EObject", "EObject", null); //$NON-NLS-1$ //$NON-NLS-2$
		compartmentEntry.addToolEntry(stackEntry);

		// add all create-features to the new stack-entry
		IFeatureProvider featureProvider = getFeatureProvider();
		ICreateFeature[] createFeatures = featureProvider.getCreateFeatures();
		for (ICreateFeature cf : createFeatures) {
			ObjectCreationToolEntry objectCreationToolEntry = new ObjectCreationToolEntry(cf.getCreateName(), cf.getCreateDescription(),
					cf.getCreateImageId(), cf.getCreateLargeImageId(), cf);

			stackEntry.addCreationToolEntry(objectCreationToolEntry);
		}

		// add all create-connection-features to the new stack-entry
		ICreateConnectionFeature[] createConnectionFeatures = featureProvider.getCreateConnectionFeatures();
		for (ICreateConnectionFeature cf : createConnectionFeatures) {
			ConnectionCreationToolEntry connectionCreationToolEntry = new ConnectionCreationToolEntry(cf.getCreateName(),
					cf.getCreateDescription(), cf.getCreateImageId(), cf.getCreateLargeImageId());
			connectionCreationToolEntry.addCreateConnectionFeature(cf);
			stackEntry.addCreationToolEntry(connectionCreationToolEntry);
		}

		return ret.toArray(new IPaletteCompartmentEntry[ret.size()]);
	}

	@Override
	public ICustomFeature getDoubleClickFeature(IDoubleClickContext context) {
		ICustomFeature customFeature = new TutorialRenameEClassFeature(getFeatureProvider());
		// canExecute() tests especially if the context contains a EClass
		if (customFeature.canExecute(context)) {
			return customFeature;
		}

		return super.getDoubleClickFeature(context);
	}

	@Override
	public IDecorator[] getDecorators(PictogramElement pe) {
		IFeatureProvider featureProvider = getFeatureProvider();
		Object bo = featureProvider.getBusinessObjectForPictogramElement(pe);
		if (bo instanceof EClass) {
			EClass eClass = (EClass) bo;
			String name = eClass.getName();
			if (name != null && name.length() > 0 && !(name.charAt(0) >= 'A' && name.charAt(0) <= 'Z')) {
				IDecorator imageRenderingDecorator = new ImageDecorator(IPlatformImageConstants.IMG_ECLIPSE_WARNING_TSK);
				imageRenderingDecorator.setMessage("Name should start with upper case letter"); //$NON-NLS-1$
				return new IDecorator[] { imageRenderingDecorator };
			}
		}

		return super.getDecorators(pe);
	}

	@Override
	public GraphicsAlgorithm[] getClickArea(PictogramElement pe) {
		IFeatureProvider featureProvider = getFeatureProvider();
		Object bo = featureProvider.getBusinessObjectForPictogramElement(pe);
		if (bo instanceof EClass) {
			GraphicsAlgorithm invisible = pe.getGraphicsAlgorithm();
			GraphicsAlgorithm rectangle = invisible.getGraphicsAlgorithmChildren().get(0);
			return new GraphicsAlgorithm[] { rectangle };
		}
		return super.getClickArea(pe);
	}

	@Override
	public GraphicsAlgorithm getSelectionBorder(PictogramElement pe) {
		if (pe instanceof ContainerShape) {
			GraphicsAlgorithm invisible = pe.getGraphicsAlgorithm();
			if (!invisible.getLineVisible()) {
				EList<GraphicsAlgorithm> graphicsAlgorithmChildren = invisible.getGraphicsAlgorithmChildren();
				if (!graphicsAlgorithmChildren.isEmpty()) {
					return graphicsAlgorithmChildren.get(0);
				}
			}
		}
		return super.getSelectionBorder(pe);
	}

	@Override
	public String getToolTip(GraphicsAlgorithm ga) {
		PictogramElement pe = ga.getPictogramElement();
		Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(pe);
		if (bo instanceof EClass) {
			String name = ((EClass) bo).getName();
			if (name != null && !(name.length() == 0)) {
				return name;
			}
		}
		return super.getToolTip(ga);
	}

}
