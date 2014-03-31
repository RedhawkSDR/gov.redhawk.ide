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
package gov.redhawk.ide.internal.ui.templates.handlers;

import gov.redhawk.ide.internal.ui.templates.ResourceControlPanelTemplateSection;
import gov.redhawk.ide.internal.ui.templates.ResourceControlPanelWizard;
import gov.redhawk.ide.internal.ui.templates.ScaTemplateSection;
import gov.redhawk.ide.ui.templates.TemplatesActivator;
import gov.redhawk.model.sca.util.ModelUtil;
import gov.redhawk.ui.editor.SCAFormEditor;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.dcd.util.DcdResourceImpl;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.sad.util.SadResourceImpl;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.util.SpdResourceImpl;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.environments.IExecutionEnvironment;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.pde.internal.core.ICoreConstants;
import org.eclipse.pde.internal.core.util.VMUtil;
import org.eclipse.pde.internal.ui.wizards.IProjectProvider;
import org.eclipse.pde.internal.ui.wizards.plugin.NewProjectCreationOperation;
import org.eclipse.pde.internal.ui.wizards.plugin.PluginFieldData;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * 
 */
public class NewControlPanelProject extends AbstractHandler {

	/*
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(HandlerUtil.getActiveShell(event));

		final IEditorPart editor = HandlerUtil.getActiveEditor(event);
		EObject eObj = null;
		String name = null;
		if (editor != null) {
			if (editor instanceof SCAFormEditor) {
				SCAFormEditor scaEditor = (SCAFormEditor) editor;
				Resource resource = scaEditor.getMainResource();
				if (resource instanceof SpdResourceImpl) {
					SoftPkg spd = SoftPkg.Util.getSoftPkg(resource);
					name = spd.getName();
					eObj = spd;
				} else if (resource instanceof SadResourceImpl) {
					SoftwareAssembly sad = SoftwareAssembly.Util.getSoftwareAssembly(resource);
					name = sad.getName();
					eObj = sad;
				} else if (resource instanceof DcdResourceImpl) {
					DeviceConfiguration dcd = DeviceConfiguration.Util.getDeviceConfiguration(resource);
					name = dcd.getName();
					eObj = dcd;
				}
			}
		}
		if (eObj == null) {
			return null;
		}
		IProject spdProject = ModelUtil.getProject(eObj);

		String baseName = null;
		if (spdProject != null) {
			baseName = spdProject.getName();
		} else {
			if (name != null) {
				baseName = ScaTemplateSection.makeNameSafe(name);
			}
		}
		String tmpProjectName = baseName + ".ui";
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(tmpProjectName);
		if (project.exists()) {
			InputDialog inputDialog = new InputDialog(HandlerUtil.getActiveShell(event), "Name Conflict", "Enter a name for the project:", baseName + "2.ui",
				new IInputValidator() {

					@Override
					public String isValid(String newText) {
						if (newText.trim().isEmpty()) {
							return "Must enter a value.";
						} else if (ResourcesPlugin.getWorkspace().getRoot().getProject(newText).exists()) {
							return "Project '" + newText + "' already exists.";
						}
						return null;
					}
				});
			if (inputDialog.open() == Window.OK) {
				tmpProjectName = inputDialog.getValue();
			} else {
				return null;
			}
		}

		final String projectName = tmpProjectName;
		final String pluginName = name;

		ResourceControlPanelWizard contentWizard = new ResourceControlPanelWizard();
		contentWizard.setResource(eObj);
		ResourceControlPanelTemplateSection resourceTemplate = contentWizard.getResourceControlPanelTemplateSection();
		String packageName = ScaTemplateSection.makeNameSafe(projectName).toLowerCase(Locale.ENGLISH);

		PluginFieldData fPluginData = new PluginFieldData();
		updateData(fPluginData, pluginName, projectName, packageName);
		IProjectProvider fProjectProvider = new IProjectProvider() {

			@Override
			public String getProjectName() {
				return projectName;
			}

			@Override
			public IProject getProject() {
				return ResourcesPlugin.getWorkspace().getRoot().getProject(getProjectName());
			}

			@Override
			public IPath getLocationPath() {
				// return null for in workspace
				return null;
			}

		};
		try {
			dialog.run(false, true, new NewProjectCreationOperation(fPluginData, fProjectProvider, contentWizard));
			final IFile file = project.getFile(new Path("src/" + packageName.replace(".", "/") + "/" + resourceTemplate.getCompositeClassName() + ".java"));
			final IWorkbenchWindow ww = HandlerUtil.getActiveWorkbenchWindow(event);
			final IWorkbenchPage page = ww.getActivePage();
			IWorkbenchPart focusPart = page.getActivePart();
			if (focusPart instanceof ISetSelectionTarget) {
				ISelection selection = new StructuredSelection(file);
				((ISetSelectionTarget) focusPart).selectReveal(selection);
			}
			try {
				try {
					file.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
				} catch (CoreException e) {
					StatusManager.getManager().handle(e, TemplatesActivator.getPluginId());
				}
				IDE.openEditor(page, file, true);
			} catch (PartInitException e) {
				StatusManager.getManager().handle(new Status(IStatus.ERROR, TemplatesActivator.getPluginId(), "Failed to open Control Panel Project editor", e));
			}
		} catch (InvocationTargetException e) {
			StatusManager.getManager().handle(
				new Status(IStatus.ERROR, TemplatesActivator.getPluginId(), "Failed to generate Control Panel Project.", e.getCause()));
		} catch (InterruptedException e) {
			// PASS
		}
		return null;
	}

	/**
	 * @param fPluginData
	 */
	protected void updateData(PluginFieldData fData, String pluginName, String projectName, String packageName) {
		fData.setSimple(false);
		fData.setSourceFolderName("src");
		fData.setOutputFolderName("bin");
		fData.setLegacy(false);

		// No project structure changes since 3.5, mark as latest version (though using any constant 3.5 or greater is
		// equivalent)
		fData.setTargetVersion(ICoreConstants.TARGET_VERSION_LATEST);

		// No longer support 3.0 non-osgi bundles in wizard
		fData.setHasBundleStructure(true);
		fData.setOSGiFramework(null);
		fData.setWorkingSets(new IWorkingSet[0]);

		fData.setId(projectName);
		fData.setVersion("1.0.0.qualifier");
		fData.setName(pluginName);
		fData.setProvider("");

		PluginFieldData data = fData;
		data.setClassname(packageName + ".Activator");
		data.setUIPlugin(true);
		data.setDoGenerateClass(true);
		data.setRCPApplicationPlugin(false);
		// Don't turn on API analysis if disabled (no java project available)
		data.setEnableAPITooling(false);
		fData.setExecutionEnvironment(getDefaultExecutionEnvirornment());
	}

	/**
	 * 
	 */
	private String getDefaultExecutionEnvirornment() {
		// Gather EEs
		IExecutionEnvironment[] exeEnvs = VMUtil.getExecutionEnvironments();

		// Set default EE based on strict match to default VM
		IVMInstall defaultVM = JavaRuntime.getDefaultVMInstall();

		for (int i = 0; i < exeEnvs.length; i++) {
			if (VMUtil.getExecutionEnvironment(exeEnvs[i].getId()).isStrictlyCompatible(defaultVM)) {
				return exeEnvs[i].getId();
			}
		}

		return null;
	}
}
