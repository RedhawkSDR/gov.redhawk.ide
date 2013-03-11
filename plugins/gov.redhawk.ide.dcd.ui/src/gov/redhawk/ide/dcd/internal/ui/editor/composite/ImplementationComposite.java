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
import gov.redhawk.common.ui.parts.FormEntry;
import gov.redhawk.ide.dcd.internal.ui.editor.provider.ImplementationDetailsSectionImplementationItemProvider;
import gov.redhawk.ide.spd.internal.ui.editor.provider.SpdItemProviderAdapterFactoryAdapter;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

/**
 * 
 */
public class ImplementationComposite extends Composite {
	private static final int NUM_COLUMNS = 3;

	private final FormToolkit toolkit;
	private ComposedAdapterFactory adapterFactory;

	private FormEntry idEntry;
	private FormEntry prfEntry;
	private FormEntry descriptionEntry;

	/**
	 * @param parent
	 * @param style
	 */
	public ImplementationComposite(final Composite parent, final int style, final FormToolkit toolkit) {
		super(parent, style);
		this.toolkit = toolkit;

		this.setLayout(FormLayoutFactory.createSectionClientGridLayout(false, ImplementationComposite.NUM_COLUMNS));

		createIDEntry();

		createPropertyFileEntry();

		createDescriptionEntry();
		toolkit.paintBordersFor(this);
	}

	/**
	 * Creates the id entry.
	 * 
	 * @param client the client
	 * @param toolkit the toolkit
	 * @param actionBars the action bars
	 */
	private void createIDEntry() {
		this.idEntry = new FormEntry(this, this.toolkit, "ID:", SWT.SINGLE, "Generate", false);
		this.idEntry.getText().setToolTipText(
		        "The implementation element's id attribute uniquely identifies a specific implementation of the device and is a DCE UUID value.");
	}

	/**
	 * Creates the property file entry.
	 * 
	 * @param client the client
	 * @param toolkit the toolkit
	 * @param actionBars the action bars
	 */
	private void createPropertyFileEntry() {
		this.prfEntry = new FormEntry(this, this.toolkit, "Property File:", SWT.SINGLE, "Browse", true);
		this.prfEntry.getText().setToolTipText(
		        "The propertyfile element is used to indicate the local filename of the Property Descriptor file associated with this device "
		                + "package described by the implementation element.");
	}

	/**
	 * Gets the adapter factory.
	 * 
	 * @return the adapter factory
	 */
	protected AdapterFactory getAdapterFactory() {
		if (this.adapterFactory == null) {
			this.adapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);

			this.adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());
			final SpdItemProviderAdapterFactoryAdapter spdFactory = new SpdItemProviderAdapterFactoryAdapter();
			spdFactory.setImplementationAdapter(new ImplementationDetailsSectionImplementationItemProvider(spdFactory));
			this.adapterFactory.addAdapterFactory(spdFactory);
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
	 * Creates the description entry.
	 * 
	 * @param client the client
	 * @param toolkit the toolkit
	 * @param actionBars the action bars
	 */
	private void createDescriptionEntry() {
		this.descriptionEntry = new FormEntry(this, this.toolkit, "Description:", SWT.MULTI | SWT.WRAP);
		final Object data = this.descriptionEntry.getText().getLayoutData();
		if (data instanceof GridData) {
			((GridData) this.descriptionEntry.getLabel().getLayoutData()).verticalAlignment = SWT.TOP;
			final GridData gData = (GridData) data;
			gData.verticalAlignment = SWT.FILL;
			gData.grabExcessVerticalSpace = true;
			gData.heightHint = 75; // SUPPRESS CHECKSTYLE MagicNumber
		} else if (data instanceof TableWrapData) {
			((TableWrapData) this.descriptionEntry.getLabel().getLayoutData()).valign = SWT.TOP;
			final TableWrapData tData = (TableWrapData) data;
			tData.valign = SWT.FILL;
			tData.grabVertical = true;
			tData.heightHint = 75; // SUPPRESS CHECKSTYLE MagicNumber
		}
	}

	/**
	 * @return the idEntry
	 */
	public FormEntry getIdEntry() {
		return this.idEntry;
	}

	/**
	 * @return the prfEntry
	 */
	public FormEntry getPrfEntry() {
		return this.prfEntry;
	}

	/**
	 * @return the descriptionEntry
	 */
	public FormEntry getDescriptionEntry() {
		return this.descriptionEntry;
	}

}
