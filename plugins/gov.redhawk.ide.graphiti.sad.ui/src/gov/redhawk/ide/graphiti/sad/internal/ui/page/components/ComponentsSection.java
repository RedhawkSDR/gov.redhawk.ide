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
package gov.redhawk.ide.graphiti.sad.internal.ui.page.components;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.emf.databinding.edit.EMFEditObservables;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.progress.WorkbenchJob;

import gov.redhawk.core.graphiti.sad.ui.utils.SADUtils;
import gov.redhawk.ide.graphiti.sad.internal.ui.editor.GraphitiSADEditor;
import gov.redhawk.ide.sdr.LoadState;
import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.TargetSdrRoot;
import gov.redhawk.model.sca.util.ModelUtil;
import gov.redhawk.sca.ui.parts.FormFilteredTree;
import gov.redhawk.ui.editor.TreeSection;
import gov.redhawk.ui.parts.TreePart;
import gov.redhawk.ui.util.SCAEditorUtil;
import mil.jpeojtrs.sca.partitioning.PartitioningPackage;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.sad.provider.SadItemProviderAdapterFactory;
import mil.jpeojtrs.sca.spd.SoftPkg;

public class ComponentsSection extends TreeSection implements IPropertyChangeListener {

	private static final int BUTTON_REMOVE = 1;
	private static final int BUTTON_ADD = 0;

	private FormFilteredTree fFilteredTree;
	private TreeViewer fExtensionTree;
	private Resource sadResource;

	private boolean editable;

	private DataBindingContext context;

	private final WorkbenchJob refreshViewerJob = new WorkbenchJob("Refresh") {

		@Override
		public IStatus runInUIThread(final IProgressMonitor monitor) {
			ComponentsSection.this.fExtensionTree.refresh();
			return Status.OK_STATUS;
		}
	};

	private final EContentAdapter refreshAdapter = new EContentAdapter() {
		@Override
		public void notifyChanged(final Notification notification) {
			super.notifyChanged(notification);
			final Object feature = notification.getFeature();
			if (feature == SadPackage.Literals.SOFTWARE_ASSEMBLY__PARTITIONING || feature == PartitioningPackage.Literals.PARTITIONING__COMPONENT_PLACEMENT) {
				ComponentsSection.this.refreshViewerJob.schedule(10); // SUPPRESS CHECKSTYLE MagicNumber
			}
		}
	};

	public ComponentsSection(final SadComponentsPage page, final Composite parent) {
		super(page, parent, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR, new String[] { "Add...", "Remove" });
		this.fHandleDefaultButton = false;
	}

	@Override
	protected void createClient(Section section, FormToolkit toolkit) {
		final Composite container = createClientContainer(section, 2, toolkit);
		final TreePart treePart = getTreePart();
		createViewerPartControl(container, SWT.MULTI, 2, toolkit);
		this.fExtensionTree = treePart.getTreeViewer();

		this.fExtensionTree.setContentProvider(new SadComponentContentProvider());
		this.fExtensionTree.setLabelProvider(new DecoratingLabelProvider(new AdapterFactoryLabelProvider(new SadItemProviderAdapterFactory()),
			PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()) {

			@Override
			public String getText(Object element) {
				if (element instanceof SadComponentInstantiation) {
					return ((SadComponentInstantiation) element).getUsageName();
				}
				return super.getText(element);
			}

		});

		toolkit.paintBordersFor(container);
		section.setClient(container);
		section.setDescription("Select components to include in this waveform within the following section.");
		section.setText("All Components");

		initialize();
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
	protected TreeViewer createTreeViewer(final Composite parent, final int style) {
		this.fFilteredTree = new FormFilteredTree(parent, style, new PatternFilter());
		parent.setData("filtered", Boolean.TRUE); //$NON-NLS-1$
		return this.fFilteredTree.getViewer();
	}

	private SoftwareAssembly getSoftwareAssembly() {
		return ModelUtil.getSoftwareAssembly(this.sadResource);
	}

	private EditingDomain getEditingDomain() {
		return getPage().getEditor().getEditingDomain();
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
	}

	private void initialize() {
		selectFirstElement();
		final TreePart treePart = getTreePart();
		treePart.setButtonEnabled(ComponentsSection.BUTTON_ADD, true);
		treePart.setButtonEnabled(ComponentsSection.BUTTON_REMOVE, false);
	}

	@Override
	protected void selectionChanged(final IStructuredSelection selection) {
		getPage().setSelection(selection);
		updateButtons(selection);
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
		final SdrRoot sdrRoot = TargetSdrRoot.getSdrRoot();
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

		final ScaComponentsWizardPage wizardPage = new ScaComponentsWizardPage("Select Components to Add");

		// Create the 'Add Components' wizard
		final List<SoftPkg> components = new ArrayList<>();
		final Wizard wiz = new Wizard() {

			@Override
			public boolean performFinish() {
				final ScaComponentsWizardPage page = (ScaComponentsWizardPage) this.getPages()[0];

				if (page != null) {
					components.addAll(Arrays.asList(page.getComponents()));
				}

				return true;
			}

		};
		wiz.addPage(wizardPage);
		wiz.setWindowTitle("Add Components Wizard");

		// Used to set selection to the final component added
		SadComponentInstantiation[] lastComp = new SadComponentInstantiation[1];

		// Open the component selection dialog
		final WizardDialog dialog = new WizardDialog(getPage().getSite().getShell(), wiz);
		if (dialog.open() == Window.OK) {
			SoftwareAssembly sad = getSoftwareAssembly();
			if (components.size() == 0 || sad == null) {
				return;
			}

			for (final SoftPkg spd : components) {
				TransactionalEditingDomain editingDomain = (TransactionalEditingDomain) getEditingDomain();
				getEditingDomain().getCommandStack().execute(new RecordingCommand(editingDomain) {
					@Override
					protected void doExecute() {
						lastComp[0] = SADUtils.createComponentInstantiation(spd, sad, null, null, null, spd.getImplementation().get(0).getId());
					}
				});
			}

			this.refresh(this.sadResource);
			this.selectElement(lastComp[0]);
		}
	}

	private void handleRemove() {
		IStructuredSelection selections = (IStructuredSelection) getTreePart().getViewer().getSelection();
		if (selections.isEmpty()) {
			updateButtons(null);
			return;
		}

		SoftwareAssembly sad = getSoftwareAssembly();

		// Delete each selected element individually
		for (Object selection : selections.toList()) {
			final SadComponentInstantiation compInst = (SadComponentInstantiation) selection;

			// Find the Graphiti SAD Editor
			IEditorPart editorPart = null;
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
			String workspaceRelativeStr = sad.eResource().getURI().toPlatformString(false);
			IPath workspaceRelativePath = Path.fromPortableString(workspaceRelativeStr);
			if (workspaceRelativePath != null) {
				editorPart = ResourceUtil.findEditor(page, workspaceRoot.getFile(workspaceRelativePath));
			}

			if (editorPart != null && editorPart instanceof GraphitiSADEditor) {
				GraphitiSADEditor editor = (GraphitiSADEditor) editorPart;

				// Find the diagram && featureProvider
				Diagram diagram = editor.getDiagramEditor().getDiagramTypeProvider().getDiagram();
				IFeatureProvider featureProvider = editor.getDiagramEditor().getDiagramTypeProvider().getFeatureProvider();

				// Find pictogram elements associated with the component to be deleted
				final List<PictogramElement> pictogramElements = Graphiti.getLinkService().getPictogramElements(diagram,
					Arrays.asList(new EObject[] { compInst }), false);

				// If no pictogram elements are associated with the component do a EcoreUtil delete, else rely on the
				// Graphiti delete feature
				TransactionalEditingDomain editingDomain = (TransactionalEditingDomain) getEditingDomain();
				getEditingDomain().getCommandStack().execute(new RecordingCommand(editingDomain) {
					@Override
					protected void doExecute() {
						if (pictogramElements.isEmpty()) {
							SADUtils.deleteComponentInstantiation(compInst, sad);
							SADUtils.organizeStartOrder(sad, diagram, featureProvider);
						} else {
							IDeleteContext dc = new DeleteContext(pictogramElements.get(0));
							IDeleteFeature deleteFeature = featureProvider.getDeleteFeature(dc);
							if (deleteFeature != null && deleteFeature.canDelete(dc)) {
								deleteFeature.delete(dc);
							}
						}
					}
				});
			}
		}

		this.refresh(this.sadResource);
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

		getTreePart().setButtonEnabled(ComponentsSection.BUTTON_ADD, addEnabled);
		getTreePart().setButtonEnabled(ComponentsSection.BUTTON_REMOVE, removeEnabled);
	}

	protected void fireSelection() {
		final ISelection selection = this.fExtensionTree.getSelection();
		if (selection.isEmpty()) {
			selectFirstElement();
		} else {
			this.fExtensionTree.setSelection(selection);
		}
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

	private void selectElement(final SadComponentInstantiation ci) {
		final Tree tree = this.fExtensionTree.getTree();

		for (final TreeItem item : tree.getItems()) {
			final Object obj = item.getData();

			if (obj.equals(ci)) {
				this.fExtensionTree.setSelection(new StructuredSelection(obj));
				break;
			}
		}
	}

	@Override
	public void refresh(final Resource resource) {
		this.sadResource = resource;
		if (this.sadResource == null) {
			return;
		}

		if (this.fExtensionTree != null) {
			if (this.context != null) {
				this.context.dispose();
			}
			this.context = new EMFDataBindingContext();

			final SoftwareAssembly sad = getSoftwareAssembly();

			this.context.bindValue(ViewersObservables.observeInput(this.fExtensionTree),
				EMFEditObservables.observeValue(getEditingDomain(), sad, SadPackage.Literals.SOFTWARE_ASSEMBLY__PARTITIONING));

			if (!this.sadResource.eAdapters().contains(this.refreshAdapter)) {
				this.refreshAdapter.setTarget(this.sadResource);
			}
		}

		this.fireSelection();
		this.setEditable();
		super.refresh();
	}

	private void setEditable() {
		this.editable = SCAEditorUtil.isEditableResource(getPage(), this.sadResource);
		this.getTreePart().setButtonEnabled(ComponentsSection.BUTTON_ADD, this.editable);
		this.getTreePart().setButtonEnabled(ComponentsSection.BUTTON_REMOVE, this.editable);
	}

	@Override
	public void dispose() {
		// Explicitly call the dispose method on the extensions tree
		if (this.fFilteredTree != null) {
			this.fFilteredTree.dispose();
		}
		this.refreshAdapter.unsetTarget(this.sadResource);
		super.dispose();
	}
}
