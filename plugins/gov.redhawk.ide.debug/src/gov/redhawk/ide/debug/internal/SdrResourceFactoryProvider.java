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
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
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
import gov.redhawk.core.filemanager.filesystem.JavaFileSystem;
import gov.redhawk.core.resourcefactory.AbstractResourceFactoryProvider;
import gov.redhawk.core.resourcefactory.ComponentDesc;
import gov.redhawk.core.resourcefactory.ResourceDesc;
import gov.redhawk.core.resourcefactory.ResourceFactoryPlugin;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.SpdResourceFactory;
import gov.redhawk.ide.sdr.SdrPackage;
import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.SoftPkgRegistry;
import gov.redhawk.ide.sdr.TargetSdrRoot;
import gov.redhawk.ide.sdr.preferences.IdeSdrPreferences;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.sca.util.OrbSession;
import mil.jpeojtrs.sca.spd.SoftPkg;

/**
 * Provides descriptions of resources in the SDRROOT which can be launched in the sandbox.
 */
public class SdrResourceFactoryProvider extends AbstractResourceFactoryProvider {

	private static final String SDR_CATEGORY = "SDR";

	/**
	 * Represents changes to the SPDs available in the target SDR root.
	 */
	private class SpdChanges {

		/**
		 * Either {@link Notification#ADD} or {@link Notification#REMOVE}
		 */
		private int action;

		private List<SoftPkg> spds;

		public SpdChanges(int action, Collection<SoftPkg> spds) {
			// Copy the list so we don't have to worry about it being changed
			this.action = action;
			this.spds = new ArrayList<>(spds);
		}
	}

	/**
	 * Adapts {@link SoftPkgRegistry} to find {@link SoftPkg}. Changes are passed to a job which will add or remove
	 * corresponding {@link SpdResourceFactory}.
	 */
	private EContentAdapter targetSdrRootAdapter = new EContentAdapter() {

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

		@SuppressWarnings("unchecked")
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
					addSoftPkgs(Collections.singletonList((SoftPkg) msg.getNewValue()));
					break;
				case Notification.ADD_MANY:
					addSoftPkgs((Collection<SoftPkg>) msg.getNewValue());
					break;
				case Notification.REMOVE:
					removeSoftPkgs(Collections.singletonList((SoftPkg) msg.getOldValue()));
					break;
				case Notification.REMOVE_MANY:
					removeSoftPkgs((Collection<SoftPkg>) msg.getOldValue());
					break;
				default:
					break;
				}
			}

			super.notifyChanged(msg);
		}

		private void addSoftPkgs(Collection<SoftPkg> newSoftPkgs) {
			synchronized (pendingChanges) {
				pendingChanges.push(new SpdChanges(Notification.ADD, newSoftPkgs));
				updateFactoriesJob.schedule();
			}
		}

		private void removeSoftPkgs(Collection<SoftPkg> oldSoftPkgs) {
			synchronized (pendingChanges) {
				pendingChanges.push(new SpdChanges(Notification.REMOVE, oldSoftPkgs));
				updateFactoriesJob.schedule();
			}
		}
	};

	private boolean debug;
	private volatile boolean disposed = false;
	private Deque<SpdChanges> pendingChanges = new LinkedList<>();
	private Job updateFactoriesJob = Job.create("Update SDR resource factories", monitor -> {
		if (disposed) {
			return Status.CANCEL_STATUS;
		}

		// Process changes until cancelled
		while (!monitor.isCanceled()) {
			// We're done if there are no more pending changes
			SpdChanges changes;
			synchronized (pendingChanges) {
				changes = pendingChanges.pollLast();
			}
			if (changes == null) {
				return Status.OK_STATUS;
			}

			// Add or remove resource factories for each SPD
			switch (changes.action) {
			case Notification.ADD:
				MultiStatus status = new MultiStatus(ScaDebugPlugin.ID, 0, "Invalid SPD file(s) were not added to the sandbox", null);
				for (final SoftPkg spd : changes.spds) {
					try {
						SpdResourceFactory factory = SpdResourceFactory.createResourceFactory(spd);
						ComponentDesc desc = new ComponentDesc(spd, factory);
						desc.setCategory(SDR_CATEGORY);
						SdrResourceFactoryProvider.this.resourceMap.put(spd, desc);
						addResourceDesc(desc);
					} catch (IllegalArgumentException e) {
						status.add(new Status(IStatus.WARNING, ScaDebugPlugin.ID, e.toString(), e));
					}
				}
				if (debug && !status.isOK()) {
					ScaDebugPlugin.log(status);
				}
				break;
			case Notification.REMOVE:
				for (final SoftPkg spd : changes.spds) {
					final ResourceDesc desc = this.resourceMap.remove(spd);
					if (desc != null) {
						removeResourceDesc(desc);
					}
				}
				break;
			default:
				break;
			}
		}
		return Status.CANCEL_STATUS;
	});

	private OrbSession session;
	private final Map<EObject, ResourceDesc> resourceMap = Collections.synchronizedMap(new HashMap<EObject, ResourceDesc>());
	private SdrRoot root;

	public SdrResourceFactoryProvider() {
		String debugOption = Platform.getDebugOption(ScaDebugPlugin.ID + "/debug/" + this.getClass().getSimpleName());
		debug = "true".equalsIgnoreCase(debugOption);

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
				ScaDebugPlugin.log(
					new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Error while mounting SDRROOT/dom directories into sandbox file manager", e));
			}
		}

		// Listen to the target SDR root for changes (also finds and adds existing SPDs)
		this.root = TargetSdrRoot.getSdrRoot();
		ScaModelCommand.execute(this.root, new ScaModelCommand() {

			@Override
			public void execute() {
				SdrResourceFactoryProvider.this.root.getComponentsContainer().eAdapters().add(SdrResourceFactoryProvider.this.targetSdrRootAdapter);
				SdrResourceFactoryProvider.this.root.getDevicesContainer().eAdapters().add(SdrResourceFactoryProvider.this.targetSdrRootAdapter);
				SdrResourceFactoryProvider.this.root.getServicesContainer().eAdapters().add(SdrResourceFactoryProvider.this.targetSdrRootAdapter);
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
			ScaDebugPlugin.log(new Status(e.getStatus().getSeverity(), ScaDebugPlugin.ID, "Unable to get POA", e));
			return;
		}
		FileSystemPOA fsPoa = new FileSystemPOATie(new JavaFileSystem(orb, poa, dir.toFile()));
		try {
			FileSystem domDepsFs = FileSystemHelper.narrow(poa.servant_to_reference(fsPoa));
			addFileSystemMount(domDepsFs, mountPoint);
		} catch (ServantNotActive | WrongPolicy e) {
			ScaDebugPlugin.log(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Unable to create virtual mount " + mountPoint, e));
		}
	}

	@Override
	public void dispose() {
		if (this.disposed) {
			return;
		}
		this.disposed = true;

		// Stop listening for changes to the target SDR root
		ScaModelCommand.execute(this.root, () -> {
			this.root.getComponentsContainer().eAdapters().remove(this.targetSdrRootAdapter);
			this.root.getDevicesContainer().eAdapters().remove(this.targetSdrRootAdapter);
			this.root.getServicesContainer().eAdapters().remove(this.targetSdrRootAdapter);
		});
		this.root = null;
		this.targetSdrRootAdapter = null;

		// Stop the job that is updating resource factories
		this.updateFactoriesJob.cancel();
		try {
			this.updateFactoriesJob.join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		this.updateFactoriesJob = null;

		// Remove resource factories
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
		this.session.dispose();
		this.session = null;
	}
}
