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

import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadConnectInterface;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.ecore.util.EcoreUtil;

public class SadModelAdapter extends EContentAdapter {

	private final ModelMap modelMap;

	public SadModelAdapter(final ModelMap modelMap) {
		this.modelMap = modelMap;
	}

	@Override
	public void notifyChanged(final Notification notification) {
		super.notifyChanged(notification);

		if (notification.getEventType() == Notification.ADD && notification.getNewValue() instanceof EObject) {
			if (notification.getNewValue() instanceof SadComponentInstantiation) {
				final SadComponentInstantiation object = (SadComponentInstantiation) notification.getNewValue();
				this.modelMap.add(object);
			}
		} else if (notification.getEventType() == Notification.REMOVE && notification.getOldValue() instanceof EObject) {
			if (notification.getOldValue() instanceof SadComponentInstantiation) {
				final SadComponentInstantiation object = (SadComponentInstantiation) notification.getOldValue();
				this.modelMap.remove(object);
			} else if (notification.getOldValue() instanceof SadConnectInterface) {
				final SadConnectInterface object = (SadConnectInterface) notification.getOldValue();
				final SadConnectInterface copy = EcoreUtil.copy(object);
				this.modelMap.remove(copy);
			}
		} else if (notification.getEventType() == Notification.SET && notification.getNotifier() instanceof EObject) {
			if (notification.getNotifier() instanceof SadConnectInterface) {
				final SadConnectInterface object = (SadConnectInterface) notification.getNotifier();
				if (object.getSource() == null || object.getTarget() == null || object.getId() == null) {
					return;
				}
				this.modelMap.add(object);
			}
		}
	}

}
