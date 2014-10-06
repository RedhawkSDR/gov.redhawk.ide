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
package gov.redhawk.ide.ui.tests.runtime;

import gov.redhawk.ide.sdr.ui.internal.handlers.LaunchDomainManagerWithOptions;
import gov.redhawk.ide.sdr.ui.util.DebugLevel;
import gov.redhawk.ide.sdr.ui.util.DomainManagerLaunchConfiguration;
import gov.redhawk.ide.swtbot.UIRuntimeTest;

import java.io.File;

import org.eclipse.ui.PlatformUI;
import org.junit.Assume;

/**
 * 
 */
public class AbstractDomainRuntimeTest extends UIRuntimeTest {
	
	protected void launchDomainManager(String name) {
		final DomainManagerLaunchConfiguration model = new DomainManagerLaunchConfiguration();
		model.setArguments("");
		model.setDebugLevel(DebugLevel.Error);
		model.setDomainName(name);
		model.setLaunchConfigName(name);
		model.setLocalDomainName(name);
		model.setSpdPath("/mgr/DomainManager.spd.xml");
		String sdrRoot = System.getenv("SDRROOT");
		File dmdSpd = new File(new File(sdrRoot), "/mgr/DomainManager.spd.xml");
		
		Assume.assumeTrue("${SDROOT}/mgr/DomainManager.spd.xml does not exist", dmdSpd.isFile());
		
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

			@Override
			public void run() {
				LaunchDomainManagerWithOptions.launchDomainManager(model, null);
			}

		});
	}
	
}
