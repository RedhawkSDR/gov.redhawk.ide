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
package gov.redhawk.ide.sad.internal.ui.editor;

import gov.redhawk.ui.editor.FormOutlinePage;
import gov.redhawk.ui.editor.SCAFormEditor;
import mil.jpeojtrs.sca.partitioning.Partitioning;
import mil.jpeojtrs.sca.sad.HostCollocation;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl.ContainmentUpdatingFeatureMapEntry;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.DelegatingWrapperItemProvider;
import org.eclipse.emf.edit.provider.FeatureMapEntryWrapperItemProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.ui.part.PageBook;

public class WaveformOutlinePage extends FormOutlinePage {

	/**
	 * @since 2.0
	 */
	private class ComponentOutlinePageAdapterFactoryLabelProvider extends AdapterFactoryLabelProvider {

		public ComponentOutlinePageAdapterFactoryLabelProvider(AdapterFactory adapterFactory) {
			super(adapterFactory);
		}

		@Override
		public String getText(Object object) {
			if (object instanceof Partitioning) {
				return "Diagram";
			} else if (object instanceof SadOverviewPage) {
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
	public WaveformOutlinePage(final SCAFormEditor editor) {
		super(editor);
		super.setLabelProvider(new ComponentOutlinePageAdapterFactoryLabelProvider(getAdapterFactory()));
	}
	
	/**
	 * The Constructor.
	 * 
	 * @param editor the editor
	 */
	public WaveformOutlinePage(final SCAFormEditor editor, final PageBook pagebook) {
		super(editor);
		super.setLabelProvider(new ComponentOutlinePageAdapterFactoryLabelProvider(getAdapterFactory()));
		super.createControl(pagebook);
	}

	@Override
	protected void addItemProviders(ComposedAdapterFactory itemAdapterFactory) {
		// TODO Auto-generated method stub
	}
	
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean getChildren(Object parent) {
		boolean retVal = false;
		if (parent instanceof FeatureMapEntryWrapperItemProvider) {
			FeatureMapEntryWrapperItemProvider item = (FeatureMapEntryWrapperItemProvider) parent;
			if (item.getValue() instanceof ContainmentUpdatingFeatureMapEntry) {
				if (((ContainmentUpdatingFeatureMapEntry) item.getValue()).getValue() instanceof HostCollocation) {
					retVal = true;
				}
			}
		} else if (parent instanceof Partitioning) {
			retVal = true;
		}
		return retVal;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getParentPageId(Object item) {
		String pageId = null;
		if (item instanceof Partitioning || item instanceof DelegatingWrapperItemProvider || item instanceof FeatureMapEntryWrapperItemProvider) {
			pageId = CustomDiagramEditor.PAGE_ID;
		}
		if (pageId != null) {
			return pageId;
		}
		return super.getParentPageId(item);
	}

}
