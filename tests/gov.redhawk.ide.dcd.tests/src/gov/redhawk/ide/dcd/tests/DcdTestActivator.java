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
package gov.redhawk.ide.dcd.tests;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import mil.jpeojtrs.sca.dcd.DcdPackage;

public class DcdTestActivator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		// Ensure package is registered
		DcdPackage.eINSTANCE.eClass();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
	}

}
