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
package gov.redhawk.ide.ui.preferences;

import gov.redhawk.ide.preferences.RedhawkIdePreferenceConstants;
import gov.redhawk.ide.ui.RedhawkIDEUiPlugin;
import gov.redhawk.ui.util.DirectoryFieldEditorWithSupportForEnvironment;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

// TODO: Auto-generated Javadoc
/**
 * The Class WorkbenchPreferencePage1.
 * 
 * TODO: Stop cheating by using a FieldEditor since we won't build the complex
 * preference page yet.
 */
public class RedhawkIdeTargetPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(final IWorkbench workbench) {
		setPreferenceStore(RedhawkIDEUiPlugin.getDefault().getRedhawkIdePreferenceStore());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createFieldEditors() {

		final DirectoryFieldEditorWithSupportForEnvironment runtimePath = new DirectoryFieldEditorWithSupportForEnvironment(
		        RedhawkIdePreferenceConstants.RH_IDE_RUNTIME_PATH_PREFERENCE, "Runtime Location", getFieldEditorParent());
		addField(runtimePath);

		final IdlListEditor idlLibraries = new IdlListEditor("IDL Locations", getFieldEditorParent(), this.getPreferenceStore());
		addField(idlLibraries);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performOk() {
		final IJobManager jobMan = Job.getJobManager();
		final Job[] build = jobMan.find(ResourcesPlugin.FAMILY_MANUAL_REFRESH);

		if (build.length > 0) {
			for (final Job job : build) {
				if (job.getName().equals("Updating Runtime Path")) {
					return false;
				}
			}
		}
		return super.performOk();
	}
}
