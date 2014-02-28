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

public class SadGraphitiModelAdapter extends EContentAdapter {

	private final GraphitiModelMap modelMap;

	public SadGraphitiModelAdapter(final GraphitiModelMap modelMap) {
		this.modelMap = modelMap;
	}

	@Override
	public void notifyChanged(final Notification notification) {
		super.notifyChanged(notification);
		Object newValue = notification.getNewValue();
		Object notifier = notification.getNotifier();
		Object oldValue = notification.getOldValue();
		switch (notification.getEventType()) {
		case Notification.ADD:
			if (newValue instanceof SadComponentInstantiation) {
				final SadComponentInstantiation object = (SadComponentInstantiation) newValue;
				this.modelMap.add(object);
			}
			// Don't listen for sad connection since is isn't complete at this point
			break;
		case Notification.ADD_MANY:
			for (Object obj : ((Collection< ? >) newValue)) {
				if (obj instanceof SadComponentInstantiation) {
					final SadComponentInstantiation object = (SadComponentInstantiation) obj;
					this.modelMap.add(object);
				}
				// Don't listen for sad connection since is isn't complete at this point
			}
			break;
		case Notification.SET:
			if (notifier instanceof SadConnectInterface) {
				final SadConnectInterface object = (SadConnectInterface) notification.getNotifier();
				if (object.getSource() == null || object.getTarget() == null || object.getId() == null) {
					return;
				}
				this.modelMap.add(object);
			}
			break;
		case Notification.REMOVE:
			if (oldValue instanceof SadComponentInstantiation) {
				final SadComponentInstantiation object = (SadComponentInstantiation) oldValue;
				this.modelMap.remove(object);
			} else if (oldValue instanceof SadComponentPlacement) {
				final SadComponentPlacement object = (SadComponentPlacement) oldValue;
				for (SadComponentInstantiation i : object.getComponentInstantiation()) {
					this.modelMap.remove(i);
				}
			} else if (oldValue instanceof SadConnectInterface) {
				final SadConnectInterface object = (SadConnectInterface) oldValue;
				this.modelMap.remove(object);
			}
			break;
		case Notification.REMOVE_MANY:
			for (Object obj : ((Collection< ? >) oldValue)) {
				if (obj instanceof SadComponentInstantiation) {
					final SadComponentInstantiation object = (SadComponentInstantiation) obj;
					this.modelMap.remove(object);
				} else if (oldValue instanceof SadComponentPlacement) {
					final SadComponentPlacement object = (SadComponentPlacement) oldValue;
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
