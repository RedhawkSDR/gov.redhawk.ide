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
package gov.redhawk.codegen.internal.validation;

import gov.redhawk.ide.codegen.CodegenPackage;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.WaveDevSettings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mil.jpeojtrs.sca.validator.EnhancedConstraintStatus;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.ConstraintStatus;

public class UniqueImplementationNameConstraint extends AbstractModelConstraint {

	public UniqueImplementationNameConstraint() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IStatus validate(IValidationContext ctx) {
		final EObject target = ctx.getTarget();
		IStatus retVal = null;
		if (target instanceof ImplementationSettings) {
			ImplementationSettings implSettings = (ImplementationSettings) target;
			WaveDevSettings settings = (WaveDevSettings) implSettings.eResource().getEObject("/");
			Collection<ImplementationSettings> implSettingsColl = settings.getImplSettings().values();
			List<ImplementationSettings> myList = new ArrayList<ImplementationSettings>();
			myList.addAll(implSettingsColl);
			Set<String> nameSet = new HashSet<String>();
			for (ImplementationSettings i : myList) {
				String name = i.getName();
				if (nameSet.contains(name)) {
					retVal = new EnhancedConstraintStatus((ConstraintStatus) ctx.createFailureStatus(name),
					        CodegenPackage.Literals.IMPLEMENTATION_SETTINGS__NAME);
					break;
				} else if (name != null) {
					nameSet.add(name);
				}
			}
		}
		if (retVal == null) {
			return ctx.createSuccessStatus();
		} else {
			return retVal;
		}
	}

}
