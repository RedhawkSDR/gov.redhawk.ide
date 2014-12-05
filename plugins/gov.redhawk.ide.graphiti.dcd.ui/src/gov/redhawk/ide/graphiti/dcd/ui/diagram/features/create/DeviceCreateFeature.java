/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.graphiti.dcd.ui.diagram.features.create;

import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;

public class DeviceCreateFeature extends AbstractCreateFeature {

	// TODO: Device creation feature stub.  Still needs to be imlpemented
	
	private SoftPkg spd = null;
	private String implId = null;
	
	@Override
	public String getDescription() {
		// Provides the context menu Undo/Redo description
		return "Add Device to Diagram";
	}
	
	public DeviceCreateFeature(IFeatureProvider fp, String name, String description) {
		super(fp, name, description);
	}

	public DeviceCreateFeature(IFeatureProvider fp, SoftPkg spd, String implId) {
		super(fp, spd.getName(), spd.getDescription());
		this.spd = spd;
		this.implId = implId;
	}

	@Override
	public boolean canCreate(ICreateContext context) {
		return false;
	}

	@Override
	public Object[] create(ICreateContext context) {
		return null;
	}

}
