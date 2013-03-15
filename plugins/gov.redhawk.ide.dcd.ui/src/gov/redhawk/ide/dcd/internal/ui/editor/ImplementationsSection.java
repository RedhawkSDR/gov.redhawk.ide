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

import gov.redhawk.ide.dcd.internal.ui.editor.provider.ImplementationSectionImplementationItemProvider;
import gov.redhawk.ide.dcd.internal.ui.editor.provider.ImplementationSectionSoftPkgItemProvider;
import gov.redhawk.ide.spd.internal.ui.editor.provider.SpdItemProviderAdapterFactoryAdapter;
import gov.redhawk.model.sca.util.ModelUtil;
import gov.redhawk.sca.ui.parts.FormFilteredTree;
import gov.redhawk.ui.actions.SortAction;
import gov.redhawk.ui.editor.TreeSection;
import gov.redhawk.ui.parts.TreePart;
import mil.jpeojtrs.sca.spd.Code;
import mil.jpeojtrs.sca.spd.CodeFileType;
import mil.jpeojtrs.sca.spd.Dependency;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.LocalFile;
import mil.jpeojtrs.sca.spd.Os;
import mil.jpeojtrs.sca.spd.Processor;
import mil.jpeojtrs.sca.spd.PropertyRef;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SoftPkgRef;
import mil.jpeojtrs.sca.spd.SpdFactory;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.spd.UsesDevice;
import mil.jpeojtrs.sca.util.DceUuidUtil;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.EList;
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
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
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
import org.eclipse.ui.dialogs.PatternFilter;
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

	/**
	 * The Constructor.
	 * 
	 * @param page the page
	 * @param parent the parent
	 */
	public ImplementationsSection(final ImplementationPage page, final Composite parent) {
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
	public ImplementationPage getPage() {
		return (ImplementationPage) super.getPage();
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
		this.fExtensionTree.setContentProvider(new AdapterFactoryContentProvider(getAdapterFactory()));
		this.fExtensionTree.setLabelProvider(new LabelProvider() {

			@Override
			public String getText(final Object element) {
				if (element instanceof Implementation) {
					final Implementation impl = (Implementation) element;
					final StringBuilder buff = new StringBuilder();
					final EList<Os> oses = impl.getOs();
					if (oses.size() > 0) {
						buff.append("OS");
						if (oses.size() > 1) {
							buff.append("s");
						}
						buff.append(": ");
						for (final Os os : oses) {
							buff.append(os.getName());
							buff.append("(");
							if (os.getVersion() != null) {
								buff.append(os.getVersion());
							}
							buff.append(")");
							buff.append(", ");
						}
						buff.deleteCharAt(buff.length() - 2);
					}
					final EList<Processor> procs = impl.getProcessor();
					if (procs.size() > 0) {
						if (buff.length() > 0) {
							buff.append(" ");
						}
						buff.append("Processor");
						if (procs.size() > 1) {
							buff.append("s");
						}
						buff.append(": ");
						for (final Processor proc : procs) {
							buff.append(proc.getName()).append(", ");
						}
						buff.deleteCharAt(buff.length() - 2);
					}
					if (buff.length() > 0) {
						return buff.toString();
					}
				}
				return super.getText(element);
			}
		});
		//		this.fExtensionTree.setLabelProvider(new DecoratingLabelProvider(new AdapterFactoryLabelProvider(
		//				getAdapterFactory()), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));
		toolkit.paintBordersFor(container);
		section.setClient(container);
		section.setDescription("Define implementations within the following section.");
		// See Bug # 160554: Set text before text client
		section.setText("All Implementations");
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
			provider.setImplementationAdapter(new ImplementationSectionImplementationItemProvider(provider, getPage()));
			this.adapterFactory.addAdapterFactory(provider);

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
		refresh(this.spdResource);
		selectFirstElement();
		final TreePart treePart = getTreePart();
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
		this.fExtensionTree.setSelection(new StructuredSelection(obj));
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

		if (item != null) {
			removeEnabled = true;
		}
		if (filtered) {
			// Fix for bug 194529 and bug 194828
			addEnabled = false;
		}
		getTreePart().setButtonEnabled(ImplementationsSection.BUTTON_ADD, addEnabled);
		getTreePart().setButtonEnabled(ImplementationsSection.BUTTON_REMOVE, removeEnabled);
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
	 * Deletes the implementation from the model.
	 * 
	 * @param impl the implementation to remove
	 */
	private void deleteImplementation(final Implementation impl) {
		final CompoundCommand command = new CompoundCommand("Remove Implementation");
		command.append(RemoveCommand.create(getEditingDomain(), getSoftPkg(), SpdPackage.Literals.SOFT_PKG__IMPLEMENTATION, impl));

		execute(command);
	}

	/**
	 * TODO
	 * 
	 * @return
	 */
	private SoftPkg getSoftPkg() {
		return ModelUtil.getSoftPkg(this.spdResource);
	}

	/**
	 * Deletes the PropertyRef from the model.
	 * 
	 * @param impl the PropertyRef to remove
	 */
	private void deletePropertyRef(final PropertyRef obj) {
		if (obj.eContainer() instanceof Dependency) {
			execute(SetCommand.create(getEditingDomain(), obj.eContainer(), SpdPackage.Literals.DEPENDENCY__PROPERTY_REF, null));
		} else if (obj.eContainer() instanceof UsesDevice) {
			execute(RemoveCommand.create(getEditingDomain(), obj.eContainer(), SpdPackage.Literals.USES_DEVICE__PROPERTY_REF, obj));
		}
	}

	/**
	 * Removes the selected SoftPkgRef from the model.
	 * 
	 * @param obj the SoftPkgRef to remove
	 */
	private void deleteSoftPkgRef(final SoftPkgRef obj) {
		execute(SetCommand.create(getEditingDomain(), obj.eContainer(), SpdPackage.Literals.DEPENDENCY__SOFT_PKG_REF, null));
	}

	/**
	 * Removes the selected UsesDevice from the model.
	 * 
	 * @param obj the Device to remove
	 */
	private void deleteUsesDevice(final UsesDevice obj) {
		execute(RemoveCommand.create(getEditingDomain(), obj.eContainer(), SpdPackage.Literals.IMPLEMENTATION__USES_DEVICE, obj));
	}

	/**
	 * Removes the selected Dependency from the model.
	 * 
	 * @param obj the Dependency to remove
	 */
	private void deleteDependency(final Dependency obj) {
		execute(RemoveCommand.create(getEditingDomain(), obj.eContainer(), SpdPackage.Literals.IMPLEMENTATION__DEPENDENCY, obj));
	}

	/**
	 * Handle delete.
	 */
	private void handleDelete() {
		final Object obj = getSelection();
		if (obj instanceof Implementation) {
			deleteImplementation((Implementation) obj);
		} else if (obj instanceof Dependency) {
			deleteDependency((Dependency) obj);
		} else if (obj instanceof UsesDevice) {
			deleteUsesDevice((UsesDevice) obj);
		} else if (obj instanceof SoftPkgRef) {
			deleteSoftPkgRef((SoftPkgRef) obj);
		} else if (obj instanceof PropertyRef) {
			deletePropertyRef((PropertyRef) obj);
		}
	}

	/**
	 * Handle new.
	 */
	private void handleNew() {
		final Implementation impl = SpdFactory.eINSTANCE.createImplementation();
		final LocalFile file = SpdFactory.eINSTANCE.createLocalFile();
		file.setName("nodeBooter");
		final Code code = SpdFactory.eINSTANCE.createCode();
		code.setType(CodeFileType.EXECUTABLE);
		code.setLocalFile(file);
		code.setEntryPoint("nodeBooter");
		impl.setId(DceUuidUtil.createDceUUID());
		impl.setCode(code);

		final CompoundCommand command = new CompoundCommand("Add Implementation");

		command.append(AddCommand.create(getEditingDomain(), getSoftPkg(), SpdPackage.Literals.SOFT_PKG__IMPLEMENTATION, impl));

		execute(command);

		Display.getCurrent().asyncExec(new Runnable() {
			public void run() {
				getTreePart().getTreeViewer().setSelection(new StructuredSelection(impl));
			}
		});

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
		final SoftPkg spd = getSoftPkg();
		if (this.fExtensionTree != null) {
			if (spd != null) {
				this.fExtensionTree.setInput(getSoftPkg());
			} else {
				this.fExtensionTree.setInput(null);
			}
			this.fireSelection();
		}

		super.refresh(resource);
	}
}
