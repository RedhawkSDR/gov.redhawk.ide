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
package gov.redhawk.ide.sad.ui.providers;

import gov.redhawk.ide.sad.ui.edit.policies.SadDNDEditPolicy;
import gov.redhawk.sca.sad.diagram.providers.SadEditPolicyProvider;

import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.EditPolicyRoles;

/**
 * @since 4.0
 * 
 */
public class SadIdeEditPolicyProvider extends SadEditPolicyProvider {

	/**
	 * @since 1.2
	 */
	@Override
	protected void setupHostCollocationCompartmentEditPart(final EditPart editPart) {
		super.setupHostCollocationCompartmentEditPart(editPart);
		editPart.installEditPolicy(EditPolicyRoles.DRAG_DROP_ROLE, new SadDNDEditPolicy());
	}

	/**
	 * @since 1.2
	 */
	@Override
	protected void setupSoftwareAssemblyEditPart(final EditPart editPart) {
		super.setupSoftwareAssemblyEditPart(editPart);
		editPart.installEditPolicy(EditPolicyRoles.DRAG_DROP_ROLE, new SadDNDEditPolicy());
	}

}
