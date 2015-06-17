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
// BEGIN GENERATED CODE
package gov.redhawk.ide.debug;

import gov.redhawk.model.sca.ScaDevice;
import CF.Device;

/**
 * <!-- begin-user-doc -->
 * A model object to represent sandbox devices. It is used both for devices launched locally as well as domain
 * devices referenced by proxy in the sandbox device manager.
 * <!-- end-user-doc -->
 *
 *
 * @see gov.redhawk.ide.debug.ScaDebugPackage#getLocalScaDevice()
 * @model superTypes="gov.redhawk.model.sca.ScaDevice<mil.jpeojtrs.sca.cf.Device> gov.redhawk.ide.debug.LocalAbstractComponent"
 * @generated
 */
public interface LocalScaDevice extends ScaDevice<Device>, LocalAbstractComponent {
} // LocalScaDevice
