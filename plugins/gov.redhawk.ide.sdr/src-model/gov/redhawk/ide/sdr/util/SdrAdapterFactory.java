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
// BEGIN GENERATED CODE
package gov.redhawk.ide.sdr.util;

import java.util.Map;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;

import gov.redhawk.ide.sdr.ComponentsContainer;
import gov.redhawk.ide.sdr.ComponentsSubContainer;
import gov.redhawk.ide.sdr.DevicesContainer;
import gov.redhawk.ide.sdr.NodesContainer;
import gov.redhawk.ide.sdr.NodesSubContainer;
import gov.redhawk.ide.sdr.PropertyRegistry;
import gov.redhawk.ide.sdr.SdrPackage;
import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.ServicesContainer;
import gov.redhawk.ide.sdr.SharedLibrariesContainer;
import gov.redhawk.ide.sdr.SoftPkgRegistry;
import gov.redhawk.ide.sdr.WaveformsContainer;
import gov.redhawk.ide.sdr.WaveformsSubContainer;
import mil.jpeojtrs.sca.prf.AbstractProperty;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see gov.redhawk.ide.sdr.SdrPackage
 * @generated
 */
public class SdrAdapterFactory extends AdapterFactoryImpl {
	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static SdrPackage modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SdrAdapterFactory() {
		if (modelPackage == null) {
			modelPackage = SdrPackage.eINSTANCE;
		}
	}

	/**
	 * Returns whether this factory is applicable for the type of the object.
	 * <!-- begin-user-doc -->
	 * This implementation returns <code>true</code> if the object is either the model's package or is an instance
	 * object of the model.
	 * <!-- end-user-doc -->
	 * @return whether this factory is applicable for the type of the object.
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object object) {
		if (object == modelPackage) {
			return true;
		}
		if (object instanceof EObject) {
			return ((EObject) object).eClass().getEPackage() == modelPackage;
		}
		return false;
	}

	/**
	 * The switch that delegates to the <code>createXXX</code> methods.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SdrSwitch<Adapter> modelSwitch = new SdrSwitch<Adapter>() {
		@Override
		public Adapter caseSdrRoot(SdrRoot object) {
			return createSdrRootAdapter();
		}

		@Override
		public Adapter caseComponentsContainer(ComponentsContainer object) {
			return createComponentsContainerAdapter();
		}

		@Override
		public Adapter caseComponentsSubContainer(ComponentsSubContainer object) {
			return createComponentsSubContainerAdapter();
		}

		@Override
		public Adapter caseWaveformsContainer(WaveformsContainer object) {
			return createWaveformsContainerAdapter();
		}

		@Override
		public Adapter caseWaveformsSubContainer(WaveformsSubContainer object) {
			return createWaveformsSubContainerAdapter();
		}

		@Override
		public Adapter caseDevicesContainer(DevicesContainer object) {
			return createDevicesContainerAdapter();
		}

		@Override
		public Adapter caseServicesContainer(ServicesContainer object) {
			return createServicesContainerAdapter();
		}

		@Override
		public Adapter caseSharedLibrariesContainer(SharedLibrariesContainer object) {
			return createSharedLibrariesContainerAdapter();
		}

		@Override
		public Adapter caseNodesContainer(NodesContainer object) {
			return createNodesContainerAdapter();
		}

		@Override
		public Adapter caseNodesSubContainer(NodesSubContainer object) {
			return createNodesSubContainerAdapter();
		}

		@Override
		public Adapter caseStringToAbstractProperty(Map.Entry<String, AbstractProperty> object) {
			return createStringToAbstractPropertyAdapter();
		}

		@Override
		public Adapter casePropertyRegistry(PropertyRegistry object) {
			return createPropertyRegistryAdapter();
		}

		@Override
		public Adapter caseSoftPkgRegistry(SoftPkgRegistry object) {
			return createSoftPkgRegistryAdapter();
		}

		@Override
		public Adapter defaultCase(EObject object) {
			return createEObjectAdapter();
		}
	};

	/**
	 * Creates an adapter for the <code>target</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param target the object to adapt.
	 * @return the adapter for the <code>target</code>.
	 * @generated
	 */
	@Override
	public Adapter createAdapter(Notifier target) {
		return modelSwitch.doSwitch((EObject) target);
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.ide.sdr.SdrRoot <em>Root</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.ide.sdr.SdrRoot
	 * @generated
	 */
	public Adapter createSdrRootAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.ide.sdr.ComponentsContainer <em>Components
	 * Container</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.ide.sdr.ComponentsContainer
	 * @generated
	 */
	public Adapter createComponentsContainerAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.ide.sdr.ComponentsSubContainer <em>Components
	 * Sub Container</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.ide.sdr.ComponentsSubContainer
	 * @generated
	 */
	public Adapter createComponentsSubContainerAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.ide.sdr.WaveformsContainer <em>Waveforms
	 * Container</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.ide.sdr.WaveformsContainer
	 * @generated
	 */
	public Adapter createWaveformsContainerAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.ide.sdr.WaveformsSubContainer <em>Waveforms Sub
	 * Container</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.ide.sdr.WaveformsSubContainer
	 * @generated
	 */
	public Adapter createWaveformsSubContainerAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.ide.sdr.DevicesContainer <em>Devices
	 * Container</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.ide.sdr.DevicesContainer
	 * @generated
	 */
	public Adapter createDevicesContainerAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.ide.sdr.ServicesContainer <em>Services
	 * Container</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.ide.sdr.ServicesContainer
	 * @generated
	 */
	public Adapter createServicesContainerAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.ide.sdr.SharedLibrariesContainer <em>Shared
	 * Libraries Container</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.ide.sdr.SharedLibrariesContainer
	 * @generated
	 */
	public Adapter createSharedLibrariesContainerAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.ide.sdr.NodesContainer <em>Nodes
	 * Container</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.ide.sdr.NodesContainer
	 * @generated
	 */
	public Adapter createNodesContainerAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.ide.sdr.NodesSubContainer <em>Nodes Sub
	 * Container</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.ide.sdr.NodesSubContainer
	 * @generated
	 */
	public Adapter createNodesSubContainerAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link java.util.Map.Entry <em>String To Abstract Property</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see java.util.Map.Entry
	 * @generated
	 */
	public Adapter createStringToAbstractPropertyAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.ide.sdr.PropertyRegistry <em>Property
	 * Registry</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.ide.sdr.PropertyRegistry
	 * @generated
	 */
	public Adapter createPropertyRegistryAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.ide.sdr.SoftPkgRegistry <em>Soft Pkg
	 * Registry</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.ide.sdr.SoftPkgRegistry
	 * @generated
	 */
	public Adapter createSoftPkgRegistryAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for the default case.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @generated
	 */
	public Adapter createEObjectAdapter() {
		return null;
	}

} // SdrAdapterFactory
