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
import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;

import gov.redhawk.common.ui.editor.FormLayoutFactory;
import gov.redhawk.common.ui.parts.FormEntry;
import gov.redhawk.logging.ui.LogLevels;
import gov.redhawk.ui.editor.IScaComposite;

public class SadComponentComposite extends Composite implements IScaComposite {
	private static final int NUM_COLUMNS = 3;

	private final FormToolkit toolkit;
	private FormEntry idEntry;
	private FormEntry nameEntry;
	private FormEntry loggingUri;
	private ComboViewer levelViewer;

	private Button enableLoggingButton;

	public SadComponentComposite(final Composite parent, final int style, final FormToolkit toolkit, final IEditingDomainProvider editingDomainProvider) {
		super(parent, style);

		this.toolkit = toolkit;
		setLayout(FormLayoutFactory.createSectionClientGridLayout(false, SadComponentComposite.NUM_COLUMNS));

		createIdEntry();
		createNameEntry();
		createLoggingConfigEntry();

		toolkit.paintBordersFor(this);
	}

	private void createLoggingConfigEntry() {
		// Logging Config
		Label loggingConfigLabel = toolkit.createLabel(this, "Logging Configuration:");
		loggingConfigLabel.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		loggingConfigLabel.setLayoutData(GridDataFactory.fillDefaults().span(1, 1).create());
		this.enableLoggingButton = toolkit.createButton(this, "Enable", SWT.CHECK);
		this.enableLoggingButton.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).create());

		toolkit.createLabel(this, "");
		Composite loggingComposite = toolkit.createComposite(this);
		loggingComposite.setLayout(FormLayoutFactory.createSectionClientGridLayout(false, 3));
		loggingComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Log Level
		Label levelLabel = toolkit.createLabel(loggingComposite, "Log Level:");
		levelLabel.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		levelLabel.setLayoutData(GridDataFactory.fillDefaults().create());
		this.levelViewer = new ComboViewer(loggingComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		toolkit.adapt(this.levelViewer.getCombo());
		this.levelViewer.getControl().setLayoutData(GridDataFactory.fillDefaults().span(2, 1).grab(true, false).create());
		this.levelViewer.setContentProvider(new ArrayContentProvider());
		this.levelViewer.setLabelProvider(new LabelProvider());

		List<Object> logLevels = new ArrayList<>();
		logLevels.addAll(Arrays.asList(LogLevels.values()));
		logLevels.add(0, ""); // Add blank entry to allow unsetting log level
		this.levelViewer.setInput(logLevels);

		// Logging URI
		this.loggingUri = new FormEntry(loggingComposite, this.toolkit, "Logging URI:", SWT.SINGLE);

		toolkit.paintBordersFor(loggingComposite);
	}

	/**
	 * Text field for Component ID
	 */
	private void createIdEntry() {
		this.idEntry = new FormEntry(this, this.toolkit, "Component ID:", SWT.SINGLE);
	}

	/**
	 * Text field for Component Usage Name / Naming Service Name (edits here change both)
	 */
	private void createNameEntry() {
		this.nameEntry = new FormEntry(this, this.toolkit, "Usage Name:", SWT.SINGLE);
		this.nameEntry.getText().setToolTipText("Human readable name for the component instantiation");
	}

	public FormEntry getIdEntry() {
		return this.idEntry;
	}

	public FormEntry getNameEntry() {
		return this.nameEntry;
	}

	public FormEntry getLoggingUri() {
		return this.loggingUri;
	}

	public ComboViewer getLevelViewer() {
		return this.levelViewer;
	}

	public Button getEnableLoggingButton() {
		return this.enableLoggingButton;
	}

	@Override
	public void setEditable(final boolean canEdit) {
		this.idEntry.setEditable(canEdit);
		this.nameEntry.setEditable(canEdit);
	}
}
