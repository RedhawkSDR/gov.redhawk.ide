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
package gov.redhawk.ide.dcd.ui.providers;

import gov.redhawk.ide.dcd.ui.edit.policies.DcdDNDEditPolicy;
import gov.redhawk.sca.dcd.diagram.providers.DcdEditPolicyProvider;

import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles;

/**
 * 
 */
public class DcdIdeEditPolicyProvider extends DcdEditPolicyProvider {

	@Override
	protected void setupDeviceConfigurationEditPartEditPart(final EditPart editPart) {
		super.setupDeviceConfigurationEditPartEditPart(editPart);
		editPart.installEditPolicy(EditPolicyRoles.DRAG_DROP_ROLE, new DcdDNDEditPolicy());
	}

}
