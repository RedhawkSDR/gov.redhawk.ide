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

import gov.redhawk.common.ui.editor.FormLayoutFactory;
import gov.redhawk.ui.editor.AbstractOverviewPage;
import gov.redhawk.ui.editor.ScaSection;
import gov.redhawk.ui.util.SCAEditorUtil;

import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.sad.Port;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.emf.databinding.edit.EMFEditProperties;
import org.eclipse.emf.databinding.edit.IEMFEditValueProperty;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;

public class ExternalPortsSection extends ScaSection {

	private ExternalPortsComposite client;
	private Resource sadResource;
	private final List<Binding> bindings = new ArrayList<Binding>();

	public ExternalPortsSection(final AbstractOverviewPage page, final Composite parent) {
		super(page, parent, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
		createClient(getSection(), page.getEditor().getToolkit());
	}

	@Override
	protected void createClient(final Section section, final FormToolkit toolkit) {
		section.setText("External Ports");
		section.setLayout(FormLayoutFactory.createClearTableWrapLayout(false, 1));
		final TableWrapData data = new TableWrapData(TableWrapData.FILL_GRAB);
		section.setLayoutData(data);

		section.setDescription("In this section you can assign the external ports of the waveform.");

		final IActionBars actionBars = getPage().getEditor().getEditorSite().getActionBars();
		this.client = new ExternalPortsComposite(section, SWT.None, toolkit, actionBars);
		section.setClient(this.client);

		addListeners(actionBars);

		toolkit.adapt(this.client);
		toolkit.paintBordersFor(this.client);
	}

	private void addListeners(final IActionBars actionBars) {
		this.client.getAddButton().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				handleAddExternalPort();
			}
		});
		this.client.getRemoveButton().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				handleRemoveExternalPort();
			};
		});
	}

	private void handleRemoveExternalPort() {
		if (!this.client.getPortViewer().getSelection().isEmpty()) {
			final RemoveExternalPortAction action = new RemoveExternalPortAction();
			action.setEditingDomain(getPage().getEditingDomain());
			action.setPort((Port) ((IStructuredSelection) this.client.getPortViewer().getSelection()).getFirstElement());
			action.run();
		}
	}

	private void handleAddExternalPort() {
		final ExternalPortWizard wizard = new ExternalPortWizard();
		wizard.setSoftwareAssembly(getWaveform());
		final WizardDialog dialog = new WizardDialog(getPage().getSite().getShell(), wizard);
		if (dialog.open() == Window.OK) {
			final AddExternalPortAction action = new AddExternalPortAction();
			action.setSoftwareAssembly(getWaveform());
			action.setComponentPort(wizard.getPortSelection());
			action.setComponentInstantiation(wizard.getComponentSelection());
			action.setPortDescription(wizard.getPortDescription());
			action.run();
		}
	}

	@Override
	public AbstractOverviewPage getPage() {
		return (AbstractOverviewPage) super.getPage();
	}

	private SoftwareAssembly getWaveform() {
		if (this.sadResource != null) {
			return SoftwareAssembly.Util.getSoftwareAssembly(this.sadResource);
		}
		return null;
	}

	@Override
	public void refresh(final Resource resource) {
		super.refresh(resource);
		this.sadResource = resource;

		this.client.setEditable(SCAEditorUtil.isEditableResource(getPage(), this.sadResource));
		for (final Binding binding : this.bindings) {
			binding.dispose();
		}
		this.bindings.clear();

		final IEMFEditValueProperty inputValue = EMFEditProperties.value(getPage().getEditingDomain(), SadPackage.Literals.SOFTWARE_ASSEMBLY__EXTERNAL_PORTS);
		final DataBindingContext context = this.getPage().getEditor().getDataBindingContext();

		@SuppressWarnings("unchecked")
		IObservableValue< ? > waveformExtPortsObservable = inputValue.observe(getWaveform());
		this.bindings.add(context.bindValue(ViewersObservables.observeInput(this.client.getPortViewer()), waveformExtPortsObservable));
	}

}
