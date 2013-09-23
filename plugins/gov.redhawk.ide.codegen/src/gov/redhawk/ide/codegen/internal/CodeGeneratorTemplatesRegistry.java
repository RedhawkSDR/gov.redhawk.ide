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

import gov.redhawk.ide.codegen.ICodeGeneratorTemplatesRegistry;
import gov.redhawk.ide.codegen.ITemplateDesc;
import gov.redhawk.ide.codegen.RedhawkCodegenActivator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.dynamichelpers.ExtensionTracker;
import org.eclipse.core.runtime.dynamichelpers.IExtensionChangeHandler;
import org.eclipse.core.runtime.dynamichelpers.IExtensionTracker;
import org.eclipse.core.runtime.dynamichelpers.IFilter;

/**
 * @since 7.0
 */
public class CodeGeneratorTemplatesRegistry implements ICodeGeneratorTemplatesRegistry, IExtensionChangeHandler {

	public static final String EP_ID = "codegenTemplate";

	/**
	 * The Constant ATTR_CODEGEN
	 */
	private static final String ATTR_TEMPLATE = "template";

	/**
	 * Create a tracker.
	 */
	private final ExtensionTracker tracker;

	/**
	 * The codegen Codegen objects.
	 */
	private final Map<String, ITemplateDesc> templateMap = new HashMap<String, ITemplateDesc>();;

	/**
	 * The codegen Codegen objects.
	 */
	private final Map<String, Set<String>> codegenToIdMap = new HashMap<String, Set<String>>();

	public CodeGeneratorTemplatesRegistry() {
		final IExtensionRegistry reg = Platform.getExtensionRegistry();
		final IExtensionPoint ep = reg.getExtensionPoint(RedhawkCodegenActivator.PLUGIN_ID, CodeGeneratorTemplatesRegistry.EP_ID);

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
			final ITemplateDesc desc = addConfig(element);
			if (desc != null) {
				tracker.registerObject(extension, desc, IExtensionTracker.REF_SOFT);
			}
		}
	}

	private ITemplateDesc addConfig(final IConfigurationElement element) {
		if (element.getName().equals(CodeGeneratorTemplatesRegistry.ATTR_TEMPLATE)) {
			final TemplateDescriptor desc = new TemplateDescriptor(element);
			if (!this.templateMap.containsKey(desc.getId())) {
				this.templateMap.put(desc.getId(), desc);
				final String lang = desc.getCodegenId();
				Set<String> ids = this.codegenToIdMap.get(lang);
				if (ids == null) {
					ids = new HashSet<String>();
					this.codegenToIdMap.put(lang, ids);
				}
				ids.add(desc.getId());
				return desc;
			} else {
				RedhawkCodegenActivator.logError("Duplicate Code Generator registered with an ID of: " + desc.getId(), null);
			}
		}
		return null;
	}

	@Override
	public void removeExtension(final IExtension extension, final Object[] objects) {
		for (final Object obj : objects) {
			if (obj instanceof ITemplateDesc) {
				final ITemplateDesc desc = (ITemplateDesc) obj;
				this.templateMap.remove(desc.getId());
				final Set<String> ids = this.codegenToIdMap.get(desc.getCodegenId());
				ids.remove(desc.getId());
				if (ids.size() == 0) {
					this.codegenToIdMap.remove(desc.getCodegenId());
				}
			}
		}
	}

	@Override
	public ITemplateDesc findTemplate(final String id) {
		return this.templateMap.get(id);
	}

	@Override
	public ITemplateDesc[] getTemplates() {
		return this.templateMap.values().toArray(new ITemplateDesc[this.templateMap.size()]);
	}

	@Override
	public ITemplateDesc[] findTemplatesByCodegen(final String codeGenId) {
		return findTemplatesByCodegen(codeGenId, null);
	}

	/**
	 * @since 9.0
	 */
	@Override
	public ITemplateDesc[] findTemplatesByCodegen(String codeGenId, String componentType) {
		final Set<String> ids = this.codegenToIdMap.get(codeGenId);
		final ArrayList<ITemplateDesc> codegens = new ArrayList<ITemplateDesc>();

		if (ids != null) {
			for (final String id : ids) {
				final ITemplateDesc codegen = this.templateMap.get(id);
				if ((codegen != null) && codegen.isSelectable()) {
					if ((componentType == null) || (codegen.supportsComponentType(componentType))) {
						codegens.add(codegen);
					}
				}
			}
			Collections.sort(codegens);
		}

		return codegens.toArray(new ITemplateDesc[codegens.size()]);
	}

}
