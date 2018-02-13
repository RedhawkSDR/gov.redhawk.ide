/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.snapshot.writer.internal;

import java.util.HashMap;
import java.util.Map;

import gov.redhawk.ide.snapshot.SnapshotActivator;
import gov.redhawk.ide.snapshot.writer.IDataWriterDesc;
import gov.redhawk.ide.snapshot.writer.IDataWriterRegistry;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.dynamichelpers.ExtensionTracker;
import org.eclipse.core.runtime.dynamichelpers.IExtensionChangeHandler;
import org.eclipse.core.runtime.dynamichelpers.IExtensionTracker;
import org.eclipse.core.runtime.dynamichelpers.IFilter;

public enum DataWriterRegistry implements IExtensionChangeHandler, IDataWriterRegistry {

	INSTANCE;

	private static final String EP_ID = "writers";
	private ExtensionTracker tracker;
	private Map<String, IDataWriterDesc> registry = new HashMap<String, IDataWriterDesc>();

	private DataWriterRegistry() {
		populateRegistry();
	}

	private void populateRegistry() {
		final IExtensionRegistry reg = Platform.getExtensionRegistry();
		final IExtensionPoint ep = reg.getExtensionPoint(SnapshotActivator.PLUGIN_ID, EP_ID);

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
	public IDataWriterDesc getRecieverDesc(String id) {
		return registry.get(id);
	}

	@Override
	public IDataWriterDesc[] getRecieverDescs() {
		return registry.values().toArray(new IDataWriterDesc[registry.size()]);
	}

	@Override
	public void addExtension(IExtensionTracker tracker, IExtension extension) {
		for (IConfigurationElement element : extension.getConfigurationElements()) {
			if ("writer".equals(element.getName())) {
				DataWriterDesc newDesc = new DataWriterDesc(element);
				registry.put(newDesc.getID(), newDesc);
			}
		}
	}

	@Override
	public void removeExtension(IExtension extension, Object[] objects) {
		for (IConfigurationElement element : extension.getConfigurationElements()) {
			if ("writer".equals(element.getName())) {
				registry.remove(element.getAttribute("id"));
			}
		}
	}

}
