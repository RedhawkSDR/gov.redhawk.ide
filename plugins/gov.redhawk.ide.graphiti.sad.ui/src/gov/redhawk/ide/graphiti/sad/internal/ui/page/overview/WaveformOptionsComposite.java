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

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.widgets.FormToolkit;

import gov.redhawk.ui.editor.IScaComposite;
import mil.jpeojtrs.sca.sad.Option;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.provider.OptionItemProvider;
import mil.jpeojtrs.sca.sad.provider.SadItemProviderAdapterFactory;

public class WaveformOptionsComposite extends Composite implements IScaComposite {

	private static final String KEY_COLUMN_LABEL = "Key";
	private static final String VALUE_COLUMN_LABEL = "Value";
	private static final String[] SUPPORTED_NAMES = new String[] { "AWARE_APPLICATION", "STOP_TIMEOUT" };

	private FormToolkit toolkit;
	private TableViewer tableViewer;

	public WaveformOptionsComposite(Composite parent, int style, FormToolkit toolkit) {
		super(parent, style);
		this.toolkit = toolkit;

		this.setLayout(new GridLayout(1, false));
		this.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		createTable(this);
	}

	private void createTable(Composite parent) {
		final Table optionsTable = this.toolkit.createTable(parent, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		optionsTable.setHeaderVisible(true);
		optionsTable.setLayoutData(GridDataFactory.fillDefaults().hint(50, 100).grab(true, true).create());

		final TableLayout tableLayout = new TableLayout();
		tableLayout.addColumnData(new ColumnWeightData(50, 10, true));
		tableLayout.addColumnData(new ColumnWeightData(50, 10, true));
		optionsTable.setLayout(tableLayout);
		this.tableViewer = new TableViewer(optionsTable);

		// Create Key Column
		final TableViewerColumn column1 = new TableViewerColumn(this.tableViewer, SWT.LEFT);
		column1.getColumn().setText(KEY_COLUMN_LABEL);
		column1.getColumn().setResizable(true);
		column1.setEditingSupport(createColumnEditingSupport(KEY_COLUMN_LABEL));

		// Create Value Column
		final TableViewerColumn column2 = new TableViewerColumn(this.tableViewer, SWT.LEFT);
		column2.getColumn().setText(VALUE_COLUMN_LABEL);
		column2.getColumn().setResizable(true);
		column2.setEditingSupport(createColumnEditingSupport(VALUE_COLUMN_LABEL));

		AdapterFactory adapterFactory = new OptionsAdapterFactory();
		this.tableViewer.setContentProvider(new AdapterFactoryContentProvider(adapterFactory));
		this.tableViewer.setLabelProvider(new AdapterFactoryLabelProvider(adapterFactory));

	}

	/**
	 * Creates the editing support for the composites columns. Defaults to {@link TextCellEditor}. Sub-classes should
	 * override this to provide support additional cell editor types, keying off the columnLabel parameter.
	 * @param columnLabel - either KEY_COLUMN_LABEL or VALUE_COLUMN_LABEL
	 * @return
	 */
	protected EditingSupport createColumnEditingSupport(final String columnLabel) {
		EditingSupport editingSupport = new EditingSupport(tableViewer) {

			@Override
			protected void setValue(Object element, Object value) {
				TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(element);
				if (editingDomain != null) {
					Option option = (Option) element;
					EAttribute eAttribute = KEY_COLUMN_LABEL.equals(columnLabel) ? SadPackage.Literals.OPTION__NAME : SadPackage.Literals.OPTION__VALUE;
					Command command = SetCommand.create(editingDomain, option, eAttribute, value.toString());
					if (command != null) {
						editingDomain.getCommandStack().execute(command);
						tableViewer.refresh();
					}
				}
			}

			@Override
			protected Object getValue(Object element) {
				if (!(element instanceof Option)) {
					return null;
				}
				// Key Column value
				if (KEY_COLUMN_LABEL.equals(columnLabel)) {
					return ((Option) element).getName();
				}

				// Value Column value
				return ((Option) element).getValue();
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				if (VALUE_COLUMN_LABEL.equals(columnLabel)) {
					return new TextCellEditor(tableViewer.getTable());
				}
				// For our kind column, we want to use a combo cell editor that makes suggestions for supported values
				ComboBoxViewerCellEditor cellEditor = new ComboBoxViewerCellEditor(getTableViewer().getTable()) {
					protected Object doGetValue() {
						// User entered a custom value, so return that
						if (super.doGetValue() == null) {
							return this.getViewer().getCCombo().getText();
						}

						return super.doGetValue();
					};

					@Override
					protected void doSetValue(Object value) {
						if (value == null) {
							getViewer().setSelection(StructuredSelection.EMPTY);
						} else {
							getViewer().setSelection(new StructuredSelection(value));
						}

						// This happens when the cell has a custom value it in. We need to set the text here, or the
						// cell will clear when selected
						ISelection newSelection = getViewer().getSelection();
						if (newSelection.isEmpty()) {
							getViewer().getCCombo().setText(value.toString());
						}
					}
				};

				cellEditor.setContentProvider(new IStructuredContentProvider() {
					@Override
					public Object[] getElements(Object inputElement) {
						return (Object[]) inputElement;
					}
				});

				cellEditor.setInput(SUPPORTED_NAMES);
				return cellEditor;
			}

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		};
		return editingSupport;
	}

	public TableViewer getTableViewer() {
		return tableViewer;
	}

	@Override
	public void setEditable(boolean canEdit) {

	}

	/**
	 * Used for the options table in the SAD editor overview tab
	 */
	private class OptionsAdapterFactory extends SadItemProviderAdapterFactory {
		@Override
		public Adapter createOptionAdapter() {
			if (optionItemProvider == null) {
				optionItemProvider = new WaveformOptionItemProvider(this);
			}

			return optionItemProvider;
		}
	}

	/**
	 * Used for the options table in the SAD editor overview tab
	 */
	private class WaveformOptionItemProvider extends OptionItemProvider {

		public WaveformOptionItemProvider(AdapterFactory adapterFactory) {
			super(adapterFactory);
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof Option) {
				Option option = (Option) element;
				switch (columnIndex) {
				case 0:
					return option.getName();
				case 1:
					return option.getValue();
				default:
					break;
				}
			}
			return "";
		}

	}

}
