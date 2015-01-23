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
package gov.redhawk.ide.graphiti.sad.ui.preferences;

import gov.redhawk.ide.graphiti.sad.ui.SADUIGraphitiPlugin;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public PreferenceInitializer() {
	}

	@Override
	public void initializeDefaultPreferences() {
		SADUIGraphitiPlugin.getDefault().getPreferenceStore().setDefault(GraphitiSadPreferenceConstants.PREF_SAD_PORT_STATISTICS_QUEUE_LEVEL, 60);
		SADUIGraphitiPlugin.getDefault().getPreferenceStore().setDefault(GraphitiSadPreferenceConstants.PREF_SAD_PORT_STATISTICS_NO_DATA_PUSHED_SECONDS, 1);
		SADUIGraphitiPlugin.getDefault().getPreferenceStore().setDefault(GraphitiSadPreferenceConstants.PREF_SAD_PORT_STATISTICS_QUEUE_FLUSH_DISPLAY, 30);
	}

}
