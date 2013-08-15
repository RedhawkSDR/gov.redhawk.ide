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

import gov.redhawk.ide.debug.LocalSca;
import gov.redhawk.ide.debug.LocalScaDeviceManager;
import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.debug.ScaDebugPackage;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.model.sca.ScaDomainManagerRegistry;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.model.sca.provider.ScaItemProviderAdapterFactory;
import gov.redhawk.sca.ui.ScaContentProvider;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.jface.viewers.TreeViewer;

/**
 * 
 */
public class ScaDebugContentProvider extends ScaContentProvider {
	private static final Object[] EMPTY_OBJECTS = new Object[0];
	private boolean disposed = false;
	private Adapter listener = new AdapterImpl() {
		public void notifyChanged(org.eclipse.emf.common.notify.Notification msg) {
			if (disposed) {
				if (msg.getNotifier() instanceof Notifier) {
					((Notifier) msg.getNotifier()).eAdapters().remove(this);
					return;
				}
			}
			super.notifyChanged(msg);
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
				case ScaDebugPackage.LOCAL_SCA__SANDBOX_WAVEFORM:
					if (msg.getNewValue() instanceof LocalScaWaveform) {
						((LocalScaWaveform) msg.getNewValue()).eAdapters().add(this);
					}
					if (msg.getOldValue() instanceof LocalScaWaveform) {
						((LocalScaWaveform) msg.getOldValue()).eAdapters().remove(this);
					}
					break;
				case ScaDebugPackage.LOCAL_SCA__SANDBOX_DEVICE_MANAGER:
					if (msg.getNewValue() instanceof LocalScaDeviceManager) {
						((LocalScaDeviceManager) msg.getNewValue()).eAdapters().add(this);
					}
					if (msg.getOldValue() instanceof LocalScaDeviceManager) {
						((LocalScaDeviceManager) msg.getOldValue()).eAdapters().remove(this);
					}
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

		private void reveal(final Object obj) {
			if (obj == null) {
				return;
			} else if (viewer != null && !viewer.getControl().isDisposed()) {
				viewer.getControl().getDisplay().asyncExec(new Runnable() {

					@Override
					public void run() {
						if (obj instanceof Collection< ? >) {
							for (Object o : ((Collection< ? >) obj)) {
								((TreeViewer) viewer).reveal(o);
							}
						} else {
							((TreeViewer) viewer).reveal(obj);
						}
					}

				});
			}
		}
	};

	public ScaDebugContentProvider() {
		super(ScaDebugContentProvider.createAdapterFactory());
		final ScaDebugPlugin activator = ScaDebugPlugin.getInstance();
		final LocalSca localSca = activator.getLocalSca();
		ScaModelCommand.execute(localSca, new ScaModelCommand() {

			@Override
			public void execute() {
				localSca.eAdapters().add(listener);
				if (localSca.getSandboxWaveform() != null) {
					localSca.getSandboxWaveform().eAdapters().add(listener);
				}
			}
		});
	}

	@Override
	public void dispose() {
		if (disposed) {
			return;
		}
		disposed = true;
		final ScaDebugPlugin activator = ScaDebugPlugin.getInstance();
		final LocalSca localSca = activator.getLocalSca();
		ScaModelCommand.execute(localSca, new ScaModelCommand() {

			@Override
			public void execute() {
				localSca.eAdapters().remove(listener);
				if (localSca.getSandboxWaveform() != null) {
					localSca.getSandboxWaveform().eAdapters().remove(listener);
				}
				if (localSca.getSandboxDeviceManager() != null) {
					localSca.getSandboxDeviceManager().eAdapters().remove(listener);
				}
			}
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
		final ScaDebugPlugin activator = ScaDebugPlugin.getInstance();
		if (activator == null) {
			return EMPTY_OBJECTS;
		}
		if (object instanceof IWorkspaceRoot) {
			return new Object[] { activator.getLocalSca() };
		} else if (object instanceof ScaDomainManagerRegistry) {
			return new Object[] { activator.getLocalSca() };
		} else {
			return EMPTY_OBJECTS;
		}
	}
}
