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
package gov.redhawk.ide.sad.graphiti.ui.diagram.providers;

import gov.redhawk.ide.sad.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.sad.graphiti.ui.diagram.features.add.CreateComponentFeature;
import gov.redhawk.ide.sad.graphiti.ui.diagram.features.custom.FindByEditFeature;
import gov.redhawk.ide.sad.graphiti.ui.diagram.palette.SpdToolEntry;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.FindByCORBANamePattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.FindByDomainManagerPattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.FindByEventChannelPattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.FindByFileManagerPattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.FindByServicePattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.HostCollocationPattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.SADConnectInterfacePattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.sdr.ComponentsContainer;
import gov.redhawk.ide.sdr.SdrPackage;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.spd.Code;
import mil.jpeojtrs.sca.spd.CodeFileType;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.palette.IToolEntry;
import org.eclipse.graphiti.palette.impl.ConnectionCreationToolEntry;
import org.eclipse.graphiti.palette.impl.ObjectCreationToolEntry;
import org.eclipse.graphiti.palette.impl.PaletteCompartmentEntry;
import org.eclipse.graphiti.tb.DefaultToolBehaviorProvider;
import org.eclipse.ui.progress.WorkbenchJob;

public class RHToolBehaviorProvider extends DefaultToolBehaviorProvider {

	public RHToolBehaviorProvider(final IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);

		// sync palette Components with Target SDR Components
		addComponentContainerRefreshJob(diagramTypeProvider);

	}

	/**
	 * Returns true if the business objects are equal. Overriding this because default implementation
	 * doesn't check the objects container and in some cases when attempting to automatically create connections
	 * in the diagram they are drawn to the wrong ports.
	 */
	@Override
	public boolean equalsBusinessObjects(Object o1, Object o2) {

		if (o1 instanceof ProvidesPortStub && o2 instanceof ProvidesPortStub) {
			ProvidesPortStub ps1 = (ProvidesPortStub) o1;
			ProvidesPortStub ps2 = (ProvidesPortStub) o2;
			boolean ecoreEqual = EcoreUtil.equals(ps1, ps2);
			if (ecoreEqual) {
				// ecore says they are equal, but lets verify their containers are the same
				return this.equalsBusinessObjects(ps1.eContainer(), ps2.eContainer());
			}
		} else if (o1 instanceof UsesPortStub && o2 instanceof UsesPortStub) {
			UsesPortStub ps1 = (UsesPortStub) o1;
			UsesPortStub ps2 = (UsesPortStub) o2;
			boolean ecoreEqual = EcoreUtil.equals(ps1, ps2);
			if (ecoreEqual) {
				// ecore says they are equal, but lets verify their containers are the same
				return this.equalsBusinessObjects(ps1.eContainer(), ps2.eContainer());
			}
		} else if (o1 instanceof ComponentSupportedInterfaceStub && o2 instanceof ComponentSupportedInterfaceStub) {
			ComponentSupportedInterfaceStub obj1 = (ComponentSupportedInterfaceStub) o1;
			ComponentSupportedInterfaceStub obj2 = (ComponentSupportedInterfaceStub) o2;
			boolean ecoreEqual = EcoreUtil.equals(obj1, obj2);
			if (ecoreEqual) {
				// ecore says they are equal, but lets verify their containers are the same
				return this.equalsBusinessObjects(obj1.eContainer(), obj2.eContainer());
			}
		}

		if (o1 instanceof EObject && o2 instanceof EObject) {
			return EcoreUtil.equals((EObject) o1, (EObject) o2);
		}
		// Both BOs have to be EMF objects. Otherwise the IndependenceSolver
		// does the job.
		return false;
	}

	/**
	 * Disable selection for PictogramElements that contain certain property values
	 */
	@Override
	public PictogramElement getSelection(PictogramElement originalPe, PictogramElement[] oldSelection) {

		if (originalPe instanceof FixPointAnchor || DUtil.doesPictogramContainProperty(originalPe, new String[] {// DiagramUtil.SHAPE_providesPortsContainerShape,
																													// DiagramUtil.SHAPE_usesPortsContainerShape,
				RHContainerShapeImpl.SHAPE_USES_PORT_RECTANGLE, RHContainerShapeImpl.SHAPE_PROVIDES_PORT_RECTANGLE, })) {
			return null;
		}

		// Always select outerContainershape instead of its contents
		if (DUtil.doesPictogramContainProperty(originalPe, new String[] { RHContainerShapeImpl.SHAPE_PROVIDES_PORT_CONTAINER,
			RHContainerShapeImpl.SHAPE_USES_PORT_CONTAINER, RHContainerShapeImpl.SHAPE_PROVIDES_PORTS_CONTAINER,
			RHContainerShapeImpl.SHAPE_USES_PORTS_CONTAINER, RHContainerShapeImpl.SHAPE_PROVIDES_PORT_CONTAINER,
			RHContainerShapeImpl.SHAPE_USES_PORT_CONTAINER, RHContainerShapeImpl.SHAPE_INTERFACE_CONTAINER,
			RHContainerShapeImpl.SHAPE_INTERFACE_ELLIPSE, RHContainerShapeImpl.SHAPE_INNER_CONTAINER })) {
			ContainerShape outerContainerShape = DUtil.findContainerShapeParentWithProperty(originalPe, RHContainerShapeImpl.SHAPE_OUTER_CONTAINER);
			return outerContainerShape;
		}
		return null;
	}

	/**
	 * Add a refresh listener job that will refresh the palette of the provided diagramTypeProvider
	 * every time Target SDR refreshes
	 * @param diagramTypeProvider
	 */
	private void addComponentContainerRefreshJob(final IDiagramTypeProvider diagramTypeProvider) {

		final ComponentsContainer container = SdrUiPlugin.getDefault().getTargetSdrRoot().getComponentsContainer();
		container.eAdapters().add(new AdapterImpl() {
			private final WorkbenchJob refreshPalletteJob = new WorkbenchJob("Refresh Pallette") {

				@Override
				public IStatus runInUIThread(final IProgressMonitor monitor) {
					// refresh pallete which will call RHToolBehaviorProvider.getPalette()
					diagramTypeProvider.getDiagramBehavior().refreshPalette();
					return Status.OK_STATUS;
				}

			};

			@Override
			public void notifyChanged(final Notification msg) {
				super.notifyChanged(msg);
				switch (msg.getFeatureID(ComponentsContainer.class)) {
				case SdrPackage.COMPONENTS_CONTAINER__COMPONENTS:
					this.refreshPalletteJob.schedule(1000); // SUPPRESS CHECKSTYLE MagicNumber
					break;
				default:
					break;
				}
			}
		});
	}

	/**
	 * Populates the palette entries.
	 */
	@Override
	public IPaletteCompartmentEntry[] getPalette() {

		// palette compartments
		List<IPaletteCompartmentEntry> compartments = new ArrayList<IPaletteCompartmentEntry>();

		// COMPONENT Compartment
		// get component container
		final ComponentsContainer container = SdrUiPlugin.getDefault().getTargetSdrRoot().getComponentsContainer();
		// populate compartment Entry with components from container
		PaletteCompartmentEntry componentCompartmentEntry = getComponentCompartmentEntry(container);
		compartments.add(componentCompartmentEntry);

		// FINDBY Compartment
		PaletteCompartmentEntry findByCompartmentEntry = getFindByCompartmentEntry();
		compartments.add(findByCompartmentEntry);

		// BASE TYPES Compartment
		PaletteCompartmentEntry baseTypesCompartmentEntry = getBaseTypesCompartmentEntry();
		compartments.add(baseTypesCompartmentEntry);

		// return palette compartments
		return compartments.toArray(new IPaletteCompartmentEntry[compartments.size()]);

	}

	/**
	 * Returns a populated CompartmentEntry containing all the Base Types
	 * @return
	 */
	private PaletteCompartmentEntry getBaseTypesCompartmentEntry() {

		final PaletteCompartmentEntry compartmentEntry = new PaletteCompartmentEntry("Base Types", null);

		IFeatureProvider featureProvider = getFeatureProvider();
		// connection
		ICreateConnectionFeature[] createConnectionFeatures = featureProvider.getCreateConnectionFeatures();
		if (createConnectionFeatures.length > 0) {
			for (ICreateConnectionFeature ccf : createConnectionFeatures) {
				if (SADConnectInterfacePattern.NAME.equals(ccf.getCreateName())) {
					ConnectionCreationToolEntry ccTool = new ConnectionCreationToolEntry(ccf.getCreateName(), ccf.getCreateDescription(),
						ccf.getCreateImageId(), ccf.getCreateLargeImageId());
					ccTool.addCreateConnectionFeature(ccf);
					compartmentEntry.addToolEntry(ccTool);
				}
			}

		}
		// host collocation
		ICreateFeature[] createFeatures = featureProvider.getCreateFeatures();
		for (ICreateFeature cf : createFeatures) {
			if (HostCollocationPattern.NAME.equals(cf.getCreateName())) {
				ObjectCreationToolEntry objectCreationToolEntry = new ObjectCreationToolEntry(cf.getCreateName(), cf.getCreateDescription(),
					cf.getCreateImageId(), cf.getCreateLargeImageId(), cf);

				compartmentEntry.addToolEntry(objectCreationToolEntry);
			}
		}
		return compartmentEntry;
	}

	/**
	 * Returns a populated CompartmentEntry containing all the Find By tools
	 * @return
	 */
	private PaletteCompartmentEntry getFindByCompartmentEntry() {

		final PaletteCompartmentEntry compartmentEntry = new PaletteCompartmentEntry("Find By", null);
//		compartmentEntry.setDescription("Contains Find By tooling.");

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

	/**
	 * Returns a populated CompartmentEntry containing all components in Target SDR
	 * @param container
	 * @return
	 */
	private PaletteCompartmentEntry getComponentCompartmentEntry(final ComponentsContainer container) {

		final PaletteCompartmentEntry compartmentEntry = new PaletteCompartmentEntry("Components", null);

		// add all palette entries into a entriesToRemove list.
		final List<SpdToolEntry> entriesToRemove = new ArrayList<SpdToolEntry>();
		for (final Object obj : compartmentEntry.getToolEntries()) {
			if (obj instanceof SpdToolEntry) {
				entriesToRemove.add((SpdToolEntry) obj);
			}
		}

		// loop through all components in Target SDR and if present then remove it from the entriesToRemove list
		final EList<SoftPkg> components = container.getComponents();
		final SoftPkg[] componentsArray = components.toArray(new SoftPkg[components.size()]);
		spdLoop: for (final SoftPkg spd : componentsArray) {
			for (Implementation impl : spd.getImplementation()) {
				Code code = impl.getCode();
				if (code == null) {
					continue spdLoop;
				}
				CodeFileType type = code.getType();
				if (type == null) {
					continue spdLoop;
				}
				switch (type) {
				case EXECUTABLE:
					break;
				default:
					continue spdLoop;
				}
			}
			boolean foundTool = false;
			for (int i = 0; i < entriesToRemove.size(); i++) {
				final SpdToolEntry entry = entriesToRemove.get(i);
				if (entry.getSpdID().equals(spd.getId())) {
					foundTool = true;
					entriesToRemove.remove(i);
					break;
				}
			}
			if (!foundTool) {
				// special way of instantiating create feature
				// allows us to know which palette tool was used
				ICreateFeature createComponentFeature = new CreateComponentFeature(getFeatureProvider(), spd);
				final SpdToolEntry entry = new SpdToolEntry(spd, createComponentFeature);
				compartmentEntry.addToolEntry(entry);
			}
		}
		for (final IToolEntry entry : entriesToRemove) {
			compartmentEntry.getToolEntries().remove(entry);
		}

		// Sort the children
		final ArrayList<IToolEntry> top = new ArrayList<IToolEntry>();
		final ArrayList<IToolEntry> childrenToSort = new ArrayList<IToolEntry>();
		top.addAll(compartmentEntry.getToolEntries());
		for (final IToolEntry entry : top) {
			if (entry instanceof SpdToolEntry) {
				childrenToSort.add(entry);
			}
		}
		top.removeAll(childrenToSort);
		Collections.sort(childrenToSort, new Comparator<IToolEntry>() {

			@Override
			public int compare(final IToolEntry o1, final IToolEntry o2) {
				final String str1 = o1.getLabel();
				final String str2 = o2.getLabel();
				if (str1 == null) {
					if (str2 == null) {
						return 0;
					} else {
						return 1;
					}
				} else if (str2 == null) {
					return -1;
				} else {
					return str1.compareToIgnoreCase(str2);
				}
			}

		});
		top.addAll(childrenToSort);
		compartmentEntry.getToolEntries().clear();
		compartmentEntry.getToolEntries().addAll(childrenToSort);

		return compartmentEntry;
	}
	


    @Override
    public ICustomFeature getDoubleClickFeature(IDoubleClickContext context) {
        ICustomFeature customFeature =
            new FindByEditFeature(getFeatureProvider());
        if (customFeature.canExecute(context)) {
            return customFeature;
        }
 
        return super.getDoubleClickFeature(context);
    }


	// ATTEMPT at ERROR decorator on Connections
//	public IDecorator[] getDecorators(PictogramElement pe) {
//		IFeatureProvider featureProvider = getFeatureProvider();
//		//if(pe instanceof Connection) {
////			IDecorator imageRenderingDecorator = new ImageDecorator(IPlatformImageConstants.IMG_ECLIPSE_WARNING_TSK);
////			imageRenderingDecorator.setMessage("You should fix this");
//			IDecorator imageRenderingDecorator = new ImageDecorator(IPlatformImageConstants.IMG_ECLIPSE_ERROR_TSK);
//			imageRenderingDecorator.setMessage("You should fix this");
//			
//			return new IDecorator[] { imageRenderingDecorator };
//		//}
//		
//		//return super.getDecorators(pe);
//		
//	}

}
