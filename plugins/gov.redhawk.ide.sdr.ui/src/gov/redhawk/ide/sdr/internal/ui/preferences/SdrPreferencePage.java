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
package gov.redhawk.ide.sdr.internal.ui.preferences;

import gov.redhawk.ide.sdr.IdeSdrActivator;
import gov.redhawk.ide.sdr.preferences.IdeSdrPreferenceConstants;
import gov.redhawk.ui.util.DirectoryFieldEditorWithSupportForEnvironment;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class SdrPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public SdrPreferencePage() {
		super(GRID);
		setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, IdeSdrActivator.PLUGIN_ID));
	}

	@Override
	public void createFieldEditors() {
		final DirectoryFieldEditorWithSupportForEnvironment localSdrPath = new DirectoryFieldEditorWithSupportForEnvironment(
			IdeSdrPreferenceConstants.SCA_LOCAL_SDR_PATH_PREFERENCE, "Local SDR Location:", getFieldEditorParent());
		addField(localSdrPath);
	}

	@Override
	public void init(final IWorkbench workbench) {
	}

}
