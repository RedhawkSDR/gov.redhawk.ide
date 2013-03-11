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
package gov.redhawk.ide.dcd.internal.ui.editor.composite;

import gov.redhawk.common.ui.editor.FormLayoutFactory;
import gov.redhawk.ide.dcd.internal.ui.editor.provider.OverviewAdapterFactory;
import gov.redhawk.ide.dcd.internal.ui.parts.AuthorsPart;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * 
 */
public class AuthorsComposite extends Composite {
	private static final int NUM_COLUMNS = 3;
	private AuthorsPart authorsPart;
	private ComposedAdapterFactory adapterFactory;

	public AuthorsComposite(final Composite parent, final int style, final FormToolkit toolkit, final IActionBars actionBars) {
		super(parent, style);
		this.setLayout(FormLayoutFactory.createSectionClientGridLayout(false, AuthorsComposite.NUM_COLUMNS));

		createAuthorsArea(this, toolkit, actionBars);
	}

	/**
	 * Creates the authors area.
	 * 
	 * @param client the client
	 * @param toolkit the toolkit
	 * @param actionBars the action bars
	 */
	private void createAuthorsArea(final Composite client, final FormToolkit toolkit, final IActionBars actionBars) {
		this.authorsPart = new AuthorsPart(client, toolkit);
		getAuthorsViewer().setContentProvider(new AdapterFactoryContentProvider(getAdapterFactory()));
		getAuthorsViewer().setLabelProvider(new AdapterFactoryLabelProvider(getAdapterFactory()));
		this.getEditAuthorButton().setEnabled(false);
		this.authorsPart.getTableViewer().addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(final SelectionChangedEvent event) {
				getEditAuthorButton().setEnabled(AuthorsComposite.this.authorsPart.isEnabled() && !event.getSelection().isEmpty());
			}
		});

		this.getRemoveAuthorButton().setEnabled(false);
		getAuthorsViewer().addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(final SelectionChangedEvent event) {
				getRemoveAuthorButton().setEnabled(AuthorsComposite.this.authorsPart.isEnabled() && !event.getSelection().isEmpty());
			}
		});
	}

	/**
	 * Gets the adapter factory.
	 * 
	 * @return the adapter factory
	 */
	private AdapterFactory getAdapterFactory() {
		if (this.adapterFactory == null) {
			this.adapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);

			this.adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());
			this.adapterFactory.addAdapterFactory(new OverviewAdapterFactory());
			this.adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());
		}
		return this.adapterFactory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		if (this.adapterFactory != null) {
			this.adapterFactory.dispose();
			this.adapterFactory = null;
		}
		super.dispose();
	}

	/**
	 * @return the authorsViewer
	 */
	public TableViewer getAuthorsViewer() {
		return this.authorsPart.getTableViewer();
	}

	/**
	 * @return the addAuthorButton
	 */
	public Button getAddAuthorButton() {
		return this.authorsPart.getAddButton();
	}

	/**
	 * @return the editAuthorButton
	 */
	public Button getEditAuthorButton() {
		return this.authorsPart.getEditButton();
	}

	/**
	 * @return the removeAuthorButton
	 */
	public Button getRemoveAuthorButton() {
		return this.authorsPart.getRemoveButton();
	}

	/**
	 * @param editable
	 */
	public void setEditable(final boolean editable) {
		this.authorsPart.setEnabled(editable);
	}

}
