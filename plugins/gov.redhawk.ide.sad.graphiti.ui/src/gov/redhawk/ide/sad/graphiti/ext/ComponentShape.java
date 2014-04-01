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
package gov.redhawk.ide.sad.graphiti.ext;

import mil.jpeojtrs.sca.sad.AssemblyController;
import mil.jpeojtrs.sca.sad.ExternalPorts;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Component Shape</b></em>'.
 * <!-- end-user-doc -->
 *
 *
 * @see gov.redhawk.ide.sad.graphiti.ext.RHGxPackage#getComponentShape()
 * @model
 * @generated
 */
public interface ComponentShape extends RHContainerShape {
	/**
   * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
   * @model targetContainerShapeUnique="false" ciDataType="gov.redhawk.ide.sad.graphiti.ext.SadComponentInstantiation" ciUnique="false" featureProviderDataType="gov.redhawk.ide.sad.graphiti.ext.IFeatureProvider" featureProviderUnique="false" externalPortsDataType="gov.redhawk.ide.sad.graphiti.ext.ExternalPorts" externalPortsUnique="false" assemblyControllerDataType="gov.redhawk.ide.sad.graphiti.ext.AssemblyController" assemblyControllerUnique="false"
   * @generated
   */
	void init(ContainerShape targetContainerShape, SadComponentInstantiation ci, IFeatureProvider featureProvider, ExternalPorts externalPorts,
		AssemblyController assemblyController);

	/**
   * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
   * <!-- begin-model-doc -->
   * performs a layout on the contents of this shape
   * <!-- end-model-doc -->
   * @model
   * @generated
   */
	void layout();

	/**
   * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
   * <!-- begin-model-doc -->
   * Updates the shape with supplied values
   * <!-- end-model-doc -->
   * @model dataType="gov.redhawk.ide.sad.graphiti.ext.Reason" unique="false" ciDataType="gov.redhawk.ide.sad.graphiti.ext.SadComponentInstantiation" ciUnique="false" featureProviderDataType="gov.redhawk.ide.sad.graphiti.ext.IFeatureProvider" featureProviderUnique="false" externalPortsDataType="gov.redhawk.ide.sad.graphiti.ext.ExternalPorts" externalPortsUnique="false" assemblyControllerDataType="gov.redhawk.ide.sad.graphiti.ext.AssemblyController" assemblyControllerUnique="false"
   * @generated
   */
	Reason update(SadComponentInstantiation ci, IFeatureProvider featureProvider, ExternalPorts externalPorts, AssemblyController assemblyController);

	/**
   * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
   * <!-- begin-model-doc -->
   * Checks if shape requires an update.
   * If update required returns Reason with true
   * boolean value and message describing what needs to be updated
   * <!-- end-model-doc -->
   * @model dataType="gov.redhawk.ide.sad.graphiti.ext.Reason" unique="false" ciDataType="gov.redhawk.ide.sad.graphiti.ext.SadComponentInstantiation" ciUnique="false" featureProviderDataType="gov.redhawk.ide.sad.graphiti.ext.IFeatureProvider" featureProviderUnique="false" externalPortsDataType="gov.redhawk.ide.sad.graphiti.ext.ExternalPorts" externalPortsUnique="false" assemblyControllerDataType="gov.redhawk.ide.sad.graphiti.ext.AssemblyController" assemblyControllerUnique="false"
   * @generated
   */
	Reason updateNeeded(SadComponentInstantiation ci, IFeatureProvider featureProvider, ExternalPorts externalPorts, AssemblyController assemblyController);

} // ComponentShape
