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

import java.util.List;
import java.util.regex.Pattern;

import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

/**
 * A databinding validator for implementation ids.
 * 
 * @since 2.0
 */
public class ImplementationIdValidator implements IValidator {

	public static final String VALID_IMPL_ID_REGEX = "[A-Za-z_:][A-Za-z0-9._\\-:]*";

	private final SoftPkg softPkg;
	private final boolean importingCode;

	public ImplementationIdValidator(final SoftPkg softPkg, boolean importingCode) {
		this.softPkg = softPkg;
		this.importingCode = importingCode;
	}

	/**
	 * {@inheritDoc}
	 */
	public IStatus validate(final Object value) {
		final String s = (String) value;
		if ((s == null) || (s.length() == 0)) {
			return ValidationStatus.error("An implementation id should be set.");
		} else if (!Pattern.matches(ImplementationIdValidator.VALID_IMPL_ID_REGEX, s)) {
			return ValidationStatus.error("Invalid character detected in implementation id.");
		} else if (this.softPkg != null) {
			final List<Implementation> implList = this.softPkg.getImplementation();
			for (final Implementation anImpl : implList) {
				final String theId = anImpl.getId();
				if (theId != null && theId.equals(s) && !importingCode) {
					return ValidationStatus.error("Implementation id must be unique.  The id " + s + " is already in use.");
				}
			}
		}
		return ValidationStatus.ok();
	}

}
