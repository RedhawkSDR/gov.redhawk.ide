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
package gov.redhawk.ide.internal.ui.templates;

import gov.redhawk.eclipsecorba.idl.expressions.util.ExpressionsAdapterFactory;
import gov.redhawk.eclipsecorba.idl.operations.provider.OperationsItemProviderAdapterFactory;
import gov.redhawk.eclipsecorba.idl.provider.IdlItemProviderAdapterFactory;
import gov.redhawk.eclipsecorba.idl.types.provider.TypesItemProviderAdapterFactory;
import gov.redhawk.eclipsecorba.library.IdlLibrary;
import gov.redhawk.eclipsecorba.library.provider.RepositoryItemProviderAdapterFactory;
import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.provider.SdrItemProviderAdapterFactory;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.pde.ui.templates.BaseOptionTemplateSection;
import org.eclipse.pde.ui.templates.TemplateOption;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.dialogs.FilteredTree;

public class SdrResourceOption extends TemplateOption {

	private FilteredTree viewer;
	private SdrRoot root;
	private EObject selection;

	/**
	 * Constructor for ComboChoiceOption.
	 *
	 * @param section
	 *            the parent section.
	 * @param name
	 *            the unique name
	 * @param label
	 *            the presentable label
	 */
	public SdrResourceOption(BaseOptionTemplateSection section, String name, String label, SdrRoot root) {
		super(section, name, label);
		this.root = root;
	}

	/*
	 * @see org.eclipse.pde.ui.templates.TemplateField#createControl(org.eclipse.swt.widgets.Composite, int)
	 */
	@Override
	public void createControl(Composite parent, int span) {
		Group group = new Group(parent, SWT.None);
		group.setLayout(new GridLayout());
		group.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).hint(SWT.DEFAULT, 300).create());
		group.setText(getMessageLabel());

		viewer = new FilteredTree(group, SWT.FULL_SELECTION | SWT.SINGLE | SWT.H_SCROLL, new SdrPatternFilter(), true);

		final ComposedAdapterFactory adapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);

		adapterFactory.addAdapterFactory(new RepositoryItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new SdrItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new IdlItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new OperationsItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new ExpressionsAdapterFactory());
		adapterFactory.addAdapterFactory(new TypesItemProviderAdapterFactory());

		adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());

		viewer.getViewer().setLabelProvider(new AdapterFactoryLabelProvider(adapterFactory));
		viewer.getViewer().setContentProvider(new AdapterFactoryContentProvider(adapterFactory));
		viewer.getViewer().setSorter(new ViewerSorter());
		viewer.getViewer().addFilter(new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (element instanceof IdlLibrary || parentElement instanceof SoftPkg || parentElement instanceof SoftwareAssembly
					|| parentElement instanceof DeviceConfiguration) {
					return false;
				}
				return true;
			}
		});
		viewer.getViewer().setInput(this.root);
	}
	
	public void setSelection(EObject selection) {
		this.selection = selection;
		// TODO Select element in viewer
	}

	public EObject getSelection() {
		if (this.selection != null) {
			return this.selection;
		}
		if (viewer.getViewer().getSelection().isEmpty()) {
			return null;
		}
		Object retVal = ((IStructuredSelection) viewer.getViewer().getSelection()).getFirstElement();
		if (retVal instanceof EObject) {
			return (EObject) retVal;
		}
		return null;
	}

	/**
	 * @return the viewer
	 */
	public FilteredTree getViewer() {
		return viewer;
	}

	/*
	 * @see org.eclipse.pde.ui.templates.TemplateOption#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return getSelection() != null;
	}

}
