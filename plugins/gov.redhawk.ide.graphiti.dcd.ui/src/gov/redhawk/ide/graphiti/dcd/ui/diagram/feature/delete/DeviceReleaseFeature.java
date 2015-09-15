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
package gov.redhawk.ide.graphiti.dcd.ui.diagram.feature.delete;

import org.eclipse.graphiti.features.IFeatureProvider;

import gov.redhawk.ide.graphiti.dcd.ext.DeviceShape;
import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.ReleaseFeature;

public class DeviceReleaseFeature extends ReleaseFeature {

	public DeviceReleaseFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	protected boolean isReleaseableShape(RHContainerShape shape) {
		return shape instanceof DeviceShape;
	}

}
