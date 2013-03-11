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
package gov.redhawk.ide.debug.internal.ui;

import gov.redhawk.ide.debug.ui.tabs.ImplementationTab;
import gov.redhawk.ide.debug.ui.tabs.LocalWaveformMainTab;
import gov.redhawk.sca.launch.ui.tabs.WaveformPropertiesTab;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

/**
 * 
 */
public class LocalWaveformLaunchConfigurationTabGroup extends AbstractLaunchConfigurationTabGroup {

	/**
	 * 
	 */
	public LocalWaveformLaunchConfigurationTabGroup() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * {@inheritDoc}
	 */
	public void createTabs(final ILaunchConfigurationDialog dialog, final String mode) {
		final LocalWaveformMainTab mainTab = new LocalWaveformMainTab();
		final WaveformPropertiesTab propTab = new WaveformPropertiesTab();
		final ImplementationTab implTab = new ImplementationTab();
		final ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
		        mainTab, propTab, implTab, new CommonTab()
		};
		setTabs(tabs);
	}

}
