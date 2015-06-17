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

import gov.redhawk.ide.spd.internal.ui.editor.provider.ImplementationSectionImplementationItemProvider;
import gov.redhawk.ide.spd.internal.ui.editor.provider.ImplementationSectionSoftPkgItemProvider;
import gov.redhawk.ide.spd.internal.ui.editor.provider.SpdItemProviderAdapterFactoryAdapter;
import gov.redhawk.prf.ui.editor.page.PropertiesFormPage;
import gov.redhawk.prf.ui.provider.PropertiesEditorPrfItemProviderAdapterFactory;
import gov.redhawk.ui.editor.FormOutlinePage;
import gov.redhawk.ui.editor.SCAFormEditor;
import gov.redhawk.ui.editor.ScaFormPage;
import mil.jpeojtrs.sca.prf.Properties;
import mil.jpeojtrs.sca.scd.provider.ScdItemProviderAdapterFactory;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.ui.PlatformUI;

/**
 * 
 */
public class ComponentOutlinePage extends FormOutlinePage {

	/**
	 * @since 2.0
	 */
	private class ComponentOutlinePageAdapterFactoryLabelProvider extends AdapterFactoryLabelProvider {

		public ComponentOutlinePageAdapterFactoryLabelProvider(AdapterFactory adapterFactory) {
			super(adapterFactory);
		}

		@Override
		public String getText(Object object) {
			if (object instanceof SoftPkg) {
				return "Implementations";
			} else if (object instanceof Properties) {
				return "Properties";
			} else if (object instanceof ScaFormPage) {
				return "Overview";
			}
			return super.getText(AdapterFactoryEditingDomain.unwrap(object));
		}

	}

	/**
	 * The Constructor.
	 * 
	 * @param editor the editor
	 */
	public ComponentOutlinePage(final SCAFormEditor editor) {
		super(editor);
		super.setLabelProvider(new DecoratingLabelProvider(new ComponentOutlinePageAdapterFactoryLabelProvider(getAdapterFactory()), PlatformUI.getWorkbench()
		        .getDecoratorManager().getLabelDecorator()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addItemProviders(final ComposedAdapterFactory adapterFactory) {
		final SpdItemProviderAdapterFactoryAdapter provider = new SpdItemProviderAdapterFactoryAdapter();
		provider.setSoftPkgAdapter(new ImplementationSectionSoftPkgItemProvider(provider));
		provider.setImplementationAdapter(new ImplementationSectionImplementationItemProvider(provider, this.fEditor.getMainResource()));
		adapterFactory.addAdapterFactory(provider);
		adapterFactory.addAdapterFactory(new ScdItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new PropertiesEditorPrfItemProviderAdapterFactory());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean getChildren(Object parent) {
		// Defer to the content provider 
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getParentPageId(Object item) {
		String pageId = getRootPageId(item);
		if (pageId != null) {
			return pageId;
		}
		return super.getParentPageId(item);
	}

	private String getRootPageId(Object item) {
		Object target = AdapterFactoryEditingDomain.unwrap(item);
		if (target instanceof SoftPkg) {
			return ImplementationPage.PAGE_ID;
		} else if (target instanceof Properties) {
			return PropertiesFormPage.PAGE_ID;
		} else if (target instanceof EObject) {
			// Check the parent of the item
			return getRootPageId(((EObject) target).eContainer());
		} else {
			return null;
		}
	}
}
