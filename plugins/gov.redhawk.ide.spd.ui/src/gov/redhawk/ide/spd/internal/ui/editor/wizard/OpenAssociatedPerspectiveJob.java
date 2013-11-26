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
package gov.redhawk.ide.spd.internal.ui.editor.wizard;

import gov.redhawk.ide.codegen.ICodeGeneratorDescriptor;
import gov.redhawk.ide.spd.ui.ComponentUiPlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.progress.UIJob;

/**
 * Default behavior is to prompt the user to change perspective to the
 * perspective associated with the {@link ICodeGeneratorDescriptor}. If the
 * associated preference has previously been changed, will either do nothing or
 * automatically change the perspective.
 */
public class OpenAssociatedPerspectiveJob extends UIJob {

	private final ICodeGeneratorDescriptor descriptor;

	/**
	 * Creates an {@link OpenAssociatedPerspectiveJob} based on the specified
	 * {@link ICodeGeneratorDescriptor}.
	 * 
	 * @param descriptor the descriptor to obtain the perspective id from
	 */
	public OpenAssociatedPerspectiveJob(final ICodeGeneratorDescriptor descriptor) {
		super("Change Perspective");
		this.descriptor = descriptor;
		this.setSystem(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IStatus runInUIThread(final IProgressMonitor monitor) {
		final String perspectiveId = this.descriptor.getAssociatedPerspectiveId();
		if (perspectiveId != null && !perspectiveId.isEmpty()) {
			final IPreferenceStore store = ComponentUiPlugin.getDefault().getPreferenceStore();
			final String pref = store.getString(this.descriptor.getId());
			if (pref.equals(MessageDialogWithToggle.PROMPT)) {
				final MessageDialogWithToggle dialog = this.createDialog();
				final int result = dialog.open();
				this.updatePreferences(this.descriptor.getId(), result, dialog.getToggleState());
				if (result == IDialogConstants.YES_ID) {
					this.changePerspective();
				}
			} else if (pref.equals(MessageDialogWithToggle.ALWAYS)) {
				this.changePerspective();
			}
		}
		return Status.OK_STATUS;
	}

	/**
	 * Creates the dialog for changing perspectives.
	 * 
	 * @return the {@link MessageDialogWithToggle} to display
	 */
	private MessageDialogWithToggle createDialog() {
		final String[] buttons = new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL };
		return new MessageDialogWithToggle(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Open Associated Perspective?", null,
		        "Do you want to open the perspective associated with the " + this.descriptor.getName() + "?", MessageDialog.QUESTION, buttons, 0,
		        "Remember my decision", false);
	}

	/**
	 * Change the perspective to the perspective associated with the descriptor.
	 */
	private void changePerspective() {
		try {
			final IWorkbench workbench = PlatformUI.getWorkbench();
			if (workbench != null) {
				workbench.showPerspective(this.descriptor.getAssociatedPerspectiveId(), workbench.getActiveWorkbenchWindow());
			}

		} catch (final WorkbenchException e) {
			ComponentUiPlugin.logException(e);
		}
	}

	/**
	 * Updates the preference with the specified value.
	 * 
	 * @param key the key to update
	 * @param value the value to associate with the preference
	 * @param save whether to update the preference with the specified value
	 */
	private void updatePreferences(final String key, final int value, final boolean save) {
		if (save) {
			final IPreferenceStore store = ComponentUiPlugin.getDefault().getPreferenceStore();
			if (value == IDialogConstants.YES_ID) {
				store.setValue(key, MessageDialogWithToggle.ALWAYS);
			} else {
				store.setValue(key, MessageDialogWithToggle.NEVER);
			}
		}
	}
}
