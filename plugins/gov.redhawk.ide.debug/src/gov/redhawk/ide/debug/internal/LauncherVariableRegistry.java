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

import gov.redhawk.ide.debug.ILauncherVariableDesc;
import gov.redhawk.ide.debug.ILauncherVariableRegistry;
import gov.redhawk.ide.debug.ILauncherVariableResolver;
import gov.redhawk.ide.debug.ScaDebugPlugin;

import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.dynamichelpers.ExtensionTracker;
import org.eclipse.core.runtime.dynamichelpers.IExtensionChangeHandler;
import org.eclipse.core.runtime.dynamichelpers.IExtensionTracker;
import org.eclipse.core.runtime.dynamichelpers.IFilter;
import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

/**
 * 
 */
public enum LauncherVariableRegistry implements ILauncherVariableRegistry, IExtensionChangeHandler {
	INSTANCE;

	private static class VariableDesc implements ILauncherVariableDesc {

		private final ILauncherVariableResolver resolver;
		private final String name;
		private boolean prependName;
		private final String description;

		public VariableDesc(final String name, final ILauncherVariableResolver resolver, final String description, final boolean prependName) {
			this.name = name;
			this.resolver = resolver;
			this.description = description;
			this.prependName = prependName;
		}
		
		@Override
		public boolean prependName() {
		    return this.prependName;
		}

		public String getName() {
			return this.name;
		}

		public String resolveValue(String arg, final SoftPkg spd, final ILaunch launch, final ILaunchConfiguration config) throws CoreException {
			return this.resolver.resolveValue(arg, spd, launch, config);
		}

		public String getDescription() {
			return this.description;
		}

		public String resolveValue(IDynamicVariable variable, String argument) throws CoreException {
	        return this.resolver.resolveValue(variable, argument);
        }
	}

	private final List<VariableDesc> descriptors = new ArrayList<VariableDesc>();
	private final ExtensionTracker tracker;

	private LauncherVariableRegistry() {
		final IExtensionRegistry reg = Platform.getExtensionRegistry();

		final IExtensionPoint ep = reg.getExtensionPoint("org.eclipse.core.variables.dynamicVariables");

		this.tracker = new ExtensionTracker(reg);

		if (ep != null) {
			final IFilter filter = ExtensionTracker.createExtensionPointFilter(ep);
			this.tracker.registerHandler(this, filter);
			final IExtension[] extensions = ep.getExtensions();
			for (final IExtension extension : extensions) {
				addExtension(this.tracker, extension);
			}
		}
	}

	public ILauncherVariableDesc[] getDescriptors() {
		return this.descriptors.toArray(new ILauncherVariableDesc[this.descriptors.size()]);
	}

	public ILauncherVariableDesc getDesc(final String name) {
		for (final VariableDesc desc : this.descriptors) {
			if (name.equals(desc.name)) {
				return desc;
			}
		}
		return null;
	}

	public void addExtension(final IExtensionTracker tracker, final IExtension extension) {
		for (final IConfigurationElement element : extension.getConfigurationElements()) {
			if (element.getName().equals("variable")) {
				VariableDesc descriptor;
				try {
					descriptor = createDescriptor(element);
					if (descriptor != null) {
						this.descriptors.add(descriptor);
						tracker.registerObject(extension, descriptor, IExtensionTracker.REF_SOFT);
					}
				} catch (final CoreException e) {
					ScaDebugPlugin.getInstance().getLog().log(e.getStatus());
				}
			}
		}
	}

	private VariableDesc createDescriptor(final IConfigurationElement element) throws CoreException {
		final String name = element.getAttribute("name");
		final String description = element.getAttribute("description");
		boolean prependName = !"false".equalsIgnoreCase(element.getAttribute("prependName"));
		Object obj = element.createExecutableExtension("resolver");
		if (obj instanceof ILauncherVariableResolver) {
			final ILauncherVariableResolver resolver = (ILauncherVariableResolver) obj; 
			final VariableDesc desc = new VariableDesc(name, resolver, description, prependName);
			return desc;
		}
		return null;
	}

	public void removeExtension(final IExtension extension, final Object[] objects) {
		for (final Object obj : objects) {
			if (obj instanceof VariableDesc) {
				this.descriptors.remove(obj);
			}
		}
	}

}
