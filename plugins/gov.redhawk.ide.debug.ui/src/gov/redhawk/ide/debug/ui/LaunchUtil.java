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
package gov.redhawk.ide.debug.ui;

import gov.redhawk.ide.debug.ILaunchConfigurationFactory;
import gov.redhawk.ide.debug.ILaunchConfigurationFactoryRegistry;
import gov.redhawk.ide.debug.ScaDebugLaunchConstants;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.model.sca.util.ModelUtil;
import gov.redhawk.sca.launch.ScaLaunchConfigurationConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.spd.provider.SpdItemProviderAdapterFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

/**
 * 
 */
public final class LaunchUtil {
	private LaunchUtil() {

	}

	public static ILaunchConfiguration chooseConfiguration(final String mode, final ILaunchConfiguration[] configs, final Shell shell) {
		if (configs == null || configs.length == 0) {
			return null;
		}
		if (configs.length == 1) {
			return configs[0];
		}
		final IDebugModelPresentation labelProvider = DebugUITools.newDebugModelPresentation();
		final ElementListSelectionDialog dialog = new ElementListSelectionDialog(shell, labelProvider);
		dialog.setElements(configs);
		dialog.setTitle("Select Configuration");
		if (mode != null) {
			dialog.setMessage("Select a launch configuration to " + mode + ":");
		} else {
			dialog.setMessage("Select launch configuration: ");
		}
		dialog.setMultipleSelection(false);
		final int result = dialog.open();
		labelProvider.dispose();
		if (result == Window.OK) {
			return (ILaunchConfiguration) dialog.getFirstResult();
		}
		return null;
	}

	public static Implementation chooseImplementation(final Collection<Implementation> impls, final String mode, final Shell shell) {
		Assert.isNotNull(impls);
		Assert.isNotNull(mode);
		Assert.isNotNull(shell);
		if (impls.size() == 1) {
			return impls.iterator().next();
		}
		final SpdItemProviderAdapterFactory adapterFactory = new SpdItemProviderAdapterFactory();
		final DecoratingLabelProvider labelProvider = new DecoratingLabelProvider(new AdapterFactoryLabelProvider(adapterFactory), PlatformUI.getWorkbench()
		        .getDecoratorManager()
		        .getLabelDecorator());
		final ElementListSelectionDialog dialog = new ElementListSelectionDialog(shell, labelProvider);
		dialog.setElements(impls.toArray());
		dialog.setTitle("Select Implementation");
		dialog.setMessage("Select an implementation to " + mode + ":");
		dialog.setMultipleSelection(false);
		final int result = dialog.open();
		labelProvider.dispose();
		adapterFactory.dispose();
		if (result == Window.OK) {
			return (Implementation) dialog.getFirstResult();
		}
		return null;
	}
	
	public static ILaunchConfigurationWorkingCopy createLaunchConfiguration(final SoftwareAssembly softwareAssembly, final Shell shell) throws CoreException {
		return LaunchUtil.createLaunchConfiguration(softwareAssembly);
	}

	private static ILaunchConfigurationWorkingCopy createLaunchConfiguration(final SoftwareAssembly softwareAssembly)
	        throws CoreException {
		final ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		final String launcherPrefix = softwareAssembly.getName();
		final String launchConfigName = launchManager.generateLaunchConfigurationName(launcherPrefix);
		final ILaunchConfigurationType configType = launchManager.getLaunchConfigurationType(ScaDebugLaunchConstants.ID_LOCAL_WAVEFORM_LAUNCH);
		final ILaunchConfigurationWorkingCopy retVal = configType.newInstance(null, launchConfigName);
		if (softwareAssembly.eResource().getURI().isPlatform()) {
			IFile sadFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(softwareAssembly.eResource().getURI().toPlatformString(true)));
			retVal.setMappedResources(new IResource[] {
				sadFile.getProject()
			});
			retVal.setAttribute(ScaLaunchConfigurationConstants.ATT_PROFILE, softwareAssembly.eResource().getURI().toPlatformString(true));
		} else {
			retVal.setAttribute(ScaLaunchConfigurationConstants.ATT_WORKSPACE, false);
			retVal.setAttribute(ScaLaunchConfigurationConstants.ATT_PROFILE, softwareAssembly.eResource().getURI().toString());
		}
		
		return retVal;
	}
	
	public static ILaunchConfiguration[] findLaunchConfigurations(final ILaunchConfiguration newConfig) throws CoreException {
		if (newConfig == null) {
			return null;
		}
		final ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		final ILaunchConfiguration[] launchers = launchManager.getLaunchConfigurations(newConfig.getType());
		final List<ILaunchConfiguration> retVal = new ArrayList<ILaunchConfiguration>(1);
		for (final ILaunchConfiguration config : launchers) {
			if (config.getAttribute(ScaLaunchConfigurationConstants.ATT_PROFILE, "").equals(newConfig.getAttribute(ScaLaunchConfigurationConstants.ATT_PROFILE, ""))) {
				retVal.add(config);
			}
		}
		if (retVal.isEmpty()) {
			return null;
		}
		return retVal.toArray(new ILaunchConfiguration[retVal.size()]);
	}
	
	public static ILaunchConfigurationWorkingCopy createLaunchConfiguration(final SoftPkg spd, Shell shell) throws CoreException {
		if (spd.getImplementation().isEmpty()) {
			return null;
		}
		if (spd.getImplementation().size() == 1) {
			final Implementation impl = spd.getImplementation().get(0);
			return createLaunchConfiguration(impl);
		} else {
			Implementation impl = chooseImplementation(spd.getImplementation(), ILaunchManager.RUN_MODE, shell);
			return createLaunchConfiguration(impl);
		}
	}
	
	public static ILaunchConfigurationWorkingCopy createLaunchConfiguration(final Implementation impl) throws CoreException {
		final SoftPkg spd = impl.getSoftPkg();
		ILaunchConfigurationFactoryRegistry registry = ScaDebugPlugin.getInstance().getLaunchConfigurationFactoryRegistry();
		ILaunchConfigurationFactory factory = registry.getFactory(spd, impl.getId());
		ILaunchConfigurationWorkingCopy config = factory.createLaunchConfiguration(spd.getName(), impl.getId(), spd);
		return config;
	}
	
	/**
	 * @deprecated Use {@link #createLaunchConfiguration(SoftPkg, Shell)} instead
	 * Doesn't save launch configuration 
	 * @param file
	 * @param mode
	 * @param shell
	 * @throws CoreException 
	 */
	@Deprecated
	public static void launch(IFile file, String mode, Shell shell) throws CoreException {
		if (!file.exists()) {
			return;
		}
		if (file.getName().endsWith(SpdPackage.FILE_EXTENSION)) {
			SoftPkg spd = ModelUtil.loadSoftPkg(URI.createURI(file.getLocationURI().toString()));
			ILaunchConfigurationWorkingCopy config = createLaunchConfiguration(spd, shell);
			launch(config, mode);
		}
	}
	
	public static void launch(final ILaunchConfiguration config, final String mode) {
		if (config == null || mode == null) {
			return;
		}
		final Job job = new Job("Launching " + config.getName()) {

			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				try {
					config.launch(mode, monitor, false);
				} catch (final CoreException e) {
					return e.getStatus();
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}
}
