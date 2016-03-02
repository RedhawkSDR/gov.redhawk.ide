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
package gov.redhawk.ide.spd.internal.ui.editor.detailspart;

import gov.redhawk.common.ui.editor.FormLayoutFactory;
import gov.redhawk.ide.spd.internal.ui.editor.ImplementationsSection;
import gov.redhawk.ide.spd.internal.ui.editor.composite.UsesDeviceComposite;
import gov.redhawk.ui.editor.ScaDetails;
import gov.redhawk.ui.parts.FormEntryBindingFactory;
import gov.redhawk.ui.util.EMFEmptyStringToNullUpdateValueStrategy;
import gov.redhawk.ui.util.SCAEditorUtil;

import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.spd.UsesDevice;
import mil.jpeojtrs.sca.util.DceUuidUtil;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * 
 */
public class UsesDeviceDetailsPage extends ScaDetails {

	private final ImplementationsSection fSection;
	private UsesDeviceComposite client;
	private UsesDevice input;

	/**
	 * The Constructor.
	 * 
	 * @param fSection the f section
	 */
	public UsesDeviceDetailsPage(final ImplementationsSection fSection) {
		super(fSection.getPage());
		this.fSection = fSection;
	}

	/**
	 * Creates the uses device section.
	 * 
	 * @param toolkit the toolkit
	 * @param parent the parent
	 */
	private void createUsesDeviceSection(final FormToolkit toolkit, final Composite parent) {
		final Section section = toolkit.createSection(parent, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE
		        | ExpandableComposite.EXPANDED);
		section.clientVerticalSpacing = FormLayoutFactory.SECTION_HEADER_VERTICAL_SPACING;
		section.setText("Uses Device");
		section.setDescription("The 'usesdevice' element describes any \"uses\" " + "relationships this resource has with a device in the system. "
		        + "The propertyref element references allocation properties, which "
		        + "indicate the CF Device to be used, and/or the capacity needed from the CF Device to be used.");

		section.setLayout(FormLayoutFactory.createClearGridLayout(false, 1));
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));

		// Align the master and details section headers (misalignment caused
		// by section toolbar icons)
		getPage().alignSectionHeaders(this.fSection.getSection(), section);

		this.client = new UsesDeviceComposite(section, SWT.None, toolkit);

		addListeners();

		section.setClient(this.client);
		toolkit.adapt(this.client);
	}

	/**
	 * 
	 */
	private void addListeners() {
		this.client.getIdEntry().getButton().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				handleGenerateId();
			}
		});

	}

	/**
	 * 
	 */
	protected void handleGenerateId() {
		execute(SetCommand.create(getEditingDomain(), this.input, SpdPackage.Literals.USES_DEVICE__ID, DceUuidUtil.createDceUUID()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Binding> bind(final DataBindingContext dataBindingContext, final EObject input) {
		final List<Binding> retVal = new ArrayList<Binding>();
		this.input = (UsesDevice) input;
		retVal.add(FormEntryBindingFactory.bind(dataBindingContext, this.client.getIdEntry(), getEditingDomain(), SpdPackage.Literals.USES_DEVICE__ID, input,
		        new EMFEmptyStringToNullUpdateValueStrategy(), null));
		retVal.add(FormEntryBindingFactory.bind(dataBindingContext, this.client.getTypeEntry(), getEditingDomain(), SpdPackage.Literals.USES_DEVICE__TYPE,
		        input, null, null));
		this.client.setEditable(SCAEditorUtil.isEditableResource(getPage(), input.eResource()));
		return retVal;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createSpecificContent(final Composite parent) {
		final FormToolkit toolkit = getManagedForm().getToolkit();

		createUsesDeviceSection(toolkit, parent);
	}

}
