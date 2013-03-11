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
package gov.redhawk.ide.sad.internal.ui.composite;

import gov.redhawk.common.ui.editor.FormLayoutFactory;
import gov.redhawk.common.ui.parts.FormEntry;
import gov.redhawk.ui.editor.IScaComposite;
import gov.redhawk.ui.parts.ComboViewerPart;
import mil.jpeojtrs.sca.partitioning.ComponentInstantiation;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

/**
 * 
 */
public class GeneralInformationComposite extends Composite implements IScaComposite {
	private static final int NUM_COLUMNS = 3;

	private FormEntry descriptionEntry;
	private FormEntry idEntry;
	private FormEntry versionEntry;
	private FormEntry nameEntry;
	private ComboViewerPart assemblyControllerPart;

	/**
	 * @param parent
	 * @param style
	 * @param toolkit
	 * @param actionBars
	 */
	public GeneralInformationComposite(final Composite parent, final int style, final FormToolkit toolkit, final IActionBars actionBars) {
		super(parent, style);
		this.setLayout(FormLayoutFactory.createSectionClientGridLayout(false, GeneralInformationComposite.NUM_COLUMNS));

		createIDEntry(this, toolkit, actionBars);

		createNameEntry(this, toolkit, actionBars);

		createVersionEntry(this, toolkit, actionBars);

		createAssemblyControllerPart(this, toolkit, actionBars);

		createDescriptionEntry(this, toolkit, actionBars);
	}

	/**
	 * @param generalInformationComposite
	 * @param toolkit
	 * @param actionBars
	 */
	private void createAssemblyControllerPart(final Composite client, final FormToolkit toolkit, final IActionBars actionBars) {
		final Label label = toolkit.createLabel(client, "Controller:");
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		this.assemblyControllerPart = new ComboViewerPart();
		this.assemblyControllerPart.createControl(client, toolkit, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.SINGLE);
		this.assemblyControllerPart.getControl().setToolTipText("Assembly Controller");
		this.assemblyControllerPart.setComparator(new ViewerComparator());
		this.assemblyControllerPart.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(final Object element) {
				if (element instanceof ComponentInstantiation) {
					final ComponentInstantiation inst = (ComponentInstantiation) element;
					return inst.getUsageName();
				}
				return super.getText(element);
			}
		});
		final GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		data.horizontalIndent = FormLayoutFactory.CONTROL_HORIZONTAL_INDENT;
		this.assemblyControllerPart.getControl().setLayoutData(data);
	}

	/**
	 * @return the assemblyControllerPart
	 */
	public ComboViewerPart getAssemblyControllerPart() {
		return this.assemblyControllerPart;
	}

	/**
	 * Creates the description area.
	 * 
	 * @param client the client
	 * @param toolkit the toolkit
	 * @param actionBars the action bars
	 */
	private void createDescriptionEntry(final Composite client, final FormToolkit toolkit, final IActionBars actionBars) {
		this.descriptionEntry = new FormEntry(client, toolkit, "Description:", SWT.MULTI | SWT.WRAP);
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
	 * Creates the id entry.
	 * 
	 * @param client the client
	 * @param toolkit the toolkit
	 * @param actionBars the action bars
	 */
	private void createIDEntry(final Composite client, final FormToolkit toolkit, final IActionBars actionBars) {
		this.idEntry = new FormEntry(client, toolkit, "ID:", "Generate", false);

	}

	/**
	 * Creates the version entry.
	 * 
	 * @param client the client
	 * @param toolkit the toolkit
	 * @param actionBars the action bars
	 */
	private void createVersionEntry(final Composite client, final FormToolkit toolkit, final IActionBars actionBars) {
		this.versionEntry = new FormEntry(client, toolkit, "Version:", null, false);
	}

	/**
	 * Creates the name entry.
	 * 
	 * @param client the client
	 * @param toolkit the toolkit
	 * @param actionBars the action bars
	 */
	private void createNameEntry(final Composite client, final FormToolkit toolkit, final IActionBars actionBars) {
		this.nameEntry = new FormEntry(client, toolkit, "Name:", null, false);
	}

	/**
	 * @return the descriptionEntry
	 */
	public FormEntry getDescriptionEntry() {
		return this.descriptionEntry;
	}

	/**
	 * @return the idEntry
	 */
	public FormEntry getIdEntry() {
		return this.idEntry;
	}

	/**
	 * @return the versionEntry
	 */
	public FormEntry getVersionEntry() {
		return this.versionEntry;
	}

	/**
	 * @return the nameEntry
	 */
	public FormEntry getNameEntry() {
		return this.nameEntry;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setEditable(final boolean editable) {
		this.descriptionEntry.setEditable(editable);
		this.idEntry.setEditable(editable);
		this.nameEntry.setEditable(editable);
		this.assemblyControllerPart.setEnabled(editable);
		// this.typeEntry.setEditable(editable);
		this.versionEntry.setEditable(editable);
	}

}
