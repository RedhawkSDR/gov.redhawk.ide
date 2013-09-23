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

import gov.redhawk.core.filemanager.IFileManager;
import gov.redhawk.core.resourcefactory.ResourceFactoryPlugin;
import gov.redhawk.ide.debug.LocalFileManager;
import gov.redhawk.ide.debug.LocalSca;
import gov.redhawk.ide.debug.LocalScaDeviceManager;
import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.debug.NotifyingNamingContext;
import gov.redhawk.ide.debug.ScaDebugFactory;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.impl.LocalScaImpl;
import gov.redhawk.ide.debug.internal.cf.extended.impl.ApplicationImpl;
import gov.redhawk.ide.debug.internal.cf.extended.impl.SandboxImpl;
import gov.redhawk.ide.debug.internal.cf.impl.DeviceManagerImpl;
import gov.redhawk.model.sca.RefreshDepth;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.sca.ScaPlugin;
import mil.jpeojtrs.sca.dcd.DcdDocumentRoot;
import mil.jpeojtrs.sca.dcd.DcdFactory;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.sad.SadDocumentRoot;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.util.DceUuidUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import CF.FileManager;
import CF.FileManagerHelper;
import CF.FileManagerPOATie;
import ExtendedCF.Sandbox;
import ExtendedCF.SandboxHelper;
import ExtendedCF.SandboxOperations;
import ExtendedCF.SandboxPOATie;

/**
 * 
 */
public enum ScaDebugInstance {
	INSTANCE;
	private LocalScaImpl localSca;
	private TransactionalEditingDomain editingDomain;
	private Resource resource;

	private ScaDebugInstance() {
		this.editingDomain = TransactionalEditingDomain.Registry.INSTANCE.getEditingDomain(ScaPlugin.EDITING_DOMAIN_ID);
		resource = editingDomain.getResourceSet().createResource(URI.createURI("virtual://localSca.scaDebug"));
		this.localSca = (LocalScaImpl) ScaDebugFactory.eINSTANCE.createLocalSca();
		editingDomain.getCommandStack().execute(new ScaModelCommand() {

			@Override
			public void execute() {
				resource.getContents().add(ScaDebugInstance.this.localSca);
			}
		});
		Job job = new Job("Init Local SCA") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					init(monitor);
				} catch (CoreException e) {
					return e.getStatus();
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	public synchronized void init(IProgressMonitor monitor) throws CoreException {
		if (localSca.getSandbox() != null) {
			return;
		}

		final SandboxImpl impl = new SandboxImpl(localSca);
		if (localSca.getSession() == null) {
			return;
		}
		final POA poa = localSca.getSession().getPOA();
		final Sandbox sandboxRef = createSandboxRef(poa, impl);
		final LocalFileManager fileManagerRef = createFileManager(poa);

		editingDomain.getCommandStack().execute(new ScaModelCommand() {

			@Override
			public void execute() {
				final NotifyingNamingContext rootContext = createRootContext(poa);
				final LocalScaWaveform sandboxWaveformRef = createSandboxWaveform(editingDomain.getResourceSet(), rootContext);
				final LocalScaDeviceManager sandboxDeviceManagerRef = createSandboxDeviceManager(editingDomain.getResourceSet(), fileManagerRef.getObj(),
					rootContext);
				localSca.setSandbox(impl);
				localSca.init(sandboxRef, fileManagerRef, sandboxWaveformRef, sandboxDeviceManagerRef, rootContext);
			}
		});

		try {
			localSca.refresh(monitor, RefreshDepth.FULL);
		} catch (InterruptedException e) {
			throw new CoreException(new Status(Status.ERROR, ScaDebugPlugin.ID, "Failed to refresh local sca.", e));
		}
	}

	public LocalSca getLocalSca() {
		return this.localSca;
	}

	private static LocalFileManager createFileManager(final POA poa) throws CoreException {
		final LocalFileManager tmp = ScaDebugFactory.eINSTANCE.createLocalFileManager();
		ResourceFactoryPlugin rp = ResourceFactoryPlugin.getDefault();
		if (rp != null) {
			final IFileManager fm = rp.getResourceFactoryRegistry().getFileManager();
			FileManager ref;
			try {
				ref = FileManagerHelper.narrow(poa.servant_to_reference(new FileManagerPOATie(fm)));
			} catch (final ServantNotActive e) {
				throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to create file manager"));
			} catch (final WrongPolicy e) {
				throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to create file manager"));
			}
			tmp.setCorbaObj(ref);
		}
		return tmp;
	}

	private static Sandbox createSandboxRef(final POA poa, SandboxOperations sandbox) throws CoreException {
		Sandbox ref;
		try {
			ref = SandboxHelper.narrow(poa.servant_to_reference(new SandboxPOATie(sandbox)));
		} catch (final ServantNotActive e) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to create sandbox ref"));
		} catch (final WrongPolicy e) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to create sandbox ref"));
		}
		return ref;
	}

	private static NotifyingNamingContext createRootContext(final POA poa) {
		final NotifyingNamingContext context = ScaDebugFactory.eINSTANCE.createNotifyingNamingContext();
		context.setPoa(poa);
		return context;
	}

	private static LocalScaDeviceManager createSandboxDeviceManager(final ResourceSet resourceSet, final FileManager fm, final NotifyingNamingContext context) {
		final LocalScaDeviceManager tmp = ScaDebugFactory.eINSTANCE.createLocalScaDeviceManager();
		final DeviceConfiguration dcd = createSandboxDeviceConfiguration(resourceSet);
		final URI uri = dcd.eResource().getURI();

		tmp.setDataProvidersEnabled(false);
		tmp.setProfileURI(uri);
		tmp.setProfile(uri.path());
		tmp.setProfileObj(dcd);
		tmp.setNamingContext(context.getResourceContext(dcd.eResource().getURI()));
		final DeviceManagerImpl impl = new DeviceManagerImpl(uri.path(), DceUuidUtil.createDceUUID(), "Device Manager", tmp, fm);
		tmp.setLocalDeviceManager(impl);
		return tmp;
	}

	private static DeviceConfiguration createSandboxDeviceConfiguration(final ResourceSet resourceSet) {
		final URI uri = URI.createURI("mem:///sandboxDeviceManager.dcd.xml");
		final Resource resource = resourceSet.createResource(uri);
		final DeviceConfiguration dcd = DcdFactory.eINSTANCE.createDeviceConfiguration();
		final DcdDocumentRoot root = DcdFactory.eINSTANCE.createDcdDocumentRoot();
		root.setDeviceconfiguration(dcd);
		resource.getContents().add(root);
		return dcd;
	}

	private static LocalScaWaveform createSandboxWaveform(final ResourceSet resourceSet, final NotifyingNamingContext context) {
		final String name = "Chalkboard";
		final SoftwareAssembly sad = createSandboxSoftwareAssembly(resourceSet);

		final LocalScaWaveform waveform = ScaDebugFactory.eINSTANCE.createLocalScaWaveform();
		waveform.setDataProvidersEnabled(false);
		waveform.setProfile(sad.eResource().getURI().path());
		waveform.setProfileURI(sad.eResource().getURI());
		waveform.setProfileObj(sad);
		waveform.setNamingContext(context.getResourceContext(sad.eResource().getURI()));

		final ApplicationImpl app = new ApplicationImpl(waveform, name, name);
		waveform.setLocalApp(app);
		return waveform;
	}

	private static SoftwareAssembly createSandboxSoftwareAssembly(final ResourceSet resourceSet) {
		final URI sadUri = URI.createURI("mem://sandbox.sad.xml");

		final SoftwareAssembly sad = SadFactory.eINSTANCE.createSoftwareAssembly();
		final SadDocumentRoot root = SadFactory.eINSTANCE.createSadDocumentRoot();
		root.setSoftwareassembly(sad);

		if (resourceSet != null) {
			final Resource resource = resourceSet.createResource(sadUri);
			resource.getContents().add(root);
		}
		return sad;
	}

}
