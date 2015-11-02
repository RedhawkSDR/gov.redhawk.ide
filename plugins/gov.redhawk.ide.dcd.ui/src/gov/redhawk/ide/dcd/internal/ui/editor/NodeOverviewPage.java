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
package gov.redhawk.ide.dcd.internal.ui.editor;

import gov.redhawk.common.ui.editor.FormLayoutFactory;
import gov.redhawk.ide.dcd.internal.ui.HelpContextIds;
import gov.redhawk.ide.dcd.internal.ui.ScaIdeConstants;
import gov.redhawk.ide.sdr.ui.export.DeployableScaExportWizard;
import gov.redhawk.ide.spd.ui.editor.AuthorsSection;
import gov.redhawk.model.sca.util.ModelUtil;
import gov.redhawk.ui.editor.AbstractOverviewPage;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.dcd.DeviceManagerSoftPkg;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.menus.IMenuService;

/**
 * The Class NodeOverviewPage.
 */
public class NodeOverviewPage extends AbstractOverviewPage {

	/** The Constant PAGE_ID. */
	public static final String PAGE_ID = "nodeOverview"; //$NON-NLS-1$
	private static final String TOOLBAR_ID = "gov.redhawk.ide.dcd.internal.ui.editor.overview.toolbar";
	private GeneralInfoSection fInfoSection;
	private NodeContentSection nodeContent;
	private TestingSection testingSection;
	private ExportingSection exportingSection;
	private AuthorsSection fAuthorsSection;
	private Resource dcdResource;
	private Resource spdResource;

	/**
	 * Instantiates a new node overview page.
	 * 
	 * @param editor the editor
	 */
	public NodeOverviewPage(final NodeEditor editor) {
		super(editor, NodeOverviewPage.PAGE_ID, "Overview");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeEditor getEditor() {
		return (NodeEditor) super.getEditor();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getHelpResource() {
		return ScaIdeConstants.PLUGIN_DOC_ROOT + "guide/tools/editors/node_editor/overview.htm"; //$NON-NLS-1$
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
		PlatformUI.getWorkbench().getHelpSystem().setHelp(form.getBody(), HelpContextIds.NODE_OVERVIEW);
		
		final ToolBarManager manager = (ToolBarManager) form.getToolBarManager();
		final IMenuService service = (IMenuService) getSite().getService(IMenuService.class);
		service.populateContributionManager(manager, "toolbar:" + NodeOverviewPage.TOOLBAR_ID);
		manager.update(true);

		refresh(this.dcdResource);
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

		// XXX Disable Authors Sections
		// createAuthorsSection(managedForm, left, toolkit);

		final Composite right = toolkit.createComposite(body);
		right.setLayout(FormLayoutFactory.createFormPaneTableWrapLayout(false, 1));
		right.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

		createNodeContentSection(managedForm, right, toolkit);

		createTestingSection(managedForm, right, toolkit);

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
	 * Creates the node content section.
	 * 
	 * @param managedForm the managed form
	 * @param right the right
	 * @param toolkit the toolkit
	 */
	private void createNodeContentSection(final IManagedForm managedForm, final Composite right, final FormToolkit toolkit) {
		this.nodeContent = new NodeContentSection(this, right);
		managedForm.addPart(this.nodeContent);
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
	 * {@inheritDoc}
	 */
	@Override
	public void linkActivated(final HyperlinkEvent e) {
		final Object href = e.getHref();
		if (NodeContentSection.DEVICE_HREF.equals(href)) {
			getEditor().setActivePage(DevicesPage.PAGE_ID);
		} else if (TestingSection.TESTING_HREF.equals(e.getHref())) {
			MessageDialog.openInformation(getEditor().getSite().getShell(), "Unsupported Operation", "Currently testing is unsupported.");
		} else if ("export".equals(e.getHref())) {
			final DeployableScaExportWizard wizard = new DeployableScaExportWizard();
			final IProject project = ModelUtil.getProject(getInput());
			wizard.init(getEditor().getSite().getWorkbenchWindow().getWorkbench(), new StructuredSelection(project));
			final WizardDialog dialog = new WizardDialog(getEditor().getSite().getShell(), wizard);
			dialog.open();
		}
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setInput(final Resource input) {
		this.dcdResource = input;
		super.setInput(input);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		removeResourceListener(this.spdResource);
		super.dispose();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void refresh(final Resource resource) {
		if (resource == null) {
			return;
		}
		if (resource == this.dcdResource) {
			removeResourceListener(this.spdResource);

			final DeviceConfiguration dcd = ModelUtil.getDeviceConfiguration(this.dcdResource);
			if (dcd == null) {
				return;
			}

			// Listen for changes on the referenced softpkg of the device manager (if any)
			final DeviceManagerSoftPkg dmSpd = dcd.getDeviceManagerSoftPkg();
			if (dmSpd == null || dmSpd.getSoftPkg() == null) {
				this.spdResource = null;
			} else {
				this.spdResource = dmSpd.getSoftPkg().eResource();
				addResourceListener(this.spdResource);
			}

			if (this.exportingSection != null) {
				this.exportingSection.refresh(resource);
			}
			if (this.fInfoSection != null) {
				this.fInfoSection.refresh(resource);
			}
			if (this.nodeContent != null) {
				this.nodeContent.refresh(resource);
			}
			if (this.testingSection != null) {
				this.testingSection.refresh(resource);
			}

			refresh(this.spdResource);
		} else if (resource == this.spdResource) {
			if (this.fAuthorsSection != null) {
				this.fAuthorsSection.refresh(resource);
			}
		}
	}
}
