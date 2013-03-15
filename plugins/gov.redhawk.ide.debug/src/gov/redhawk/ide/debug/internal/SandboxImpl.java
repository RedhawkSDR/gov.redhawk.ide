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

import gov.redhawk.core.resourcefactory.IResourceFactoryRegistry;
import gov.redhawk.core.resourcefactory.ResourceDesc;
import gov.redhawk.core.resourcefactory.ResourceFactoryPlugin;
import gov.redhawk.ide.debug.LocalSca;
import gov.redhawk.ide.debug.NotifyingNamingContext;
import gov.redhawk.model.sca.CorbaObjWrapper;
import gov.redhawk.model.sca.RefreshDepth;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaDevice;
import gov.redhawk.model.sca.ScaWaveform;
import gov.redhawk.model.sca.commands.ScaModelCommandWithResult;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.omg.CosNaming.NamingContext;

import CF.DeviceManager;
import CF.FileManager;
import CF.FileSystemOperations;
import CF.ResourceFactory;
import ExtendedCF.SandboxOperations;
import ExtendedCF.SandboxPackage.Depth;

/**
 * 
 */
public class SandboxImpl implements SandboxOperations {

	private final LocalSca localSca;
	
	/**
     * @since 2.0
     */
	public SandboxImpl(LocalSca localSca) {
		this.localSca = localSca;
	}

	/**
	 * {@inheritDoc}
	 */
	public DeviceManager deviceManager() {
		return this.localSca.getSandboxDeviceManager().getObj();
	}

	public NamingContext namingContext() {
		return this.localSca.getSandboxWaveform().getNamingContext().getNamingContext();
	}

	public String[] availableProfiles() {
		final IResourceFactoryRegistry registry = ResourceFactoryPlugin.getDefault().getResourceFactoryRegistry();
		final ResourceDesc[] resources = registry.getResources();
		final List<String> retVal = new ArrayList<String>(resources.length);
		for (final ResourceDesc desc : resources) {
			final String path = registry.getProfilePath(desc);
			retVal.add(path);
		}
		return retVal.toArray(new String[retVal.size()]);
	}

	public FileManager fileManager() {
		return this.localSca.getFileManager().getObj();
	}

	public ExtendedCF.ResourceDesc[] registeredResources() {
		final List<ExtendedCF.ResourceDesc> retVal = new ArrayList<ExtendedCF.ResourceDesc>(this.localSca.getSandboxWaveform()
		        .getComponents()
		        .size()
		        + this.localSca.getWaveforms().size() + this.localSca.getSandboxDeviceManager().getDevices().size());
		final IResourceFactoryRegistry registry = ResourceFactoryPlugin.getDefault().getResourceFactoryRegistry();

		for (final ScaComponent cp : this.localSca.getSandboxWaveform().getComponents()) {
			final ExtendedCF.ResourceDesc desc = new ExtendedCF.ResourceDesc();
			desc.profile = registry.getProfilePath(registry.getDescByID(cp.getProfileObj().getId()));
			desc.resource = cp.getObj();
			retVal.add(desc);
		}
		for (final ScaDevice< ? > cp : this.localSca.getSandboxDeviceManager().getAllDevices()) {
			final ExtendedCF.ResourceDesc desc = new ExtendedCF.ResourceDesc();
			desc.profile = registry.getProfilePath(registry.getDescByID(cp.getProfileObj().getId()));
			desc.resource = cp.getObj();
			retVal.add(desc);
		}
		for (final ScaWaveform cp : this.localSca.getWaveforms()) {
			final ExtendedCF.ResourceDesc desc = new ExtendedCF.ResourceDesc();
			desc.profile = registry.getProfilePath(registry.getDescByID(cp.getProfileObj().getId()));
			desc.resource = cp.getObj();
			retVal.add(desc);
		}
		return retVal.toArray(new ExtendedCF.ResourceDesc[retVal.size()]);
	}

	public ResourceFactory getResourceFactory(final String identifier) {
		final IResourceFactoryRegistry registry = ResourceFactoryPlugin.getDefault().getResourceFactoryRegistry();
		final ResourceDesc desc = registry.getDescByID(identifier);
		if (desc != null) {
			return desc.getFactory();
		} else {
			return null;
		}
	}

	public ResourceFactory getResourceFactoryByProfile(final String profile) {
		final IResourceFactoryRegistry registry = ResourceFactoryPlugin.getDefault().getResourceFactoryRegistry();
		final ResourceDesc desc = registry.getDescByProfile(profile);
		if (desc != null) {
			return desc.getFactory();
		} else {
			return null;
		}
	}

	public void refresh(final org.omg.CORBA.Object corbaObj, final Depth depth) {
		final CorbaObjWrapper< ? > wrapper = ScaModelCommandWithResult.execute(this.localSca, new ScaModelCommandWithResult<CorbaObjWrapper< ? >>() {

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
