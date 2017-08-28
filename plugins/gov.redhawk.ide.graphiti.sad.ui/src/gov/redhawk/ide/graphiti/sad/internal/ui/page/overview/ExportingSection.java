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

import gov.redhawk.common.ui.editor.FormLayoutFactory;
import gov.redhawk.ui.editor.AbstractOverviewPage;
import gov.redhawk.ui.editor.ScaSection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;

public class ExportingSection extends ScaSection {

	/**
	 * Instantiates a new exporting section.
	 * 
	 * @param page the page
	 * @param parent the parent
	 */
	public ExportingSection(final AbstractOverviewPage page, final Composite parent) {
		super(page, parent, SWT.None);
		createClient(getSection(), page.getEditor().getToolkit());
	}

	@Override
	protected void createClient(final Section section, final FormToolkit toolkit) {
		section.setText(Messages.ExportingSection_Title);
		section.setLayout(FormLayoutFactory.createClearTableWrapLayout(false, 1));
		final TableWrapData data = new TableWrapData(TableWrapData.FILL_GRAB);
		section.setLayoutData(data);

		final Composite client = toolkit.createComposite(section);
		client.setLayout(FormLayoutFactory.createClearTableWrapLayout(true, 1));
		section.setClient(client);

		final IActionBars actionBars = getPage().getEditor().getEditorSite().getActionBars();

		createExportingArea(client, toolkit, actionBars);

		toolkit.paintBordersFor(client);
	}

	@Override
	public AbstractOverviewPage getPage() {
		return (AbstractOverviewPage) super.getPage();
	}

	/**
	 * Creates the exporting area.
	 * 
	 * @param client the client
	 * @param toolkit the toolkit
	 * @param actionBars the action bars
	 */
	private void createExportingArea(final Composite client, final FormToolkit toolkit, final IActionBars actionBars) {
		getPage().createClient(client, Messages.ExportingSection_Export_Form, toolkit);
	}

}
