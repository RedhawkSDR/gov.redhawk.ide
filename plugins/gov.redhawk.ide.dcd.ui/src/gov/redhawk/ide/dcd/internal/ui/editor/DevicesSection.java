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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.emf.databinding.edit.EMFEditObservables;
import org.eclipse.emf.ecore.provider.EcoreItemProviderAdapterFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
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
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.progress.WorkbenchJob;

import gov.redhawk.core.graphiti.dcd.ui.utils.DCDUtils;
import gov.redhawk.ide.dcd.internal.ui.DcdComponentContentProvider;
import gov.redhawk.ide.dcd.internal.ui.editor.provider.DcdItemProviderAdapterFactoryAdapter;
import gov.redhawk.ide.dcd.internal.ui.editor.provider.DevicesSectionComponentPlacementItemProvider;
import gov.redhawk.ide.dcd.ui.wizard.ScaNodeProjectDevicesWizardPage;
import gov.redhawk.ide.sdr.LoadState;
import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import gov.redhawk.model.sca.util.ModelUtil;
import gov.redhawk.sca.ui.parts.FormFilteredTree;
import gov.redhawk.ui.editor.TreeSection;
import gov.redhawk.ui.parts.TreePart;
import gov.redhawk.ui.util.SCAEditorUtil;
import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;
import mil.jpeojtrs.sca.dcd.DcdPackage;
import mil.jpeojtrs.sca.dcd.DcdPartitioning;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.dcd.provider.DcdItemProviderAdapterFactory;
import mil.jpeojtrs.sca.partitioning.ComponentFile;
import mil.jpeojtrs.sca.partitioning.ComponentPlacement;
import mil.jpeojtrs.sca.partitioning.PartitioningPackage;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

/**
 * @since 1.1
 */
public class DevicesSection extends TreeSection implements IPropertyChangeListener {

	private static final int BUTTON_REMOVE = 1;
	private static final int BUTTON_ADD = 0;

	private FormFilteredTree fFilteredTree;
	private TreeViewer fExtensionTree;
	private ComposedAdapterFactory adapterFactory;
	private Resource dcdResource;
	private SoftPkg[] nodeElements;

	private boolean editable;

	private DataBindingContext context;

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

	public DevicesSection(final DevicesPage page, final Composite parent) {
		super(page, parent, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR, new String[] { "Add...", "Remove" });
		this.fHandleDefaultButton = false;
	}

	@Override
	protected void selectionChanged(final IStructuredSelection selection) {
		getPage().setSelection(selection);
		updateButtons(selection);
	}

	@Override
	public DevicesPage getPage() {
		return (DevicesPage) super.getPage();
	}

	@Override
	protected void createClient(final Section section, final FormToolkit toolkit) {

		final Composite container = createClientContainer(section, 2, toolkit);
		final TreePart treePart = getTreePart();
		createViewerPartControl(container, SWT.MULTI, 2, toolkit);
		this.fExtensionTree = treePart.getTreeViewer();

		this.fExtensionTree.setContentProvider(new DcdComponentContentProvider());
		this.fExtensionTree.setLabelProvider(new DecoratingLabelProvider(new AdapterFactoryLabelProvider(getAdapterFactory()),
			PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()) {

			@Override
			public String getText(final Object element) {
				if (element instanceof DcdComponentInstantiation) {
					return ((DcdComponentInstantiation) element).getUsageName();
				}
				return super.getText(element);
			}
		});

		toolkit.paintBordersFor(container);
		section.setClient(container);
		section.setDescription("Devices and services which are launched by this node");
		// See Bug # 160554: Set text before text client
		section.setText("Devices and Services");
		initialize();

		// Create the adapted listener for the filter entry field
		final Text filterText = this.fFilteredTree.getFilterControl();
		if (filterText != null) {
			filterText.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(final ModifyEvent e) {
					final StructuredViewer viewer = getStructuredViewerPart().getViewer();
					final IStructuredSelection ssel = (IStructuredSelection) viewer.getSelection();
					updateButtons((ssel.size() != 1) ? null : ssel); // SUPPRESS CHECKSTYLE AvoidInline
				}
			});
		}
	}

	@Override
	public boolean setFormInput(final Object object) {
		if (object != null) {
			this.fExtensionTree.setSelection(new StructuredSelection(object), true);
			return true;
		} else {
			return false;
		}
	}

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

	@Override
	protected TreeViewer createTreeViewer(final Composite parent, final int style) {
		this.fFilteredTree = new FormFilteredTree(parent, style, new PatternFilter());
		parent.setData("filtered", Boolean.TRUE); //$NON-NLS-1$
		return this.fFilteredTree.getViewer();
	}

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

	private void initialize() {
		selectFirstElement();
		final TreePart treePart = getTreePart();
		treePart.setButtonEnabled(DevicesSection.BUTTON_ADD, true);
		treePart.setButtonEnabled(DevicesSection.BUTTON_REMOVE, false);
	}

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

	private void selectElement(final DcdComponentInstantiation ci) {
		final Tree tree = this.fExtensionTree.getTree();

		for (final TreeItem item : tree.getItems()) {
			final Object obj = item.getData();

			if (obj.equals(ci)) {
				this.fExtensionTree.setSelection(new StructuredSelection(obj));
				break;
			}
		}
	}

	private void updateButtons(final Object item) {
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

	@Override
	protected void buttonSelected(final int index) {
		switch (index) {
		case BUTTON_ADD:
			handleAdd();
			break;
		case BUTTON_REMOVE:
			handleRemove();
			break;
		default:
			break;
		}
	}

	private void handleAdd() {
		// Make sure Target SDR has finished loading
		final SdrRoot sdrRoot = SdrUiPlugin.getDefault().getTargetSdrRoot();
		if (sdrRoot.getState() != LoadState.LOADED) {
			final IRunnableWithProgress waitForLoad = new IRunnableWithProgress() {

				@Override
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

		final ScaNodeProjectDevicesWizardPage devWizardPage = new ScaNodeProjectDevicesWizardPage("Select Devices / Services to Add");

		// Create the 'Add Devices' wizard
		final List<SoftPkg> devices = new ArrayList<>();
		final Wizard wiz = new Wizard() {

			@Override
			public boolean performFinish() {
				final ScaNodeProjectDevicesWizardPage page = (ScaNodeProjectDevicesWizardPage) this.getPages()[0];

				if (page != null) {
					DevicesSection.this.nodeElements = page.getNodeElements();
				}

				return true;
			}

		};
		wiz.setWindowTitle("Add Devices / Services Wizard");
		
		final WizardDialog dialog = new WizardDialog(getPage().getSite().getShell(), wiz);
		dialog.setMinimumPageSize(400, 400);
		wiz.addPage(devWizardPage);
		
		final DcdComponentInstantiation[] lastComp = new DcdComponentInstantiation[1];

		if (dialog.open() == Window.OK) {
			final DeviceConfiguration dcd = getDeviceConfiguration();

			if (this.nodeElements.length == 0 || dcd == null) {
				return;
			}

			for (final SoftPkg spd : devices) {
				TransactionalEditingDomain editingDomain = (TransactionalEditingDomain) getEditingDomain();
				getEditingDomain().getCommandStack().execute(new RecordingCommand(editingDomain) {

					@Override
					protected void doExecute() {
						lastComp[0] = DCDUtils.createComponentInstantiation(spd, dcd, null, null, spd.getImplementation().get(0).getId());
					}
				});
			}

			this.refresh(this.dcdResource);
			this.selectElement(lastComp[0]);
		}
	}

	private void handleRemove() {
		final DeviceConfiguration dcd = getDeviceConfiguration();
		final IStructuredSelection selections = ((IStructuredSelection) getTreePart().getViewer().getSelection());
		if (selections.isEmpty()) {
			updateButtons(null);
			return;
		}

		// Delete each selected element individually.
		for (Object selection : selections.toList()) {
			final DcdComponentInstantiation compInst = (DcdComponentInstantiation) selection;

			TransactionalEditingDomain editingDomain = (TransactionalEditingDomain) getEditingDomain();
			getEditingDomain().getCommandStack().execute(new RecordingCommand(editingDomain) {
				@Override
				protected void doExecute() {
					// Notify any listeners added to the componentInstantiation objects to do things like clean up stale
					// diagram shapes
					compInst.eNotify(new NotificationImpl(Notification.REMOVE, true, false));

					DcdPartitioning partitioning = ScaEcoreUtils.getEContainerOfType(compInst, DcdPartitioning.class);
					// If partitioning is null, then a graphiti listener already clean this up.
					if (partitioning != null) {
						DCDUtils.deleteComponentInstantiation(compInst, dcd);
					}
				}
			});
		}

		this.refresh(this.dcdResource);
	}

	private DeviceConfiguration getDeviceConfiguration() {
		return ModelUtil.getDeviceConfiguration(this.dcdResource);
	}

	private Object getSelection() {
		return ((IStructuredSelection) getTreePart().getViewer().getSelection()).getFirstElement();
	}

	public SoftPkg getSelectedDevice() {
		final DeviceConfiguration dcd = getDeviceConfiguration();
		final ComponentPlacement< ? > place = (ComponentPlacement< ? >) getSelection();
		final String targetId = place.getComponentFileRef().getRefid();
		if (dcd.getComponentFiles() != null) {
			for (final ComponentFile file : dcd.getComponentFiles().getComponentFile()) {
				if (file.getId().equals(targetId)) {
					return file.getSoftPkg();
				}
			}
		}

		return null;
	}

	private EditingDomain getEditingDomain() {
		return getPage().getEditor().getEditingDomain();
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
	}

	protected void fireSelection() {
		final ISelection selection = this.fExtensionTree.getSelection();
		if (selection.isEmpty()) {
			selectFirstElement();
		} else {
			this.fExtensionTree.setSelection(selection);
		}
	}

	@Override
	public void refresh(final Resource resource) {
		this.dcdResource = resource;
		if (this.dcdResource == null) {
			return;
		}
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

	private void setEditable() {
		this.editable = SCAEditorUtil.isEditableResource(getPage(), this.dcdResource);
		this.getTreePart().setButtonEnabled(DevicesSection.BUTTON_ADD, this.editable);
		this.getTreePart().setButtonEnabled(DevicesSection.BUTTON_REMOVE, this.editable);
	}

}
