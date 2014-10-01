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
package gov.redhawk.ide.ui.tests.projectCreation.stubs;

import gov.redhawk.ide.codegen.CodegenPackage;
import gov.redhawk.ide.codegen.CodegenUtil;
import gov.redhawk.ide.codegen.ICodeGeneratorDescriptor;
import gov.redhawk.ide.codegen.ICodeGeneratorsRegistry;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.Property;
import gov.redhawk.ide.codegen.RedhawkCodegenActivator;
import mil.jpeojtrs.sca.validator.EnhancedConstraintStatus;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.ConstraintStatus;

/**
 * @since 1.2
 */
public class StubPackageNameValidator extends AbstractModelConstraint implements IValidator {

	@Override
	public IStatus validate(final Object value) {
		final String s = (String) value;
		final ICodeGeneratorsRegistry registry = RedhawkCodegenActivator.getCodeGeneratorsRegistry();
		final ICodeGeneratorDescriptor[] generators = registry.findCodegenByLanguage(CodegenUtil.JAVA);
		if (generators.length > 0) {
			final String version = generators[0].getLanguageVersion();
			return Status.OK_STATUS;
		}
		return new Status(IStatus.WARNING, "gov.redhawk.codegen.validation", "Unable to validate package name");
	}

	@Override
	public IStatus validate(final IValidationContext ctx) {
		final EObject target = ctx.getTarget();
		IStatus retVal = null;

		if (target instanceof ImplementationSettings) {
			final ImplementationSettings implSettings = (ImplementationSettings) target;

			if (implSettings.getGeneratorId().contains("java")) {
				final ICodeGeneratorsRegistry registry = RedhawkCodegenActivator.getCodeGeneratorsRegistry();
				ICodeGeneratorDescriptor generator = registry.findCodegen(implSettings.getGeneratorId());
				if (generator == null) {
					return ctx.createSuccessStatus();
				}
				final String version = registry.findCodegen(implSettings.getGeneratorId()).getLanguageVersion();
				IStatus status = null;

				for (final Property prop : implSettings.getProperties()) {
					if (StubGeneratorProperties.PROP_PACKAGE.equals(prop.getId())) {
						status = Status.OK_STATUS;
						break;
					}
				}

				if (status == null) {
					retVal = new EnhancedConstraintStatus((ConstraintStatus) ctx.createFailureStatus("Invalid Package"),
					        CodegenPackage.Literals.IMPLEMENTATION_SETTINGS__PROPERTIES);
				} else if (status.getSeverity() >= IStatus.ERROR) {
					retVal = new EnhancedConstraintStatus((ConstraintStatus) ctx.createFailureStatus(status.getMessage()),
					        CodegenPackage.Literals.IMPLEMENTATION_SETTINGS__PROPERTIES);
				}
			}
		}
		return (retVal == null) ? ctx.createSuccessStatus() : retVal; // SUPPRESS CHECKSTYLE AvoidInline
	}

}
