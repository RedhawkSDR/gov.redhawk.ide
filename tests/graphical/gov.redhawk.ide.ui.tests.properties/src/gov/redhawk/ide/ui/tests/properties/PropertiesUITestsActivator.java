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
package gov.redhawk.ide.ui.tests.properties;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * 
 */
public class PropertiesUITestsActivator extends Plugin {

	private static PropertiesUITestsActivator instance;
	public static final String ID = "gov.redhawk.ide.ui.tests.properties";
	
	@Override
	public void start(BundleContext context) throws Exception {
		instance = this;
		super.start(context);
	}
	
	@Override
	public void stop(BundleContext context) throws Exception {
		instance = null;
		super.stop(context);
	}
	
	public static PropertiesUITestsActivator getInstance() {
		return instance;
	}
}
