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
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.ui.internal.EMFTransactionUIPlugin;
import org.eclipse.emf.transaction.ui.internal.EMFTransactionUIStatusCodes;
import org.eclipse.emf.transaction.ui.internal.Tracing;
import org.eclipse.emf.transaction.ui.internal.l10n.Messages;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.swt.graphics.Image;

/**
 * Automatically wraps any potential access to model objects in read transactions.
 * 
 */
public class DynamicTransactionalAdapterFactoryLabelProvider extends org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider {

	/**
	 * Initializes me with the editing domain in which I create read
	 * transactions and that adapter factory that provides content providers.
	 * 
	 * @param domain my editing domain
	 * @param adapterFactory the adapter factory
	 */
	public DynamicTransactionalAdapterFactoryLabelProvider(final AdapterFactory adapterFactory) {
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
			Tracing.catching(DynamicTransactionalAdapterFactoryLabelProvider.class, "run", e); //$NON-NLS-1$

			// propagate interrupt status because we are not throwing
			Thread.currentThread().interrupt();

			EMFTransactionUIPlugin.INSTANCE.log(new Status(IStatus.ERROR, EMFTransactionUIPlugin.getPluginId(),
			        EMFTransactionUIStatusCodes.LABEL_PROVIDER_INTERRUPTED, Messages.labelInterrupt, e));

			return null;
		}
	}

	private TransactionalEditingDomain getEditingDomain(final Object object) {
		return TransactionUtil.getEditingDomain(object);
	}

	/**
	 * Extends the inherited implementation by running in a read-only transaction.
	 */
	@Override
	public Image getColumnImage(final Object object, final int columnIndex) {
		return run(getEditingDomain(object), new RunnableWithResult.Impl<Image>() {
			@Override
			public void run() {
				setResult(DynamicTransactionalAdapterFactoryLabelProvider.super.getColumnImage(object, columnIndex));
			}
		});
	}

	/**
	 * Extends the inherited implementation by running in a read-only transaction.
	 */
	@Override
	public String getColumnText(final Object object, final int columnIndex) {
		return run(getEditingDomain(object), new RunnableWithResult.Impl<String>() {
			@Override
			public void run() {
				setResult(DynamicTransactionalAdapterFactoryLabelProvider.super.getColumnText(object, columnIndex));
			}
		});
	}

	/**
	 * Extends the inherited implementation by running in a read-only transaction.
	 */
	@Override
	protected Image getDefaultImage(final Object object) {
		return run(getEditingDomain(object), new RunnableWithResult.Impl<Image>() {
			@Override
			public void run() {
				setResult(DynamicTransactionalAdapterFactoryLabelProvider.super.getDefaultImage(object));
			}
		});
	}

	/**
	 * Extends the inherited implementation by running in a read-only transaction.
	 */
	@Override
	public Image getImage(final Object object) {
		return run(getEditingDomain(object), new RunnableWithResult.Impl<Image>() {
			@Override
			public void run() {
				setResult(DynamicTransactionalAdapterFactoryLabelProvider.super.getImage(object));
			}
		});
	}

	/**
	 * Extends the inherited implementation by running in a read-only transaction.
	 */
	@Override
	protected Image getImageFromObject(final Object object) {
		return run(getEditingDomain(object), new RunnableWithResult.Impl<Image>() {
			@Override
			public void run() {
				setResult(DynamicTransactionalAdapterFactoryLabelProvider.super.getImageFromObject(object));
			}
		});
	}

	/**
	 * Extends the inherited implementation by running in a read-only transaction.
	 */
	@Override
	public String getText(final Object object) {
		return run(getEditingDomain(object), new RunnableWithResult.Impl<String>() {
			@Override
			public void run() {
				setResult(DynamicTransactionalAdapterFactoryLabelProvider.super.getText(object));
			}
		});
	}

	/**
	 * Extends the inherited implementation by running in a read-only transaction.
	 */
	@Override
	public boolean isLabelProperty(final Object object, final String id) {
		return run(getEditingDomain(object), new RunnableWithResult.Impl<Boolean>() {
			@Override
			public void run() {
				setResult(DynamicTransactionalAdapterFactoryLabelProvider.super.isLabelProperty(object, id));
			}
		});
	}
}
