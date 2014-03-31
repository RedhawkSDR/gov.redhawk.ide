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
package gov.redhawk.ide.sad.graphiti.ui.properties;

import gov.redhawk.ide.sad.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.model.sca.IDisposable;
import gov.redhawk.sca.ui.ScaComponentFactory;
import gov.redhawk.sca.ui.properties.ScaPropertiesAdapterFactory;
import mil.jpeojtrs.sca.partitioning.ComponentInstantiation;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.platform.GFPropertySection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class ComponentPropertiesSection extends GFPropertySection implements ITabbedPropertyConstants, IEditingDomainProvider {
	private AdapterFactory adapterFactory;
	private final ComponentInstantiationPropertyViewerAdapter adapter = new ComponentInstantiationPropertyViewerAdapter(this);

	public ComponentPropertiesSection() {
	}

	protected AdapterFactory createAdapterFactory() {
		return new ScaPropertiesAdapterFactory();
	}

	@Override
	public final void createControls(final Composite parent, final TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);
		this.adapterFactory = createAdapterFactory();

		final TreeViewer viewer = createTreeViewer(parent);
		this.adapter.setViewer(viewer);
	}

	public TreeViewer getViewer() {
		return adapter.getViewer();
	}

	public AdapterFactory getAdapterFactory() {
		return adapterFactory;
	}

	protected TreeViewer createTreeViewer(final Composite parent) {
		return ScaComponentFactory.createPropertyTable(getWidgetFactory(), parent, SWT.SINGLE, this.adapterFactory);
	}

	public TransactionalEditingDomain getEditingDomain() {
		return super.getDiagramEditor().getEditingDomain();
//kepler		return super.getDiagramContainer().getDiagramBehavior().getEditingDomain();
	}

	@Override
	public final void setInput(final IWorkbenchPart part, final ISelection selection) {
		super.setInput(part, selection);
		PictogramElement pe = getSelectedPictogramElement();
		ContainerShape containerShape = (ContainerShape) DUtil.findContainerShapeParentWithProperty(pe, RHContainerShapeImpl.SHAPE_outerContainerShape);
		Object obj = DUtil.getBusinessObject(containerShape);
		if (obj instanceof ComponentInstantiation) {
			final ComponentInstantiation newInput = (ComponentInstantiation) obj;
			this.adapter.setInput(newInput);
		} else {
			this.adapter.setInput(null);
		}
	}

	@Override
	public final void dispose() {
		if (this.adapterFactory != null) {
			if (adapterFactory instanceof IDisposable) {
				((IDisposable) this.adapterFactory).dispose();
			}
			this.adapterFactory = null;

		}
		super.dispose();
	}

	// TODO: delete this two methods
	@Override
	public final boolean shouldUseExtraSpace() {
		return true;
	}

}
