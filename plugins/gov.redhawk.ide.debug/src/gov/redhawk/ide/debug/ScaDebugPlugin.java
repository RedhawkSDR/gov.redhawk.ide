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
package gov.redhawk.ide.debug;

import gov.redhawk.ExtendedCF.Sandbox;
import gov.redhawk.ide.debug.internal.ScaDebugInstance;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * 
 */
public class ScaDebugPlugin extends Plugin {
	public static final String ID = "gov.redhawk.ide.debug";

	private static ScaDebugPlugin instance;

	@Override
	public void start(final BundleContext context) throws Exception {
		ScaDebugPlugin.instance = this;
		super.start(context);
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		super.stop(context);
		ScaDebugInstance.INSTANCE.getLocalSca().dispose();
		ScaDebugPlugin.instance = null;
	}

	public LocalSca getLocalSca() {
		return ScaDebugInstance.INSTANCE.getLocalSca();
	}

	public Sandbox getSandbox() {
		return getLocalSca().getSandbox();
	}

	public static ScaDebugPlugin getInstance() {
		return ScaDebugPlugin.instance;
	}

	public static void logError(final String msg, final Throwable e) {
		ScaDebugPlugin.instance.getLog().log(new Status(IStatus.ERROR, ScaDebugPlugin.ID, msg, e));
	}
}
