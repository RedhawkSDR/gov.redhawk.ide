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
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.widgets.Composite;

import mil.jpeojtrs.sca.partitioning.PartitioningPackage;
import mil.jpeojtrs.sca.partitioning.Requires;

/**
 * Create the tree viewer for the {@link AbstractRequirementsPropertySection}
 * @since 2.0
 */
public class RequirementsPropertyComposite extends AbstractKeyValuePropertiesComposite {

	public RequirementsPropertyComposite(Composite parent, int style, int treeStyle) {
		super(parent, style, treeStyle);
	}

	@Override
	protected Command createSetCommand(Object element, String columnLabel, TransactionalEditingDomain editingDomain, Object value) {
		if (!(element instanceof Requires)) {
			return null;
		}

		Requires requires = (Requires) element;
		EAttribute eAttribute = columnLabel.equals(getKeyColumnLabel()) ? PartitioningPackage.Literals.REQUIRES__ID
			: PartitioningPackage.Literals.REQUIRES__VALUE;
		return SetCommand.create(editingDomain, requires, eAttribute, value.toString());
	}

	@Override
	protected String getElementId(Object element) {
		if (!(element instanceof Requires)) {
			return null;
		}
		return ((Requires) element).getId();
	}

	@Override
	protected Object getElementValue(Object element) {
		if (!(element instanceof Requires)) {
			return null;
		}
		return ((Requires) element).getValue();
	}

	@Override
	protected ITreeContentProvider getTreeViewerContentProvider() {
		return new RequirementsContentProvider();
	}

	@Override
	protected ITableLabelProvider getTreeViewerLabelProvider() {
		return new RequirementsLabelProvider();
	}
}
