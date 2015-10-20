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

import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.FindByEditFeature;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.LogLevelFeature;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.AbstractFindByPattern;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.palette.PaletteTreeEntry;
import gov.redhawk.ide.sdr.ComponentsContainer;
import gov.redhawk.ide.sdr.SdrPackage;
import gov.redhawk.ide.sdr.SoftPkgRegistry;

import java.util.ArrayList;
import java.util.Arrays;
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
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.draw2d.IFigure;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.context.impl.LayoutContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.palette.IToolEntry;
import org.eclipse.graphiti.palette.impl.ObjectCreationToolEntry;
import org.eclipse.graphiti.palette.impl.PaletteCompartmentEntry;
import org.eclipse.graphiti.palette.impl.StackEntry;
import org.eclipse.graphiti.pattern.CreateFeatureForPattern;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.tb.ContextButtonEntry;
import org.eclipse.graphiti.tb.ContextMenuEntry;
import org.eclipse.graphiti.tb.DefaultToolBehaviorProvider;
import org.eclipse.graphiti.tb.IColorDecorator;
import org.eclipse.graphiti.tb.IContextButtonPadData;
import org.eclipse.graphiti.tb.IContextMenuEntry;
import org.eclipse.graphiti.tb.IDecorator;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.util.IColorConstant;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.progress.WorkbenchJob;

public abstract class AbstractGraphitiToolBehaviorProvider extends DefaultToolBehaviorProvider {

	protected final WorkbenchJob refreshPaletteJob = new WorkbenchJob("Refresh Palette") {
		@Override
		public IStatus runInUIThread(final IProgressMonitor monitor) {
			// refresh palette which will call GraphitiDCDToolBehaviorProvider.getPalette()
			getDiagramTypeProvider().getDiagramBehavior().refreshPalette();
			return Status.OK_STATUS;
		}
	};

	protected final Adapter sdrListener = new AdapterImpl() {
		@Override
		public void notifyChanged(final Notification msg) {
			if (msg.getFeatureID(ComponentsContainer.class) == SdrPackage.COMPONENTS_CONTAINER__COMPONENTS) {
				refreshPaletteJob.schedule(1000); // SUPPRESS CHECKSTYLE MagicNumber
			}
		}
	};

	private List<IPaletteCompartmentEntry> paletteCompartments;
	private List<SoftPkgRegistry> registries = new ArrayList<SoftPkgRegistry>();
	private List<IDecoratorProvider> decoratorProviders	= new ArrayList<IDecoratorProvider>();
	private List<IToolTipDelegate> tooltipDelegates = new ArrayList<IToolTipDelegate>();

	private PortMonitorDecoratorProvider portMonitor;
	private ConnectionHighlightingDecoratorProvider connectionHighlighter = new ConnectionHighlightingDecoratorProvider();

	/**
	 * @param diagramTypeProvider
	 */
	public AbstractGraphitiToolBehaviorProvider(IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);
		if (DUtil.isDiagramRuntime(diagramTypeProvider.getDiagram())) {
			portMonitor = new PortMonitorDecoratorProvider(diagramTypeProvider);
			addDecoratorProvider(portMonitor);
		} else {
			ConnectionValidationDecoratorProvider validator = new ConnectionValidationDecoratorProvider();
			addDecoratorProvider(validator);
			addToolTipDelegate(validator);
		}
		addDecoratorProvider(connectionHighlighter);
	}

	@Override
	public void dispose() {
		super.dispose();
		for (SoftPkgRegistry registry : registries) {
			registry.eAdapters().remove(sdrListener);
		}
		connectionHighlighter.dispose();
		if (portMonitor != null) {
			portMonitor.dispose();
		}
	}

	@Override
	public boolean isShowFlyoutPalette() {
		if (DUtil.isDiagramExplorer(getFeatureProvider().getDiagramTypeProvider().getDiagram())) {
			return false;
		}
		return super.isShowFlyoutPalette();
	}

	/**
	 * Turn on highlighting of potential connection endpoints that are compatible with source.
	 * @param source the starting Anchor of the connection
	 */
	public void startConnectionHighlighting(Anchor source) {
		connectionHighlighter.startHighlighting(source);
		if (portMonitor != null) {
			portMonitor.setEnabled(false);
		}
		getDiagramTypeProvider().getDiagramBehavior().refreshContent();
	}

	/**
	 * Turn off highlighting of potential connection endpoints.
	 */
	public void endConnectionHighlighting() {
		connectionHighlighter.endHighlighting();
		if (portMonitor != null) {
			portMonitor.setEnabled(true);
		}
		getDiagramTypeProvider().getDiagramBehavior().refreshContent();
	}

	public List<IDecoratorProvider> getDecoratorProviders() {
		 return decoratorProviders;
	}

	public void addDecoratorProvider(IDecoratorProvider provider) {
		getDecoratorProviders().add(provider);
	}

	public void removeDecoratorProvider(IDecoratorProvider provider) {
		if (decoratorProviders != null) {
			decoratorProviders.remove(provider);
		}
	}

	@Override
	public IDecorator[] getDecorators(PictogramElement pe) {
		List<IDecorator> decorators = new ArrayList<IDecorator>();
		for (IDecoratorProvider provider : getDecoratorProviders()) {
			decorators.addAll(Arrays.asList(provider.getDecorators(pe)));
		}
		if (pe instanceof Connection) {
			applyConnectionDecorators((Connection) pe, decorators);
		}
		return decorators.toArray(new IDecorator[decorators.size()]);
	}

	private void applyConnectionDecorators(Connection connection, List<IDecorator> decorators) {
		DiagramBehavior diagramBehavior = (DiagramBehavior) getDiagramTypeProvider().getDiagramBehavior();
		GraphicalViewer viewer = diagramBehavior.getDiagramContainer().getGraphicalViewer();
		GraphicalEditPart part = (GraphicalEditPart) viewer.getEditPartRegistry().get(connection);

		Color foreground = null;
		Color background = null;
		for (IDecorator decorator : decorators) {
			if (decorator instanceof IColorDecorator) {
				IColorDecorator colorDecorator = (IColorDecorator) decorator;
				if (colorDecorator.getForegroundColor() != null) {
					foreground = getSwtColor(colorDecorator.getForegroundColor());
				}
				if (colorDecorator.getBackgroundColor() != null) {
					background = getSwtColor(colorDecorator.getBackgroundColor());
				}
			}
		}

		// Assume that there's a 1:1 mapping between figure children and connection decorators, as there does not
		// appear to be any other way to reconcile the two
		List< ? > children = part.getFigure().getChildren();
		for (int index = 0; index < children.size(); index++) {
			ConnectionDecorator connectionDecorator = connection.getConnectionDecorators().get(index);
			if (!connectionDecorator.isActive()) {
				IFigure figure = (IFigure) children.get(index);
				refreshColors(figure, connectionDecorator.getGraphicsAlgorithm(), foreground, background);
			}
		}
	}

	private void refreshColors(IFigure figure, GraphicsAlgorithm ga, Color foreground, Color background) {
		if (foreground == null) {
			foreground = getSwtColor(Graphiti.getGaService().getForegroundColor(ga, true));
		}
		if (background == null) {
			background = getSwtColor(Graphiti.getGaService().getBackgroundColor(ga, true));
		}
		figure.setForegroundColor(foreground);
		figure.setBackgroundColor(background);
	}

	private Color getSwtColor(org.eclipse.graphiti.mm.algorithms.styles.Color color) {
		return new Color(null, color.getRed(), color.getGreen(), color.getBlue());
	}

	private Color getSwtColor(IColorConstant constant) {
		return new Color(null, constant.getRed(), constant.getGreen(), constant.getBlue());
	}

	public List<IToolTipDelegate> getToolTipDelegates() {
		return tooltipDelegates;
	}

	public void addToolTipDelegate(IToolTipDelegate provider) {
		tooltipDelegates.add(provider);
	}

	public void removeToolTipDelegate(IToolTipDelegate provider) {
		tooltipDelegates.remove(provider);
	}

	@Override
	public Object getToolTip(GraphicsAlgorithm ga) {
		for (IToolTipDelegate delegate : getToolTipDelegates()) {
			Object tooltip = delegate.getToolTip(ga);
			if (tooltip != null) {
				return tooltip;
			}
		}
		return null;
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
		// Both BOs have to be EMF objects. Otherwise the IndependenceSolver does the job.
		return false;
	}

	/**
	 * IDE-1021: Adds start/stop/etc. buttons to hover context button pad of component as applicable.
	 */
	@Override
	public IContextButtonPadData getContextButtonPad(IPictogramElementContext context) {
		// IDE-1061 allow button pad to appear when cursor is anywhere
		// inside the ComponentShape
		PictogramElement pe = context.getPictogramElement();
		if (pe instanceof Shape) {
			while (!(pe instanceof RHContainerShapeImpl || pe == null)) {
				pe = (PictogramElement) pe.eContainer();
			}
			if (pe == null) {
				return null;
			}
		}
		context = new LayoutContext(pe);

		IContextButtonPadData pad = super.getContextButtonPad(context);

		// Add domain-specific context buttons
		CustomContext cc = new CustomContext(new PictogramElement[] {pe});
		ICustomFeature[] cf = getFeatureProvider().getCustomFeatures(cc);
		for (ICustomFeature feature: cf) {
			if (feature.getImageId() != null && feature.isAvailable(cc) && feature.canExecute(cc)) {
				pad.getDomainSpecificContextButtons().add(new ContextButtonEntry(feature, cc));
			}
		}
		return pad;
	}

	@Override
	public IContextMenuEntry[] getContextMenu(ICustomContext context) {
		List<IContextMenuEntry> contextMenuItems = new ArrayList<IContextMenuEntry>();

		for (ICustomFeature customFeature : getFeatureProvider().getCustomFeatures(context)) {
			ContextMenuEntry entry = new ContextMenuEntry(customFeature, context);
			if (customFeature instanceof LogLevelFeature) {
				// Create a sub-menu for logging
				ContextMenuEntry loggingSubMenu = new ContextMenuEntry(null, context);
				loggingSubMenu.setText("Logging");
				loggingSubMenu.setDescription("Logging");
				loggingSubMenu.setSubmenu(true);
				contextMenuItems.add(0, loggingSubMenu);

				// Make the log level feature a sub-entry of this menu
				loggingSubMenu.add(entry);
			} else {
				contextMenuItems.add(entry);
			}
		}

		return contextMenuItems.toArray(new IContextMenuEntry[contextMenuItems.size()]);
	}

	/**
	 * Provides compartment entries that can be used extending ToolBehavoirProvider classes
	 */
	@Override
	public IPaletteCompartmentEntry[] getPalette() {
		if (paletteCompartments == null) {
			paletteCompartments = new ArrayList<IPaletteCompartmentEntry>();
			addPaletteCompartments(paletteCompartments);
		}

		// Allow subclasses to refresh their palette entries
		refreshPalette();

		return paletteCompartments.toArray(new IPaletteCompartmentEntry[paletteCompartments.size()]);
	}

	protected void addPaletteCompartments(List<IPaletteCompartmentEntry> compartments) {
		if (!DUtil.isDiagramRuntime(getDiagramTypeProvider().getDiagram())) {
			compartments.add(createFindByCompartmentEntry());
		}
	}

	protected void refreshPalette() {
	}

	private PaletteCompartmentEntry createFindByCompartmentEntry() {
		PaletteCompartmentEntry compartmentEntry = new PaletteCompartmentEntry("Find By", null);

		for (ICreateFeature cf : getFeatureProvider().getCreateFeatures()) {
			// Search for create features that use a pattern derived from the abstract FindBy patten
			if ((cf instanceof CreateFeatureForPattern && ((CreateFeatureForPattern) cf).getPattern() instanceof AbstractFindByPattern)) {
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
		// Select ports directly (including super ports)
		if (originalPe instanceof Anchor) {
			Object bo = DUtil.getBusinessObject(originalPe);
			if (bo instanceof UsesPortStub || bo instanceof ProvidesPortStub) {
				return null;
			}
		}

		// Select the container shape itself
		if (originalPe instanceof RHContainerShape) {
			return null;
		}

		// Otherwise, always select outer container instead of its contents
		return ScaEcoreUtils.getEContainerOfType(originalPe, RHContainerShape.class);
	}
	
	protected void sort(List<IToolEntry> entries) {
		Collections.sort(entries, new Comparator<IToolEntry>() {

			@Override
			public int compare(final IToolEntry o1, final IToolEntry o2) {
				// Put the namespace folders together at the top
				if (o1 instanceof PaletteTreeEntry && !(o2 instanceof PaletteTreeEntry)) {
					return -1;
				}
				if (o2 instanceof PaletteTreeEntry && !(o1 instanceof PaletteTreeEntry)) {
					return 1;
				}
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
		for (IToolEntry entry: entries) {
			if (entry instanceof PaletteTreeEntry) {
				sort(((PaletteTreeEntry) entry).getToolEntries());
			}
		}
	}

	protected String getLastSegment(String[] segments) {
		if (segments == null || segments.length < 1) {
			return "";
		}
		return segments[segments.length - 1];
	}
	
	private PaletteTreeEntry getSegmentEntry(PaletteCompartmentEntry parent, String label) {
		if (label == null) {
			return null;
		}
		if (parent == null) {
			return new PaletteTreeEntry(label);
		}
		for (IToolEntry entry: parent.getToolEntries()) {
			if (entry instanceof PaletteTreeEntry && label.equals(entry.getLabel())) {
				return (PaletteTreeEntry) entry;
			}
		}
		return new PaletteTreeEntry(label, parent);
	}
	
	protected void addToolToCompartment(PaletteCompartmentEntry compartment, SoftPkg spd, String iconId) {
		Assert.isNotNull(compartment, "Cannot add tool to non-existent compartment");
		String[] segments = getNameSegments(spd);
		PaletteCompartmentEntry folder = compartment;
		for (int index = 0; index < segments.length - 1; ++index) {
			folder = getSegmentEntry(folder, segments[index]);
		}
		folder.addToolEntry(makeTool(spd, iconId));
	}
	
	/**
	 * Creates a new IToolEntry for each implementation in the component description.
	 * Also assigns the createComponentFeature to the palette entry so that the diagram knows which shape to create.
	 */
	private List<IToolEntry> createPaletteEntries(SoftPkg spd, String iconId) {
		String label = getLastSegment(getNameSegments(spd));
		List<IToolEntry> entries = new ArrayList<IToolEntry>(spd.getImplementation().size());
		if (spd.getImplementation().size() == 1 || DUtil.isDiagramWorkpace(this.getDiagramTypeProvider().getDiagram())) {
			entries.add(createSpdToolEntry(label, spd, null, iconId));
		} else {
			for (Implementation impl : spd.getImplementation()) {
				entries.add(createSpdToolEntry(label, spd, impl.getId(), iconId));
			}
		}
		return entries;
	}

	private ObjectCreationToolEntry createSpdToolEntry(String label, SoftPkg spd, String implId, String iconId) {
		if (implId == null) {
			// Use default implementation
			implId = spd.getImplementation().get(0).getId();
		} else {
			// Implementation given, include it in the label
			label = label + " (" + implId + ")";
		}
		String description = spd.getDescription();
		if (description == null) {
			description = "Create a new instance of \"" + label + "\"";
		}
		ICreateFeature createComponentFeature = getCreateFeature(spd, implId, iconId);
		return new ObjectCreationToolEntry(label, description, iconId, null, createComponentFeature);
	}

	protected boolean isExecutable(SoftPkg spd) {
		for (Implementation impl : spd.getImplementation()) {
			Code code = impl.getCode();
			if (code == null) {
				return false;
			}
			CodeFileType type = code.getType();
			if (type == null) {
				return false;
			}
			return type == CodeFileType.EXECUTABLE;
		}
		return false;
	}

	private IToolEntry makeTool(SoftPkg spd, String iconId) {
		List<IToolEntry> newEntries = createPaletteEntries(spd, iconId);
		if (newEntries != null && newEntries.size() > 1) {
			sort(newEntries);
			IToolEntry firstEntry = newEntries.get(0);
			StackEntry stackEntry = new StackEntry(firstEntry.getLabel(), ((ObjectCreationToolEntry) firstEntry).getDescription(), firstEntry.getIconId());
			for (IToolEntry entry : newEntries) {
				stackEntry.addCreationToolEntry((ObjectCreationToolEntry) entry);
			}
			return stackEntry;
		}
		return newEntries.get(0);
	}

	protected String[] getNameSegments(SoftPkg spd) {
		String fullName = spd.getName();
		if (fullName == null) {
			return new String[] {""};
		}
		if (!fullName.contains(".")) {
			return new String[] {fullName};
		}
		return fullName.split("\\.");
	}

	protected void refreshCompartmentEntry(PaletteCompartmentEntry compartmentEntry, SoftPkgRegistry container, String iconId) {
		compartmentEntry.getToolEntries().clear();

		for (SoftPkg spd : container.getComponents()) {
			if (isExecutable(spd)) {
				addToolToCompartment(compartmentEntry, spd, iconId);
			}
		}

		sort(compartmentEntry.getToolEntries());
	}
	
	/**
	 * Add a refresh listener job that will refresh the palette of the associated diagramTypeProvider every time
	 * the Target SDR refreshes
	 */
	protected void addTargetSdrRefreshJob(SoftPkgRegistry container) {
		container.eAdapters().add(sdrListener);
		registries.add(container);
	}

	protected abstract ICreateFeature getCreateFeature(SoftPkg spd, String implId, String iconId);

}
