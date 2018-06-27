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

import mil.jpeojtrs.sca.spd.Dependency;
import mil.jpeojtrs.sca.spd.provider.DependencyItemProvider;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.provider.IItemLabelProvider;

public class ImplementationDetailsSectionDependencyItemProvider extends DependencyItemProvider {

	public ImplementationDetailsSectionDependencyItemProvider(final AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	@Override
	public String getText(final Object object) {
		final Dependency dependency = (Dependency) object;
		if (!dependency.getProperties().isEmpty()) {
			Object value = dependency.getProperties().getValue(0);
			final IItemLabelProvider lp = (IItemLabelProvider) getRootAdapterFactory().adapt(value, IItemLabelProvider.class);
			return dependency.getType() + " " + lp.getText(value);
		} else if (dependency.getSoftPkgRef() != null) {
			final IItemLabelProvider lp = (IItemLabelProvider) getRootAdapterFactory().adapt(dependency.getSoftPkgRef(), IItemLabelProvider.class);
			return lp.getText(dependency.getSoftPkgRef());
		} else {
			return dependency.getType();
		}
	}

	@Override
	public Object getImage(final Object object) {
		final Dependency dependency = (Dependency) object;
		if (!dependency.getProperties().isEmpty()) {
			Object value = dependency.getProperties().getValue(0);
			final IItemLabelProvider lp = (IItemLabelProvider) getRootAdapterFactory().adapt(value, IItemLabelProvider.class);
			return lp.getImage(value);
		} else if (dependency.getSoftPkgRef() != null) {
			final IItemLabelProvider lp = (IItemLabelProvider) getRootAdapterFactory().adapt(dependency.getSoftPkgRef(), IItemLabelProvider.class);
			return lp.getImage(dependency.getSoftPkgRef());
		}
		return super.getImage(object);
	}

}
