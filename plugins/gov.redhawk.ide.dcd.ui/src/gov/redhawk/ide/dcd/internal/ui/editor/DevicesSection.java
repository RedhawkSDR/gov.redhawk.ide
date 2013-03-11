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
package gov.redhawk.ide.dcd.internal.ui.editor;

import gov.redhawk.ide.dcd.internal.ui.ComponentPlacementContentProvider;
import gov.redhawk.ide.dcd.internal.ui.editor.provider.DcdItemProviderAdapterFactoryAdapter;
import gov.redhawk.ide.dcd.internal.ui.editor.provider.DevicesSectionComponentPlacementItemProvider;
import gov.redhawk.ide.dcd.ui.wizard.ScaNodeProjectDevicesWizardPage;
import gov.redhawk.ide.sdr.LoadState;
import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import gov.redhawk.model.sca.util.ModelUtil;
import gov.redhawk.ui.actions.SortAction;
import gov.redhawk.ui.editor.TreeSection;
import gov.redhawk.ui.parts.FormFilteredTree;
import gov.redhawk.ui.parts.TreePart;
import gov.redhawk.ui.util.SCAEditorUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;
import mil.jpeojtrs.sca.dcd.DcdComponentPlacement;
import mil.jpeojtrs.sca.dcd.DcdFactory;
import mil.jpeojtrs.sca.dcd.DcdPackage;
import mil.jpeojtrs.sca.dcd.DcdPartitioning;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.dcd.provider.DcdItemProviderAdapterFactory;
import mil.jpeojtrs.sca.partitioning.ComponentFile;
import mil.jpeojtrs.sca.partitioning.ComponentFileRef;
import mil.jpeojtrs.sca.partitioning.ComponentFiles;
import mil.jpeojtrs.sca.partitioning.ComponentPlacement;
import mil.jpeojtrs.sca.partitioning.PartitioningFactory;
import mil.jpeojtrs.sca.partitioning.PartitioningPackage;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.emf.databinding.edit.EMFEditObservables;
import org.eclipse.emf.ecore.provider.EcoreItemProviderAdapterFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.progress.WorkbenchJob;

/**
 * 
 */
public class DevicesSection extends TreeSection implements IPropertyChangeListener {

	private static final int BUTTON_REMOVE = 1;
	private static final int BUTTON_ADD = 0;
	protected static final long SDR_REFRESH_DELAY = 500;

	private FormFilteredTree fFilteredTree;
	private TreeViewer fExtensionTree;
	private SortAction fSortAction;
	private ComposedAdapterFactory adapterFactory;
	private Resource dcdResource;
	private SoftPkg[] devices;

	private boolean disposed;
	private boolean editable;

	private DataBindingContext context;

	private DcdComponentPlacement lastComp;

	private final WorkbenchJob refreshViewerJob = new WorkbenchJob("Refresh") {

		@Override
		public IStatus runInUIThread(final IProgressMonitor monitor) {
			DevicesSection.this.fExtensionTree.refresh();
			return Status.OK_STATUS;
		}
	};

	private final EContentAdapter refreshAdapter = new EContentAdapter() {
		@Override
		public void notifyChanged(final Notification notification) {
			super.notifyChanged(notification);
			final Object feature = notification.getFeature();
			if (feature == DcdPackage.Literals.DEVICE_CONFIGURATION__PARTITIONING || feature == PartitioningPackage.Literals.PARTITIONING__COMPONENT_PLACEMENT
			        || feature == DcdPackage.Literals.DCD_COMPONENT_PLACEMENT__COMPOSITE_PART_OF_DEVICE
			        || feature == DcdPackage.Literals.COMPOSITE_PART_OF_DEVICE__REF_ID) {
				DevicesSection.this.refreshViewerJob.schedule(10); // SUPPRESS CHECKSTYLE MagicNumber
			}
		}
	};

	/**
	 * The Constructor.
	 * 
	 * @param page the page
	 * @param parent the parent
	 */
	public DevicesSection(final DevicesPage page, final Composite parent) {
		super(page, parent, Section.DESCRIPTION, new String[] { "Add...", "Remove" });
		this.fHandleDefaultButton = false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void selectionChanged(final IStructuredSelection selection) {
		getPage().setSelection(selection);
		updateButtons(selection);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DevicesPage getPage() {
		return (DevicesPage) super.getPage();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createClient(final Section section, final FormToolkit toolkit) {

		final Composite container = createClientContainer(section, 2, toolkit);
		final TreePart treePart = getTreePart();
		createViewerPartControl(container, SWT.MULTI, 2, toolkit);
		this.fExtensionTree = treePart.getTreeViewer();

		this.fExtensionTree.setContentProvider(new ComponentPlacementContentProvider());
		this.fExtensionTree.setLabelProvider(new DecoratingLabelProvider(new AdapterFactoryLabelProvider(getAdapterFactory()), PlatformUI.getWorkbench()
		        .getDecoratorManager().getLabelDecorator()) {

			@Override
			public String getText(final Object element) {
				if (element instanceof DcdComponentPlacement) {
					return ((DcdComponentPlacement) element).getComponentInstantiation().get(0).getUsageName();
				}
				return super.getText(element);
			}
		});

		this.fExtensionTree.setFilters(createComponentPlacementViewerFilter());

		toolkit.paintBordersFor(container);
		section.setClient(container);
		section.setDescription("Select devices to include in this node within the following section.");
		// See Bug # 160554: Set text before text client
		section.setText("All Devices");
		initialize();
		createSectionToolbar(section, toolkit);
		// Create the adapted listener for the filter entry field
		final Text filterText = this.fFilteredTree.getFilterControl();
		if (filterText != null) {
			filterText.addModifyListener(new ModifyListener() {
				public void modifyText(final ModifyEvent e) {
					final StructuredViewer viewer = getStructuredViewerPart().getViewer();
					final IStructuredSelection ssel = (IStructuredSelection) viewer.getSelection();
					updateButtons((ssel.size() != 1) ? null : ssel); // SUPPRESS CHECKSTYLE AvoidInline
				}
			});
		}
	}

	private ViewerFilter[] createComponentPlacementViewerFilter() {
		return new ViewerFilter[] { new ViewerFilter() {
			@Override
			public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
				return element instanceof ComponentPlacement;
			}
		} };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean setFormInput(final Object object) {
		if (object != null) {
			this.fExtensionTree.setSelection(new StructuredSelection(object), true);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		// Explicitly call the dispose method on the extensions tree
		if (this.fFilteredTree != null) {
			this.fFilteredTree.dispose();
		}
		this.adapterFactory.dispose();
		this.refreshAdapter.unsetTarget(this.dcdResource);
		super.dispose();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected TreeViewer createTreeViewer(final Composite parent, final int style) {
		this.fFilteredTree = new FormFilteredTree(parent, style, new PatternFilter());
		parent.setData("filtered", Boolean.TRUE); //$NON-NLS-1$
		return this.fFilteredTree.getViewer();
	}

	/**
	 * Gets the adapter factory.
	 * 
	 * @return the adapter factory
	 */
	private AdapterFactory getAdapterFactory() {
		if (this.adapterFactory == null) {
			this.adapterFactory = new ComposedAdapterFactory();
			final DcdItemProviderAdapterFactoryAdapter dcdAdapter = new DcdItemProviderAdapterFactoryAdapter();
			dcdAdapter.setComponentPlacementAdapter(new DevicesSectionComponentPlacementItemProvider(dcdAdapter));

			this.adapterFactory.addAdapterFactory(dcdAdapter);
			this.adapterFactory.addAdapterFactory(new DcdItemProviderAdapterFactory());
			this.adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());

			this.adapterFactory.addAdapterFactory(new EcoreItemProviderAdapterFactory());
			this.adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());
		}
		return this.adapterFactory;
	}

	/**
	 * Initialize.
	 * 
	 * @param model the model
	 */
	private void initialize() {
		selectFirstElement();
		final TreePart treePart = getTreePart();
		treePart.setButtonEnabled(DevicesSection.BUTTON_ADD, true);
		treePart.setButtonEnabled(DevicesSection.BUTTON_REMOVE, false);
	}

	/**
	 * Select first element.
	 */
	private void selectFirstElement() {
		final Tree tree = this.fExtensionTree.getTree();
		final TreeItem[] items = tree.getItems();
		if (items.length == 0) {
			return;
		}
		final TreeItem firstItem = items[0];
		final Object obj = firstItem.getData();
		this.fExtensionTree.setSelection(new StructuredSelection(obj));
	}

	/**
	 * Select the desired element
	 */
	private void selectElement(final DcdComponentPlacement placement) {
		final Tree tree = this.fExtensionTree.getTree();

		for (final TreeItem item : tree.getItems()) {
			final Object obj = item.getData();

			if (obj.equals(placement)) {
				this.fExtensionTree.setSelection(new StructuredSelection(obj));
				break;
			}
		}
	}

	/**
	 * Creates the section toolbar.
	 * 
	 * @param section the section
	 * @param toolkit the toolkit
	 */
	private void createSectionToolbar(final Section section, final FormToolkit toolkit) {
		final ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
		final ToolBar toolbar = toolBarManager.createControl(section);
		final Cursor handCursor = new Cursor(Display.getCurrent(), SWT.CURSOR_HAND);
		toolbar.setCursor(handCursor);
		// Cursor needs to be explicitly disposed
		toolbar.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(final DisposeEvent e) {
				if ((handCursor != null) && !handCursor.isDisposed()) {
					handCursor.dispose();
				}
			}
		});
		// Add sort action to the tool bar
		this.fSortAction = new SortAction(this.fExtensionTree, "Sort the properties alphabetically.", null, null, this);
		toolBarManager.add(this.fSortAction);

		toolBarManager.update(true);

		section.setTextClient(toolbar);
	}

	/**
	 * Update buttons.
	 * 
	 * @param item the item
	 */
	private void updateButtons(final Object item) {
		final boolean sorted = this.fSortAction != null && this.fSortAction.isChecked();
		if (sorted) {
			return;
		}

		final boolean filtered = this.fFilteredTree.isFiltered();
		boolean addEnabled = true;
		boolean removeEnabled = false;

		if (item != null && this.editable) {
			removeEnabled = true;
		}
		if (filtered || !this.editable) {
			// Fix for bug 194529 and bug 194828
			addEnabled = false;
		}

		getTreePart().setButtonEnabled(DevicesSection.BUTTON_ADD, addEnabled);
		getTreePart().setButtonEnabled(DevicesSection.BUTTON_REMOVE, removeEnabled);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void buttonSelected(final int index) {
		switch (index) {
		case BUTTON_ADD:
			handleNew();
			break;
		case BUTTON_REMOVE:
			handleDelete();
			break;
		default:
			break;
		}
	}

	/**
	 * Handle delete.
	 */
	private void handleDelete() {
		final IStructuredSelection selections = ((IStructuredSelection) getTreePart().getViewer().getSelection());

		if (selections != null) {
			List< ? > selectionList = selections.toList();
			for (Object selection : selectionList) {
				execute(RemoveCommand.create(getEditingDomain(), getDeviceConfiguration().getPartitioning(),
						PartitioningPackage.Literals.PARTITIONING__COMPONENT_PLACEMENT, selection));
			}
		}

		this.refresh(this.dcdResource);
	}

	/**
	 * Handle new.
	 */
	private void handleNew() {
		final SdrRoot sdrRoot = SdrUiPlugin.getDefault().getTargetSdrRoot();
		if (sdrRoot.getState() != LoadState.LOADED) {
			final IRunnableWithProgress waitForLoad = new IRunnableWithProgress() {

				public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask("Waiting for SDR Root to load", IProgressMonitor.UNKNOWN);
					sdrRoot.load(monitor);
				}

			};
			try {
				new ProgressMonitorDialog(getPage().getEditorSite().getWorkbenchWindow().getShell()).run(true, false, waitForLoad);
			} catch (final InvocationTargetException e) {
				return;
			} catch (final InterruptedException e) {
				return;
			}
		}

		final ScaNodeProjectDevicesWizardPage devWizardPage = new ScaNodeProjectDevicesWizardPage("Select Devices to Add", sdrRoot.getDevicesContainer()
		        .getComponents().toArray(new SoftPkg[0]));

		final Wizard wiz = new Wizard() {

			@Override
			public boolean performFinish() {
				final ScaNodeProjectDevicesWizardPage page = (ScaNodeProjectDevicesWizardPage) this.getPages()[0];

				if (page != null) {
					DevicesSection.this.devices = page.getNodeDevices();
				}

				return true;
			}

		};
		final WizardDialog dialog = new WizardDialog(getPage().getSite().getShell(), wiz);
		wiz.addPage(devWizardPage);

		if (dialog.open() == Window.OK) {
			final DeviceConfiguration dcd = getDeviceConfiguration();

			if (this.devices.length == 0 || dcd == null) {
				return;
			}

			final CompoundCommand command = new CompoundCommand("Add Devices");
			// First see if we need to add a componentfile for this
			ComponentFiles files = dcd.getComponentFiles();
			if (files == null) {
				files = PartitioningFactory.eINSTANCE.createComponentFiles();
				command.append(SetCommand.create(getEditingDomain(), dcd, DcdPackage.Literals.DEVICE_CONFIGURATION__COMPONENT_FILES, files));
			}

			for (final SoftPkg device : this.devices) {
				ComponentFile file = null;
				for (final ComponentFile f : files.getComponentFile()) {
					if (f == null) {
						continue;
					}
					final SoftPkg fSpd = f.getSoftPkg();
					if (fSpd != null && device.getId().equals(fSpd.getId())) {
						file = f;
						break;
					}
				}

				if (file == null) {
					file = DcdFactory.eINSTANCE.createComponentFile();
					file.setSoftPkg(device);
					command.append(AddCommand.create(getEditingDomain(), dcd.getComponentFiles(), PartitioningPackage.Literals.COMPONENT_FILES__COMPONENT_FILE,
					        file));
				}

				DcdPartitioning partitioning = dcd.getPartitioning();
				if (partitioning == null) {
					partitioning = DcdFactory.eINSTANCE.createDcdPartitioning();
					command.append(SetCommand.create(getEditingDomain(), dcd, DcdPackage.Literals.DEVICE_CONFIGURATION__PARTITIONING, partitioning));
				}

				final DcdComponentPlacement placement = DcdFactory.eINSTANCE.createDcdComponentPlacement();
				final ComponentFileRef cfp = PartitioningFactory.eINSTANCE.createComponentFileRef();
				cfp.setRefid(file.getId());
				placement.setComponentFileRef(cfp);

				command.append(AddCommand.create(getEditingDomain(), dcd.getPartitioning(), PartitioningPackage.Literals.PARTITIONING__COMPONENT_PLACEMENT,
				        placement));

				final DcdComponentInstantiation instantiation = DcdFactory.eINSTANCE.createDcdComponentInstantiation();
				final String uniqueName = DeviceConfiguration.Util.createDeviceUsageName(dcd, device.getName());
				instantiation.setId(dcd.getName() + ":" + uniqueName);
				instantiation.setUsageName(uniqueName);

				command.append(SetCommand.create(getEditingDomain(), placement, PartitioningPackage.Literals.COMPONENT_PLACEMENT__COMPONENT_INSTANTIATION,
				        Arrays.asList((new DcdComponentInstantiation[] { instantiation }))));

				this.lastComp = placement;
			}

			execute(command);
			refresh(this.dcdResource);
			selectElement(this.lastComp);
		}
	}

	private DeviceConfiguration getDeviceConfiguration() {
		return ModelUtil.getDeviceConfiguration(this.dcdResource);
	}

	/**
	 * Execute.
	 * 
	 * @param command the command
	 */
	private void execute(final Command command) {
		getEditingDomain().getCommandStack().execute(command);
	}

	/**
	 * Gets the selection.
	 * 
	 * @return the selection
	 */
	private Object getSelection() {
		return ((IStructuredSelection) getTreePart().getViewer().getSelection()).getFirstElement();
	}

	/**
	 * Gets the selection.
	 * 
	 * @return the selection
	 */
	public SoftPkg getSelectedDevice() {
		final DeviceConfiguration dcd = getDeviceConfiguration();
		final ComponentPlacement< ? > place = (ComponentPlacement< ? >) getSelection();
		final String targetId = place.getComponentFileRef().getRefid();

		for (final ComponentFile file : dcd.getComponentFiles().getComponentFile()) {
			if (file.getId().equals(targetId)) {
				return file.getSoftPkg();
			}
		}

		return null;
	}

	/**
	 * Gets the editing domain.
	 * 
	 * @return the editing domain
	 */
	private EditingDomain getEditingDomain() {
		return getPage().getEditor().getEditingDomain();
	}

	/**
	 * {@inheritDoc}
	 */
	public void propertyChange(final PropertyChangeEvent event) {
		if (this.fSortAction.equals(event.getSource()) && IAction.RESULT.equals(event.getProperty())) {
			final StructuredViewer viewer = getStructuredViewerPart().getViewer();
			final IStructuredSelection ssel = (IStructuredSelection) viewer.getSelection();
			updateButtons(ssel);
		}
	}

	/**
	 * Fire selection.
	 */
	protected void fireSelection() {
		final ISelection selection = this.fExtensionTree.getSelection();
		if (selection.isEmpty()) {
			selectFirstElement();
		} else {
			this.fExtensionTree.setSelection(selection);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refresh(final Resource resource) {
		if (isDisposed()) {
			return;
		}
		this.dcdResource = resource;
		if (this.fExtensionTree != null) {
			if (this.context != null) {
				this.context.dispose();
			}
			this.context = new EMFDataBindingContext();

			final DeviceConfiguration dcd = getDeviceConfiguration();

			this.context.bindValue(ViewersObservables.observeInput(this.fExtensionTree),
			        EMFEditObservables.observeValue(getEditingDomain(), dcd, DcdPackage.Literals.DEVICE_CONFIGURATION__PARTITIONING));
			if (!this.dcdResource.eAdapters().contains(this.refreshAdapter)) {
				this.refreshAdapter.setTarget(this.dcdResource);
			}
		}
		this.fireSelection();
		this.setEditable();
	}

	private boolean isDisposed() {
		return this.disposed;
	}

	private void setEditable() {
		this.editable = SCAEditorUtil.isEditableResource(getPage(), this.dcdResource);
		this.getTreePart().setButtonEnabled(DevicesSection.BUTTON_ADD, this.editable);
		this.getTreePart().setButtonEnabled(DevicesSection.BUTTON_REMOVE, this.editable);
	}

}
