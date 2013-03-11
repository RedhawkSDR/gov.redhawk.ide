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
package gov.redhawk.ide.spd.internal.ui.editor;

import gov.redhawk.ide.codegen.CodegenUtil;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.WaveDevSettings;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;

/**
 * @since 2.0
 */
public class ImplementationListener extends AdapterImpl {
	private SoftPkg spd;
	private WaveDevSettings waveDev;

	public ImplementationListener(SoftPkg spd) {
		this.spd = spd;
		if (this.spd != null) {
			for (Implementation impl : this.spd.getImplementation()) {
				impl.eAdapters().add(this);
			}
		}
		this.waveDev = CodegenUtil.loadWaveDevSettings(this.spd);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void notifyChanged(final Notification msg) {
		super.notifyChanged(msg);
		if (msg.getNotifier() instanceof Implementation) {
			switch (msg.getFeatureID(Implementation.class)) {
			case SpdPackage.IMPLEMENTATION__ID:
				if (msg.getEventType() == Notification.SET) {
					ImplementationSettings settings = this.waveDev.getImplSettings().get(msg.getOldStringValue());
					this.waveDev.getImplSettings().removeKey(msg.getOldStringValue());
					this.waveDev.getImplSettings().put(msg.getNewStringValue(), settings);	
				}
				break;
			default:
				break;
			}
		} 
	}
	
	public void addImplementation(Implementation impl) {
		if (impl != null) {
			if (!impl.eAdapters().contains(this)) {
				impl.eAdapters().add(this);
			}
		}
	}
	
	public void removeImplementation(Implementation impl) {
		if (impl != null) {
			impl.eAdapters().remove(this);
		}
	}
	
	public void dispose() {
		if (this.spd != null) {
			for (Implementation impl : this.spd.getImplementation()) {
				impl.eAdapters().remove(this);
			}
		}
	}
}
