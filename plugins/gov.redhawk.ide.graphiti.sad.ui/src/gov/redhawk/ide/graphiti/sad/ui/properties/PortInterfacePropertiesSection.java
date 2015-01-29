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
package gov.redhawk.ide.graphiti.sad.ui.properties;

import gov.redhawk.eclipsecorba.idl.Identifiable;
import gov.redhawk.eclipsecorba.idl.expressions.util.ExpressionsAdapterFactory;
import gov.redhawk.eclipsecorba.idl.operations.provider.OperationsItemProviderAdapterFactory;
import gov.redhawk.eclipsecorba.idl.provider.IdlItemProviderAdapterFactory;
import gov.redhawk.eclipsecorba.idl.types.provider.TypesItemProviderAdapterFactory;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import gov.redhawk.model.sca.ScaPort;
import gov.redhawk.sca.ui.ScaComponentFactory;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * 
 */
public class PortInterfacePropertiesSection extends GFPropertySection implements ITabbedPropertyConstants, IEditingDomainProvider {

	private AdapterFactory adapterFactory;
	private TreeViewer viewer;
	private Label label;
	private TreePath[] expandedPaths;

	/**
	 * 
	 */
	public PortInterfacePropertiesSection() {
	}

	@Override
	public TransactionalEditingDomain getEditingDomain() {
		return super.getDiagramContainer().getDiagramBehavior().getEditingDomain();
	}

	protected AdapterFactory createAdapterFactory() {
		if (this.adapterFactory == null) {
			ComposedAdapterFactory newFactory = new ComposedAdapterFactory();
			this.adapterFactory = newFactory;
//			newFactory.addAdapterFactory(new LibraryItemProviderAdapterFactory());
			newFactory.addAdapterFactory(new IdlItemProviderAdapterFactory());
			newFactory.addAdapterFactory(new OperationsItemProviderAdapterFactory());
			newFactory.addAdapterFactory(new ExpressionsAdapterFactory());
			newFactory.addAdapterFactory(new TypesItemProviderAdapterFactory());
		}
		return this.adapterFactory;
	}

	@Override
	public final void createControls(final Composite parent, final TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);
		final Composite root = getWidgetFactory().createComposite(parent);
		root.setLayout(GridLayoutFactory.fillDefaults().numColumns(1).create());
		this.label = getWidgetFactory().createLabel(root, "");
		final Tree tree = this.getWidgetFactory().createTree(root, SWT.BORDER);
		tree.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		this.adapterFactory = createAdapterFactory();

		this.viewer = new TreeViewer(tree);
		this.viewer.setContentProvider(new AdapterFactoryContentProvider(adapterFactory) {
			@Override
			public Object[] getElements(final Object object) {
				if (object instanceof List< ? >) {
					return ((List< ? >) object).toArray();
				}
				return super.getChildren(object);
			}

		});
		this.viewer.setLabelProvider(new AdapterFactoryLabelProvider(adapterFactory));
	}

	protected TreeViewer createTreeViewer(final Composite parent) {
		return ScaComponentFactory.createPropertyTable(getWidgetFactory(), parent, SWT.SINGLE, this.adapterFactory);
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Object newInput = null;
		if (selection instanceof IStructuredSelection) {
			final IStructuredSelection ss = (IStructuredSelection) selection;
			final Object obj = ss.getFirstElement();
			final Object theAdapter = Platform.getAdapterManager().getAdapter(obj, ScaPort.class);
			String repId = null;
			if (theAdapter instanceof ScaPort) {
				ScaPort<?, ?> port = (ScaPort<?, ?>) theAdapter;
				repId = port.getProfileObj().getRepID();
			}
			if (repId != null) {
				this.label.setText(repId);
				Identifiable item = SdrUiPlugin.getDefault().getTargetSdrRoot().getIdlLibrary().find(repId);
				ArrayList<Identifiable> list = new ArrayList<Identifiable>();
				list.add(item);
				newInput = list;
			}
		}
		this.viewer.setInput(newInput);
	}
	
	@Override
	public boolean shouldUseExtraSpace() {
		return true;
	}
	
	@Override
	public void aboutToBeHidden() {
		if (!this.viewer.getControl().isDisposed()) {
			expandedPaths = this.viewer.getExpandedTreePaths();
		}
		super.aboutToBeHidden();
	}
	
	@Override
	public void aboutToBeShown() {
		if (expandedPaths != null) {
			this.viewer.setExpandedTreePaths(expandedPaths);
		}
		super.aboutToBeShown();
	}
	
}
