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

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 * @see gov.redhawk.ide.debug.ScaDebugPackage
 * @generated
 */
public interface ScaDebugFactory extends EFactory {

	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ScaDebugFactory eINSTANCE = gov.redhawk.ide.debug.impl.ScaDebugFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Local Sca</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Local Sca</em>'.
	 * @generated
	 */
	LocalSca createLocalSca();

	/**
	 * Returns a new object of class '<em>Notifying Naming Context</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Notifying Naming Context</em>'.
	 * @generated
	 */
	NotifyingNamingContext createNotifyingNamingContext();

	/**
	 * Returns a new object of class '<em>Local File Manager</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Local File Manager</em>'.
	 * @generated
	 */
	LocalFileManager createLocalFileManager();

	/**
	 * Returns a new object of class '<em>Local Sca Waveform</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Local Sca Waveform</em>'.
	 * @generated
	 */
	LocalScaWaveform createLocalScaWaveform();

	/**
	 * Returns a new object of class '<em>Local Sca Component</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Local Sca Component</em>'.
	 * @generated
	 */
	LocalScaComponent createLocalScaComponent();

	/**
	 * Returns a new object of class '<em>Local Sca Device Manager</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Local Sca Device Manager</em>'.
	 * @generated
	 */
	LocalScaDeviceManager createLocalScaDeviceManager();

	/**
	 * Returns a new object of class '<em>Local Sca Executable Device</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Local Sca Executable Device</em>'.
	 * @generated
	 */
	LocalScaExecutableDevice createLocalScaExecutableDevice();

	/**
	 * Returns a new object of class '<em>Local Sca Loadable Device</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Local Sca Loadable Device</em>'.
	 * @generated
	 */
	LocalScaLoadableDevice createLocalScaLoadableDevice();

	/**
	 * Returns a new object of class '<em>Local Sca Device</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Local Sca Device</em>'.
	 * @generated
	 */
	LocalScaDevice createLocalScaDevice();

	/**
	 * Returns a new object of class '<em>Local Sca Service</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Local Sca Service</em>'.
	 * @generated
	 */
	LocalScaService createLocalScaService();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	ScaDebugPackage getScaDebugPackage();

} //ScaDebugFactory
