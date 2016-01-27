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
package gov.redhawk.ide.debug.internal.cf.extended.impl;

import gov.redhawk.core.resourcefactory.IResourceFactoryRegistry;
import gov.redhawk.core.resourcefactory.ResourceDesc;
import gov.redhawk.core.resourcefactory.ResourceFactoryPlugin;
import gov.redhawk.ide.debug.LocalSca;
import gov.redhawk.ide.debug.NotifyingNamingContext;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.model.sca.CorbaObjWrapper;
import gov.redhawk.model.sca.RefreshDepth;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaDevice;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.model.sca.commands.ScaModelCommandWithResult;
import gov.redhawk.sca.util.OrbSession;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.omg.CosNaming.NamingContext;

import CF.DeviceManager;
import CF.FileManager;
import CF.FileSystemOperations;
import CF.ResourceFactory;
import ExtendedCF.SandboxOperations;
import ExtendedCF.SandboxPackage.Depth;

public class SandboxImpl implements SandboxOperations {

	private final LocalSca localSca;
	private OrbSession session = OrbSession.createSession();

	public SandboxImpl(LocalSca localSca) throws CoreException {
		this.localSca = localSca;
		session.getPOA();
	}

	public void dispose() {
		session.dispose();
	}

	public OrbSession getSession() {
		return session;
	}

	@Override
	public DeviceManager deviceManager() {
		return this.localSca.getSandboxDeviceManager().getObj();
	}

	@Override
	public NamingContext namingContext() {
		return this.localSca.getSandboxWaveform().getNamingContext().getNamingContext();
	}

	@Override
	public String[] availableProfiles() {
		final IResourceFactoryRegistry registry = ResourceFactoryPlugin.getDefault().getResourceFactoryRegistry();
		final ResourceDesc[] resources = registry.getResourceDescriptors();
		final Set<String> retVal = new HashSet<String>(resources.length);
		for (final ResourceDesc desc : resources) {
			final String path = desc.getProfile();
			retVal.add(path);
		}
		return retVal.toArray(new String[retVal.size()]);
	}

	@Override
	public FileManager fileManager() {
		return this.localSca.getFileManager().getObj();
	}

	@Override
	public ExtendedCF.ResourceDesc[] registeredResources() {
		try {
			return ScaModelCommand.runExclusive(this.localSca, new RunnableWithResult.Impl<ExtendedCF.ResourceDesc[]>() {
				@Override
				public void run() {
					List<ExtendedCF.ResourceDesc> retVal = new ArrayList<ExtendedCF.ResourceDesc>();
					for (final ScaComponent cp : SandboxImpl.this.localSca.getSandboxWaveform().getComponents()) {
						final ExtendedCF.ResourceDesc desc = new ExtendedCF.ResourceDesc();
						desc.profile = cp.getProfileObj().eResource().getURI().path();
						desc.resource = cp.getObj();
						retVal.add(desc);
					}
					for (final ScaDevice< ? > cp : SandboxImpl.this.localSca.getSandboxDeviceManager().getAllDevices()) {
						final ExtendedCF.ResourceDesc desc = new ExtendedCF.ResourceDesc();
						desc.profile = cp.getProfileObj().eResource().getURI().path();
						desc.resource = cp.getObj();
						retVal.add(desc);
					}
					setResult(retVal.toArray(new ExtendedCF.ResourceDesc[retVal.size()]));
				}
			});
		} catch (InterruptedException e) {
			ScaDebugPlugin.logError("Interrupted while creating list of registered resources", e);
			return new ExtendedCF.ResourceDesc[0];
		}
	}

	@Override
	public ResourceFactory getResourceFactory(final String identifier) {
		final IResourceFactoryRegistry registry = ResourceFactoryPlugin.getDefault().getResourceFactoryRegistry();
		final ResourceDesc desc = registry.getResourceDesc(identifier);
		if (desc != null) {
			return desc.getFactoryRef();
		} else {
			return null;
		}
	}

	@Override
	public ResourceFactory getResourceFactoryByProfile(final String profile) {
		final IResourceFactoryRegistry registry = ResourceFactoryPlugin.getDefault().getResourceFactoryRegistry();
		final ResourceDesc desc = registry.getResourceDesc(profile);
		if (desc != null) {
			return desc.getFactoryRef();
		} else {
			return null;
		}
	}

	@Override
	public void refresh(final org.omg.CORBA.Object corbaObj, final Depth depth) {
		final CorbaObjWrapper< ? > wrapper = ScaModelCommandWithResult.execute(this.localSca, new ScaModelCommandWithResult<CorbaObjWrapper< ? >>() {

			@Override
			public void execute() {
				final TreeIterator<java.lang.Object> contents = EcoreUtil.getAllContents(SandboxImpl.this.localSca, false);
				while (contents.hasNext()) {
					final Object emfObj = contents.next();
					if (emfObj instanceof NotifyingNamingContext) {
						contents.prune();
						continue;
					} else if (emfObj instanceof FileSystemOperations) {
						contents.prune();
						continue;
					}
					if (emfObj instanceof CorbaObjWrapper< ? >) {
						final CorbaObjWrapper< ? > emfObjWrapper = (CorbaObjWrapper< ? >) emfObj;
						if (emfObjWrapper.getObj() != null && emfObjWrapper.getObj()._is_equivalent(corbaObj)) {
							setResult(emfObjWrapper);
							return;
						}
					}
				}
			}
		});
		if (wrapper != null) {
			switch (depth.value()) {
			case Depth._FULL:
				try {
					wrapper.refresh(null, RefreshDepth.FULL);
				} catch (final InterruptedException e) {
					// PASS
				}
				return;
			case Depth._SELF:
				try {
					wrapper.refresh(null, RefreshDepth.SELF);
				} catch (final InterruptedException e) {
					// PASS
				}
				return;
			default:
				return;
			}
		}
	}

}
