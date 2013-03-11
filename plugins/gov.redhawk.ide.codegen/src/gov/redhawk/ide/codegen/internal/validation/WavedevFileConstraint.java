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
package gov.redhawk.ide.codegen.internal.validation;

import gov.redhawk.ide.codegen.CodegenPackage;
import gov.redhawk.ide.codegen.CodegenUtil;
import gov.redhawk.ide.codegen.WaveDevSettings;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.validator.EnhancedConstraintStatus;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.ConstraintStatus;

/**
 * @since 7.0
 */
public class WavedevFileConstraint extends AbstractModelConstraint {
	public static final String WAVEDEV_ID = "gov.redhawk.ide.codegen.wavedevfile_constraint";
	public static final String IMPL_SETTINGS_ID = "gov.redhawk.ide.codegen.implsettings_constraint";

	@Override
	public IStatus validate(final IValidationContext ctx) {
		final EObject target = ctx.getTarget();
		IStatus retVal = null;

		if (ctx.getCurrentConstraintId().equals(WavedevFileConstraint.WAVEDEV_ID)) {
			if (target instanceof Implementation) {
				final Implementation impl = (Implementation) target;
				final SoftPkg softPkg = (SoftPkg) impl.eContainer();
				if (softPkg != null) {
					final WaveDevSettings waveDev = CodegenUtil.loadWaveDevSettings(softPkg);
					if (waveDev == null) {
						retVal = new EnhancedConstraintStatus((ConstraintStatus) ctx.createFailureStatus(""),
						        CodegenPackage.Literals.WAVE_DEV_SETTINGS__IMPL_SETTINGS) {

							@Override
							protected void setSeverity(final int severity) {
								super.setSeverity(IStatus.WARNING);
							}

							@Override
							protected void setMessage(final String message) {
								super.setMessage("Unable to find code generator settings; code generation is disabled.");
							}
						};
					}
				}
			}
		} else if (ctx.getCurrentConstraintId().equals(WavedevFileConstraint.IMPL_SETTINGS_ID)) {
			if (target instanceof Implementation) {
				final Implementation impl = (Implementation) target;
				final SoftPkg softPkg = (SoftPkg) impl.eContainer();
				if (softPkg != null) {
					final WaveDevSettings waveDev = CodegenUtil.loadWaveDevSettings(softPkg);
					if (waveDev != null) {
						if (!waveDev.getImplSettings().keySet().contains(impl.getId())) {
							retVal = new EnhancedConstraintStatus((ConstraintStatus) ctx.createFailureStatus(impl.getId()),
							        CodegenPackage.Literals.WAVE_DEV_SETTINGS__IMPL_SETTINGS) {
								@Override
								protected void setSeverity(final int severity) {
									super.setSeverity(IStatus.WARNING);
								}

								@Override
								protected void setMessage(final String message) {
									super.setMessage("Unable to find code generator settings; code generation is disabled.");
								}
							};
						}
					}
				}
			}
		}

		if (retVal != null) {
			return retVal;
		} else {
			return ctx.createSuccessStatus();
		}
	}
}
