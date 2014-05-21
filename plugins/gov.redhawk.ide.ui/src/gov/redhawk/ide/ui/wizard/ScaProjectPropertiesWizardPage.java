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

import gov.redhawk.ui.validation.ProjectNameValidator;
import mil.jpeojtrs.sca.util.DceUuidUtil;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

/**
 * @since 8.0
 */
public class ScaProjectPropertiesWizardPage extends WizardNewProjectCreationPage implements IValidatableWizardPage, IWizardPage {

	/**
	 * The string name of the resourceType of resource being created("Component", "Device", "Node", "Service" or "Waveform")
	 */
	private String resourceType = "";

	/** Extension type of the associated resource */
	private String resourceExtension = "";

	private ContentsGroup contentsGroup;

	private IDGroup idGroup;

	private boolean showContentsGroup = true;

	private boolean showComponentIDGroup = true;

	private boolean showWorkingSetGroup = true;

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

	/**
	 * @since 9.1
	 */
	public void setShowComponentIDGroup(final boolean value) {
		this.showComponentIDGroup = value;
	}

	/**
	 * @since 9.1
	 */
	public void setShowWorkingSetGroup(final boolean value) {
		this.showWorkingSetGroup = value;
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
		if (this.showComponentIDGroup) {
			this.idGroup = new IDGroup(dialogArea, SWT.None, this.resourceType, this);
			this.idGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL));
		}

		// Working Set group
		if (this.showWorkingSetGroup) {
			String[] wsTypes = new String[1];
			wsTypes[0] = "org.eclipse.ui.resourceWorkingSetPage";
			createWorkingSetGroup(dialogArea, new StructuredSelection(), wsTypes);
		}

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

	public void customCreateControl(final Composite composite) {
		return;
	}

	/**
	 * Returns the possibly null contents group
	 * @deprecated use {@link getIsCreateNewResource()} instead if that is needed
	 * @return
	 */
	@Deprecated
	public ContentsGroup getContentsGroup() {
		return this.contentsGroup;
	}

	/**
	 * 
	 * @return If a new resource is being created.
	 * @since 9.1
	 */
	public boolean isCreateNewResource() {
		if (this.showContentsGroup) {
			return this.contentsGroup.isCreateNewResource();
		} else {
			return true;
		}
	}

	/**
	 * @since 9.1
	 */
	public IPath getExistingResourcePath() {
		if (this.showContentsGroup) {
			return this.contentsGroup.getExistingResourcePath();
		} else {
			return null;
		}
	}

	/**
	 * Returns the ID group which could be null.
	 * 
	 * @deprecated use {@link getID()} instead if the ID is needed
	 */
	@Deprecated
	public IDGroup getIdGroup() {
		return this.idGroup;
	}

	/**
	 * @since 9.1
	 */
	public String getID() {
		String id = null;

		if (this.showComponentIDGroup) {
			id = this.idGroup.getId();
		} else {
			id = DceUuidUtil.createDceUUID();
		}

		return id;
	}

	@Override
	protected boolean validatePage() {
		ProjectNameValidator nameValidator = new ProjectNameValidator();

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
				if (this.contentsGroup != null && !this.contentsGroup.isCreateNewResource()) {
					path = this.contentsGroup.getExistingResourcePath().toOSString();
				}
				((IImportWizard) this.getWizard()).importSelected(path);
			}

		}

		if (this.showComponentIDGroup) {
			status = this.idGroup.validateGroup();
		}

		if (!status.isOK()) {
			setMessage(status);
			return false;
		}
		status = nameValidator.validate(getProjectName());
		if (!status.isOK()) {
			setMessage(status);
			return false;
		}
		return true;
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

	@Override
	public void validate() {
		final boolean ok = validatePage();
		if (ok) {
			setMessage((String) null);
		}
		setPageComplete(ok);
	}
}
