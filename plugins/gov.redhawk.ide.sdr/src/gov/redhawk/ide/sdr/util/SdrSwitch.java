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
package gov.redhawk.ide.sdr.util;

import gov.redhawk.ide.sdr.*;
import gov.redhawk.ide.sdr.ComponentsContainer;
import gov.redhawk.ide.sdr.ComponentsSubContainer;
import gov.redhawk.ide.sdr.DevicesContainer;
import gov.redhawk.ide.sdr.NodesContainer;
import gov.redhawk.ide.sdr.PropertyRegistry;
import gov.redhawk.ide.sdr.SdrPackage;
import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.ServicesContainer;
import gov.redhawk.ide.sdr.SoftPkgRegistry;
import gov.redhawk.ide.sdr.WaveformsContainer;

import java.util.Map;

import mil.jpeojtrs.sca.prf.AbstractProperty;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.Switch;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see gov.redhawk.ide.sdr.SdrPackage
 * @generated
 */
public class SdrSwitch< T > extends Switch<T> {

	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static SdrPackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SdrSwitch() {
		if (modelPackage == null) {
			modelPackage = SdrPackage.eINSTANCE;
		}
	}

	/**
	 * Checks whether this is a switch for the given package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @parameter ePackage the package in question.
	 * @return whether this is a switch for the given package.
	 * @generated
	 */
	@Override
	protected boolean isSwitchFor(EPackage ePackage) {
		return ePackage == modelPackage;
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	@Override
	protected T doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
		case SdrPackage.SDR_ROOT: {
			SdrRoot sdrRoot = (SdrRoot) theEObject;
			T result = caseSdrRoot(sdrRoot);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case SdrPackage.COMPONENTS_CONTAINER: {
			ComponentsContainer componentsContainer = (ComponentsContainer) theEObject;
			T result = caseComponentsContainer(componentsContainer);
			if (result == null)
				result = caseSoftPkgRegistry(componentsContainer);
			if (result == null)
				result = casePropertyRegistry(componentsContainer);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case SdrPackage.COMPONENTS_SUB_CONTAINER: {
			ComponentsSubContainer componentsSubContainer = (ComponentsSubContainer) theEObject;
			T result = caseComponentsSubContainer(componentsSubContainer);
			if (result == null)
				result = caseSoftPkgRegistry(componentsSubContainer);
			if (result == null)
				result = casePropertyRegistry(componentsSubContainer);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case SdrPackage.WAVEFORMS_CONTAINER: {
			WaveformsContainer waveformsContainer = (WaveformsContainer) theEObject;
			T result = caseWaveformsContainer(waveformsContainer);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case SdrPackage.DEVICES_CONTAINER: {
			DevicesContainer devicesContainer = (DevicesContainer) theEObject;
			T result = caseDevicesContainer(devicesContainer);
			if (result == null)
				result = caseSoftPkgRegistry(devicesContainer);
			if (result == null)
				result = casePropertyRegistry(devicesContainer);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case SdrPackage.SERVICES_CONTAINER: {
			ServicesContainer servicesContainer = (ServicesContainer) theEObject;
			T result = caseServicesContainer(servicesContainer);
			if (result == null)
				result = caseSoftPkgRegistry(servicesContainer);
			if (result == null)
				result = casePropertyRegistry(servicesContainer);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case SdrPackage.SHARED_LIBRARIES_CONTAINER: {
			SharedLibrariesContainer sharedLibrariesContainer = (SharedLibrariesContainer) theEObject;
			T result = caseSharedLibrariesContainer(sharedLibrariesContainer);
			if (result == null)
				result = caseSoftPkgRegistry(sharedLibrariesContainer);
			if (result == null)
				result = casePropertyRegistry(sharedLibrariesContainer);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case SdrPackage.NODES_CONTAINER: {
			NodesContainer nodesContainer = (NodesContainer) theEObject;
			T result = caseNodesContainer(nodesContainer);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case SdrPackage.STRING_TO_ABSTRACT_PROPERTY: {
			@SuppressWarnings("unchecked")
			Map.Entry<String, AbstractProperty> stringToAbstractProperty = (Map.Entry<String, AbstractProperty>) theEObject;
			T result = caseStringToAbstractProperty(stringToAbstractProperty);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case SdrPackage.PROPERTY_REGISTRY: {
			PropertyRegistry propertyRegistry = (PropertyRegistry) theEObject;
			T result = casePropertyRegistry(propertyRegistry);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case SdrPackage.SOFT_PKG_REGISTRY: {
			SoftPkgRegistry softPkgRegistry = (SoftPkgRegistry) theEObject;
			T result = caseSoftPkgRegistry(softPkgRegistry);
			if (result == null)
				result = casePropertyRegistry(softPkgRegistry);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		default:
			return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Root</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Root</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSdrRoot(SdrRoot object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Components Container</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Components Container</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseComponentsContainer(ComponentsContainer object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Components Sub Container</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Components Sub Container</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseComponentsSubContainer(ComponentsSubContainer object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Waveforms Container</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Waveforms Container</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseWaveformsContainer(WaveformsContainer object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Devices Container</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Devices Container</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDevicesContainer(DevicesContainer object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Services Container</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Services Container</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseServicesContainer(ServicesContainer object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Shared Libraries Container</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Shared Libraries Container</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSharedLibrariesContainer(SharedLibrariesContainer object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Nodes Container</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Nodes Container</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseNodesContainer(NodesContainer object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>String To Abstract Property</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>String To Abstract Property</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseStringToAbstractProperty(Map.Entry<String, AbstractProperty> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Property Registry</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Property Registry</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePropertyRegistry(PropertyRegistry object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Soft Pkg Registry</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Soft Pkg Registry</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSoftPkgRegistry(SoftPkgRegistry object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	@Override
	public T defaultCase(EObject object) {
		return null;
	}

} //SdrSwitch
