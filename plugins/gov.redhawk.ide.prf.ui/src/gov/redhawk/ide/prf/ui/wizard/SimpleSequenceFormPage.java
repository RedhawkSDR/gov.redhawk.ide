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
package gov.redhawk.ide.prf.ui.wizard;

import gov.redhawk.ui.editor.SCAFormEditor;
import mil.jpeojtrs.sca.prf.AccessType;
import mil.jpeojtrs.sca.prf.PrfFactory;
import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.prf.PropertyValueType;
import mil.jpeojtrs.sca.prf.SimpleSequence;
import mil.jpeojtrs.sca.util.DceUuidUtil;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.emf.databinding.EMFObservables;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * 
 */
public class SimpleSequenceFormPage extends Composite {
	private static final int NUM_COLUMNS = 3;
	private Text idText;
	private ComboViewer typeViewer;
	private Text nameText;
	private ComboViewer modeViewer;
	private final SimpleSequence simpleSequence = PrfFactory.eINSTANCE.createSimpleSequence();
	private final EMFDataBindingContext context = new EMFDataBindingContext();

	/**
	 * The Constructor.
	 * 
	 * @param parent the parent
	 */
	public SimpleSequenceFormPage(final Composite parent) {
		super(parent, SWT.NULL);
		createControls();
	}

	/**
	 * Gets the binding context.
	 * 
	 * @return the binding context
	 */
	public DataBindingContext getBindingContext() {
		return this.context;
	}

	/**
	 * @return the simpleSequence
	 */
	public SimpleSequence getSimpleSequence() {
		return this.simpleSequence;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		this.context.dispose();
		super.dispose();
	}

	/**
	 * Creates the simple property config page.
	 * 
	 * @return the composite
	 */
	private Composite createControls() {
		final Composite client = this;
		client.setLayout(new GridLayout(SimpleSequenceFormPage.NUM_COLUMNS, false));
		Label label;
		Button button;
		GridData data;

		label = new Label(client, SWT.None);
		label.setText("ID:");
		this.idText = new Text(client, SWT.BORDER);
		this.idText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
		this.context.bindValue(WidgetProperties.text(SWT.Modify).observeDelayed(SCAFormEditor.getFieldBindingDelay(), this.idText),
		        EMFObservables.observeValue(this.simpleSequence, PrfPackage.Literals.ABSTRACT_PROPERTY__ID),
		        null,
		        null);
		button = new Button(client, SWT.PUSH);
		button.setText("Generate");
		button.addSelectionListener(new SelectionAdapter() {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(final SelectionEvent e) {
				SimpleSequenceFormPage.this.simpleSequence.setId(DceUuidUtil.createDceUUID());
			}
		});

		label = new Label(client, SWT.None);
		label.setText("Type:");
		this.typeViewer = new ComboViewer(client, SWT.None);
		this.typeViewer.setContentProvider(new ArrayContentProvider());
		this.typeViewer.setLabelProvider(new LabelProvider());
		this.typeViewer.setInput(PropertyValueType.values());
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		data.horizontalSpan = 2;
		this.typeViewer.getControl().setLayoutData(data);
		this.context.bindValue(ViewersObservables.observeSingleSelection(this.typeViewer),
		        EMFObservables.observeValue(this.simpleSequence, PrfPackage.Literals.SIMPLE_SEQUENCE__TYPE),
		        null,
		        null);

		label = new Label(client, SWT.None);
		label.setText("Name:");
		this.nameText = new Text(client, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		data.horizontalSpan = 2;
		this.nameText.setLayoutData(data);
		this.context.bindValue(WidgetProperties.text(SWT.Modify).observeDelayed(SCAFormEditor.getFieldBindingDelay(), this.nameText),
		        EMFObservables.observeValue(this.simpleSequence, PrfPackage.Literals.ABSTRACT_PROPERTY__NAME),
		        null,
		        null);

		label = new Label(client, SWT.None);
		label.setText("Mode:");
		this.modeViewer = new ComboViewer(client, SWT.None);
		this.modeViewer.setContentProvider(new ArrayContentProvider());
		this.modeViewer.setLabelProvider(new LabelProvider());
		this.modeViewer.setInput(AccessType.values());
		this.modeViewer.setSelection(new StructuredSelection(AccessType.READWRITE));
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		data.horizontalSpan = 2;
		this.modeViewer.getControl().setLayoutData(data);
		this.context.bindValue(ViewersObservables.observeSingleSelection(this.modeViewer),
		        EMFObservables.observeValue(this.simpleSequence, PrfPackage.Literals.ABSTRACT_PROPERTY__MODE),
		        null,
		        null);

		return client;
	}

}
