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
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.SpdResourceFactory;

import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.dynamichelpers.ExtensionTracker;
import org.eclipse.core.runtime.dynamichelpers.IExtensionChangeHandler;
import org.eclipse.core.runtime.dynamichelpers.IExtensionTracker;
import org.eclipse.core.runtime.dynamichelpers.IFilter;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

/**
 * 
 */
public class BundleResourceFactoryProvider extends AbstractResourceFactoryProvider implements IExtensionChangeHandler {

	private static class FactoryDescEntry {
		private String bundle;
		private String path;
		private ResourceDesc desc;

		public FactoryDescEntry(String bundle, String path) {
			this.bundle = bundle;
			this.path = path;
		}
	}

	public static final String EP_ID = "spdBundle";
	private static final String BUNDLE_CATEGORY = "Bundle";
	private final List<FactoryDescEntry> spdBundleFactories = new ArrayList<FactoryDescEntry>();
	private final ExtensionTracker tracker;

	/**
	 * 
	 */
	public BundleResourceFactoryProvider() {
		final IExtensionRegistry reg = Platform.getExtensionRegistry();

		final IExtensionPoint ep = reg.getExtensionPoint(ScaDebugPlugin.ID, EP_ID);

		this.tracker = new ExtensionTracker(reg);

		if (ep != null) {
			final IFilter filter = ExtensionTracker.createExtensionPointFilter(ep);
			this.tracker.registerHandler(this, filter);
			final IExtension[] extensions = ep.getExtensions();
			for (final IExtension extension : extensions) {
				addExtension(this.tracker, extension);
			}
		}
		
		for (FactoryDescEntry entry : spdBundleFactories) {
			ResourceDesc desc;
			try {
				desc = createDesc(entry);
				if (desc == null) {
					addResourceDesc(desc);
				} else {
					ScaDebugPlugin.logError("Failed to add Factory Descriptor entry: " + entry.bundle + ", " + entry.path, null);
				}
			} catch (ServantNotActive e) {
				ScaDebugPlugin.logError("Failed to add Factory Descriptor entry: " + entry.bundle + ", " + entry.path, e);
			} catch (WrongPolicy e) {
				ScaDebugPlugin.logError("Failed to add Factory Descriptor entry: " + entry.bundle + ", " + entry.path, e);
			} catch (Exception e) {
				ScaDebugPlugin.logError("Failed to add Factory Descriptor entry: " + entry.bundle + ", " + entry.path, e);
			}
		}
	}

	private ResourceDesc createDesc(FactoryDescEntry entry) throws ServantNotActive, WrongPolicy {
		final String profilePathStr = entry.path;
		final String bundle = entry.bundle;
		ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		Resource spdResource = resourceSet.getResource(URI.createPlatformPluginURI(bundle + "/" + profilePathStr, true), true);
		SoftPkg spd = SoftPkg.Util.getSoftPkg(spdResource);
		if (spd == null) {
			return null;
		}
		entry.desc = new ComponentDesc(spd, new SpdResourceFactory(spd));
		entry.desc.setCategory(BUNDLE_CATEGORY);
		return entry.desc;
	}

	/* (non-Javadoc)
	 * @see gov.redhawk.core.resourcefactory.IResourceFactoryProvider#dispose()
	 */
	public void dispose() {
		for (FactoryDescEntry entry : this.spdBundleFactories) {
			removeResourceDesc(entry.desc);
		}
		this.spdBundleFactories.clear();
	}

	public void addExtension(IExtensionTracker tracker, IExtension extension) {
		for (final IConfigurationElement element : extension.getConfigurationElements()) {
			FactoryDescEntry descriptor;
			try {
				descriptor = createDescriptor(element);
				this.spdBundleFactories.add(descriptor);
				tracker.registerObject(extension, descriptor, IExtensionTracker.REF_SOFT);
			} catch (Exception e) {
				ScaDebugPlugin.getInstance().getLog().log(new Status(Status.ERROR, ScaDebugPlugin.ID, "Failed to create resource factory desc.", e));
			}

		}
	}

	private FactoryDescEntry createDescriptor(IConfigurationElement element) throws Exception {
		String path = element.getAttribute("path");
		FactoryDescEntry entry = new FactoryDescEntry(element.getContributor().getName(), path);
		return entry;
	}

	public void removeExtension(IExtension extension, Object[] objects) {
		for (Object obj : objects) {
			if (obj instanceof FactoryDescEntry) {
				FactoryDescEntry desc = (FactoryDescEntry) obj;
				if (desc.desc != null) {
					removeResourceDesc(desc.desc);
				}
				this.spdBundleFactories.remove(desc);
			}
		}

	}

}
