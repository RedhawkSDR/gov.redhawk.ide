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
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeColumnViewerLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;

/**
 * @since 2.0
 */
public abstract class AbstractKeyValuePropertiesComposite extends Composite {

	private TreeViewer treeViewer;
	private TreeColumnLayout treeColumnLayout;

	private final String keyColumnLabel;
	private final String valueColumnLabel;

	/**
	 * @return String to be used as the 'Key' value for the tree item
	 */
	protected abstract String getElementId(Object element);

	/**
	 * @return String to be used as the 'Value' value for the tree item
	 */
	protected abstract Object getElementValue(Object element);

	/**
	 * @return {@link ITreeContentProvider} for the treeViewer
	 */
	protected abstract ITreeContentProvider getTreeViewerContentProvider();

	/**
	 * @return {@link ITableLabelProvider} for the treeViewer
	 */
	protected abstract ITableLabelProvider getTreeViewerLabelProvider();

	/**
	 * @param editingDomain
	 * @return {@link SetCommand} for adding a new element to the model
	 */
	protected abstract Command createSetCommand(Object element, String columnLabel, TransactionalEditingDomain editingDomain, Object value);

	public AbstractKeyValuePropertiesComposite(Composite parent, int style, int treeStyle) {
		super(parent, style);
		keyColumnLabel = getKeyColumnLabel();
		valueColumnLabel = getValueColumnLabel();

		this.setLayout(new GridLayout());
		this.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		init(this, treeStyle);
	}

	private void init(Composite parent, int treeStyle) {
		treeColumnLayout = new TreeColumnLayout();
		parent.setLayout(treeColumnLayout);

		treeViewer = createTreeViewer(parent, treeStyle);
		treeViewer.setContentProvider(getTreeViewerContentProvider());
		ITableLabelProvider labelProvider = getTreeViewerLabelProvider();
		treeViewer.setLabelProvider(labelProvider);

		createColumn(labelProvider, keyColumnLabel);
		createColumn(labelProvider, valueColumnLabel);
	}

	/**
	 * Creates the {@link TreeViewer} for the tree composite
	 * @return {@link TreeViewer}
	 */
	private TreeViewer createTreeViewer(Composite parent, int style) {
		final Tree tree = new Tree(parent, style | SWT.FULL_SELECTION);
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		return new TreeViewer(tree);
	}

	/**
	 * Creates a default style {@link TreeViewerColumn} with {@link TextCellEditor} editing support
	 * @param labelProvider
	 * @param columnLabel
	 */
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

	/**
	 * Creates the editing support for the composites columns. Defaults to {@link TextCellEditor}. Sub-classes should
	 * override this to
	 * provide support additional cell editor types, keying off the columnLabel parameter.
	 * @param columnLabel - either KEY_COLUMN_LABEL or VALUE_COLUMN_LABEL
	 * @return
	 */
	protected EditingSupport createColumnEditingSupport(final String columnLabel) {
		EditingSupport editingSupport = new EditingSupport(treeViewer) {

			@Override
			protected void setValue(Object element, Object value) {
				TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(element);
				if (editingDomain != null) {
					Command command = createSetCommand(element, columnLabel, editingDomain, value);
					if (command != null) {
						editingDomain.getCommandStack().execute(command);
						treeViewer.refresh();
					}
				}
			}

			@Override
			protected Object getValue(Object element) {
				// Key Column value
				if (columnLabel.equals(keyColumnLabel)) {
					return getElementId(element);
				}

				// Value Column value
				return getElementValue(element);
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return getCustomCellEditor(columnLabel);
			}

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		};
		return editingSupport;
	}

	/**
	 * Allows implementation of custom cell editor behavior on a per-column basis.
	 * By default returns a generic {@link TextCellEditor}
	 */
	protected CellEditor getCustomCellEditor(String columnLabel) {
		return new TextCellEditor(treeViewer.getTree());
	};

	protected String getKeyColumnLabel() {
		return "ID";
	}

	protected String getValueColumnLabel() {
		return "Value";
	}

	public TreeViewer getTreeViewer() {
		return treeViewer;
	}
}
