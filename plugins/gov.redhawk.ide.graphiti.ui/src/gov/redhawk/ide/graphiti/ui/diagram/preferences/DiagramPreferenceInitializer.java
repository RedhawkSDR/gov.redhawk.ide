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
package gov.redhawk.ide.graphiti.ui.diagram.preferences;

import gov.redhawk.ide.graphiti.ui.GraphitiUIPlugin;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class DiagramPreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = GraphitiUIPlugin.getDefault().getPreferenceStore();
		
		store.setDefault(DiagramPreferenceConstants.HIDE_DETAILS, false);
		store.setDefault(DiagramPreferenceConstants.HIDE_UNUSED_PORTS, false);
		
		store.setDefault(DiagramPreferenceConstants.PREF_PORT_STATISTICS_QUEUE_LEVEL, 60);
		store.setDefault(DiagramPreferenceConstants.PREF_PORT_STATISTICS_NO_DATA_PUSHED_SECONDS, 1);
		store.setDefault(DiagramPreferenceConstants.PREF_PORT_STATISTICS_QUEUE_FLUSH_DISPLAY, 30);
	}

}
