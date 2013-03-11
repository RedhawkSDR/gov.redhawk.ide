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
package gov.redhawk.ide.spd.internal.ui.editor.detailspart;

import gov.redhawk.common.ui.editor.FormLayoutFactory;
import gov.redhawk.common.ui.parts.FormEntry;
import gov.redhawk.ide.spd.internal.ui.editor.ImplementationsSection;
import gov.redhawk.ide.spd.ui.ComponentUiPlugin;
import gov.redhawk.model.sca.util.ModelUtil;
import gov.redhawk.ui.editor.FormEntryAdapter;
import gov.redhawk.ui.editor.ScaDetails;
import gov.redhawk.ui.util.EntryUtil;

import java.util.List;

import mil.jpeojtrs.sca.spd.LocalFile;
import mil.jpeojtrs.sca.spd.SoftPkgRef;
import mil.jpeojtrs.sca.spd.SpdFactory;
import mil.jpeojtrs.sca.spd.SpdPackage;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * The Class SoftPkgRefDetailsPage.
 */
public class SoftPkgRefDetailsPage extends ScaDetails {
	private static final int NUM_COLUMNS = 3;

	private final ImplementationsSection fSection;
	private SoftPkgRef input;
	private FormEntry fFileEntry;
	private FormEntry fImplRefEntry;

	/**
	 * The Constructor.
	 * 
	 * @param fSection the f section
	 */
	public SoftPkgRefDetailsPage(final ImplementationsSection fSection) {
		super(fSection.getPage());
		this.fSection = fSection;
	}

	/**
	 * Creates the property ref section.
	 * 
	 * @param toolkit the toolkit
	 * @param parent the parent
	 */
	private void createSoftPkgSection(final FormToolkit toolkit, final Composite parent) {
		final Section section = toolkit.createSection(parent, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE
		        | ExpandableComposite.EXPANDED);
		section.clientVerticalSpacing = FormLayoutFactory.SECTION_HEADER_VERTICAL_SPACING;
		section.setText("Software Package Reference Dependency");
		section.setDescription("The softpkgref element refers to a 'softpkg' element contained in another Software Package Descriptor file and "
		        + "indicates a file-load dependency on that file. The other file is referenced by the file element. An optional implementation "
		        + "reference element refers to a particular implementation-unique identifier, within the Software Package Descriptor of the other file.");

		section.setLayout(FormLayoutFactory.createClearGridLayout(false, 1));
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));

		// Align the master and details section headers (misalignment caused
		// by section toolbar icons)
		getPage().alignSectionHeaders(this.fSection.getSection(), section);

		final Composite client = toolkit.createComposite(section);
		client.setLayout(FormLayoutFactory.createSectionClientGridLayout(false, SoftPkgRefDetailsPage.NUM_COLUMNS));
		section.setClient(client);

		final IActionBars actionBars = getPage().getEditor().getEditorSite().getActionBars();

		createFileEntry(client, toolkit, actionBars);

		createImplRefEntry(client, toolkit, actionBars);
		toolkit.paintBordersFor(client);
	}

	/**
	 * Creates the value entry.
	 * 
	 * @param client the client
	 * @param toolkit the toolkit
	 * @param actionBars the action bars
	 */
	private void createFileEntry(final Composite client, final FormToolkit toolkit, final IActionBars actionBars) {
		this.fFileEntry = new FormEntry(client, toolkit, "SPD File:", "Browse", true);
		this.fFileEntry.setFormEntryListener(new FormEntryAdapter(actionBars) {

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void buttonSelected(final FormEntry entry) {
				final String newPath = EntryUtil.browse(getProject(), getSpdRefFile(), SpdPackage.FILE_EXTENSION);
				setSpdRefFileName(newPath);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void linkActivated(final HyperlinkEvent e) {
				openSpdRefFile();
			}
		});
	}

	/**
	 * Open prf file.
	 */
	private void openSpdRefFile() {
		final IFile prfFile = getSpdRefFile();
		if (prfFile == null || !prfFile.exists()) {
			return;
		} else {
			try {
				IDE.openEditor(getPage().getEditor().getSite().getPage(), prfFile, true);
			} catch (final PartInitException e1) {
				final Status status = new Status(IStatus.ERROR, ComponentUiPlugin.getPluginId(), "Failed to open SPD File: " + getSpdRefFileName(), e1);
				StatusManager.getManager().handle(status, StatusManager.SHOW | StatusManager.LOG);
			}
		}
	}

	/**
	 * Gets the prf file.
	 * 
	 * @return the prf file
	 */
	private IFile getSpdRefFile() {
		final String fileName = getSpdRefFileName();
		if (fileName != null) {
			return getProject().getFile(new Path(fileName));
		}
		return null;
	}

	/**
	 * Gets the prf file name.
	 * 
	 * @return the prf file name
	 */
	private String getSpdRefFileName() {
		final LocalFile localFile = this.input.getLocalFile();
		if (localFile == null) {
			return null;
		}
		return localFile.getName();
	}

	/**
	 * Sets the prf file name.
	 * 
	 * @param name the new prf file name
	 */
	private void setSpdRefFileName(final String name) {
		LocalFile localFile = this.input.getLocalFile();
		final CompoundCommand command = new CompoundCommand();
		if (localFile == null && name != null && name.length() != 0) {
			localFile = SpdFactory.eINSTANCE.createLocalFile();
			command.append(SetCommand.create(getEditingDomain(), this.input, SpdPackage.Literals.SOFT_PKG_REF__LOCAL_FILE, localFile));
		}

		if (name != null && name.length() != 0) {
			command.append(SetCommand.create(getEditingDomain(), localFile, SpdPackage.Literals.LOCAL_FILE__NAME, name));
		} else {
			command.append(SetCommand.create(getEditingDomain(), this.input, SpdPackage.Literals.SOFT_PKG_REF__LOCAL_FILE, null));
		}

		getEditingDomain().getCommandStack().execute(command);
	}

	/**
	 * Gets the project.
	 * 
	 * @return the project
	 */
	private IProject getProject() {
		return ModelUtil.getProject(this.input);
	}

	/**
	 * Creates the id entry.
	 * 
	 * @param client the client
	 * @param toolkit the toolkit
	 * @param actionBars the action bars
	 */
	private void createImplRefEntry(final Composite client, final FormToolkit toolkit, final IActionBars actionBars) {
		this.fImplRefEntry = new FormEntry(client, toolkit, "Impl Ref:", null, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Binding> bind(final DataBindingContext dataBindingContext, final EObject input) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createSpecificContent(final Composite parent) {
		final FormToolkit toolkit = getManagedForm().getToolkit();

		createSoftPkgSection(toolkit, parent);
	}

}
