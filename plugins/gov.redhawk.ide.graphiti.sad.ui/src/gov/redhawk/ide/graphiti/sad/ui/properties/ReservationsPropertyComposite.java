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
package gov.redhawk.ide.graphiti.sad.ui.properties;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;

import gov.redhawk.ide.graphiti.ui.properties.AbstractKeyValuePropertiesComposite;
import mil.jpeojtrs.sca.sad.Reservation;
import mil.jpeojtrs.sca.sad.SadPackage;

/**
 * Create the tree viewer for the {@link SadReservationsPropertySection}
 * @since 2.1
 */
public class ReservationsPropertyComposite extends AbstractKeyValuePropertiesComposite {

	private static final String[] SUPPORTED_KINDS = { "cpucores" };

	public ReservationsPropertyComposite(Composite parent, int style, int treeStyle) {
		super(parent, style, treeStyle);
	}

	@Override
	protected Command createSetCommand(Object element, String columnLabel, TransactionalEditingDomain editingDomain, Object value) {
		if (!(element instanceof Reservation)) {
			return null;
		}

		Reservation reservation = (Reservation) element;
		EAttribute eAttribute = columnLabel.equals(getKeyColumnLabel()) ? SadPackage.Literals.RESERVATION__KIND : SadPackage.Literals.RESERVATION__VALUE;
		return SetCommand.create(editingDomain, reservation, eAttribute, value.toString());
	}

	@Override
	protected String getElementId(Object element) {
		if (!(element instanceof Reservation)) {
			return null;
		}
		return ((Reservation) element).getKind();
	}

	@Override
	protected Object getElementValue(Object element) {
		if (!(element instanceof Reservation)) {
			return null;
		}
		return ((Reservation) element).getValue();
	}

	@Override
	protected CellEditor getCustomCellEditor(String columnLabel) {
		if (columnLabel.equals(getValueColumnLabel())) {
			return super.getCustomCellEditor(columnLabel);
		}

		// For our kind column, we want to use a combo cell editor that makes suggestions for supported values
		ComboBoxViewerCellEditor cellEditor = new ComboBoxViewerCellEditor(getTreeViewer().getTree()) {
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

				// This happens when the cell has a custom value it in. We need to set the text here, or the cell will
				// clear when selected
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
		cellEditor.setInput(SUPPORTED_KINDS);
		return cellEditor;
	}

	@Override
	protected String getKeyColumnLabel() {
		return "Kind";
	}

	@Override
	protected ITreeContentProvider getTreeViewerContentProvider() {
		return new ReservationsContentProvider();
	}

	@Override
	protected ITableLabelProvider getTreeViewerLabelProvider() {
		return new ReservationsLabelProvider();
	}
}
