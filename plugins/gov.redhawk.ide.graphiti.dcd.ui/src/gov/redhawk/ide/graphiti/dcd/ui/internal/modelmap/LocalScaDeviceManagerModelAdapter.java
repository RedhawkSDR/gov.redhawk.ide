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
package gov.redhawk.ide.graphiti.dcd.ui.internal.modelmap;

import org.eclipse.emf.common.notify.Notification;

import gov.redhawk.core.graphiti.dcd.ui.modelmap.GraphitiDCDModelMap;
import gov.redhawk.core.graphiti.dcd.ui.modelmap.ScaDeviceManagerModelAdapter;
import gov.redhawk.ide.debug.LocalScaService;
import gov.redhawk.ide.debug.ScaDebugPackage;
import gov.redhawk.model.sca.ScaService;

/**
 * 
 */
public class LocalScaDeviceManagerModelAdapter extends ScaDeviceManagerModelAdapter {

	public LocalScaDeviceManagerModelAdapter(GraphitiDCDModelMap modelMap) {
		super(modelMap);
	}

	@Override
	public void notifyChanged(Notification notification) {
		super.notifyChanged(notification);
		if (notification.getNotifier() instanceof LocalScaService) {
			LocalScaService service = (LocalScaService) notification.getNotifier();
			switch (notification.getFeatureID(LocalScaService.class)) {
			case ScaDebugPackage.LOCAL_SCA_SERVICE__LAUNCH:
				if (checkServiceAttr(service)) {
					getModelMap().add(service);
				}
				break;
			default:
				break;
			}
		}
	}

	/**
	 * For LocalScaService, both the name and the launch are required before adding to the model map
	 */
	@Override
	protected boolean checkServiceAttr(ScaService service) {
		if (service instanceof LocalScaService && ((LocalScaService) service).getLaunch() == null) {
			return false;
		}
		return super.checkServiceAttr(service);

	}
}
