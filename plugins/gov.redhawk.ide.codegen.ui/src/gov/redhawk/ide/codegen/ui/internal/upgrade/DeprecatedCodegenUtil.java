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
package gov.redhawk.ide.codegen.ui.internal.upgrade;

import gov.redhawk.ide.codegen.CodegenUtil;
import gov.redhawk.ide.codegen.ICodeGeneratorDescriptor;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.RedhawkCodegenActivator;
import gov.redhawk.ide.codegen.WaveDevSettings;
import gov.redhawk.ide.codegen.ui.IComponentProjectUpgrader;
import gov.redhawk.ide.codegen.ui.RedhawkCodegenUiActivator;
import gov.redhawk.ide.codegen.ui.internal.WaveDevUtil;
import gov.redhawk.ui.RedhawkUiActivator;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

/**
 * Utility methods for upgrading deprecated code generators.
 */
public class DeprecatedCodegenUtil {

	private DeprecatedCodegenUtil() {
	}

	/**
	 * Checks if any of the specified implementations are deprecated and prompts the user to upgrade if so.
	 * @param shell
	 * @param impls
	 * @throws CoreException
	 */
	public static void checkDeprecated(Shell shell, List<Implementation> impls) throws CoreException {
		if (impls == null || impls.isEmpty()) {
			return;
		}

		final SoftPkg softPkg = (SoftPkg) impls.get(0).eContainer();
		final WaveDevSettings waveDev = CodegenUtil.loadWaveDevSettings(softPkg);
		boolean hasDeprecated = false;
		for (final Implementation impl : impls) {
			hasDeprecated = isDeprecated(impl, waveDev);
			if (hasDeprecated) {
				break;
			}
		}
		if (hasDeprecated && shouldUpgrade(shell, softPkg.getName())) {
			upgrade(shell, softPkg, waveDev);
		}
	}

	private static boolean shouldUpgrade(Shell parent, String name) throws CoreException {
		String message = name + " uses deprecated code generators.\n\n" + "Would you like to upgrade this project?";
		MessageDialog dialog = new MessageDialog(parent, "Deprecated Generator", null, message, MessageDialog.WARNING, new String[] { "Upgrade", "Cancel" }, 1);
		switch (dialog.open()) {
		case 0: // Upgrade
			return true;
		case 1:// Cancel
		default:
			throw new OperationCanceledException();
		}
	}

	private static void upgrade(Shell parent, final SoftPkg spd, final WaveDevSettings implSettings) throws CoreException {
		ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(parent);
		try {
			progressDialog.run(true, true, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						IComponentProjectUpgrader service = RedhawkCodegenUiActivator.getDefault().getComponentProjectUpgraderService();
						if (service != null) {
							service.upgrade(monitor, spd, implSettings);
						} else {
							throw new CoreException(new Status(Status.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID, "Failed to find project upgrade service.",
								null));
						}
					} catch (CoreException e) {
						throw new InvocationTargetException(e);
					}
				}
			});
		} catch (InvocationTargetException e1) {
			if (e1.getCause() instanceof CoreException) {
				CoreException core = (CoreException) e1.getCause();
				throw core;
			} else if (e1.getCause() instanceof OperationCanceledException) {
				throw new OperationCanceledException();
			} else {
				Status status = new Status(Status.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID, "Failed to update code generator.", e1.getCause());
				throw new CoreException(status);
			}
		} catch (InterruptedException e1) {
			throw new OperationCanceledException();
		}
	}

	private static boolean isDeprecated(Implementation impl, WaveDevSettings waveDev) throws CoreException {
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
			if (generator != null) {
				return generator.isDeprecated();
			} else {
				// Can't find generator assume then deprecated
				return true;
			}
		} else {
			// try to auto-generate implementation settings
			ImplementationSettings generatedImplSettings = WaveDevUtil.generateWaveDev(impl.getSoftPkg()).getImplSettings().get(impl.getId());
			if (generatedImplSettings != null) {
				ICodeGeneratorDescriptor generator = RedhawkCodegenActivator.getCodeGeneratorsRegistry().findCodegen(generatedImplSettings.getGeneratorId());
				if (generator != null) {
					return generator.isDeprecated();
				} else {
					// Can't find generator assume then deprecated
					return true;
				}
			} else {
				throw new CoreException(new Status(Status.ERROR, RedhawkUiActivator.PLUGIN_ID,
					"GENERATE FAILED: Failed to find implementation settings for implementation: " + impl.getId(), null));
			}
		}
	}
}
