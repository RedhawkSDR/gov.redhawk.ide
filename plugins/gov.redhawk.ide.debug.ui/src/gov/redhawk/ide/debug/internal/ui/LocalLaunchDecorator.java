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
package gov.redhawk.ide.debug.internal.ui;

import gov.redhawk.ide.debug.LocalLaunch;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;

/**
 * 
 */
public class LocalLaunchDecorator extends LabelProvider implements ILightweightLabelDecorator {

	/**
	 * {@inheritDoc}
	 */
	public void decorate(final Object element, final IDecoration decoration) {
		if (element instanceof LocalLaunch) {
			final LocalLaunch ll = (LocalLaunch) element;
			final ILaunch launch = ll.getLaunch();
			if (launch != null && ILaunchManager.DEBUG_MODE.equals(launch.getLaunchMode())) {
				decoration.addSuffix(" < DEBUGGING > ");
			}
		}

	}

}
