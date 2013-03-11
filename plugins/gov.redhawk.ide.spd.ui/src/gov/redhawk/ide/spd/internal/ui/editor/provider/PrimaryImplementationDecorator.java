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

import gov.redhawk.ide.codegen.CodegenUtil;
import gov.redhawk.ide.codegen.ImplementationSettings;
import mil.jpeojtrs.sca.spd.Implementation;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.provider.IWrapperItemProvider;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;

/**
 * 
 */
public class PrimaryImplementationDecorator extends LabelProvider implements ILightweightLabelDecorator {

	/**
	 * {@inheritDoc}
	 */
	public void decorate(final Object element, final IDecoration decoration) {
		if (!(element instanceof EObject || element instanceof FeatureMap.Entry || element instanceof IWrapperItemProvider)) {
			return;
		}

		EObject object = null;
		if (element instanceof EObject) {
			object = (EObject) element;
		} else {
			final Object unwrapped = AdapterFactoryEditingDomain.unwrap(element);
			if (unwrapped instanceof EObject) {
				object = (EObject) unwrapped;
			}
		}

		if (object instanceof Implementation) {
			final Implementation impl = (Implementation) object;
			final ImplementationSettings settings = CodegenUtil.getImplementationSettings(impl);
			if ((settings != null) && settings.isPrimary()) {
				decoration.addSuffix(" *");
			}
		}
	}

}
