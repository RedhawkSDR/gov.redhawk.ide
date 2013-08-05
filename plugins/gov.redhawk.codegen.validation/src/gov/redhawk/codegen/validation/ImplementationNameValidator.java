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

import gov.redhawk.ide.codegen.CodegenUtil;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.WaveDevSettings;

import java.util.List;
import java.util.regex.Pattern;

import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

/**
 * A databinding validator for implementation names.
 * 
 * @since 1.1
 */
public class ImplementationNameValidator implements IValidator {

	public static final String VALID_IMPL_NAME_REGEX = "[A-Za-z][A-Za-z0-9_]*";

	private final SoftPkg softPkg;

	public ImplementationNameValidator(final SoftPkg softPkg) {
		this.softPkg = softPkg;
	}

	/**
	 * {@inheritDoc}
	 */
	public IStatus validate(final Object value) {
		final String s = (String) value;
		if ((s == null) || (s.length() == 0)) {
			return ValidationStatus.error("An implementation name should be set.");
		} else if (!Pattern.matches(ImplementationNameValidator.VALID_IMPL_NAME_REGEX, s)) {
			return ValidationStatus.error("Invalid character detected in implementation name.");
		} else if (this.softPkg != null) {
			final List<Implementation> implList = this.softPkg.getImplementation();
			final WaveDevSettings waveDevSettings = CodegenUtil.loadWaveDevSettings(this.softPkg);
			if (waveDevSettings != null) {
				for (final Implementation anImpl : implList) {
					final ImplementationSettings settings = waveDevSettings.getImplSettings().get(anImpl.getId());
					if (settings != null) {
						@SuppressWarnings("deprecation")
						final String theName = settings.getName();
						if (theName != null && theName.equals(s)) {
							return ValidationStatus.warning("Implementation names should be unique.  The name " + s + " is already in use.");
						}
					}
				}
			}
		}
		return ValidationStatus.ok();
	}

}
