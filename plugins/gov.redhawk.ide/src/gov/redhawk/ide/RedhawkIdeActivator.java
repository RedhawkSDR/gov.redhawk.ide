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
package gov.redhawk.ide;

import gov.redhawk.ide.preferences.RedhawkIdePreferenceConstants;
import gov.redhawk.sca.util.PluginUtil;
import gov.redhawk.sca.util.ScopedPreferenceAccessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.variables.VariablesPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 */
public class RedhawkIdeActivator extends Plugin {

	/** The plug-in ID. */
	public static final String PLUGIN_ID = "gov.redhawk.ide";

	// The shared instance
	/** The plugin. */
	private static RedhawkIdeActivator plugin;

	private final ScopedPreferenceAccessor preferenceAccessor = new ScopedPreferenceAccessor(InstanceScope.INSTANCE, RedhawkIdeActivator.PLUGIN_ID);

	/**
	 * The constructor.
	 */
	public RedhawkIdeActivator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		RedhawkIdeActivator.plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		RedhawkIdeActivator.plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return the shared instance
	 */
	public static RedhawkIdeActivator getDefault() {
		return RedhawkIdeActivator.plugin;
	}

	/**
	 * Gets the target platform runtime location (i.e. OSSIEHOME) from the REDHAWK IDE preferences. Variable
	 * expansion in the path occurs as follows:
	 * <ol>
	 * <li>Eclipse variables are expanded</li>
	 * <li>Environment variables are expanded</li>
	 * </ol>
	 *  
	 * @return The path of the target platform runtime location
	 */
	public IPath getRuntimePath() {
		String runtimePath = getRuntimePathWithEnv();
		if (runtimePath == null) {
			return null;
		}

		// Resolve remaining variable references using environment variables
		runtimePath = PluginUtil.replaceEnvIn(runtimePath, null);

		return new Path(runtimePath);
	}

	/**
	 * Same as {@link #getRuntimePath()}, but environment variables are NOT expanded. This is useful for situations
	 * where the environment variable needs to be expanded later.
	 * 
	 * @return The path of the target platform runtime location
	 * @since 5.0
	 */
	public String getRuntimePathWithEnv() {
		String runtimePath = this.preferenceAccessor.getString(RedhawkIdePreferenceConstants.RH_IDE_RUNTIME_PATH_PREFERENCE).trim();
		if ("".equals(runtimePath)) {
			return null;
		}

		// Let Eclipse perform any variable substitution it can
		try {
			runtimePath = VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(runtimePath, false);
		} catch (final CoreException e) {
			// This shouldn't happen ever (we ask for no error reports above)
			logError("Unexpected error while resolving variables in runtime path (" + runtimePath + ")", e);
			return null;
		}

		return runtimePath;
	}

	/**
	 * Gets the path to the include files for the target platform's runtime. Uses a path relative to
	 * {@link #getRuntimePath()}.
	 * 
	 * @return The path of the target platform runtime location's include files
	 * @since 4.0
	 */
	public IPath getRuntimeIncludePath() {
		final IPath path = getRuntimePath();
		if (path == null) {
			return null;
		}
		path.append("/include/ossie");
		return path;
	}

	/**
	 * Get the IDL include paths from IDE preferences. Variable substitution in the paths occurs as follows:
	 * <p />
	 * <ol>
	 * <li>Eclipse variables are substituted</li>
	 * <li>References to the OSSIEHOME variable are substituted with the value of {@link #getRuntimePath()}</li>
	 * <li>Environment variables are substituted</li>
	 * </ol>
	 * 
	 * @return The IDL include paths
	 */
	public IPath[] getDefaultIdlIncludePath() {
		final String prefValue = this.preferenceAccessor.getString(RedhawkIdePreferenceConstants.RH_IDE_IDL_INCLUDE_PATH_PREFERENCE);
		final String[] values = prefValue.split(this.preferenceAccessor.getString(RedhawkIdePreferenceConstants.RH_IDE_IDL_INCLUDE_PATH_PREFERENCE_DELIMITER));
		final ArrayList<IPath> retVal = new ArrayList<IPath>();

		final Map<String, String> override = new HashMap<String, String>();
		override.put("OSSIEHOME", getRuntimePath().toString());
		for (int i = 0; i < values.length; i++) {
			if ((values[i] == null) || (values[i].trim().length() == 0)) {
				continue;
			}

			// Let Eclipse perform any variable substitution it can
			try {
				values[i] = VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(values[i], false);
			} catch (final CoreException e) {
				// This shouldn't happen ever (we ask for no error reports above)
				logError("Unexpected error while resolving variables in preference node path (" + values[i] + ")", e);
				continue;
			}

			// Resolve remaining variable references using environment variables (special case for OSSIEHOME)
			values[i] = PluginUtil.replaceEnvIn(values[i], override);

			retVal.add(new Path(values[i]));
		}
		return retVal.toArray(new IPath[retVal.size()]);
	}

	/**
	 * Logging functionality
	 * 
	 * @param msg The message to be logged
	 * @param e The associated exception, if any
	 * @since 2.1
	 */
	public static final void logError(final String msg, final Throwable e) {
		RedhawkIdeActivator.getDefault().getLog().log(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, msg, e));
	}

	/**
	 * @since 3.0
	 */
	public ScopedPreferenceAccessor getPreferenceAccessor() {
		return this.preferenceAccessor;
	}
}
