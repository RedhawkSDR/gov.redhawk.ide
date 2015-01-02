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
package gov.redhawk.ide.graphiti.dcd.internal.ui;

import gov.redhawk.ide.debug.LocalSca;
import gov.redhawk.ide.debug.LocalScaComponent;
import gov.redhawk.model.sca.ScaPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.util.EContentAdapter;

public class DcdGraphitiModelAdapter extends EContentAdapter {

	private final GraphitiDcdModelMap modelMap;

	public DcdGraphitiModelAdapter(final GraphitiDcdModelMap modelMap) {
		this.modelMap = modelMap;
	}

	// TODO: Add cases to this adapter and the dcd model map to handle when a device is released via the SCA Explorer
	
	@Override
	public void notifyChanged(final Notification notification) {
		super.notifyChanged(notification);
		if (notification.getNotifier() instanceof LocalScaComponent) {
			switch (notification.getFeatureID(LocalSca.class)) {
			case ScaPackage.SCA_COMPONENT__STARTED:
				LocalScaComponent localScaComponent = (LocalScaComponent) notification.getNotifier();
				final Boolean started = (Boolean) notification.getNewValue();
//				this.modelMap.startStopComponent(localScaComponent, started);
				break;
			default:
				break;
			}
		}
	}
}
