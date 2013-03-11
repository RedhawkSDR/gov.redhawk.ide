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
package gov.redhawk.ide.sad.internal.ui.editor;

import gov.redhawk.common.ui.editor.FormLayoutFactory;
import gov.redhawk.ide.debug.ui.LaunchUtil;
import gov.redhawk.ide.sad.internal.ui.section.ExportingSection;
import gov.redhawk.ide.sad.internal.ui.section.ExternalPortsSection;
import gov.redhawk.ide.sad.internal.ui.section.GeneralInfoSection;
import gov.redhawk.ide.sad.internal.ui.section.TestingSection;
import gov.redhawk.ide.sad.ui.SadUiActivator;
import gov.redhawk.ide.sdr.ui.export.DeployableScaExportWizard;
import gov.redhawk.ide.ui.doc.IdeHelpConstants;
import gov.redhawk.model.sca.util.ModelUtil;
import gov.redhawk.ui.editor.AbstractOverviewPage;
import gov.redhawk.ui.editor.SCAFormEditor;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * 
 */
public class SadOverviewPage extends AbstractOverviewPage {

	public static final String PAGE_ID = "sadEditorOverviewPage";
	private TestingSection testingSection;
	private ExportingSection exportingSection;
	private GeneralInfoSection fInfoSection;
	private ExternalPortsSection externalPortsSection;

	/**
	 * @param editor
	 * @param id
	 * @param title
	 */
	public SadOverviewPage(final SCAFormEditor editor) {
		super(editor, SadOverviewPage.PAGE_ID, "Overview");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getHelpResource() {
		return IdeHelpConstants.reference_editors_waveform_overview;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createFormContent(final IManagedForm managedForm) {
		super.createFormContent(managedForm);
		final ScrolledForm form = managedForm.getForm();
		final FormToolkit toolkit = managedForm.getToolkit();
		// TODO form.setImage();
		form.setText("Overview");
		fillBody(managedForm, toolkit);

		PlatformUI.getWorkbench().getHelpSystem().setHelp(form.getBody(), IdeHelpConstants.reference_editors_waveform_overview);
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

		final Composite right = toolkit.createComposite(body);
		right.setLayout(FormLayoutFactory.createFormPaneTableWrapLayout(false, 1));
		right.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

		createExternalPortsSection(managedForm, right, toolkit);

		createTestingSection(managedForm, right, toolkit);

		createExportingSection(managedForm, right, toolkit);

	}

	private void createExternalPortsSection(final IManagedForm managedForm, final Composite parent, final FormToolkit toolkit) {
		this.externalPortsSection = new ExternalPortsSection(this, parent);
		managedForm.addPart(this.externalPortsSection);
		this.externalPortsSection.refresh(getInput());
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
	 * Creates the general info section.
	 * 
	 * @param managedForm the managed form
	 * @param left the left
	 * @param toolkit the toolkit
	 */
	private void createGeneralInfoSection(final IManagedForm managedForm, final Composite left, final FormToolkit toolkit) {
		this.fInfoSection = new GeneralInfoSection(this, left);
		managedForm.addPart(this.fInfoSection);
		this.fInfoSection.refresh(getInput());
	}

	/**
	 * {@inheritDoc}
	 */
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
		final IFile sadFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(getEditor().getMainResource().getURI().toPlatformString(true)));
		try {
			LaunchUtil.launch(SoftwareAssembly.Util.getSoftwareAssembly(this.getEditor().getMainResource()), sadFile, mode, getEditorSite().getShell());
		} catch (final CoreException e) {
			final Status status = new Status(IStatus.ERROR, SadUiActivator.PLUGIN_ID, e.getStatus().getMessage(), e.getStatus().getException());
			StatusManager.getManager().handle(status, StatusManager.LOG | StatusManager.SHOW);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void refresh(final Resource resource) {
		if (this.fInfoSection != null) {
			this.fInfoSection.refresh(resource);
		}
	}

}
