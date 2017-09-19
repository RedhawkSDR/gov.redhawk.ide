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
package gov.redhawk.ide.graphiti.ui.properties;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.DeleteCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;

import mil.jpeojtrs.sca.dcd.DcdPackage;
import mil.jpeojtrs.sca.partitioning.PartitioningFactory;
import mil.jpeojtrs.sca.partitioning.PartitioningPackage;
import mil.jpeojtrs.sca.partitioning.Requirements;
import mil.jpeojtrs.sca.partitioning.Requires;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadPackage;

/**
 * An abstract implementation of component instantiations requirements property section.
 * This section displays either the device_requires or deployer_requires values of the selection.
 * Add/remove buttons handle adding/removing requires elements from the selection.
 * @since 2.0
 */
public abstract class AbstractRequirementsPropertySection extends AbstractKeyValuePropertiesSection {

	/**
	 * Get either the {@link Requirements} model element for the selected component or device, depending on context
	 * @return
	 */
	protected abstract Requirements getSelectionRequirements();

	@Override
	protected AbstractKeyValuePropertiesComposite getTreeComposite(Composite parent, int style, int treeStyle) {
		return new RequirementsPropertyComposite(parent, style, treeStyle);
	}

	/**
	 * Add a new {@link Requires} element to the component instantiation
	 */
	protected Command createAddCommand() {
		CompoundCommand command = new CompoundCommand();
		Requirements requirements = getSelectionRequirements();

		// Need to create a requirements section if it doesn't exist
		if (requirements == null) {
			requirements = PartitioningFactory.eINSTANCE.createRequirements();

			EReference eRef = null;
			if (getEObject() instanceof SadComponentInstantiation) {
				eRef = SadPackage.Literals.SAD_COMPONENT_INSTANTIATION__DEVICE_REQUIRES;
			} else {
				eRef = DcdPackage.Literals.DCD_COMPONENT_INSTANTIATION__DEPLOYER_REQUIRES;
			}

			Command addRequirementsCommand = SetCommand.create(getEditingDomain(), getEObject(), eRef, requirements);
			command.append(addRequirementsCommand);
		}

		Requires newRequires = PartitioningFactory.eINSTANCE.createRequires();
		newRequires.setId("<Enter ID>");
		newRequires.setValue("<Enter Value>");

		Command addRequiresCommand = AddCommand.create(getEditingDomain(), requirements, PartitioningPackage.Literals.REQUIREMENTS__REQUIRES, newRequires);
		command.append(addRequiresCommand);

		return command;
	}

	/**
	 * Remove a {@link Requires} element from the component instantiation. Delete the {@link Requirements} element if it
	 * is now empty.
	 */
	protected Command createRemoveCommand() {

		Requirements requirements = getSelectionRequirements();
		// If the last element is removed, the requirements section also needs to be removed
		if (requirements.getRequires().size() <= 1) {
			return DeleteCommand.create(getEditingDomain(), requirements);
		}

		Requires requires = (Requires) ((StructuredSelection) getTreeViewer().getSelection()).getFirstElement();
		return RemoveCommand.create(getEditingDomain(), requirements, PartitioningPackage.Literals.REQUIREMENTS__REQUIRES, requires);
	}

	@Override
	protected String getToolTipSuffix() {
		return "Requires";
	}
}
