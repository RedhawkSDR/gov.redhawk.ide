/**
 */
package gov.redhawk.ide.sad.graphiti.ext;

import mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;

import org.eclipse.emf.common.util.EList;

import org.eclipse.graphiti.features.IFeatureProvider;

import org.eclipse.graphiti.features.impl.Reason;

import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Text;

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
public interface RHContainerShape extends ContainerShape
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model targetContainerShapeUnique="false" outerTextUnique="false" businessObjectUnique="false" featureProviderDataType="gov.redhawk.ide.sad.graphiti.ext.IFeatureProvider" featureProviderUnique="false" outerImageIdUnique="false" outerContainerStyleUnique="false" innerTextUnique="false" innerImageIdUnique="false" innerContainerStyleUnique="false" interfaceStubDataType="gov.redhawk.ide.sad.graphiti.ext.ComponentSupportedInterfaceStub" interfaceStubUnique="false" usesUnique="false" usesMany="false" providesUnique="false" providesMany="false"
   * @generated
   */
  void init(ContainerShape targetContainerShape, String outerText, Object businessObject, IFeatureProvider featureProvider, String outerImageId, Style outerContainerStyle, String innerText, String innerImageId, Style innerContainerStyle, ComponentSupportedInterfaceStub interfaceStub, EList<UsesPortStub> uses, EList<ProvidesPortStub> provides);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * <!-- begin-model-doc -->
   * Add inner container
   * <!-- end-model-doc -->
   * @model unique="false" targetContainerShapeUnique="false" textUnique="false" featureProviderDataType="gov.redhawk.ide.sad.graphiti.ext.IFeatureProvider" featureProviderUnique="false" imageIdUnique="false" containerStyleUnique="false"
   * @generated
   */
  ContainerShape addInnerContainer(ContainerShape targetContainerShape, String text, IFeatureProvider featureProvider, String imageId, Style containerStyle);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * <!-- begin-model-doc -->
   * Add lollipop to targetContainerShape.  Lollipop anchor will link to the provided business object.
   * <!-- end-model-doc -->
   * @model unique="false" targetContainerShapeUnique="false" anchorBusinessObjectUnique="false" featureProviderDataType="gov.redhawk.ide.sad.graphiti.ext.IFeatureProvider" featureProviderUnique="false"
   * @generated
   */
  ContainerShape addLollipop(ContainerShape targetContainerShape, Object anchorBusinessObject, IFeatureProvider featureProvider);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * <!-- begin-model-doc -->
   * Adds a ProvidesPortStub shape to the providesPortsContainerShape
   * <!-- end-model-doc -->
   * @model pDataType="gov.redhawk.ide.sad.graphiti.ext.ProvidesPortStub" pUnique="false" providesPortsContainerShapeUnique="false" providesPortNameLengthUnique="false" featureProviderDataType="gov.redhawk.ide.sad.graphiti.ext.IFeatureProvider" featureProviderUnique="false"
   * @generated
   */
  void addProvidesPortContainerShape(ProvidesPortStub p, ContainerShape providesPortsContainerShape, int providesPortNameLength, IFeatureProvider featureProvider);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * <!-- begin-model-doc -->
   * Adds provides port container to provided container shape.  Adds a port shape with name and anchor for each providesPortStub.
   * <!-- end-model-doc -->
   * @model outerContainerShapeUnique="false" providesPortStubsUnique="false" providesPortStubsMany="false" featureProviderDataType="gov.redhawk.ide.sad.graphiti.ext.IFeatureProvider" featureProviderUnique="false"
   * @generated
   */
  void addProvidesPorts(ContainerShape outerContainerShape, EList<ProvidesPortStub> providesPortStubs, IFeatureProvider featureProvider);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * <!-- begin-model-doc -->
   * Adds a UsesPort shape to the usesPortsContainerShape
   * <!-- end-model-doc -->
   * @model pDataType="gov.redhawk.ide.sad.graphiti.ext.UsesPortStub" pUnique="false" usesPortsContainerShapeUnique="false" usesPortNameLengthUnique="false" featureProviderDataType="gov.redhawk.ide.sad.graphiti.ext.IFeatureProvider" featureProviderUnique="false"
   * @generated
   */
  void addUsesPortContainerShape(UsesPortStub p, ContainerShape usesPortsContainerShape, int usesPortNameLength, IFeatureProvider featureProvider);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * <!-- begin-model-doc -->
   * Adds uses port container to provided container shape.  Adds a port shape with name and anchor for each usesPortStub.
   * <!-- end-model-doc -->
   * @model outerContainerShapeUnique="false" usesPortStubsUnique="false" usesPortStubsMany="false" featureProviderDataType="gov.redhawk.ide.sad.graphiti.ext.IFeatureProvider" featureProviderUnique="false"
   * @generated
   */
  void addUsesPorts(ContainerShape outerContainerShape, EList<UsesPortStub> usesPortStubs, IFeatureProvider featureProvider);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * <!-- begin-model-doc -->
   * Return the usesPortsContainerShape
   * <!-- end-model-doc -->
   * @model kind="operation" unique="false"
   * @generated
   */
  ContainerShape getUsesPortsContainerShape();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * <!-- begin-model-doc -->
   * Return the usesPortsContainerShape
   * <!-- end-model-doc -->
   * @model kind="operation" unique="false"
   * @generated
   */
  ContainerShape getProvidesPortsContainerShape();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * <!-- begin-model-doc -->
   * Returns usesPortsStubs business object list linked to getUsesPortsContainerShape()
   * <!-- end-model-doc -->
   * @model kind="operation" unique="false" many="false"
   * @generated
   */
  EList<UsesPortStub> getUsesPortStubs();

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
   * Return the text for outer container
   * <!-- end-model-doc -->
   * @model kind="operation" unique="false"
   * @generated
   */
  Text getOuterText();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * <!-- begin-model-doc -->
   * Return the image for outer container
   * <!-- end-model-doc -->
   * @model kind="operation" unique="false"
   * @generated
   */
  Image getOuterImage();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * <!-- begin-model-doc -->
   * Return the text for inner container
   * <!-- end-model-doc -->
   * @model kind="operation" unique="false"
   * @generated
   */
  Text getInnerText();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * <!-- begin-model-doc -->
   * Return the image for inner container
   * <!-- end-model-doc -->
   * @model kind="operation" unique="false"
   * @generated
   */
  Image getInnerImage();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * <!-- begin-model-doc -->
   * Return the inner container polyline
   * <!-- end-model-doc -->
   * @model kind="operation" unique="false"
   * @generated
   */
  Polyline getInnerPolyline();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * <!-- begin-model-doc -->
   * Return the innerContainerShape
   * <!-- end-model-doc -->
   * @model kind="operation" unique="false"
   * @generated
   */
  ContainerShape getInnerContainerShape();

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
   * @model dataType="gov.redhawk.ide.sad.graphiti.ext.Reason" unique="false" targetContainerShapeUnique="false" outerTextUnique="false" businessObjectUnique="false" featureProviderDataType="gov.redhawk.ide.sad.graphiti.ext.IFeatureProvider" featureProviderUnique="false" outerImageIdUnique="false" outerContainerStyleUnique="false" innerTextUnique="false" innerImageIdUnique="false" innerContainerStyleUnique="false" interfaceStubDataType="gov.redhawk.ide.sad.graphiti.ext.ComponentSupportedInterfaceStub" interfaceStubUnique="false" usesUnique="false" usesMany="false" providesUnique="false" providesMany="false"
   * @generated
   */
  Reason update(ContainerShape targetContainerShape, String outerText, Object businessObject, IFeatureProvider featureProvider, String outerImageId, Style outerContainerStyle, String innerText, String innerImageId, Style innerContainerStyle, ComponentSupportedInterfaceStub interfaceStub, EList<UsesPortStub> uses, EList<ProvidesPortStub> provides);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * <!-- begin-model-doc -->
   * Checks if shape requires an update.
   * If update required returns Reason with true
   * boolean value and message describing what needs to be updated
   * <!-- end-model-doc -->
   * @model dataType="gov.redhawk.ide.sad.graphiti.ext.Reason" unique="false" targetContainerShapeUnique="false" outerTextUnique="false" businessObjectUnique="false" featureProviderDataType="gov.redhawk.ide.sad.graphiti.ext.IFeatureProvider" featureProviderUnique="false" outerImageIdUnique="false" outerContainerStyleUnique="false" innerTextUnique="false" innerImageIdUnique="false" innerContainerStyleUnique="false" interfaceStubDataType="gov.redhawk.ide.sad.graphiti.ext.ComponentSupportedInterfaceStub" interfaceStubUnique="false" usesUnique="false" usesMany="false" providesUnique="false" providesMany="false"
   * @generated
   */
  Reason updateNeeded(ContainerShape targetContainerShape, String outerText, Object businessObject, IFeatureProvider featureProvider, String outerImageId, Style outerContainerStyle, String innerText, String innerImageId, Style innerContainerStyle, ComponentSupportedInterfaceStub interfaceStub, EList<UsesPortStub> uses, EList<ProvidesPortStub> provides);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * <!-- begin-model-doc -->
   * Returns minimum width of shape
   * <!-- end-model-doc -->
   * @model unique="false" outerTitleUnique="false" innerTitleUnique="false" providesPortStubsUnique="false" providesPortStubsMany="false" usesPortStubsUnique="false" usesPortStubsMany="false"
   * @generated
   */
  int getMinimumWidth(String outerTitle, String innerTitle, EList<ProvidesPortStub> providesPortStubs, EList<UsesPortStub> usesPortStubs);

} // RHContainerShape
