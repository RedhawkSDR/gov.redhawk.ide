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
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeColumnViewerLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;

import mil.jpeojtrs.sca.partitioning.PartitioningPackage;
import mil.jpeojtrs.sca.partitioning.Requires;

/**
 * Create the tree viewer for the {@link AbstractRequirementsPropertySection}
 */
public class RequirementsPropertyComposite extends Composite {

	private TreeViewer treeViewer;
	private TreeColumnLayout treeColumnLayout;

	private static final String KEY_COLUMN_LABEL = "ID";
	private static final String VALUE_COLUMN_LABEL = "Value";

	public RequirementsPropertyComposite(Composite parent, int style, int treeStyle) {
		super(parent, style);
		this.setLayout(new GridLayout());
		this.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		init(this, treeStyle);
	}

	public void init(Composite parent, int treeStyle) {
		treeColumnLayout = new TreeColumnLayout();
		parent.setLayout(treeColumnLayout);

		treeViewer = createTreeViewer(parent, treeStyle);
		treeViewer.setContentProvider(new RequirementsContentProvider());
		ITableLabelProvider labelProvider = new RequirementsLabelProvider();
		treeViewer.setLabelProvider(labelProvider);

		createColumn(labelProvider, KEY_COLUMN_LABEL);
		createColumn(labelProvider, VALUE_COLUMN_LABEL);
	}

	private TreeViewer createTreeViewer(Composite parent, int style) {
		final Tree tree = new Tree(parent, style | SWT.FULL_SELECTION); // TODO: Do we want full_selection?
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		return new TreeViewer(tree);
	}

	private void createColumn(ITableLabelProvider labelProvider, String columnLabel) {
		TreeViewerColumn column = new TreeViewerColumn(treeViewer, SWT.None);
		treeViewer.getTree().setSortColumn(column.getColumn());
		column.getColumn().setMoveable(false);
		column.getColumn().setResizable(true);
		column.getColumn().setText(columnLabel);

		treeColumnLayout.setColumnData(column.getColumn(), new ColumnPixelData(250, column.getColumn().getResizable()));
		column.setLabelProvider(new TreeColumnViewerLabelProvider(labelProvider));
		column.setEditingSupport(createColumnEditingSupport(columnLabel));
	}

	// TODO: check if new value == old value, if so don't execute a command 
	private EditingSupport createColumnEditingSupport(final String columnLabel) {
		final TextCellEditor cellEditor = new TextCellEditor(treeViewer.getTree());

		EditingSupport editingSupport = new EditingSupport(treeViewer) {

			@Override
			protected void setValue(Object element, Object value) {
				// Null or cast check here?
				Requires requires = (Requires) element;
				// TODO: Talk to Dan about editingDomain's
				TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(requires);
				if (editingDomain != null) {
					EAttribute eAttribute = columnLabel.equals(KEY_COLUMN_LABEL) ? PartitioningPackage.Literals.REQUIRES__ID
						: PartitioningPackage.Literals.REQUIRES__VALUE;
					Command command = SetCommand.create(editingDomain, requires, eAttribute, value.toString());
					editingDomain.getCommandStack().execute(command);
					treeViewer.refresh();
				}
			}

			@Override
			protected Object getValue(Object element) {
				if (columnLabel.equals(KEY_COLUMN_LABEL)) {
					return ((Requires) element).getId();
				}

				return ((Requires) element).getValue();
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return cellEditor;
			}

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		};
		return editingSupport;
	}

	public TreeViewer getTreeViewer() {
		return treeViewer;
	}

}
