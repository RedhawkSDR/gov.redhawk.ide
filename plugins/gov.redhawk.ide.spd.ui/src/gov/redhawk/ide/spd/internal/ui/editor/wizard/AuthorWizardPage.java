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
package gov.redhawk.ide.spd.internal.ui.editor.wizard;

import mil.jpeojtrs.sca.spd.Author;
import mil.jpeojtrs.sca.spd.SpdFactory;
import mil.jpeojtrs.sca.spd.SpdPackage;

import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.emf.databinding.EMFObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
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
public class AuthorWizardPage extends WizardPage {

	private static final String PAGE_NAME = "authorWizardPage";
	private static final String TITLE = "New Author";

	private Author author = SpdFactory.eINSTANCE.createAuthor();

	private EMFDataBindingContext context = new EMFDataBindingContext();
	private WizardPageSupport pageSupport;
	private TableViewer tableViewer;

	/**
	 * The Constructor.
	 */
	protected AuthorWizardPage() {
		super(AuthorWizardPage.PAGE_NAME, AuthorWizardPage.TITLE, null);
	}

	/**
	 * @return the author
	 */
	public Author getAuthor() {
		return this.author;
	};

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		if (this.context != null) {
			this.context.dispose();
			this.context = null;
		}
		if (this.pageSupport != null) {
			this.pageSupport.dispose();
			this.pageSupport = null;
		}
		super.dispose();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createControl(final Composite parent) {
		final Composite client = new Composite(parent, SWT.NULL);
		client.setLayout(new GridLayout(2, false));

		Label label;
		final GridDataFactory labelFactory = GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP);
		Text text;
		final GridDataFactory textFactory = GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false);

		label = new Label(client, SWT.NULL);
		label.setLayoutData(labelFactory.create());
		label.setText("Company:");
		text = new Text(client, SWT.BORDER);
		text.setLayoutData(textFactory.create());
		this.context.bindValue(SWTObservables.observeText(text, SWT.Modify), EMFObservables.observeValue(this.author,
		        SpdPackage.Literals.AUTHOR__COMPANY), null, null);

		label = new Label(client, SWT.NULL);
		label.setLayoutData(labelFactory.create());
		label.setText("Webpage:");
		text = new Text(client, SWT.BORDER);
		text.setLayoutData(textFactory.create());
		this.context.bindValue(SWTObservables.observeText(text, SWT.Modify), EMFObservables.observeValue(this.author,
		        SpdPackage.Literals.AUTHOR__WEBPAGE), null, null);

		label = new Label(client, SWT.NULL);
		label.setLayoutData(labelFactory.create());
		label.setText("Names:");
		final Composite namesComp = new Composite(client, SWT.NULL);
		namesComp.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_VERTICAL | GridData.GRAB_HORIZONTAL));
		final GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		namesComp.setLayout(layout);

		this.tableViewer = new TableViewer(namesComp, SWT.BORDER);
		this.tableViewer.setContentProvider(new IStructuredContentProvider() {

			@Override
			public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
				// TODO Auto-generated method stub

			}

			@Override
			public void dispose() {
				// TODO Auto-generated method stub

			}

			@Override
			public Object[] getElements(final Object inputElement) {
				return ((Author) inputElement).getName().toArray();
			}
		});
		this.tableViewer.setLabelProvider(new LabelProvider());
		this.tableViewer.setInput(this.author);
		this.tableViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));

		final Button addButton = new Button(namesComp, SWT.PUSH);
		addButton.setText("Add");
		addButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		addButton.addSelectionListener(new SelectionAdapter() {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(final SelectionEvent e) {
				handleAddName();
			}
		});

		final Button removeButton = new Button(namesComp, SWT.PUSH);
		removeButton.setText("Remove");
		removeButton.setEnabled(false);
		removeButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		this.tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				removeButton.setEnabled(!event.getSelection().isEmpty());
			}
		});
		removeButton.addSelectionListener(new SelectionAdapter() {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(final SelectionEvent e) {
				AuthorWizardPage.this.author.getName().remove(
				        ((IStructuredSelection) AuthorWizardPage.this.tableViewer.getSelection()).getFirstElement());
				AuthorWizardPage.this.tableViewer.refresh();
			}
		});

		this.pageSupport = WizardPageSupport.create(this, this.context);

		this.setControl(client);
	}

	/**
	 * Sets the author.
	 * 
	 * @param author the new author
	 */
	public void setAuthor(final Author author) {
		this.author = author;
		this.setTitle("Edit Author");
		this.setDescription("Edit the author attributes.");
	}

	/**
     * 
     */
	protected void handleAddName() {
		final InputDialog dialog = new InputDialog(getShell(), "New Name", "Name:", "", new IInputValidator() {

			@Override
			public String isValid(final String newText) {
				if (newText.length() == 0) {
					return "Must enter a value.";
				}
				return null;
			}
		});
		if (dialog.open() == Window.OK) {
			this.author.getName().add(dialog.getValue());
			this.tableViewer.refresh();
		}

	}
}
