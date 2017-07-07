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
package gov.redhawk.ide.codegen.ui.internal;

import gov.redhawk.ide.codegen.RedhawkCodegenActivator;
import gov.redhawk.ide.codegen.ui.ICodeGeneratorLanguagePageRegistry;
import gov.redhawk.ide.codegen.ui.ICodegenComposite;
import gov.redhawk.ide.codegen.ui.ICodegenDisplayFactory;
import gov.redhawk.ide.codegen.ui.ICodegenDisplayFactory2;
import gov.redhawk.ide.codegen.ui.ICodegenWizardPage;
import gov.redhawk.ide.codegen.ui.RedhawkCodegenUiActivator;
import mil.jpeojtrs.sca.spd.Implementation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.dynamichelpers.ExtensionTracker;
import org.eclipse.core.runtime.dynamichelpers.IExtensionChangeHandler;
import org.eclipse.core.runtime.dynamichelpers.IExtensionTracker;
import org.eclipse.core.runtime.dynamichelpers.IFilter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class CodeGeneratorTemplatePageRegistry implements ICodeGeneratorLanguagePageRegistry, IExtensionChangeHandler {

	public static final String EP_ID = "codegenTemplatePages";

	/**
	 * The Constant ATTR_CODEGEN
	 */
	private static final String ELM_CODEGEN = "codegenTemplatePage";

	/**
	 * The Constant ATTR_CODEGEN
	 */
	private static final String ELM_CODEGEN_ID = "codegenTemplateId";

	/**
	 * The Constant ATTR_CODEGEN
	 */
	private static final String ATTR_TARGET_CODEGEN_ID = "targetCodegenTemplateId";

	private static final String ATTR_WIZARD_CLASS = "wizardClass";

	/**
	 * Create a tracker.
	 */
	private final ExtensionTracker tracker;

	/**
	 * The map of codegen id's to Codegen Wizard page objects.
	 */
	private final Map<String, List<ICodegenDisplayFactory>> codegenTemplateToWizardMap = new HashMap<String, List<ICodegenDisplayFactory>>();

	public CodeGeneratorTemplatePageRegistry() {
		final IExtensionRegistry reg = Platform.getExtensionRegistry();
		final IExtensionPoint ep = reg.getExtensionPoint(RedhawkCodegenUiActivator.PLUGIN_ID, CodeGeneratorTemplatePageRegistry.EP_ID);

		this.tracker = new ExtensionTracker(reg);

		if (ep != null) {
			final IFilter filter = ExtensionTracker.createExtensionPointFilter(ep);
			this.tracker.registerHandler(this, filter);
			final IExtension[] extensions = ep.getExtensions();
			for (final IExtension extension : extensions) {
				addExtension(this.tracker, extension);
			}
		}
	}

	@Override
	public void addExtension(final IExtensionTracker tracker, final IExtension extension) {
		final IConfigurationElement[] configs = extension.getConfigurationElements();
		for (final IConfigurationElement element : configs) {
			final ICodegenDisplayFactory factory = addConfig(element);
			if (factory != null) {
				tracker.registerObject(extension, factory, IExtensionTracker.REF_SOFT);
			}
		}
	}

	private ICodegenDisplayFactory addConfig(final IConfigurationElement element) {
		ICodegenDisplayFactory factory = null;

		if (element.getName().equals(CodeGeneratorTemplatePageRegistry.ELM_CODEGEN)) {
			// Check if there's a wizard pages defined, if so, add it to the 
			// list of available wizard pages
			final String wizardClassId = element.getAttribute(CodeGeneratorTemplatePageRegistry.ATTR_WIZARD_CLASS);
			if (wizardClassId != null) {
				try {
					// Use the element to instantiate the specified class
					factory = (ICodegenDisplayFactory) element.createExecutableExtension(CodeGeneratorTemplatePageRegistry.ATTR_WIZARD_CLASS);

					// Loop through all of the codegenId elements and add this factory to the codegen listed
					for (final IConfigurationElement elem : element.getChildren(CodeGeneratorTemplatePageRegistry.ELM_CODEGEN_ID)) {
						final String codegenId = elem.getAttribute(CodeGeneratorTemplatePageRegistry.ATTR_TARGET_CODEGEN_ID);

						// Get the list of factories for the specified code generator, create a new list if needed
						List<ICodegenDisplayFactory> ids = this.codegenTemplateToWizardMap.get(codegenId);
						if (ids == null) {
							ids = new ArrayList<ICodegenDisplayFactory>();
							this.codegenTemplateToWizardMap.put(codegenId, ids);
						}
						// Only add the factory if it's not already in the list
						if (!ids.contains(factory)) {
							ids.add(factory);
						}
					}
				} catch (final CoreException e) {
					RedhawkCodegenActivator.logError("Could not instantiate Code Generator Display Factory with an ID of: " + wizardClassId, null);
				}
			} else {
				RedhawkCodegenActivator.logError("Unable to find wizard class ID for element: " + element.getName() + " (value: " + element.getValue() + ")", null);
			}
		}
		return factory;
	}

	@Override
	public void removeExtension(final IExtension extension, final Object[] objects) {
		for (final Object obj : objects) {
			if (obj instanceof ICodegenDisplayFactory) {
				// TODO make this work
				// NOTE! THIS DOES NOT WORK!
				final ICodegenDisplayFactory guiId = (ICodegenDisplayFactory) obj;
				List<ICodegenDisplayFactory> ids = this.codegenTemplateToWizardMap.get(guiId);
				ids.remove(guiId);
				if (ids.size() == 0) {
					this.codegenTemplateToWizardMap.remove(guiId);
				}
			}
		}
	}

	/**
	 * There is no default page for the codegenTemplate, returns null.
	 */
	@Override
	public ICodegenWizardPage[] getDefaultPages() {
		return null;
	}

	@Override
	public ICodegenWizardPage[] findPageByGeneratorId(final String codegen) {
		final ArrayList<ICodegenWizardPage> codegens = new ArrayList<ICodegenWizardPage>();
		final List<ICodegenDisplayFactory> factories = this.codegenTemplateToWizardMap.get(codegen);

		if (factories != null) {
			for (final ICodegenDisplayFactory factory : factories) {
				if (factory instanceof ICodegenDisplayFactory2) {
					final ICodegenWizardPage[] wizPages = ((ICodegenDisplayFactory2) factory).createPages();
					codegens.addAll(Arrays.asList(wizPages));
				} else {
					codegens.add(factory.createPage());
				}
			}
		}

		return codegens.toArray(new ICodegenWizardPage[codegens.size()]);
	}

	/**
	 * There is no composite for codegenTemplate returns null.
	 */
	@Override
	public ICodegenComposite getDefaultComposite(final Composite parent, final int style, final FormToolkit toolkit) {
		return null;
	}

	/**
	 * No composite for codegenTemplate, returns null
	 */
	@Override
	public ICodegenComposite[] findCompositeByGeneratorId(final String codegen, final Composite parent, final int style, final FormToolkit toolkit) {
		return null;
	}

	/**
	 * No composite for codegenTemplate, returns null
	 */
	@Override
	public ICodegenComposite[] findCompositeByGeneratorId(Implementation impl, String codegenId, Composite parent, int style, FormToolkit toolkit) {
		return null;
	}

	/**
	 * This method should return the same factories that was used to create the Wizard pages & Composites from
	 * findCompositeByGeneratorId and findPageByGeneratorId since the factories are kept within a map generated
	 * from the start.
	 */
	@Override
	public List<ICodegenDisplayFactory> findCodegenDisplayFactoriesByGeneratorId(final String codegen) {
		final List<ICodegenDisplayFactory> factories = this.codegenTemplateToWizardMap.get(codegen);
		return factories;
	}

	/**
	 * There is no default page for codegenTemplates.  Returns null
	 */
	@Override
	public ICodegenWizardPage getDefaultPage() {
		return null;
	}
}
