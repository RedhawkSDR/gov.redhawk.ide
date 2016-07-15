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
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
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
	 * Ensure there aren't obvious errors with the XML or dependent XML (SPDs, etc.) that will prevent a launch.
	 * @since 8.3
	 */
	public static IStatus validateAllXML(SoftwareAssembly sad) {
		// Check SAD - throw immediately if there are errors
		if (!validateNoXMLErrors(sad)) {
			return new Status(IStatus.ERROR, ScaDebugPlugin.ID,
				"There are errors in the SAD file");
		}

		// Check each referenced component
		MultiStatus multiStatus = new MultiStatus(ScaDebugPlugin.ID, 0, "Some XML file(s) have errors", null);
		for (ComponentFile componentFile : sad.getComponentFiles().getComponentFile()) {
			// Check SPD - no further checks if it is missing
			SoftPkg spd = componentFile.getSoftPkg();
			if (spd == null) {
				String msg = String.format("Missing component SPD for component %s (%s)", componentFile.getId(), componentFile.getLocalFile().getName());
				multiStatus.add(new Status(IStatus.ERROR, ScaDebugPlugin.ID, msg));
				continue;
			}

			IStatus status = SpdLauncherUtil.validateAllXML(spd);
			if (!status.isOK()) {
				if (status.isMultiStatus()) {
					multiStatus.addAll(status);
				} else {
					multiStatus.add(status);
				}
			}
		}

		if (!multiStatus.isOK()) {
			return multiStatus;
		} else {
			return Status.OK_STATUS;
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
