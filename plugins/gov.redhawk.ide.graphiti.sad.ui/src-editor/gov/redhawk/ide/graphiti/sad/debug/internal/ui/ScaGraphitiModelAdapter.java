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
package gov.redhawk.ide.graphiti.sad.debug.internal.ui;

import gov.redhawk.ide.debug.LocalSca;
import gov.redhawk.ide.debug.LocalScaComponent;
import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.debug.ScaDebugPackage;
import gov.redhawk.model.sca.IDisposable;
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
public class ScaGraphitiModelAdapter extends EContentAdapter {

	private final GraphitiModelMap modelMap;

	public ScaGraphitiModelAdapter(final GraphitiModelMap modelMap) {
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
					Object newVal = notification.getNewValue();
					if (newVal != null) {
						this.modelMap.add((LocalScaComponent) newVal);
					}
					break;
				case Notification.ADD_MANY:
					for (final Object obj : (Collection< ? >) notification.getNewValue()) {
						if (obj != null) {
							this.modelMap.add((LocalScaComponent) obj);
						}
					}
					break;
				case Notification.REMOVE:
					Object oldVal = notification.getOldValue();
					if (oldVal != null) {
						this.modelMap.remove((LocalScaComponent) oldVal);
					}
					break;
				case Notification.REMOVE_MANY:
					for (final Object obj : (Collection< ? >) notification.getOldValue()) {
						if (obj != null) {
							this.modelMap.remove((LocalScaComponent) obj);
						}
					}
					break;
				default:
					break;
				}
				break;
			default:
				break;
			}
		} else if (notification.getNotifier() instanceof LocalScaComponent) {
			switch (notification.getFeatureID(LocalSca.class)) {
			case ScaPackage.SCA_COMPONENT__STARTED:
				LocalScaComponent localScaComponent = (LocalScaComponent) notification.getNotifier();
				final Boolean started = (Boolean) notification.getNewValue();
				this.modelMap.startStopComponent(localScaComponent, started);
				break;
			default:
				break;
			}

			//enable when device diagram uses model map
//			switch (notification.getFeatureID(ScaDevice.class)) {
//			case ScaPackage.SCA_DEVICE__STARTED:
//				if (notification.getNotifier() instanceof ScaDevice) {
//					final Boolean started = (Boolean) notification.getNewValue();
//					//this.modelMap.paintComponent(componentShape, started);
//				}
//				break;
//			default:
//				break;
//			}

			switch (notification.getFeatureID(IDisposable.class)) {
			case ScaPackage.IDISPOSABLE__DISPOSED:
				if (notification.getNotifier() instanceof Notifier) {
					final Notifier notifier = (Notifier) notification.getNotifier();
					notifier.eAdapters().remove(this);
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
					Object newVal = notification.getNewValue();
					if (newVal != null) {
						this.modelMap.add((ScaConnection) newVal);
					}
					break;
				case Notification.ADD_MANY:
					for (final Object obj : (Collection< ? >) notification.getNewValue()) {
						if (obj != null) {
							this.modelMap.add((ScaConnection) obj);
						}
					}
					break;
				case Notification.REMOVE:
					Object oldVal = notification.getOldValue();
					if (oldVal != null) {
						this.modelMap.remove((ScaConnection) oldVal);
					}
					break;
				case Notification.REMOVE_MANY:
					for (final Object obj : (Collection< ? >) notification.getOldValue()) {
						if (obj != null) {
							this.modelMap.remove((ScaConnection) obj);
						}
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
	public void addAdapter(final Notifier notifier) {
		if (notifier instanceof ScaWaveform || notifier instanceof ScaComponent || notifier instanceof ScaUsesPort) {
			super.addAdapter(notifier);
		}

	}
}
