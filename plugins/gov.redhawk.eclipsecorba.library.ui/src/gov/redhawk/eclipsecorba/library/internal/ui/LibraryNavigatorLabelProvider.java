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
package gov.redhawk.eclipsecorba.library.internal.ui;

import gov.redhawk.eclipsecorba.idl.expressions.util.ExpressionsAdapterFactory;
import gov.redhawk.eclipsecorba.idl.operations.provider.OperationsItemProviderAdapterFactory;
import gov.redhawk.eclipsecorba.idl.provider.IdlItemProviderAdapterFactory;
import gov.redhawk.eclipsecorba.idl.types.provider.TypesItemProviderAdapterFactory;
import gov.redhawk.eclipsecorba.library.provider.IdlLibraryItemProviderAdapterFactory;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * 
 */
public class LibraryNavigatorLabelProvider extends DynamicTransactionalAdapterFactoryLabelProvider implements ILabelProvider {

	private final WorkbenchLabelProvider labelProvider = new WorkbenchLabelProvider();

	public LibraryNavigatorLabelProvider() {
		super(LibraryNavigatorLabelProvider.createAdapterFactory());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText(final Object object) {
		if (object instanceof IWorkbenchAdapter) {
			return this.labelProvider.getText(object);
		}
		return super.getText(object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image getImage(final Object object) {
		if (object instanceof IWorkbenchAdapter) {
			return this.labelProvider.getImage(object);
		}
		return super.getImage(object);
	}

	private static AdapterFactory createAdapterFactory() {
		final ComposedAdapterFactory factory = new ComposedAdapterFactory();
		factory.addAdapterFactory(new IdlLibraryItemProviderAdapterFactory());
		factory.addAdapterFactory(new IdlItemProviderAdapterFactory());
		factory.addAdapterFactory(new OperationsItemProviderAdapterFactory());
		factory.addAdapterFactory(new ExpressionsAdapterFactory());
		factory.addAdapterFactory(new TypesItemProviderAdapterFactory());
		return factory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		this.labelProvider.dispose();
		((ComposedAdapterFactory) getAdapterFactory()).dispose();
		super.dispose();
	}

}
