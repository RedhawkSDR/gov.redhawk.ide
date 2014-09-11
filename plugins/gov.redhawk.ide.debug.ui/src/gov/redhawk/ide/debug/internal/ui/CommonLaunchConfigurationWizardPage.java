/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.debug.internal.ui;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

public class CommonLaunchConfigurationWizardPage extends WizardPage {
	private Button startButton;
	private Spinner timeout;

	public CommonLaunchConfigurationWizardPage() {
		super("commonConfigPage", "Launch Configuration Options", null);
	}

	@Override
	public final void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.None);
		main.setLayout(new GridLayout(2, false));
		
		startButton = new Button(main, SWT.CHECK);
		startButton.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(2, 1).create());
		startButton.setText("Auto-start");

		final Label timeoutLabel = new Label(main, SWT.None);
		timeoutLabel.setText("Timeout:");
		timeout = new Spinner(main, SWT.BORDER);
		timeout.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		timeout.setMinimum(-1);
		timeout.setToolTipText("Time in seconds to wait for a resource to register with the naming context.  "
			+ "A value of -1 will wait forever.");

		createOtherControls(main);
		
		setControl(main);
	}
	
	public Button getStartButton() {
		return startButton;
	}
	
	public Spinner getTimeout() {
		return timeout;
	}

	protected void createOtherControls(Composite main) {
		// TODO Auto-generated method stub
		
	}

}
