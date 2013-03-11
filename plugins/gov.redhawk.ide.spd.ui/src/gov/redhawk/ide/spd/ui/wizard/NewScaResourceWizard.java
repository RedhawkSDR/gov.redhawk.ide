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
package gov.redhawk.ide.spd.ui.wizard;

import gov.redhawk.ide.codegen.ICodeGeneratorDescriptor;
import gov.redhawk.ide.codegen.IScaComponentCodegen;
import gov.redhawk.ide.codegen.ITemplateDesc;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.RedhawkCodegenActivator;
import gov.redhawk.ide.codegen.ui.ICodegenWizardPage;
import gov.redhawk.ide.codegen.ui.RedhawkCodegenUiActivator;
import gov.redhawk.ide.codegen.util.ImplementationAndSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mil.jpeojtrs.sca.spd.Code;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @since 7.0
 */
public abstract class NewScaResourceWizard extends Wizard implements INewWizard, ScaImplementationWizard {

	private IFile openEditorOn;

	private final List<ImplementationAndSettings> implList = new ArrayList<ImplementationAndSettings>();
	private SoftPkg softPkg;
	private HashMap<String, Boolean> importedSettingsMap = new HashMap<String, Boolean>();
	private String lastSpdFile = "";
	private List<IWizardPage> wizPages;
	private boolean initializing = false;

	private IConfigurationElement fConfig;

	private int firstImplPage;

	private final String componentType;

	public NewScaResourceWizard(final String componentType) {
		super();
		this.componentType = componentType;
	}

	public void init(final IWorkbench workbench, final IStructuredSelection selection) {
		this.firstImplPage = 1;
	}

	@Override
	public void createPageControls(final Composite pageContainer) {
		super.createPageControls(pageContainer);
	}

	public void generatorChanged(final Implementation impl, final ICodeGeneratorDescriptor codeGeneratorDescriptor) {
		if (this.initializing) {
			return;
		}

		// If the code generator has settings, update the page as needed
		if (codeGeneratorDescriptor != null) {
			int implIndex = -1;
			for (int i = 0; i < this.implList.size(); i++) {
				if (this.implList.get(i).getImplementation() == impl) {
					implIndex = i;
					break;
				}
			}
			int codegenIndex = this.wizPages.size();
			IWizardPage oldGenPage = null;
			ImplementationSettings settings = null;

			// Figure out where the codegen page to replace is, based on the
			// current implementation
			for (int i = this.firstImplPage; i < this.wizPages.size(); ++i) {
				final IWizardPage page = this.wizPages.get(i);
				// If this is an implementation page, check to see if its index
				// matches the one for the current implementation
				if (page instanceof ImplementationWizardPage) {
					// If it does, check to see where to put the codegen page
					if (implIndex == 0) {
						settings = ((ImplementationWizardPage) page).getImplSettings();
						// The generator page is going after this one
						codegenIndex = i + 1;

						// Three scenarios:
						// - Implementation is the last in the list and it
						// doesn't have a settings page previously
						// - The implementation is in the middle of the list
						// without a settings page
						// - There is a settings page after this implementation
						// page, replace it
						// Store the generator page if it's next, otherwise
						// we'll be inserting one
						if ((i != (this.wizPages.size() - 1)) && !(this.wizPages.get(i + 1) instanceof ImplementationWizardPage)) {
							oldGenPage = this.wizPages.get(i + 1);
						}
						break;
					}
					// Otherwise decrement and try again
					implIndex--;
				}
			}

			// Allow the wizards to exit now if the generators are the same
			if (this.checkGenerator(settings, codeGeneratorDescriptor)) {
				this.getContainer().updateButtons();
				return;
			}
			ICodegenWizardPage codeGenPage = null;
			boolean createControl = true;
			final ITemplateDesc[] templates = RedhawkCodegenActivator.getCodeGeneratorTemplatesRegistry()
			        .findTemplatesByCodegen(codeGeneratorDescriptor.getId(), this.componentType);

			// Add the new page first
			if (templates.length > 0) {
				// findPageByGeneratorId is always guaranteed to return at least
				// one page.
				codeGenPage = RedhawkCodegenUiActivator.getCodeGeneratorsRegistry().findPageByGeneratorId(settings.getGeneratorId())[0];

				if (oldGenPage != null) {
					// If the generator page is different, add the new one
					if (codeGenPage.getClass() != oldGenPage.getClass()) {
						this.wizPages.add(codegenIndex, codeGenPage);
						// Enable the canFlip if this isn't the last page in the
						// list
						codeGenPage.setCanFlipToNextPage(oldGenPage.canFlipToNextPage());

						// Otherwise get rid of the one we just created and
						// reuse it
					} else {
						codeGenPage.dispose();
						codeGenPage = (ICodegenWizardPage) oldGenPage;
						oldGenPage = null;
						codeGenPage.setCanFinish(false);
						createControl = false;
					}
				} else if (codegenIndex == this.wizPages.size()) {
					this.wizPages.add(codeGenPage);
					// Disable canFlip since this is the last page in the list
					codeGenPage.setCanFlipToNextPage(false);
				} else {
					this.wizPages.add(codegenIndex, codeGenPage);
					// Enable canFlip since this isn't the last page in the list
					codeGenPage.setCanFlipToNextPage(true);
				}
			}

			if (oldGenPage != null) {
				this.wizPages.remove(oldGenPage);
				oldGenPage.dispose();
			}

			// Initialize the settings page
			if (templates.length > 0) {
				if (createControl) {
					codeGenPage.setWizard(this);
				}

				settings.setOutputDir(null); // let the page pick the outputdir

				// Configure the wizard page with the current settings
				codeGenPage.configure(this.getSoftPkg(), impl, codeGeneratorDescriptor, settings, this.componentType);
			}
		}

		// Force an update on the buttons, changing the generator may have
		// enabled or disabled the display of one of the buttons
		this.getContainer().updateButtons();
	}

	public boolean hasMoreImplementations(final Implementation curImpl) {
		// Return true if there are implementations and the current
		// implementation isn't the last one in the list
		return ((this.implList.size() > 0) && (curImpl != this.implList.get(this.implList.size() - 1).getImplementation()));
	}

	@Override
	public IWizardPage getNextPage(final IWizardPage page) {
		if (page instanceof ImplementationWizardPage) {
			final ImplementationWizardPage implPage = (ImplementationWizardPage) page;
			this.importedSettingsMap.put(implPage.getImplementation().getId(), implPage.shouldImportCode());
		}
		return super.getNextPage(page);
	}

	protected void updateEntryPoints() {
		for (final ImplementationAndSettings pair : this.implList) {
			final Implementation impl = pair.getImplementation();
			final ImplementationSettings settings = pair.getImplementationSettings();

			if (impl.getCode() != null) {
				if (!impl.getCode().getLocalFile().toString().equals(settings.getOutputDir())) {
					final ICodeGeneratorDescriptor desc = RedhawkCodegenActivator.getCodeGeneratorsRegistry().findCodegen(settings.getGeneratorId());

					if (desc != null) {
						try {
							final IScaComponentCodegen generator = desc.getGenerator();
							Assert.isNotNull(impl.getSoftPkg());
							final Code code = generator.getInitialCodeSettings(impl.getSoftPkg(), settings, impl);

							impl.setCode(code);
						} catch (final CoreException e) {
							//PASS
						}
					}
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setInitializationData(final IConfigurationElement config, final String propertyName, final Object data) throws CoreException {
		this.fConfig = config;
	}

	public IFile getOpenEditorOn() {
		return this.openEditorOn;
	}

	public void setOpenEditorOn(final IFile openEditorOn) {
		this.openEditorOn = openEditorOn;
	}

	public SoftPkg getSoftPkg() {
		return this.softPkg;
	}

	public void setSoftPkg(final SoftPkg softPkg) {
		this.softPkg = softPkg;
	}

	public HashMap<String, Boolean> getImportedSettingsMap() {
		return this.importedSettingsMap;
	}

	public void setImportedSettingsMap(final HashMap<String, Boolean> importedSettingsMap) {
		this.importedSettingsMap = importedSettingsMap;
	}

	public String getLastSpdFile() {
		return this.lastSpdFile;
	}

	public void setLastSpdFile(final String lastSpdFile) {
		this.lastSpdFile = lastSpdFile;
	}

	public List<IWizardPage> getWizPages() {
		return this.wizPages;
	}

	public void setWizPages(final List<IWizardPage> wizPages) {
		this.wizPages = wizPages;
	}

	public boolean isInitializing() {
		return this.initializing;
	}

	public void setInitializing(final boolean initializing) {
		this.initializing = initializing;
	}

	public IConfigurationElement getfConfig() {
		return this.fConfig;
	}

	public void setfConfig(final IConfigurationElement fConfig) {
		this.fConfig = fConfig;
	}

	public int getFirstImplPage() {
		return this.firstImplPage;
	}

	public void setFirstImplPage(final int firstImplPage) {
		this.firstImplPage = firstImplPage;
	}

	public List<ImplementationAndSettings> getImplList() {
		return this.implList;
	}

	/**
	 * 
	 * @param settings
	 * @param codeGeneratorDescriptor
	 * @return
	 */
	public boolean checkGenerator(final ImplementationSettings settings, final ICodeGeneratorDescriptor codeGeneratorDescriptor) {
		return false;
	}

}
