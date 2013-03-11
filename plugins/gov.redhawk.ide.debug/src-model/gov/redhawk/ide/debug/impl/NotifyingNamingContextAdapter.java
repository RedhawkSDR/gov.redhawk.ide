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
package gov.redhawk.ide.debug.impl;

import gov.redhawk.ide.debug.NotifyingNamingContext;
import gov.redhawk.ide.debug.ScaDebugPackage;

import java.util.Collection;
import java.util.Map.Entry;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.jacorb.naming.Name;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextPackage.InvalidName;

/**
 * 
 */
public abstract class NotifyingNamingContextAdapter extends EContentAdapter {

	@Override
	protected void addAdapter(final Notifier notifier) {
		if (notifier instanceof NotifyingNamingContext) {
			super.addAdapter(notifier);
		}
	}

	@Override
	public void notifyChanged(final Notification msg) {
		super.notifyChanged(msg);
		switch (msg.getFeatureID(NotifyingNamingContext.class)) {
		case ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__OBJECT_MAP:
			// The feature ID may not always filter out all non-relevant notifications.
			// In the case of the local waveform additions, Local_Waveform & NOTIFYING_NAMING_CONTEXT__OBJECT_MAP both equal 1 
			if (msg.getNotifier() instanceof NotifyingNamingContext) {
				switch (msg.getEventType()) {
				case Notification.ADD:
					addObject((Entry< ? , ? >) msg.getNewValue(), msg);
					break;
				case Notification.ADD_MANY:
					for (final Object obj : (Collection< ? >) msg.getNewValue()) {
						addObject((Entry< ? , ? >) obj, msg);
					}
					break;
				case Notification.REMOVE:
					removeObject((Entry< ? , ? >) msg.getOldValue(), msg);
					break;
				case Notification.REMOVE_MANY:
					for (final Object obj : (Collection< ? >) msg.getOldValue()) {
						removeObject((Entry< ? , ? >) obj, msg);
					}
					break;
				default:
					break;
				}
			}
			break;
		default:
			break;
		}
	}

	private void removeObject(final Entry< ? , ? > entry, final Notification msg) {
		final NotifyingNamingContext sourceContext = (NotifyingNamingContext) msg.getNotifier();
		final String fullName = sourceContext.getFullName();
		final Name name = (Name) entry.getKey();
		final org.omg.CORBA.Object obj = (org.omg.CORBA.Object) entry.getValue();
		try {
			final String location = fullName + "/" + Name.toString(name.components());
			removeObject(Name.toName(location), obj, msg);
		} catch (final InvalidName e) {
			throw new IllegalStateException(e);
		}
	}

	private void addObject(final Entry< ? , ? > entry, final Notification msg) {
		final NotifyingNamingContext sourceContext = (NotifyingNamingContext) msg.getNotifier();
		final String fullName = sourceContext.getFullName();
		final Name name = (Name) entry.getKey();
		final org.omg.CORBA.Object obj = (org.omg.CORBA.Object) entry.getValue();
		try {
			final String location = fullName + "/" + Name.toString(name.components());
			addObject(Name.toName(location), obj, msg);
		} catch (final InvalidName e) {
			throw new IllegalStateException(e);
		}

	}

	protected abstract void addObject(NameComponent[] location, org.omg.CORBA.Object obj, Notification msg);

	protected abstract void removeObject(NameComponent[] location, org.omg.CORBA.Object obj, Notification msg);

}
