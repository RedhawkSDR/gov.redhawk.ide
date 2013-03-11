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

import gov.redhawk.ide.codegen.CodegenUtil;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.dcd.internal.ui.editor.ImplementationPage;

import java.util.ArrayList;
import java.util.Collection;

import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SpdPackage;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * The Class ImplementationItemProvider.
 */
public class ImplementationSectionImplementationItemProvider extends mil.jpeojtrs.sca.spd.provider.ImplementationItemProvider {

	/**
	 * The Constructor.
	 * 
	 * @param adapterFactory the adapter factory
	 * @param page the page
	 */
	public ImplementationSectionImplementationItemProvider(final AdapterFactory adapterFactory, final ImplementationPage page) {
		super(adapterFactory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText(final Object object) {
		final Implementation impl = (Implementation) object;
		final ImplementationSettings settings = CodegenUtil.getImplementationSettings(impl);
		if (settings != null) {
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
