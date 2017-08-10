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

import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.forms.widgets.FormToolkit;

import gov.redhawk.common.ui.editor.FormLayoutFactory;
import gov.redhawk.common.ui.parts.FormEntry;
import gov.redhawk.diagram.sheet.properties.ComponentInstantiationPropertyViewerAdapter;
import gov.redhawk.sca.ui.ScaComponentFactory;
import gov.redhawk.sca.ui.properties.ScaPropertiesAdapterFactory;
import gov.redhawk.ui.editor.IScaComposite;
import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;

public abstract class ComponentPlacementComposite extends Composite implements IScaComposite {
	private static final int NUM_COLUMNS = 3;

	protected final FormToolkit toolkit; // SUPPRESS CHECKSTYLE INLINE exposed to extending classes
	private FormEntry nameEntry;

	private ScaPropertiesAdapterFactory adapterFactory = new ScaPropertiesAdapterFactory();
	private final IEditingDomainProvider editingDomainProvider;

	private final ComponentInstantiationPropertyViewerAdapter adapter;

	private TreeViewer propertiesViewer;

	/**
	 * @param showProps - True if the properties section should be shown
	 */
	protected abstract void createCompositeSections(boolean showProps);

	/**
	 * @param parent
	 * @param style
	 * @param toolkit
	 */
	public ComponentPlacementComposite(final Composite parent, final int style, final FormToolkit toolkit, final IEditingDomainProvider editingDomainProvider) {
		this(parent, style, toolkit, editingDomainProvider, true);
	}

	public ComponentPlacementComposite(final Composite parent, final int style, final FormToolkit toolkit, final IEditingDomainProvider editingDomainProvider,
		boolean showProps) {
		super(parent, style);

		this.toolkit = toolkit;
		this.editingDomainProvider = editingDomainProvider;
		this.adapter = new ComponentInstantiationPropertyViewerAdapter(this.editingDomainProvider);

		setLayout(FormLayoutFactory.createSectionClientGridLayout(false, ComponentPlacementComposite.NUM_COLUMNS));

		createCompositeSections(showProps);

		toolkit.paintBordersFor(this);
	}

	/**
	 * Creates the name entry.
	 * 
	 * @param client the client
	 * @param toolkit the toolkit
	 * @param actionBars the action bars
	 */
	protected void createNameEntry() {
		this.nameEntry = new FormEntry(this, this.toolkit, "Name:", SWT.SINGLE);
	}

	/**
	 * Creates the properties entry.
	 * 
	 * @param client the client
	 * @param toolkit the toolkit
	 * @param actionBars the action bars
	 */
	protected void createPropertiesArea() {
		final Group group = new Group(this, SWT.SHADOW_ETCHED_IN);
		group.setText("Properties");
		group.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).span(3, 1).hint(SWT.DEFAULT, 300).create());
		group.setLayout(new FillLayout());
		this.propertiesViewer = ScaComponentFactory.createPropertyTable(group, SWT.SINGLE | SWT.BORDER, this.adapterFactory);
		this.adapter.setViewer(this.propertiesViewer);
	}

	public FormEntry getNameEntry() {
		return this.nameEntry;
	}

	@Override
	public void setEditable(final boolean canEdit) {
		this.nameEntry.setEditable(canEdit);
	}

	public void setInput(final DcdComponentInstantiation instantiation) {
		this.adapter.setInput(instantiation);
	}

	@Override
	public void dispose() {
		this.adapter.dispose();
		if (this.adapterFactory != null) {
			this.adapterFactory.dispose();
			this.adapterFactory = null;
		}
		super.dispose();
	}

}
