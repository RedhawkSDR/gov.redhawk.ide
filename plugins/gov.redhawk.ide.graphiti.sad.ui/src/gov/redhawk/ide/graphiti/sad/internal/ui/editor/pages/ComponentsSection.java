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
package gov.redhawk.ide.graphiti.sad.internal.ui.editor.pages;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.emf.databinding.edit.EMFEditObservables;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import gov.redhawk.model.sca.util.ModelUtil;
import gov.redhawk.sca.ui.parts.FormFilteredTree;
import gov.redhawk.ui.editor.TreeSection;
import gov.redhawk.ui.parts.TreePart;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.sad.provider.SadItemProviderAdapterFactory;

public class ComponentsSection extends TreeSection implements IPropertyChangeListener {

	private FormFilteredTree fFilteredTree;
	private TreeViewer fExtensionTree;
	private Resource sadResource;

	private DataBindingContext context;

	public ComponentsSection(final SadComponentsPage page, final Composite parent) {
		super(page, parent, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR, null);
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
	}

	@Override
	protected void selectionChanged(final IStructuredSelection selection) {
		getPage().setSelection(selection);
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
		}

		this.fireSelection();
		super.refresh();
	}
}
