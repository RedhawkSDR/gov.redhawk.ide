/******************************************************************************
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package gov.redhawk.statistics.ui.internal;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

public abstract class CustomAction extends Action implements IWorkbenchAction {

	private static final String ID = "gov.redhawk.statistics.ui.customAction";

	public CustomAction() {
		setId(ID);
	}

	@Override
	public abstract void run();

	@Override
	public void dispose() {

	}

}
