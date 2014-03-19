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
package gov.redhawk.ide.internal.ui.templates;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.pde.core.plugin.IPluginBase;
import org.eclipse.pde.core.plugin.IPluginElement;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.IPluginModelFactory;
import org.eclipse.pde.core.plugin.IPluginReference;
import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.templates.AbstractTemplateSection;
import org.eclipse.pde.ui.templates.ITemplateSection;
import org.eclipse.pde.ui.templates.PluginReference;
import org.eclipse.pde.ui.templates.TemplateOption;

/**
 * 
 */
public class CorbaControlPanelTemplateSection extends BaseControlPanelTemplate implements ITemplateSection {

	/**
	 * Constructor for MultiPageEditorTemplate.
	 */
	public CorbaControlPanelTemplateSection() {
		setPageCount(1);
		createOptions();
	}

	@Override
	public String getSectionId() {
		return "corbaControlPanel"; //$NON-NLS-1$
	}

	@Override
	public IPluginReference[] getDependencies(final String schemaVersion) {
		if (schemaVersion != null) {
			final IPluginReference[] dep = new IPluginReference[5]; // SUPPRESS CHECKSTYLE MagicNumber
			dep[0] = new PluginReference("org.eclipse.ui", null, 0); //$NON-NLS-1$
			dep[1] = new PluginReference("org.eclipse.core.runtime", null, 0); //$NON-NLS-1$
			dep[2] = new PluginReference("gov.redhawk.sca.ui", null, 0); //$NON-NLS-1$
			dep[3] = new PluginReference("gov.redhawk.sca.model", null, 0); // SUPPRESS CHECKSTYLE MagicNumber //$NON-NLS-1$
			dep[4] = new PluginReference("org.eclipse.emf.edit.ui", null, 0); // SUPPRESS CHECKSTYLE MagicNumber //$NON-NLS-1$
			return dep;
		}
		return super.getDependencies(schemaVersion);
	}

	/*
	 * @see ITemplateSection#getNumberOfWorkUnits()
	 */
	@Override
	public int getNumberOfWorkUnits() {
		return super.getNumberOfWorkUnits() + 1;
	}

	private void createOptions() {
		// first page
		addOption(AbstractTemplateSection.KEY_PACKAGE_NAME, "&Java Package Name:", (String) null, 0).setRequired(true);
		addOption("editorClassName", //$NON-NLS-1$
		        "&Editor Class Name:", "SampleCorbaControlPanel", //$NON-NLS-1$
		        0).setRequired(false);
		addOption("contributorClassName", //$NON-NLS-1$
		        "&Editor Contributor Class Name:", "SampleCorbaControlPanelContributor", //$NON-NLS-1$
		        0).setRequired(true);
		addOption("editorName", //$NON-NLS-1$
		        "&Control Panel Name:", "Sample Corba Control Panel", 0).setRequired(true);

		addOption("contentTypeNname", //$NON-NLS-1$
		        "&Content Type Name:", "Sample Corba Content Type", 0);
		addOption("contentTypeProfileId", //$NON-NLS-1$
		        "&Profile ID:", "", 0).setRequired(false);
		addOption("contentTypeCorbaRepId", //$NON-NLS-1$
		        "&CORBA REP ID:", "IDL:CF/Application:1.0", 0).setRequired(true);
		addOption("contentTypePriority", "Priority:",
		        new String[][] { new String[] { "LOW", "LOW" }, new String[] { "NORMAL", "NORMAL" }, new String[] { "HIGH", "HIGH" } }, "NORMAL", 0)
		        .setRequired(true);
	}

	@Override
	protected void initializeFields(final IFieldData data) {
		// In a new project wizard, we don't know this yet - the
		// model has not been created
		final String id = data.getId();
		initializeOption(AbstractTemplateSection.KEY_PACKAGE_NAME, getFormattedPackageName(id));
	}

	@Override
	public void initializeFields(final IPluginModelBase model) {
		// In the new extension wizard, the model exists so
		// we can initialize directly from it
		final String pluginId = model.getPluginBase().getId();
		initializeOption(AbstractTemplateSection.KEY_PACKAGE_NAME, getFormattedPackageName(pluginId));
	}

	@Override
	public boolean isDependentOnParentWizard() {
		return true;
	}

	@Override
	public void addPages(final Wizard wizard) {
		final WizardPage page = createPage(0, null);
		page.setTitle("Sample Corba Control Panel");
		page.setDescription("Choose the options that will be used to generate the control panel.");
		wizard.addPage(page);
		markPagesAdded();
	}

	@Override
	public void validateOptions(final TemplateOption source) {
		if (source.isRequired() && source.isEmpty()) {
			flagMissingRequiredOption(source);
		} else {
			validateContainerPage(source);
		}
	}

	private void validateContainerPage(final TemplateOption source) {
		final TemplateOption[] allPageOptions = getOptions(0);
		for (int i = 0; i < allPageOptions.length; i++) {
			final TemplateOption nextOption = allPageOptions[i];
			if (nextOption.isRequired() && nextOption.isEmpty()) {
				flagMissingRequiredOption(nextOption);
				return;
			}
		}
		resetPageState();
	}

	@Override
	protected void updateModel(final IProgressMonitor monitor) throws CoreException {
		final IPluginBase plugin = this.model.getPluginBase();
		final IPluginModelFactory factory = this.model.getPluginFactory();

		final String editorClassName = getStringOption(AbstractTemplateSection.KEY_PACKAGE_NAME) + "." + getStringOption("editorClassName"); //$NON-NLS-1$ //$NON-NLS-2$
		final String contributorClassName = getStringOption(AbstractTemplateSection.KEY_PACKAGE_NAME) + "." //$NON-NLS-1$
		        + getStringOption("contributorClassName"); //$NON-NLS-1$

		final IPluginExtension extension = createExtension("org.eclipse.ui.editors", true); //$NON-NLS-1$
		final IPluginElement editorElement = factory.createElement(extension);
		createEditorElement(editorElement, editorClassName, contributorClassName);
		extension.add(editorElement);
		if (!extension.isInTheModel()) {
			plugin.add(extension);
		}

		final String contentTypeId = editorClassName + ".contentType";
		final String priority = getStringOption("contentTypePriority");

		final IPluginExtension contentTypeExtension = createExtension("gov.redhawk.sca.ui.scaContentTypes", true); //$NON-NLS-1$
		final IPluginElement contentTypeElement = factory.createElement(contentTypeExtension);
		createContentTypeElement(contentTypeElement, contentTypeId, priority, factory);
		contentTypeExtension.add(contentTypeElement);

		final IPluginElement bindingElement = factory.createElement(contentTypeExtension);
		createBindingElement(bindingElement, editorClassName, contentTypeId, priority);
		contentTypeExtension.add(bindingElement);

		if (!contentTypeExtension.isInTheModel()) {
			plugin.add(contentTypeExtension);
		}
	}

	/**
	 * @param bindingElement
	 * @param editorClassName
	 * @param contentTypeId
	 * @param priority
	 * @throws CoreException
	 */
	private void createBindingElement(final IPluginElement bindingElement, final String editorClassName, final String contentTypeId, final String priority)
	        throws CoreException {
		bindingElement.setName("contentTypeBinding"); //$NON-NLS-1$
		bindingElement.setAttribute("editorId", editorClassName); //$NON-NLS-1$
		bindingElement.setAttribute("contentTypeId", contentTypeId); //$NON-NLS-1$
		bindingElement.setAttribute("priority", priority); //$NON-NLS-1$
	}

	/**
	 * @param contentTypeElement
	 * @param contentTypeId
	 * @param priority
	 * @throws CoreException
	 */
	private void createContentTypeElement(final IPluginElement contentTypeElement, final String contentTypeId, final String priority,
	        final IPluginModelFactory factory) throws CoreException {
		contentTypeElement.setName("contentType"); //$NON-NLS-1$
		contentTypeElement.setAttribute("id", contentTypeId); //$NON-NLS-1$
		contentTypeElement.setAttribute("name", getStringOption("contentTypeNname")); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeElement.setAttribute("priority", priority); //$NON-NLS-1$

		final IPluginElement describerElement = factory.createElement(contentTypeElement);
		createDescriberElement(describerElement, factory);
		contentTypeElement.add(describerElement);
	}

	/**
	 * @param describerElement
	 * @param factory
	 * @throws CoreException
	 */
	private void createDescriberElement(final IPluginElement describerElement, final IPluginModelFactory factory) throws CoreException {
		describerElement.setName("describer");
		describerElement.setAttribute("class", "gov.redhawk.sca.ui.editors.ScaContentDescriber");
		describerElement.setAttribute("plugin", "gov.redhawk.sca.ui");
		final String profileId = getStringOption("contentTypeProfileId");
		if (profileId != null && profileId.trim().length() > 0) {
			final IPluginElement parameter = factory.createElement(describerElement);
			parameter.setName("parameter");
			parameter.setAttribute("name", "profileId");
			parameter.setAttribute("value", profileId);
			describerElement.add(parameter);
		}

		final String repId = getStringOption("contentTypeCorbaRepId");
		if (repId != null && repId.trim().length() > 0) {
			final IPluginElement parameter = factory.createElement(describerElement);
			parameter.setName("parameter");
			parameter.setAttribute("name", "corbaRepId");
			parameter.setAttribute("value", repId);
			describerElement.add(parameter);
		}
	}

	/**
	 * @param editorClassName
	 * @param contributorClassName
	 * @param createElement
	 * @throws CoreException
	 */
	private void createEditorElement(final IPluginElement editorElement, final String editorClassName, final String contributorClassName) throws CoreException {
		editorElement.setName("editor"); //$NON-NLS-1$
		editorElement.setAttribute("id", editorClassName); //$NON-NLS-1$
		editorElement.setAttribute("name", getStringOption("editorName")); //$NON-NLS-1$ //$NON-NLS-2$
		editorElement.setAttribute("icon", "icons/sample.gif"); //$NON-NLS-1$ //$NON-NLS-2$
		editorElement.setAttribute("class", editorClassName); //$NON-NLS-1$
		editorElement.setAttribute("contributorClass", contributorClassName); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.pde.internal.ui.wizards.templates.PDETemplateSection#
	 * formatPackageName(java.lang.String)
	 */
	public static String getFormattedPackageName(final String id) {
		final String packageName = ScaTemplateSection.getFormattedPackageName(id);
		if (packageName.length() != 0) {
			return packageName + ".controlPanels"; //$NON-NLS-1$
		}
		return "controlPanels"; //$NON-NLS-1$
	}
}
