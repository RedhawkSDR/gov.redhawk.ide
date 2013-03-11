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
import gov.redhawk.ide.codegen.ICodeGeneratorsRegistry;
import gov.redhawk.ide.codegen.RedhawkCodegenActivator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
 * @since 2.0
 */
public class CodeGeneratorsRegistry implements ICodeGeneratorsRegistry, IExtensionChangeHandler {

	public static final String EP_ID = "codegens";

	/**
	 * The Constant ATTR_CODEGEN
	 */
	private static final String ATTR_CODEGEN = "codegen";

	private static final StringComparator STRING_COMPARATOR = new CodeGeneratorsRegistry.StringComparator();

	/**
	 * Create a tracker.
	 */
	private final ExtensionTracker tracker;

	/**
	 * The codegen Codegen objects.
	 */
	private final Map<String, ICodeGeneratorDescriptor> codegenMap = new HashMap<String, ICodeGeneratorDescriptor>();;

	/**
	 * The codegen Codegen objects.
	 */
	private final Map<String, Set<String>> langToIdMap = new HashMap<String, Set<String>>();

	public CodeGeneratorsRegistry() {
		final IExtensionRegistry reg = Platform.getExtensionRegistry();
		final IExtensionPoint ep = reg.getExtensionPoint(RedhawkCodegenActivator.PLUGIN_ID, CodeGeneratorsRegistry.EP_ID);

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

	public void addExtension(final IExtensionTracker tracker, final IExtension extension) {
		final IConfigurationElement[] configs = extension.getConfigurationElements();
		for (final IConfigurationElement element : configs) {
			final ICodeGeneratorDescriptor desc = addConfig(element);
			if (desc != null) {
				tracker.registerObject(extension, desc, IExtensionTracker.REF_SOFT);
			}
		}
	}

	private ICodeGeneratorDescriptor addConfig(final IConfigurationElement element) {
		if (element.getName().equals(CodeGeneratorsRegistry.ATTR_CODEGEN)) {
			final CodeGeneratorDescriptor desc = new CodeGeneratorDescriptor(element);
			if (!this.codegenMap.containsKey(desc.getId())) {
				this.codegenMap.put(desc.getId(), desc);
				final String lang = desc.getLanguage();
				Set<String> ids = this.langToIdMap.get(lang);
				if (ids == null) {
					ids = new HashSet<String>();
					this.langToIdMap.put(lang, ids);
				}
				ids.add(desc.getId());
				return desc;
			} else {
				RedhawkCodegenActivator.logError("Duplicate Code Generator registered with an ID of: " + desc.getId(), null);
			}
		}
		return null;
	}

	public void removeExtension(final IExtension extension, final Object[] objects) {
		for (final Object obj : objects) {
			if (obj instanceof ICodeGeneratorDescriptor) {
				final ICodeGeneratorDescriptor desc = (ICodeGeneratorDescriptor) obj;
				this.codegenMap.remove(desc.getId());
				final Set<String> ids = this.langToIdMap.get(desc.getLanguage());
				ids.remove(desc.getId());
				if (ids.size() == 0) {
					this.langToIdMap.remove(desc.getLanguage());
				}
			}
		}
	}

	public ICodeGeneratorDescriptor findCodegen(final String id) {
		return this.codegenMap.get(id);
	}

	public ICodeGeneratorDescriptor[] getCodegens() {
		return this.codegenMap.values().toArray(new ICodeGeneratorDescriptor[this.codegenMap.size()]);
	}

	public String[] getLanguages() {
		Set<String> languages = new HashSet<String>(this.langToIdMap.keySet());
		languages.remove("*");
		final String[] keys = languages.toArray(new String[languages.size()]);
		Arrays.sort(keys, CodeGeneratorsRegistry.STRING_COMPARATOR);
		return keys;
	}
	
	public ICodeGeneratorDescriptor[] findCodegenByLanguage(final String language) {
		return findCodegenByLanguage(language, null);
	}
	
	/**
	 * @since 9.0
	 */
	public ICodeGeneratorDescriptor[] findCodegenByLanguage(final String language, final String componentType) {
		final Set<String> ids = this.langToIdMap.get(language);
		if (ids == null) {
			return new ICodeGeneratorDescriptor[0];
		}
		
		final Set<String> wildcard = this.langToIdMap.get("*");
		if (wildcard != null) {
			ids.addAll(wildcard);
		}
		
		final ArrayList<ICodeGeneratorDescriptor> codegens = new ArrayList<ICodeGeneratorDescriptor>();
		
		for (final String id : ids) {
			final ICodeGeneratorDescriptor codegen = this.codegenMap.get(id);
			if (codegen != null) {
				if ((componentType == null) || (codegen.supportsComponentType(componentType))) {
					codegens.add(codegen);
				}
			}
		}
		Collections.sort(codegens, new PriorityComparator());

		return codegens.toArray(new ICodeGeneratorDescriptor[codegens.size()]);
	}

	private static class StringComparator implements Comparator<String> {
		public int compare(final String o1, final String o2) {
			if (o1 == null) {
				return 1;
			}

			if (o2 == null) {
				return -1;
			}
			return o1.compareTo(o2);
		}
	}
	
	private static class PriorityComparator implements Comparator<ICodeGeneratorDescriptor> {
		private static HashMap<String, Integer> priorityMap = new HashMap<String, Integer>();
		{
			priorityMap.put("high", 0);
			priorityMap.put("normal", 1);
			priorityMap.put("low", 2);
		}
		
		public int compare(final ICodeGeneratorDescriptor o1, final ICodeGeneratorDescriptor o2) {
			if (o1 == null) {
				return 1;
			}

			if (o2 == null) {
				return -1;
			}
			
			Integer p1 = priorityMap.get(o1.getPriority());
			Integer p2 = priorityMap.get(o2.getPriority());
			
			if (p1 == null) {
				return 1;
			}

			if (p2 == null) {
				return -1;
			}
			
			return p1.compareTo(p2);
		}
	}
}
