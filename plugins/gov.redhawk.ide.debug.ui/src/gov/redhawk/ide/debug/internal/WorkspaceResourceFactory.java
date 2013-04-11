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

import gov.redhawk.ide.debug.LocalComponentProgramLaunchDelegate;
import gov.redhawk.ide.debug.LocalSca;
import gov.redhawk.ide.debug.ScaDebugLaunchConstants;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.ScaLauncherUtil;
import gov.redhawk.model.sca.ScaComponent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.AnyUtils;
import mil.jpeojtrs.sca.util.NamedThreadFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import CF.DataType;
import CF.ErrorNumberType;
import CF.Resource;
import CF.ResourceFactoryPackage.CreateResourceFailure;

/**
 * 
 */
public class WorkspaceResourceFactory extends AbstractResourceFactory {

	private static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory(WorkspaceResourceFactory.class.getName()));

	private final SoftPkg spd;
	private final IFile profile;

	public WorkspaceResourceFactory(final IFile profile) throws IOException {
		final ResourceSet resourceSet = new ResourceSetImpl();
		final org.eclipse.emf.ecore.resource.Resource resource = resourceSet.getResource(URI.createPlatformResourceURI(profile.getFullPath().toString(), true),
		        true);
		resource.load(null);
		this.spd = SoftPkg.Util.getSoftPkg(resource);
		this.profile = profile;
	}

	/**
	 * {@inheritDoc}
	 */
	public String identifier() {
		return this.spd.getId();
	}

	@Override
	protected Resource createInstance(final String resourceId, final DataType[] qualifiers, String mode) throws CreateResourceFailure {
		final LocalSca localSca = ScaDebugPlugin.getInstance().getLocalSca();
		final Implementation impl = chooseImplementation(this.spd);
		try {

			final Map<String, String> argMap = new HashMap<String, String>();
			argMap.put(ScaDebugLaunchConstants.ARG_COMPONENT_IDENTIFIER, resourceId);
			argMap.put(ScaDebugLaunchConstants.ARG_EXEC_PARAMS, createParams(qualifiers));

			final ILaunchConfigurationWorkingCopy config = LocalComponentProgramLaunchDelegate.createLaunchConfiguration(resourceId,
			        impl.getId(),
			        argMap,
			        URI.createPlatformResourceURI(this.profile.getFullPath().toPortableString(), true));
			if (mode == null) {
				mode = ILaunchManager.RUN_MODE;
			}
			config.launch(mode, new NullProgressMonitor());
		} catch (final CoreException e) {
			throw new CreateResourceFailure(ErrorNumberType.CF_EIO, e.getMessage());
		}
		final Callable<Resource> callable = new Callable<Resource>() {

			public Resource call() throws Exception {
				while (!Thread.interrupted()) {
					for (final ScaComponent comp : localSca.getSandboxWaveform().getComponents()) {
						if (resourceId.equals(comp.fetchIdentifier(null))) {
							return comp.getObj();
						}
					}
					Thread.sleep(500);
				}
				return null;
			}
		};
		final Future<Resource> result = WorkspaceResourceFactory.executor.submit(callable);

		try {
			return result.get(10000, TimeUnit.MILLISECONDS);
		} catch (final InterruptedException e) {
			throw new CreateResourceFailure(ErrorNumberType.CF_EIO, e.getMessage());
		} catch (final ExecutionException e) {
			throw new CreateResourceFailure(ErrorNumberType.CF_EIO, e.getMessage());
		} catch (final TimeoutException e) {
			result.cancel(true);
			throw new CreateResourceFailure(ErrorNumberType.CF_EIO, e.getMessage());
		}
	}

	private String createParams(final DataType[] qualifiers) {
		final Map<String, Object> execParams = new HashMap<String, Object>();
		for (final DataType t : qualifiers) {
			execParams.put(t.id, AnyUtils.convertAny(t.value));
		}
		return ScaLauncherUtil.createExecParamString(execParams);
	}

	private Implementation chooseImplementation(final SoftPkg spd) {
		return spd.getImplementation().get(0);
	}

}
