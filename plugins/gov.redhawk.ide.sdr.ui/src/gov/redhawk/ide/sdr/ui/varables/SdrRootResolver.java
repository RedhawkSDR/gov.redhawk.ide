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
package gov.redhawk.ide.sdr.ui.varables;

import gov.redhawk.ide.RedhawkIdeActivator;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.core.variables.IDynamicVariableResolver;

/**
 * This resolver provides the REDHAWK IDE preference value for the SDRROOT variable.
 * 
 * @see RedhawkIdeActivator#getRuntimePath()
 */
public class SdrRootResolver implements IDynamicVariableResolver {

	public String resolveValue(IDynamicVariable variable, String argument) throws CoreException {
		return SdrUiPlugin.getDefault().getTargetSdrPath().toOSString();
	}

}
