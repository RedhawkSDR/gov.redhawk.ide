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
package gov.redhawk.ide.debug.internal.ui.wizards;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

public class CommonLaunchConfigurationWizardPage extends WizardPage {
	private Button startButton;
	private Spinner timeout;
	private Button runConfigurationButton;
	private boolean showAutoStart;

	public CommonLaunchConfigurationWizardPage(boolean withAutoStart) {
		super("commonConfigPage", "Launch Configuration Options", null);
		setDescription("Set common post-launch options.");
		showAutoStart = withAutoStart;
	}

	public CommonLaunchConfigurationWizardPage() {
		this(true);
	}

	@Override
	public final void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.None);
		main.setLayout(new GridLayout(1, false));

		Group postLaunch = new Group(main, SWT.None);
		postLaunch.setText("Post Launch");
		postLaunch.setLayout(new GridLayout(2, false));
		postLaunch.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(1, 1).create());
		if (showAutoStart) {
			startButton = new Button(postLaunch, SWT.CHECK);
			startButton.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(2, 1).create());
			startButton.setText("Auto-start");
			startButton.setToolTipText("Start the component automatically immediately after launching.");
		}
		final Label timeoutLabel = new Label(postLaunch, SWT.None);
		timeoutLabel.setText("Timeout:");
		timeout = new Spinner(postLaunch, SWT.BORDER);
		timeout.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		timeout.setMinimum(-1);
		timeout.setToolTipText("Time in seconds to wait for a resource to register with the naming context.  " + "A value of -1 will wait forever.");

		createOtherControls(main);

		Label spacer = new Label(main, SWT.None);
		spacer.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		runConfigurationButton = new Button(main, SWT.CHECK);
		runConfigurationButton.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		runConfigurationButton.setText("Save as new run configuration");
		runConfigurationButton.setToolTipText("Saves the settings as a new run configuration for quick reuse.  "
			+ "The run configuration is available from the 'Run' menu or from the run icon on the toolbar.");

		bindControls();

		setControl(main);
	}

	protected void bindControls() {
		// TODO Auto-generated method stub

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

	public Button getRunConfigurationButton() {
		return runConfigurationButton;
	}

}
