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
import gov.redhawk.ide.codegen.ui.DefaultGeneratorDisplayFactory;
import gov.redhawk.ide.codegen.ui.ICodeGeneratorPageRegistry;
import gov.redhawk.ide.codegen.ui.ICodegenComposite;
import gov.redhawk.ide.codegen.ui.ICodegenDisplayFactory;
import gov.redhawk.ide.codegen.ui.ICodegenWizardPage;
import gov.redhawk.ide.codegen.ui.RedhawkCodegenUiActivator;

import java.util.ArrayList;
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

public class CodeGeneratorPageRegistry implements ICodeGeneratorPageRegistry, IExtensionChangeHandler {

	public static final String EP_ID = "codegenPages";

	/**
	 * The Constant ATTR_CODEGEN
	 */
	private static final String ELM_CODEGEN = "codegenPage";

	/**
	 * The Constant ATTR_CODEGEN
	 */
	private static final String ELM_CODEGEN_ID = "codegenId";

	/**
	 * The Constant ATTR_CODEGEN
	 */
	private static final String ATTR_TARGET_CODEGEN_ID = "targetCodegenId";

	private static final String ATTR_WIZARD_CLASS = "wizardClass";
	private static final String ATTR_COMP_CLASS = "compositeClass";

	/**
	 * Create a tracker.
	 */
	private final ExtensionTracker tracker;

	/**
	 * The map of codegen id's to Codegen Wizard page objects.
	 */
	private final Map<String, List<ICodegenDisplayFactory>> codegenToWizardMap = new HashMap<String, List<ICodegenDisplayFactory>>();

	/**
	 * The map of codegen id's to Codegen Composite page objects.
	 */
	private final Map<String, List<ICodegenDisplayFactory>> codegenToCompositeMap = new HashMap<String, List<ICodegenDisplayFactory>>();

	private final DefaultGeneratorDisplayFactory defaultFactory = new DefaultGeneratorDisplayFactory();

	public CodeGeneratorPageRegistry() {
		final IExtensionRegistry reg = Platform.getExtensionRegistry();
		final IExtensionPoint ep = reg.getExtensionPoint(RedhawkCodegenUiActivator.PLUGIN_ID, CodeGeneratorPageRegistry.EP_ID);

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

		if (element.getName().equals(CodeGeneratorPageRegistry.ELM_CODEGEN)) {
			// Check if there's a wizard pages defined, if so, add it to the 
			// list of available wizard pages
			final String wizardClassId = element.getAttribute(CodeGeneratorPageRegistry.ATTR_WIZARD_CLASS);
			if (wizardClassId != null) {
				try {
					// Use the element to instantiate the specified class
					factory = (ICodegenDisplayFactory) element.createExecutableExtension(CodeGeneratorPageRegistry.ATTR_WIZARD_CLASS);

					// Loop through all of the codegenId elements and add this factory to the codegen listed
					for (final IConfigurationElement elem : element.getChildren(CodeGeneratorPageRegistry.ELM_CODEGEN_ID)) {
						final String codegenId = elem.getAttribute(CodeGeneratorPageRegistry.ATTR_TARGET_CODEGEN_ID);

						// Get the list of factories for the specified code generator, create a new list if needed
						List<ICodegenDisplayFactory> ids = this.codegenToWizardMap.get(codegenId);
						if (ids == null) {
							ids = new ArrayList<ICodegenDisplayFactory>();
							this.codegenToWizardMap.put(codegenId, ids);
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

			// Check if there's a composite defined, if so, add it to the 
			// list of available composites
			final String compositeClassId = element.getAttribute(CodeGeneratorPageRegistry.ATTR_COMP_CLASS);
			if (compositeClassId != null) {
				try {
					// Use the element to instantiate the specified class
					factory = (ICodegenDisplayFactory) element.createExecutableExtension(CodeGeneratorPageRegistry.ATTR_COMP_CLASS);

					// Loop through all of the codegenId elements and add this factory to the codegen listed
					for (final IConfigurationElement elem : element.getChildren(CodeGeneratorPageRegistry.ELM_CODEGEN_ID)) {
						final String codegenId = elem.getAttribute(CodeGeneratorPageRegistry.ATTR_TARGET_CODEGEN_ID);

						// Get the list of factories for the specified code generator, create a new list if needed
						List<ICodegenDisplayFactory> ids = this.codegenToCompositeMap.get(codegenId);
						if (ids == null) {
							ids = new ArrayList<ICodegenDisplayFactory>();
							this.codegenToCompositeMap.put(codegenId, ids);
						}
						// Only add the factory if it's not already in the list
						if (!ids.contains(factory)) {
							ids.add(factory);
						}
					}
				} catch (final CoreException e) {
					RedhawkCodegenActivator.logError("Could not instantiate Code Generator Display Factory with an ID of: " + compositeClassId, null);
				}
			} else {
				RedhawkCodegenActivator.logError("Unable to find composite class ID for element: " + element.getName() + " (value: " + element.getValue() + ")", null);
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
				List<ICodegenDisplayFactory> ids = this.codegenToWizardMap.get(guiId);
				ids.remove(guiId);
				if (ids.size() == 0) {
					this.codegenToWizardMap.remove(guiId);
				}

				ids = this.codegenToCompositeMap.get(guiId);
				ids.remove(guiId);
				if (ids.size() == 0) {
					this.codegenToCompositeMap.remove(guiId);
				}
			}
		}
	}

	@Override
	public ICodegenWizardPage getDefaultPage() {
		return this.defaultFactory.createPage();
	}

	@Override
	public ICodegenWizardPage[] findPageByGeneratorId(final String codegen) {
		final ArrayList<ICodegenWizardPage> codegens = new ArrayList<ICodegenWizardPage>();
		final List<ICodegenDisplayFactory> factories = this.codegenToWizardMap.get(codegen);

		if (factories != null) {
			for (final ICodegenDisplayFactory factory : factories) {
				final ICodegenWizardPage wizPage = factory.createPage();
				if (codegen != null) {
					codegens.add(wizPage);
				}
			}
		}

		if (codegens.size() == 0) {
			codegens.add(this.defaultFactory.createPage());
		}

		return codegens.toArray(new ICodegenWizardPage[codegens.size()]);
	}

	@Override
	public ICodegenComposite getDefaultComposite(final Composite parent, final int style, final FormToolkit toolkit) {
		return this.defaultFactory.createComposite(parent, style, toolkit);
	}

	@Override
	public ICodegenComposite[] findCompositeByGeneratorId(final String codegen, final Composite parent, final int style, final FormToolkit toolkit) {
		final ArrayList<ICodegenComposite> codegens = new ArrayList<ICodegenComposite>();
		final List<ICodegenDisplayFactory> factories = this.codegenToCompositeMap.get(codegen);

		if (factories != null) {
			for (final ICodegenDisplayFactory factory : factories) {
				final ICodegenComposite composite = factory.createComposite(parent, style, toolkit);
				if (codegen != null) {
					codegens.add(composite);
				}
			}
		}

		if (codegens.size() == 0) {
			codegens.add(this.defaultFactory.createComposite(parent, style, toolkit));
		}

		return codegens.toArray(new ICodegenComposite[codegens.size()]);
	}
}
