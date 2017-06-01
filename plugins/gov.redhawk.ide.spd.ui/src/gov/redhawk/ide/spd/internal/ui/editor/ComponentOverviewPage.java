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
package gov.redhawk.ide.spd.internal.ui.editor;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.emf.common.ui.viewer.IViewerProvider;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.menus.IMenuService;
import org.eclipse.ui.statushandlers.StatusManager;

import gov.redhawk.common.ui.editor.FormLayoutFactory;
import gov.redhawk.ide.codegen.CodegenUtil;
import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.debug.ScaDebugLaunchConstants;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.internal.ComponentLaunch;
import gov.redhawk.ide.debug.internal.ComponentProgramLaunchUtils;
import gov.redhawk.ide.debug.ui.LaunchUtil;
import gov.redhawk.ide.sdr.ui.export.DeployableScaExportWizard;
import gov.redhawk.ide.spd.ui.ComponentUiPlugin;
import gov.redhawk.ide.spd.ui.editor.AuthorsSection;
import gov.redhawk.model.sca.util.ModelUtil;
import gov.redhawk.ui.editor.AbstractOverviewPage;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

/**
 * Provides the "Overview" page in the SPD editor.
 */
@SuppressWarnings("restriction")
public class ComponentOverviewPage extends AbstractOverviewPage implements IViewerProvider {

	/** The Constant PAGE_ID. */
	public static final String PAGE_ID = "componentOverview"; //$NON-NLS-1$
	/** The Constant Toolbar ID. */
	public static final String TOOLBAR_ID = "gov.redhawk.ide.spd.internal.ui.editor.overview.toolbar"; //$NON-NLS-1$
	private GeneralInfoSection fInfoSection;
	private InterfaceSection fInterfaceSection;
	private ProjectDocumentationSection projectDocumentationSection;
	private TestingSection testingSection;
	private ExportingSection exportingSection;
	private AuthorsSection fAuthorsSection;
	private Resource spdResource;
	private Resource scdResource;
	private IResource wavDevResource;

	/**
	 * Instantiates a new component overview page.
	 * 
	 * @param editor the editor
	 * @param spdResource the spd resource
	 */
	public ComponentOverviewPage(final ComponentEditor editor) {
		super(editor, ComponentOverviewPage.PAGE_ID, "Overview");
	}

	@Override
	public ComponentEditor getEditor() {
		return (ComponentEditor) super.getEditor();
	}

	@Override
	protected void createFormContent(final IManagedForm managedForm) {

		final ScrolledForm form = managedForm.getForm();
		final FormToolkit toolkit = managedForm.getToolkit();

		form.setText("Overview");

		fillBody(managedForm, toolkit);

		// Refresh Page
		refresh(this.spdResource);
		refresh(this.scdResource);

		super.createFormContent(managedForm);

		final ToolBarManager manager = (ToolBarManager) form.getToolBarManager();
		final IMenuService service = (IMenuService) getSite().getService(IMenuService.class);
		service.populateContributionManager(manager, "toolbar:" + ComponentOverviewPage.TOOLBAR_ID);
		manager.update(true);
	}

	@Override
	public void dispose() {
		this.removeResourceListener(this.scdResource);
		super.dispose();
	}

	/**
	 * Fill body.
	 * 
	 * @param managedForm the managed form
	 * @param toolkit the toolkit
	 */
	private void fillBody(final IManagedForm managedForm, final FormToolkit toolkit) {
		final Composite body = managedForm.getForm().getBody();
		body.setLayout(FormLayoutFactory.createFormTableWrapLayout(true, 2));

		final Composite left = toolkit.createComposite(body);
		left.setLayout(FormLayoutFactory.createFormPaneTableWrapLayout(false, 1));
		left.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		createGeneralInfoSection(managedForm, left, toolkit);
		createProjectDocumentationSection(managedForm, left, toolkit);
		// XXX Don't add the author section since it was deemed confusing
		// createAuthorsSection(managedForm, left, toolkit);

		final Composite right = toolkit.createComposite(body);
		right.setLayout(FormLayoutFactory.createFormPaneTableWrapLayout(false, 1));
		right.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

		// The following sections don't make sense for libraries, since the don't have .prf or .scd
		if (!isSoftpackageLibrary()) {
			createInterfaceSection(managedForm, right, toolkit);
			createTestingSection(managedForm, right, toolkit);
		}

		createExportingSection(managedForm, right, toolkit);

	}

	/**
	 * Creates the testing section.
	 * 
	 * @param managedForm the managed form
	 * @param right the right
	 * @param toolkit the toolkit
	 */
	private void createTestingSection(final IManagedForm managedForm, final Composite right, final FormToolkit toolkit) {
		this.testingSection = new TestingSection(this, right);
		managedForm.addPart(this.testingSection);
	}

	/**
	 * Creates the project documentation section.
	 * 
	 * @param managedForm the managed form
	 * @param right the right
	 * @param toolkit the toolkit
	 */
	private void createProjectDocumentationSection(final IManagedForm managedForm, final Composite right, final FormToolkit toolkit) {
		this.projectDocumentationSection = new ProjectDocumentationSection(this, right);
		managedForm.addPart(this.projectDocumentationSection);
	}

	/**
	 * Creates the general info section.
	 * 
	 * @param managedForm the managed form
	 * @param left the left
	 * @param toolkit the toolkit
	 */
	private void createGeneralInfoSection(final IManagedForm managedForm, final Composite left, final FormToolkit toolkit) {
		this.fInfoSection = new GeneralInfoSection(this, left);
		managedForm.addPart(this.fInfoSection);
	}

	/**
	 * Creates the exporting section.
	 * 
	 * @param managedForm the managed form
	 * @param right the right
	 * @param toolkit the toolkit
	 */
	private void createExportingSection(final IManagedForm managedForm, final Composite right, final FormToolkit toolkit) {
		this.exportingSection = new ExportingSection(this, right);
		managedForm.addPart(this.exportingSection);
	}

	/**
	 * Creates the Interface section.
	 * 
	 * @param managedForm the managed form
	 * @param right the right
	 * @param toolkit the toolkit
	 * @since 2.1
	 */
	public void createInterfaceSection(final IManagedForm managedForm, final Composite right, final FormToolkit toolkit) {
		this.fInterfaceSection = new InterfaceSection(this, right);
		managedForm.addPart(this.fInterfaceSection);
	}

	@Override
	public void linkActivated(final HyperlinkEvent e) {
		final Object href = e.getHref();
		if (TestingSection.TESTING_HREF_LOCAL_LAUNCH.equals(href)) {
			launch(ILaunchManager.RUN_MODE);
		} else if (TestingSection.TESTING_HREF_LOCAL_DEBUG.equals(href)) {
			launch(ILaunchManager.DEBUG_MODE);
		} else if ("export".equals(e.getHref())) {
			final DeployableScaExportWizard wizard = new DeployableScaExportWizard();
			final IProject project = ModelUtil.getProject(getInput());
			wizard.init(getEditor().getSite().getWorkbenchWindow().getWorkbench(), new StructuredSelection(project));
			final WizardDialog dialog = new WizardDialog(getEditor().getSite().getShell(), wizard);
			dialog.open();
		}
	}

	private void launch(final String mode) {
		try {
			ILaunchConfigurationWorkingCopy newConfig = LaunchUtil.createLaunchConfiguration(SoftPkg.Util.getSoftPkg(spdResource), getEditorSite().getShell());
			if (spdResource.getURI().isPlatform()) {
				ILaunchConfiguration config = LaunchUtil.chooseConfiguration(mode, LaunchUtil.findLaunchConfigurations(newConfig), getEditorSite().getShell());
				if (config == null) {
					config = newConfig.doSave();
				}

				// Get implementation
				final SoftPkg spd = SoftPkg.Util.getSoftPkg(spdResource);
				String implID = config.getAttribute(ScaDebugLaunchConstants.ATT_IMPL_ID, (String) null);
				final Implementation impl = spd.getImplementation(implID);

				// Shared address space components must be launched within a component host
				if (SoftPkg.Util.isContainedComponent(impl)) {
					final ILaunchConfigurationWorkingCopy workingCopy = config.getWorkingCopy();
					Job job = new Job("Contained component overview tab launch") {

						@Override
						protected IStatus run(IProgressMonitor monitor) {
							try {
								launchContainedComponent(workingCopy, mode, spd, impl, monitor);
							} catch (CoreException e) {
								final Status status = new Status(e.getStatus().getSeverity(), ComponentUiPlugin.PLUGIN_ID, e.getLocalizedMessage(), e);
								StatusManager.getManager().handle(status, StatusManager.LOG | StatusManager.SHOW);
							}
							return Status.OK_STATUS;
						}
					};
					job.setUser(false);
					job.schedule();
					return;
				}

				// Legacy launch
				DebugUITools.launch(config, mode);
			} else {
				LaunchUtil.launch(newConfig, mode);
			}
		} catch (CoreException e) {
			final Status status = new Status(e.getStatus().getSeverity(), ComponentUiPlugin.PLUGIN_ID, e.getLocalizedMessage(), e.getCause());
			StatusManager.getManager().handle(status, StatusManager.LOG | StatusManager.SHOW);
		}
	}

	private void launchContainedComponent(ILaunchConfigurationWorkingCopy workingCopy, String mode, SoftPkg spd, Implementation impl, IProgressMonitor monitor)
		throws CoreException {

		// Create component launch
		ComponentLaunch launch = new ComponentLaunch(workingCopy, mode, null);
		ComponentProgramLaunchUtils.insertProgramArguments(spd, launch, workingCopy);

		LocalScaWaveform waveform = ScaDebugPlugin.getInstance().getLocalSca().getSandboxWaveform();

		ComponentProgramLaunchUtils.launch(waveform, workingCopy, launch, spd, impl, mode, monitor);

		// Add the created ILaunch to the launch manager so that it receives notifications for things like terminate
		DebugPlugin.getDefault().getLaunchManager().addLaunch(launch);
	}

	@Override
	public void setInput(final Resource input) {
		super.setInput(input);
		this.spdResource = input;
		final SoftPkg spd = SoftPkg.Util.getSoftPkg(input);
		if (spd != null && spd.getDescriptor() != null && spd.getDescriptor().getComponent() != null) {
			this.scdResource = spd.getDescriptor().getComponent().eResource();
			addResourceListener(this.scdResource);
			refresh(this.scdResource);

			// Adding the wavDev to the list of tracked resources. This will allow it to be updated when external
			// forces (From the Generate Code methods) update the CRC32s contained within the File.
			// This was to fix Bug # 21
			final URI wavDevUri = CodegenUtil.getWaveDevSettingsURI(input.getURI());
			if (wavDevUri != null && wavDevUri.isPlatform()) {
				IPath wavDevPath = new Path(wavDevUri.toPlatformString(true));
				this.wavDevResource = ResourcesPlugin.getWorkspace().getRoot().getFile(wavDevPath);
				this.getEditor().getResourceTracker().addTrackedResource(wavDevResource);
			}
		}
	}

	@Override
	protected void refresh(final Resource resource) {
		if (resource == null) {
			return;
		}

		if (resource == this.scdResource) {
			if (this.fInterfaceSection != null) {
				this.fInterfaceSection.refresh(this.scdResource);
			}
		} else {
			if (this.projectDocumentationSection != null) {
				this.projectDocumentationSection.refresh(resource);
			}
			if (this.exportingSection != null) {
				this.exportingSection.refresh(resource);
			}
			if (this.fAuthorsSection != null) {
				this.fAuthorsSection.refresh(resource);
			}
			if (this.fInfoSection != null) {
				this.fInfoSection.refresh(resource);
			}
			if (this.testingSection != null) {
				this.testingSection.refresh(resource);
			}
		}
	}

	/**
	 * Checks to see if the resource is a Softpackage Library
	 * Important because a number of entry fields are omitted in the case of libraries
	 */
	public boolean isSoftpackageLibrary() {
		for (EObject i : spdResource.getContents()) {
			if (i instanceof SoftPkg) {
				for (Implementation impl : ((SoftPkg) i).getImplementation()) {
					if (impl.isSharedLibrary()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * @return the scdResource
	 */
	public Resource getScdResource() {
		return this.scdResource;
	}

	/**
	 * @return the spdResource
	 */
	public Resource getSpdResource() {
		return this.spdResource;
	}

	@Override
	public Viewer getViewer() {
		return null;
	}
}
