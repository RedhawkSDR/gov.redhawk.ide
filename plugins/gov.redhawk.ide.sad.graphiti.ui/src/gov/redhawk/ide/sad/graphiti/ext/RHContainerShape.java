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

import java.util.List;

import mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;

import mil.jpeojtrs.sca.sad.Port;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;

import org.eclipse.graphiti.features.impl.Reason;

import org.eclipse.graphiti.mm.algorithms.styles.Style;

import org.eclipse.graphiti.mm.pictograms.ContainerShape;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>RH Container Shape</b></em>'.
 * <!-- end-user-doc -->
 *
 *
 * @see gov.redhawk.ide.sad.graphiti.ext.RHGxPackage#getRHContainerShape()
 * @model
 * @generated
 */
public interface RHContainerShape extends ContainerShape {
	/**
   * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
   * @model targetContainerShapeUnique="false" outerTextUnique="false" businessObjectsDataType="gov.redhawk.ide.sad.graphiti.ext.List<org.eclipse.emf.ecore.EObject>" businessObjectsUnique="false" businessObjectsMany="false" featureProviderDataType="gov.redhawk.ide.sad.graphiti.ext.IFeatureProvider" featureProviderUnique="false" outerImageIdUnique="false" outerContainerStyleUnique="false" innerTextUnique="false" innerImageIdUnique="false" innerContainerStyleUnique="false" interfaceStubDataType="gov.redhawk.ide.sad.graphiti.ext.ComponentSupportedInterfaceStub" interfaceStubUnique="false" usesUnique="false" usesMany="false" providesUnique="false" providesMany="false" externalPortsDataType="gov.redhawk.ide.sad.graphiti.ext.List<gov.redhawk.ide.sad.graphiti.ext.Port>" externalPortsUnique="false" externalPortsMany="false"
   * @generated
   */
	void init(ContainerShape targetContainerShape, String outerText, List<EObject> businessObjects, IFeatureProvider featureProvider, String outerImageId,
		Style outerContainerStyle, String innerText, String innerImageId, Style innerContainerStyle, ComponentSupportedInterfaceStub interfaceStub,
		EList<UsesPortStub> uses, EList<ProvidesPortStub> provides, List<Port> externalPorts);

	/**
   * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
   * <!-- begin-model-doc -->
   * Returns providesPortsStubs business object list linked to getProvidesPortsContainerShape()
   * <!-- end-model-doc -->
   * @model kind="operation" unique="false" many="false"
   * @generated
   */
	EList<ProvidesPortStub> getProvidesPortStubs();

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
   * @model dataType="gov.redhawk.ide.sad.graphiti.ext.Reason" unique="false" outerTextUnique="false" businessObjectUnique="false" featureProviderDataType="gov.redhawk.ide.sad.graphiti.ext.IFeatureProvider" featureProviderUnique="false" outerImageIdUnique="false" outerContainerStyleUnique="false" innerTextUnique="false" innerImageIdUnique="false" innerContainerStyleUnique="false" interfaceStubDataType="gov.redhawk.ide.sad.graphiti.ext.ComponentSupportedInterfaceStub" interfaceStubUnique="false" usesUnique="false" usesMany="false" providesUnique="false" providesMany="false" externalPortsDataType="gov.redhawk.ide.sad.graphiti.ext.List<gov.redhawk.ide.sad.graphiti.ext.Port>" externalPortsUnique="false" externalPortsMany="false"
   * @generated
   */
	Reason update(String outerText, Object businessObject, IFeatureProvider featureProvider, String outerImageId, Style outerContainerStyle, String innerText,
		String innerImageId, Style innerContainerStyle, ComponentSupportedInterfaceStub interfaceStub, EList<UsesPortStub> uses,
		EList<ProvidesPortStub> provides, List<Port> externalPorts);

	/**
   * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
   * <!-- begin-model-doc -->
   * Checks if shape requires an update.
   * If update required returns Reason with true
   * boolean value and message describing what needs to be updated
   * <!-- end-model-doc -->
   * @model dataType="gov.redhawk.ide.sad.graphiti.ext.Reason" unique="false" outerTextUnique="false" businessObjectUnique="false" featureProviderDataType="gov.redhawk.ide.sad.graphiti.ext.IFeatureProvider" featureProviderUnique="false" outerImageIdUnique="false" outerContainerStyleUnique="false" innerTextUnique="false" innerImageIdUnique="false" innerContainerStyleUnique="false" interfaceStubDataType="gov.redhawk.ide.sad.graphiti.ext.ComponentSupportedInterfaceStub" interfaceStubUnique="false" usesUnique="false" usesMany="false" providesUnique="false" providesMany="false" externalPortsDataType="gov.redhawk.ide.sad.graphiti.ext.List<gov.redhawk.ide.sad.graphiti.ext.Port>" externalPortsUnique="false" externalPortsMany="false"
   * @generated
   */
	Reason updateNeeded(String outerText, Object businessObject, IFeatureProvider featureProvider, String outerImageId, Style outerContainerStyle,
		String innerText, String innerImageId, Style innerContainerStyle, ComponentSupportedInterfaceStub interfaceStub, EList<UsesPortStub> uses,
		EList<ProvidesPortStub> provides, List<Port> externalPorts);

} // RHContainerShape
