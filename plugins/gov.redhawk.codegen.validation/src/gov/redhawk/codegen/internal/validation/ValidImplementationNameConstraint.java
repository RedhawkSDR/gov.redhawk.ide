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

import gov.redhawk.codegen.validation.ImplementationNameValidator;
import gov.redhawk.ide.codegen.CodegenPackage;
import gov.redhawk.ide.codegen.ImplementationSettings;

import java.util.regex.Pattern;

import mil.jpeojtrs.sca.validator.EnhancedConstraintStatus;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.ConstraintStatus;

public class ValidImplementationNameConstraint extends AbstractModelConstraint {

	public static final String ID = "gov.redhawk.validation.constraint.codegen.validImplementationName";

	public ValidImplementationNameConstraint() {
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("deprecation")
	@Override
	public IStatus validate(IValidationContext ctx) {
		final EObject target = ctx.getTarget();
		IStatus retVal = null;
		if (target instanceof ImplementationSettings) {
			ImplementationSettings implSettings = (ImplementationSettings) target;
			String name = implSettings.getName();
			if (name == null || name.length() == 0) {
				retVal = new EnhancedConstraintStatus((ConstraintStatus) ctx.createFailureStatus(name), CodegenPackage.Literals.IMPLEMENTATION_SETTINGS__NAME);
			} else if (implSettings.getName() != null) {
				if (Pattern.matches(ImplementationNameValidator.VALID_IMPL_NAME_REGEX, name)) {
					retVal = ctx.createSuccessStatus();
				} else {
					retVal = new EnhancedConstraintStatus((ConstraintStatus) ctx.createFailureStatus(name),
					        CodegenPackage.Literals.IMPLEMENTATION_SETTINGS__NAME);
				}
			}
		}
		return (retVal == null) ? ctx.createSuccessStatus() : retVal; //SUPPRESS CHECKSTYLE AvoidInLine
	}
}
