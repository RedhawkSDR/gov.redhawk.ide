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
package gov.redhawk.ide.graphiti.dcd.ui.properties;

import gov.redhawk.ide.graphiti.dcd.ext.DeviceShape;

import org.eclipse.graphiti.mm.pictograms.PictogramElement;

/**
 * 
 */
public class DeviceFilter extends ComponentFilter {

	@Override
	protected boolean accept(PictogramElement pictogramElement) {
		return super.accept(pictogramElement) && pictogramElement instanceof DeviceShape;
	}
}
