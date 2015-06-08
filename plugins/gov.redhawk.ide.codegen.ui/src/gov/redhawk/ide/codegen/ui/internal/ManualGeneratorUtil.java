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
package gov.redhawk.ide.codegen.ui.internal;

import gov.redhawk.ide.codegen.CodegenUtil;
import gov.redhawk.ide.codegen.ICodeGeneratorDescriptor;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.RedhawkCodegenActivator;
import gov.redhawk.ide.codegen.WaveDevSettings;
import gov.redhawk.ui.RedhawkUiActivator;

import java.util.List;

import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class ManualGeneratorUtil {

	private ManualGeneratorUtil() {
	}

	/**
	 * Checks if any of the specified implementations have a manual code generator. If so, an informational message is
	 * displayed to the user. The method returns true if code generation is required (i.e. there are some non-manual
	 * code generators).
	 * generation is required.
	 * @param shell
	 * @param impls
	 * @returns True if code generation is required
	 * @throws CoreException
	 */
	public static boolean checkManualGenerator(Shell shell, List<Implementation> impls) throws CoreException {
		if (impls == null || impls.isEmpty()) {
			return false;
		}
		boolean shouldGenerate = true;
		final SoftPkg softPkg = (SoftPkg) impls.get(0).eContainer();
		final WaveDevSettings waveDev = CodegenUtil.loadWaveDevSettings(softPkg);
		boolean hasManualGenerator = false;
		int manualImpls = 0;
		for (final Implementation impl : impls) {
			hasManualGenerator = isManualGenerator(impl, waveDev);
			if (hasManualGenerator) {
				manualImpls++;
			}
		}
		if (manualImpls > 0) {
			String name = softPkg.getName();
			String message = "Some implementations in " + name + " require manual code generation.\n\n"
				+ "Automatic Code Generation is only available for implementations using supported code generators.";
			MessageDialog dialog = new MessageDialog(shell, "Manual Code Generation Required", null, message, MessageDialog.INFORMATION, new String[] { "OK" },
				0);
			dialog.open();
		}
		// If all implementations require manual code generation, then do not start the generation process
		if (manualImpls == impls.size()) {
			shouldGenerate = false;
		}
		return shouldGenerate;
	}

	/**
	 * @param impl
	 * @param waveDev
	 * @return True if the implementation relies on Manual Code generation
	 * @throws CoreException
	 */
	private static boolean isManualGenerator(Implementation impl, WaveDevSettings waveDev) throws CoreException {
		if (waveDev == null) {
			waveDev = WaveDevUtil.generateWaveDev(impl.getSoftPkg());
		}
		if (waveDev == null) {
			throw new CoreException(new Status(Status.ERROR, RedhawkUiActivator.PLUGIN_ID, "GENERATE FAILED: Failed to find implementation settings in "
				+ impl.getSoftPkg().getName() + ".wavedev file", null));
		}
		final ImplementationSettings implSettings = waveDev.getImplSettings().get(impl.getId());
		if (implSettings != null) {
			ICodeGeneratorDescriptor generator = RedhawkCodegenActivator.getCodeGeneratorsRegistry().findCodegen(implSettings.getGeneratorId());
			if (generator != null && generator.getName().matches(".*Manual Generator.*")) {
				return true;
			}
		} else {
			// try to auto-generate implementation settings
			ImplementationSettings generatedImplSettings = WaveDevUtil.generateWaveDev(impl.getSoftPkg()).getImplSettings().get(impl.getId());
			if (generatedImplSettings != null) {
				ICodeGeneratorDescriptor generator = RedhawkCodegenActivator.getCodeGeneratorsRegistry().findCodegen(generatedImplSettings.getGeneratorId());
				if (generator != null && generator.getName().matches(".*Manual Generator.*")) {
					return true;
				}
			} else {
				throw new CoreException(new Status(Status.ERROR, RedhawkUiActivator.PLUGIN_ID,
					"GENERATE FAILED: Failed to find implementation settings for implementation: " + impl.getId(), null));
			}
		}
		return false;
	}

}
