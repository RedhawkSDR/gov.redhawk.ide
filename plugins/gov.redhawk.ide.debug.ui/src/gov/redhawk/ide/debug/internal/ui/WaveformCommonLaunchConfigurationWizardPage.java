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

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.jface.databinding.swt.WidgetProperties;

/**
 * 
 */
public class WaveformCommonLaunchConfigurationWizardPage extends CommonLaunchConfigurationWizardPage {
	private DataBindingContext dbc = new DataBindingContext();
	private LaunchLocalWaveformWizard wizard;
	
	public WaveformCommonLaunchConfigurationWizardPage(LaunchLocalWaveformWizard wizard) {
		this.wizard = wizard;
	}
	
	@Override
	protected void bindControls() {
		dbc.bindValue(WidgetProperties.selection().observe(getStartButton()), BeanProperties.value(wizard.getClass(), "autoStart").observe(wizard));
		dbc.bindValue(WidgetProperties.selection().observe(getTimeout()), BeanProperties.value(wizard.getClass(), "timeout").observe(wizard));
		dbc.bindValue(WidgetProperties.selection().observe(getRunConfigurationButton()), BeanProperties.value(wizard.getClass(), "saveRunConfiguration").observe(wizard));
	}

}
