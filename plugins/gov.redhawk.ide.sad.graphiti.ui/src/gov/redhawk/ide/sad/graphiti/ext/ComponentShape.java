/**
 */
package gov.redhawk.ide.sad.graphiti.ext;

import java.util.List;

import mil.jpeojtrs.sca.sad.AssemblyController;
import mil.jpeojtrs.sca.sad.Port;
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
public interface ComponentShape extends RHContainerShape
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model targetContainerShapeUnique="false" ciDataType="gov.redhawk.ide.sad.graphiti.ext.SadComponentInstantiation" ciUnique="false" featureProviderDataType="gov.redhawk.ide.sad.graphiti.ext.IFeatureProvider" featureProviderUnique="false" ciExternalPortsDataType="gov.redhawk.ide.sad.graphiti.ext.List<gov.redhawk.ide.sad.graphiti.ext.Port>" ciExternalPortsUnique="false" ciExternalPortsMany="false" assemblyControllerDataType="gov.redhawk.ide.sad.graphiti.ext.AssemblyController" assemblyControllerUnique="false"
   * @generated
   */
  void init(ContainerShape targetContainerShape, SadComponentInstantiation ci, IFeatureProvider featureProvider, List<Port> ciExternalPorts, AssemblyController assemblyController);

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
   * @model dataType="gov.redhawk.ide.sad.graphiti.ext.Reason" unique="false" ciDataType="gov.redhawk.ide.sad.graphiti.ext.SadComponentInstantiation" ciUnique="false" featureProviderDataType="gov.redhawk.ide.sad.graphiti.ext.IFeatureProvider" featureProviderUnique="false" ciExternalPortsDataType="gov.redhawk.ide.sad.graphiti.ext.List<gov.redhawk.ide.sad.graphiti.ext.Port>" ciExternalPortsUnique="false" ciExternalPortsMany="false" assemblyControllerDataType="gov.redhawk.ide.sad.graphiti.ext.AssemblyController" assemblyControllerUnique="false"
   * @generated
   */
  Reason update(SadComponentInstantiation ci, IFeatureProvider featureProvider, List<Port> ciExternalPorts, AssemblyController assemblyController);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * <!-- begin-model-doc -->
   * Checks if shape requires an update.
   * If update required returns Reason with true
   * boolean value and message describing what needs to be updated
   * <!-- end-model-doc -->
   * @model dataType="gov.redhawk.ide.sad.graphiti.ext.Reason" unique="false" ciDataType="gov.redhawk.ide.sad.graphiti.ext.SadComponentInstantiation" ciUnique="false" featureProviderDataType="gov.redhawk.ide.sad.graphiti.ext.IFeatureProvider" featureProviderUnique="false" ciExternalPortsDataType="gov.redhawk.ide.sad.graphiti.ext.List<gov.redhawk.ide.sad.graphiti.ext.Port>" ciExternalPortsUnique="false" ciExternalPortsMany="false" assemblyControllerDataType="gov.redhawk.ide.sad.graphiti.ext.AssemblyController" assemblyControllerUnique="false"
   * @generated
   */
  Reason updateNeeded(SadComponentInstantiation ci, IFeatureProvider featureProvider, List<Port> ciExternalPorts, AssemblyController assemblyController);

} // ComponentShape
