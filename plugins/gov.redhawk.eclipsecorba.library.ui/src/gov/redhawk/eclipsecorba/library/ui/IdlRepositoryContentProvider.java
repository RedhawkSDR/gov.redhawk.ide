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
package gov.redhawk.eclipsecorba.library.ui;

import gov.redhawk.eclipsecorba.idl.expressions.util.ExpressionsAdapterFactory;
import gov.redhawk.eclipsecorba.idl.operations.provider.OperationsItemProviderAdapterFactory;
import gov.redhawk.eclipsecorba.idl.provider.IdlItemProviderAdapterFactory;
import gov.redhawk.eclipsecorba.idl.types.provider.TypesItemProviderAdapterFactory;
import gov.redhawk.eclipsecorba.library.IdlLibrary;
import gov.redhawk.eclipsecorba.library.provider.RepositoryItemProviderAdapterFactory;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IDisposable;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.ui.provider.TransactionalAdapterFactoryContentProvider;

/**
 * A deferred content provided for IdlLibraries.
 * @since 1.1
 */
public class IdlRepositoryContentProvider extends  TransactionalAdapterFactoryContentProvider {
	
	public IdlRepositoryContentProvider(TransactionalEditingDomain editingDomain) {
		this(editingDomain, createAdapterFactory());
	}
	
	public IdlRepositoryContentProvider(TransactionalEditingDomain editingDomain, AdapterFactory adapterFactory) {
		super(editingDomain, adapterFactory);
		Assert.isNotNull(editingDomain);
	}
	
	/**
	 * Creates the adapter factory.
	 * 
	 * @return the adapter factory
	 */
	protected static AdapterFactory createAdapterFactory() {
		final ComposedAdapterFactory adapterFactory = new ComposedAdapterFactory();
		adapterFactory.addAdapterFactory(new IdlItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new OperationsItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new ExpressionsAdapterFactory());
		adapterFactory.addAdapterFactory(new TypesItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new RepositoryItemProviderAdapterFactory());

		return adapterFactory;
	}

	@Override
	public Object getParent(final Object object) {
		if (object instanceof IdlLibrary) {
			return null;
		}
		final Object retVal = super.getParent(object);
		return retVal;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		if (this.adapterFactory instanceof IDisposable) {
			((IDisposable) this.adapterFactory).dispose();
		}
		this.adapterFactory = null;
		super.dispose();
	}
	

}
