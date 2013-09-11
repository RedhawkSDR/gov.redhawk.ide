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
package gov.redhawk.ide.debug.internal.ui.diagram;

import java.util.Collection;

import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadComponentPlacement;
import mil.jpeojtrs.sca.sad.SadConnectInterface;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.util.EContentAdapter;

public class SadModelAdapter extends EContentAdapter {

	private final ModelMap modelMap;

	public SadModelAdapter(final ModelMap modelMap) {
		this.modelMap = modelMap;
	}

	@Override
	public void notifyChanged(final Notification notification) {
		super.notifyChanged(notification);
		switch (notification.getEventType()) {
		case Notification.ADD:
			if (notification.getNewValue() instanceof SadComponentInstantiation) {
				final SadComponentInstantiation object = (SadComponentInstantiation) notification.getNewValue();
				this.modelMap.add(object);
			}
			// Don't listen for sad connection since is isn't complete at this point
			break;
		case Notification.ADD_MANY:
			for (Object obj : ((Collection< ? >) notification.getNewValue())) {
				if (obj instanceof SadComponentInstantiation) {
					final SadComponentInstantiation object = (SadComponentInstantiation) obj;
					this.modelMap.add(object);
				}
				// Don't listen for sad connection since is isn't complete at this point
			}
			break;
		case Notification.SET:
			if (notification.getNotifier() instanceof SadConnectInterface) {
				final SadConnectInterface object = (SadConnectInterface) notification.getNotifier();
				if (object.getSource() == null || object.getTarget() == null || object.getId() == null) {
					return;
				}
				this.modelMap.add(object);
			}
			break;
		case Notification.REMOVE:
			if (notification.getOldValue() instanceof SadComponentInstantiation) {
				final SadComponentInstantiation object = (SadComponentInstantiation) notification.getOldValue();
				this.modelMap.remove(object);
			} else if (notification.getOldValue() instanceof SadComponentPlacement) {
				final SadComponentPlacement object = (SadComponentPlacement) notification.getOldValue();
				for (SadComponentInstantiation i : object.getComponentInstantiation()) {
					this.modelMap.remove(i);
				}
			} else if (notification.getOldValue() instanceof SadConnectInterface) {
				final SadConnectInterface object = (SadConnectInterface) notification.getOldValue();
				this.modelMap.remove(object);
			}
			break;
		case Notification.REMOVE_MANY:
			for (Object obj : ((Collection< ? >) notification.getOldValue())) {
				if (obj instanceof SadComponentInstantiation) {
					final SadComponentInstantiation object = (SadComponentInstantiation) obj;
					this.modelMap.remove(object);
				} else if (notification.getOldValue() instanceof SadComponentPlacement) {
					final SadComponentPlacement object = (SadComponentPlacement) notification.getOldValue();
					for (SadComponentInstantiation i : object.getComponentInstantiation()) {
						this.modelMap.remove(i);
					}	
				} else if (obj instanceof SadConnectInterface) {
					final SadConnectInterface object = (SadConnectInterface) obj;
//					final SadConnectInterface copy = EcoreUtil.copy(object);
					this.modelMap.remove(object);
				}
			}
			break;
		default:
			break;
		}
	}

}
