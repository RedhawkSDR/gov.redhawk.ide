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
package gov.redhawk.ide.ui.wizard;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

/**
 * @since 8.0
 */
public class ScaProjectPropertiesWizardPage extends WizardNewProjectCreationPage implements IValidatableWizardPage {

	/**
	 * The string name of the resourceType of resource being created("Component", "Device", "Node", "Service" or "Waveform")
	 */
	private String resourceType = "";

	/** Extension type of the associated resource */
	private String resourceExtension = "";

	private ContentsGroup contentsGroup;

	private IDGroup idGroup;

	private boolean showContentsGroup = true;

	private static final String VALID_IMPL_NAME_REGEX = "^[A-Za-z][A-Za-z0-9_-]*";

	protected ScaProjectPropertiesWizardPage(final String pageName, final String resourceType, final String resourceExtension) {
		super(pageName);
		this.resourceType = resourceType;
		this.resourceExtension = resourceExtension;
	}

	public String getResourceType() {
		return this.resourceType;
	}

	public String getResourceExtension() {
		return this.resourceExtension;
	}

	public void setShowContentsGroup(final boolean value) {
		this.showContentsGroup = value;
	}

	@Override
	public void createControl(final Composite parent) {
		super.createControl(parent);

		final Composite dialogArea = (Composite) getControl();

		// Custom Wizard settings
		customCreateControl(dialogArea);

		// Contents Group
		if (this.showContentsGroup) {
			createContentsGroup(dialogArea);
		}

		// ID Group
		this.idGroup = new IDGroup(dialogArea, SWT.None, this.resourceType, this);
		this.idGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL));

		// Working Set group
		createWorkingSetGroup(dialogArea, new StructuredSelection(), getWorkingSetNames());

		setPageComplete(validatePage());
		// Show description on opening
		setErrorMessage(null);
		setMessage((String) null);
	}

	/**
     * @since 9.0
     */
	protected void createContentsGroup(Composite parent) {
		this.contentsGroup = new ContentsGroup(parent, SWT.None, this.resourceType, this.resourceExtension, this);
		this.contentsGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL));
    }

	private String[] getWorkingSetNames() {
		final List<String> workingSetNames = new ArrayList<String>();
		for (final IWorkingSet set : PlatformUI.getWorkbench().getWorkingSetManager().getWorkingSets()) {
			workingSetNames.add(set.getId());
		}
		return workingSetNames.toArray(new String[workingSetNames.size()]);
	}

	public void customCreateControl(final Composite composite) {
		return;
	}

	public ContentsGroup getContentsGroup() {
		return this.contentsGroup;
	}

	public IDGroup getIdGroup() {
		return this.idGroup;
	}

	@Override
	protected boolean validatePage() {
		if (!super.validatePage()) {
			return false;
		}

		IStatus status = Status.OK_STATUS;
		if (this.contentsGroup != null) {
			status = this.contentsGroup.validateGroup();
		}
		if (!status.isOK()) {
			setMessage(status);
			if (this.getWizard() instanceof IImportWizard) {
				((IImportWizard) this.getWizard()).importSelected("");
			}
			return false;
		} else {
			if (this.getWizard() instanceof IImportWizard) {
				String path = "";
				if (!this.contentsGroup.isCreateNewResource()) {
					path = this.contentsGroup.getExistingResourcePath().toOSString();
				}
				((IImportWizard) this.getWizard()).importSelected(path);
			}

		}
		status = this.idGroup.validateGroup();
		if (!status.isOK()) {
			setMessage(status);
			return false;
		}

		status = validateProjectName(getProjectName());
		if (!status.isOK()) {
			setMessage(status);
			return false;
		}
		return true;
	}

	private IStatus validateProjectName(final String projectName) {
		if (!Pattern.matches(ScaProjectPropertiesWizardPage.VALID_IMPL_NAME_REGEX, projectName)) {
			return ValidationStatus.error("Invalid character present in project name.");
		}

		return ValidationStatus.ok();
	}

	protected void setMessage(final IStatus status) {
		int severity = 0;
		switch (status.getSeverity()) {
		case IStatus.WARNING:
			severity = IMessageProvider.WARNING;
			break;
		case IStatus.ERROR:
			severity = IMessageProvider.ERROR;
			break;
		case IStatus.INFO:
			severity = IMessageProvider.INFORMATION;
			break;
		default:
			break;
		}
		setMessage(status.getMessage(), severity);
	}

	public void validate() {
		final boolean ok = validatePage();
		if (ok) {
			setMessage((String) null);
		}
		setPageComplete(ok);
	}
}
