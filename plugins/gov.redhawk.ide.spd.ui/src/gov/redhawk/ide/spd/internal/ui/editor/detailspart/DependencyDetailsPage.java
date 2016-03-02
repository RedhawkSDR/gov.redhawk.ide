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
import gov.redhawk.ide.spd.internal.ui.editor.composite.DependencyComposite;
import gov.redhawk.ui.editor.ScaDetails;
import gov.redhawk.ui.parts.FormEntryBindingFactory;
import gov.redhawk.ui.util.EMFEmptyStringToNullUpdateValueStrategy;

import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.spd.SpdPackage;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * 
 */
public class DependencyDetailsPage extends ScaDetails {
	private final ImplementationsSection fSection;
	private DependencyComposite dependencyComposite;

	/**
	 * The Constructor.
	 * 
	 * @param fSection the f section
	 */
	public DependencyDetailsPage(final ImplementationsSection fSection) {
		super(fSection.getPage());
		this.fSection = fSection;
	}

	/**
	 * Creates the uses device section.
	 * 
	 * @param toolkit the toolkit
	 * @param parent the parent
	 */
	private void createDependencySection(final FormToolkit toolkit, final Composite parent) {
		final Section section = toolkit.createSection(parent, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR
		        | ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED);
		section.clientVerticalSpacing = FormLayoutFactory.SECTION_HEADER_VERTICAL_SPACING;
		section.setText("Dependency");
		section.setDescription("The dependency element is used to "
		        + "indicate the dependent relationships between the resources being"
		        + " delivered and other resources in the system.");

		section.setLayout(FormLayoutFactory.createClearGridLayout(false, 1));
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));

		// Align the master and details section headers (misalignment caused
		// by section toolbar icons)
		getPage().alignSectionHeaders(this.fSection.getSection(), section);

		this.dependencyComposite = new DependencyComposite(section, SWT.NULL, toolkit);
		section.setClient(this.dependencyComposite);
		toolkit.adapt(this.dependencyComposite);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Binding> bind(final DataBindingContext dataBindingContext, final EObject input) {
		final ArrayList<Binding> retVal = new ArrayList<Binding>();
		retVal.add(FormEntryBindingFactory.bind(dataBindingContext, this.dependencyComposite.getTypeEntry(),
		        getEditingDomain(), SpdPackage.Literals.DEPENDENCY__TYPE, input,
		        new EMFEmptyStringToNullUpdateValueStrategy(), null));
		return retVal;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createSpecificContent(final Composite parent) {
		final FormToolkit toolkit = getManagedForm().getToolkit();

		createDependencySection(toolkit, parent);
	}

}
