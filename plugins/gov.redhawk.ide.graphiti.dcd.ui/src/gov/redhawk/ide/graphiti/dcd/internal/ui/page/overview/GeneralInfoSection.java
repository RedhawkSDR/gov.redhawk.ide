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
package gov.redhawk.ide.graphiti.dcd.internal.ui.page.overview;

import gov.redhawk.common.ui.editor.FormLayoutFactory;
import gov.redhawk.common.ui.parts.FormEntry;
import gov.redhawk.model.sca.util.ModelUtil;
import gov.redhawk.ui.editor.FormEntryAdapter;
import gov.redhawk.ui.editor.SCAFormEditor;
import gov.redhawk.ui.editor.ScaSection;
import gov.redhawk.ui.util.EMFEmptyStringToNullUpdateValueStrategy;
import gov.redhawk.ui.util.SCAEditorUtil;

import java.util.ArrayList;
import java.util.Collection;

import mil.jpeojtrs.sca.dcd.DcdPackage;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.DceUuidUtil;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.databinding.edit.EMFEditObservables;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;

/**
 * @since 1.1
 */
public class GeneralInfoSection extends ScaSection {
	private GeneralInformationComposite client;
	private final Collection<Binding> bindings = new ArrayList<Binding>();
	private Resource dcdResource;

	public GeneralInfoSection(final NodeOverviewPage page, final Composite parent) {
		super(page, parent, Section.DESCRIPTION);
		createClient(getSection(), page.getEditor().getToolkit());
	}

	@Override
	public NodeOverviewPage getPage() {
		return (NodeOverviewPage) super.getPage();
	}

	@Override
	protected void createClient(final Section section, final FormToolkit toolkit) {
		section.setText("General Information");
		section.setLayout(FormLayoutFactory.createClearTableWrapLayout(false, 1));
		final TableWrapData data = new TableWrapData(TableWrapData.FILL_GRAB);
		section.setLayoutData(data);

		section.setDescription("This section describes general information about this node.");

		final IActionBars actionBars = getPage().getEditor().getEditorSite().getActionBars();
		this.client = new GeneralInformationComposite(section, SWT.None, toolkit, actionBars);
		section.setClient(this.client);

		addListeners(actionBars);

		toolkit.adapt(this.client);
		toolkit.paintBordersFor(this.client);
	}

	private void addListeners(final IActionBars actionBars) {
		final FormEntryAdapter fIdEntryAdapter = new FormEntryAdapter(actionBars) {
			@Override
			public void buttonSelected(final FormEntry entry) {
				execute(SetCommand.create(getEditingDomain(),
				        getDeviceConfiguration(),
				        DcdPackage.Literals.DEVICE_CONFIGURATION__ID,
				        DceUuidUtil.createDceUUID()));
			}
		};
		this.client.getIdEntry().setFormEntryListener(fIdEntryAdapter);
	}

	protected IProject getProject() {
		return ModelUtil.getProject(getSoftPkg());
	}

	protected void execute(final Command command) {
		getEditingDomain().getCommandStack().execute(command);
	}

	private EditingDomain getEditingDomain() {
		return getPage().getEditor().getEditingDomain();
	}

	private void setEditable(final boolean editable) {
		this.client.setEditable(editable);
	}

	@Override
	public void refresh(final Resource resource) {
		this.dcdResource = resource;

		final DeviceConfiguration dcd = ModelUtil.getDeviceConfiguration(this.dcdResource);

		setEditable(!getPage().getEditingDomain().isReadOnly(this.dcdResource));

		for (final Binding binding : this.bindings) {
			binding.dispose();
		}
		this.bindings.clear();

		final DataBindingContext context = this.getPage().getEditor().getDataBindingContext();

		this.bindings.add(context.bindValue(WidgetProperties.text(SWT.Modify).observeDelayed(SCAFormEditor.getFieldBindingDelay(),
		        this.client.getIdEntry().getText()),
		        EMFEditObservables.observeValue(getEditingDomain(), dcd, DcdPackage.Literals.DEVICE_CONFIGURATION__ID),
		        new EMFEmptyStringToNullUpdateValueStrategy(),
		        null));

		this.bindings.add(context.bindValue(WidgetProperties.text(SWT.Modify).observeDelayed(SCAFormEditor.getFieldBindingDelay(),
		        this.client.getNameEntry().getText()),
		        EMFEditObservables.observeValue(getEditingDomain(), dcd, DcdPackage.Literals.DEVICE_CONFIGURATION__NAME),
		        new EMFEmptyStringToNullUpdateValueStrategy(),
		        null));

		this.bindings.add(context.bindValue(WidgetProperties.text(SWT.Modify).observeDelayed(SCAFormEditor.getFieldBindingDelay(),
		        this.client.getDescriptionEntry().getText()),
		        EMFEditObservables.observeValue(getEditingDomain(), dcd, DcdPackage.Literals.DEVICE_CONFIGURATION__DESCRIPTION),
		        new EMFEmptyStringToNullUpdateValueStrategy(),
		        null));

		this.client.setEditable(SCAEditorUtil.isEditableResource(getPage(), this.dcdResource));
	}

	private DeviceConfiguration getDeviceConfiguration() {
		return ModelUtil.getDeviceConfiguration(this.dcdResource);
	}

	private SoftPkg getSoftPkg() {
		final DeviceConfiguration dcd = getDeviceConfiguration();
		if (dcd != null) {
			return dcd.getDeviceManagerSoftPkg().getSoftPkg();
		}
		return null;
	}

}
