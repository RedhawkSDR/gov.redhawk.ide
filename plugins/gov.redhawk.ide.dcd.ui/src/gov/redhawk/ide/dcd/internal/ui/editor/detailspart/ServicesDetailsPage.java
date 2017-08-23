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
package gov.redhawk.ide.dcd.internal.ui.editor.detailspart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import gov.redhawk.common.ui.editor.FormLayoutFactory;
import gov.redhawk.ide.dcd.internal.ui.editor.DevicesPage;
import gov.redhawk.ide.dcd.internal.ui.editor.DevicesSection;
import gov.redhawk.ide.dcd.internal.ui.editor.composite.ServiceDetailsComposite;
import gov.redhawk.ui.editor.ScaDetails;
import gov.redhawk.ui.parts.FormEntryBindingFactory;
import gov.redhawk.ui.util.EMFEmptyStringToNullUpdateValueStrategy;
import gov.redhawk.ui.util.SCAEditorUtil;
import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;
import mil.jpeojtrs.sca.dcd.DcdComponentPlacement;
import mil.jpeojtrs.sca.partitioning.PartitioningPackage;
import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.prf.SimpleRef;
import mil.jpeojtrs.sca.prf.SimpleSequenceRef;
import mil.jpeojtrs.sca.prf.StructRef;
import mil.jpeojtrs.sca.prf.StructSequenceRef;
import mil.jpeojtrs.sca.spd.SoftPkg;

/**
 * The Class ImplementationDetailsPage.
 */
public class ServicesDetailsPage extends ScaDetails {

	private ServiceDetailsComposite serviceComposite;
	private final DevicesSection fSection;
	private SoftPkg softPkg;
	private DcdComponentInstantiation input;
	private DcdComponentPlacement inputPlacement;

	/**
	 * The Constructor.
	 * 
	 * @param fSection the f section
	 */
	public ServicesDetailsPage(final DevicesSection fSection) {
		super(fSection.getPage());
		this.fSection = fSection;
	}

	public DataBindingContext getContext() {
		return getPage().getEditor().getDataBindingContext();
	}

	/**
	 * Creates the implementation section.
	 * 
	 * @param toolkit the toolkit
	 * @param parent the parent
	 */
	private void createServiceSection(final FormToolkit toolkit, final Composite parent) {
		final Section section = toolkit.createSection(parent,
			Section.DESCRIPTION | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED);
		section.clientVerticalSpacing = FormLayoutFactory.SECTION_HEADER_VERTICAL_SPACING;
		section.setText("Service Details");
		section.setDescription("This allows you to override particular properties of the selected service");
		section.setLayout(FormLayoutFactory.createClearGridLayout(false, 1));
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));

		// Align the master and details section headers (misalignment caused by section toolbar icons)
		getPage().alignSectionHeaders(this.fSection.getSection(), section);

		this.serviceComposite = getComposite(section, toolkit);
		toolkit.adapt(this.serviceComposite);

		section.setClient(this.serviceComposite);

	}

	protected ServiceDetailsComposite getComposite(Section section, FormToolkit toolkit) {
		return new ServiceDetailsComposite(section, SWT.NONE, toolkit, this.getEditor(), false);		
	}

	/**
	 * Execute.
	 * 
	 * @param command the command
	 */
	@Override
	public void execute(final Command command) {
		getEditingDomain().getCommandStack().execute(command);
	}

	@Override
	public DevicesPage getPage() {
		return (DevicesPage) super.getPage();
	}

	@Override
	protected List<Binding> bind(final DataBindingContext context, final EObject obj) {
		if (!(obj instanceof DcdComponentInstantiation)) {
			return null;
		}
		this.input = (DcdComponentInstantiation) obj;
		this.inputPlacement = (DcdComponentPlacement) input.getPlacement();

		if (this.inputPlacement.getComponentFileRef().getFile() != null) {
			this.softPkg = this.inputPlacement.getComponentFileRef().getFile().getSoftPkg();
		}

		if ((this.softPkg == null) || (this.softPkg.eIsProxy())) {
			return Collections.emptyList();
		}

		final List<Binding> retVal = new ArrayList<Binding>();
		retVal.add(FormEntryBindingFactory.bind(context, this.serviceComposite.getNameEntry(), getEditingDomain(),
			PartitioningPackage.Literals.COMPONENT_INSTANTIATION__USAGE_NAME, this.input, new EMFEmptyStringToNullUpdateValueStrategy(), null));

		this.serviceComposite.setInput(this.input);
		this.serviceComposite.setEditable(SCAEditorUtil.isEditableResource(getPage(), this.inputPlacement.eResource()));

		return retVal;
	}

	@Override
	protected void createSpecificContent(final Composite parent) {
		final FormToolkit toolkit = getManagedForm().getToolkit();

		createServiceSection(toolkit, parent);

	}

	/**
	 * This converts a PropertyRef to a Literals reference of the appropriate type.
	 * 
	 * @param property the property to map
	 * @return the DcdPackage.Literals corresponding to the property
	 */
	protected EReference getTypeLiteral(final EObject property) {
		if (property instanceof SimpleRef) {
			return PartitioningPackage.Literals.COMPONENT_PROPERTIES__SIMPLE_REF;
		} else if (property instanceof SimpleSequenceRef) {
			return PartitioningPackage.Literals.COMPONENT_PROPERTIES__SIMPLE_SEQUENCE_REF;
		} else if (property instanceof StructRef) {
			return PartitioningPackage.Literals.COMPONENT_PROPERTIES__STRUCT_REF;
		} else if (property instanceof StructSequenceRef) {
			return PartitioningPackage.Literals.COMPONENT_PROPERTIES__STRUCT_SEQUENCE_REF;
		}
		return null;
	}

	/**
	 * This converts a PropertyRef's ID to a Literals reference of the appropriate type.
	 * 
	 * @param property the property to map
	 * @return the DcdPackage.Literals corresponding to the property's ID
	 */
	protected EAttribute getIdLiteral(final EObject property) {
		return PrfPackage.Literals.ABSTRACT_PROPERTY_REF__REF_ID;
	}

	/**
	 * This converts a PropertyRef's value to a Literals reference of the appropriate type.
	 * 
	 * @param property the property to map
	 * @return the DcdPackage.Literals corresponding to the property's value
	 */
	protected EObject getValueLiteral(final EObject property) {
		if (property instanceof SimpleRef) {
			return PrfPackage.Literals.SIMPLE_REF__VALUE;
		} else if (property instanceof SimpleSequenceRef) {
			return PrfPackage.Literals.SIMPLE_SEQUENCE_REF__VALUES;
		} else if (property instanceof StructRef) {
			return PrfPackage.Literals.STRUCT_REF__SIMPLE_REF;
		} else if (property instanceof StructSequenceRef) {
			return PrfPackage.Literals.STRUCT_SEQUENCE_REF__STRUCT_VALUE;
		}
		return null;
	}
}
