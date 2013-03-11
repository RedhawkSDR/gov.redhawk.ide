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
package gov.redhawk.ide.spd.internal.ui.editor;

import gov.redhawk.common.ui.editor.FormLayoutFactory;
import gov.redhawk.common.ui.parts.FormEntry;
import gov.redhawk.ide.prf.ui.wizard.PrfModelWizard;
import gov.redhawk.ide.spd.internal.ui.editor.composite.GeneralInformationComposite;
import gov.redhawk.ide.spd.ui.ComponentUiPlugin;
import gov.redhawk.ide.spd.ui.wizard.ScdModelWizard;
import gov.redhawk.model.sca.util.ModelUtil;
import gov.redhawk.prf.ui.editor.page.PropertiesFormPage;
import gov.redhawk.ui.editor.FormEntryAdapter;
import gov.redhawk.ui.editor.SCAFormEditor;
import gov.redhawk.ui.editor.ScaSection;
import gov.redhawk.ui.util.EMFEmptyStringToNullUpdateValueStrategy;
import gov.redhawk.ui.util.EntryUtil;
import gov.redhawk.ui.util.SCAEditorUtil;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.scd.ScdPackage;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.spd.Descriptor;
import mil.jpeojtrs.sca.spd.LocalFile;
import mil.jpeojtrs.sca.spd.PropertyFile;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdFactory;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.util.DceUuidUtil;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.databinding.FeaturePath;
import org.eclipse.emf.databinding.edit.EMFEditObservables;
import org.eclipse.emf.databinding.edit.EMFEditProperties;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * The Class GeneralInfoSection.
 */
public class GeneralInfoSection extends ScaSection {
	private GeneralInformationComposite client;
	private Resource spdResource;
	private final Collection<Binding> bindings = new ArrayList<Binding>();

	/**
	 * Instantiates a new general info section.
	 * 
	 * @param page the page
	 * @param parent the parent
	 */
	public GeneralInfoSection(final ComponentOverviewPage page, final Composite parent) {
		super(page, parent, Section.DESCRIPTION);
		createClient(getSection(), page.getEditor().getToolkit());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createClient(final Section section, final FormToolkit toolkit) {
		section.setText("General Information");
		section.setLayout(FormLayoutFactory.createClearTableWrapLayout(false, 1));
		final TableWrapData data = new TableWrapData(TableWrapData.FILL_GRAB);
		section.setLayoutData(data);

		section.setDescription("This section describes general information about this component.");

		final IActionBars actionBars = getPage().getEditor().getEditorSite().getActionBars();
		this.client = new GeneralInformationComposite(section, SWT.None, toolkit, actionBars);
		section.setClient(this.client);

		addListeners(actionBars);

		toolkit.adapt(this.client);
		toolkit.paintBordersFor(this.client);
	}

	/**
	 * Add Listeners to handle Control Events
	 * 
	 * @param actionBars
	 */
	private void addListeners(final IActionBars actionBars) {
		this.client.getScdEntry().setFormEntryListener(new FormEntryAdapter(actionBars) {
			@Override
			public void buttonSelected(final FormEntry entry) {
				final IProject project = getProject();
				if (project != null) {
					final IResource file = ModelUtil.getResource(getSoftPkg());
					if (file != null) {
						final String newPath = EntryUtil.browse(project, getScdFile(), ScdPackage.FILE_EXTENSION);
						if (newPath != null && (getScdFile() == null || !newPath.equals(getScdFile().getName()))) {
							setScdFileName(newPath);
						}
					}
				}
			}

			@Override
			public void linkActivated(final HyperlinkEvent e) {
				handleScdLinkActivated();
			}
		});
		this.client.getPrfEntry().setFormEntryListener(new FormEntryAdapter(actionBars) {
			@Override
			public void buttonSelected(final FormEntry entry) {
				final IProject project = getProject();
				if (project != null) {
					final String newPath = EntryUtil.browse(project, getPrfFile(), PrfPackage.FILE_EXTENSION);
					if (newPath != null && (getPrfFile() == null || !newPath.equals(getPrfFile().getName()))) {
						setPrfFileName(newPath);
					}

				}
			}

			@Override
			public void linkActivated(final HyperlinkEvent e) {
				handlePrfLinkActivated();
			}
		});
		final FormEntryAdapter fIdEntryAdapter = new FormEntryAdapter(actionBars) {
			@Override
			public void buttonSelected(final FormEntry entry) {
				execute(SetCommand.create(getEditingDomain(), getSoftPkg(), SpdPackage.Literals.SOFT_PKG__ID, DceUuidUtil.createDceUUID()));
			}
		};
		this.client.getIdEntry().setFormEntryListener(fIdEntryAdapter);
	}

	private SoftwareComponent getSoftwareComponent() {
		try {
			return getSoftPkg().getDescriptor().getComponent();
		} catch (final NullPointerException e) {
			return null;
		}
	}

	/**
	 * Open scd file.
	 */
	private void openScdFile() {
		final SoftwareComponent scd = getSoftwareComponent();
		if (scd == null || scd.eResource() == null) {
			return;
		} else {
			try {
				org.eclipse.emf.common.util.URI uri = scd.eResource().getURI();
				if (uri.isPlatformResource()) {
					IDE.openEditor(getWorkbenchPage(), (IFile) ResourcesPlugin.getWorkspace().getRoot().findMember(uri.toPlatformString(true)));
				} else {
					if (uri.isPlatform()) {
						uri = CommonPlugin.resolve(uri);
					}
					IDE.openEditorOnFileStore(getWorkbenchPage(), EFS.getStore(URI.create((uri.toString()))));
				}
			} catch (final PartInitException e1) {
				final Status status = new Status(IStatus.ERROR, ComponentUiPlugin.getPluginId(), "Failed to open SCD File: " + getScdFileName(), e1);
				StatusManager.getManager().handle(status, StatusManager.SHOW | StatusManager.LOG);
			} catch (final CoreException e) {
				final Status status = new Status(IStatus.ERROR, ComponentUiPlugin.getPluginId(), "Failed to open SCD File: " + getScdFileName(), e);
				StatusManager.getManager().handle(status, StatusManager.SHOW | StatusManager.LOG);
			}
		}
	}

	/**
	 * Gets the workbench page.
	 * 
	 * @return the workbench page
	 */
	private IWorkbenchPage getWorkbenchPage() {
		return getPage().getEditor().getSite().getPage();
	}

	/**
	 * Sets the scd file name.
	 * 
	 * @param value the value
	 */
	private void setScdFileName(final String value) {
		if (value == null || value.length() == 0) {
			SetCommand.create(getEditingDomain(), getSoftPkg(), SpdPackage.Literals.SOFT_PKG__DESCRIPTOR, null).execute();
		} else {
			final CompoundCommand command = new CompoundCommand("Set SCD file");

			Descriptor descriptor = getSoftPkg().getDescriptor();
			if (descriptor == null) {
				descriptor = SpdFactory.eINSTANCE.createDescriptor();
				command.append(SetCommand.create(getEditingDomain(), getSoftPkg(), SpdPackage.Literals.SOFT_PKG__DESCRIPTOR, descriptor));
			}
			LocalFile file = descriptor.getLocalfile();
			if (file == null) {
				file = SpdFactory.eINSTANCE.createLocalFile();
				command.append(SetCommand.create(getEditingDomain(), descriptor, SpdPackage.Literals.DESCRIPTOR__LOCALFILE, file));
			}
			command.append(SetCommand.create(getEditingDomain(), file, SpdPackage.Literals.LOCAL_FILE__NAME, value));
			getEditingDomain().getCommandStack().execute(command);
		}
	}

	/**
	 * Gets the scd file name.
	 * 
	 * @return the scd file name
	 */
	private String getScdFileName() {
		String retVal = ModelUtil.getScdFileName(getSoftPkg());
		if (retVal == null) {
			retVal = "";
		}
		return retVal;
	}

	/**
	 * Gets the prf file name.
	 * 
	 * @return the prf file name
	 */
	private String getPrfFileName() {
		String retVal = ModelUtil.getPrfFileName(getSoftPkg().getPropertyFile());
		if (retVal == null) {
			retVal = "";
		}
		return retVal;
	}

	/**
	 * Sets the prf file name.
	 * 
	 * @param name the new prf file name
	 */
	private void setPrfFileName(final String name) {
		if (name == null || name.length() == 0) {
			final Command command = SetCommand.create(getEditingDomain(), getSoftPkg(), SpdPackage.Literals.SOFT_PKG__PROPERTY_FILE, null);
			getEditingDomain().getCommandStack().execute(command);
		} else {
			final CompoundCommand command = new CompoundCommand("Set Prf File");
			PropertyFile propfile = getSoftPkg().getPropertyFile();
			if (propfile == null) {
				propfile = SpdFactory.eINSTANCE.createPropertyFile();
				command.append(SetCommand.create(getEditingDomain(), getSoftPkg(), SpdPackage.Literals.SOFT_PKG__PROPERTY_FILE, propfile));
			}
			LocalFile localFile = propfile.getLocalFile();
			if (localFile == null) {
				localFile = SpdFactory.eINSTANCE.createLocalFile();
				command.append(SetCommand.create(getEditingDomain(), propfile, SpdPackage.Literals.PROPERTY_FILE__LOCAL_FILE, localFile));
				propfile.setLocalFile(localFile);
			}
			command.append(SetCommand.create(getEditingDomain(), localFile, SpdPackage.Literals.LOCAL_FILE__NAME, name));
			getEditingDomain().getCommandStack().execute(command);
		}
	}

	/**
	 * Handle prf link activated.
	 */
	private void handlePrfLinkActivated() {
		if (getPrfFile() == null) {
			handleNewPrfFile();
		} else {
			openPrfFile();
		}
	}

	/**
	 * Handle prf link activated.
	 */
	private void handleScdLinkActivated() {
		if (getScdFile() == null) {
			handleNewScdFile();
		} else {
			openScdFile();
		}
	}

	/**
	 * Handle new prf file.
	 */
	private void handleNewPrfFile() {
		final IProject project = getProject();
		if (project != null) {
			final PrfModelWizard wizard = new PrfModelWizard(project);
			wizard.init(getPage().getSite().getWorkbenchWindow().getWorkbench(), new StructuredSelection(project));
			final WizardDialog dialog = new WizardDialog(getPage().getSite().getShell(), wizard);
			if (dialog.open() == Window.OK) {
				final String value = wizard.getModelFile().getProjectRelativePath().toPortableString();
				setPrfFileName(value);
			}
		}
	}

	/**
	 * Handle new scd file.
	 */
	private void handleNewScdFile() {
		final IProject project = getProject();
		if (project != null) {
			final ScdModelWizard wizard = new ScdModelWizard(project);
			wizard.init(getPage().getSite().getWorkbenchWindow().getWorkbench(), new StructuredSelection(project));
			final WizardDialog dialog = new WizardDialog(getPage().getSite().getShell(), wizard);
			if (dialog.open() == Window.OK) {
				final String value = wizard.getModelFile().getProjectRelativePath().toPortableString();
				setScdFileName(value);
			}
		}
	}

	/**
	 * Open prf file.
	 */
	protected void openPrfFile() {
		final IFile prfFile = getPrfFile();
		if (prfFile == null || !prfFile.exists()) {
			return;
		} else {
			getPage().getEditor().setActivePage(PropertiesFormPage.PAGE_ID);
		}
	}

	/**
	 * Gets the prf file.
	 * 
	 * @return the prf file
	 */
	private IFile getPrfFile() {
		return ModelUtil.getPrfFile(getSoftPkg().getPropertyFile());
	}

	/**
	 * Gets the scd file.
	 * 
	 * @return the scd file
	 */
	private IFile getScdFile() {
		return ModelUtil.getScdFile(getSoftPkg().getDescriptor());
	}

	/**
	 * Gets the project.
	 * 
	 * @return the project
	 */
	protected IProject getProject() {
		return ModelUtil.getProject(getSoftPkg());
	}

	/**
	 * @param create
	 */
	protected void execute(final Command command) {
		getEditingDomain().getCommandStack().execute(command);
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
	 * TODO
	 * 
	 * @return
	 */
	private SoftPkg getSoftPkg() {
		if (this.spdResource != null) {
			return SoftPkg.Util.getSoftPkg(this.spdResource);
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refresh(final Resource resource) {
		this.spdResource = resource;

		this.client.setEditable(SCAEditorUtil.isEditableResource(getPage(), this.spdResource));
		for (final Binding binding : this.bindings) {
			binding.dispose();
		}
		this.bindings.clear();

		final SoftPkg model = getSoftPkg();
		if (model == null) {
			return;
		}

		final DataBindingContext context = this.getPage().getEditor().getDataBindingContext();

		this.bindings.add(context.bindValue(WidgetProperties.text(SWT.Modify).observeDelayed(SCAFormEditor.getFieldBindingDelay(),
		        this.client.getIdEntry().getText()),
		        EMFEditObservables.observeValue(getEditingDomain(), model, SpdPackage.Literals.SOFT_PKG__ID),
		        new EMFEmptyStringToNullUpdateValueStrategy(),
		        null));

		this.bindings.add(context.bindValue(WidgetProperties.text(SWT.Modify).observeDelayed(SCAFormEditor.getFieldBindingDelay(),
		        this.client.getVersionEntry().getText()),
		        EMFEditObservables.observeValue(getEditingDomain(), model, SpdPackage.Literals.SOFT_PKG__VERSION),
		        new EMFEmptyStringToNullUpdateValueStrategy(),
		        null));

		this.bindings.add(context.bindValue(WidgetProperties.text(SWT.Modify).observeDelayed(SCAFormEditor.getFieldBindingDelay(),
		        this.client.getNameEntry().getText()),
		        EMFEditObservables.observeValue(getEditingDomain(), model, SpdPackage.Literals.SOFT_PKG__NAME),
		        new EMFEmptyStringToNullUpdateValueStrategy(),
		        null));
		SWTObservables.observeText(this.client.getNameEntry().getText(), SWT.Modify);

		this.bindings.add(context.bindValue(WidgetProperties.text(SWT.Modify).observeDelayed(SCAFormEditor.getFieldBindingDelay(),
		        this.client.getTitleEntry().getText()),
		        EMFEditObservables.observeValue(getEditingDomain(), model, SpdPackage.Literals.SOFT_PKG__TITLE),
		        new EMFEmptyStringToNullUpdateValueStrategy(),
		        null));

		this.bindings.add(context.bindValue(WidgetProperties.text(SWT.Modify).observeDelayed(SCAFormEditor.getFieldBindingDelay(),
		        this.client.getDescriptionEntry().getText()),
		        EMFEditObservables.observeValue(getEditingDomain(), model, SpdPackage.Literals.SOFT_PKG__DESCRIPTION),
		        new EMFEmptyStringToNullUpdateValueStrategy(),
		        null));

		this.bindings.add(context.bindValue(WidgetProperties.text(SWT.Modify).observeDelayed(SCAFormEditor.getFieldBindingDelay(),
		        this.client.getPrfEntry().getText()),
		        EMFEditProperties.value(getEditingDomain(),
		                FeaturePath.fromList(SpdPackage.Literals.SOFT_PKG__PROPERTY_FILE,
		                        SpdPackage.Literals.PROPERTY_FILE__LOCAL_FILE,
		                        SpdPackage.Literals.LOCAL_FILE__NAME)).observe(model),
		        null,
		        null));

		this.bindings.add(context.bindValue(WidgetProperties.text(SWT.Modify).observeDelayed(SCAFormEditor.getFieldBindingDelay(),
		        this.client.getScdEntry().getText()),
		        EMFEditProperties.value(getEditingDomain(),
		                FeaturePath.fromList(SpdPackage.Literals.SOFT_PKG__DESCRIPTOR,
		                        SpdPackage.Literals.DESCRIPTOR__LOCALFILE,
		                        SpdPackage.Literals.LOCAL_FILE__NAME)).observe(model),
		        null,
		        null));

		this.client.getTypeEntry().setValue(model.getType().getLiteral());
	}

}
