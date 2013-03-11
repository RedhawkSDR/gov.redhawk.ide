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
package gov.redhawk.ide.spd.ui;

import gov.redhawk.ide.codegen.ICodeGeneratorDescriptor;
import gov.redhawk.ide.codegen.RedhawkCodegenActivator;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @since 5.0
 */
public class ComponentUiPreferenceInitializer extends AbstractPreferenceInitializer {

	public ComponentUiPreferenceInitializer() {
		//Default constructor
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initializeDefaultPreferences() {
		final IPreferenceStore store = ComponentUiPlugin.getDefault().getPreferenceStore();
		for (final ICodeGeneratorDescriptor descriptor : RedhawkCodegenActivator.getCodeGeneratorsRegistry().getCodegens()) {
			store.setDefault(descriptor.getId(), MessageDialogWithToggle.PROMPT);
		}
	}
}
