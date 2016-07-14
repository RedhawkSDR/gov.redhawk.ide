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
package gov.redhawk.ide.debug;

import java.util.Collections;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreValidator;

import mil.jpeojtrs.sca.partitioning.ComponentFile;
import mil.jpeojtrs.sca.prf.Properties;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.spd.SoftPkg;

/**
 * @since 4.0
 */
public final class SadLauncherUtil {

	private SadLauncherUtil() {
	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> getImplementationMap(final ILaunchConfiguration config) throws CoreException {
		if (config == null) {
			return Collections.emptyMap();
		}
		return config.getAttribute(ScaDebugLaunchConstants.ATT_LW_IMPLS, (Map<String, String>) Collections.EMPTY_MAP);
	}

	/**
	 * @throws CoreException
	 * @since 8.3
	 */
	public static void validateAllXML(SoftwareAssembly sad) throws CoreException {
		// Check SAD - throw immediately if there are errors
		if (!validateNoXMLErrors(sad)) {
			Status status = new Status(IStatus.ERROR, ScaDebugPlugin.ID, "There are 1 or more errors in the SAD file. Open the SAD file from a project to see the errors and correct them.");
			throw new CoreException(status);
		}

		// Check each referenced component
		MultiStatus multiStatus = new MultiStatus(ScaDebugPlugin.ID, 0, "Some XML file(s) have errors", null);
		for (ComponentFile componentFile : sad.getComponentFiles().getComponentFile()) {
			// Check SPD - no further checks for this component if there are errors
			SoftPkg spd = componentFile.getSoftPkg();
			if (spd == null) {
				String msg = String.format("Missing component SPD for %s", componentFile.getId());
				multiStatus.add(new Status(IStatus.ERROR, ScaDebugPlugin.ID, msg));
				continue;
			} else if (!validateNoXMLErrors(spd)) {
				String msg = String.format("Errors in SPD for component %s (%s)", spd.getName(), componentFile.getLocalFile().getName());
				multiStatus.add(new Status(IStatus.ERROR, ScaDebugPlugin.ID, msg));
				continue;
			}

			// Check PRF if applicable
			if (spd.getPropertyFile() != null) {
				Properties prf = spd.getPropertyFile().getProperties();
				String prfFilePath = spd.getPropertyFile().getLocalFile().getName();
				if (prf == null) {
					String msg = String.format("Missing PRF for component %s (%s)", spd.getName(), prfFilePath);
					multiStatus.add(new Status(IStatus.ERROR, ScaDebugPlugin.ID, msg));
				} else if (!validateNoXMLErrors(prf)) {
					String msg = String.format("Errors in PRF for component  %s (%s)", spd.getName(), prfFilePath);
					multiStatus.add(new Status(IStatus.ERROR, ScaDebugPlugin.ID, msg));
				}
			}

			// Check SCD if applicable
			if (spd.getDescriptor() != null) {
				SoftwareComponent scd = spd.getDescriptor().getComponent();
				String scdFilePath = spd.getDescriptor().getLocalfile().getName();
				if (scd == null) {
					String msg = String.format("Missing SCD for component %s (%s)", spd.getName(), scdFilePath);
					multiStatus.add(new Status(IStatus.ERROR, ScaDebugPlugin.ID, msg));
					continue;
				} else if (!validateNoXMLErrors(scd)) {
					String msg = String.format("Errors in SCD for component %s (%s)", spd.getName(), scdFilePath);
					multiStatus.add(new Status(IStatus.ERROR, ScaDebugPlugin.ID, msg));
				}
			}
		}

		if (!multiStatus.isOK()) {
			throw new CoreException(multiStatus);
		}
	}

	private static boolean validateNoXMLErrors(EObject object) {
		TreeIterator<EObject> allContents = object.eResource().getAllContents();
		boolean modelIsValid = true;
		while (allContents.hasNext()) {
			boolean validatorResult = EcoreValidator.INSTANCE.validate(allContents.next(), null, null);
			modelIsValid = modelIsValid && validatorResult;
		}
		return modelIsValid;
	}
}
