package gov.redhawk.ide.graphiti.dcd.ui.runtime.tests;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class DcdGraphitiRuntimeTestsActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "gov.redhawk.ide.graphiti.dcd.ui.runtime.tests"; //$NON-NLS-1$

	// The shared instance
	private static DcdGraphitiRuntimeTestsActivator plugin;
	
	/**
	 * The constructor
	 */
	public DcdGraphitiRuntimeTestsActivator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static DcdGraphitiRuntimeTestsActivator getDefault() {
		return plugin;
	}

}
