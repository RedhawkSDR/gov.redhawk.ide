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
package gov.redhawk.ide.graphiti.dcd.internal.ui.page.overview;

import gov.redhawk.common.ui.editor.FormLayoutFactory;
import gov.redhawk.ide.spd.ui.ComponentImages;
import gov.redhawk.ui.editor.AbstractOverviewPage;
import gov.redhawk.ui.editor.ScaSection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;

/**
 * @since 1.1
 */
public class TestingSection extends ScaSection {

	public static final String TESTING_HREF = "test";

	public TestingSection(final AbstractOverviewPage page, final Composite parent) {
		super(page, parent, Section.DESCRIPTION);
		createClient(getSection(), page.getEditor().getToolkit());
	}

	@Override
	public AbstractOverviewPage getPage() {
		return (AbstractOverviewPage) super.getPage();
	}

	@Override
	protected void createClient(final Section section, final FormToolkit toolkit) {
		section.setText("Testing");
		section.setLayout(FormLayoutFactory.createClearTableWrapLayout(false, 1));
		final TableWrapData data = new TableWrapData(TableWrapData.FILL_GRAB);
		section.setLayoutData(data);

		section.setDescription("Test the node by:");
		final Composite client = toolkit.createComposite(section);
		client.setLayout(FormLayoutFactory.createClearTableWrapLayout(true, 1));
		section.setClient(client);

		final IActionBars actionBars = getPage().getEditor().getEditorSite().getActionBars();

		createTestingArea(client, toolkit, actionBars);

		toolkit.paintBordersFor(client);

	}

	private void createTestingArea(final Composite client, final FormToolkit toolkit, final IActionBars actionBars) {
		ImageHyperlink imageHyperlink = toolkit.createImageHyperlink(client, SWT.None);
		imageHyperlink.setText("Launch a local node");
		imageHyperlink.setHref(TestingSection.TESTING_HREF);
		imageHyperlink.addHyperlinkListener(this.getPage());
		imageHyperlink.setImage(ComponentImages.getImage(ComponentImages.RUN_EXEC));

		imageHyperlink = toolkit.createImageHyperlink(client, SWT.None);
		imageHyperlink.setText("Launch a remote node");
		imageHyperlink.setHref(TestingSection.TESTING_HREF);
		imageHyperlink.addHyperlinkListener(this.getPage());
		imageHyperlink.setImage(ComponentImages.getImage(ComponentImages.RUN_EXEC));

		imageHyperlink = toolkit.createImageHyperlink(client, SWT.None);
		imageHyperlink.setText("Launch a local node in debug mode");
		imageHyperlink.setHref(TestingSection.TESTING_HREF);
		imageHyperlink.addHyperlinkListener(this.getPage());
		imageHyperlink.setImage(ComponentImages.getImage(ComponentImages.DEBUG_EXEC));

		imageHyperlink = toolkit.createImageHyperlink(client, SWT.None);
		imageHyperlink.setText("Launch a remote node in debug mode");
		imageHyperlink.setHref(TestingSection.TESTING_HREF);
		imageHyperlink.addHyperlinkListener(this.getPage());
		imageHyperlink.setImage(ComponentImages.getImage(ComponentImages.DEBUG_EXEC));
	}

}
