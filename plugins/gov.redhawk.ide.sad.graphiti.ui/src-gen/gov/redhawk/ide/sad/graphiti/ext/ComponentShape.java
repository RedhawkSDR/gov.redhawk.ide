/**
 */
package gov.redhawk.ide.sad.graphiti.ext;

import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

import org.eclipse.graphiti.features.IFeatureProvider;

import org.eclipse.graphiti.features.impl.Reason;

import org.eclipse.graphiti.mm.algorithms.Text;

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
   * @model targetContainerShapeUnique="false" ciDataType="gov.redhawk.ide.sad.graphiti.ext.SadComponentInstantiation" ciUnique="false" featureProviderDataType="gov.redhawk.ide.sad.graphiti.ext.IFeatureProvider" featureProviderUnique="false"
   * @generated
   */
  void init(ContainerShape targetContainerShape, SadComponentInstantiation ci, IFeatureProvider featureProvider);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * <!-- begin-model-doc -->
   * Add an Ellipse to provided container shape that will contain the start order from sadComponentInstantiation
   * <!-- end-model-doc -->
   * @model unique="false" innerContainerShapeUnique="false" sadComponentInstantiationDataType="gov.redhawk.ide.sad.graphiti.ext.SadComponentInstantiation" sadComponentInstantiationUnique="false"
   * @generated
   */
  ContainerShape addStartOrderEllipse(ContainerShape innerContainerShape, SadComponentInstantiation sadComponentInstantiation);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * <!-- begin-model-doc -->
   * Return the startOrderEllipseShape
   * <!-- end-model-doc -->
   * @model kind="operation" unique="false"
   * @generated
   */
  ContainerShape getStartOrderEllipseShape();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * <!-- begin-model-doc -->
   * Return the startOrderText
   * <!-- end-model-doc -->
   * @model kind="operation" unique="false"
   * @generated
   */
  Text getStartOrderText();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * <!-- begin-model-doc -->
   * Updates the shape with supplied values
   * <!-- end-model-doc -->
   * @model dataType="gov.redhawk.ide.sad.graphiti.ext.Reason" unique="false" ciDataType="gov.redhawk.ide.sad.graphiti.ext.SadComponentInstantiation" ciUnique="false" featureProviderDataType="gov.redhawk.ide.sad.graphiti.ext.IFeatureProvider" featureProviderUnique="false"
   * @generated
   */
  Reason update(SadComponentInstantiation ci, IFeatureProvider featureProvider);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * <!-- begin-model-doc -->
   * Checks if shape requires an update.
   * If update required returns Reason with true
   * boolean value and message describing what needs to be updated
   * <!-- end-model-doc -->
   * @model dataType="gov.redhawk.ide.sad.graphiti.ext.Reason" unique="false" ciDataType="gov.redhawk.ide.sad.graphiti.ext.SadComponentInstantiation" ciUnique="false" featureProviderDataType="gov.redhawk.ide.sad.graphiti.ext.IFeatureProvider" featureProviderUnique="false"
   * @generated
   */
  Reason updateNeeded(SadComponentInstantiation ci, IFeatureProvider featureProvider);

} // ComponentShape
