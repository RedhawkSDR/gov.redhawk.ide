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
package gov.redhawk.ide.dcd.internal.ui.editor;

import gov.redhawk.common.ui.editor.FormLayoutFactory;
import gov.redhawk.ui.editor.AbstractOverviewPage;
import gov.redhawk.ui.editor.ScaSection;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;

/**
 * @since 1.1
 * 
 */
public class NodeContentSection extends ScaSection {

	/** The Constant PROP_HREF. */
	public static final String DEVICE_HREF = "http://devices";

	private static final String FORM_TEXT = "<form>" + "<li> <a href=\"" + NodeContentSection.DEVICE_HREF
	        + "\" nowrap=\"true\">Devices</a>: declares devices contained in the node.</li></form>";

	/**
	 * The Constructor.
	 * 
	 * @param page the page
	 * @param parent the parent
	 */
	public NodeContentSection(final AbstractOverviewPage page, final Composite parent) {
		super(page, parent, Section.DESCRIPTION);
		createClient(getSection(), page.getEditor().getToolkit());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractOverviewPage getPage() {
		return (AbstractOverviewPage) super.getPage();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createClient(final Section section, final FormToolkit toolkit) {
		section.setText("Node Content");
		section.setLayout(FormLayoutFactory.createClearTableWrapLayout(false, 1));
		final TableWrapData data = new TableWrapData(TableWrapData.FILL_GRAB);
		section.setLayoutData(data);

		section.setDescription("The content of the node:");
		final Composite client = toolkit.createComposite(section);
		client.setLayout(FormLayoutFactory.createClearTableWrapLayout(true, 1));
		section.setClient(client);

		final IActionBars actionBars = getPage().getEditor().getEditorSite().getActionBars();

		createFormTextArea(client, toolkit, actionBars);

		toolkit.paintBordersFor(client);

	}

	/**
	 * Creates the form text area.
	 * 
	 * @param client the client
	 * @param toolkit the toolkit
	 * @param actionBars the action bars
	 */
	private void createFormTextArea(final Composite client, final FormToolkit toolkit, final IActionBars actionBars) {
		final FormText formText = toolkit.createFormText(client, true);
		formText.setWhitespaceNormalized(true);
		final TableWrapData td = new TableWrapData(TableWrapData.FILL);
		formText.setLayoutData(td);
		// formText.setImage(key, image)
		formText.setText(getFormText(), true, false);
		formText.addHyperlinkListener(getPage());

	}

	/**
	 * Gets the form text.
	 * 
	 * @return the form text
	 */
	private String getFormText() {
		return NodeContentSection.FORM_TEXT;
	}

}
