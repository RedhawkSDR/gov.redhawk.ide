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
package gov.redhawk.ide.graphiti.sad.internal.ui.page.overview;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.StructuredSelection;
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
import gov.redhawk.ide.debug.ui.LaunchUtil;
import gov.redhawk.ide.graphiti.sad.internal.ui.editor.overview.ProjectDocumentationSection;
import gov.redhawk.ide.graphiti.sad.ui.SADUIGraphitiPlugin;
import gov.redhawk.ide.sdr.ui.export.DeployableScaExportWizard;
import gov.redhawk.model.sca.util.ModelUtil;
import gov.redhawk.ui.editor.AbstractOverviewPage;
import gov.redhawk.ui.editor.SCAFormEditor;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

/**
 * Provides the "Overview" page in the SAD editor.
 */
public class SadOverviewPage extends AbstractOverviewPage {
 
	public static final String PAGE_ID = "sadEditorOverviewPage"; //$NON-NLS-1$
	private static final String TOOLBAR_ID = "gov.redhawk.ide.sad.internal.ui.editor.overview.toolbar"; //$NON-NLS-1$
	private GeneralInfoSection fInfoSection;
	private ProjectDocumentationSection projectDocumentationSection;
	private ExternalPortsSection externalPortsSection;
	private TestingSection testingSection;
	private ExportingSection exportingSection;

	public SadOverviewPage(final SCAFormEditor editor) {
		super(editor, SadOverviewPage.PAGE_ID, "Overview");
	}

	@Override
	protected void createFormContent(final IManagedForm managedForm) {
		super.createFormContent(managedForm);
		final ScrolledForm form = managedForm.getForm();
		final FormToolkit toolkit = managedForm.getToolkit();
		form.setText("Overview");
		fillBody(managedForm, toolkit);

		final ToolBarManager manager = (ToolBarManager) form.getToolBarManager();
		final IMenuService service = (IMenuService) getSite().getService(IMenuService.class);
		service.populateContributionManager(manager, "toolbar:" + SadOverviewPage.TOOLBAR_ID);
		manager.update(true);
	}

	private void fillBody(final IManagedForm managedForm, final FormToolkit toolkit) {
		final Composite body = managedForm.getForm().getBody();
		body.setLayout(FormLayoutFactory.createFormTableWrapLayout(true, 2));

		boolean isPlatformProject = getEditor().getMainResource().getURI().isPlatform();

		final Composite left = toolkit.createComposite(body);
		left.setLayout(FormLayoutFactory.createFormPaneTableWrapLayout(false, 1));
		left.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		createGeneralInfoSection(managedForm, left, toolkit);
		if (isPlatformProject) {
			createProjectDocumentationSection(managedForm, left, toolkit);
		}

		final Composite right = toolkit.createComposite(body);
		right.setLayout(FormLayoutFactory.createFormPaneTableWrapLayout(false, 1));
		right.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		createExternalPortsSection(managedForm, right, toolkit);
		createTestingSection(managedForm, right, toolkit);
		if (isPlatformProject) {
			createExportingSection(managedForm, right, toolkit);
		}
	}

	private void createExternalPortsSection(final IManagedForm managedForm, final Composite parent, final FormToolkit toolkit) {
		this.externalPortsSection = new ExternalPortsSection(this, parent);
		managedForm.addPart(this.externalPortsSection);
		this.externalPortsSection.refresh(getInput());
	}

	private void createTestingSection(final IManagedForm managedForm, final Composite right, final FormToolkit toolkit) {
		this.testingSection = new TestingSection(this, right);
		managedForm.addPart(this.testingSection);
	}

	private void createExportingSection(final IManagedForm managedForm, final Composite right, final FormToolkit toolkit) {
		this.exportingSection = new ExportingSection(this, right);
		managedForm.addPart(this.exportingSection);

	}

	private void createGeneralInfoSection(final IManagedForm managedForm, final Composite left, final FormToolkit toolkit) {
		this.fInfoSection = new GeneralInfoSection(this, left);
		managedForm.addPart(this.fInfoSection);
		this.fInfoSection.refresh(getInput());
	}

	private void createProjectDocumentationSection(final IManagedForm managedForm, final Composite left, final FormToolkit toolkit) {
		this.projectDocumentationSection = new ProjectDocumentationSection(this, left);
		managedForm.addPart(this.projectDocumentationSection);
		this.projectDocumentationSection.refresh(getInput());
	}

	@Override
	public void linkActivated(final HyperlinkEvent e) {
		if (TestingSection.TESTING_HREF_DEBUG.equals(e.getHref())) {
			launch(ILaunchManager.DEBUG_MODE);
		} else if (TestingSection.TESTING_HREF_RUN.equals(e.getHref())) {
			launch(ILaunchManager.RUN_MODE);
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
			ILaunchConfigurationWorkingCopy newConfig = LaunchUtil.createLaunchConfiguration(
				SoftwareAssembly.Util.getSoftwareAssembly(this.getEditor().getMainResource()), getEditorSite().getShell());
			if (getEditor().getMainResource().getURI().isPlatform()) {
				ILaunchConfiguration[] oldConfigs = LaunchUtil.findLaunchConfigurations(newConfig);
				ILaunchConfiguration config = null;
				if (oldConfigs != null) {
					config = LaunchUtil.chooseConfiguration(mode, oldConfigs, getEditorSite().getShell());
				} else {
					config = newConfig.doSave();
				}
				if (config == null) {
					return;
				}
				LaunchUtil.launch(config, mode);
			} else {
				LaunchUtil.launch(newConfig, mode);
			}
		} catch (final CoreException e) {
			final Status status = new Status(IStatus.ERROR, SADUIGraphitiPlugin.PLUGIN_ID, e.getLocalizedMessage(), e);
			StatusManager.getManager().handle(status, StatusManager.LOG | StatusManager.SHOW);
		}
	}

	@Override
	protected void refresh(final Resource resource) {
		if (this.fInfoSection != null) {
			this.fInfoSection.refresh(resource);
		}
		if (this.externalPortsSection != null) {
			this.externalPortsSection.refresh(resource);
		}
	}

}
