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
package gov.redhawk.ide.spd.ui.wizard;

import gov.redhawk.ide.codegen.CodegenFactory;
import gov.redhawk.ide.codegen.CodegenUtil;
import gov.redhawk.ide.codegen.ICodeGeneratorDescriptor;
import gov.redhawk.ide.codegen.ITemplateDesc;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.RedhawkCodegenActivator;
import gov.redhawk.ide.codegen.WaveDevSettings;
import gov.redhawk.ide.codegen.ui.ICodegenWizardPage;
import gov.redhawk.ide.codegen.ui.RedhawkCodegenUiActivator;
import gov.redhawk.ide.codegen.util.ImplementationAndSettings;
import gov.redhawk.ide.spd.ui.ComponentUiPlugin;
import gov.redhawk.model.sca.util.ModelUtil;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;

/**
 * The Class RecreateWavedevWizard.
 * 
 * @since 7.0
 */
public class RecreateSettingsWizard extends NewScaResourceWizard {

	private WaveDevSettings waveSettings;

	/**
	 * 
	 */
	public RecreateSettingsWizard(final SoftPkg spdFile) {
		super(ICodeGeneratorDescriptor.COMPONENT_TYPE_RESOURCE);
		this.setWindowTitle("Recreate Settings Wizard");
		this.setNeedsProgressMonitor(true);
		setSoftPkg(spdFile);
		this.waveSettings = null;
	}

	@Override
	public void init(final IWorkbench workbench, final IStructuredSelection selection) {
		setFirstImplPage(0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performFinish() {
		updateEntryPoints();

		// Create the SCA component project
		final WorkspaceJob job = new WorkspaceJob("Creating New Project") {

			@Override
			public IStatus runInWorkspace(final IProgressMonitor monitor) throws CoreException {
				try {
					final SubMonitor progress = SubMonitor.convert(monitor, 1);

					// Create the settings
					final SoftPkg softPkg = getSoftPkg();
					final IProject project = ModelUtil.getProject(softPkg);
					// Create the URI to the .wavedev file
					final URI uri = URI.createPlatformResourceURI(project.getName() + "/." + softPkg.getName() + ".wavedev", false);
					final ResourceSet set = ScaResourceFactoryUtil.createResourceSet();
					final Resource res = set.createResource(uri);

					// Add the WaveDevSettings to the resource and save to disk to persist the newly created WaveDevSettings
					res.getContents().add(RecreateSettingsWizard.this.waveSettings);
					try {
						res.save(null);
					} catch (final IOException e) {
						return new Status(IStatus.ERROR, ComponentUiPlugin.PLUGIN_ID, "Error saving new settings file");
					}
					progress.worked(1);

				} finally {
					if (monitor != null) {
						monitor.done();
					}
				}
				return Status.OK_STATUS;
			}
		};

		final Thread jobThread = new Thread();
		job.setThread(jobThread);
		job.setUser(true);
		job.schedule();

		try {
			job.join();
		} catch (final InterruptedException e) {
			return true;
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void addPages() {
		try {
			final Field field = Wizard.class.getDeclaredField("pages");
			field.getModifiers();
			if (!Modifier.isPublic(field.getModifiers())) {
				field.setAccessible(true);
			}
			setWizPages((List<IWizardPage>) field.get(this));
		} catch (final SecurityException e1) {
			// PASS
		} catch (final NoSuchFieldException e1) {
			// PASS
		} catch (final IllegalArgumentException e) {
			// PASS
		} catch (final IllegalAccessException e) {
			// PASS
		}
		importSelected(getSoftPkg().eResource().getURI());
	}

	public void importSelected(final URI fileURI) {
		final SoftPkg softPkg = getSoftPkg();

		try {
			this.waveSettings = CodegenUtil.getWaveDevSettings(CodegenUtil.getWaveDevSettingsURI(fileURI));
		} catch (final Exception e) { // SUPPRESS CHECKSTYLE Logged Catch all exception
			this.waveSettings = CodegenFactory.eINSTANCE.createWaveDevSettings();
		}

		// If we're missing settings, recreate them
		CodegenUtil.recreateImplSettings(softPkg, this.waveSettings);

		setInitializing(true);

		try {
			// Loop through all the implementations
			for (final Implementation impl : softPkg.getImplementation()) {
				final ImplementationSettings oldImplSettings = this.waveSettings.getImplSettings().get(impl.getId());

				// Create and add the page for the implementation
				final ImplementationWizardPage page = new ImplementationWizardPage(oldImplSettings.getName(), ICodeGeneratorDescriptor.COMPONENT_TYPE_RESOURCE);
				addPage(page);

				// Import the implementation
				page.importImplementation(impl, oldImplSettings);

				final ImplementationSettings settings = page.getImplSettings();
				this.waveSettings.getImplSettings().put(impl.getId(), settings);

				page.setTitle("Verify Settings");
				page.setDescription("Verify the settings for the implementations: " + settings.getName());

				// Configure the settings page if there is one for this
				// implementation
				final ICodeGeneratorDescriptor codeGen = page.getCodeGenerator();
				final ITemplateDesc[] templates = RedhawkCodegenActivator.getCodeGeneratorTemplatesRegistry().findTemplatesByCodegen(codeGen.getId());
				if (templates.length > 0) {
					// findPageByGeneratorId is always guaranteed to return
					// at least one page. Add this page to the wizard
					final ICodegenWizardPage codeGenPage = RedhawkCodegenUiActivator.getCodeGeneratorsRegistry()
					        .findPageByGeneratorId(settings.getGeneratorId())[0];
					addPage(codeGenPage);

					// Enable the canFlip if this isn't the last page in the list
					codeGenPage.setCanFlipToNextPage((getImplList().size() + 1) != softPkg.getImplementation().size());

					// Configure the wizard page with the current settings
					String componentType = ICodeGeneratorDescriptor.COMPONENT_TYPE_RESOURCE;
					if (softPkg.getDescriptor() != null) {
						componentType = softPkg.getDescriptor().getComponent().getComponentType();
					}
					codeGenPage.configure(getSoftPkg(), impl, codeGen, settings, componentType);
				}

				// Save the settings
				getImplList().add(new ImplementationAndSettings(impl, settings));
			}

		} finally {
			setInitializing(false);
		}
	}

	@Override
	public void switchingResourcePage() {
		final ImplementationWizardPage page = (ImplementationWizardPage) getWizPages().get(0);
		page.setName(ModelUtil.getProject(getSoftPkg()).getName());
	}

	@Override
	public boolean checkGenerator(final ImplementationSettings settings, final ICodeGeneratorDescriptor codeGeneratorDescriptor) {
		if (settings.getGeneratorId().equals(codeGeneratorDescriptor.getId())) {
			this.getContainer().updateButtons();
			return true;
		}
		return false;
	}

	public String getName() {
		return getSoftPkg().getName();
	}

	@Override
    protected IFile createComponentFiles(IProject project, String name, String id, String author, IProgressMonitor monitor) throws CoreException {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    protected IProject createEmptyProject(String projectName, java.net.URI locationURI, IProgressMonitor monitor) throws CoreException {
	    // TODO Auto-generated method stub
	    return null;
    }

}
