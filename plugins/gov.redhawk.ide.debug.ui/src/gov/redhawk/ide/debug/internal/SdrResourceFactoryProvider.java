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
package gov.redhawk.ide.debug.internal;

import gov.redhawk.core.resourcefactory.AbstractResourceFactoryProvider;
import gov.redhawk.core.resourcefactory.ComponentDesc;
import gov.redhawk.core.resourcefactory.ResourceDesc;
import gov.redhawk.ide.debug.SpdResourceFactory;
import gov.redhawk.ide.sdr.SdrPackage;
import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.sca.util.MutexRule;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EObject;

import CF.ResourceFactoryOperations;

/**
 * 
 */
public class SdrResourceFactoryProvider extends AbstractResourceFactoryProvider {

	private static final MutexRule RULE = new MutexRule(SdrResourceFactoryProvider.class);
	private static final String SDR_CATEGORY = "SDR";

	private class SPDListener extends AdapterImpl {

		@Override
		public void notifyChanged(final org.eclipse.emf.common.notify.Notification msg) {
			if (disposed) {
				if (msg.getNotifier() instanceof Notifier) {
					((Notifier) msg.getNotifier()).eAdapters().remove(this);
				}
				return;
			}
			if (msg.getFeature() == SdrPackage.Literals.SOFT_PKG_REGISTRY__COMPONENTS) {
				switch (msg.getEventType()) {
				case Notification.ADD:
					addResource((SoftPkg) msg.getNewValue(), new SpdResourceFactory((SoftPkg) msg.getNewValue()));
					break;
				case Notification.ADD_MANY:
					for (final Object obj : (Collection< ? >) msg.getNewValue()) {
						addResource((SoftPkg) obj, new SpdResourceFactory((SoftPkg) obj));
					}
					break;
				case Notification.REMOVE:
					removeResource((SoftPkg) msg.getOldValue());
					break;
				case Notification.REMOVE_MANY:
					for (final Object obj : (Collection< ? >) msg.getOldValue()) {
						removeResource((SoftPkg) obj);
					}
					break;
				default:
					break;
				}
			}
		}
	};

	private final Adapter waveformsListener = new AdapterImpl() {
		@Override
		public void notifyChanged(final org.eclipse.emf.common.notify.Notification msg) {
			if (disposed) {
				if (msg.getNotifier() instanceof Notifier) {
					((Notifier) msg.getNotifier()).eAdapters().remove(this);
				}
				return;
			}
			if (msg.getFeature() == SdrPackage.Literals.WAVEFORMS_CONTAINER__WAVEFORMS) {
				switch (msg.getEventType()) {
				case Notification.ADD:
					// TODO Add SDR Waveform Factory
					//					addResource((SoftwareAssembly) msg.getNewValue(), new SdrWaveformFactory((SoftwareAssembly) msg.getNewValue()));
					break;
				case Notification.ADD_MANY:
					// TODO Add SDR Waveform Factory
					//					for (final Object obj : (Collection< ? >) msg.getNewValue()) {
					//						addResource((SoftwareAssembly) obj, new SdrWaveformFactory((SoftwareAssembly) obj));
					//					}
					break;
				case Notification.REMOVE:
					removeResource((SoftwareAssembly) msg.getOldValue());
					break;
				case Notification.REMOVE_MANY:
					for (final Object obj : (Collection< ? >) msg.getOldValue()) {
						removeResource((SoftwareAssembly) obj);
					}
					break;
				default:
					break;
				}
			}
		}
	};
	private final Map<EObject, ResourceDesc> resourceMap = Collections.synchronizedMap(new HashMap<EObject, ResourceDesc>());
	private SdrRoot root;
	private SPDListener componentsListener;
	private SPDListener devicesListener;
	private SPDListener serviceListener;
	private boolean disposed;

	/**
	 * {@inheritDoc}
	 */
	public SdrResourceFactoryProvider() {
		SdrUiPlugin sdrPlugin = SdrUiPlugin.getDefault();
		if (sdrPlugin != null) {
			this.root = sdrPlugin.getTargetSdrRoot();
		}
		if (this.root != null) {
			this.componentsListener = new SPDListener();
			this.devicesListener = new SPDListener();
			this.serviceListener = new SPDListener();
			ScaModelCommand.execute(this.root, new ScaModelCommand() {

				public void execute() {
					for (final SoftPkg spd : SdrResourceFactoryProvider.this.root.getComponentsContainer().getComponents()) {
						addResource(spd, new SpdResourceFactory(spd));
					}
					for (final SoftPkg spd : SdrResourceFactoryProvider.this.root.getDevicesContainer().getComponents()) {
						addResource(spd, new SpdResourceFactory(spd));
					}
					for (final SoftPkg spd : SdrResourceFactoryProvider.this.root.getServicesContainer().getComponents()) {
						addResource(spd, new SpdResourceFactory(spd));
					}
					// TODO Add SDR Waveform Factory
					//					for (final SoftwareAssembly sad : SdrResourceFactoryProvider.this.root.getWaveformsContainer().getWaveforms()) {
					//						addResource(sad, new SdrWaveformFactory(sad));
					//					}
					SdrResourceFactoryProvider.this.root.getComponentsContainer().eAdapters().add(SdrResourceFactoryProvider.this.componentsListener);
					SdrResourceFactoryProvider.this.root.getDevicesContainer().eAdapters().add(SdrResourceFactoryProvider.this.devicesListener);
					SdrResourceFactoryProvider.this.root.getServicesContainer().eAdapters().add(SdrResourceFactoryProvider.this.serviceListener);
					SdrResourceFactoryProvider.this.root.getWaveformsContainer().eAdapters().add(SdrResourceFactoryProvider.this.waveformsListener);
				}
			});
		}
	}

	private void addResource(final SoftPkg spd, final ResourceFactoryOperations factory) {
		ComponentDesc desc = new ComponentDesc(spd, factory);
		desc.setCategory(SDR_CATEGORY);
		SdrResourceFactoryProvider.this.resourceMap.put(spd, desc);
		addResourceDesc(desc);
	}

	private void removeResource(final EObject resource) {
		final ResourceDesc desc = this.resourceMap.get(resource);
		if (desc != null) {
			removeResourceDesc(desc);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void dispose() {
		Job.getJobManager().beginRule(RULE, null);
		try {
			if (disposed) {
				return;
			}
			disposed = true;
		} finally {
			Job.getJobManager().endRule(RULE);
		}
		this.root.getComponentsContainer().eAdapters().remove(this.componentsListener);
		this.root.getDevicesContainer().eAdapters().remove(this.devicesListener);
		this.root.getServicesContainer().eAdapters().remove(this.serviceListener);
		this.root.getWaveformsContainer().eAdapters().remove(this.waveformsListener);
		this.root = null;
		synchronized (this.resourceMap) {
			for (final ResourceDesc desc : this.resourceMap.values()) {
				removeResourceDesc(desc);
			}
			this.resourceMap.clear();
		}
	}

}
