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

import gov.redhawk.ide.codegen.ICodeGeneratorPortTemplatesRegistry;
import gov.redhawk.ide.codegen.IPortTemplateDesc;
import gov.redhawk.ide.codegen.RedhawkCodegenActivator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

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
public class CodeGeneratorPortTemplatesRegistry implements IExtensionChangeHandler, ICodeGeneratorPortTemplatesRegistry {

	public static final String EP_ID = "portGenerator";

	/**
	 * The Constant ATTR_CODEGEN
	 */
	private static final String ATTR_TEMPLATE = "portHandler";

	/**
	 * Create a tracker.
	 */
	private final ExtensionTracker tracker;

	/**
	 * The codegen Codegen objects.
	 */
	private final Map<String, IPortTemplateDesc> templateMap = new HashMap<String, IPortTemplateDesc>();;

	/**
	 * The map of repId to code generator ids.
	 */
	private final Map<String, Set<String>> repToIdMap = new HashMap<String, Set<String>>();

	public CodeGeneratorPortTemplatesRegistry() {
		final IExtensionRegistry reg = Platform.getExtensionRegistry();
		final IExtensionPoint ep = reg.getExtensionPoint(RedhawkCodegenActivator.PLUGIN_ID, CodeGeneratorPortTemplatesRegistry.EP_ID);

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
			final IPortTemplateDesc desc = addConfig(element);
			if (desc != null) {
				tracker.registerObject(extension, desc, IExtensionTracker.REF_SOFT);
			}
		}
	}

	private IPortTemplateDesc addConfig(final IConfigurationElement element) {
		if (element.getName().equals(CodeGeneratorPortTemplatesRegistry.ATTR_TEMPLATE)) {
			final PortTemplateDescriptor desc = new PortTemplateDescriptor(element);
			if (!this.templateMap.containsKey(desc.getId())) {
				this.templateMap.put(desc.getId(), desc);
				final String[] ifaces = desc.getInterfaces();
				for (final String i : ifaces) {
					if (i == null) {
						continue;
					}
					Set<String> ids = this.repToIdMap.get(i);
					if (ids == null) {
						ids = new HashSet<String>();
						this.repToIdMap.put(i, ids);
					}
					ids.add(desc.getId());
				}
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
			if (obj instanceof IPortTemplateDesc) {
				final IPortTemplateDesc desc = (IPortTemplateDesc) obj;
				this.templateMap.remove(desc.getId());
				final String[] ifaces = desc.getInterfaces();
				for (final String i : ifaces) {
					final Set<String> ids = this.repToIdMap.get(i);
					ids.remove(desc.getId());
					if (ids.size() == 0) {
						this.repToIdMap.remove(i);
					}
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IPortTemplateDesc findTemplate(final String id) {
		return this.templateMap.get(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IPortTemplateDesc[] getTemplates() {
		return this.templateMap.values().toArray(new IPortTemplateDesc[this.templateMap.size()]);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IPortTemplateDesc[] findTemplatesByRepId(final String repId, final String language) {
		final ArrayList<IPortTemplateDesc> codegens = new ArrayList<IPortTemplateDesc>();

		// Search all repId's
		for (final Entry<String, Set<String>> ent : this.repToIdMap.entrySet()) {
			final String key = ent.getKey();

			// We allow for patterns, check if the passed in repId matches
			if (Pattern.matches(key, repId)) {

				// Loop through all the generators registered for this repId
				for (final String id : ent.getValue()) {
					final IPortTemplateDesc codegen = this.templateMap.get(id);
					if (codegen != null) {

						// Check for a match
						for (final String lang : codegen.getLanguages()) {
							if (lang.equals(language)) {
								codegens.add(codegen);
								break;
							}
						}
					}
				}
			}
		}

		Collections.sort(codegens);
		codegens.size();
		return codegens.toArray(new IPortTemplateDesc[codegens.size()]);
	}

}
