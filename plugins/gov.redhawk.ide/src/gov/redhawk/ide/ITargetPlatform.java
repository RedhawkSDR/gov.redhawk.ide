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
package gov.redhawk.ide;

import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.spd.SoftPkg;

/**
 * The Interface ITargetPlatform.
 */
public interface ITargetPlatform {

	/**
	 * Get's the location of the target platform as specified in the
	 * preferences. The platform is only read when the user requests a Reload or
	 * changes the platform location.
	 * 
	 * This location may be file URL or a CORBA URL to a DomainManager. For
	 * example: file:///sdr corbaloc::host:port/DomainName1/DomainManager
	 * 
	 * @return the target platforms main location
	 */
	String getLocation();

	/**
	 * Get's an array of paths to SAD files, relative to the taget platform
	 * location.
	 * 
	 * /waveforms/ossie_demo/ossie_demo.sad.xml
	 * 
	 * If the location is a CORBA accessed, domain these strings are paths that
	 * can be accessed via the DomainManagers fileMgr.open() operations. See SCA
	 * specification 3.1.3.4.2.5.9.5 to understand how paths are handled by
	 * FileManagers
	 * 
	 * @return the paths to the SAD files.
	 */
	String[] getWaveformSadFiles();

	/**
	 * Get's an array of paths to SPD files, relative to the taget platform
	 * location.
	 * 
	 * /xml/am_demod/am_demod.spd.xml
	 * 
	 * Although both components and devices are described in SPD files,
	 * components are differentiated from Devices because they do not implement
	 * the Device interface. As such, you must parse the SCD file to distinguish
	 * between the two.
	 * 
	 * If the location is a CORBA accessed, domain these strings are paths that
	 * can be accessed via the DomainManagers fileMgr.open() operations. See SCA
	 * specification 3.1.3.4.2.5.9.5 to understand how paths are handled by
	 * FileManagers
	 * 
	 * @return the paths to the component SPD files.
	 */
	String[] getComponentSpdFiles();

	/**
	 * Get's an array of paths to SPD files, relative to the taget platform
	 * location.
	 * 
	 * /xml/am_demod/am_demod.spd.xml
	 * 
	 * Although both components and devices are described in SPD files,
	 * components are differentiated from Devices because they do not implement
	 * the Device interface. As such, you must parse the SCD file to distinguish
	 * between the two.
	 * 
	 * If the location is a CORBA accessed, domain these strings are paths that
	 * can be accessed via the DomainManagers fileMgr.open() operations. See SCA
	 * specification 3.1.3.4.2.5.9.5 to understand how paths are handled by
	 * FileManagers
	 * 
	 * @return the paths to the device SPD files.
	 */
	String[] getDeviceSpdFiles();

	/**
	 * Get's an array of paths to DCD files, relative to the taget platform
	 * location.
	 * 
	 * /nodes/default_GPP_node/DeviceMander.dcd.xml
	 * 
	 * If the location is a CORBA accessed, domain these strings are paths that
	 * can be accessed via the DomainManagers fileMgr.open() operations. See SCA
	 * specification 3.1.3.4.2.5.9.5 to understand how paths are handled by
	 * FileManagers
	 * 
	 * @return the paths to the device SPD files.
	 */
	String[] getNodeDcdFiles();

	/**
	 * Get's the list of components in the target platform.
	 * 
	 * @return the components
	 */
	SoftPkg[] getComponents();

	/**
	 * Get's the component at the location referenced by localFile.
	 * 
	 * @param localFile
	 *            the localfile path
	 * @return the component
	 */
	SoftPkg getComponent(String localFile);

	/**
	 * Get's the list of devices in the target platform.
	 * 
	 * @return the devices
	 */
	SoftPkg[] getDevices();

	/**
	 * Get's the list of waveforms in the target platform.
	 * 
	 * @return the waveforms
	 */
	SoftwareAssembly[] getWaveforms();

	/**
	 * Returns the list of nodes in the taget platform.
	 * 
	 * @return the nodes
	 */
	DeviceConfiguration[] getNodes();

	/**
	 * Add a listener who is notified when the target platform changes.
	 * 
	 * @param listener
	 *            the listener
	 */
	void addTargetPlatformChangeListener(ITargetPlatformChangeListener listener);

	/**
	 * Remove a listener who is notified when the target platform changes.
	 * 
	 * @param listener
	 *            the listener
	 */
	void removeTargetPlatformChangeListener(
			ITargetPlatformChangeListener listener);
}
