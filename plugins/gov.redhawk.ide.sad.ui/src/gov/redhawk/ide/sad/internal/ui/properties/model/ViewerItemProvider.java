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
package gov.redhawk.ide.sad.internal.ui.properties.model;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.ItemProvider;

public abstract class ViewerItemProvider extends ItemProvider implements Adapter {

	public ViewerItemProvider() {
		super();
	}

	public abstract EditingDomain getEditingDomain();

	@Override
	public void notifyChanged(Notification notification) {
		// TODO Auto-generated method stub
	}

	@Override
	public Notifier getTarget() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTarget(Notifier newTarget) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isAdapterForType(Object type) {
		// TODO Auto-generated method stub
		return false;
	}
}
