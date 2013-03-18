/** 
 * REDHAWK HEADER
 *
 * Identification: $Revision: 9208 $
 */
package gov.redhawk.ide.debug.internal;

import gov.redhawk.ide.codegen.CodegenUtil;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.debug.ILaunchConfigurationFactory;
import gov.redhawk.ide.debug.ILaunchConfigurationFactoryRegistry;
import gov.redhawk.ide.debug.ScaDebugPlugin;

import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.spd.Implementation;
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

/**
 * 
 */
public enum LaunchConfigurationFactoryRegistry implements ILaunchConfigurationFactoryRegistry, IExtensionChangeHandler {
	INSTANCE;

	private static class LaunchConfigurationFactoryDesc {
		private final String id;
		private final String codegenref;
		private final String launchConfigType;
		private final ILaunchConfigurationFactory factory;

		public LaunchConfigurationFactoryDesc(final String id, final String codegenref, final String launchConfigType, final ILaunchConfigurationFactory factory) {
			super();
			this.id = id;
			this.codegenref = codegenref;
			this.launchConfigType = launchConfigType;
			this.factory = factory;
		}

		public String getCodegenRefID() {
			return this.codegenref;
		}

		public String getId() {
			return this.id;
		}

		public String getLaunchConfigType() {
			return this.launchConfigType;
		}

		public ILaunchConfigurationFactory getFactory() {
			return this.factory;
		}
	}

	public static final String EP_ID = "launchConfigurationFactories";
	private final List<LaunchConfigurationFactoryDesc> descriptors = new ArrayList<LaunchConfigurationFactoryDesc>();
	private final ExtensionTracker tracker;

	private LaunchConfigurationFactoryRegistry() {
		final IExtensionRegistry reg = Platform.getExtensionRegistry();

		final IExtensionPoint ep = reg.getExtensionPoint(ScaDebugPlugin.ID, LaunchConfigurationFactoryRegistry.EP_ID);

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

	public ILaunchConfigurationFactory getFactory(final SoftPkg spd, final String implID) {
		final Implementation impl = spd.getImplementation(implID);
		final ImplementationSettings settings = CodegenUtil.getImplementationSettings(impl);
		if (settings != null) {
			final String codegenID = settings.getGeneratorId();
			for (final LaunchConfigurationFactoryDesc desc : this.descriptors) {
				if (desc.codegenref != null && desc.codegenref.equals(codegenID)) {
					if (desc.factory.supports(spd, implID)) {
						return desc.factory;
					}
				} 
			}			
		} else {
			for (final LaunchConfigurationFactoryDesc desc : this.descriptors) {
				if (desc.codegenref == null) {
					if (desc.factory.supports(spd, implID)) {
						return desc.factory;
					}
				} 
			}
		}
		return null;
	}

	public void addExtension(final IExtensionTracker tracker, final IExtension extension) {
		for (final IConfigurationElement element : extension.getConfigurationElements()) {
			if (element.getName().equals("launchConfigurationFactory")) {
				LaunchConfigurationFactoryDesc descriptor;
				try {
					descriptor = createDescriptor(element);
					this.descriptors.add(descriptor);
					tracker.registerObject(extension, descriptor, IExtensionTracker.REF_SOFT);
				} catch (final CoreException e) {
					ScaDebugPlugin.getInstance().getLog().log(e.getStatus());
				}

			}
		}
	}

	private LaunchConfigurationFactoryDesc createDescriptor(final IConfigurationElement element) throws CoreException {
		final String id = element.getAttribute("id");
		final String codegenref = element.getAttribute("codegenref");
		final String launchConfigType = element.getAttribute("launchConfigType");
		final ILaunchConfigurationFactory factory = (ILaunchConfigurationFactory) element.createExecutableExtension("factory");
		return new LaunchConfigurationFactoryDesc(id, codegenref, launchConfigType, factory);
	}

	public void removeExtension(final IExtension extension, final Object[] objects) {
		for (final Object obj : objects) {
			if (obj instanceof LaunchConfigurationFactoryDesc) {
				this.descriptors.remove(obj);
			}
		}
	}

}
