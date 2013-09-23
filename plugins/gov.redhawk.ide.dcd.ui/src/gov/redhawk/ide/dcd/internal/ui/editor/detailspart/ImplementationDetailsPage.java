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
package gov.redhawk.ide.dcd.internal.ui.editor.detailspart;

import gov.redhawk.common.ui.editor.FormLayoutFactory;
import gov.redhawk.common.ui.parts.FormEntry;
import gov.redhawk.ide.dcd.generator.newnode.ImplPrfFileTemplate;
import gov.redhawk.ide.dcd.internal.ui.editor.ImplementationPage;
import gov.redhawk.ide.dcd.internal.ui.editor.ImplementationsSection;
import gov.redhawk.ide.dcd.internal.ui.editor.composite.ImplementationComposite;
import gov.redhawk.ide.dcd.internal.ui.editor.composite.NodeDependencyComposite;
import gov.redhawk.ide.dcd.ui.DcdUiActivator;
import gov.redhawk.ide.spd.ui.wizard.OsWizard;
import gov.redhawk.ide.spd.ui.wizard.ProcessorWizard;
import gov.redhawk.model.sca.util.ModelUtil;
import gov.redhawk.ui.editor.EMFTableViewerElementSelector;
import gov.redhawk.ui.editor.EMFViewerElementSelector;
import gov.redhawk.ui.editor.FormEntryAdapter;
import gov.redhawk.ui.editor.ScaDetails;
import gov.redhawk.ui.parts.FormEntryBindingFactory;
import gov.redhawk.ui.util.EMFEmptyStringToNullUpdateValueStrategy;
import gov.redhawk.ui.util.EntryUtil;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.LocalFile;
import mil.jpeojtrs.sca.spd.Os;
import mil.jpeojtrs.sca.spd.Processor;
import mil.jpeojtrs.sca.spd.PropertyFile;
import mil.jpeojtrs.sca.spd.SpdFactory;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.util.DceUuidUtil;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.databinding.EMFUpdateValueStrategy;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.ReplaceCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * The Class ImplementationDetailsPage.
 */
public class ImplementationDetailsPage extends ScaDetails {

	private Implementation input;
	private EMFViewerElementSelector osViewerSelector;
	private EMFViewerElementSelector processorViewerSelector;
	private ImplementationComposite implementationComposite;
	private final ImplementationsSection fSection;
	private NodeDependencyComposite dependencyComposite;

	/**
	 * The Constructor.
	 * 
	 * @param fSection the f section
	 */
	public ImplementationDetailsPage(final ImplementationsSection fSection) {
		super(fSection.getPage());
		this.fSection = fSection;
	}

	public DataBindingContext getContext() {
		return getPage().getEditor().getDataBindingContext();
	}

	/**
	 * Creates the implementation section.
	 * 
	 * @param toolkit the toolkit
	 * @param parent the parent
	 */
	private void createImplementationSection(final FormToolkit toolkit, final Composite parent) {
		final Section section = toolkit.createSection(parent, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE
		        | ExpandableComposite.EXPANDED);
		section.clientVerticalSpacing = FormLayoutFactory.SECTION_HEADER_VERTICAL_SPACING;
		section.setText("Implementation");
		section.setDescription("The implementation element contains descriptive information about the particular implementation template for a software device contained in the softpkg element.");
		section.setLayout(FormLayoutFactory.createClearGridLayout(false, 1));
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));

		// Align the master and details section headers (misalignment caused
		// by section toolbar icons)
		getPage().alignSectionHeaders(this.fSection.getSection(), section);

		this.implementationComposite = new ImplementationComposite(section, SWT.NONE, toolkit);
		toolkit.adapt(this.implementationComposite);

		this.implementationComposite.getIdEntry().getButton().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				handleGenerateID();
			}
		});

		this.implementationComposite.getPrfEntry().setFormEntryListener(new FormEntryAdapter() {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public void buttonSelected(final FormEntry entry) {
				final IProject project = getProject();
				if (project != null) {
					final String newPath = EntryUtil.browse(project, getPrfFile(), PrfPackage.FILE_EXTENSION);
					if (newPath != null) {
						setPrfFileName(newPath);
					}
				}
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void linkActivated(final HyperlinkEvent e) {
				handlePrfLinkSelected();
			}
		});

		section.setClient(this.implementationComposite);

	}

	/**
	 * Creates a new PRF File if none exists and user affirms choice. Otherwise
	 * opens the editor.
	 */
	protected void handlePrfLinkSelected() {
		if (this.input.getPropertyFile() == null) {
			final boolean newPRF = MessageDialog.openQuestion(this.getShell(), "Add Property File", "Would you like to add a new PRF file?");
			if (newPRF) {
				final String fileName = "DeviceManager_Linux_i686.prf.xml";
				final String prf = new ImplPrfFileTemplate().generate(null);
				final IFile prfFile = ModelUtil.getProject(this.input).getFile(fileName);
				if (!prfFile.exists()) {
					try {
						prfFile.create(new ByteArrayInputStream(prf.getBytes("UTF-8")), true, null);
					} catch (final CoreException e) {
						DcdUiActivator.logException(e);
					} catch (final UnsupportedEncodingException e) {
						DcdUiActivator.logException(e);
					}
				}
				setPrfFileName(fileName);
			}
		}
		openPrfFile();
	}

	/**
	 * Creates the implementation section.
	 * 
	 * @param toolkit the toolkit
	 * @param parent the parent
	 */
	private void createDependencySection(final FormToolkit toolkit, final Composite parent) {
		final Section section = toolkit.createSection(parent, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE
		        | ExpandableComposite.EXPANDED);
		section.clientVerticalSpacing = FormLayoutFactory.SECTION_HEADER_VERTICAL_SPACING;
		section.setText("Dependencies");
		section.setDescription("Dependencies are used to assign a deployment details for the device.  You must specify at least one dependency.");
		section.setLayout(FormLayoutFactory.createClearGridLayout(false, 1));
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));

		// Align the master and details section headers (misalignment caused
		// by section toolbar icons)
		getPage().alignSectionHeaders(this.fSection.getSection(), section);

		this.dependencyComposite = new NodeDependencyComposite(section, SWT.NONE, toolkit);
		toolkit.adapt(this.dependencyComposite);

		this.dependencyComposite.getAddOsButton().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				handleAddOs();
			}
		});

		this.dependencyComposite.getEditOsButton().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				handleEditOs();
			}
		});

		this.dependencyComposite.getRemoveOsButton().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				handleRemoveOs();
			}
		});

		this.dependencyComposite.getAddProcessorButton().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				handleAddProcessor();
			}
		});

		this.dependencyComposite.getEditProcessorButton().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				handleEditProcessor();
			}
		});

		this.dependencyComposite.getRemoveProcessorButton().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				handleRemoveProcessor();
			}
		});

		section.setClient(this.dependencyComposite);

	}

	/**
	 * 
	 */
	protected void handleGenerateID() {
		execute(SetCommand.create(getEditingDomain(), this.input, SpdPackage.Literals.IMPLEMENTATION__ID, DceUuidUtil.createDceUUID()));
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
	 * Open prf file.
	 */
	private void openPrfFile() {
		final IFile prfFile = getPrfFile();
		if (prfFile == null || !prfFile.exists()) {
			return;
		} else {
			try {
				IDE.openEditor(getPage().getEditor().getSite().getPage(), prfFile, true);
			} catch (final PartInitException e1) {
				final Status status = new Status(IStatus.ERROR, DcdUiActivator.getPluginId(), "Failed to open PRF File: " + getPrfFileName(), e1);
				StatusManager.getManager().handle(status, StatusManager.SHOW | StatusManager.LOG);
			}
		}
	}

	/**
	 * Gets the prf file.
	 * 
	 * @return the prf file
	 */
	private IFile getPrfFile() {
		return ModelUtil.getPrfFile(this.input.getPropertyFile());
	}

	/**
	 * Gets the prf file name.
	 * 
	 * @return the prf file name
	 */
	private String getPrfFileName() {
		return ModelUtil.getPrfFileName(this.input.getPropertyFile());
	}

	/**
	 * Sets the prf file name.
	 * 
	 * @param name the new prf file name
	 */
	private void setPrfFileName(final String name) {
		PropertyFile propfile = this.input.getPropertyFile();

		if (name == null || name.length() == 0) {
			final Command command = SetCommand.create(getEditingDomain(), this.input, SpdPackage.Literals.IMPLEMENTATION__PROPERTY_FILE, null);
			getEditingDomain().getCommandStack().execute(command);
		} else {
			final CompoundCommand command = new CompoundCommand("Set PRF File");
			if (propfile == null) {
				propfile = SpdFactory.eINSTANCE.createPropertyFile();
				command.append(SetCommand.create(getEditingDomain(), this.input, SpdPackage.Literals.IMPLEMENTATION__PROPERTY_FILE, propfile));
			}
			LocalFile localFile = propfile.getLocalFile();
			if (localFile == null) {
				localFile = SpdFactory.eINSTANCE.createLocalFile();
				command.append(SetCommand.create(getEditingDomain(), propfile, SpdPackage.Literals.PROPERTY_FILE__LOCAL_FILE, localFile));
			}
			command.append(SetCommand.create(getEditingDomain(), localFile, SpdPackage.Literals.LOCAL_FILE__NAME, name));

			getEditingDomain().getCommandStack().execute(command);
			this.implementationComposite.getPrfEntry().getText().setText(name);
		}
	}

	private Processor getProcessorViewerSelection() {
		return (Processor) ((IStructuredSelection) this.dependencyComposite.getProcessorViewer().getSelection()).getFirstElement();
	}

	/**
	 * Execute.
	 * 
	 * @param command the command
	 */
	@Override
	public void execute(final Command command) {
		getEditingDomain().getCommandStack().execute(command);
	}

	/**
	 * Handle edit processor.
	 */
	protected void handleEditProcessor() {
		final ProcessorWizard wizard = new ProcessorWizard(null);
		final EObject obj = getProcessorViewerSelection();

		wizard.setProcessor((Processor) obj);

		final WizardDialog dialog = new WizardDialog(getShell(), wizard);

		if (dialog.open() == Window.OK) {
			final Command command = ReplaceCommand.create(getEditingDomain(),
			        this.input,
			        SpdPackage.Literals.IMPLEMENTATION__PROCESSOR,
			        obj,
			        Collections.singleton(wizard.getProcessor()));
			getEditingDomain().getCommandStack().execute(command);
		}
	}

	/**
	 * Handle remove processor.
	 */
	protected void handleRemoveProcessor() {
		final Processor p = getProcessorViewerSelection();
		final Command command = RemoveCommand.create(getEditingDomain(), this.input, SpdPackage.Literals.IMPLEMENTATION__PROCESSOR, p);
		if (command.canExecute()) {
			getEditingDomain().getCommandStack().execute(command);
		}
	}

	/**
	 * Handle add processor.
	 */
	protected void handleAddProcessor() {
		final ProcessorWizard wizard = new ProcessorWizard(null);
		final WizardDialog dialog = new WizardDialog(getShell(), wizard);
		if (dialog.open() == Window.OK) {
			final Command command = AddCommand.create(getEditingDomain(), this.input, SpdPackage.Literals.IMPLEMENTATION__PROCESSOR, wizard.getProcessor());
			getEditingDomain().getCommandStack().execute(command);
		}
	}

	/**
	 * Gets the shell.
	 * 
	 * @return the shell
	 */
	private Shell getShell() {
		return getPage().getSite().getShell();
	}

	/**
	 * Handle os added.
	 */
	protected void handleAddOs() {
		final OsWizard wizard = new OsWizard(null);
		final WizardDialog dialog = new WizardDialog(getShell(), wizard);

		if (dialog.open() == Window.OK) {
			final Command command = AddCommand.create(getEditingDomain(), this.input, SpdPackage.Literals.IMPLEMENTATION__OS, wizard.getOs());
			execute(command);
		}

	}

	private Os getOsViewerSelection() {
		return (Os) ((IStructuredSelection) this.dependencyComposite.getOsViewer().getSelection()).getFirstElement();
	}

	/**
	 * Handle edit os.
	 */
	protected void handleEditOs() {
		final OsWizard wizard = new OsWizard(null);
		final Os obj = getOsViewerSelection();
		// TODO Send in copy instead of original
		// obj = EcoreUtil.copy(obj);

		wizard.setOs(obj);

		final WizardDialog dialog = new WizardDialog(getShell(), wizard);

		if (dialog.open() == Window.OK) {
			final Command command = ReplaceCommand.create(getEditingDomain(),
			        this.input,
			        SpdPackage.Literals.IMPLEMENTATION__OS,
			        obj,
			        Collections.singleton(wizard.getOs()));
			execute(command);
		}
	}

	/**
	 * Handle os removed.
	 */
	protected void handleRemoveOs() {
		final Command command = RemoveCommand.create(getEditingDomain(), this.input, SpdPackage.Literals.IMPLEMENTATION__OS, getOsViewerSelection());
		if (command.canExecute()) {
			getEditingDomain().getCommandStack().execute(command);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ImplementationPage getPage() {
		return (ImplementationPage) super.getPage();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Binding> bind(final DataBindingContext context, final EObject obj) {
		if (!(obj instanceof Implementation)) {
			return null;
		}
		this.input = (Implementation) obj;
		if (this.input.getCode() == null) {
			final Command command = SetCommand.create(getEditingDomain(),
			        this.input,
			        SpdPackage.Literals.IMPLEMENTATION__CODE,
			        SpdFactory.eINSTANCE.createCode());
			execute(command);
		}

		final ArrayList<Binding> retVal = new ArrayList<Binding>();

		retVal.add(FormEntryBindingFactory.bind(context,
		        this.implementationComposite.getDescriptionEntry(),
		        getEditingDomain(),
		        SpdPackage.Literals.IMPLEMENTATION__DESCRIPTION,
		        obj,
		        new EMFEmptyStringToNullUpdateValueStrategy(),
		        null));
		retVal.add(FormEntryBindingFactory.bind(context,
		        this.implementationComposite.getIdEntry(),
		        getEditingDomain(),
		        SpdPackage.Literals.IMPLEMENTATION__ID,
		        obj,
		        new EMFEmptyStringToNullUpdateValueStrategy(),
		        null));

		retVal.add(FormEntryBindingFactory.bind(context,
		        this.implementationComposite.getPrfEntry(),
		        getEditingDomain(),
		        SpdPackage.Literals.IMPLEMENTATION__PROPERTY_FILE,
		        obj,
		        createPrfTargetToModel(),
		        createPrfModelToTarget()));

		this.dependencyComposite.getOsViewer().setInput(this.input);
		this.dependencyComposite.getProcessorViewer().setInput(this.input);

		if (this.osViewerSelector == null) {
			this.osViewerSelector = new EMFTableViewerElementSelector(this.dependencyComposite.getOsViewer());
			this.processorViewerSelector = new EMFTableViewerElementSelector(this.dependencyComposite.getProcessorViewer());
		}
		if (!this.input.eAdapters().contains(this.osViewerSelector)) {
			this.input.eAdapters().add(this.osViewerSelector);
			this.input.eAdapters().add(this.processorViewerSelector);
		}

		return retVal;
	}

	/**
	 * @return
	 */
	private UpdateValueStrategy createPrfModelToTarget() {
		final EMFUpdateValueStrategy strategy = new EMFUpdateValueStrategy();
		strategy.setConverter(new Converter(PropertyFile.class, String.class) {

			@Override
			public Object convert(final Object fromObject) {
				if (fromObject == null) {
					return "";
				}
				final PropertyFile file = (PropertyFile) fromObject;
				final LocalFile lFile = file.getLocalFile();
				if (lFile == null) {
					return "";
				}
				return lFile.getName();
			}

		});
		return strategy;
	}

	/**
	 * @return
	 */
	private UpdateValueStrategy createPrfTargetToModel() {
		final EMFEmptyStringToNullUpdateValueStrategy strategy = new EMFEmptyStringToNullUpdateValueStrategy();
		strategy.setConverter(new Converter(String.class, PropertyFile.class) {

			@Override
			public Object convert(final Object fromObject) {
				if (fromObject == null) {
					return null;
				}
				final PropertyFile file = SpdFactory.eINSTANCE.createPropertyFile();
				file.setType("PRF");
				final LocalFile lFile = SpdFactory.eINSTANCE.createLocalFile();
				lFile.setName(fromObject.toString());
				file.setLocalFile(lFile);
				return file;
			}

		});
		return strategy;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createSpecificContent(final Composite parent) {
		final FormToolkit toolkit = getManagedForm().getToolkit();

		createImplementationSection(toolkit, parent);

		createDependencySection(toolkit, parent);
	}

}
