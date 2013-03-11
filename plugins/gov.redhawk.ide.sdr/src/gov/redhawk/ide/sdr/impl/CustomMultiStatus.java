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
package gov.redhawk.ide.sdr.impl;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;

/**
 * @since 8.0
 * 
 */
public class CustomMultiStatus extends MultiStatus {

	public CustomMultiStatus(String pluginId, int code, IStatus[] newChildren, String message, Throwable exception) {
	    super(pluginId, code, newChildren, message, exception);
	    // TODO Auto-generated constructor stub
    }

	public CustomMultiStatus(String pluginId, int code, String message, Throwable exception) {
	    super(pluginId, code, message, exception);
	    // TODO Auto-generated constructor stub
    }

	@Override
    public void add(IStatus status) {
	    if (!status.isOK()) {
	    	super.add(status);
	    }
    }


}
