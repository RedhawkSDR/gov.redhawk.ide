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

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import CF.FileSystem;
import CF.FileSystemHelper;
import CF.FileSystemPOA;
import CF.FileSystemPOATie;
import CF.ResourceFactoryOperations;
import gov.redhawk.core.filemanager.filesystem.JavaFileSystem;
import gov.redhawk.core.resourcefactory.AbstractResourceFactoryProvider;
import gov.redhawk.core.resourcefactory.ComponentDesc;
import gov.redhawk.core.resourcefactory.ResourceDesc;
import gov.redhawk.core.resourcefactory.ResourceFactoryPlugin;
import gov.redhawk.ide.debug.SpdResourceFactory;
import gov.redhawk.ide.debug.ui.ScaDebugUiPlugin;
import gov.redhawk.ide.sdr.SdrPackage;
import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.SoftPkgRegistry;
import gov.redhawk.ide.sdr.TargetSdrRoot;
import gov.redhawk.ide.sdr.preferences.IdeSdrPreferences;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.sca.util.MutexRule;
import gov.redhawk.sca.util.OrbSession;
import mil.jpeojtrs.sca.spd.SoftPkg;

/**
 * Provides descriptions of resources in the SDRROOT which can be launched in the sandbox.
 */
public class SdrResourceFactoryProvider extends AbstractResourceFactoryProvider {

	private static final MutexRule RULE = new MutexRule(SdrResourceFactoryProvider.class);
	private static final String SDR_CATEGORY = "SDR";
	private boolean debug;

	private class SPDListener extends EContentAdapter {

		@Override
		protected void addAdapter(Notifier notifier) {
			if (notifier instanceof SoftPkgRegistry) {
				addSoftPkgs(((SoftPkgRegistry) notifier).getComponents());
				super.addAdapter(notifier);
			}
		}

		@Override
		protected void removeAdapter(Notifier notifier) {
			if (notifier instanceof SoftPkgRegistry) {
				removeSoftPkgs(((SoftPkgRegistry) notifier).getComponents());
			}
			super.removeAdapter(notifier);
		}

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
					addSoftPkgs(Collections.singletonList(msg.getNewValue()));
					break;
				case Notification.ADD_MANY:
					addSoftPkgs((Collection< ? >) msg.getNewValue());
					break;
				case Notification.REMOVE:
					removeSoftPkgs(Collections.singletonList(msg.getOldValue()));
					break;
				case Notification.REMOVE_MANY:
					removeSoftPkgs((Collection< ? >) msg.getOldValue());
					break;
				default:
					break;
				}
			}

			super.notifyChanged(msg);
		}

		private void addSoftPkgs(Collection< ? > newSoftPkgs) {
			MultiStatus status = new MultiStatus(ScaDebugUiPlugin.PLUGIN_ID, 0, "Invalid SPD file(s) were not added to the sandbox", null);
			for (final Object obj : newSoftPkgs) {
				SoftPkg spd = (SoftPkg) obj;
				try {
					SpdResourceFactory resFactory = SpdResourceFactory.createResourceFactory(spd);
					addResource(spd, resFactory);
				} catch (IllegalArgumentException e) {
					status.add(new Status(IStatus.WARNING, ScaDebugUiPlugin.PLUGIN_ID, e.toString(), e));
				}
			}
			if (debug && !status.isOK()) {
				ScaDebugUiPlugin.log(status);
			}
		}

		private void removeSoftPkgs(Collection< ? > oldSoftPkgs) {
			for (final Object obj : oldSoftPkgs) {
				removeResource((SoftPkg) obj);
			}
		}
	};

	private OrbSession session;
	private final Map<EObject, ResourceDesc> resourceMap = Collections.synchronizedMap(new HashMap<EObject, ResourceDesc>());
	private SdrRoot root;
	private SPDListener componentsListener;
	private SPDListener devicesListener;
	private SPDListener serviceListener;
	private boolean disposed;

	public SdrResourceFactoryProvider() {
		String debugOption = Platform.getDebugOption(ScaDebugUiPlugin.PLUGIN_ID + "/debug/" + this.getClass().getSimpleName());
		debug = "true".equalsIgnoreCase(debugOption);

		this.root = TargetSdrRoot.getSdrRoot();
		if (this.root == null) {
			return;
		}

		IPath domPath = IdeSdrPreferences.getTargetSdrDomPath();
		DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
			@Override
			public boolean accept(Path entry) {
				// Accept directories that don't start with a dot
				return Files.isDirectory(entry) && !entry.getFileName().toString().startsWith(".");
			}
		};
		if (domPath != null && domPath.toFile().exists()) {
			try {
				try (DirectoryStream<Path> paths = Files.newDirectoryStream(domPath.toFile().toPath(), filter)) {
					for (Path path : paths) {
						addVirtualMount(path);
					}
				}
			} catch (IOException e) {
				ScaDebugUiPlugin.log(
					new Status(IStatus.ERROR, ScaDebugUiPlugin.PLUGIN_ID, "Error while mounting SDRROOT/dom directories into sandbox file manager", e));
			}
		}

		this.componentsListener = new SPDListener();
		this.devicesListener = new SPDListener();
		this.serviceListener = new SPDListener();
		ScaModelCommand.execute(this.root, new ScaModelCommand() {

			@Override
			public void execute() {
				MultiStatus status = new MultiStatus(ScaDebugUiPlugin.PLUGIN_ID, 0, "Invalid SPD file(s) were not added to the sandbox", null);
				List<SoftPkg> spds = new ArrayList<>(SdrResourceFactoryProvider.this.root.getComponentsContainer().getAllComponents());
				spds.addAll(SdrResourceFactoryProvider.this.root.getDevicesContainer().getAllComponents());
				spds.addAll(SdrResourceFactoryProvider.this.root.getServicesContainer().getAllComponents());
				for (final SoftPkg spd : spds) {
					try {
						SpdResourceFactory resFactory = SpdResourceFactory.createResourceFactory(spd);
						addResource(spd, resFactory);
					} catch (IllegalArgumentException e) {
						status.add(new Status(IStatus.WARNING, ScaDebugUiPlugin.PLUGIN_ID, e.toString(), e));
					}
				}
				SdrResourceFactoryProvider.this.root.getComponentsContainer().eAdapters().add(SdrResourceFactoryProvider.this.componentsListener);
				SdrResourceFactoryProvider.this.root.getDevicesContainer().eAdapters().add(SdrResourceFactoryProvider.this.devicesListener);
				SdrResourceFactoryProvider.this.root.getServicesContainer().eAdapters().add(SdrResourceFactoryProvider.this.serviceListener);
				if (debug && !status.isOK()) {
					ScaDebugUiPlugin.log(status);
				}
			}
		});
	}

	/**
	 * Adds a virtual mount for a location in the file system
	 * @param dir
	 * @throws CoreException
	 */
	private void addVirtualMount(Path dir) {
		String mountPoint = dir.getFileSystem().getSeparator() + dir.getFileName().toString();
		if (session == null) {
			session = OrbSession.createSession(ResourceFactoryPlugin.ID);
		}
		ORB orb = session.getOrb();
		POA poa;
		try {
			poa = session.getPOA();
		} catch (CoreException e) {
			ScaDebugUiPlugin.log(e);
			return;
		}
		FileSystemPOA fsPoa = new FileSystemPOATie(new JavaFileSystem(orb, poa, dir.toFile()));
		try {
			FileSystem domDepsFs = FileSystemHelper.narrow(poa.servant_to_reference(fsPoa));
			addFileSystemMount(domDepsFs, mountPoint);
		} catch (ServantNotActive | WrongPolicy e) {
			ScaDebugUiPlugin.log(new Status(IStatus.ERROR, ScaDebugUiPlugin.PLUGIN_ID, "Unable to create virtual mount " + mountPoint, e));
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

	@Override
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

		// Stop listening for changes
		ScaModelCommand.execute(this.root, new ScaModelCommand() {
			@Override
			public void execute() {
				root.getComponentsContainer().eAdapters().remove(componentsListener);
				root.getDevicesContainer().eAdapters().remove(devicesListener);
				root.getServicesContainer().eAdapters().remove(serviceListener);
			}
		});
		this.root = null;

		// Remove resource descriptions
		synchronized (this.resourceMap) {
			for (final ResourceDesc desc : this.resourceMap.values()) {
				removeResourceDesc(desc);
			}
			this.resourceMap.clear();
		}

		// Remove file system mounts
		for (String mount : getFileSystemMounts().keySet()) {
			removeFileSystemMount(mount);
		}

		// Dispose session
		if (session != null) {
			session.dispose();
			session = null;
		}
	}
}
