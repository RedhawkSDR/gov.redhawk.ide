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
package gov.redhawk.ide.spd.internal.ui.editor.provider;

import gov.redhawk.ide.sdr.LoadState;
import gov.redhawk.ide.sdr.SdrPackage;
import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.TargetSdrRoot;
import mil.jpeojtrs.sca.prf.AbstractProperty;
import mil.jpeojtrs.sca.spd.PropertyRef;
import mil.jpeojtrs.sca.spd.provider.PropertyRefItemProvider;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.edit.provider.ViewerNotification;

/**
 * 
 */
public class ImplementationDetailsSectionPropertyRefItemProvider extends PropertyRefItemProvider {

	private final Adapter loadStateListener = new AdapterImpl() {
		@Override
		public void notifyChanged(final org.eclipse.emf.common.notify.Notification msg) {
			switch (msg.getFeatureID(SdrRoot.class)) {
			case SdrPackage.SDR_ROOT__STATE:
				if (msg.getNewValue() == LoadState.LOADED) {
					fireNotifyChanged(new ViewerNotification(msg));
				}
				break;
			default:
				break;
			}
		}
	};
	private boolean added;

	public ImplementationDetailsSectionPropertyRefItemProvider(final AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	@Override
	public void dispose() {
		if (this.added) {
			TargetSdrRoot.getSdrRoot().eAdapters().remove(this.loadStateListener);
			this.added = false;
		}
		super.dispose();
	}

	public AbstractProperty getProperty(final PropertyRef propRef) {
		if (propRef == null) {
			return null;
		}
		if (TargetSdrRoot.getSdrRoot().getState() == LoadState.LOADED) {
			return TargetSdrRoot.getSdrRoot().getDevicesContainer().getProperties().get(propRef.getRefId());
		} else if (!this.added) {
			TargetSdrRoot.getSdrRoot().eAdapters().add(this.loadStateListener);
			this.added = true;
		}
		return null;
	}

	@Override
	public String getText(final Object object) {
		final String type = getString("_UI_PropertyRef_type");
		final PropertyRef propRef = (PropertyRef) object;
		final AbstractProperty property = getProperty(propRef);
		if (property != null) {
			return type + " " + property.getName() + " = " + propRef.getValue();
		} else {
			return super.getText(object);
		}
	}
}
