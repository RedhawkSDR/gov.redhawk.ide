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
package gov.redhawk.ide.dcd.internal.ui.editor.provider;

import java.util.ArrayList;
import java.util.Collection;

import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.spd.provider.ImplementationItemProvider;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.ViewerNotification;

/**
 * 
 */
public class ImplementationDetailsSectionImplementationItemProvider extends ImplementationItemProvider {
	/**
	 * The Constructor.
	 * 
	 * @param adapterFactory the adapter factory
	 */
	public ImplementationDetailsSectionImplementationItemProvider(final AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection< ? extends EStructuralFeature> getChildrenFeatures(final Object object) {
		if (this.childrenFeatures == null) {
			this.childrenFeatures = new ArrayList<EStructuralFeature>();
			this.childrenFeatures.add(SpdPackage.Literals.IMPLEMENTATION__OS);
			this.childrenFeatures.add(SpdPackage.Literals.IMPLEMENTATION__PROCESSOR);
			this.childrenFeatures.add(SpdPackage.Literals.IMPLEMENTATION__DEPENDENCY);
		}
		return this.childrenFeatures;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void notifyChanged(final Notification notification) {
		updateChildren(notification);

		switch (notification.getFeatureID(Implementation.class)) {
		case SpdPackage.IMPLEMENTATION__DESCRIPTION:
		case SpdPackage.IMPLEMENTATION__PROPERTY_FILE:
		case SpdPackage.IMPLEMENTATION__CODE:
		case SpdPackage.IMPLEMENTATION__COMPILER:
		case SpdPackage.IMPLEMENTATION__PROGRAMMING_LANGUAGE:
		case SpdPackage.IMPLEMENTATION__HUMAN_LANGUAGE:
		case SpdPackage.IMPLEMENTATION__RUNTIME:
		case SpdPackage.IMPLEMENTATION__AEP_COMPLIANCE:
		case SpdPackage.IMPLEMENTATION__ID:
			fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
			return;
		case SpdPackage.IMPLEMENTATION__OS:
		case SpdPackage.IMPLEMENTATION__PROCESSOR:
		case SpdPackage.IMPLEMENTATION__DEPENDENCY:
		case SpdPackage.IMPLEMENTATION__USES_DEVICE:
			fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
			return;
		default:
			break;
		}
		super.notifyChanged(notification);
	}
}
