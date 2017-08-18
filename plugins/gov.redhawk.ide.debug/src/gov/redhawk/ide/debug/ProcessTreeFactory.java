/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.debug;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

/**
 * @since 10.0
 */
public class ProcessTreeFactory {

	private ProcessTreeFactory() {
	}

	public static IProcessTree createProcessTree() {
		CoreException exception = null;
		IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor("gov.redhawk.ide.debug.processTree");
		for (IConfigurationElement element : elements) {
			try {
				return (IProcessTree) element.createExecutableExtension("class");
			} catch (CoreException e) {
				exception = e;
			}
		}
		ScaDebugPlugin.logError("Unable to create an IProcessTree", exception);
		return null;
	}

}
