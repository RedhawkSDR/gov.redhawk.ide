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

import gov.redhawk.ide.debug.ui.tabs.ComponentPropertiesTab;
import gov.redhawk.ide.debug.ui.tabs.LocalComponentMainTab;

import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.ui.externaltools.internal.program.launchConfigurations.ProgramTabGroup;

/**
 * 
 */
@SuppressWarnings("restriction")
public class LocalComponentProgramTabGroup extends ProgramTabGroup {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createTabs(final ILaunchConfigurationDialog dialog, final String mode) {
		super.createTabs(dialog, mode);
		final ILaunchConfigurationTab[] tabs = getTabs();
		final ILaunchConfigurationTab[] newTabs = new ILaunchConfigurationTab[tabs.length + 2];
		newTabs[0] = new LocalComponentMainTab();
		newTabs[1] = new ComponentPropertiesTab();
		System.arraycopy(tabs, 0, newTabs, 2, tabs.length);
		setTabs(newTabs);

	}

}
