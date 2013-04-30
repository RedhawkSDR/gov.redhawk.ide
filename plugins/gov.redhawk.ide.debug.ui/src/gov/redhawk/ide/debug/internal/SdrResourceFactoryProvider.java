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

import gov.redhawk.core.filemanager.filesystem.FileStoreFileSystem;
import gov.redhawk.core.resourcefactory.AbstractResourceFactoryProvider;
import gov.redhawk.core.resourcefactory.IResourceFactoryRegistry;
import gov.redhawk.core.resourcefactory.ResourceDesc;
import gov.redhawk.ide.debug.ui.ScaDebugUiPlugin;
import gov.redhawk.ide.sdr.SdrPackage;
import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.sca.util.MutexRule;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import CF.FileSystem;
import CF.FileSystemHelper;
import CF.FileSystemPOATie;
import CF.InvalidFileName;
import CF.ResourceFactory;
import CF.ResourceFactoryHelper;
import CF.ResourceFactoryOperations;
import CF.ResourceFactoryPOATie;
import CF.FileManagerPackage.InvalidFileSystem;
import CF.FileManagerPackage.MountPointAlreadyExists;

/**
 * 
 */
public class SdrResourceFactoryProvider extends AbstractResourceFactoryProvider {

	private IResourceFactoryRegistry registry;
	private ORB orb;
	private POA poa;
	private static final MutexRule RULE = new MutexRule(SdrResourceFactoryProvider.class);

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
					addResource((SoftPkg) msg.getNewValue(), new SdrResourceFactory((SoftPkg) msg.getNewValue()));
					break;
				case Notification.ADD_MANY:
					for (final Object obj : (Collection< ? >) msg.getNewValue()) {
						addResource((SoftPkg) obj, new SdrResourceFactory((SoftPkg) obj));
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
					addResource((SoftwareAssembly) msg.getNewValue(), new SdrWaveformFactory((SoftwareAssembly) msg.getNewValue()) );
					break;
				case Notification.ADD_MANY:
					for (final Object obj : (Collection< ? >) msg.getNewValue()) {
						addResource((SoftwareAssembly) obj, new SdrWaveformFactory((SoftwareAssembly) obj));
					}
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
	public void init(final IResourceFactoryRegistry registry, final ORB orb, final POA poa) {
		this.registry = registry;
		this.poa = poa;
		this.orb = orb;
		this.root = SdrUiPlugin.getDefault().getTargetSdrRoot();
		if (this.root != null) {
			this.componentsListener = new SPDListener();
			this.devicesListener = new SPDListener();
			this.serviceListener = new SPDListener();
			ScaModelCommand.execute(this.root, new ScaModelCommand() {

				public void execute() {
					for (final SoftPkg spd : SdrResourceFactoryProvider.this.root.getComponentsContainer().getComponents()) {
						addResource(spd, new SdrResourceFactory(spd));
					}
					for (final SoftwareAssembly sad : SdrResourceFactoryProvider.this.root.getWaveformsContainer().getWaveforms()) {
						addResource(sad, new SdrWaveformFactory(sad));
					}
					SdrResourceFactoryProvider.this.root.getComponentsContainer().eAdapters().add(SdrResourceFactoryProvider.this.componentsListener);
					SdrResourceFactoryProvider.this.root.getDevicesContainer().eAdapters().add(SdrResourceFactoryProvider.this.devicesListener);
					SdrResourceFactoryProvider.this.root.getServicesContainer().eAdapters().add(SdrResourceFactoryProvider.this.serviceListener);
					SdrResourceFactoryProvider.this.root.getWaveformsContainer().eAdapters().add(SdrResourceFactoryProvider.this.waveformsListener);
				}
			});
		}
	}

	private void addResource(final EObject resource, final ResourceFactoryOperations factory) {
		final Job job = new Job("Adding resource") {
			
			@Override
			public boolean shouldRun() {
			    return super.shouldRun() && !disposed;
			}

			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				synchronized (SdrResourceFactoryProvider.this.resourceMap) {
					if (SdrResourceFactoryProvider.this.resourceMap.get(resource) != null) {
						return Status.CANCEL_STATUS;
					}
					IFileStore store;
					try {
						store = EFS.getStore(URI.create(resource.eResource().getURI().trimSegments(1).toString()));
						final FileStoreFileSystem fs = new FileStoreFileSystem(SdrResourceFactoryProvider.this.orb, SdrResourceFactoryProvider.this.poa, store);
						final FileSystem ref = FileSystemHelper.narrow(SdrResourceFactoryProvider.this.poa.servant_to_reference(new FileSystemPOATie(fs)));
						final ResourceFactory factoryRef = ResourceFactoryHelper.narrow(SdrResourceFactoryProvider.this.poa.servant_to_reference(new ResourceFactoryPOATie(factory)));
						final ResourceDesc desc = new ResourceDesc(ref,
						        resource.eResource().getURI().path(),
						        EcoreUtil.getID(resource),
						        factoryRef,
						        getPriority());

						SdrResourceFactoryProvider.this.registry.addResourceFactory(desc);
						SdrResourceFactoryProvider.this.resourceMap.put(resource, desc);
					} catch (final CoreException e) {
						ScaDebugUiPlugin.getDefault()
						        .getLog()
						        .log(new Status(IStatus.ERROR, ScaDebugUiPlugin.PLUGIN_ID, "Failed to add SDR resource: " + resource.eResource().getURI(), e));
					} catch (final ServantNotActive e) {
						ScaDebugUiPlugin.getDefault()
						        .getLog()
						        .log(new Status(IStatus.ERROR, ScaDebugUiPlugin.PLUGIN_ID, "Failed to add SDR resource: " + resource.eResource().getURI(), e));
					} catch (final WrongPolicy e) {
						ScaDebugUiPlugin.getDefault()
						        .getLog()
						        .log(new Status(IStatus.ERROR, ScaDebugUiPlugin.PLUGIN_ID, "Failed to add SDR resource: " + resource.eResource().getURI(), e));
					} catch (final MountPointAlreadyExists e) {
						ScaDebugUiPlugin.getDefault()
						        .getLog()
						        .log(new Status(IStatus.ERROR, ScaDebugUiPlugin.PLUGIN_ID, "Failed to add SDR resource: " + resource.eResource().getURI(), e));
					} catch (final InvalidFileName e) {
						ScaDebugUiPlugin.getDefault()
						        .getLog()
						        .log(new Status(IStatus.ERROR, ScaDebugUiPlugin.PLUGIN_ID, "Failed to add SDR resource: " + resource.eResource().getURI(), e));
					} catch (final InvalidFileSystem e) {
						ScaDebugUiPlugin.getDefault()
						        .getLog()
						        .log(new Status(IStatus.ERROR, ScaDebugUiPlugin.PLUGIN_ID, "Failed to add SDR resource: " + resource.eResource().getURI(), e));
					}
				}
				return Status.OK_STATUS;
			}

		};
		job.setRule(RULE);
		job.schedule();
	}

	private void removeResource(final EObject resource) {
		final ResourceDesc desc = this.resourceMap.get(resource);
		if (desc != null) {
			removeResourceDesc(desc);
		}
	}

	private void removeResourceDesc(final ResourceDesc desc) {
		final Job job = new Job("Remove resource") {
			
			@Override
			public boolean shouldRun() {
			    return super.shouldRun() && !disposed;
			}

			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				SdrResourceFactoryProvider.this.registry.removeResourceFactory(desc);
				if (desc != null) {
					desc.dispose();
				}
				return Status.OK_STATUS;
			}

		};
		job.setRule(RULE);
		job.schedule();
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
		this.orb = null;
		this.poa = null;
		this.registry = null;
		synchronized (this.resourceMap) {
			for (final ResourceDesc desc : this.resourceMap.values()) {
				removeResourceDesc(desc);
			}
			this.resourceMap.clear();
		}
	}

}
