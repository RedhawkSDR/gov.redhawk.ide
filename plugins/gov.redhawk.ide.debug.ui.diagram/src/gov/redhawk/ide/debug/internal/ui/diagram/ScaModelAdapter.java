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

import gov.redhawk.ide.debug.LocalSca;
import gov.redhawk.ide.debug.LocalScaComponent;
import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.debug.ScaDebugPackage;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaConnection;
import gov.redhawk.model.sca.ScaPackage;
import gov.redhawk.model.sca.ScaUsesPort;
import gov.redhawk.model.sca.ScaWaveform;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.util.EContentAdapter;

/**
 * 
 */
public class ScaModelAdapter extends EContentAdapter {

	private final ModelMap modelMap;

	public ScaModelAdapter(final ModelMap modelMap) {
		this.modelMap = modelMap;
	}

	@Override
	public void notifyChanged(final Notification notification) {
		super.notifyChanged(notification);
		if (notification.getNotifier() instanceof LocalScaWaveform) {
			switch (notification.getFeatureID(LocalSca.class)) {
			case ScaDebugPackage.LOCAL_SCA_WAVEFORM__COMPONENTS:
				switch (notification.getEventType()) {
				case Notification.ADD:
					this.modelMap.add((LocalScaComponent) notification.getNewValue());
					break;
				case Notification.ADD_MANY:
					for (final Object obj : (Collection< ? >) notification.getNewValue()) {
						this.modelMap.add((LocalScaComponent) obj);
					}
					break;
				case Notification.REMOVE:
					this.modelMap.remove((LocalScaComponent) notification.getOldValue());
					break;
				case Notification.REMOVE_MANY:
					for (final Object obj : (Collection< ? >) notification.getOldValue()) {
						this.modelMap.remove((LocalScaComponent) obj);
					}
					break;
				default:
					break;
				}
				break;
			default:
				break;
			}
		} else if (notification.getNotifier() instanceof ScaUsesPort) {
			switch (notification.getFeatureID(ScaUsesPort.class)) {
			case ScaPackage.SCA_USES_PORT__CONNECTIONS:
				switch (notification.getEventType()) {
				case Notification.ADD:
					this.modelMap.add((ScaConnection) notification.getNewValue());
					break;
				case Notification.ADD_MANY:
					for (final Object obj : (Collection< ? >) notification.getNewValue()) {
						this.modelMap.add((ScaConnection) obj);
					}
					break;
				case Notification.REMOVE:
					this.modelMap.remove((ScaConnection) notification.getOldValue());
					break;
				case Notification.REMOVE_MANY:
					for (final Object obj : (Collection< ? >) notification.getNewValue()) {
						this.modelMap.remove((ScaConnection) obj);
					}
					break;
				default:
					break;
				}
				break;
			default:
				break;
			}
		}
	}

	@Override
	protected void addAdapter(final Notifier notifier) {
		if (notifier instanceof ScaWaveform || notifier instanceof ScaComponent || notifier instanceof ScaUsesPort) {
			super.addAdapter(notifier);
		}

	}
}
