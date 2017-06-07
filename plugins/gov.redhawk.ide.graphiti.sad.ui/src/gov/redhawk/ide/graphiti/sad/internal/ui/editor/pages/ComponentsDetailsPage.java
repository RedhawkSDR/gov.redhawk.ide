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
package gov.redhawk.ide.graphiti.sad.internal.ui.editor.pages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import gov.redhawk.common.ui.editor.FormLayoutFactory;
import gov.redhawk.ui.editor.ScaDetails;
import gov.redhawk.ui.parts.FormEntryBindingFactory;
import gov.redhawk.ui.util.EMFEmptyStringToNullUpdateValueStrategy;
import gov.redhawk.ui.util.SCAEditorUtil;
import mil.jpeojtrs.sca.partitioning.ComponentPlacement;
import mil.jpeojtrs.sca.partitioning.NamingService;
import mil.jpeojtrs.sca.partitioning.PartitioningPackage;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

public class ComponentsDetailsPage extends ScaDetails {

	private SadComponentComposite componentComposite;

	public ComponentsDetailsPage(final ComponentsSection section) {
		super(section.getPage());
	}

	@Override
	protected void createSpecificContent(Composite parent) {
		final FormToolkit toolkit = getManagedForm().getToolkit();
		createComponentSection(toolkit, parent);
	}

	private void createComponentSection(final FormToolkit toolkit, final Composite parent) {
		final Section section = toolkit.createSection(parent,
			Section.DESCRIPTION | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED);
		section.clientVerticalSpacing = FormLayoutFactory.SECTION_HEADER_VERTICAL_SPACING;
		section.setText("Component");
		section.setDescription("This allows you to edit particular details of the selected component");
		section.setLayout(FormLayoutFactory.createClearGridLayout(false, 1));
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));

		this.componentComposite = new SadComponentComposite(section, SWT.NONE, toolkit, this.getEditor());
		toolkit.adapt(this.componentComposite);

		section.setClient(this.componentComposite);
	}

	/**
	 * Bind composite fields with model elements
	 */
	@Override
	protected List<Binding> bind(DataBindingContext context, EObject input) {
		if (!(input instanceof SadComponentInstantiation)) {
			return null;
		}

		SadComponentInstantiation compInst = (SadComponentInstantiation) input;

		NamingService namingService = null;
		if (compInst.getFindComponent() != null) {
			namingService = compInst.getFindComponent().getNamingService();
		}

		SoftPkg softPkg = null;
		ComponentPlacement< ? > placement = compInst.getPlacement();
		if (placement.getComponentFileRef() != null && placement.getComponentFileRef().getFile() != null) {
			softPkg = placement.getComponentFileRef().getFile().getSoftPkg();
		}
		if (softPkg == null || softPkg.eIsProxy()) {
			return Collections.emptyList();
		}

		boolean isAssemblyController = false;
		if (SoftwareAssembly.Util.isAssemblyController(compInst)) {
			isAssemblyController = true;
		}

		final List<Binding> retVal = new ArrayList<>();

		// If we change the component ID, and that component is the assembly controller, we need to update that as well
		retVal.add(FormEntryBindingFactory.bind(context, this.componentComposite.getIdEntry(), getEditingDomain(),
			PartitioningPackage.Literals.COMPONENT_INSTANTIATION__ID, compInst, new EMFEmptyStringToNullUpdateValueStrategy(), null));

		if (isAssemblyController) {
			SoftwareAssembly sad = ScaEcoreUtils.getEContainerOfType(compInst, SoftwareAssembly.class);
			retVal.add(FormEntryBindingFactory.bind(context, this.componentComposite.getIdEntry(), getEditingDomain(),
				PartitioningPackage.Literals.COMPONENT_INSTANTIATION_REF__REFID, sad.getAssemblyController().getComponentInstantiationRef(),
				new EMFEmptyStringToNullUpdateValueStrategy(), null));
		}

		// If we change the usage name, also update the naming service to match
		retVal.add(FormEntryBindingFactory.bind(context, this.componentComposite.getNameEntry(), getEditingDomain(),
			PartitioningPackage.Literals.COMPONENT_INSTANTIATION__USAGE_NAME, compInst, new EMFEmptyStringToNullUpdateValueStrategy(), null));

		if (namingService != null) {
			retVal.add(FormEntryBindingFactory.bind(context, this.componentComposite.getNameEntry(), getEditingDomain(),
				PartitioningPackage.Literals.NAMING_SERVICE__NAME, namingService, new EMFEmptyStringToNullUpdateValueStrategy(), null));
		}

		this.componentComposite.setEditable(SCAEditorUtil.isEditableResource(getPage(), compInst.eResource()));
		return retVal;
	}

}
