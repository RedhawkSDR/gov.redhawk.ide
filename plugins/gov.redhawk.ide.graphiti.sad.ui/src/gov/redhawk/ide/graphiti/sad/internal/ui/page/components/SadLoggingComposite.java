/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.graphiti.sad.internal.ui.page.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;

import gov.redhawk.common.ui.editor.FormLayoutFactory;
import gov.redhawk.common.ui.parts.FormEntry;
import gov.redhawk.logging.ui.LogLevels;
import gov.redhawk.ui.editor.IScaComposite;

public class SadLoggingComposite extends Composite implements IScaComposite {

	private FormToolkit toolkit;
	private Button enableLoggingButton;
	private ComboViewer levelViewer;
	private FormEntry loggingUri;

	public SadLoggingComposite(final Composite parent, final int style, final FormToolkit toolkit, final IEditingDomainProvider editingDomainProvider) {
		super(parent, style);

		this.toolkit = toolkit;
		setLayout(FormLayoutFactory.createSectionClientGridLayout(false, 2));

		createLoggingConfigEntry();

		toolkit.paintBordersFor(this);
	}

	private void createLoggingConfigEntry() {
		// Logging Config
		Label label = toolkit.createLabel(this, Messages.SadLoggingComposite_Enabled);
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		label.setToolTipText(Messages.SadLoggingComposite_EnabledTooltip);
		this.enableLoggingButton = toolkit.createButton(this, null, SWT.CHECK);
		this.enableLoggingButton.setToolTipText(label.getToolTipText());

		// Log Level
		label = toolkit.createLabel(this, Messages.SadLoggingComposite_LogLevel);
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		this.levelViewer = new ComboViewer(this, SWT.DROP_DOWN | SWT.READ_ONLY);
		toolkit.adapt(this.levelViewer.getCombo(), true, false);
		this.levelViewer.getControl().setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		this.levelViewer.setContentProvider(new ArrayContentProvider());
		this.levelViewer.setLabelProvider(new LabelProvider());
		List<Object> logLevels = new ArrayList<>();
		logLevels.addAll(Arrays.asList(LogLevels.values()));
		logLevels.add(0, ""); // Add blank entry to allow unsetting log level
		this.levelViewer.setInput(logLevels);

		// Logging URI
		this.loggingUri = new FormEntry(this, this.toolkit, Messages.SadLoggingComposite_LoggingURI, SWT.SINGLE);
		this.loggingUri.setTooltip(Messages.SadLoggingComposite_LoggingURITooltip);
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
	public void setEditable(boolean canEdit) {
		this.enableLoggingButton.setEnabled(canEdit);
		this.loggingUri.setEditable(canEdit);
		this.levelViewer.getCombo().setEnabled(canEdit);
	}

}
