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
package gov.redhawk.ide.debug.internal;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;

public interface IComponentLaunch {

	/**
	 * Sends terminate event notification to allow component to be cleaned up in the ScaModel
	 */
	public void terminateContainedComponent();

	public void setParent(IProcess parentProcess);

	/**
	 * Associates contained components with their component host. </br>
	 * Setting this implicitly marks this component as a shared-address component.
	 * @param parentLaunch
	 */
	public void setParent(ILaunch parentLaunch);

}
