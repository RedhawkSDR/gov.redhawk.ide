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
package gov.redhawk.ide.preferences;

import gov.redhawk.eclipsecorba.library.IdlLibrary;
import gov.redhawk.eclipsecorba.library.LibraryFactory;
import gov.redhawk.eclipsecorba.library.PreferenceNodePathSet;
import gov.redhawk.ide.RedhawkIdeActivator;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * Class used to initialize default REDHAWK IDE preferences:
 * <p />
 * <ul>
 * <li>Runtime path (i.e. OSSIEHOME)</li>
 * <li>IDL include path</li>
 * <li>IDL include path delimiter</li>
 * </ul>
 */
public class RedhawkIdePreferenceInitializer extends AbstractPreferenceInitializer {

	public RedhawkIdePreferenceInitializer() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initializeDefaultPreferences() {
		final IEclipsePreferences defaultNode = new DefaultScope().getNode(RedhawkIdeActivator.PLUGIN_ID);

		if (defaultNode != null) {
			if (Platform.getOS().equals(Platform.OS_WIN32)) {
				// The RH Runtime cannot be installed on Windows...yet
				defaultNode.put(RedhawkIdePreferenceConstants.RH_IDE_RUNTIME_PATH_PREFERENCE, "");
				defaultNode.put(RedhawkIdePreferenceConstants.RH_IDE_IDL_INCLUDE_PATH_PREFERENCE, "");
				defaultNode.put(RedhawkIdePreferenceConstants.RH_IDE_IDL_INCLUDE_PATH_PREFERENCE_DELIMITER, ";");
			} else {
				defaultNode.put(RedhawkIdePreferenceConstants.RH_IDE_RUNTIME_PATH_PREFERENCE, "${OSSIEHOME}");
				defaultNode.put(RedhawkIdePreferenceConstants.RH_IDE_IDL_INCLUDE_PATH_PREFERENCE,
				        "${OssieHome}/share/idl:/usr/share/idl/omniORB:/usr/share/idl/omniORB/COS");
				defaultNode.put(RedhawkIdePreferenceConstants.RH_IDE_IDL_INCLUDE_PATH_PREFERENCE_DELIMITER, ":");
			}

		}
	}

	/**
	 * Configures the {@link IdlLibrary}'s paths. They are set to reference the IDE's preference settings.
	 * 
	 * @since 3.0
	 */
	public static void initializeIdlLibraryToDefaults(final IdlLibrary library) {
		final PreferenceNodePathSet pathSet = LibraryFactory.eINSTANCE.createPreferenceNodePathSet();
		IEclipsePreferences node = new DefaultScope().getNode(RedhawkIdeActivator.PLUGIN_ID);
		pathSet.setDelimiter(node.get(RedhawkIdePreferenceConstants.RH_IDE_IDL_INCLUDE_PATH_PREFERENCE_DELIMITER,
		        RedhawkIdePreferenceConstants.RH_IDE_IDL_INCLUDE_PATH_PREFERENCE_DELIMITER));
		pathSet.setFileUri(true);
		pathSet.setKey(RedhawkIdePreferenceConstants.RH_IDE_IDL_INCLUDE_PATH_PREFERENCE);
		pathSet.setQualifier(RedhawkIdeActivator.PLUGIN_ID);
		pathSet.setReplaceEnv(true);

		library.getPaths().clear();
		library.getPaths().add(pathSet);
	}

}
