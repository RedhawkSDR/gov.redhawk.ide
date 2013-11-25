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
package gov.redhawk.ide.codegen.internal;

import gov.redhawk.ide.codegen.ICodeGeneratorDescriptor;
import gov.redhawk.ide.codegen.IOperatingSystem;
import gov.redhawk.ide.codegen.IProcessor;
import gov.redhawk.ide.codegen.IScaComponentCodegen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @since 2.0
 * 
 */
public class CodeGeneratorDescriptor implements ICodeGeneratorDescriptor {
	/**
	 * The Constant ATTR_CODEGEN
	 */
	private static final String ATTR_LANGUAGE = "language";

	/**
	 * The Constant ATTR_ID
	 */
	private static final String ATTR_ID = "id";

	private static final String ATTR_NAME = "name";

	private static final String ATTR_ICON = "icon";

	private static final String ATTR_CLASS = "class";

	private static final String ATT_COMPILER = "compiler";

	private static final String ATT_COMPILER_VERSION = "compilerVersion";

	protected static final String ELM_DESCRIPTION = "description";

	private static final String ATT_LANGUAGE_VERSION = "languageVersion";

	private static final String ATT_HUMAN_LANGUAGE = "humanLanguage";

	private static final String ATT_RUNTIME = "runtime";

	private static final String ATT_RUNTIME_VERSION = "runtimeVersion";

	private static final String ATT_NOT_DEFAULTABLE = "notDefaultable";

	private static final String ATT_AUTO_GENERATE = "autoGenerate";

	private static final String ATT_ASSOCIATED_PERSPECTIVE_ID = "associatedPerspectiveId";

	private static final String ELM_OPERATING_SYSTEM = "operatingSystem";

	private static final String ELM_PROCESSOR_TYPE = "processor";
	
	private static final String ELM_COMPONENT_TYPE = "componenttype";
	
	private static final String ELM_COMPONENT_TYPE_ATTR_TYPE = "type";
	
	private static final String ATT_PRIORITY = "priority";

	private final String name;

	private final String id;

	private final String language;

	private final String iconPath;

	private final IConfigurationElement element;

	private final String contributingBundleId;

	private final String compiler;

	private final String compilerVersion;

	private final String description;

	private final String associatedPerspectiveId;

	private final String languageVersion;

	private final String humanLanguage;

	private final String runtime;

	private final String runtimeVersion;

	private final String bundleId;

	private final String notDefaultable;

	private final String autoGenerate;

	private final IOperatingSystem[] operatingSystems;

	private final IProcessor[] processors;

	private String priority;
	
	private final String[] componentTypes;
	private final boolean deprecated;

	/**
	 * @param name
	 * @param id
	 * @param language
	 * @param sourceBundle
	 * @param iconPath
	 * @param generatorPath
	 */
	public CodeGeneratorDescriptor(final IConfigurationElement element) {
		super();
		this.deprecated = Boolean.valueOf(element.getAttribute("deprecated"));
		this.notDefaultable = element.getAttribute(CodeGeneratorDescriptor.ATT_NOT_DEFAULTABLE);
		this.autoGenerate = element.getAttribute(CodeGeneratorDescriptor.ATT_AUTO_GENERATE);
		this.name = element.getAttribute(CodeGeneratorDescriptor.ATTR_NAME);
		this.id = element.getAttribute(CodeGeneratorDescriptor.ATTR_ID);
		this.language = element.getAttribute(CodeGeneratorDescriptor.ATTR_LANGUAGE);
		this.iconPath = element.getAttribute(CodeGeneratorDescriptor.ATTR_ICON);
		this.contributingBundleId = element.getContributor().getName();
		this.element = element;
		this.compiler = element.getAttribute(CodeGeneratorDescriptor.ATT_COMPILER);
		this.compilerVersion = element.getAttribute(CodeGeneratorDescriptor.ATT_COMPILER_VERSION);
		
		final IConfigurationElement[] descChildren = element.getChildren(CodeGeneratorDescriptor.ELM_DESCRIPTION);
		this.description = (descChildren.length == 1) ? descChildren[0].getValue() : null; // SUPPRESS CHECKSTYLE AvoidInline
		
		this.languageVersion = element.getAttribute(CodeGeneratorDescriptor.ATT_LANGUAGE_VERSION);
		this.humanLanguage = element.getAttribute(CodeGeneratorDescriptor.ATT_HUMAN_LANGUAGE);
		this.runtime = element.getAttribute(CodeGeneratorDescriptor.ATT_RUNTIME);
		this.runtimeVersion = element.getAttribute(CodeGeneratorDescriptor.ATT_RUNTIME_VERSION);
		this.associatedPerspectiveId = element.getAttribute(CodeGeneratorDescriptor.ATT_ASSOCIATED_PERSPECTIVE_ID);
		this.priority = element.getAttribute(CodeGeneratorDescriptor.ATT_PRIORITY);
		
		final List<IOperatingSystem> tempOs = new ArrayList<IOperatingSystem>();
		for (final IConfigurationElement elem : element.getChildren(CodeGeneratorDescriptor.ELM_OPERATING_SYSTEM)) {
			tempOs.add(new OperatingSystem(elem));
		}
		this.operatingSystems = tempOs.toArray(new IOperatingSystem[tempOs.size()]);

		final List<IProcessor> tempProc = new ArrayList<IProcessor>();
		for (final IConfigurationElement elem : element.getChildren(CodeGeneratorDescriptor.ELM_PROCESSOR_TYPE)) {
			tempProc.add(new ProcessorType(elem));
		}

		this.processors = tempProc.toArray(new IProcessor[tempProc.size()]);

		final List<String> tempCt = new ArrayList<String>();
		for (final IConfigurationElement elem : element.getChildren(CodeGeneratorDescriptor.ELM_COMPONENT_TYPE)) {
			tempCt.add(elem.getAttribute(ELM_COMPONENT_TYPE_ATTR_TYPE));
		}
		this.componentTypes = tempCt.toArray(new String[tempCt.size()]);
		Arrays.sort(this.componentTypes);
		this.bundleId = element.getContributor().getName();
	}

	public String contributingBundleId() {
		return this.contributingBundleId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IScaComponentCodegen getGenerator() throws CoreException {
		return (IScaComponentCodegen) this.element.createExecutableExtension(CodeGeneratorDescriptor.ATTR_CLASS);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getIconPath() {
		return this.iconPath;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getId() {
		return this.id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLanguage() {
		return this.language;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof CodeGeneratorDescriptor) {
			return this.id.equals(((CodeGeneratorDescriptor) obj).id);
		}
		return super.equals(obj);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getCompiler() {
		return this.compiler;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getCompilerVersion() {
		return this.compilerVersion;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {
		return this.description;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getHumanLanguage() {
		return this.humanLanguage;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLanguageVersion() {
		return this.languageVersion;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getRuntime() {
		return this.runtime;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getRuntimeVersion() {
		return this.runtimeVersion;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getContributingBundleId() {
		return this.bundleId;
	}

	@Override
	public int compareTo(final ICodeGeneratorDescriptor o) {
		if (o == null) {
			return -1;
		}
		if (!this.language.equals(o.getLanguage())) {
			return this.language.compareTo(o.getLanguage());
		}
		if (!this.languageVersion.equals(o.getLanguageVersion())) {
			return this.languageVersion.compareTo(o.getLanguageVersion());
		}

		return this.name.compareTo(o.getName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean notDefaultableGenerator() {
		return Boolean.parseBoolean(this.notDefaultable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IOperatingSystem[] getOperatingSystems() {
		return this.operatingSystems;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IProcessor[] getProcessors() {
		return this.processors;
	}

	/**
	 * @since 4.0
	 */
	@Override
	public boolean autoGenerate() {
		return Boolean.parseBoolean(this.autoGenerate);
	}

	/**
	 * @since 5.0
	 */
	@Override
	public String getAssociatedPerspectiveId() {
		return this.associatedPerspectiveId;
	}

	/**
	 * @since 9.0
	 */
	@Override
	public String getPriority() {
		return this.priority;
	}

	/**
	 * @since 9.0
	 */
	@Override
	public boolean supportsComponentType(String componentType) {
		final int i = Arrays.binarySearch(this.componentTypes, componentType);
		return (i >= 0);
	}
	
	@Override
	public boolean isDeprecated() {
		return this.deprecated;
	}

}
