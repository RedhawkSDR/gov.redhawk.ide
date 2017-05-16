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
package gov.redhawk.ide.spd.ui.editor;

import gov.redhawk.common.ui.editor.FormLayoutFactory;
import gov.redhawk.ide.spd.internal.ui.editor.composite.AuthorsComposite;
import gov.redhawk.ide.spd.internal.ui.editor.wizard.AuthorWizard;
import gov.redhawk.ui.editor.ScaFormPage;
import gov.redhawk.ui.editor.ScaSection;

import java.util.Collections;

import mil.jpeojtrs.sca.spd.Author;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdPackage;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.ReplaceCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;

/**
 * 
 */
public class AuthorsSection extends ScaSection {
	private static final int NUM_COLUMNS = 3;

	private AuthorsComposite client;
	private Resource spdResource;

	/**
	 * Instantiates a new general info section.
	 * 
	 * @param page the page
	 * @param parent the parent
	 */
	public AuthorsSection(final ScaFormPage page, final Composite parent) {
		super(page, parent, ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		createClient(getSection(), page.getEditor().getToolkit());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createClient(final Section section, final FormToolkit toolkit) {
		section.setText("Authors");
		section.setLayout(FormLayoutFactory.createClearTableWrapLayout(false, 1));
		final TableWrapData data = new TableWrapData(TableWrapData.FILL_GRAB);
		section.setLayoutData(data);

		// section.setDescription("This section describes general information about this component.");

		final IActionBars actionBars = getPage().getEditor().getEditorSite().getActionBars();
		this.client = new AuthorsComposite(section, SWT.None, toolkit, actionBars);
		this.client.setLayout(FormLayoutFactory.createSectionClientGridLayout(false, AuthorsSection.NUM_COLUMNS));
		section.setClient(this.client);

		addListeners(actionBars);

		toolkit.adapt(this.client);
		toolkit.paintBordersFor(this.client);
	}

	/**
	 * @param actionBars
	 */
	private void addListeners(final IActionBars actionBars) {
		this.client.getAddAuthorButton().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				handleAddAuthor();
			}
		});
		this.client.getEditAuthorButton().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				handleEditAuthor();
			}
		});
		this.client.getAuthorsViewer().addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				setSelection(event.getSelection());
			}
		});
		this.client.getRemoveAuthorButton().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				handleRemoveAuthor();
			}
		});

	}

	/**
	 * Handle add author.
	 */
	protected void handleAddAuthor() {
		final AuthorWizard wizard = new AuthorWizard();
		final WizardDialog dialog = new WizardDialog(getPage().getSite().getShell(), wizard);
		if (dialog.open() == Window.OK) {
			final Command command = AddCommand.create(getEditingDomain(), getSoftPkg(), SpdPackage.Literals.SOFT_PKG__AUTHOR, wizard.getAuthor());
			getEditingDomain().getCommandStack().execute(command);
			this.client.getAuthorsViewer().setSelection(new StructuredSelection(wizard.getAuthor()), true);
		}
	}

	/**
	 * Gets the editing domain.
	 * 
	 * @return the editing domain
	 */
	private EditingDomain getEditingDomain() {
		return getPage().getEditor().getEditingDomain();
	}

	/**
	 * Handle edit author.
	 */
	protected void handleEditAuthor() {
		final Object obj = ((IStructuredSelection) this.client.getAuthorsViewer().getSelection()).getFirstElement();
		if (obj instanceof Author) {
			final AuthorWizard wizard = new AuthorWizard();
			final Author authorCopy = (Author) EcoreUtil.copy((EObject) obj);
			wizard.setAuthor(authorCopy);
			final WizardDialog dialog = new WizardDialog(getPage().getSite().getShell(), wizard);
			if (dialog.open() == Window.OK) {
				final Command command = ReplaceCommand.create(getEditingDomain(), getSoftPkg(), SpdPackage.Literals.SOFT_PKG__AUTHOR, obj, Collections
				        .singleton(authorCopy));
				getEditingDomain().getCommandStack().execute(command);
			}
		}
	}

	/**
	 * Sets the selection.
	 * 
	 * @param selection the new selection
	 */
	private void setSelection(final ISelection selection) {
		getPage().setSelection(selection);
	}

	/**
	 * Handle remove author.
	 */
	protected void handleRemoveAuthor() {
		final Object obj = ((IStructuredSelection) this.client.getAuthorsViewer().getSelection()).getFirstElement();
		if (obj instanceof Author) {
			final Author auth = (Author) obj;
			final Command command = RemoveCommand.create(getEditingDomain(), getSoftPkg(), SpdPackage.Literals.SOFT_PKG__AUTHOR, auth);
			getEditingDomain().getCommandStack().execute(command);
		}
	}

	private SoftPkg getSoftPkg() {
		return SoftPkg.Util.getSoftPkg(this.spdResource);
	}

	private void setEditable(final boolean editable) {
		this.client.setEditable(editable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refresh(final Resource resource) {
		this.spdResource = resource;
		this.client.getAuthorsViewer().setInput(getSoftPkg());

		setEditable(!getPage().getEditingDomain().isReadOnly(resource));
	}
}
