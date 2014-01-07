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

import gov.redhawk.ide.codegen.ICodeGeneratorDescriptor;
import gov.redhawk.ide.codegen.ITemplateDesc;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.RedhawkCodegenActivator;
import gov.redhawk.ide.codegen.ui.ICodegenWizardPage;
import gov.redhawk.ide.codegen.ui.RedhawkCodegenUiActivator;
import gov.redhawk.ide.spd.ui.wizard.ImplementationWizardPage;
import gov.redhawk.ide.spd.ui.wizard.ScaImplementationWizard;
import gov.redhawk.model.sca.util.ModelUtil;

import java.lang.reflect.InvocationTargetException;

import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdFactory;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * The Class ImplementationWizard.
 */
public class ImplementationWizard extends Wizard implements ScaImplementationWizard {

	private final ImplementationWizardPage implPage;
	private ICodegenWizardPage codeGenPage;
	private ICodeGeneratorDescriptor lastCodegen;
	private final String projectName;
	private final SoftPkg softPkg;
	private final EditingDomain editingDomain;

	/**
	 * Instantiates a new implementation wizard.
	 */
	public ImplementationWizard(final EditingDomain editingDomain, final String name, final SoftPkg softPkg) {
		this.setWindowTitle("New Implementation");
		Implementation impl = SpdFactory.eINSTANCE.createImplementation();
		this.implPage = new ImplementationWizardPage(name, softPkg);
		this.implPage.setImpl(impl);
		this.setNeedsProgressMonitor(true);
		this.projectName = name;
		this.softPkg = softPkg;
		this.editingDomain = editingDomain;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPages() {
		this.addPage(this.implPage);
	}

	@Override
	public boolean needsPreviousAndNextButtons() {
		return true;
	}

	@Override
	public boolean canFinish() {
		boolean canFinish = super.canFinish();
		final String tempId = this.implPage.getImplSettings().getTemplate();
		final ITemplateDesc template = RedhawkCodegenActivator.getCodeGeneratorTemplatesRegistry().findTemplate(tempId);
		final ITemplateDesc[] templates = RedhawkCodegenActivator.getCodeGeneratorTemplatesRegistry().findTemplatesByCodegen(this.implPage.getImplSettings()
		        .getGeneratorId());
		final boolean hasSettings = (template != null && template.hasSettings()) || (template == null && templates.length > 0);

		// Can finish if:
		// - super() says yes AND
		// --- codegen page is displayed AND completed OR
		// --- first page is displayed AND the generator has no settings
		if (hasSettings) {
			canFinish &= this.codeGenPage != null && this.codeGenPage.isPageComplete();
		}
		return canFinish;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performFinish() {
		try {
			this.getContainer().run(true, false, new IRunnableWithProgress() {

				@Override
				public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					// Nothing to do...all of the work is in the editor
				}
			});
		} catch (final InvocationTargetException e) {
			final IStatus status = new Status(IStatus.ERROR, RedhawkCodegenActivator.PLUGIN_ID, "Failed to create implementation.", e.getCause());
			StatusManager.getManager().handle(status, StatusManager.LOG | StatusManager.SHOW);
			return false;
		} catch (final InterruptedException e) {
			// PASS
		}

		return true;
	}

	/**
	 * Gets the implementation.
	 * 
	 * @return the implementation
	 */
	public Implementation getImplementation() {
		return this.implPage.getImplementation();
	}

	/**
	 * Gets the settings.
	 * 
	 * @return the settings
	 */
	public ImplementationSettings getSettings() {
		return this.implPage.getImplSettings();
	}

	@Override
	public IWizardPage getNextPage(final IWizardPage page) {
		final IProject project = ModelUtil.getProject(this.getSettings());
		if (page == this.implPage) {
			final ICodeGeneratorDescriptor codegen = this.implPage.getCodeGenerator();
			final ITemplateDesc[] templates = RedhawkCodegenActivator.getCodeGeneratorTemplatesRegistry().findTemplatesByCodegen(codegen.getId());

			// If this code generator has settings to display, get the page
			if (templates.length > 0) {
				// Check if the generator has changed since last time, try to
				// reuse the generator page if possible
				if (!codegen.equals(this.lastCodegen)) {
					this.lastCodegen = codegen;

					// findPageByGeneratorId is always guaranteed to return at
					// least one page.
					this.codeGenPage = RedhawkCodegenUiActivator.getCodeGeneratorsRegistry().findPageByGeneratorId(this.getSettings().getGeneratorId())[0];
					this.codeGenPage.setWizard(this);
				}

				// Configure the wizard page with the current settings
				String componentType = ICodeGeneratorDescriptor.COMPONENT_TYPE_RESOURCE;
				if (this.softPkg.getDescriptor() != null) {
					componentType = this.softPkg.getDescriptor().getComponent().getComponentType();
				}
				this.codeGenPage.configure(this.softPkg, this.implPage.getImplementation(), codegen, this.implPage.getImplSettings(), componentType);

				return this.codeGenPage;
			}
		}

		return super.getNextPage(page);
	}

	@Override
	public void generatorChanged(final Implementation impl, final ICodeGeneratorDescriptor codeGeneratorDescriptor) {
		final ITemplateDesc[] templates = RedhawkCodegenActivator.getCodeGeneratorTemplatesRegistry().findTemplatesByCodegen(codeGeneratorDescriptor.getId());
		// If the code generator has settings, update the page as needed
		if (templates.length > 0) {
			if (!codeGeneratorDescriptor.equals(this.lastCodegen)) {
				this.lastCodegen = codeGeneratorDescriptor;

				final ICodegenWizardPage lastPage = this.codeGenPage;

				// findPageByGeneratorId is always guaranteed to return at least
				// one page.
				this.codeGenPage = RedhawkCodegenUiActivator.getCodeGeneratorsRegistry().findPageByGeneratorId(this.getSettings().getGeneratorId())[0];
				this.codeGenPage.setWizard(this);

				if ((this.codeGenPage != lastPage) && (lastPage != null)) {
					lastPage.dispose();
				} else if (this.codeGenPage == lastPage) {
					this.codeGenPage.dispose();
					this.codeGenPage = lastPage;
					this.codeGenPage.setCanFinish(false);
				}
			}
		}
		this.getContainer().updateButtons();
	}

	@Override
	public boolean hasMoreImplementations(final Implementation curImpl) {
		return false;
	}

	public String getName() {
		return this.projectName;
	}

}
