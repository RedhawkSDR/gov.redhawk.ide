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
package gov.redhawk.ide.spd.internal.ui.editor;

import gov.redhawk.ide.codegen.CodegenPackage;
import gov.redhawk.ide.codegen.CodegenUtil;
import gov.redhawk.ide.codegen.ICodeGeneratorDescriptor;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.RedhawkCodegenActivator;
import gov.redhawk.ide.codegen.WaveDevSettings;
import gov.redhawk.ide.codegen.ui.action.CleanUpGeneratorRunnable;
import gov.redhawk.ide.codegen.util.ProjectCreator;
import gov.redhawk.ide.codegen.util.PropertyUtil;
import gov.redhawk.ide.spd.internal.ui.editor.provider.ImplementationSectionImplementationItemProvider;
import gov.redhawk.ide.spd.internal.ui.editor.provider.ImplementationSectionSoftPkgItemProvider;
import gov.redhawk.ide.spd.internal.ui.editor.provider.SpdItemProviderAdapterFactoryAdapter;
import gov.redhawk.ide.spd.internal.ui.editor.wizard.ImplementationWizard;
import gov.redhawk.model.sca.util.ModelUtil;
import gov.redhawk.sca.ui.parts.FormFilteredTree;
import gov.redhawk.ui.actions.SortAction;
import gov.redhawk.ui.editor.TreeSection;
import gov.redhawk.ui.parts.TreePart;
import gov.redhawk.ui.util.SCAEditorUtil;

import java.util.List;

import mil.jpeojtrs.sca.spd.Dependency;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.PropertyRef;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SoftPkgRef;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.spd.UsesDevice;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.provider.EcoreItemProviderAdapterFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * 
 */
public class ImplementationsSection extends TreeSection implements IPropertyChangeListener {

	private static final int BUTTON_REMOVE = 1;

	private static final int BUTTON_ADD = 0;

	private FormFilteredTree fFilteredTree;

	private TreeViewer fExtensionTree;

	private SortAction fSortAction;

	private ComposedAdapterFactory adapterFactory;

	private Resource spdResource;

	private boolean editable;

	/**
	 * The Constructor.
	 * 
	 * @param page the page
	 * @param parent the parent
	 */
	public ImplementationsSection(final ImplementationPage page, final Composite parent) {
		super(page, parent, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR, new String[] {
		        "Add...", "Remove"
		});
		this.fHandleDefaultButton = false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void selectionChanged(final IStructuredSelection selection) {
		this.getPage().setSelection(selection);
		this.updateButtons(selection);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createClient(final Section section, final FormToolkit toolkit) {

		final Composite container = this.createClientContainer(section, 2, toolkit);
		final TreePart treePart = this.getTreePart();
		this.createViewerPartControl(container, SWT.MULTI, 2, toolkit);
		this.fExtensionTree = treePart.getTreeViewer();
		this.fExtensionTree.setContentProvider(new AdapterFactoryContentProvider(this.getAdapterFactory()));
		this.fExtensionTree.setLabelProvider(new DecoratingLabelProvider(new AdapterFactoryLabelProvider(this.getAdapterFactory()), PlatformUI.getWorkbench()
		        .getDecoratorManager()
		        .getLabelDecorator()));
		this.fExtensionTree.addSelectionChangedListener(new ISelectionChangedListener() {
			//Try selecting the first element if the event's selection is empty.
			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				if (event.getSelection().isEmpty()) {
					ImplementationsSection.this.selectFirstElement();
				}
			}
		});
		toolkit.paintBordersFor(container);
		section.setClient(container);
		section.setDescription("Define implementations within the following section.");
		// See Bug # 160554: Set text before text client
		section.setText("All Implementations");
		this.initialize();
		this.createSectionToolbar(section, toolkit);
		// Create the adapted listener for the filter entry field
		final Text filterText = this.fFilteredTree.getFilterControl();
		if (filterText != null) {
			filterText.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(final ModifyEvent e) {
					final StructuredViewer viewer = ImplementationsSection.this.getStructuredViewerPart().getViewer();
					final IStructuredSelection ssel = (IStructuredSelection) viewer.getSelection();
					ImplementationsSection.this.updateButtons((ssel.size() != 1) ? null : ssel); // SUPPRESS CHECKSTYLE AvoidInline
				}
			});
		}

		this.refresh(this.spdResource);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean setFormInput(final Object object) {
		this.fExtensionTree.setSelection(new StructuredSelection(object), true);
		return true;
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
			// Create an adapter factory that yields item providers.
			//
			this.adapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);

			this.adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());

			final SpdItemProviderAdapterFactoryAdapter provider = new SpdItemProviderAdapterFactoryAdapter();
			provider.setSoftPkgAdapter(new ImplementationSectionSoftPkgItemProvider(provider));
			provider.setImplementationAdapter(new ImplementationSectionImplementationItemProvider(provider, this.getPage().getEditor().getMainResource()));
			this.adapterFactory.addAdapterFactory(provider);

			this.adapterFactory.addAdapterFactory(new EcoreItemProviderAdapterFactory());
			this.adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());
		}
		return this.adapterFactory;
	}

	/**
	 * Initialize.
	 */
	private void initialize() {
		this.selectFirstElement();
		final TreePart treePart = this.getTreePart();
		treePart.setButtonEnabled(ImplementationsSection.BUTTON_ADD, true);
		treePart.setButtonEnabled(ImplementationsSection.BUTTON_REMOVE, false);
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
		if (obj != null) {
			this.fExtensionTree.setSelection(new StructuredSelection(obj));
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
			@Override
			public void widgetDisposed(final DisposeEvent e) {
				if (!handCursor.isDisposed()) {
					handCursor.dispose();
				}
			}
		});
		// Add sort action to the tool bar
		this.fSortAction = new SortAction(this.fExtensionTree, "Sort the properties alpabetically.", null, null, this);
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
		this.getTreePart().setButtonEnabled(ImplementationsSection.BUTTON_ADD, addEnabled);
		this.getTreePart().setButtonEnabled(ImplementationsSection.BUTTON_REMOVE, removeEnabled);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void buttonSelected(final int index) {
		switch (index) {
		case BUTTON_ADD:
			this.handleNew();
			break;
		case BUTTON_REMOVE:
			this.handleDelete();
			break;
		default:
			break;
		}
	}

	/**
	 * Deletes the implementation from the model.
	 * 
	 * @param impl the implementation to remove
	 */
	private void deleteImplementation(final Implementation impl) {
		final SoftPkg softpkg = this.getSoftPkg();
		final CompoundCommand command = new CompoundCommand("Remove Implementation");
		command.append(RemoveCommand.create(this.getEditingDomain(), softpkg, SpdPackage.Literals.SOFT_PKG__IMPLEMENTATION, impl));
		final ImplementationSettings implSettings = CodegenUtil.getImplementationSettings(impl);
		if (implSettings != null) {
			command.append(RemoveCommand.create(this.getEditingDomain(),
			        this.getWaveDevSettings(),
			        CodegenPackage.Literals.WAVE_DEV_SETTINGS__IMPL_SETTINGS,
			        implSettings.eContainer()));

			final IResource resource = ModelUtil.getResource(softpkg);
			final IProject project = resource.getProject();
			final IResource spdFile = ModelUtil.getResource(implSettings);
			IFolder dir = null;
			if (!"".equals(implSettings.getOutputDir()) && (implSettings.getOutputDir() != null)) {
				dir = spdFile.getProject().getFolder(implSettings.getOutputDir());
			}
			final IFolder outputDir = dir;
			if (outputDir != null && outputDir.exists()) {
				final Shell shell = this.getPage().getEditorSite().getShell();
				final boolean remove = MessageDialog.openQuestion(shell,
				        "Remove Implementation Directory",
				        "Would you like to remove the implementation directory(" + implSettings.getOutputDir() + ") also?");
				if (remove) {

					PropertyUtil.setLastGenerated(CodegenUtil.loadWaveDevSettings(softpkg), implSettings, null);

					final WorkspaceJob job = new WorkspaceJob("Removing Implementation Directory") {

						@Override
						@SuppressWarnings("deprecation")
						public IStatus runInWorkspace(final IProgressMonitor monitor) throws CoreException {
							final SubMonitor subMonitor = SubMonitor.convert(monitor, "Deleting Implementation " + implSettings.getName(), 90);
							try {
								outputDir.delete(true, subMonitor.newChild(10)); // SUPPRESS CHECKSTYLE MagicNumber

								final String codegenId = implSettings.getGeneratorId();

								final ICodeGeneratorDescriptor codeGenDesc = RedhawkCodegenActivator.getCodeGeneratorsRegistry().findCodegen(codegenId);

								final CleanUpGeneratorRunnable cleanup = new CleanUpGeneratorRunnable(codeGenDesc, project);
								cleanup.run(subMonitor.newChild(90)); // SUPPRESS CHECKSTYLE MagicNumber

								return Status.OK_STATUS;
							} finally {
								subMonitor.done();
							}
						}

					};
					job.setPriority(Job.LONG);
					job.setUser(true);
					job.setSystem(false);
					job.setRule(ResourcesPlugin.getWorkspace().getRuleFactory().buildRule());
					job.schedule();
				}
			}
			this.execute(command);
		} else {
			this.execute(RemoveCommand.create(this.getEditingDomain(), softpkg, SpdPackage.Literals.SOFT_PKG__IMPLEMENTATION, impl));
		}

	}

	private SoftPkg getSoftPkg() {
		if (this.spdResource != null) {
			return SoftPkg.Util.getSoftPkg(this.spdResource);
		}
		return null;
	}

	/**
	 * Deletes the PropertyRef from the model.
	 */
	private void deletePropertyRef(final PropertyRef obj) {
		if (obj.eContainer() instanceof Dependency) {
			this.execute(SetCommand.create(this.getEditingDomain(), obj.eContainer(), SpdPackage.Literals.DEPENDENCY__PROPERTY_REF, null));
		} else if (obj.eContainer() instanceof UsesDevice) {
			this.execute(RemoveCommand.create(this.getEditingDomain(), obj.eContainer(), SpdPackage.Literals.USES_DEVICE__PROPERTY_REF, obj));
		}
	}

	/**
	 * Removes the selected SoftPkgRef from the model.
	 * 
	 * @param obj the SoftPkgRef to remove
	 */
	private void deleteSoftPkgRef(final SoftPkgRef obj) {
		this.execute(SetCommand.create(this.getEditingDomain(), obj.eContainer(), SpdPackage.Literals.DEPENDENCY__SOFT_PKG_REF, null));
	}

	/**
	 * Removes the selected UsesDevice from the model.
	 * 
	 * @param obj the Device to remove
	 */
	private void deleteUsesDevice(final UsesDevice obj) {
		this.execute(RemoveCommand.create(this.getEditingDomain(), obj.eContainer(), SpdPackage.Literals.IMPLEMENTATION__USES_DEVICE, obj));
	}

	/**
	 * Removes the selected Dependency from the model.
	 * 
	 * @param obj the Dependency to remove
	 */
	private void deleteDependency(final Dependency obj) {
		this.execute(RemoveCommand.create(this.getEditingDomain(), obj.eContainer(), SpdPackage.Literals.IMPLEMENTATION__DEPENDENCY, obj));
	}

	/**
	 * Handle delete.
	 */
	private void handleDelete() {
		final List<Object> objs = this.getSelections();

		for (final Object obj : objs) {
			if (obj instanceof Implementation) {
				this.deleteImplementation((Implementation) obj);
			} else if (obj instanceof Dependency) {
				this.deleteDependency((Dependency) obj);
			} else if (obj instanceof UsesDevice) {
				this.deleteUsesDevice((UsesDevice) obj);
			} else if (obj instanceof SoftPkgRef) {
				this.deleteSoftPkgRef((SoftPkgRef) obj);
			} else if (obj instanceof PropertyRef) {
				this.deletePropertyRef((PropertyRef) obj);
			}
		}
	}

	/**
	 * Handle new.
	 */
	private void handleNew() {
		final ImplementationWizard wizard = new ImplementationWizard(this.getEditingDomain(), this.getSoftPkg().getName(), this.getSoftPkg());
		final WizardDialog dialog = new WizardDialog(this.getPage().getSite().getShell(), wizard);
		if (dialog.open() == Window.OK) {
			final Implementation impl = wizard.getImplementation();
			final ImplementationSettings settings = wizard.getSettings();
			final WaveDevSettings waveDevSettings = CodegenUtil.loadWaveDevSettings(this.getSoftPkg());

			ProjectCreator.setupImplementation(this.getSoftPkg(), impl, settings);

			final CompoundCommand command = new CompoundCommand("Add Implementation");
			command.append(AddCommand.create(this.getEditingDomain(), this.getSoftPkg(), SpdPackage.Literals.SOFT_PKG__IMPLEMENTATION, impl));
			command.append(CodegenUtil.createAddImplementationSettingsCommand(this.getEditingDomain(), impl.getId(), settings, waveDevSettings));

			Assert.isNotNull(settings);
			Assert.isNotNull(impl);

			if (CodegenUtil.canPrimary(impl.getProgrammingLanguage().getName())) {
				final EMap<String, ImplementationSettings> settingsMap = waveDevSettings.getImplSettings();
				boolean primary = true;
				for (final Implementation tmpImpl : this.getSoftPkg().getImplementation()) {
					if (tmpImpl.getProgrammingLanguage().getName().equals(impl.getProgrammingLanguage().getName())) {
						final ImplementationSettings set = settingsMap.get(tmpImpl.getId());
						if (set.isPrimary()) {
							primary = false; // Don't change the primary if another implementation of the same language is primary
						}
					}
				}
				final SetCommand setPrimary = new SetCommand(this.getEditingDomain(),
				        settings,
				        CodegenPackage.Literals.IMPLEMENTATION_SETTINGS__PRIMARY,
				        primary);
				command.append(setPrimary);
			}

			this.getEditingDomain().getCommandStack().execute(command);

			Display.getCurrent().asyncExec(new Runnable() {

				@Override
				public void run() {
					ImplementationsSection.this.getTreePart().getTreeViewer().setSelection(new StructuredSelection(impl));
				}
			});

//			new OpenAssociatedPerspectiveJob(desc).schedule();
		}

	}

	/**
	 * Gets the wave dev settings.
	 * 
	 * @return the wave dev settings
	 */
	private WaveDevSettings getWaveDevSettings() {
		return CodegenUtil.loadWaveDevSettings(this.getSoftPkg());
	}

	/**
	 * Execute.
	 * 
	 * @param command the command
	 */
	private void execute(final Command command) {
		this.getEditingDomain().getCommandStack().execute(command);
	}

	/**
	 * Gets the list of currently selected items.
	 * 
	 * @return list of selections
	 */
	@SuppressWarnings("unchecked")
	private List<Object> getSelections() {
		return ((IStructuredSelection) this.getTreePart().getViewer().getSelection()).toList();
	}

	/**
	 * Gets the editing domain.
	 * 
	 * @return the editing domain
	 */
	private EditingDomain getEditingDomain() {
		return this.getPage().getEditor().getEditingDomain();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		if (this.fSortAction.equals(event.getSource()) && IAction.RESULT.equals(event.getProperty())) {
			final StructuredViewer viewer = this.getStructuredViewerPart().getViewer();
			final IStructuredSelection ssel = (IStructuredSelection) viewer.getSelection();
			this.updateButtons(ssel);
		}
	}

	/**
	 * Fire selection.
	 */
	protected void fireSelection() {
		final ISelection selection = this.fExtensionTree.getSelection();
		if (selection.isEmpty()) {
			this.selectFirstElement();
		} else {
			this.fExtensionTree.setSelection(this.fExtensionTree.getSelection());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refresh(final Resource resource) {
		this.spdResource = resource;
		final SoftPkg spd = this.getSoftPkg();
		if (this.fExtensionTree != null) {
			if (spd != null) {
				this.fExtensionTree.setInput(this.getSoftPkg());
			} else {
				this.fExtensionTree.setInput(null);
			}
			this.fireSelection();
		}

		this.setEditable();

		super.refresh(resource);
	}

	private void setEditable() {
		this.editable = SCAEditorUtil.isEditableResource(this.getPage(), this.spdResource);
		this.getTreePart().setButtonEnabled(ImplementationsSection.BUTTON_ADD, this.editable);
		this.getTreePart().setButtonEnabled(ImplementationsSection.BUTTON_REMOVE, this.editable);
	}
}
