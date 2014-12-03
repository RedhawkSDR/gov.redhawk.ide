/**
 */
package gov.redhawk.ide.graphiti.ext;

import gov.redhawk.ide.graphiti.ui.diagram.patterns.AbstractContainerPattern;

import java.util.List;

import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;

import mil.jpeojtrs.sca.sad.Port;

import org.eclipse.emf.common.util.EList;

import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IUpdateContext;

import org.eclipse.graphiti.features.impl.Reason;

import org.eclipse.graphiti.mm.pictograms.ContainerShape;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>RH Container Shape</b></em>'.
 * <!-- end-user-doc -->
 *
 *
 * @see gov.redhawk.ide.graphiti.ext.RHGxPackage#getRHContainerShape()
 * @model
 * @generated
 */
public interface RHContainerShape extends ContainerShape {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model contextDataType="gov.redhawk.ide.graphiti.ext.IAddContext" contextUnique="false" patternDataType="gov.redhawk.ide.graphiti.ext.AbstractContainerPattern" patternUnique="false"
	 * @generated
	 */
	void init(IAddContext context, AbstractContainerPattern pattern);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model contextDataType="gov.redhawk.ide.graphiti.ext.IAddContext" contextUnique="false" patternDataType="gov.redhawk.ide.graphiti.ext.AbstractContainerPattern" patternUnique="false" externalPortsDataType="gov.redhawk.ide.graphiti.ext.List<gov.redhawk.ide.graphiti.ext.Port>" externalPortsUnique="false" externalPortsMany="false"
	 * @generated
	 */
	void init(IAddContext context, AbstractContainerPattern pattern, List<Port> externalPorts);

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
	 * @model dataType="gov.redhawk.ide.graphiti.ext.Reason" unique="false" contextDataType="gov.redhawk.ide.graphiti.ext.IUpdateContext" contextUnique="false" patternDataType="gov.redhawk.ide.graphiti.ext.AbstractContainerPattern" patternUnique="false" externalPortsDataType="gov.redhawk.ide.graphiti.ext.List<gov.redhawk.ide.graphiti.ext.Port>" externalPortsUnique="false" externalPortsMany="false"
	 * @generated
	 */
	Reason update(IUpdateContext context, AbstractContainerPattern pattern, List<Port> externalPorts);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Updates the shape with supplied values
	 * <!-- end-model-doc -->
	 * @model dataType="gov.redhawk.ide.graphiti.ext.Reason" unique="false" contextDataType="gov.redhawk.ide.graphiti.ext.IUpdateContext" contextUnique="false" patternDataType="gov.redhawk.ide.graphiti.ext.AbstractContainerPattern" patternUnique="false"
	 * @generated
	 */
	Reason update(IUpdateContext context, AbstractContainerPattern pattern);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Checks if shape requires an update.
	 * If update required returns Reason with true
	 * boolean value and message describing what needs to be updated
	 * <!-- end-model-doc -->
	 * @model dataType="gov.redhawk.ide.graphiti.ext.Reason" unique="false" contextDataType="gov.redhawk.ide.graphiti.ext.IUpdateContext" contextUnique="false" patternDataType="gov.redhawk.ide.graphiti.ext.AbstractContainerPattern" patternUnique="false" externalPortsDataType="gov.redhawk.ide.graphiti.ext.List<gov.redhawk.ide.graphiti.ext.Port>" externalPortsUnique="false" externalPortsMany="false"
	 * @generated
	 */
	Reason updateNeeded(IUpdateContext context, AbstractContainerPattern pattern, List<Port> externalPorts);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Checks if shape requires an update.
	 * If update required returns Reason with true
	 * boolean value and message describing what needs to be updated
	 * <!-- end-model-doc -->
	 * @model dataType="gov.redhawk.ide.graphiti.ext.Reason" unique="false" contextDataType="gov.redhawk.ide.graphiti.ext.IUpdateContext" contextUnique="false" patternDataType="gov.redhawk.ide.graphiti.ext.AbstractContainerPattern" patternUnique="false"
	 * @generated
	 */
	Reason updateNeeded(IUpdateContext context, AbstractContainerPattern pattern);

} // RHContainerShape
