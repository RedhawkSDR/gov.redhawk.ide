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

import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.TargetSdrRoot;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.prf.Simple;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.scd.ComponentType;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.apache.commons.lang.WordUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.pde.core.plugin.IPluginBase;
import org.eclipse.pde.core.plugin.IPluginElement;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.pde.core.plugin.IPluginModelFactory;
import org.eclipse.pde.core.plugin.IPluginReference;
import org.eclipse.pde.ui.templates.ITemplateSection;
import org.eclipse.pde.ui.templates.PluginReference;

/**
 * 
 */
public class ResourceControlPanelTemplateSection extends BaseControlPanelTemplate implements ITemplateSection {

	/**
	 * 
	 */
	private static final String SCA_RESOURCE = "scaResource";
	/**
	 * 
	 */
	private static final String CONTENT_TYPE_PRIORITY = "contentTypePriority";

	private SdrResourceOption sdrOption;

	/**
	 * Constructor for ComponentControlPanelTemplateSection.
	 */
	public ResourceControlPanelTemplateSection() {
		setPageCount(1);
		createOptions();
	}

	@Override
	public String getSectionId() {
		return "resourceControlPanel"; //$NON-NLS-1$
	}

	/*
	 * @see org.eclipse.pde.ui.templates.BaseOptionTemplateSection#getStringOption(java.lang.String)
	 */
	@Override
	public String getStringOption(String name) {
		if ("packageName".equals(name)) {
			return getBasePackage();
		} else if ("resourceClassName".equals(name)) {
			return getResourceClassName(true);
		} else if ("resourceClassNameNoGeneric".equals(name)) {
			return getResourceClassName(false);
		} else if ("contentTypeProfileId".equals(name)) {
			return getProfileId();
		} else if ("sectionClassName".equals(name)) {
			return getSectionClassName();
		} else if ("editorClassName".equals(name)) {
			return getEditorClassName();
		} else if ("filterClassName".equals(name)) {
			return getFilterClassName();
		} else if ("compositeName".equals(name)) {
			return getCompositeClassName();
		} else if ("bindingsCode".equals(name)) {
			return getPropertyBindingsCode();
		} else if ("controlGroupCode".equals(name)) {
			return getControlGroupCode();
		} else if ("propertyFields".equals(name)) {
			return getPropertyFieldsCode();
		} else {
			return super.getStringOption(name);
		}
	}

	/**
	 * @return
	 */
	private String getPropertyFieldsCode() {
		StringBuilder builder = new StringBuilder();
		EObject selection = getSelection();
		if (selection instanceof SoftPkg) {
			SoftPkg spd = (SoftPkg) selection;
			if (spd.getPropertyFile() != null && spd.getPropertyFile().getProperties() != null) {
				for (Simple s : spd.getPropertyFile().getProperties().getSimple()) {
					String field = getField(s, false);
					field = WordUtils.uncapitalize(field.replace(" ", ""));
					builder.append("		private Text " + field + ";\n");
				}
			}
		}
		return builder.toString();
	}

	/**
	 * @return
	 */
	private String getControlGroupCode() {
		StringBuilder builder = new StringBuilder();
		EObject selection = getSelection();
		if (selection instanceof SoftPkg) {
			SoftPkg spd = (SoftPkg) selection;
			String intend = "		";
			if (spd.getPropertyFile() != null && spd.getPropertyFile().getProperties() != null) {
				for (Simple s : spd.getPropertyFile().getProperties().getSimple()) {
					String field = getField(s);
					String name = (s.getName() == null) ? s.getId() : s.getName();

					builder.append(intend + "label = new Label(parent, SWT.None);\n");
					builder.append(intend + "label.setText(\"" + name + ":\");\n");
					builder.append(intend + field + " = new Text(parent, SWT.BORDER);\n");
					builder.append(intend + field + ".setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());\n");
				}
			}
		}
		return builder.toString();
	}

	private String getField(Simple s) {
		return getField(s, true);
	}

	/**
	 * @param s
	 * @return
	 */
	private String getField(Simple s, boolean prepend) {
		String field;
		if (s.getName() != null) {
			field = s.getName() + "_Text";
			field = field.replaceAll("[^a-zA-Z0-9]", "_");
			field = field.replaceAll("_*_", "_");
		} else {
			field = s.getId();
			field = field.replaceAll("[^a-zA-Z0-9]", "_");
			field = field.replaceAll("_*_", "_");
		} 
		field = WordUtils.uncapitalize(field.replace(" ", ""));
		if (prepend) {
			return "fields." + field;
		} else {
			return field;
		}
	}

	/**
	 * @return
	 */
	private String getPropertyBindingsCode() {
		StringBuilder builder = new StringBuilder();
		EObject selection = getSelection();
		String intend = "		";
		if (selection instanceof SoftPkg) {
			SoftPkg spd = (SoftPkg) selection;
			if (spd.getPropertyFile() != null && spd.getPropertyFile().getProperties() != null) {
				for (Simple s : spd.getPropertyFile().getProperties().getSimple()) {
					String field = getField(s);
					builder.append(intend + "simpleProp = (ScaSimpleProperty) input.getProperty(\"" + s.getId() + "\");\n");
					builder.append(intend + "context.bindValue(\n");
					builder.append(intend + "	WidgetProperties.text(SWT.Modify).observeDelayed(500, " + field + "),\n");
					builder.append(intend + "	SCAObservables.observeSimpleProperty(simpleProp));\n");
				}
			}
		}
		return builder.toString();
	}

	/**
	 * @return
	 */
	public String getCompositeClassName() {
		return getName() + "ControlPanel";
	}

	/**
	 * @return
	 */
	public String getName() {
		String name;
		EObject selection = getSelection();
		if (selection instanceof SoftPkg) {
			name = ((SoftPkg) selection).getName();
		} else if (selection instanceof SoftwareAssembly) {
			name = ((SoftwareAssembly) selection).getName();
		} else if (selection instanceof DeviceConfiguration) {
			name = ((DeviceConfiguration) selection).getName();
		} else {
			name = "ControlPanel";
		}
		return makeNameSafe(WordUtils.capitalize(name.trim()).replace(" ", "").replaceAll("[^a-zA-Z0-9]", "_").replaceAll("_*_", "_"));
	}

	/**
	 * @return
	 */
	public String getFilterClassName() {
		return getName() + "PropertyFilter";
	}

	/**
	 * @return
	 */
	public String getEditorClassName() {
		return getName() + "Editor";
	}

	/**
	 * @return
	 */
	public String getSectionClassName() {
		return getName() + "Section";
	}

	/**
	 * @return
	 */
	public String getResourceClassName(boolean hasGeneric) {
		EObject resource = getSelection();
		if (resource instanceof SoftwareAssembly) {
			return "gov.redhawk.model.sca.ScaWaveform";
		} else if (resource instanceof DeviceConfiguration) {
			return "gov.redhawk.model.sca.ScaDeviceManager";
		} else if (resource instanceof SoftPkg) {
			SoftPkg spd = (SoftPkg) resource;
			ComponentType type;
			if (spd.getDescriptor() == null) {
				type = ComponentType.RESOURCE;
			} else {
				type = SoftwareComponent.Util.getWellKnownComponentType(spd.getDescriptor().getComponent());
			}
			switch (type) {
			case DEVICE:
				if (hasGeneric) {
					return "gov.redhawk.model.sca.ScaDevice<?>";
				} else {
					return "gov.redhawk.model.sca.ScaDevice";
				}
			case SERVICE:
				return "gov.redhawk.model.sca.ScaService";
			default:
				return "gov.redhawk.model.sca.ScaComponent";
			}
		}
		return "UnknownClass";
	}

	@Override
	public IPluginReference[] getDependencies(final String schemaVersion) {
		if (schemaVersion != null) {
			final IPluginReference[] dep = new IPluginReference[12]; // SUPPRESS CHECKSTYLE MagicNumber
			dep[0] = new PluginReference("org.eclipse.ui", null, 0); //$NON-NLS-1$
			dep[1] = new PluginReference("org.eclipse.core.runtime", null, 0); //$NON-NLS-1$
			dep[2] = new PluginReference("gov.redhawk.sca.ui", null, 0); //$NON-NLS-1$
			dep[3] = new PluginReference("gov.redhawk.sca.model", null, 0); // SUPPRESS CHECKSTYLE MagicNumber //$NON-NLS-1$
			dep[4] = new PluginReference("org.eclipse.emf.edit.ui", null, 0); // SUPPRESS CHECKSTYLE MagicNumber //$NON-NLS-1$
			dep[5] = new PluginReference("org.eclipse.ui.views.properties.tabbed", null, 0); // SUPPRESS CHECKSTYLE MagicNumber //$NON-NLS-1$
			dep[6] = new PluginReference("gov.redhawk.sca.observables", null, 0); // SUPPRESS CHECKSTYLE MagicNumber //$NON-NLS-1$
			dep[7] = new PluginReference("org.eclipse.core.databinding", null, 0); // SUPPRESS CHECKSTYLE MagicNumber //$NON-NLS-1$
			dep[8] = new PluginReference("org.eclipse.core.databinding.observable", null, 0); // SUPPRESS CHECKSTYLE MagicNumber //$NON-NLS-1$
			dep[9] = new PluginReference("org.eclipse.core.databinding.property", null, 0); // SUPPRESS CHECKSTYLE MagicNumber //$NON-NLS-1$
			dep[10] = new PluginReference("org.eclipse.emf.databinding", null, 0); // SUPPRESS CHECKSTYLE MagicNumber //$NON-NLS-1$
			dep[11] = new PluginReference("org.eclipse.jface.databinding", null, 0); // SUPPRESS CHECKSTYLE MagicNumber //$NON-NLS-1$
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
		addOption(CONTENT_TYPE_PRIORITY, "Priority:",
			new String[][] { new String[] { "LOW", "LOW" }, new String[] { "NORMAL", "NORMAL" }, new String[] { "HIGH", "HIGH" } }, "NORMAL", 0).setRequired(
			true);

		addOption("propertyStubs", "Generate Property Stubs", true, 0);
		addOption("viewer", "Generate Sample Viewer", false, 0);

		final SdrRoot sdr = TargetSdrRoot.getSdrRoot();
		sdr.load(null);

		sdrOption = new SdrResourceOption(this, SCA_RESOURCE, "Resource", sdr);
		registerOption(sdrOption, null, 0);
	}

	@Override
	public boolean isDependentOnParentWizard() {
		return true;
	}

	@Override
	public void addPages(final Wizard wizard) {
		final WizardPage page = createPage(0, null);
		page.setTitle("Control Panel");
		page.setDescription("Choose the options that will be used to generate the control panel.");
		wizard.addPage(page);
		markPagesAdded();
	}

	@Override
	protected void updateModel(final IProgressMonitor monitor) throws CoreException {
		final IPluginBase plugin = this.model.getPluginBase();
		final IPluginModelFactory factory = this.model.getPluginFactory();

		final IPluginExtension extension = createExtension("org.eclipse.ui.editors", true); //$NON-NLS-1$
		final IPluginElement editorElement = factory.createElement(extension);

		createEditorElement(editorElement);
		extension.add(editorElement);
		if (!extension.isInTheModel()) {
			plugin.add(extension);
		}

		final String priority = getStringOption(CONTENT_TYPE_PRIORITY);

		final IPluginExtension contentTypeExtension = createExtension("gov.redhawk.sca.ui.scaContentTypes", true); //$NON-NLS-1$
		final IPluginElement contentTypeElement = factory.createElement(contentTypeExtension);
		createContentTypeElement(contentTypeElement, priority, factory);
		contentTypeExtension.add(contentTypeElement);

		final IPluginElement bindingElement = factory.createElement(contentTypeExtension);
		createBindingElement(bindingElement, priority);
		contentTypeExtension.add(bindingElement);

		if (!contentTypeExtension.isInTheModel()) {
			plugin.add(contentTypeExtension);
		}

		final IPluginExtension propertyTabExtension = createExtension("org.eclipse.ui.views.properties.tabbed.propertyTabs", true); //$NON-NLS-1$
		final IPluginElement propertyTabElement = factory.createElement(propertyTabExtension);
		createPropertyTabElement(propertyTabElement, factory);
		propertyTabExtension.add(propertyTabElement);

		if (!propertyTabExtension.isInTheModel()) {
			plugin.add(propertyTabExtension);
		}

		final IPluginExtension propertySectionExtension = createExtension("org.eclipse.ui.views.properties.tabbed.propertySections", true); //$NON-NLS-1$
		final IPluginElement propertySectionElement = factory.createElement(propertySectionExtension);
		createPropertySectionElement(propertySectionElement, factory);
		propertySectionExtension.add(propertySectionElement);

		if (!propertySectionExtension.isInTheModel()) {
			plugin.add(propertySectionExtension);
		}
	}

	/**
	 * @param bindingElement
	 * @param editorClassName
	 * @param contentTypeId
	 * @param priority
	 * @throws CoreException
	 */
	private void createBindingElement(final IPluginElement bindingElement, final String priority) throws CoreException {
		bindingElement.setName("contentTypeBinding"); //$NON-NLS-1$
		String basePackage = getBasePackage() + ".";
		bindingElement.setAttribute("editorId", basePackage + getEditorClassName()); //$NON-NLS-1$
		bindingElement.setAttribute("contentTypeId", getContentTypeID()); //$NON-NLS-1$
		bindingElement.setAttribute("priority", priority); //$NON-NLS-1$
	}

	/**
	 * @param contentTypeElement
	 * @param contentTypeId
	 * @param priority
	 * @throws CoreException
	 */
	private void createContentTypeElement(final IPluginElement contentTypeElement, final String priority, final IPluginModelFactory factory)
		throws CoreException {
		contentTypeElement.setName("contentType"); //$NON-NLS-1$
		contentTypeElement.setAttribute("id", getContentTypeID()); //$NON-NLS-1$
		contentTypeElement.setAttribute("name", getContentTypeName()); //$NON-NLS-1$ //$NON-NLS-2$
		contentTypeElement.setAttribute("priority", priority); //$NON-NLS-1$

		final IPluginElement describerElement = factory.createElement(contentTypeElement);
		createDescriberElement(describerElement, factory);
		contentTypeElement.add(describerElement);
	}

	/**
	 * @return
	 */
	public String getContentTypeID() {
		return getBasePackage() + "." + getName() + ".contentType";
	}

	/**
	 * @return
	 */
	public String getContentTypeName() {
		return getName();
	}

	/**
	 * @return
	 */
	public String getProfileId() {
		EObject selection = getSelection();
		String profileId = "ProfileID";
		if (selection instanceof SoftPkg) {
			profileId = ((SoftPkg) selection).getId();
		} else if (selection instanceof SoftwareAssembly) {
			profileId = ((SoftwareAssembly) selection).getId();
		} else if (selection instanceof DeviceConfiguration) {
			profileId = ((DeviceConfiguration) selection).getId();
		}
		return profileId;
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
		String profileId = getProfileId();
		if (profileId != null && profileId.trim().length() > 0) {
			final IPluginElement parameter = factory.createElement(describerElement);
			parameter.setName("parameter");
			parameter.setAttribute("name", "profileId");
			parameter.setAttribute("value", profileId);
			describerElement.add(parameter);
		}
	}

	/**
	 * @param editorClassName
	 * @param contributorClassName
	 * @param createElement
	 * @throws CoreException
	 */
	private void createEditorElement(final IPluginElement editorElement) throws CoreException {
		editorElement.setName("editor"); //$NON-NLS-1$
		String basePackage = getBasePackage() + ".";
		editorElement.setAttribute("id", basePackage + getEditorClassName()); //$NON-NLS-1$
		editorElement.setAttribute("name", getName()); //$NON-NLS-1$ //$NON-NLS-2$
		editorElement.setAttribute("icon", "icons/sample.gif"); //$NON-NLS-1$ //$NON-NLS-2$
		editorElement.setAttribute("class", basePackage + getEditorClassName()); //$NON-NLS-1$
	}

	/**
	 * @param propertyTabElement
	 * @param contentTypeId
	 * @param priority
	 * @throws CoreException
	 */
	private void createPropertyTabElement(final IPluginElement propertyTabElement, final IPluginModelFactory factory) throws CoreException {
		propertyTabElement.setName("propertyTabs"); //$NON-NLS-1$
		propertyTabElement.setAttribute("contributorId", "gov.redhawk.ui.sca_explorer"); //$NON-NLS-1$

		final IPluginElement tabElement = factory.createElement(propertyTabElement);
		createTabElement(tabElement, factory);
		propertyTabElement.add(tabElement);
	}

	/**
	 * @param describerElement
	 * @param factory
	 * @throws CoreException
	 */
	private void createTabElement(final IPluginElement describerElement, final IPluginModelFactory factory) throws CoreException {
		describerElement.setName("propertyTab");
		describerElement.setAttribute("category", "general");
		describerElement.setAttribute("id", getPropertyTabID());
		describerElement.setAttribute("label", getName() + " Control Panel");
	}

	/**
	 * @param propertySectionElement
	 * @param contentTypeId
	 * @param priority
	 * @throws CoreException
	 */
	private void createPropertySectionElement(final IPluginElement propertySectionElement, final IPluginModelFactory factory) throws CoreException {
		propertySectionElement.setName("propertySections"); //$NON-NLS-1$
		propertySectionElement.setAttribute("contributorId", "gov.redhawk.ui.sca_explorer"); //$NON-NLS-1$

		final IPluginElement describerElement = factory.createElement(propertySectionElement);
		createSectionElement(describerElement, factory);
		propertySectionElement.add(describerElement);
	}

	public EObject getSelection() {
		return (EObject) sdrOption.getSelection();
	}

	/**
	 * @param sectionElement
	 * @param factory
	 * @throws CoreException
	 */
	private void createSectionElement(final IPluginElement sectionElement, final IPluginModelFactory factory) throws CoreException {
		sectionElement.setName("propertySection");
		String basePackage = getBasePackage() + ".";
		sectionElement.setAttribute("class", basePackage + getSectionClassName());
		sectionElement.setAttribute("id", getPropertySectionID());
		sectionElement.setAttribute("filter", basePackage + getFilterClassName());
		sectionElement.setAttribute("tab", getPropertyTabID());

		final IPluginElement parameter = factory.createElement(sectionElement);
		parameter.setName("input");
		EObject resource = getSelection();
		if (resource instanceof SoftwareAssembly) {
			parameter.setAttribute("type", "gov.redhawk.model.sca.ScaWaveform");
		} else if (resource instanceof DeviceConfiguration) {
			parameter.setAttribute("type", "gov.redhawk.model.sca.ScaDeviceManager");
		} else if (resource instanceof SoftPkg) {
			SoftPkg spd = (SoftPkg) resource;
			ComponentType type;
			if (spd.getDescriptor() != null) {
				type = SoftwareComponent.Util.getWellKnownComponentType(spd.getDescriptor().getComponent());
			} else {
				type = ComponentType.RESOURCE;
			}
			switch (type) {
			case DEVICE:
				parameter.setAttribute("type", "gov.redhawk.model.sca.ScaDevice");
				break;
			case SERVICE:
				parameter.setAttribute("type", "gov.redhawk.model.sca.ScaService");
				break;
			default:
				parameter.setAttribute("type", "gov.redhawk.model.sca.ScaComponent");
				break;
			}

		}

		sectionElement.add(parameter);
	}

	/**
	 * @return
	 */
	public String getBasePackage() {
		return getFormattedPackageName(model.getPluginBase().getId());
	}

	/**
	 * @return
	 */
	public String getPropertyTabID() {
		return getBasePackage() + "." + getName() + ".tab";
	}

	/**
	 * @return
	 */
	public String getPropertySectionID() {
		return getBasePackage() + "." + getName() + ".section";
	}

	/**
	 * @param obj
	 */
	public void setResource(EObject obj) {
		sdrOption.setSelection(obj);
	}
}
