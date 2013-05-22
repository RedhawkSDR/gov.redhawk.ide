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
package gov.redhawk.ide.debug.impl.commands;

import gov.redhawk.ide.debug.ScaDebugFactory;
import gov.redhawk.model.sca.ScaDeviceManager;
import gov.redhawk.model.sca.ScaService;
import gov.redhawk.model.sca.commands.MergeServicesCommand;

import java.util.Map;

import CF.DeviceManagerPackage.ServiceType;

public class LocalMergeServicesCommand extends MergeServicesCommand {

	public LocalMergeServicesCommand(ScaDeviceManager provider, Map<String, ServiceType> newServices) {
	    super(provider, newServices);
    }

	
	@Override
	protected ScaService createScaService() {
	    return ScaDebugFactory.eINSTANCE.createLocalScaService();
	}
}
