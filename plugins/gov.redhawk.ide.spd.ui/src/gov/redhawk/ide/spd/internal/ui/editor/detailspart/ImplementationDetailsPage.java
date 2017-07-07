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
import gov.redhawk.ide.codegen.CodegenPackage;
import gov.redhawk.ide.codegen.CodegenUtil;
import gov.redhawk.ide.codegen.ICodeGeneratorDescriptor;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.RedhawkCodegenActivator;
import gov.redhawk.ide.codegen.ui.ICodegenComposite;
import gov.redhawk.ide.codegen.ui.RedhawkCodegenUiActivator;
import gov.redhawk.ide.prf.ui.wizard.PrfModelWizard;
import gov.redhawk.ide.spd.internal.ui.editor.ImplementationPage;
import gov.redhawk.ide.spd.internal.ui.editor.ImplementationsSection;
import gov.redhawk.ide.spd.internal.ui.editor.composite.CodeComposite;
import gov.redhawk.ide.spd.internal.ui.editor.composite.ComponentDependencyComposite;
import gov.redhawk.ide.spd.internal.ui.editor.composite.ImplementationComposite;
import gov.redhawk.ide.spd.ui.ComponentUiPlugin;
import gov.redhawk.ide.spd.ui.wizard.DependencyWizard;
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
import gov.redhawk.ui.util.SCAEditorUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.spd.Code;
import mil.jpeojtrs.sca.spd.Compiler;
import mil.jpeojtrs.sca.spd.Dependency;
import mil.jpeojtrs.sca.spd.HumanLanguage;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.LocalFile;
import mil.jpeojtrs.sca.spd.Os;
import mil.jpeojtrs.sca.spd.Processor;
import mil.jpeojtrs.sca.spd.ProgrammingLanguage;
import mil.jpeojtrs.sca.spd.PropertyFile;
import mil.jpeojtrs.sca.spd.Runtime;
import mil.jpeojtrs.sca.spd.SpdFactory;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.util.DceUuidUtil;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.databinding.EMFUpdateValueStrategy;
import org.eclipse.emf.databinding.edit.EMFEditObservables;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.ReplaceCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
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
	private ComponentDependencyComposite dependencyComposite;
	private CodeComposite codeComposite;
	private ICodegenComposite codeGenerationComposite;
	private final ImplementationsSection fSection;
	private boolean bindingInProgress;
	private String lastCodegen = "";
	private Composite parent;
	private Section codegenSection;
	private FormToolkit toolkit;
	private boolean editable;

	/**
	 * The Constructor.
	 * 
	 * @param fSection the f section
	 */
	public ImplementationDetailsPage(final ImplementationsSection fSection) {
		super(fSection.getPage());
		this.fSection = fSection;
	}

	/**
	 * Gets the implementation settings.
	 * 
	 * @return the implementation settings
	 */
	private ImplementationSettings getImplementationSettings() {
		return CodegenUtil.getImplementationSettings(this.input);
	}

	/**
	 * Creates the code section.
	 * 
	 * @param toolkit the toolkit
	 * @param parent the parent
	 */
	private void createCodeSection(final FormToolkit toolkit, final Composite parent) {
		final Section section = toolkit.createSection(parent, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE
			| ExpandableComposite.COMPACT);
		section.clientVerticalSpacing = FormLayoutFactory.SECTION_HEADER_VERTICAL_SPACING;
		section.setText("Code");
		section.setDescription("The code element will be used to indicate the" + " local filename of the code that is described by the softpkg element,"
			+ " for a specific implementation of the resource.");
		section.setLayout(FormLayoutFactory.createClearGridLayout(false, 1));
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));

		this.codeComposite = new CodeComposite(section, SWT.None, toolkit);
		toolkit.adapt(this.codeComposite);
		section.setClient(this.codeComposite);

	}

	public DataBindingContext getContext() {
		return getPage().getEditor().getDataBindingContext();
	}

	/**
	 * Sets the code file name.
	 * 
	 * @param value the value
	 */
	protected void setCodeFileName(final String value) {
		Code code = this.input.getCode();
		if (value == null || value.length() == 0) {
			final Command command = SetCommand.create(getEditingDomain(), code, SpdPackage.Literals.CODE__LOCAL_FILE, null);
			getEditingDomain().getCommandStack().execute(command);
		} else {
			final CompoundCommand command = new CompoundCommand("Set Code File Name");
			if (code == null) {
				code = SpdFactory.eINSTANCE.createCode();
				command.append(SetCommand.create(getEditingDomain(), this.input, SpdPackage.Literals.IMPLEMENTATION__CODE, code));
			}
			LocalFile localFile = code.getLocalFile();
			if (localFile == null) {
				localFile = SpdFactory.eINSTANCE.createLocalFile();
				command.append(SetCommand.create(getEditingDomain(), code, SpdPackage.Literals.CODE__LOCAL_FILE, localFile));
			}
			command.append(SetCommand.create(getEditingDomain(), localFile, SpdPackage.Literals.LOCAL_FILE__NAME, value));
			getEditingDomain().getCommandStack().execute(command);
		}
	}

	/**
	 * Creates the code generation details section.
	 * 
	 * @param toolkit the toolkit
	 * @param parent the parent
	 * @param generatorId
	 */
	private void createCodeGenerationDetailsSection(final FormToolkit toolkit, final Composite parent, final String generatorId) {
		if (this.codegenSection == null) {
			this.codegenSection = toolkit.createSection(parent, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE
				| ExpandableComposite.COMPACT);
			this.codegenSection.clientVerticalSpacing = FormLayoutFactory.SECTION_HEADER_VERTICAL_SPACING;
			this.codegenSection.setText("Code Generation Details");
			this.codegenSection.setDescription("Set configuration values of this implementation's code generation properties.");
			this.codegenSection.setLayout(FormLayoutFactory.createClearGridLayout(false, 1));
			this.codegenSection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
		}

		this.codeGenerationComposite = RedhawkCodegenUiActivator.getCodeGeneratorsLanguageRegistry().findCompositeByGeneratorId(this.input, generatorId,
			this.codegenSection, SWT.None, toolkit)[0];

		toolkit.adapt((Composite) this.codeGenerationComposite);
		this.codegenSection.setClient((Composite) this.codeGenerationComposite);

		this.addGeneratorListeners(getPage().getEditor().getActionBarContributor().getActionBars());
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
		section.setDescription("The implementation element contains descriptive information about the particular implementation template for a resource contained in the softpkg element.");
		section.setLayout(FormLayoutFactory.createClearGridLayout(false, 1));
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));

		// Align the master and details section headers (misalignment caused by section toolbar icons)
		getPage().alignSectionHeaders(this.fSection.getSection(), section);

		this.implementationComposite = new ImplementationComposite(section, SWT.NONE, toolkit, getPage().isSoftpackageLibrary());
		toolkit.adapt(this.implementationComposite);

		if (this.implementationComposite.getPrfEntry() != null) {
			this.implementationComposite.getPrfEntry().setFormEntryListener(new FormEntryAdapter() {
				/**
				 * {@inheritDoc}
				 */
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

				/**
				 * {@inheritDoc}
				 */
				@Override
				public void linkActivated(final HyperlinkEvent e) {
					handlePrfLinkSelected();
				}
			});
		}

		section.setClient(this.implementationComposite);

	}

	/**
	 * Creates a new PRF File if none exists and user affirms choice. Otherwise
	 * opens the editor.
	 */
	protected void handlePrfLinkSelected() {
		if (getPrfFile() == null) {
			handleNewPrfFile();
		} else {
			openPrfFile();
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
				openPrfFile();
			}
		}
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
		section.setDescription("Dependencies are used to assign a resource to a suitable device and to allocate capacity on a device.  You must specify at least one dependency.");
		section.setLayout(FormLayoutFactory.createClearGridLayout(false, 1));
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));

		// Align the master and details section headers (misalignment caused
		// by section toolbar icons)
		getPage().alignSectionHeaders(this.fSection.getSection(), section);

		this.dependencyComposite = new ComponentDependencyComposite(section, SWT.NONE, toolkit);
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

		this.dependencyComposite.getAddDependencyButton().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				handleAddDependency();
			}
		});

		this.dependencyComposite.getEditDependencyButton().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				handleEditDependency();
			}
		});

		this.dependencyComposite.getRemoveDependencyButton().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				handleRemoveDependency();
			}
		});

		section.setClient(this.dependencyComposite);

	}

	@Override
	public void dispose() {
		if (this.input.eAdapters().contains(this.osViewerSelector)) {
			this.input.eAdapters().remove(this.osViewerSelector);
			this.input.eAdapters().remove(this.processorViewerSelector);
		}
		super.dispose();
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
				final Status status = new Status(IStatus.ERROR, ComponentUiPlugin.getPluginId(), "Failed to open PRF File: " + getPrfFileName(), e1);
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
	 * Handle edit processor.
	 */
	protected void handleEditProcessor() {
		final ProcessorWizard wizard = new ProcessorWizard(this.getImplementationSettings());
		final EObject obj = getProcessorViewerSelection();

		wizard.setProcessor((Processor) obj);

		final WizardDialog dialog = new WizardDialog(getShell(), wizard);

		if (dialog.open() == Window.OK) {
			final Command command = ReplaceCommand.create(getEditingDomain(), this.input, SpdPackage.Literals.IMPLEMENTATION__PROCESSOR, obj,
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
		final ProcessorWizard wizard = new ProcessorWizard(this.getImplementationSettings());
		final WizardDialog dialog = new WizardDialog(getShell(), wizard);
		if (dialog.open() == Window.OK) {
			final Command command = AddCommand.create(getEditingDomain(), this.input, SpdPackage.Literals.IMPLEMENTATION__PROCESSOR, wizard.getProcessor());
			getEditingDomain().getCommandStack().execute(command);
		}
	}

	private Dependency getDependencyViewerSelection() {
		return (Dependency) ((IStructuredSelection) this.dependencyComposite.getDependencyViewer().getSelection()).getFirstElement();
	}

	/**
	 * Handle edit processor.
	 */
	protected void handleEditDependency() {
		final DependencyWizard wizard = new DependencyWizard();
		final EObject obj = getDependencyViewerSelection();

		wizard.setDependency((Dependency) obj);

		final WizardDialog dialog = new WizardDialog(getShell(), wizard);

		if (dialog.open() == Window.OK) {
			final Command command = ReplaceCommand.create(getEditingDomain(), this.input, SpdPackage.Literals.IMPLEMENTATION__DEPENDENCY, obj,
				Collections.singleton(wizard.getDependency()));
			getEditingDomain().getCommandStack().execute(command);
		}
	}

	/**
	 * Handle remove processor.
	 */
	protected void handleRemoveDependency() {
		final Dependency p = getDependencyViewerSelection();
		final Command command = RemoveCommand.create(getEditingDomain(), this.input, SpdPackage.Literals.IMPLEMENTATION__DEPENDENCY, p);
		if (command.canExecute()) {
			getEditingDomain().getCommandStack().execute(command);
		}
	}

	/**
	 * Handle add processor.
	 */
	protected void handleAddDependency() {
		final DependencyWizard wizard = new DependencyWizard();
		final WizardDialog dialog = new WizardDialog(getShell(), wizard);
		if (dialog.open() == Window.OK) {
			final Command command = AddCommand.create(getEditingDomain(), this.input, SpdPackage.Literals.IMPLEMENTATION__DEPENDENCY, wizard.getDependency());
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
		final OsWizard wizard = new OsWizard(this.getImplementationSettings());

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
		final OsWizard wizard = new OsWizard(this.getImplementationSettings());
		final Os obj = getOsViewerSelection();
		// TODO Send in copy instead of original
		// obj = EcoreUtil.copy(obj);

		wizard.setOs(obj);

		final WizardDialog dialog = new WizardDialog(getShell(), wizard);

		if (dialog.open() == Window.OK) {
			final Command command = ReplaceCommand.create(getEditingDomain(), this.input, SpdPackage.Literals.IMPLEMENTATION__OS, obj,
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

	private String getText(final EObject obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof Compiler) {
			final Compiler compiler = (Compiler) obj;
			return compiler.getName() + ((compiler.getVersion() == null) ? "" : " (" + compiler.getVersion() + ")"); // SUPPRESS
																														// CHECKSTYLE
																														// AvoidInLine
		} else if (obj instanceof HumanLanguage) {
			final HumanLanguage lang = (HumanLanguage) obj;
			return lang.getName();
		} else if (obj instanceof ProgrammingLanguage) {
			final ProgrammingLanguage lang = (ProgrammingLanguage) obj;
			return lang.getName() + ((lang.getVersion() == null) ? "" : " (" + lang.getVersion() + ")"); // SUPPRESS
																											// CHECKSTYLE
																											// AvoidInLine
		} else if (obj instanceof Runtime) {
			final Runtime runtime = (Runtime) obj;
			return runtime.getName() + ((runtime.getVersion() == null) ? "" : " (" + runtime.getVersion() + ")"); // SUPPRESS
																													// CHECKSTYLE
																													// AvoidInLine
		}

		final IItemLabelProvider lp = (IItemLabelProvider) getAdapterFactory().adapt(obj, IItemLabelProvider.class);
		if (lp != null) {
			return lp.getText(obj);
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Binding> bind(final DataBindingContext context, final EObject obj) {
		this.bindingInProgress = true;
		if (!(obj instanceof Implementation)) {
			return null;
		}
		this.input = (Implementation) obj;
		if (this.input.getCode() == null) {
			final Command command = SetCommand.create(getEditingDomain(), this.input, SpdPackage.Literals.IMPLEMENTATION__CODE,
				SpdFactory.eINSTANCE.createCode());
			execute(command);
		}

		final ArrayList<Binding> retVal = new ArrayList<Binding>();
		final ImplementationSettings implSettings = this.getImplementationSettings();

		if (implSettings != null) {
			// Check if the generator has changed. If so, change the composite.
			if (!this.lastCodegen.equals(implSettings.getGeneratorId())) {
				if (this.codegenSection != null) {
					this.codegenSection.getClient().dispose();
				}

				// Create the new composite
				createCodeGenerationDetailsSection(this.toolkit, this.parent, implSettings.getGeneratorId());
				// Re-layout and redraw the section
				this.codegenSection.layout();
				this.codegenSection.redraw();
			}
			// Update the last codegen and rebind the composite
			this.lastCodegen = implSettings.getGeneratorId();
			this.codeGenerationComposite.bind(retVal, getEditingDomain(), context, this.input, implSettings);

//			if (implSettings.getGeneratorId().equals(ManualGeneratorPlugin.MANUAL_GENERATOR_ID)) {
//				this.codegenSection.setExpanded(false);
//				this.codegenSection.setEnabled(false);
//			} else {
//				this.codegenSection.setEnabled(true);
//			}	
			this.codegenSection.setEnabled(true);
		}

		this.implementationComposite.getCompilerEntry().setValue(getText(this.input.getCompiler()));

		this.implementationComposite.getHumanLangEntry().setValue(getText(this.input.getHumanLanguage()));

		this.implementationComposite.getProgLangEntry().setValue(getText(this.input.getProgrammingLanguage()));

		this.implementationComposite.getRuntimeEntry().setValue(getText(this.input.getRuntime()));

		retVal.add(FormEntryBindingFactory.bind(context, this.implementationComposite.getDescriptionEntry(), getEditingDomain(),
			SpdPackage.Literals.IMPLEMENTATION__DESCRIPTION, obj, new EMFEmptyStringToNullUpdateValueStrategy(), null));
		retVal.add(FormEntryBindingFactory.bind(context, this.implementationComposite.getIdEntry(), getEditingDomain(), SpdPackage.Literals.IMPLEMENTATION__ID,
			obj, new EMFEmptyStringToNullUpdateValueStrategy(), null));

		if (!this.getPage().isSoftpackageLibrary()) {
			retVal.add(FormEntryBindingFactory.bind(context, this.implementationComposite.getPrfEntry(), getEditingDomain(),
				SpdPackage.Literals.IMPLEMENTATION__PROPERTY_FILE, obj, createPrfTargetToModel(), createPrfModelToTarget()));
		}
		this.dependencyComposite.getOsViewer().setInput(this.input);
		this.dependencyComposite.getProcessorViewer().setInput(this.input);
		this.dependencyComposite.getDependencyViewer().setInput(this.input);

		retVal.add(FormEntryBindingFactory.bind(context, this.codeComposite.getEntryPoint(), getEditingDomain(), SpdPackage.Literals.CODE__ENTRY_POINT,
			this.input.getCode(), new EMFEmptyStringToNullUpdateValueStrategy(), null));
		retVal.add(FormEntryBindingFactory.bind(context, this.codeComposite.getCodePriority(), getEditingDomain(), SpdPackage.Literals.CODE__PRIORITY,
			this.input.getCode(), new EMFEmptyStringToNullUpdateValueStrategy(), null));
		retVal.add(FormEntryBindingFactory.bind(context, this.codeComposite.getLocalFile(), getEditingDomain(), SpdPackage.Literals.CODE__LOCAL_FILE,
			this.input.getCode(), createCodeLocalFileTargetToModel(), createCodeLocalFileModelToTarget()));
		retVal.add(FormEntryBindingFactory.bind(context, this.codeComposite.getCodeStackSize(), getEditingDomain(), SpdPackage.Literals.CODE__STACK_SIZE,
			this.input.getCode(), new EMFEmptyStringToNullUpdateValueStrategy(), null));
		retVal.add(context.bindValue(ViewersObservables.observeSingleSelection(this.codeComposite.getCodeTypeViewer()),
			EMFEditObservables.observeValue(getEditingDomain(), this.input.getCode(), SpdPackage.Literals.CODE__TYPE), null, null));

		this.codeComposite.setFieldsEditable(implSettings);

		if (this.osViewerSelector == null) {
			this.osViewerSelector = new EMFTableViewerElementSelector(this.dependencyComposite.getOsViewer());
			this.processorViewerSelector = new EMFTableViewerElementSelector(this.dependencyComposite.getProcessorViewer());
		}
		if (!this.input.eAdapters().contains(this.osViewerSelector)) {
			this.input.eAdapters().add(this.osViewerSelector);
			this.input.eAdapters().add(this.processorViewerSelector);
		}

		this.setEditable();

		this.bindingInProgress = false;

		return retVal;
	}

	/**
	 * @return
	 */
	private UpdateValueStrategy createCodeLocalFileModelToTarget() {
		final EMFUpdateValueStrategy strategy = new EMFUpdateValueStrategy();
		strategy.setConverter(new Converter(LocalFile.class, String.class) {

			@Override
			public Object convert(final Object fromObject) {
				if (!(fromObject instanceof LocalFile)) {
					return "";
				}
				final LocalFile lFile = (LocalFile) fromObject;
				return lFile.getName();
			}

		});
		return strategy;
	}

	/**
	 * @return
	 */
	private UpdateValueStrategy createCodeLocalFileTargetToModel() {
		final EMFEmptyStringToNullUpdateValueStrategy strategy = new EMFEmptyStringToNullUpdateValueStrategy();
		strategy.setConverter(new Converter(String.class, LocalFile.class) {

			@Override
			public Object convert(final Object fromObject) {
				if (fromObject == null) {
					return null;
				}
				final LocalFile lFile = SpdFactory.eINSTANCE.createLocalFile();
				lFile.setName(fromObject.toString());
				return lFile;
			}

		});
		return strategy;
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
		this.toolkit = getManagedForm().getToolkit();
		this.parent = parent;

		createImplementationSection(this.toolkit, this.parent);

		createDependencySection(this.toolkit, this.parent);

		createCodeSection(this.toolkit, this.parent);

		final IActionBars actionBars = getPage().getEditor().getEditorSite().getActionBars();
		addListeners(actionBars);

	}

	/**
	 * 
	 */
	private void addListeners(final IActionBars actionBars) {
	}

	/**
	 * 
	 */
	private void addGeneratorListeners(final IActionBars actionBars) {
		this.codeGenerationComposite.getOutputDirEntry().getText().addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent e) {
				ImplementationDetailsPage.this.updateCodeEntries(((Text) e.widget).getText());
			}
		});

		this.codeGenerationComposite.getOutputDirEntry().setFormEntryListener(new FormEntryAdapter(actionBars) {
			@Override
			public void buttonSelected(final FormEntry entry) {
				final IProject project = getProject();
				if (project != null) {
					IFolder folder = null;
					if (!entry.getText().getText().isEmpty()) {
						folder = project.getFolder(entry.getText().getText());
					}
					final IFolder newPath = EntryUtil.browseDir(project, folder);
					if ((newPath != null) && (newPath != folder)) {
						updateCodeEntries(newPath.getProjectRelativePath().toString());

					}
				}
			}
		});
	}

	protected void updateCodeEntries(final String dir) {
		if (this.bindingInProgress) {
			return;
		}

		final Implementation impl = this.input;
		final ImplementationSettings settings = getImplementationSettings();
		if (settings == null) {
			return;
		}
		final String outputDir = (dir == null) ? settings.getOutputDir() : dir; // SUPPRESS CHECKSTYLE AvoidInLine

		String commandName = "Change output directory";
		if (dir == null) {
			commandName = "Change implementation name";
		}

		final CompoundCommand command = new CompoundCommand(commandName);
		if (dir != null) {
			command.append(SetCommand.create(getEditingDomain(), settings, CodegenPackage.Literals.IMPLEMENTATION_SETTINGS__OUTPUT_DIR, outputDir,
				CommandParameter.NO_INDEX));
		}

		final ICodeGeneratorDescriptor generator = RedhawkCodegenActivator.getCodeGeneratorsRegistry().findCodegen(settings.getGeneratorId());
		final ImplementationSettings tmpSettings = EcoreUtil.copy(settings);
		tmpSettings.setOutputDir(outputDir);
		Code code;

		try {
			Assert.isNotNull(impl.getSoftPkg());
			code = generator.getGenerator().getInitialCodeSettings(impl.getSoftPkg(), tmpSettings, impl);
		} catch (final CoreException e) {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, ComponentUiPlugin.PLUGIN_ID, e.getMessage(), e.getCause()),
				StatusManager.LOG | StatusManager.SHOW);
			return;
		}
		command.append(SetCommand.create(getEditingDomain(), impl.getCode(), SpdPackage.Literals.CODE__ENTRY_POINT, code.getEntryPoint(),
			CommandParameter.NO_INDEX));
		command.append(SetCommand.create(getEditingDomain(), impl.getCode(), SpdPackage.Literals.CODE__LOCAL_FILE, code.getLocalFile(),
			CommandParameter.NO_INDEX));
		getEditingDomain().getCommandStack().execute(command);
	}

	private void setEditable() {
		this.editable = SCAEditorUtil.isEditableResource(getPage(), this.input.eResource());
		this.implementationComposite.setEditable(this.editable);
		this.codeComposite.setEditable(this.editable);
		// this.codeGenerationComposite.setEditable(editable);
		this.dependencyComposite.setEditable(this.editable);
	}

}
