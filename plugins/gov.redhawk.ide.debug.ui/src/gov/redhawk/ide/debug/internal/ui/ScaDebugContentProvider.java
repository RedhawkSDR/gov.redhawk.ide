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
package gov.redhawk.ide.debug.internal.ui;

import java.util.Collection;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl.BasicFeatureMapEntry;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.jface.viewers.TreeViewer;

import gov.redhawk.ide.debug.LocalSca;
import gov.redhawk.ide.debug.LocalScaDeviceManager;
import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.debug.ScaDebugPackage;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.model.sca.provider.ScaItemProviderAdapterFactory;
import gov.redhawk.sca.ui.ScaContentProvider;

/**
 * Content provider for the SCA debug model. Used by the REDHAWK Explorer common navigator.
 */
public class ScaDebugContentProvider extends ScaContentProvider {

	private boolean disposed = false;

	private Adapter revealListener = new EContentAdapter() {
		protected void addAdapter(Notifier notifier) {
			// We only care about LocalScaWaveform, LocalSca, LocalScaDeviceManager
			if (notifier instanceof LocalScaWaveform || notifier instanceof LocalSca || notifier instanceof LocalScaDeviceManager) {
				super.addAdapter(notifier);
			}
		}

		@Override
		public void notifyChanged(org.eclipse.emf.common.notify.Notification msg) {
			super.notifyChanged(msg);

			// Don't need to proceed further if disposed
			if (disposed) {
				return;
			}

			if (msg.getNotifier() instanceof LocalScaWaveform) {
				switch (msg.getFeatureID(LocalScaWaveform.class)) {
				case ScaDebugPackage.LOCAL_SCA_WAVEFORM__COMPONENTS:
					reveal(msg.getNewValue());
					break;
				default:
					break;
				}
			} else if (msg.getNotifier() instanceof LocalSca) {
				switch (msg.getFeatureID(LocalSca.class)) {
				case ScaDebugPackage.LOCAL_SCA__WAVEFORMS:
					reveal(msg.getNewValue());
					break;
				default:
					break;
				}
			} else if (msg.getNotifier() instanceof LocalScaDeviceManager) {
				switch (msg.getFeatureID(LocalScaDeviceManager.class)) {
				case ScaDebugPackage.LOCAL_SCA_DEVICE_MANAGER__DEVICES:
				case ScaDebugPackage.LOCAL_SCA_DEVICE_MANAGER__SERVICES:
					reveal(msg.getNewValue());
					break;
				default:
					break;
				}
			}
		}

		/**
		 * @param obj The object to reveal in the TreeViewer
		 */
		private void reveal(final Object obj) {
			if (obj instanceof BasicFeatureMapEntry) {
				BasicFeatureMapEntry entry = (BasicFeatureMapEntry) obj;
				reveal(entry.getValue());
			} else if (obj == null) {
				return;
			} else if (viewer != null && !viewer.getControl().isDisposed()) {
				viewer.getControl().getDisplay().asyncExec(() -> {
					if (viewer == null) {
						return;
					}
					if (obj instanceof Collection< ? >) {
						for (Object o : ((Collection< ? >) obj)) {
							((TreeViewer) viewer).reveal(o);
						}
					} else {
						((TreeViewer) viewer).reveal(obj);
					}
				});
			}
		}
	};

	public ScaDebugContentProvider() {
		super(ScaDebugContentProvider.createAdapterFactory());
		final LocalSca localSca = ScaDebugPlugin.getInstance().getLocalSca();
		ScaModelCommand.execute(localSca, () -> {
			localSca.eAdapters().add(revealListener);
		});
	}

	@Override
	public void dispose() {
		if (disposed) {
			return;
		}
		disposed = true;
		final LocalSca localSca = ScaDebugPlugin.getInstance().getLocalSca();
		ScaModelCommand.execute(localSca, () -> {
			localSca.eAdapters().remove(revealListener);
		});
		super.dispose();
	}

	protected static AdapterFactory createAdapterFactory() {
		final ComposedAdapterFactory factory = new ComposedAdapterFactory();
		factory.addAdapterFactory(new ScaItemProviderAdapterFactory());
		factory.addAdapterFactory(ScaContentProvider.createAdapterFactory());
		return factory;
	}

	@Override
	public Object[] getElements(final Object object) {
		return new Object[] { ScaDebugPlugin.getInstance().getLocalSca() };
	}
}
