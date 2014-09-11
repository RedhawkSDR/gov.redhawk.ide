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
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * 
 */
public class ComponentCommonLaunchConfigurationWizardPage extends CommonLaunchConfigurationWizardPage {
	
	private DataBindingContext dbc = new DataBindingContext();
	private LaunchComponentWizard wizard;
	
	public ComponentCommonLaunchConfigurationWizardPage(LaunchComponentWizard wizard) {
		this.wizard = wizard;
	}

	@Override
	protected void createOtherControls(Composite parent) {
		final Label label = new Label(parent, SWT.None);
		label.setText("Debug Level:");
		Combo levelCombo = new Combo(parent, SWT.BORDER | SWT.READ_ONLY);
		levelCombo.setItems(new String[] {
			"Default", "Fatal", "Error", "Warn", "Info", "Debug", "Trace"
		});
		levelCombo.setLayoutData(GridDataFactory.swtDefaults().create());
		levelCombo.setToolTipText("Set the debug logging level of the component.  Output will be visible in the console view.");

		dbc.bindValue(SWTObservables.observeSelection(getStartButton()), BeansObservables.observeValue(wizard, "autoStart"));
		dbc.bindValue(SWTObservables.observeSelection(getTimeout()), BeansObservables.observeValue(wizard, "timeout"));
		dbc.bindValue(SWTObservables.observeSelection(levelCombo), BeansObservables.observeValue(wizard, "debugLevel"));
	}
}
