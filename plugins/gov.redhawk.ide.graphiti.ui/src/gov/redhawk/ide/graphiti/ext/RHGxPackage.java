/**
 */
package gov.redhawk.ide.graphiti.ext;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.graphiti.mm.pictograms.PictogramsPackage;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see gov.redhawk.ide.graphiti.ext.RHGxFactory
 * @model kind="package"
 *        annotation="http://www.eclipse.org/emf/2002/GenModel prefix='RHGx' codeFormatting='true' modelPluginVariables='org.eclipse.xtext.xbase.lib' contentTypeIdentifier='http://www.redhawk.gov/model/rhgext/1.0.0' operationReflection='false' modelDirectory='/gov.redhawk.ide.graphiti.ui/src' basePackage='gov.redhawk.ide.graphiti'"
 *        annotation="http://www.eclipse.org/emf/2011/Xcore GenModel='http://www.eclipse.org/emf/2002/GenModel' Ecore='http://www.eclipse.org/emf/2002/Ecore'"
 * @generated
 */
public interface RHGxPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "ext";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://www.redhawk.gov/model/rhgext/1.0.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "ext";

	/**
	 * The package content type ID.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eCONTENT_TYPE = "http://www.redhawk.gov/model/rhgext/1.0.0";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	RHGxPackage eINSTANCE = gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.graphiti.mm.pictograms.ContainerShape <em>Container Shape Impl</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.graphiti.mm.pictograms.ContainerShape
	 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getContainerShapeImpl()
	 * @generated
	 */
	int CONTAINER_SHAPE_IMPL = 0;

	/**
	 * The number of structural features of the '<em>Container Shape Impl</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTAINER_SHAPE_IMPL_FEATURE_COUNT = 0;

	/**
	 * The meta object id for the '{@link gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl <em>RH Container Shape</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl
	 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getRHContainerShape()
	 * @generated
	 */
	int RH_CONTAINER_SHAPE = 1;

	/**
	 * The feature id for the '<em><b>Properties</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE__PROPERTIES = PictogramsPackage.CONTAINER_SHAPE__PROPERTIES;

	/**
	 * The feature id for the '<em><b>Visible</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE__VISIBLE = PictogramsPackage.CONTAINER_SHAPE__VISIBLE;

	/**
	 * The feature id for the '<em><b>Graphics Algorithm</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE__GRAPHICS_ALGORITHM = PictogramsPackage.CONTAINER_SHAPE__GRAPHICS_ALGORITHM;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE__ACTIVE = PictogramsPackage.CONTAINER_SHAPE__ACTIVE;

	/**
	 * The feature id for the '<em><b>Link</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE__LINK = PictogramsPackage.CONTAINER_SHAPE__LINK;

	/**
	 * The feature id for the '<em><b>Anchors</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE__ANCHORS = PictogramsPackage.CONTAINER_SHAPE__ANCHORS;

	/**
	 * The feature id for the '<em><b>Container</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE__CONTAINER = PictogramsPackage.CONTAINER_SHAPE__CONTAINER;

	/**
	 * The feature id for the '<em><b>Children</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE__CHILDREN = PictogramsPackage.CONTAINER_SHAPE__CHILDREN;

	/**
	 * The feature id for the '<em><b>Started</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE__STARTED = PictogramsPackage.CONTAINER_SHAPE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Update Ports</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE__UPDATE_PORTS = PictogramsPackage.CONTAINER_SHAPE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>IStatus Error State</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE__ISTATUS_ERROR_STATE = PictogramsPackage.CONTAINER_SHAPE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Connection Map</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE__CONNECTION_MAP = PictogramsPackage.CONTAINER_SHAPE_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Event</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE__EVENT = PictogramsPackage.CONTAINER_SHAPE_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Create Super Ports Container Shape</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE__CREATE_SUPER_PORTS_CONTAINER_SHAPE = PictogramsPackage.CONTAINER_SHAPE_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Create Ports Container Shape</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE__CREATE_PORTS_CONTAINER_SHAPE = PictogramsPackage.CONTAINER_SHAPE_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Hide Unused Ports</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE__HIDE_UNUSED_PORTS = PictogramsPackage.CONTAINER_SHAPE_FEATURE_COUNT + 7;

	/**
	 * The number of structural features of the '<em>RH Container Shape</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE_FEATURE_COUNT = PictogramsPackage.CONTAINER_SHAPE_FEATURE_COUNT + 8;

	/**
	 * The meta object id for the '{@link gov.redhawk.ide.graphiti.ext.Event <em>Event</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.graphiti.ext.Event
	 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getEvent()
	 * @generated
	 */
	int EVENT = 2;

	/**
	 * The meta object id for the '<em>Component Supported Interface Stub</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub
	 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getComponentSupportedInterfaceStub()
	 * @generated
	 */
	int COMPONENT_SUPPORTED_INTERFACE_STUB = 3;

	/**
	 * The meta object id for the '<em>IFeature Provider</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.graphiti.features.IFeatureProvider
	 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getIFeatureProvider()
	 * @generated
	 */
	int IFEATURE_PROVIDER = 4;

	/**
	 * The meta object id for the '<em>Uses Port Stub</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mil.jpeojtrs.sca.partitioning.UsesPortStub
	 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getUsesPortStub()
	 * @generated
	 */
	int USES_PORT_STUB = 5;

	/**
	 * The meta object id for the '<em>Provides Port Stub</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mil.jpeojtrs.sca.partitioning.ProvidesPortStub
	 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getProvidesPortStub()
	 * @generated
	 */
	int PROVIDES_PORT_STUB = 6;

	/**
	 * The meta object id for the '<em>Port</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mil.jpeojtrs.sca.sad.Port
	 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getPort()
	 * @generated
	 */
	int PORT = 7;

	/**
	 * The meta object id for the '<em>Assembly Controller</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mil.jpeojtrs.sca.sad.AssemblyController
	 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getAssemblyController()
	 * @generated
	 */
	int ASSEMBLY_CONTROLLER = 8;

	/**
	 * The meta object id for the '<em>External Ports</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mil.jpeojtrs.sca.sad.ExternalPorts
	 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getExternalPorts()
	 * @generated
	 */
	int EXTERNAL_PORTS = 9;

	/**
	 * The meta object id for the '<em>Reason</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.graphiti.features.impl.Reason
	 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getReason()
	 * @generated
	 */
	int REASON = 10;

	/**
	 * The meta object id for the '<em>List</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.util.List
	 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getList()
	 * @generated
	 */
	int LIST = 11;

	/**
	 * The meta object id for the '<em>Sad Component Instantiation</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mil.jpeojtrs.sca.sad.SadComponentInstantiation
	 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getSadComponentInstantiation()
	 * @generated
	 */
	int SAD_COMPONENT_INSTANTIATION = 12;

	/**
	 * The meta object id for the '<em>IAdd Context</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.graphiti.features.context.IAddContext
	 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getIAddContext()
	 * @generated
	 */
	int IADD_CONTEXT = 13;

	/**
	 * The meta object id for the '<em>Abstract Container Pattern</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.graphiti.ui.diagram.patterns.AbstractContainerPattern
	 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getAbstractContainerPattern()
	 * @generated
	 */
	int ABSTRACT_CONTAINER_PATTERN = 14;

	/**
	 * The meta object id for the '<em>IUpdate Context</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.graphiti.features.context.IUpdateContext
	 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getIUpdateContext()
	 * @generated
	 */
	int IUPDATE_CONTEXT = 15;

	/**
	 * The meta object id for the '<em>Map</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.util.Map
	 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getMap()
	 * @generated
	 */
	int MAP = 17;

	/**
	 * The meta object id for the '<em>IColor Constant</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.graphiti.util.IColorConstant
	 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getIColorConstant()
	 * @generated
	 */
	int ICOLOR_CONSTANT = 16;

	/**
	 * Returns the meta object for class '{@link org.eclipse.graphiti.mm.pictograms.ContainerShape <em>Container Shape Impl</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Container Shape Impl</em>'.
	 * @see org.eclipse.graphiti.mm.pictograms.ContainerShape
	 * @model instanceClass="org.eclipse.graphiti.mm.pictograms.ContainerShape"
	 * @generated
	 */
	EClass getContainerShapeImpl();

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.graphiti.ext.RHContainerShape <em>RH Container Shape</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>RH Container Shape</em>'.
	 * @see gov.redhawk.ide.graphiti.ext.RHContainerShape
	 * @generated
	 */
	EClass getRHContainerShape();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.graphiti.ext.RHContainerShape#isStarted <em>Started</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Started</em>'.
	 * @see gov.redhawk.ide.graphiti.ext.RHContainerShape#isStarted()
	 * @see #getRHContainerShape()
	 * @generated
	 */
	EAttribute getRHContainerShape_Started();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.graphiti.ext.RHContainerShape#isUpdatePorts <em>Update Ports</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Update Ports</em>'.
	 * @see gov.redhawk.ide.graphiti.ext.RHContainerShape#isUpdatePorts()
	 * @see #getRHContainerShape()
	 * @generated
	 */
	EAttribute getRHContainerShape_UpdatePorts();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.graphiti.ext.RHContainerShape#getIStatusErrorState <em>IStatus Error State</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>IStatus Error State</em>'.
	 * @see gov.redhawk.ide.graphiti.ext.RHContainerShape#getIStatusErrorState()
	 * @see #getRHContainerShape()
	 * @generated
	 */
	EAttribute getRHContainerShape_IStatusErrorState();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.graphiti.ext.RHContainerShape#getConnectionMap <em>Connection Map</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Connection Map</em>'.
	 * @see gov.redhawk.ide.graphiti.ext.RHContainerShape#getConnectionMap()
	 * @see #getRHContainerShape()
	 * @generated
	 */
	EAttribute getRHContainerShape_ConnectionMap();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.graphiti.ext.RHContainerShape#getEvent <em>Event</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Event</em>'.
	 * @see gov.redhawk.ide.graphiti.ext.RHContainerShape#getEvent()
	 * @see #getRHContainerShape()
	 * @generated
	 */
	EAttribute getRHContainerShape_Event();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.graphiti.ext.RHContainerShape#isCreateSuperPortsContainerShape <em>Create Super Ports Container Shape</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Create Super Ports Container Shape</em>'.
	 * @see gov.redhawk.ide.graphiti.ext.RHContainerShape#isCreateSuperPortsContainerShape()
	 * @see #getRHContainerShape()
	 * @generated
	 */
	EAttribute getRHContainerShape_CreateSuperPortsContainerShape();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.graphiti.ext.RHContainerShape#isCreatePortsContainerShape <em>Create Ports Container Shape</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Create Ports Container Shape</em>'.
	 * @see gov.redhawk.ide.graphiti.ext.RHContainerShape#isCreatePortsContainerShape()
	 * @see #getRHContainerShape()
	 * @generated
	 */
	EAttribute getRHContainerShape_CreatePortsContainerShape();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.graphiti.ext.RHContainerShape#isHideUnusedPorts <em>Hide Unused Ports</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Hide Unused Ports</em>'.
	 * @see gov.redhawk.ide.graphiti.ext.RHContainerShape#isHideUnusedPorts()
	 * @see #getRHContainerShape()
	 * @generated
	 */
	EAttribute getRHContainerShape_HideUnusedPorts();

	/**
	 * Returns the meta object for enum '{@link gov.redhawk.ide.graphiti.ext.Event <em>Event</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Event</em>'.
	 * @see gov.redhawk.ide.graphiti.ext.Event
	 * @generated
	 */
	EEnum getEvent();

	/**
	 * Returns the meta object for data type '{@link mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub <em>Component Supported Interface Stub</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Component Supported Interface Stub</em>'.
	 * @see mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub
	 * @model instanceClass="mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub"
	 * @generated
	 */
	EDataType getComponentSupportedInterfaceStub();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.graphiti.features.IFeatureProvider <em>IFeature Provider</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>IFeature Provider</em>'.
	 * @see org.eclipse.graphiti.features.IFeatureProvider
	 * @model instanceClass="org.eclipse.graphiti.features.IFeatureProvider"
	 * @generated
	 */
	EDataType getIFeatureProvider();

	/**
	 * Returns the meta object for data type '{@link mil.jpeojtrs.sca.partitioning.UsesPortStub <em>Uses Port Stub</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Uses Port Stub</em>'.
	 * @see mil.jpeojtrs.sca.partitioning.UsesPortStub
	 * @model instanceClass="mil.jpeojtrs.sca.partitioning.UsesPortStub"
	 * @generated
	 */
	EDataType getUsesPortStub();

	/**
	 * Returns the meta object for data type '{@link mil.jpeojtrs.sca.partitioning.ProvidesPortStub <em>Provides Port Stub</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Provides Port Stub</em>'.
	 * @see mil.jpeojtrs.sca.partitioning.ProvidesPortStub
	 * @model instanceClass="mil.jpeojtrs.sca.partitioning.ProvidesPortStub"
	 * @generated
	 */
	EDataType getProvidesPortStub();

	/**
	 * Returns the meta object for data type '{@link mil.jpeojtrs.sca.sad.Port <em>Port</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Port</em>'.
	 * @see mil.jpeojtrs.sca.sad.Port
	 * @model instanceClass="mil.jpeojtrs.sca.sad.Port"
	 * @generated
	 */
	EDataType getPort();

	/**
	 * Returns the meta object for data type '{@link mil.jpeojtrs.sca.sad.AssemblyController <em>Assembly Controller</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Assembly Controller</em>'.
	 * @see mil.jpeojtrs.sca.sad.AssemblyController
	 * @model instanceClass="mil.jpeojtrs.sca.sad.AssemblyController"
	 * @generated
	 */
	EDataType getAssemblyController();

	/**
	 * Returns the meta object for data type '{@link mil.jpeojtrs.sca.sad.ExternalPorts <em>External Ports</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>External Ports</em>'.
	 * @see mil.jpeojtrs.sca.sad.ExternalPorts
	 * @model instanceClass="mil.jpeojtrs.sca.sad.ExternalPorts"
	 * @generated
	 */
	EDataType getExternalPorts();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.graphiti.features.impl.Reason <em>Reason</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Reason</em>'.
	 * @see org.eclipse.graphiti.features.impl.Reason
	 * @model instanceClass="org.eclipse.graphiti.features.impl.Reason"
	 * @generated
	 */
	EDataType getReason();

	/**
	 * Returns the meta object for data type '{@link java.util.List <em>List</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>List</em>'.
	 * @see java.util.List
	 * @model instanceClass="java.util.List" typeParameters="Port"
	 * @generated
	 */
	EDataType getList();

	/**
	 * Returns the meta object for data type '{@link mil.jpeojtrs.sca.sad.SadComponentInstantiation <em>Sad Component Instantiation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Sad Component Instantiation</em>'.
	 * @see mil.jpeojtrs.sca.sad.SadComponentInstantiation
	 * @model instanceClass="mil.jpeojtrs.sca.sad.SadComponentInstantiation"
	 * @generated
	 */
	EDataType getSadComponentInstantiation();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.graphiti.features.context.IAddContext <em>IAdd Context</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>IAdd Context</em>'.
	 * @see org.eclipse.graphiti.features.context.IAddContext
	 * @model instanceClass="org.eclipse.graphiti.features.context.IAddContext"
	 * @generated
	 */
	EDataType getIAddContext();

	/**
	 * Returns the meta object for data type '{@link gov.redhawk.ide.graphiti.ui.diagram.patterns.AbstractContainerPattern <em>Abstract Container Pattern</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Abstract Container Pattern</em>'.
	 * @see gov.redhawk.ide.graphiti.ui.diagram.patterns.AbstractContainerPattern
	 * @model instanceClass="gov.redhawk.ide.graphiti.ui.diagram.patterns.AbstractContainerPattern"
	 * @generated
	 */
	EDataType getAbstractContainerPattern();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.graphiti.features.context.IUpdateContext <em>IUpdate Context</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>IUpdate Context</em>'.
	 * @see org.eclipse.graphiti.features.context.IUpdateContext
	 * @model instanceClass="org.eclipse.graphiti.features.context.IUpdateContext"
	 * @generated
	 */
	EDataType getIUpdateContext();

	/**
	 * Returns the meta object for data type '{@link java.util.Map <em>Map</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Map</em>'.
	 * @see java.util.Map
	 * @model instanceClass="java.util.Map<java.lang.String, org.eclipse.graphiti.util.IColorConstant>"
	 * @generated
	 */
	EDataType getMap();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.graphiti.util.IColorConstant <em>IColor Constant</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>IColor Constant</em>'.
	 * @see org.eclipse.graphiti.util.IColorConstant
	 * @model instanceClass="org.eclipse.graphiti.util.IColorConstant"
	 * @generated
	 */
	EDataType getIColorConstant();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	RHGxFactory getRHGxFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.eclipse.graphiti.mm.pictograms.ContainerShape <em>Container Shape Impl</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.graphiti.mm.pictograms.ContainerShape
		 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getContainerShapeImpl()
		 * @generated
		 */
		EClass CONTAINER_SHAPE_IMPL = eINSTANCE.getContainerShapeImpl();

		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl <em>RH Container Shape</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl
		 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getRHContainerShape()
		 * @generated
		 */
		EClass RH_CONTAINER_SHAPE = eINSTANCE.getRHContainerShape();

		/**
		 * The meta object literal for the '<em><b>Started</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RH_CONTAINER_SHAPE__STARTED = eINSTANCE.getRHContainerShape_Started();

		/**
		 * The meta object literal for the '<em><b>Update Ports</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RH_CONTAINER_SHAPE__UPDATE_PORTS = eINSTANCE.getRHContainerShape_UpdatePorts();

		/**
		 * The meta object literal for the '<em><b>IStatus Error State</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RH_CONTAINER_SHAPE__ISTATUS_ERROR_STATE = eINSTANCE.getRHContainerShape_IStatusErrorState();

		/**
		 * The meta object literal for the '<em><b>Connection Map</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RH_CONTAINER_SHAPE__CONNECTION_MAP = eINSTANCE.getRHContainerShape_ConnectionMap();

		/**
		 * The meta object literal for the '<em><b>Event</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RH_CONTAINER_SHAPE__EVENT = eINSTANCE.getRHContainerShape_Event();

		/**
		 * The meta object literal for the '<em><b>Create Super Ports Container Shape</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RH_CONTAINER_SHAPE__CREATE_SUPER_PORTS_CONTAINER_SHAPE = eINSTANCE.getRHContainerShape_CreateSuperPortsContainerShape();

		/**
		 * The meta object literal for the '<em><b>Create Ports Container Shape</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RH_CONTAINER_SHAPE__CREATE_PORTS_CONTAINER_SHAPE = eINSTANCE.getRHContainerShape_CreatePortsContainerShape();

		/**
		 * The meta object literal for the '<em><b>Hide Unused Ports</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RH_CONTAINER_SHAPE__HIDE_UNUSED_PORTS = eINSTANCE.getRHContainerShape_HideUnusedPorts();

		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.graphiti.ext.Event <em>Event</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.graphiti.ext.Event
		 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getEvent()
		 * @generated
		 */
		EEnum EVENT = eINSTANCE.getEvent();

		/**
		 * The meta object literal for the '<em>Component Supported Interface Stub</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub
		 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getComponentSupportedInterfaceStub()
		 * @generated
		 */
		EDataType COMPONENT_SUPPORTED_INTERFACE_STUB = eINSTANCE.getComponentSupportedInterfaceStub();

		/**
		 * The meta object literal for the '<em>IFeature Provider</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.graphiti.features.IFeatureProvider
		 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getIFeatureProvider()
		 * @generated
		 */
		EDataType IFEATURE_PROVIDER = eINSTANCE.getIFeatureProvider();

		/**
		 * The meta object literal for the '<em>Uses Port Stub</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mil.jpeojtrs.sca.partitioning.UsesPortStub
		 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getUsesPortStub()
		 * @generated
		 */
		EDataType USES_PORT_STUB = eINSTANCE.getUsesPortStub();

		/**
		 * The meta object literal for the '<em>Provides Port Stub</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mil.jpeojtrs.sca.partitioning.ProvidesPortStub
		 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getProvidesPortStub()
		 * @generated
		 */
		EDataType PROVIDES_PORT_STUB = eINSTANCE.getProvidesPortStub();

		/**
		 * The meta object literal for the '<em>Port</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mil.jpeojtrs.sca.sad.Port
		 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getPort()
		 * @generated
		 */
		EDataType PORT = eINSTANCE.getPort();

		/**
		 * The meta object literal for the '<em>Assembly Controller</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mil.jpeojtrs.sca.sad.AssemblyController
		 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getAssemblyController()
		 * @generated
		 */
		EDataType ASSEMBLY_CONTROLLER = eINSTANCE.getAssemblyController();

		/**
		 * The meta object literal for the '<em>External Ports</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mil.jpeojtrs.sca.sad.ExternalPorts
		 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getExternalPorts()
		 * @generated
		 */
		EDataType EXTERNAL_PORTS = eINSTANCE.getExternalPorts();

		/**
		 * The meta object literal for the '<em>Reason</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.graphiti.features.impl.Reason
		 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getReason()
		 * @generated
		 */
		EDataType REASON = eINSTANCE.getReason();

		/**
		 * The meta object literal for the '<em>List</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.util.List
		 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getList()
		 * @generated
		 */
		EDataType LIST = eINSTANCE.getList();

		/**
		 * The meta object literal for the '<em>Sad Component Instantiation</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mil.jpeojtrs.sca.sad.SadComponentInstantiation
		 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getSadComponentInstantiation()
		 * @generated
		 */
		EDataType SAD_COMPONENT_INSTANTIATION = eINSTANCE.getSadComponentInstantiation();

		/**
		 * The meta object literal for the '<em>IAdd Context</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.graphiti.features.context.IAddContext
		 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getIAddContext()
		 * @generated
		 */
		EDataType IADD_CONTEXT = eINSTANCE.getIAddContext();

		/**
		 * The meta object literal for the '<em>Abstract Container Pattern</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.graphiti.ui.diagram.patterns.AbstractContainerPattern
		 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getAbstractContainerPattern()
		 * @generated
		 */
		EDataType ABSTRACT_CONTAINER_PATTERN = eINSTANCE.getAbstractContainerPattern();

		/**
		 * The meta object literal for the '<em>IUpdate Context</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.graphiti.features.context.IUpdateContext
		 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getIUpdateContext()
		 * @generated
		 */
		EDataType IUPDATE_CONTEXT = eINSTANCE.getIUpdateContext();

		/**
		 * The meta object literal for the '<em>Map</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.util.Map
		 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getMap()
		 * @generated
		 */
		EDataType MAP = eINSTANCE.getMap();

		/**
		 * The meta object literal for the '<em>IColor Constant</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.graphiti.util.IColorConstant
		 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getIColorConstant()
		 * @generated
		 */
		EDataType ICOLOR_CONSTANT = eINSTANCE.getIColorConstant();

	}

} //RHGxPackage
