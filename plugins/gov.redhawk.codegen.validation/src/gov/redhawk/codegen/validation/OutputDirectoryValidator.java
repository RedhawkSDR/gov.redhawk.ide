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
package gov.redhawk.codegen.validation;

import gov.redhawk.ide.codegen.CodegenPackage;
import gov.redhawk.ide.codegen.CodegenUtil;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.WaveDevSettings;

import java.util.List;
import java.util.regex.Pattern;

import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.validator.EnhancedConstraintStatus;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.ConstraintStatus;

/**
 * @since 1.2
 */
public class OutputDirectoryValidator extends AbstractModelConstraint implements IValidator {

	public static final String ID = "gov.redhawk.validation.constraint.codegen.OutputDirectory";
	public static final String VALID_OUTPUTDIR_REGEX = "((/)|[A-Za-z0-9_])[A-Za-z0-9_/]+";
	private static final String NO_DIR = "An output directory must be provided";
	private static final String TRAILING_SLASH = "Output directory must not end with a /";
	private static final String BAD_DIR = "Invalid output directory";

	private SoftPkg softPkg;

	public OutputDirectoryValidator() {
		this.softPkg = null;
	}

	/**
	 * @since 2.0
	 */
	public OutputDirectoryValidator(SoftPkg softPkg) {
		this.softPkg = softPkg;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Validates the following conditions:
	 * 1. Output Directory, when trimmed of leading/trailing whitespace, cannot be "".
	 * 2. Cannot contain leading or trailing whitespace
	 * 3. May start with '/' or [A-Za-z]
	 * 4. Cannot contain '.' or '..'
	 * 5. Can only contain [A-Za-z0-9_/]
	 */
	public IStatus validate(final Object value) {
		final String s = (String) value;
		String result = validateDir(s);
		if (result != null) {
			return ValidationStatus.error(result);
		}
		result = validateUnique(s);
		if (result != null) {
			return ValidationStatus.error(result);
		}
		return ValidationStatus.ok();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Validates the following conditions:
	 * 1. Output Directory, when trimmed of leading/trailing whitespace, cannot be "".
	 * 2. Cannot contain leading or trailing whitespace
	 * 3. May start with '/' or [A-Za-z]
	 * 4. Cannot contain '.' or '..'
	 * 5. Can only contain [A-Za-z0-9_/]
	 */
	@Override
	public IStatus validate(final IValidationContext ctx) {
		final EObject target = ctx.getTarget();
		if (target instanceof ImplementationSettings) {
			final ImplementationSettings implSettings = (ImplementationSettings) target;
			final String dir = implSettings.getOutputDir();
			String result = null;
			// If it is a manual template don't worry about output directory
			if (implSettings.getTemplate() != null && !(implSettings.getTemplate().toUpperCase().contains("MANUAL"))) {
				result = validateDir(dir);
			}
			if (result != null) {
				return new EnhancedConstraintStatus((ConstraintStatus) ctx.createFailureStatus(result),
					CodegenPackage.Literals.IMPLEMENTATION_SETTINGS__OUTPUT_DIR);
			}
			result = validateUnique(dir);
			if (result != null) {
				return new EnhancedConstraintStatus((ConstraintStatus) ctx.createFailureStatus(result),
					CodegenPackage.Literals.IMPLEMENTATION_SETTINGS__OUTPUT_DIR);
			}
		}
		return ctx.createSuccessStatus();
	}

	/**
	 * 
	 * @param dirname
	 * @return error reason, null on success
	 */
	protected String validateDir(final String dir) {
		if ((dir == null) || (dir.trim().length() == 0)) {
			return OutputDirectoryValidator.NO_DIR;
		} else if ((dir.length() > 1) && dir.endsWith("/")) {
			return OutputDirectoryValidator.TRAILING_SLASH;
		} else if (!Pattern.matches(OutputDirectoryValidator.VALID_OUTPUTDIR_REGEX, dir)) {
			return OutputDirectoryValidator.BAD_DIR;
		}
		return null;
	}

	/**
	 * @since 2.0
	 */
	protected String validateUnique(final String dir) {
		if (this.softPkg != null) {
			final List<Implementation> implList = this.softPkg.getImplementation();
			final WaveDevSettings waveDevSettings = CodegenUtil.loadWaveDevSettings(this.softPkg);
			if (waveDevSettings != null) {
				for (final Implementation anImpl : implList) {
					final ImplementationSettings settings = waveDevSettings.getImplSettings().get(anImpl.getId());
					if (settings != null) {
						final String theDir = settings.getOutputDir();
						if (theDir != null && theDir.equals(dir)) {
							return "Output directories should be unique.  The directory " + dir + " is already in use.";
						}
					}
				}
			}
		}
		return null;
	}

}
