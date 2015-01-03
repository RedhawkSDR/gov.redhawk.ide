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
package gov.redhawk.ide.graphiti.sad.ui.diagram.patterns;

import mil.jpeojtrs.sca.spd.UsesDevice;

import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.pattern.IPattern;

public class UsesDeviceFrontEndListenerPattern extends AbstractUsesDevicePattern implements IPattern {

	public static final String NAME = "Front End Listener";

	@Override
	public boolean isMainBusinessObjectApplicable(Object mainBusinessObject) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getInnerTitle(UsesDevice usesDevice) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] create(ICreateContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOuterImageId() {
		// TODO Auto-generated method stub
		return null;
	}
}
