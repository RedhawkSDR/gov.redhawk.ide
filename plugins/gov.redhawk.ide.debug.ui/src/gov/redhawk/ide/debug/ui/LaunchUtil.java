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

import gov.redhawk.ide.debug.LocalComponentProgramLaunchDelegate;
import gov.redhawk.ide.debug.ScaDebugLaunchConstants;
import gov.redhawk.sca.launch.ScaLaunchConfigurationConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.provider.SpdItemProviderAdapterFactory;

import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.IEvaluationContext;
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
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationManager;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchGroupExtension;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchShortcutExtension;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.activities.WorkbenchActivityHelper;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

/**
 * 
 */
public final class LaunchUtil {
	private LaunchUtil() {

	}

	public static void launch(final IFile spdFile, final String mode, final Shell shell) {
		String launchGroupIdentifier;
		if (mode.equals(ILaunchManager.RUN_MODE)) {
			launchGroupIdentifier = IDebugUIConstants.ID_RUN_LAUNCH_GROUP;
		} else if (mode.equals(ILaunchManager.DEBUG_MODE)) {
			launchGroupIdentifier = IDebugUIConstants.ID_DEBUG_LAUNCH_GROUP;
		} else {
			throw new IllegalStateException("Unknown run mode: " + mode);
		}
		final LaunchGroupExtension fGroup = DebugUIPlugin.getDefault().getLaunchConfigurationManager().getLaunchGroup(launchGroupIdentifier);
		final IEvaluationContext context = LaunchUtil.createContext(spdFile);
		final List< ? > allShortCuts = LaunchUtil.getLaunchConfigurationManager().getLaunchShortcuts(fGroup.getCategory());
		final Iterator< ? > iter = allShortCuts.iterator();
		final List<LaunchShortcutExtension> filteredShortCuts = new ArrayList<LaunchShortcutExtension>(10);
		while (iter.hasNext()) {
			final LaunchShortcutExtension ext = (LaunchShortcutExtension) iter.next();
			try {
				if (!WorkbenchActivityHelper.filterItem(ext) && LaunchUtil.isApplicable(ext, context) && LaunchUtil.supportsMode(ext, mode)) {
					filteredShortCuts.add(ext);
				}
			} catch (final CoreException e) {
				//PASS 
				/*not supported*/
			}
		}
		if (filteredShortCuts.isEmpty()) {
			MessageDialog.openError(shell, "No Launch Configurations", "Unable to locate a matching launch configuration, launch aborted.");
			return;
		} else if (filteredShortCuts.size() == 1) {
			LaunchUtil.launch(filteredShortCuts.get(0), spdFile, mode);
		} else {
			final ImageRegistry registry = new ImageRegistry();
			for (final LaunchShortcutExtension ext : filteredShortCuts) {
				registry.put(ext.getId(), ext.getImageDescriptor());
			}
			final ElementListSelectionDialog dialog = new ElementListSelectionDialog(shell, new LabelProvider() {

				@Override
				public String getText(final Object element) {
					return ((LaunchShortcutExtension) element).getContextLabel(mode);
				}

				@Override
				public Image getImage(final Object element) {
					return registry.get(((LaunchShortcutExtension) element).getId());
				}
			});
			dialog.setElements(filteredShortCuts.toArray());
			dialog.setTitle("Select Launch Type");
			final String label = mode.toUpperCase();
			dialog.setMessage(label + " as:");
			dialog.setMultipleSelection(false);
			final int result = dialog.open();
			registry.dispose();
			if (result == Window.OK) {
				LaunchUtil.launch((LaunchShortcutExtension) dialog.getFirstResult(), spdFile, mode);
			}
		}

	}

	private static boolean supportsMode(final LaunchShortcutExtension ext, final String mode) {
		return ext.getModes().contains(mode);
	}

	private static void launch(final LaunchShortcutExtension launchShortcutExtension, final IFile spd, final String mode) {
		launchShortcutExtension.launch(new StructuredSelection(spd), mode);
	}

	private static boolean isApplicable(final LaunchShortcutExtension ext, final IEvaluationContext context) throws CoreException {
		final Expression expr = ext.getContextualLaunchEnablementExpression();
		return ext.evalEnablementExpression(context, expr);
	}

	private static LaunchConfigurationManager getLaunchConfigurationManager() {
		return DebugUIPlugin.getDefault().getLaunchConfigurationManager();
	}

	private static IEvaluationContext createContext(final IFile file) {
		final List< ? > list = Collections.singletonList(file);
		final IEvaluationContext context = new EvaluationContext(null, list);
		context.setAllowPluginActivation(true);
		context.addVariable("selection", list); //$NON-NLS-1$
		return context;
	}

	public static ILaunchConfiguration chooseConfiguration(final String mode, final ILaunchConfiguration[] configs, final Shell shell) {
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
		final ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();
		final ILaunchConfigurationType configType = lm.getLaunchConfigurationType(ScaDebugLaunchConstants.ID_LOCAL_WAVEFORM_LAUNCH);
		final ILaunchConfiguration[] configs = LaunchUtil.findLaunchConfigurations(softwareAssembly, configType);
		ILaunchConfigurationWorkingCopy config;
		if (configs == null || configs.length == 0) {
			config = LaunchUtil.createLaunchConfiguration(softwareAssembly);
		} else if (configs.length == 1) {
			config = configs[0].getWorkingCopy();
		} else {
			final ILaunchConfiguration result = LaunchUtil.chooseConfiguration(null, configs, shell);
			if (result != null) {
				config = result.getWorkingCopy();
			} else {
				return null;
			}
		}
		return config;
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

	private static ILaunchConfiguration[] findLaunchConfigurations(final SoftwareAssembly sad, final ILaunchConfigurationType configType) throws CoreException {
		if (!sad.eResource().getURI().isPlatform()) {
			return null;
		}
		final ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		final ILaunchConfiguration[] launchers = launchManager.getLaunchConfigurations(configType);
		final List<ILaunchConfiguration> retVal = new ArrayList<ILaunchConfiguration>(1);
		for (final ILaunchConfiguration config : launchers) {
			if (config.getAttribute(ScaLaunchConfigurationConstants.ATT_PROFILE, "").equals(sad.eResource().getURI().toPlatformString(true))) {
				retVal.add(config);
			}
		}
		if (retVal.isEmpty()) {
			return null;
		}
		return retVal.toArray(new ILaunchConfiguration[retVal.size()]);
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
//		final String instId = DceUuidUtil.createDceUUID() + ":" + spd.getId();
		final URI uri = impl.eResource().getURI();
		final ILaunchConfigurationWorkingCopy config = LocalComponentProgramLaunchDelegate.createLaunchConfiguration(spd.getName(),
		        null,
		        impl.getId(),
		        null,
		        uri);
		return config;
	}
	
	public static void launch(final ILaunchConfiguration config, final String mode) {
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
