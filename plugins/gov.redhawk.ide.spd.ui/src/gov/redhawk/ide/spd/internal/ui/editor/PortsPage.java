/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.spd.internal.ui.editor;

import gov.redhawk.common.ui.editor.FormLayoutFactory;
import gov.redhawk.ide.spd.internal.ui.handlers.PortsHandlerUtil;
import gov.redhawk.ui.editor.SCAFormEditor;
import gov.redhawk.ui.editor.ScaFormPage;

import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.scd.AbstractPort;
import mil.jpeojtrs.sca.scd.Ports;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.ui.viewer.IViewerProvider;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.menus.IMenuService;

public class PortsPage extends ScaFormPage implements IViewerProvider {

	/** The Constant PAGE_ID. */
	public static final String PAGE_ID = "ports"; //$NON-NLS-1$

	/** Context ID - Used for keybindings */
	public static final String PORTS_TAB_CONTEXT = "gov.redhawk.ide.spd.ui.ports.context";

	/** The toolbar contribution ID */
	public static final String TOOLBAR_ID = "gov.redhawk.ide.spd.internal.ui.editor.overview.toolbar";

	private Resource scdResource;
	private ISelectionChangedListener portViewerListener;
	private SelectionListener directionComboListener;
	private List<IContextActivation> contextActivations = new ArrayList<IContextActivation>();
	private SoftPkg spd;

	private PortsSection fPortsSection;
	private PortDetailsSection fPortDetailsSection;
	private final PortsPageModel model;

	// This adapter makes sure things that affect the model (like adding/removing ports) are
	// immediately updated in the ports viewer
	private final Adapter resourceChangedListener = new AdapterImpl() {

		@Override
		public void notifyChanged(final Notification msg) {
			switch (msg.getFeatureID(Resource.class)) {
			case Resource.RESOURCE__IS_MODIFIED:
				Object selectedPage = getEditor().getSelectedPage();
				if (selectedPage != null && selectedPage instanceof PortsPage) {
					fPortsSection.refresh(PortsPage.this.scdResource);
				}
				break;
			default:
				break;
			}
		}
	};

	public PortsPage(SCAFormEditor editor) {
		super(editor, PAGE_ID, "Ports");
		activateContext(PORTS_TAB_CONTEXT);
		this.model = new PortsPageModel(editor);
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		final ScrolledForm form = managedForm.getForm();
		final FormToolkit toolkit = managedForm.getToolkit();

		form.setText("Ports");

		fillBody(managedForm, toolkit);

		refresh(this.scdResource);

		super.createFormContent(managedForm);

		final ToolBarManager manager = (ToolBarManager) form.getToolBarManager();
		final IMenuService service = (IMenuService) getSite().getService(IMenuService.class);
		service.populateContributionManager(manager, "toolbar:" + ComponentOverviewPage.TOOLBAR_ID);
		manager.update(true);
	}

	@Override
	public void dispose() {
		deactivateAllContexts();
		this.removeResourceListener(this.scdResource);
		this.removeResourceChangedListener(this.scdResource);
		if (this.fPortsSection != null) {
			this.fPortsSection.getViewer().removeSelectionChangedListener(portViewerListener);
		}
		// TODO: Was getting errors when I tried to remove this
//		this.fPortDetailsSection.getDirectionCombo().getCombo().removeSelectionListener(directionComboListener);
		super.dispose();
	}

	/**
	 * Create different sections in the page body.
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

		final Composite right = toolkit.createComposite(body);
		right.setLayout(FormLayoutFactory.createFormPaneTableWrapLayout(false, 1));
		right.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

		createPortDetailsSection(managedForm, right, toolkit);
		createPortSection(managedForm, left, toolkit);
	}

	/**
	 * Creates the port section.
	 * 
	 * @param managedForm the managed form
	 * @param right the right
	 * @param toolkit the toolkit
	 */
	private void createPortSection(final IManagedForm managedForm, final Composite left, final FormToolkit toolkit) {
		this.fPortsSection = new PortsSection(this, left, model);

		final TableViewer portViewer = (TableViewer) this.fPortsSection.getViewer();

		portViewerListener = new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (scdResource.getEObject("/") == null) {
					PortsPage.this.fPortDetailsSection.getSection().setVisible(false);
					return;
				}
				
				Ports ports = PortsHandlerUtil.getPorts(PortsPage.this.spd);
				if (ports != null && model.getPorts() != ports) {
					model.setPorts(ports);
				}

				StructuredSelection ss = (StructuredSelection) event.getSelection();
				AbstractPort port = (AbstractPort) ss.getFirstElement();
				if (port == null) {
					PortsPage.this.fPortDetailsSection.getSection().setVisible(false);
				} else if (port != null && model.getPort() != port) {
					model.setPort(port);
					PortsPage.this.fPortDetailsSection.getSection().setVisible(true);
				}

				PortsPage.this.fPortsSection.refresh(scdResource);
				PortsPage.this.fPortDetailsSection.refresh(scdResource);
			}
		};

		portViewer.addSelectionChangedListener(portViewerListener);

		ComboViewer directionCombo = this.fPortDetailsSection.getDirectionCombo();
		directionComboListener = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// Refresh all composites
				PortsPage.this.fPortsSection.refresh(scdResource);
				PortsPage.this.fPortDetailsSection.refresh(scdResource);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		};

		directionCombo.getCombo().addSelectionListener(directionComboListener);

		managedForm.addPart(this.fPortsSection);
	}

	/**
	 * Creates the port section.
	 * 
	 * @param managedForm the managed form
	 * @param right the right
	 * @param toolkit the toolkit
	 */
	private void createPortDetailsSection(final IManagedForm managedForm, final Composite right, final FormToolkit toolkit) {
		this.fPortDetailsSection = new PortDetailsSection(this, right, getEditor(), model);
		this.fPortDetailsSection.getSection().setVisible(false);
		managedForm.addPart(this.fPortDetailsSection);
	}

	@Override
	public void setInput(Resource input) {
		super.setInput(input);
		spd = SoftPkg.Util.getSoftPkg(input);
		if (spd != null && spd.getDescriptor() != null && spd.getDescriptor().getComponent() != null) {
			this.model.setSoftPkg(spd);
			this.scdResource = spd.getDescriptor().getComponent().eResource();
			addResourceListener(this.scdResource);
			addResourceChangedListener(this.scdResource);
			refresh(this.scdResource);
		}
	}

	private void addResourceChangedListener(final Resource resource) {
		if (resource != null) {
			resource.eAdapters().add(this.resourceChangedListener);
		}
	}

	private void removeResourceChangedListener(final Resource resource) {
		if (resource != null) {
			resource.eAdapters().remove(this.resourceChangedListener);
		}
	}

	@Override
	protected void refresh(Resource resource) {
		if (resource == this.scdResource) {
			if (this.fPortsSection != null) {
				this.fPortsSection.refresh(this.scdResource);
			}
			if (this.fPortDetailsSection != null) {
				this.fPortDetailsSection.refresh(this.scdResource);
			}
		}
	}

	@Override
	public Viewer getViewer() {
		if (this.fPortsSection != null) {
			return this.fPortsSection.getViewer();
		}
		return null;
	}

	public void activateContext(String context) {
		IContextService contextService = (IContextService) getEditor().getSite().getService(IContextService.class);
		if (contextService != null) {
			IContextActivation activation = contextService.activateContext(context);
			contextActivations.add(activation);
		}

	}

	private void deactivateAllContexts() {
		if (!contextActivations.isEmpty()) {
			IContextService contextService = (IContextService) getSite().getService(IContextService.class);
			for (IContextActivation activation : contextActivations) {
				contextService.deactivateContext(activation);
			}
		}
	}
}
