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

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.ui.PlatformUI;

import gov.redhawk.ide.scd.ui.editor.page.PortsFormPage;
import gov.redhawk.prf.ui.editor.page.PropertiesFormPage;
import gov.redhawk.ui.editor.FormOutlinePage;
import gov.redhawk.ui.editor.SCAFormEditor;
import gov.redhawk.ui.editor.ScaFormPage;
import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.prf.Properties;
import mil.jpeojtrs.sca.scd.ScdPackage;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdPackage;

/**
 * Provides the outline for the SPD editor.
 */
public class ComponentOutlinePage extends FormOutlinePage {

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

	public ComponentOutlinePage(final SCAFormEditor editor) {
		super(editor);

		// Create our own label provider that will decorate items that have warnings/errors.
		// We re-use the editor's adapter factory so that objects are the same between the editor & outline
		ILabelProvider provider = new ComponentOutlinePageAdapterFactoryLabelProvider(fEditor.getAdapterFactory());
		ILabelDecorator decorator = PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
		super.setLabelProvider(new DecoratingLabelProvider(provider, decorator));
	}

	@Override
	protected void addItemProviders(final ComposedAdapterFactory adapterFactory) {
		// We override getAdapterFactory(), so this isn't necessary
		throw new IllegalStateException("Internal error - this method should never be called");
	}

	@Override
	public AdapterFactory getAdapterFactory() {
		// We re-use the editor's adapter factory
		return fEditor.getAdapterFactory();
	}

	@Override
	protected boolean getChildren(Object parent) {
		// Defer to the content provider 
		return true;
	}

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
		if (target instanceof EObject) {
			EObject eTarget = (EObject) target;
			switch (eTarget.eClass().getEPackage().getName()) {
			case SpdPackage.eNAME:
				return ImplementationPage.PAGE_ID;
			case PrfPackage.eNAME:
				return PropertiesFormPage.PAGE_ID;
			case ScdPackage.eNAME:
				return PortsFormPage.PAGE_ID;
			default:
				return null;
			}
		}
		return null;
	}
}
