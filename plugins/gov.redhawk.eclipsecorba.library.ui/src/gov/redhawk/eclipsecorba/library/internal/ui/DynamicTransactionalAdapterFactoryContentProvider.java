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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.ui.internal.EMFTransactionUIPlugin;
import org.eclipse.emf.transaction.ui.internal.EMFTransactionUIStatusCodes;
import org.eclipse.emf.transaction.ui.internal.Tracing;
import org.eclipse.emf.transaction.ui.internal.l10n.Messages;
import org.eclipse.emf.transaction.ui.provider.TransactionalPropertySource;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * Automatically wraps any potential access to model objects in read transactions.
 * Note that this is not necessary in the case of the
 * method because this will always be called in a transaction context.
 */
public class DynamicTransactionalAdapterFactoryContentProvider extends org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider {

	/**
	 * Initializes me with the editing domain in which I create read
	 * transactions and that adapter factory that provides content providers.
	 * 
	 * @param domain my editing domain
	 * @param adapterFactory the adapter factory
	 */
	public DynamicTransactionalAdapterFactoryContentProvider(final AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/**
	 * Runs the specified runnable in the editing domain, with interrupt
	 * handling.
	 * 
	 * @param <T> the result type of the runnable
	 * 
	 * @param run the runnable to run
	 * 
	 * @return its result, or <code>null</code> on interrupt
	 */
	protected < T > T run(final TransactionalEditingDomain domain, final RunnableWithResult< ? extends T> run) {
		if (domain == null) {
			run.run();
			return run.getResult();
		}
		try {
			return TransactionUtil.runExclusive(domain, run);
		} catch (final InterruptedException e) {
			Tracing.catching(DynamicTransactionalAdapterFactoryContentProvider.class, "run", e); //$NON-NLS-1$

			// propagate interrupt status because we are not throwing
			Thread.currentThread().interrupt();

			EMFTransactionUIPlugin.INSTANCE.log(new Status(IStatus.ERROR, EMFTransactionUIPlugin.getPluginId(),
			        EMFTransactionUIStatusCodes.CONTENT_PROVIDER_INTERRUPTED, Messages.contentInterrupt, e));

			return null;
		}
	}

	/**
	 * Extends the inherited implementation by running in a read-only transaction.
	 * The returned property source also uses transactions to access properties.
	 */
	@Override
	protected IPropertySource createPropertySource(final Object object, final IItemPropertySource itemPropertySource) {
		return wrap(run(getEditingDomain(object), new RunnableWithResult.Impl<IPropertySource>() {
			public void run() {
				setResult(DynamicTransactionalAdapterFactoryContentProvider.super.createPropertySource(object, itemPropertySource));
			}
		}));
	}

	private TransactionalEditingDomain getEditingDomain(final Object object) {
		return TransactionUtil.getEditingDomain(object);
	}

	/**
	 * Extends the inherited implementation by running in a read-only transaction.
	 */
	@Override
	public Object[] getChildren(final Object object) {
		return run(getEditingDomain(object), new RunnableWithResult.Impl<Object[]>() {
			public void run() {
				setResult(DynamicTransactionalAdapterFactoryContentProvider.super.getChildren(object));
			}
		});
	}

	/**
	 * Extends the inherited implementation by running in a read-only transaction.
	 */
	@Override
	public Object[] getElements(final Object object) {
		return run(getEditingDomain(object), new RunnableWithResult.Impl<Object[]>() {
			public void run() {
				setResult(DynamicTransactionalAdapterFactoryContentProvider.super.getElements(object));
			}
		});
	}

	/**
	 * Extends the inherited implementation by running in a read-only transaction.
	 */
	@Override
	public Object getParent(final Object object) {
		return run(getEditingDomain(object), new RunnableWithResult.Impl<Object>() {
			public void run() {
				setResult(DynamicTransactionalAdapterFactoryContentProvider.super.getParent(object));
			}
		});
	}

	/**
	 * Extends the inherited implementation by running in a read-only transaction.
	 * The returned property source also uses transactions to access properties.
	 */
	@Override
	public IPropertySource getPropertySource(final Object object) {
		return wrap(run(getEditingDomain(object), new RunnableWithResult.Impl<IPropertySource>() {
			public void run() {
				setResult(DynamicTransactionalAdapterFactoryContentProvider.super.getPropertySource(object));
			}
		}));
	}

	/**
	 * Extends the inherited implementation by running in a read-only transaction.
	 */
	@Override
	public boolean hasChildren(final Object object) {
		return run(getEditingDomain(object), new RunnableWithResult.Impl<Boolean>() {
			public void run() {
				setResult(DynamicTransactionalAdapterFactoryContentProvider.super.hasChildren(object));
			}
		});
	}

	/**
	 * Extends the inherited implementation by running in a read-only transaction.
	 */
	@Override
	public void inputChanged(final Viewer vwr, final Object oldInput, final Object newInput) {
		run(getEditingDomain(newInput), new RunnableWithResult.Impl<Object>() {
			public void run() {
				DynamicTransactionalAdapterFactoryContentProvider.super.inputChanged(vwr, oldInput, newInput);
			}
		});
	}

	/**
	 * Wraps a property source in a transactional property source.
	 * 
	 * @param propertySource the property source to wrap
	 * 
	 * @return a wrapper that delegates to the original property source within
	 *     transactions
	 */
	protected IPropertySource wrap(final IPropertySource propertySource) {
		return (propertySource == null) ? null : new TransactionalPropertySource(getEditingDomain(propertySource.getEditableValue()), propertySource);
	}
}
