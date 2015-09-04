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

import java.util.HashMap;
import java.util.Map;

import mil.jpeojtrs.sca.dcd.DcdDocumentRoot;
import mil.jpeojtrs.sca.dcd.DcdFactory;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.sad.SadDocumentRoot;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.util.DceUuidUtil;
import mil.jpeojtrs.sca.util.QueryParser;
import mil.jpeojtrs.sca.util.ScaFileSystemConstants;

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
	private static final LocalScaImpl SANDBOX = (LocalScaImpl) ScaDebugFactory.eINSTANCE.createLocalSca();
	private static final TransactionalEditingDomain SANDBOX_DOMAIN;
	private static final Resource SANDBOX_RESOURCE;
	static {
		SANDBOX_DOMAIN = TransactionalEditingDomain.Registry.INSTANCE.getEditingDomain(ScaPlugin.EDITING_DOMAIN_ID);
		SANDBOX_RESOURCE = SANDBOX_DOMAIN.getResourceSet().createResource(URI.createURI("virtual://localSca.scaDebug"));
		SANDBOX_DOMAIN.getCommandStack().execute(new ScaModelCommand() {

			@Override
			public void execute() {
				SANDBOX_RESOURCE.getContents().add(SANDBOX);
			}
		});
	}
	
	

	private ScaDebugInstance() {
		Job job = new Job("Init Local REDHAWK Sandbox") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					init(monitor);
				} catch (CoreException e) {
					return new Status(e.getStatus().getSeverity(), ScaDebugPlugin.ID, "Failed in initialize Sandbox.", e);
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	public boolean isInit() {
		return SANDBOX.getSandbox() != null;
	}

	public synchronized void init(IProgressMonitor monitor) throws CoreException {
		if (SANDBOX.isDisposed()) {
			throw new CoreException(new Status(Status.ERROR, ScaDebugPlugin.ID, "Sandbox is disposed.", null));
		}
		if (isInit()) {
			return;
		}

		final SandboxImpl impl = new SandboxImpl(SANDBOX);
		if (SANDBOX.getSession() == null) {
			return;
		}
		final POA poa = SANDBOX.getSession().getPOA();
		final Sandbox sandboxRef = ScaDebugInstance.createSandboxRef(poa, impl);
		final LocalFileManager fileManagerRef = ScaDebugInstance.createFileManager(poa);

		SANDBOX_DOMAIN.getCommandStack().execute(new ScaModelCommand() {

			@Override
			public void execute() {
				final NotifyingNamingContext rootContext = ScaDebugInstance.createRootContext(poa);
				final LocalScaWaveform sandboxWaveformRef = ScaDebugInstance.createSandboxWaveform(SANDBOX_DOMAIN.getResourceSet(), rootContext);
				final LocalScaDeviceManager sandboxDeviceManagerRef = ScaDebugInstance.createSandboxDeviceManager(SANDBOX_DOMAIN.getResourceSet(),
					fileManagerRef.getObj(), rootContext);
				SANDBOX.setSandbox(impl);
				SANDBOX.init(sandboxRef, fileManagerRef, sandboxWaveformRef, sandboxDeviceManagerRef, rootContext);
			}
		});

		try {
			SANDBOX.refresh(monitor, RefreshDepth.FULL);
		} catch (InterruptedException e) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to refresh local sca.", e));
		}
	}

	public LocalSca getLocalSca() {
		return SANDBOX;
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
		final DeviceConfiguration dcd = ScaDebugInstance.createSandboxDeviceConfiguration(resourceSet);
		final URI uri = dcd.eResource().getURI();

		tmp.setProfileURI(uri);
		tmp.setProfile(uri.path());
		tmp.setProfileObj(dcd);
		tmp.setNamingContext(context.getResourceContext(dcd.eResource().getURI()));
		final DeviceManagerImpl impl = new DeviceManagerImpl(uri.path(), DceUuidUtil.createDceUUID(), "Device Manager", tmp, fm);
		tmp.setLocalDeviceManager(impl);
		return tmp;
	}

	private static DeviceConfiguration createSandboxDeviceConfiguration(final ResourceSet resourceSet) {
		URI uri = getLocalSandboxDeviceManagerURI();

		final Resource resource = resourceSet.createResource(uri);
		final DeviceConfiguration dcd = DcdFactory.eINSTANCE.createDeviceConfiguration();
		dcd.setId("Chalkboard");
		dcd.setName("Chalkboard");
		final DcdDocumentRoot root = DcdFactory.eINSTANCE.createDcdDocumentRoot();
		root.setDeviceconfiguration(dcd);
		resource.getContents().add(root);
		return dcd;
	}

	private static LocalScaWaveform createSandboxWaveform(final ResourceSet resourceSet, final NotifyingNamingContext context) {
		final String name = "Chalkboard";
		final SoftwareAssembly sad = ScaDebugInstance.createSandboxSoftwareAssembly(resourceSet);

		final LocalScaWaveform waveform = ScaDebugFactory.eINSTANCE.createLocalScaWaveform();
		waveform.setProfile(sad.eResource().getURI().path());
		waveform.setProfileURI(sad.eResource().getURI());
		waveform.setProfileObj(sad);
		waveform.setNamingContext(context.getResourceContext(sad.eResource().getURI()));

		final ApplicationImpl app = new ApplicationImpl(waveform, name, name);
		waveform.setLocalApp(app);

		// Mark the ports and components as set
		if (!waveform.isSetPorts()) {
			waveform.getPorts().clear();
		}
		if (!waveform.isSetComponents()) {
			waveform.getComponents().clear();
		}
		if (!waveform.isSetProperties()) {
			waveform.getProperties().clear();
		}

		return waveform;
	}

	private static SoftwareAssembly createSandboxSoftwareAssembly(final ResourceSet resourceSet) {
		URI sadUri = getLocalSandboxWaveformURI();

		final SoftwareAssembly sad = SadFactory.eINSTANCE.createSoftwareAssembly();
		sad.setName("Chalkboard");
		sad.setId("Chalkboard");
		final SadDocumentRoot root = SadFactory.eINSTANCE.createSadDocumentRoot();
		root.setSoftwareassembly(sad);

		if (resourceSet != null) {
			final Resource resource = resourceSet.createResource(sadUri);
			resource.getContents().add(root);
		}
		return sad;
	}

	public static final String SANDBOX_WF_REF = "SANDBOX";

	public static URI getLocalSandboxDeviceManagerURI() {
		URI retVal = URI.createURI("mem:///sandboxDeviceManager.dcd.xml");
		Map<String, String> queryMap = new HashMap<String, String>();
		queryMap.put(ScaFileSystemConstants.QUERY_PARAM_WF, SANDBOX_WF_REF);
		queryMap.put(ScaFileSystemConstants.QUERY_PARAM_NAME, "Chalkboard");
		String query = QueryParser.createQuery(queryMap);
		retVal = retVal.appendQuery(query);
		return retVal;
	}

	public static URI getLocalSandboxWaveformURI() {
		URI retVal = URI.createURI("mem:///LocalSca.sad.xml");
		Map<String, String> queryMap = new HashMap<String, String>();
		queryMap.put(ScaFileSystemConstants.QUERY_PARAM_WF, SANDBOX_WF_REF);
		queryMap.put(ScaFileSystemConstants.QUERY_PARAM_NAME, "Chalkboard");
		String query = QueryParser.createQuery(queryMap);
		retVal = retVal.appendQuery(query);
		return retVal;
	}

}
