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

import gov.redhawk.ide.codegen.CodegenPackage;
import gov.redhawk.ide.codegen.CodegenUtil;
import gov.redhawk.ide.codegen.ImplementationSettings;

import java.util.ArrayList;
import java.util.Collection;

import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SpdPackage;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.provider.ViewerNotification;

/**
 * The Class ImplementationItemProvider.
 */
public class ImplementationSectionImplementationItemProvider extends
        mil.jpeojtrs.sca.spd.provider.ImplementationItemProvider {
	
	private final Resource spdResource;

	/**
	 * The Constructor.
	 * 
	 * @param adapterFactory the adapter factory
	 * @param page the page
	 * @since 2.0
	 */
	public ImplementationSectionImplementationItemProvider(final AdapterFactory adapterFactory, Resource spdResource) {
		super(adapterFactory);
		this.spdResource = spdResource;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("deprecation")
	public String getText(final Object object) {
		final Implementation impl = (Implementation) object;
		final ImplementationSettings settings = CodegenUtil.getImplementationSettings(impl);
		if (settings != null) {
			if (!settings.eAdapters().contains(this)) {
				settings.eAdapters().add(this);
			}
			final String name = settings.getName();
			if (name != null && name.length() > 0) {
				return name;
			}
		}
		final String id = impl.getId();
		if (id != null && id.length() > 0) {
			return id;
		}

		return super.getText(object);
	}
	
	@Override
	public void notifyChanged(Notification notification) {
		Object feature = notification.getNotifier();
		if (feature instanceof Implementation) {
			super.notifyChanged(notification);
		} else if (feature instanceof ImplementationSettings) {
			ImplementationSettings settings = (ImplementationSettings) feature;
			switch (notification.getFeatureID(ImplementationSettings.class)) {
			case CodegenPackage.IMPLEMENTATION_SETTINGS__NAME:
				fireNotifyChanged(new ViewerNotification(notification, spdResource.getEObject(settings.getId()), false, true));
				return;
			case CodegenPackage.IMPLEMENTATION_SETTINGS__PRIMARY:
				fireNotifyChanged(new ViewerNotification(notification, spdResource.getEObject(settings.getId()), false, true));
				return;
			default:
				break;
			}
		} else {
			super.notifyChanged(notification);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection< ? extends EStructuralFeature> getChildrenFeatures(final Object object) {
		if (this.childrenFeatures == null) {
			this.childrenFeatures = new ArrayList<EStructuralFeature>();
			this.childrenFeatures.add(SpdPackage.Literals.IMPLEMENTATION__DEPENDENCY);
			this.childrenFeatures.add(SpdPackage.Literals.IMPLEMENTATION__USES_DEVICE);
		}
		return this.childrenFeatures;
	}

}
