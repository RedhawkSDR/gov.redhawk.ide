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
package gov.redhawk.ide.snapshot.ui;

import gov.redhawk.ide.snapshot.writer.IDataWriter;
import gov.redhawk.ide.snapshot.writer.IDataWriterDesc;
import gov.redhawk.ide.snapshot.writer.IDataWriterSettings;
import gov.redhawk.sca.ui.ScaUiPlugin;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.statushandlers.StatusManager;

public class SnapshotWizard extends Wizard {
	
	private static final String DIALOG_SETTINGS_SECTION = SnapshotWizard.class.getName();

	private SnapshotWizardPage snapshotPage;
	private IDataWriter dataWriter;

	public SnapshotWizard() {
		setWindowTitle("Snapshot");
		
		IDialogSettings section = ScaUiPlugin.getDefault().getDialogSettings().getSection(DIALOG_SETTINGS_SECTION);
		if (section == null) {
			section = ScaUiPlugin.getDefault().getDialogSettings().addNewSection(DIALOG_SETTINGS_SECTION);
		}
		setDialogSettings(section);
	}

	@Override
	public void addPages() {
		setSnapshotPage(new SnapshotWizardPage("snapshot", "Save As", null));

		addPage(snapshotPage);
	}

	protected void setSnapshotPage(SnapshotWizardPage snapshotWizardPage) {
		this.snapshotPage = snapshotWizardPage;
	}

	public SnapshotWizardPage getSnapshotPage() {
		return snapshotPage;
	}

	public IDataWriter getDataWriter() {
		return dataWriter;
	}

	@Override
	public boolean performFinish() {
		final SnapshotSettings settings = snapshotPage.getSettings();
		snapshotPage.saveWidgetValues(settings);
		IDataWriterDesc desc = settings.getDataWriter();
		try {
			IDataWriterSettings writerSettings = desc.createWriterSettings();
			if (settings.isSaveToWorkspace()) {
				if (settings.getPath() == null || settings.getPath().trim().length() == 0) {
					throw new CoreException(new Status(IStatus.WARNING, SnapshotActivator.PLUGIN_ID, "Workspace File Name must be specified."));
				}
				writerSettings.setDestination(settings.getIFile());
			} else {
				if (settings.getFileName() == null || settings.getFileName().trim().length() == 0) {
					throw new CoreException(new Status(IStatus.WARNING, SnapshotActivator.PLUGIN_ID, "File Name must be specified."));
				}
				writerSettings.setDestination(settings.getDestinationFile());
			}
			this.dataWriter = desc.createWriter();
			this.dataWriter.setSettings(writerSettings);

			final Shell parent = this.getShell().getParent().getShell();
			if (settings.isConfirmOverwrite() && checkForSimilarFiles()) {
				MessageBox override = new MessageBox(parent, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				override.setMessage("There are files in this directory that may be overwritten.\n" + "Do you want to proceed?");
				int result = override.open();
				if (result == SWT.NO) {
					return false;
				}
			}
		} catch (CoreException e) {
			IStatus status = e.getStatus();
			StatusManager.getManager().handle(status, StatusManager.SHOW | StatusManager.LOG);
			return false;
		}

		return true;
	}

	private boolean checkForSimilarFiles() {
		for (File f : dataWriter.getOutputFileList()) {
			if (f.exists()) {
				return true;
			}
		}
		return false;
	}

}
