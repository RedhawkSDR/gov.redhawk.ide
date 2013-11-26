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
package gov.redhawk.ide.debug.ui.tabs;

import gov.redhawk.ide.debug.ScaDebugLaunchConstants;
import gov.redhawk.ide.debug.ui.ScaDebugUiPlugin;
import gov.redhawk.sca.launch.ScaLaunchConfigurationConstants;
import gov.redhawk.sca.launch.ui.ScaUIImages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * 
 */
public abstract class LocalAbstractMainTab extends AbstractLaunchConfigurationTab {

	private Text profileText;
	private Image mainImage;
	private Button startButton;
	private Spinner timeout;

	public LocalAbstractMainTab() {
		this.mainImage = ScaUIImages.DESC_MAIN_TAB.createImage();
	}

	@Override
	public Image getImage() {
		return this.mainImage;
	}

	@Override
	public void dispose() {
		this.mainImage.dispose();
		this.mainImage = null;
		super.dispose();
	}

	protected Text getProfileText() {
		return this.profileText;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createControl(final Composite parent) {
		// create the top level composite for the dialog area
		final Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);
		composite.setFont(parent.getFont());
		composite.setLayout(new GridLayout(1, false));

		final Group profileGroup = new Group(composite, SWT.None);
		profileGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		profileGroup.setText("SCA Profile");
		createProfileGroup(profileGroup);

		final Group launchConfigGroup = new Group(composite, SWT.None);
		launchConfigGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		launchConfigGroup.setText("Launch Configuration");
		createLaunchGroup(launchConfigGroup);

	}

	/**
	 * @param launchConfigGroup
	 */
	protected void createLaunchGroup(final Composite parent) {
		parent.setLayout(new GridLayout(3, false));

		this.startButton = new Button(parent, SWT.CHECK);
		this.startButton.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(3, 1).create());
		this.startButton.setText("Auto-start");
		this.startButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
		final Label timeoutLabel = new Label(parent, SWT.None);
		timeoutLabel.setText("Timeout:");
		this.timeout = new Spinner(parent, SWT.BORDER);
		this.timeout.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(2, 1).create());
		this.timeout.setMinimum(-1);
		this.timeout.setToolTipText("Time in seconds to wait for a component to register with the naming context.  "
			+ "A value of -1 will wait forever.\n Note when run in debug mode timeout is always treated as -1.");
		this.timeout.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				updateLaunchConfigurationDialog();
			}
		});

	}

	/**
	 * @param parent
	 */
	protected void createProfileGroup(final Composite parent) {
		parent.setLayout(new GridLayout(3, false));
		createProfileLabel(parent);
		this.profileText = new Text(parent, SWT.BORDER);
		this.profileText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.profileText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
		final Button browseButton = new Button(parent, SWT.PUSH);
		browseButton.setText("&Browse...");
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final IResource resource = browseForProfile();
				if (resource != null) {
					LocalAbstractMainTab.this.profileText.setText(resource.getFullPath().toPortableString());
				}
			}
		});
	}

	protected abstract Label createProfileLabel(Composite parent);

	protected IResource browseForProfile() {

		final ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), new WorkbenchLabelProvider());
		final List<IResource> files = new ArrayList<IResource>();
		try {
			ResourcesPlugin.getWorkspace().getRoot().accept(new IResourceVisitor() {

				@Override
				public boolean visit(final IResource resource) throws CoreException {
					if (resource instanceof IContainer) {
						return true;
					}
					if (resource.getName().endsWith(getProfileExtension()) && resource.getName().charAt(0) != '.') {
						files.add(resource);
					}
					return false;
				}
			});
		} catch (final CoreException e) {
			// PASS
		}
		dialog.setElements(files.toArray());
		dialog.setTitle("Browse");
		dialog.setMessage("Select:");
		dialog.setMultipleSelection(false);
		dialog.setInitialElementSelections(Collections.singletonList(ResourcesPlugin.getWorkspace().getRoot().findMember(this.profileText.getText())));
		if (dialog.open() == Window.OK) {
			return (IResource) dialog.getFirstResult();
		}
		return null;
	}

	protected abstract String getProfileExtension();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDefaults(final ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(ScaLaunchConfigurationConstants.ATT_START, ScaLaunchConfigurationConstants.DEFAULT_VALUE_ATT_START);
		configuration.setAttribute(ScaLaunchConfigurationConstants.ATT_PROFILE, (String) null);
		configuration.setAttribute(ScaDebugLaunchConstants.ATT_LAUNCH_TIMEOUT, ScaDebugLaunchConstants.DEFAULT_ATT_LAUNCH_TIMEOUT);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initializeFrom(final ILaunchConfiguration configuration) {
		updateProfileText(configuration);
		updateStartButton(configuration);
		updateTimeout(configuration);
	}

	private void updateTimeout(final ILaunchConfiguration configuration) {
		int value = ScaDebugLaunchConstants.DEFAULT_ATT_LAUNCH_TIMEOUT;
		try {
			value = configuration.getAttribute(ScaDebugLaunchConstants.ATT_LAUNCH_TIMEOUT, ScaDebugLaunchConstants.DEFAULT_ATT_LAUNCH_TIMEOUT);
		} catch (final CoreException e) {
			ScaDebugUiPlugin.log(e);
		}
		this.timeout.setSelection(value);

	}

	private void updateStartButton(final ILaunchConfiguration configuration) {
		boolean selected = false;
		try {
			selected = configuration.getAttribute(ScaLaunchConfigurationConstants.ATT_START, ScaLaunchConfigurationConstants.DEFAULT_VALUE_ATT_START);
		} catch (final CoreException e) {
			ScaDebugUiPlugin.log(e);
		}
		this.startButton.setSelection(selected);
	}

	private void updateProfileText(final ILaunchConfiguration configuration) {
		String text = "";
		try {
			text = configuration.getAttribute(ScaLaunchConfigurationConstants.ATT_PROFILE, text);
		} catch (final CoreException e) {
			ScaDebugUiPlugin.log(e);
		}
		this.profileText.setText(text);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void performApply(final ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(ScaLaunchConfigurationConstants.ATT_START, this.startButton.getSelection());
		configuration.setAttribute(ScaLaunchConfigurationConstants.ATT_PROFILE, this.profileText.getText());
		configuration.setAttribute(ScaDebugLaunchConstants.ATT_LAUNCH_TIMEOUT, this.timeout.getSelection());
	}

	@Override
	public boolean isValid(final ILaunchConfiguration launchConfig) {
		if (!isProfileValid()) {
			return false;
		}
		setErrorMessage(null);
		return super.isValid(launchConfig);
	}

	/**
	 * 
	 */
	protected boolean isProfileValid() {
		if (this.profileText.getText().trim().length() == 0) {
			setErrorMessage("File does not exist.");
			return false;
		} else {
			final IResource member = ResourcesPlugin.getWorkspace().getRoot().findMember(this.profileText.getText());
			if (member == null) {
				setErrorMessage("File does not exist.");
				return false;
			} else {
				if (!member.getName().endsWith(getProfileExtension())) {
					setErrorMessage("Invalid profile.");
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return "&Main";
	}

}
