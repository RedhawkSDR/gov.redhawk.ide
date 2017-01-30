/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.debug.impl.commands;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EClass;
import org.omg.CORBA.Object;

import CF.Device;
import CF.ExecutableDeviceHelper;
import CF.LoadableDeviceHelper;
import gov.redhawk.ide.debug.ScaDebugFactory;
import gov.redhawk.ide.debug.ScaDebugPackage;
import gov.redhawk.model.sca.ScaDevice;
import gov.redhawk.model.sca.ScaDeviceManager;
import gov.redhawk.model.sca.commands.ScaDeviceManagerMergeDevicesCommand;

public class LocalScaDeviceManagerMergeDevicesCommand extends ScaDeviceManagerMergeDevicesCommand {

	public LocalScaDeviceManagerMergeDevicesCommand(ScaDeviceManager provider, Device[] devices, IStatus status) {
		super(provider, devices, status);
	}

	@Override
	protected DeviceData getDeviceData(Device device) {
		if (device._is_a(ExecutableDeviceHelper.id())) {
			return new DeviceData(ExecutableDeviceHelper.unchecked_narrow(device), ScaDebugPackage.Literals.LOCAL_SCA_EXECUTABLE_DEVICE);
		} else if (device._is_a(LoadableDeviceHelper.id())) {
			return new DeviceData(LoadableDeviceHelper.unchecked_narrow(device), ScaDebugPackage.Literals.LOCAL_SCA_LOADABLE_DEVICE);
		}
		return new DeviceData(device, ScaDebugPackage.Literals.LOCAL_SCA_DEVICE);
	}

	@Override
	protected ScaDevice< ? > createDevice(Object deviceObject, EClass deviceType) {
		ScaDevice< ? > device = (ScaDevice< ? >) ScaDebugFactory.eINSTANCE.create(deviceType);
		device.setCorbaObj(deviceObject);
		return device;
	}

}
