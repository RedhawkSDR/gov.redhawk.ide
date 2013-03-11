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

import gov.redhawk.ide.dcd.internal.ui.editor.provider.DcdItemProviderAdapterFactoryAdapter;
import gov.redhawk.ide.dcd.internal.ui.editor.provider.DevicesSectionComponentPlacementItemProvider;
import gov.redhawk.ui.editor.FormOutlinePage;
import gov.redhawk.ui.editor.SCAFormEditor;
import gov.redhawk.ui.editor.ScaFormPage;
import mil.jpeojtrs.sca.dcd.DcdComponentPlacement;
import mil.jpeojtrs.sca.dcd.DcdPartitioning;
import mil.jpeojtrs.sca.partitioning.Partitioning;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.ui.part.PageBook;

/**
 * 
 */
public class NodeOutlinePage extends FormOutlinePage {

	/**
	 * @since 2.0
	 */
	public class NodeOutlinePageAdapterFactoryLabelProvider extends AdapterFactoryLabelProvider {

		public NodeOutlinePageAdapterFactoryLabelProvider(final AdapterFactory adapterFactory) {
			super(adapterFactory);
		}

		@Override
		public String getText(final Object object) {
			if (object instanceof DcdPartitioning) {
				return "Devices";
			} else if (object instanceof ScaFormPage) {
				return "Overview";
			}
			return super.getText(object);
		}

	}

	/**
	 * The Constructor.
	 * 
	 * @param editor the editor
	 */
	public NodeOutlinePage(final SCAFormEditor editor) {
		super(editor);
		super.setLabelProvider(new NodeOutlinePageAdapterFactoryLabelProvider(getAdapterFactory()));
	}

	/**
	 * The Constructor.
	 * 
	 * @param editor the editor
	 */
	public NodeOutlinePage(final SCAFormEditor editor, final PageBook pagebook) {
		super(editor);
		super.setLabelProvider(new NodeOutlinePageAdapterFactoryLabelProvider(getAdapterFactory()));
		super.createControl(pagebook);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addItemProviders(final ComposedAdapterFactory adapterFactory) {
		final DcdItemProviderAdapterFactoryAdapter dcd = new DcdItemProviderAdapterFactoryAdapter();
		dcd.setComponentPlacementAdapter(new DevicesSectionComponentPlacementItemProvider(dcd));
		adapterFactory.addAdapterFactory(dcd);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean getChildren(final Object parent) {
		boolean retVal = false;
		if (parent instanceof SoftPkg || parent instanceof Partitioning) {
			retVal = true;
		}
		return retVal;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getParentPageId(final Object item) {
		String pageId = null;
		if (item instanceof Implementation || item instanceof SoftPkg) {
			pageId = ImplementationPage.PAGE_ID;
		} else if (item instanceof DcdComponentPlacement || item instanceof Partitioning) {
			pageId = DevicesPage.PAGE_ID;
		}
		if (pageId != null) {
			return pageId;
		}
		return super.getParentPageId(item);
	}

}
