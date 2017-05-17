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
package gov.redhawk.ide.spd.internal.ui.editor;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;

import gov.redhawk.common.ui.editor.FormLayoutFactory;
import gov.redhawk.ide.codegen.ui.utils.DocumentationUtils;
import gov.redhawk.ui.editor.ScaFormPage;
import gov.redhawk.ui.editor.ScaSection;

public class ProjectDocumentationSection extends ScaSection {

	public ProjectDocumentationSection(ScaFormPage page, Composite parent) {
		super(page, parent, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.COMPACT);
		createClient(getSection(), page.getEditor().getToolkit());
	}

	@Override
	protected void createClient(Section section, FormToolkit toolkit) {
		section.setText(Messages.ProjectDocumentationSection_SectionTitle);
		section.setLayout(FormLayoutFactory.createClearTableWrapLayout(false, 1));
		final TableWrapData data = new TableWrapData(TableWrapData.FILL_GRAB);
		section.setLayoutData(data);

		final Composite client = toolkit.createComposite(section);
		client.setLayout(FormLayoutFactory.createClearTableWrapLayout(true, 1));
		section.setClient(client);

		FormText headerFormText = toolkit.createFormText(client, true);
		headerFormText.setText(Messages.ProjectDocumentationSection_AddHeaderText, true, true);
		headerFormText.addHyperlinkListener(new IHyperlinkListener() {

			@Override
			public void linkExited(HyperlinkEvent e) {
			}

			@Override
			public void linkEntered(HyperlinkEvent e) {
			}

			@Override
			public void linkActivated(HyperlinkEvent e) {
				// Must have a file editor input
				IEditorInput input = getPage().getEditorInput();
				if (!(input instanceof IFileEditorInput)) {
					return;
				}

				IProject project = ((IFileEditorInput) input).getFile().getProject();
				IWorkbenchPage page = getPage().getEditorSite().getPage();
				DocumentationUtils.openHeader(project, page);
			}
		});

		toolkit.paintBordersFor(client);
	}
}
