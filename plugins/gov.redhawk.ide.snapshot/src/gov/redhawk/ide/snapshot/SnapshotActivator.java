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
package gov.redhawk.ide.snapshot;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import gov.redhawk.ide.snapshot.writer.IDataWriterRegistry;
import gov.redhawk.ide.snapshot.writer.internal.DataWriterRegistry;

/**
 * @since 1.0
 */
public class SnapshotActivator implements BundleActivator {

	public static final String PLUGIN_ID = "gov.redhawk.ide.snapshot";

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		SnapshotActivator.context = bundleContext;
	}

	public void stop(BundleContext bundleContext) throws Exception {
		SnapshotActivator.context = null;
	}

	public static IDataWriterRegistry getDataReceiverRegistry() {
		return DataWriterRegistry.INSTANCE;
	}

}
