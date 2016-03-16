/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package gov.redhawk.ide.swtbot.condition;

import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;

import gov.redhawk.ide.sdr.LoadState;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;

public class WaitForTargetSdrRootLoad extends DefaultCondition {

	private LoadState lastObservedState;

	@Override
	public boolean test() throws Exception {
		lastObservedState = SdrUiPlugin.getDefault().getTargetSdrRoot().getState();
		return lastObservedState == LoadState.LOADED;
	}

	@Override
	public String getFailureMessage() {
		return String.format("Target SDR Root failed to load in model (state was %s)", lastObservedState);
	}

}
