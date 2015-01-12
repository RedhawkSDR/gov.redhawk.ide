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

import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;
import mil.jpeojtrs.sca.dcd.DcdComponentPlacement;

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
		switch (notification.getEventType()) {
		case Notification.ADD:
			Object newValue = notification.getNewValue();
			if (newValue instanceof DcdComponentInstantiation) {
				final DcdComponentInstantiation object = (DcdComponentInstantiation) notification.getNewValue();
				this.modelMap.add(object);
			}
			break;
		case Notification.REMOVE:
			Object oldValue = notification.getOldValue();
			if (oldValue instanceof DcdComponentInstantiation) {
				final DcdComponentInstantiation object = (DcdComponentInstantiation) oldValue;
				this.modelMap.remove(object);
			} else if (oldValue instanceof DcdComponentPlacement) {
				final DcdComponentPlacement object = (DcdComponentPlacement) oldValue;
				for (DcdComponentInstantiation i : object.getComponentInstantiation()) {
					this.modelMap.remove(i);
				}
			}
			break;
		default:
			break;
		}
	}
}
