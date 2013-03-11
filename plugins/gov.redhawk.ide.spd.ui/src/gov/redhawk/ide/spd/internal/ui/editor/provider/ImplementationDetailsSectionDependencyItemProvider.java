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

/**
 * The Class ImplementationDetailsSectionOsItemProvider.
 */
public class ImplementationDetailsSectionDependencyItemProvider extends DependencyItemProvider {

	/**
	 * The Constructor.
	 * 
	 * @param adapterFactory the adapter factory
	 */
	public ImplementationDetailsSectionDependencyItemProvider(final AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText(final Object object) {
		final Dependency dependency = (Dependency) object;
		String subLabel = "";
		if (dependency.getPropertyRef() != null) {
			final IItemLabelProvider lp = (IItemLabelProvider) getRootAdapterFactory().adapt(dependency.getPropertyRef(), IItemLabelProvider.class);
			subLabel = lp.getText(dependency.getPropertyRef());
		} else if (dependency.getSoftPkgRef() != null) {
			final IItemLabelProvider lp = (IItemLabelProvider) getRootAdapterFactory().adapt(dependency.getSoftPkgRef(), IItemLabelProvider.class);
			subLabel = lp.getText(dependency.getSoftPkgRef());
		}

		return dependency.getType() + " " + subLabel;
	}

	@Override
	public Object getImage(final Object object) {
		final Dependency dependency = (Dependency) object;
		if (dependency.getPropertyRef() != null) {
			final IItemLabelProvider lp = (IItemLabelProvider) getRootAdapterFactory().adapt(dependency.getPropertyRef(), IItemLabelProvider.class);
			return lp.getImage(dependency.getPropertyRef());
		} else if (dependency.getSoftPkgRef() != null) {
			final IItemLabelProvider lp = (IItemLabelProvider) getRootAdapterFactory().adapt(dependency.getSoftPkgRef(), IItemLabelProvider.class);
			return lp.getImage(dependency.getSoftPkgRef());
		}
		return super.getImage(object);
	}

}
