/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package gov.redhawk.ide.debug;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;

import CF.DataType;
import CF.ErrorNumberType;
import CF.ResourceFactoryPackage.CreateResourceFailure;
import gov.redhawk.model.sca.ScaDevice;
import mil.jpeojtrs.sca.spd.SoftPkg;

/**
 * @since 9.0
 */
public class LocalDeviceFactory extends SpdResourceFactory {

	public LocalDeviceFactory(SoftPkg spd) {
		super(spd);
	}

	private LocalScaDeviceManager getSandboxDevMgr(IProgressMonitor monitor) throws CoreException {
		return ScaDebugPlugin.getInstance().getLocalSca(monitor).getSandboxDeviceManager();
	}

	@Override
	protected CF.Resource createInstance(final String compID, final DataType[] qualifiers, final String launchMode, String implementation)
		throws CreateResourceFailure {
		ScaDevice< ? > dev = (ScaDevice< ? >) getResource(compID);
		if (dev != null) {
			return dev.getObj();
		}

		final SoftPkg spd = loadSpd();

		if (implementation == null) {
			if (!spd.getImplementation().isEmpty()) {
				implementation = spd.getImplementation().get(0).getId();
			} else {
				throw new CreateResourceFailure(ErrorNumberType.CF_EINVAL, "No implementations for component: " + identifier());
			}
		}

		LocalScaDeviceManager devMgr;
		try {
			devMgr = getSandboxDevMgr(null);
		} catch (CoreException e) {
			ScaDebugPlugin.getInstance().getLog().log(new Status(e.getStatus().getSeverity(), ScaDebugPlugin.ID, "Failed to get sandbox device manager", e));
			throw new CreateResourceFailure(ErrorNumberType.CF_ENODEV, "Failed to get sandbox device manager");
		}

		try {
			final LocalScaDevice device = (LocalScaDevice) devMgr.launch(compID, qualifiers, getSpdUri(), implementation, launchMode);
			getLaunched().add(device);
			return device.fetchNarrowedObject(null);
		} catch (CoreException e) {
			ScaDebugPlugin.getInstance().getLog().log(new Status(e.getStatus().getSeverity(), ScaDebugPlugin.ID, "Failed to create instance.", e));
			throw new CreateResourceFailure(ErrorNumberType.CF_EFAULT, "Failed to launch: " + identifier() + " " + e.getMessage());
		}
	}
}
