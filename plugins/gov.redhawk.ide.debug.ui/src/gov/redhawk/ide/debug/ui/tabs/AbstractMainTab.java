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

import java.io.File;

import org.eclipse.core.externaltools.internal.IExternalToolConstants;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchConfigurationTab2;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.externaltools.internal.launchConfigurations.ExternalToolsLaunchConfigurationMessages;
import org.eclipse.ui.externaltools.internal.launchConfigurations.ExternalToolsMainTab;
import org.eclipse.ui.externaltools.internal.model.ExternalToolsPlugin;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import gov.redhawk.ide.debug.ScaDebugLaunchConstants;
import gov.redhawk.ide.debug.ui.ScaDebugUiPlugin;
import gov.redhawk.ide.sdr.preferences.IdeSdrPreferences;
import gov.redhawk.sca.launch.ScaLaunchConfigurationConstants;
import gov.redhawk.sca.launch.ui.ScaUIImages;

/**
 * @since 3.0
 */
@SuppressWarnings("restriction")
public abstract class AbstractMainTab extends ExternalToolsMainTab implements ILaunchConfigurationTab2 {

	private Image mainImage;
	private Button startButton;
	private Spinner timeout;

	public AbstractMainTab() {
		this.mainImage = ScaUIImages.DESC_MAIN_TAB.createImage();
	}

	@Override
	public String getName() {
		return "&Main";
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

	@Override
	public void createControl(final Composite parent) {
		// create the top level composite for the dialog area
		final Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);
		composite.setFont(parent.getFont());
		composite.setLayout(new GridLayout(1, false));

		createLocationComponent(composite);

		createOtherComponents(composite);

		final Group launchConfigGroup = new Group(composite, SWT.None);
		launchConfigGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		launchConfigGroup.setText("Launch Configuration");
		createLaunchGroup(launchConfigGroup);

	}

	/**
	 * Subclasses may add additional controls to the tab. They will appear after location information, before the
	 * launch configuration information.
	 * @since 3.0
	 */
	protected void createOtherComponents(Composite parent) {
		// Nothing added here by default
	}

	/**
	 * Creates the contents of the launch group.
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
	 * @since 3.1
	 */
	@Override
	protected void handleWorkspaceLocationButtonSelected() {
		ILabelProvider lp = new WorkbenchLabelProvider();
		ITreeContentProvider cp = new WorkbenchContentProvider();
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), lp, cp);
		dialog.addFilter(new ViewerFilter() {
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (!(element instanceof IFile)) {
					return true;
				}
				return ((IFile) element).getName().endsWith(getProfileExtension());
			}
		});
		dialog.setAllowMultiple(false);
		dialog.setBlockOnOpen(true);
		dialog.setTitle("Select a File");
		dialog.setValidator(selection -> {
			if (selection == null || selection.length != 1 || !(selection[0] instanceof IFile)) {
				return new Status(IStatus.ERROR, ScaDebugUiPlugin.PLUGIN_ID, "Select a file");
			}
			return new Status(IStatus.OK, ScaDebugUiPlugin.PLUGIN_ID, "");
		});
		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		dialog.open();
		lp.dispose();
		cp.dispose();

		Object[] results = dialog.getResult();
		if (results == null || results.length < 1) {
			return;
		}
		IFile file = (IFile) results[0];
		locationField.setText(newVariableExpression("workspace_loc", file.getFullPath().toString())); //$NON-NLS-1$
	}

	/**
	 * @since 3.1
	 */
	@Override
	protected void handleFileLocationButtonSelected() {
		FileDialog fileDialog = new FileDialog(getShell(), SWT.NONE);
		fileDialog.setFilterExtensions(new String[] { '*' + getProfileExtension() });
		IPath filterPath = getSdrDirectory();
		if (filterPath != null) {
			fileDialog.setFilterPath(filterPath.toOSString());
		}
		String text = fileDialog.open();
		if (text != null) {
			locationField.setText(text);
		}
	}

	@Override
	protected boolean validateWorkDirectory() {
		return true;
	}

	@Override
	public void setDefaults(final ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(ScaLaunchConfigurationConstants.ATT_START, ScaLaunchConfigurationConstants.DEFAULT_VALUE_ATT_START);
		configuration.setAttribute(ScaLaunchConfigurationConstants.ATT_PROFILE, (String) null);
		configuration.setAttribute(ScaDebugLaunchConstants.ATT_LAUNCH_TIMEOUT, ScaDebugLaunchConstants.DEFAULT_ATT_LAUNCH_TIMEOUT);
	}

	@Override
	public void initializeFrom(final ILaunchConfiguration configuration) {
		fInitializing = true;
		updateLocation(configuration);
		updateStartButton(configuration);
		updateTimeout(configuration);
		fInitializing = false;
		setDirty(false);
	}

	/**
	 * @since 3.0
	 */
	@Override
	protected void updateLocation(ILaunchConfiguration configuration) {
		String location = IExternalToolConstants.EMPTY_STRING;
		try {
			location = configuration.getAttribute(ScaLaunchConfigurationConstants.ATT_PROFILE, IExternalToolConstants.EMPTY_STRING);
		} catch (CoreException ce) {
			ExternalToolsPlugin.getDefault().log(ExternalToolsLaunchConfigurationMessages.ExternalToolsMainTab_Error_reading_configuration_10, ce);
		}
		locationField.setText(location);
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

	private void updateTimeout(final ILaunchConfiguration configuration) {
		int value = ScaDebugLaunchConstants.DEFAULT_ATT_LAUNCH_TIMEOUT;
		try {
			value = configuration.getAttribute(ScaDebugLaunchConstants.ATT_LAUNCH_TIMEOUT, ScaDebugLaunchConstants.DEFAULT_ATT_LAUNCH_TIMEOUT);
		} catch (final CoreException e) {
			ScaDebugUiPlugin.log(e);
		}
		this.timeout.setSelection(value);

	}

	@Override
	public void performApply(final ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(ScaLaunchConfigurationConstants.ATT_START, this.startButton.getSelection());
		configuration.setAttribute(ScaLaunchConfigurationConstants.ATT_PROFILE, this.locationField.getText());
		configuration.setAttribute(ScaDebugLaunchConstants.ATT_LAUNCH_TIMEOUT, this.timeout.getSelection());
	}

	/**
	 * @since 3.0
	 */
	@Override
	protected boolean validateLocation(boolean newConfig) {
		String location = locationField.getText().trim();
		if (location.length() < 1) {
			if (newConfig) {
				setErrorMessage(null);
				setMessage(ExternalToolsLaunchConfigurationMessages.ExternalToolsMainTab_30);
			} else {
				setErrorMessage(ExternalToolsLaunchConfigurationMessages.ExternalToolsMainTab_External_tool_location_cannot_be_empty_18);
				setMessage(null);
			}
			return false;
		}

		String expandedLocation = null;
		try {
			expandedLocation = resolveValue(location);
			if (expandedLocation == null) { // a variable that needs to be resolved at runtime
				return true;
			}
		} catch (CoreException e) {
			setErrorMessage(e.getStatus().getMessage());
			return false;
		}

		File file = new File(expandedLocation);
		if (!file.exists()) { // The file does not exist.
			if (!newConfig) {
				setErrorMessage(ExternalToolsLaunchConfigurationMessages.ExternalToolsMainTab_External_tool_location_does_not_exist_19);
			}
			return false;
		}
		if (!file.isFile()) {
			if (!newConfig) {
				setErrorMessage(ExternalToolsLaunchConfigurationMessages.ExternalToolsMainTab_External_tool_location_specified_is_not_a_file_20);
			}
			return false;
		}
		return validateLocation(file, newConfig);
	}

	/**
	 * @since 3.0
	 */
	protected File getLocationFile() {
		String location = locationField.getText().trim();
		if (location.length() < 1) {
			return null;
		}

		String expandedLocation = null;
		try {
			expandedLocation = resolveValue(location);
		} catch (CoreException e) {
			return null;
		}

		File file = new File(expandedLocation);
		if (!file.exists()) { // The file does not exist.
			return null;
		}
		if (!file.isFile()) {
			return null;
		}
		return file;
	}

	/**
	 * @param newConfig
	 * @since 3.0
	 */
	protected boolean validateLocation(File file, boolean newConfig) {
		if (!file.getName().endsWith(getProfileExtension())) {
			if (!newConfig) {
				setErrorMessage("Not a valid waveform file.  Must end with '" + getProfileExtension() + "'");
			}
			return false;
		}
		return true;
	}

	private String resolveValue(String expression) throws CoreException {
		String expanded = null;
		try {
			expanded = getValue(expression);
		} catch (CoreException e) { // possibly just a variable that needs to be resolved at runtime
			validateVaribles(expression);
			return null;
		}
		return expanded;
	}

	/**
	 * Validates the variables of the given string to determine if all variables are valid
	 *
	 * @param expression expression with variables
	 * @exception CoreException if a variable is specified that does not exist
	 */
	private void validateVaribles(String expression) throws CoreException {
		IStringVariableManager manager = VariablesPlugin.getDefault().getStringVariableManager();
		manager.validateStringVariables(expression);
	}

	/**
	 * Validates the value of the given string to determine if any/all variables are valid
	 *
	 * @param expression expression with variables
	 * @return whether the expression contained any variable values
	 * @exception CoreException if variable resolution fails
	 */
	private String getValue(String expression) throws CoreException {
		IStringVariableManager manager = VariablesPlugin.getDefault().getStringVariableManager();
		return manager.performStringSubstitution(expression);
	}

	protected abstract String getProfileExtension();

	/**
	 * @return A directory in the SDR root where files of the appropriate type can be found, or null for unspecified.
	 * @since 3.1
	 */
	protected IPath getSdrDirectory() {
		return IdeSdrPreferences.getTargetSdrPath();
	}

}
