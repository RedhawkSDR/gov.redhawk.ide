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
import gov.redhawk.ide.debug.ScaDebugLaunchConstants;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.sca.launch.ScaLaunchConfigurationConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.provider.SpdItemProviderAdapterFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.workspace.util.WorkspaceSynchronizer;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * 
 */
public final class LaunchUtil {
	private LaunchUtil() {

	}

	/**
	 * @since 1.1
	 */
	public static void launch(final SoftPkg spd, final String mode, final Shell shell) throws CoreException {
		final EList<Implementation> impls = spd.getImplementation();
		Implementation impl;
		if (impls.size() == 1) {
			impl = impls.get(0);
		} else {
			impl = LaunchUtil.chooseImplementation(spd.getImplementation(), mode, shell);
		}
		if (impl == null) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugUiPlugin.PLUGIN_ID, "Failed to find implementation.", null));
		}
		final ILaunchConfigurationFactory factory = ScaDebugPlugin.getInstance().getLaunchConfigurationFactoryRegistry().getFactory(spd, impl.getId());
		final ILaunchConfigurationWorkingCopy newConfig = factory.createLaunchConfiguration(null, impl.getId(), spd);
		LaunchUtil.launchMerge(spd, newConfig, mode, shell);
	}

	private static void launchMerge(final EObject obj, final ILaunchConfigurationWorkingCopy newConfig, final String mode, final Shell shell)
	        throws CoreException {
		final ILaunchConfiguration[] configs = LaunchUtil.findLaunchConfigurations(obj, newConfig.getType());
		final ILaunchConfigurationWorkingCopy config;
		if (configs == null) {
			config = newConfig;
		} else if (configs.length == 1) {
			config = configs[0].getWorkingCopy();
			config.setAttributes(newConfig.getAttributes());
		} else {
			final ILaunchConfiguration result = LaunchUtil.chooseConfiguration(mode, configs, shell);
			if (result != null) {
				config = result.getWorkingCopy();
				config.setAttributes(newConfig.getAttributes());
			} else {
				return;
			}
		}
		DebugUITools.launch(config.doSave(), mode);
	}

	public static ILaunchConfiguration chooseConfiguration(final String mode, final ILaunchConfiguration[] configs, final Shell shell) {
		final IDebugModelPresentation labelProvider = DebugUITools.newDebugModelPresentation();
		final ElementListSelectionDialog dialog = new ElementListSelectionDialog(shell, labelProvider);
		dialog.setElements(configs);
		dialog.setTitle("Select Configuration");
		dialog.setMessage("Select a launch configuration to " + mode + ":");
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

	/**
	 * @since 1.1
	 */
	public static void launch(final SoftwareAssembly softwareAssembly, final String mode, final Shell shell) throws CoreException {
		final ILaunchConfigurationWorkingCopy newConfig = LaunchUtil.createLaunchConfiguration(mode, softwareAssembly);
		LaunchUtil.launchMerge(softwareAssembly, newConfig, mode, shell);
	}

	private static ILaunchConfigurationWorkingCopy createLaunchConfiguration(final String mode, final SoftwareAssembly softwareAssembly) throws CoreException {
		final ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		final String launcherPrefix = softwareAssembly.getName();
		final String launchConfigName = launchManager.generateLaunchConfigurationName(launcherPrefix);
		final ILaunchConfigurationType configType = launchManager.getLaunchConfigurationType(ScaDebugLaunchConstants.ID_LOCAL_WAVEFORM_LAUNCH);
		final ILaunchConfigurationWorkingCopy retVal = configType.newInstance(null, launchConfigName);
		retVal.setMappedResources(new IResource[] {
			WorkspaceSynchronizer.getUnderlyingFile(softwareAssembly.eResource()).getProject()
		});

		retVal.setAttribute(ScaLaunchConfigurationConstants.ATT_PROFILE, softwareAssembly.eResource().getURI().toPlatformString(true));

		return retVal;
	}

	private static ILaunchConfiguration[] findLaunchConfigurations(final EObject obj, final ILaunchConfigurationType configType) throws CoreException {
		final ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		final ILaunchConfiguration[] launchers = launchManager.getLaunchConfigurations(configType);
		final List<ILaunchConfiguration> retVal = new ArrayList<ILaunchConfiguration>(1);
		for (final ILaunchConfiguration config : launchers) {
			if (config.getAttribute(ScaLaunchConfigurationConstants.ATT_PROFILE, "").equals(obj.eResource().getURI().toPlatformString(true))) {
				retVal.add(config);
			}
		}
		if (retVal.isEmpty()) {
			return null;
		}
		return retVal.toArray(new ILaunchConfiguration[retVal.size()]);
	}

	/**
	 * @deprecated Use {@link #launch(SoftwareAssembly, String, Shell)} instead.
	 */
	@Deprecated
	public static void launch(final SoftwareAssembly softwareAssembly, final IFile sadFile, final String mode, final Shell shell) throws CoreException {
		LaunchUtil.launch(softwareAssembly, mode, shell);
	}

	/**
	 * @deprecated Use {@link #launch(SoftPkg, String, Shell)} instead.
	 */
	@Deprecated
	public static void launch(final IFile spdFile, final String mode, final Shell shell) {
		final ResourceSet resourceSet = new ResourceSetImpl();
		final Resource spdResource = resourceSet.getResource(URI.createPlatformResourceURI(spdFile.getFullPath().toString(), true), true);
		try {
			LaunchUtil.launch(SoftPkg.Util.getSoftPkg(spdResource), mode, shell);
		} catch (final CoreException e) {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, ScaDebugUiPlugin.PLUGIN_ID, e.getStatus().getMessage(), e.getStatus().getException()),
			        StatusManager.LOG | StatusManager.SHOW);
		}
	}
}
