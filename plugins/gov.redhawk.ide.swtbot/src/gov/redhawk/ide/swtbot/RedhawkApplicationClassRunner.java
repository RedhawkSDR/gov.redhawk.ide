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
package gov.redhawk.ide.swtbot;

import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;

/**
 * 
 */
public class RedhawkApplicationClassRunner extends BlockJUnit4ClassRunner {

	public RedhawkApplicationClassRunner(Class< ? > klass) throws Exception {
		super(klass);
	}
	
	public void run(RunNotifier notifier) {
		RunListener failureSpy = new RedhawkApplicationRunListener();
		notifier.removeListener(failureSpy); // remove existing listeners that could be added by suite or class runners
		notifier.addListener(failureSpy);
		try {
			super.run(notifier);
		} finally {
			notifier.removeListener(failureSpy);
		}
	}

}
