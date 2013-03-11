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
package gov.redhawk.ide.sad.internal.ui.properties;

import gov.redhawk.eclipsecorba.idl.Identifiable;
import gov.redhawk.eclipsecorba.idl.IdlInterfaceDcl;
import gov.redhawk.eclipsecorba.idl.expressions.util.ExpressionsAdapterFactory;
import gov.redhawk.eclipsecorba.idl.operations.provider.OperationsItemProviderAdapterFactory;
import gov.redhawk.eclipsecorba.idl.provider.IdlItemProviderAdapterFactory;
import gov.redhawk.eclipsecorba.idl.types.provider.TypesItemProviderAdapterFactory;
import gov.redhawk.eclipsecorba.library.provider.LibraryItemProviderAdapterFactory;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mil.jpeojtrs.sca.partitioning.ComponentInstantiation;
import mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub;
import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.scd.ComponentFeatures;
import mil.jpeojtrs.sca.scd.Provides;
import mil.jpeojtrs.sca.scd.SupportsInterface;
import mil.jpeojtrs.sca.scd.Uses;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.properties.sections.AbstractModelerPropertySection;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.IFilter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * @since 3.0
 * 
 */
public class IdlPropertySection extends AbstractModelerPropertySection {

	public static class Filter implements IFilter {

		public boolean select(final Object toTest) {
			if (toTest instanceof IGraphicalEditPart) {
				final IGraphicalEditPart part = (IGraphicalEditPart) toTest;
				final Object modelObj = part.getModel();
				if (modelObj instanceof Node) {
					final Node node = (Node) modelObj;
					final EObject element = node.getElement();
					if (element instanceof UsesPortStub) {
						return true;
					} else if (element instanceof ProvidesPortStub) {
						return true;
					} else if (element instanceof ComponentSupportedInterfaceStub) {
						return !(element.eContainer() instanceof FindByStub);
					}
				}
			}
			return false;
		}

	}

	private TreeViewer treeViewer;
	private ComposedAdapterFactory adapterFactory;
	private Label label;

	/**
	 * 
	 */
	public IdlPropertySection() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public void createControls(final Composite parent, final TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		final Composite root = getWidgetFactory().createComposite(parent);
		root.setLayout(GridLayoutFactory.fillDefaults().numColumns(1).create());
		this.label = getWidgetFactory().createLabel(root, "");
		final Tree tree = this.getWidgetFactory().createTree(root, SWT.BORDER);
		tree.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		this.treeViewer = new TreeViewer(tree);
		this.treeViewer.setContentProvider(new AdapterFactoryContentProvider(getAdapterFactory()) {
			@Override
			public Object[] getElements(final Object object) {
				if (object instanceof List< ? >) {
					return ((List< ? >) object).toArray();
				}
				return super.getChildren(object);
			}

			private Object[] getInterChildren(final IdlInterfaceDcl inter) {
				final Set<Object> retVal = new HashSet<Object>();
				retVal.addAll(Arrays.asList(super.getChildren(inter)));
				for (final IdlInterfaceDcl i : inter.getInheritedInterfaces()) {
					retVal.addAll(Arrays.asList(getInterChildren(i)));
				}
				return retVal.toArray();
			}

		});
		this.treeViewer.setLabelProvider(new AdapterFactoryLabelProvider(getAdapterFactory()));
		this.treeViewer.setComparator(new ViewerComparator());

	}

	private AdapterFactory getAdapterFactory() {
		if (this.adapterFactory == null) {
			this.adapterFactory = new ComposedAdapterFactory();
			this.adapterFactory.addAdapterFactory(new LibraryItemProviderAdapterFactory());
			this.adapterFactory.addAdapterFactory(new IdlItemProviderAdapterFactory());
			this.adapterFactory.addAdapterFactory(new OperationsItemProviderAdapterFactory());
			this.adapterFactory.addAdapterFactory(new ExpressionsAdapterFactory());
			this.adapterFactory.addAdapterFactory(new TypesItemProviderAdapterFactory());
		}
		return this.adapterFactory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setInput(final IWorkbenchPart part, final ISelection selection) {
		super.setInput(part, selection);
		if (this.treeViewer != null) {
			final EObject obj = getEObject();
			final List<String> repIds = new ArrayList<String>();
			if (obj instanceof UsesPortStub) {
				final Uses uses = ((UsesPortStub) obj).getUses();
				if (uses != null) {
					repIds.add(((UsesPortStub) obj).getUses().getRepID());
				}
			} else if (obj instanceof ProvidesPortStub) {
				final Provides provides = ((ProvidesPortStub) obj).getProvides();
				if (provides != null) {
					repIds.add(provides.getRepID());
				}
			} else if (obj instanceof ComponentSupportedInterfaceStub) {
				final ComponentSupportedInterfaceStub stub = (ComponentSupportedInterfaceStub) obj;
				if (stub.eContainer() instanceof ComponentInstantiation) {
					final ComponentInstantiation instantiation = (ComponentInstantiation) stub.eContainer();
					final SoftPkg spd = instantiation.getPlacement().getComponentFileRef().getFile().getSoftPkg();
					final ComponentFeatures features = spd.getDescriptor().getComponent().getComponentFeatures();

					for (final SupportsInterface i : features.getSupportsInterface()) {
						repIds.add(i.getRepId());
					}
				}
			}
			final List<Identifiable> input = new ArrayList<Identifiable>();
			if (!repIds.isEmpty()) {
				for (final String repId : repIds) {
					input.add(SdrUiPlugin.getDefault().getTargetSdrRoot().getIdlLibrary().find(repId));
				}
				this.label.setText(repIds.toString());
			}
			this.treeViewer.setInput(input);
		}
	}

	@Override
	public boolean shouldUseExtraSpace() {
		return true;
	}

}
