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
package gov.redhawk.ide.sdr.internal.ui;

import gov.redhawk.eclipsecorba.library.IdlLibrary;
import gov.redhawk.eclipsecorba.library.LibraryPackage;
import gov.redhawk.sca.ui.EventDetailsDialog;
import gov.redhawk.sca.ui.RedhawkUiAdapterFactory;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.ui.provider.PropertyDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource2;

public class IdlLibraryAdapterFactory extends RedhawkUiAdapterFactory {

	private class IdlLibraryPropertyDescriptor extends PropertyDescriptor {

		public IdlLibraryPropertyDescriptor(final Object object, final IItemPropertyDescriptor itemPropertyDescriptor) {
			super(object, itemPropertyDescriptor);
		}

		@Override
		public CellEditor createPropertyEditor(final Composite composite) {
			final Object genericFeature = this.itemPropertyDescriptor.getFeature(this.object);
			if (genericFeature != LibraryPackage.Literals.IDL_LIBRARY__LOAD_STATUS) {
				return super.createPropertyEditor(composite);
			}
			return new DialogCellEditor(composite) {

				private Label label;
				private Button resultButton;

				@Override
				protected Object openDialogBox(final Control cellEditorWindow) {
					final IStatus status = ((IdlLibrary) IdlLibraryPropertyDescriptor.this.object).getLoadStatus();
					final EventDetailsDialog dialog = new EventDetailsDialog(cellEditorWindow.getShell(), status);
					dialog.open();
					return null;
				}

				@Override
				protected Button createButton(final Composite parent) {
					this.resultButton = new Button(parent, SWT.DOWN);
					this.resultButton.setText("Details"); //$NON-NLS-1$
					return this.resultButton;
				}

				@Override
				protected Control createContents(final Composite cell) {
					this.label = new Label(cell, SWT.LEFT);
					this.label.setFont(cell.getFont());
					this.label.setBackground(cell.getBackground());
					return this.label;
				}

				@Override
				protected void updateContents(final Object value) {
					//					final IStatus status = ((IdlLibrary) IdlLibraryPropertyDescriptor.this.object).getLoadStatus();
					if (value != null) {
						if (this.resultButton != null) {
							this.resultButton.setEnabled(!((IStatus) value).isOK());
						}
						this.label.setText(getEditLabelProvider().getText(value));
					} else {
						if (this.resultButton != null) {
							this.resultButton.setEnabled(false);
						}
						this.label.setText("");
					}
				}

			};
		}
	}

	private class IdlLibraryPropertySource extends ScaPropertySource {

		public IdlLibraryPropertySource(final Object object, final IItemPropertySource itemPropertySource) {
			super(object, itemPropertySource);
		}

		@Override
		protected IPropertyDescriptor createPropertyDescriptor(final IItemPropertyDescriptor itemPropertyDescriptor) {
			return new IdlLibraryPropertyDescriptor(this.object, itemPropertyDescriptor);
		}

	}

	@Override
	protected IPropertySource2 createPropertySource(final Object adaptableObject, final IItemPropertySource itemPropertySource) {
		return new IdlLibraryPropertySource(adaptableObject, itemPropertySource);
	}

}
