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
package gov.redhawk.mfile.parser.tests;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * 
 */
public class MFileTestActivator extends Plugin {

	private static MFileTestActivator instance;

	@Override
	public void start(BundleContext context) throws Exception {
		instance = this;
		super.start(context);
	}

	public static MFileTestActivator getInstance() {
		return instance;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		instance = null;
	}

	public static InputStream openTestFile(String name) throws IOException {
		URL url = FileLocator.find(instance.getBundle(), new Path("testFiles/" + name), null);
		return url.openStream();
	}

}
