/**
 */
package gov.redhawk.ide.sad.graphiti.ext;

import gov.redhawk.ide.graphiti.ext.RHGxPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;

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
 * @see gov.redhawk.ide.sad.graphiti.ext.RHSadGxFactory
 * @model kind="package"
 *        annotation="http://www.eclipse.org/emf/2002/GenModel prefix='RHSadGx' codeFormatting='true' modelPluginVariables='org.eclipse.xtext.xbase.lib' contentTypeIdentifier='http://www.redhawk.gov/model/rhsadgext/1.0.0' operationReflection='false' modelDirectory='/gov.redhawk.ide.sad.graphiti.ui/src' basePackage='gov.redhawk.ide.sad.graphiti'"
 *        annotation="http://www.eclipse.org/emf/2011/Xcore GenModel='http://www.eclipse.org/emf/2002/GenModel' Ecore='http://www.eclipse.org/emf/2002/Ecore'"
 * @generated
 */
public interface RHSadGxPackage extends EPackage {
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
	String eNS_URI = "http://www.redhawk.gov/model/rhsadgext/1.0.0";

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
	String eCONTENT_TYPE = "http://www.redhawk.gov/model/rhsadgext/1.0.0";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	RHSadGxPackage eINSTANCE = gov.redhawk.ide.sad.graphiti.ext.impl.RHSadGxPackageImpl.init();

	/**
	 * The meta object id for the '{@link gov.redhawk.ide.sad.graphiti.ext.impl.ComponentShapeImpl <em>Component Shape</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.sad.graphiti.ext.impl.ComponentShapeImpl
	 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHSadGxPackageImpl#getComponentShape()
	 * @generated
	 */
	int COMPONENT_SHAPE = 0;

	/**
	 * The feature id for the '<em><b>Started</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_SHAPE__STARTED = RHGxPackage.RH_CONTAINER_SHAPE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Event</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_SHAPE__EVENT = RHGxPackage.RH_CONTAINER_SHAPE_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Component Shape</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_SHAPE_FEATURE_COUNT = RHGxPackage.RH_CONTAINER_SHAPE_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link gov.redhawk.ide.sad.graphiti.ext.Event <em>Event</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.sad.graphiti.ext.Event
	 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHSadGxPackageImpl#getEvent()
	 * @generated
	 */
	int EVENT = 1;

	/**
	 * The meta object id for the '<em>Component Supported Interface Stub</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub
	 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHSadGxPackageImpl#getComponentSupportedInterfaceStub()
	 * @generated
	 */
	int COMPONENT_SUPPORTED_INTERFACE_STUB = 2;

	/**
	 * The meta object id for the '<em>IFeature Provider</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.graphiti.features.IFeatureProvider
	 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHSadGxPackageImpl#getIFeatureProvider()
	 * @generated
	 */
	int IFEATURE_PROVIDER = 3;

	/**
	 * The meta object id for the '<em>Uses Port Stub</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mil.jpeojtrs.sca.partitioning.UsesPortStub
	 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHSadGxPackageImpl#getUsesPortStub()
	 * @generated
	 */
	int USES_PORT_STUB = 4;

	/**
	 * The meta object id for the '<em>Provides Port Stub</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mil.jpeojtrs.sca.partitioning.ProvidesPortStub
	 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHSadGxPackageImpl#getProvidesPortStub()
	 * @generated
	 */
	int PROVIDES_PORT_STUB = 5;

	/**
	 * The meta object id for the '<em>Port</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mil.jpeojtrs.sca.sad.Port
	 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHSadGxPackageImpl#getPort()
	 * @generated
	 */
	int PORT = 6;

	/**
	 * The meta object id for the '<em>Assembly Controller</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mil.jpeojtrs.sca.sad.AssemblyController
	 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHSadGxPackageImpl#getAssemblyController()
	 * @generated
	 */
	int ASSEMBLY_CONTROLLER = 7;

	/**
	 * The meta object id for the '<em>External Ports</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mil.jpeojtrs.sca.sad.ExternalPorts
	 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHSadGxPackageImpl#getExternalPorts()
	 * @generated
	 */
	int EXTERNAL_PORTS = 8;

	/**
	 * The meta object id for the '<em>Reason</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.graphiti.features.impl.Reason
	 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHSadGxPackageImpl#getReason()
	 * @generated
	 */
	int REASON = 9;

	/**
	 * The meta object id for the '<em>List</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.util.List
	 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHSadGxPackageImpl#getList()
	 * @generated
	 */
	int LIST = 10;

	/**
	 * The meta object id for the '<em>Sad Component Instantiation</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mil.jpeojtrs.sca.sad.SadComponentInstantiation
	 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHSadGxPackageImpl#getSadComponentInstantiation()
	 * @generated
	 */
	int SAD_COMPONENT_INSTANTIATION = 11;

	/**
	 * The meta object id for the '<em>IAdd Context</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.graphiti.features.context.IAddContext
	 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHSadGxPackageImpl#getIAddContext()
	 * @generated
	 */
	int IADD_CONTEXT = 12;

	/**
	 * The meta object id for the '<em>Component Pattern</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.ComponentPattern
	 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHSadGxPackageImpl#getComponentPattern()
	 * @generated
	 */
	int COMPONENT_PATTERN = 13;

	/**
	 * The meta object id for the '<em>IUpdate Context</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.graphiti.features.context.IUpdateContext
	 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHSadGxPackageImpl#getIUpdateContext()
	 * @generated
	 */
	int IUPDATE_CONTEXT = 14;

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.sad.graphiti.ext.ComponentShape <em>Component Shape</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Component Shape</em>'.
	 * @see gov.redhawk.ide.sad.graphiti.ext.ComponentShape
	 * @generated
	 */
	EClass getComponentShape();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.sad.graphiti.ext.ComponentShape#isStarted <em>Started</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Started</em>'.
	 * @see gov.redhawk.ide.sad.graphiti.ext.ComponentShape#isStarted()
	 * @see #getComponentShape()
	 * @generated
	 */
	EAttribute getComponentShape_Started();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.sad.graphiti.ext.ComponentShape#getEvent <em>Event</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Event</em>'.
	 * @see gov.redhawk.ide.sad.graphiti.ext.ComponentShape#getEvent()
	 * @see #getComponentShape()
	 * @generated
	 */
	EAttribute getComponentShape_Event();

	/**
	 * Returns the meta object for enum '{@link gov.redhawk.ide.sad.graphiti.ext.Event <em>Event</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Event</em>'.
	 * @see gov.redhawk.ide.sad.graphiti.ext.Event
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
	 * Returns the meta object for data type '{@link gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.ComponentPattern <em>Component Pattern</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Component Pattern</em>'.
	 * @see gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.ComponentPattern
	 * @model instanceClass="gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.ComponentPattern"
	 * @generated
	 */
	EDataType getComponentPattern();

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
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	RHSadGxFactory getRHSadGxFactory();

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
		 * The meta object literal for the '{@link gov.redhawk.ide.sad.graphiti.ext.impl.ComponentShapeImpl <em>Component Shape</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.sad.graphiti.ext.impl.ComponentShapeImpl
		 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHSadGxPackageImpl#getComponentShape()
		 * @generated
		 */
		EClass COMPONENT_SHAPE = eINSTANCE.getComponentShape();

		/**
		 * The meta object literal for the '<em><b>Started</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute COMPONENT_SHAPE__STARTED = eINSTANCE.getComponentShape_Started();

		/**
		 * The meta object literal for the '<em><b>Event</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute COMPONENT_SHAPE__EVENT = eINSTANCE.getComponentShape_Event();

		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.sad.graphiti.ext.Event <em>Event</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.sad.graphiti.ext.Event
		 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHSadGxPackageImpl#getEvent()
		 * @generated
		 */
		EEnum EVENT = eINSTANCE.getEvent();

		/**
		 * The meta object literal for the '<em>Component Supported Interface Stub</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub
		 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHSadGxPackageImpl#getComponentSupportedInterfaceStub()
		 * @generated
		 */
		EDataType COMPONENT_SUPPORTED_INTERFACE_STUB = eINSTANCE.getComponentSupportedInterfaceStub();

		/**
		 * The meta object literal for the '<em>IFeature Provider</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.graphiti.features.IFeatureProvider
		 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHSadGxPackageImpl#getIFeatureProvider()
		 * @generated
		 */
		EDataType IFEATURE_PROVIDER = eINSTANCE.getIFeatureProvider();

		/**
		 * The meta object literal for the '<em>Uses Port Stub</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mil.jpeojtrs.sca.partitioning.UsesPortStub
		 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHSadGxPackageImpl#getUsesPortStub()
		 * @generated
		 */
		EDataType USES_PORT_STUB = eINSTANCE.getUsesPortStub();

		/**
		 * The meta object literal for the '<em>Provides Port Stub</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mil.jpeojtrs.sca.partitioning.ProvidesPortStub
		 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHSadGxPackageImpl#getProvidesPortStub()
		 * @generated
		 */
		EDataType PROVIDES_PORT_STUB = eINSTANCE.getProvidesPortStub();

		/**
		 * The meta object literal for the '<em>Port</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mil.jpeojtrs.sca.sad.Port
		 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHSadGxPackageImpl#getPort()
		 * @generated
		 */
		EDataType PORT = eINSTANCE.getPort();

		/**
		 * The meta object literal for the '<em>Assembly Controller</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mil.jpeojtrs.sca.sad.AssemblyController
		 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHSadGxPackageImpl#getAssemblyController()
		 * @generated
		 */
		EDataType ASSEMBLY_CONTROLLER = eINSTANCE.getAssemblyController();

		/**
		 * The meta object literal for the '<em>External Ports</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mil.jpeojtrs.sca.sad.ExternalPorts
		 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHSadGxPackageImpl#getExternalPorts()
		 * @generated
		 */
		EDataType EXTERNAL_PORTS = eINSTANCE.getExternalPorts();

		/**
		 * The meta object literal for the '<em>Reason</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.graphiti.features.impl.Reason
		 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHSadGxPackageImpl#getReason()
		 * @generated
		 */
		EDataType REASON = eINSTANCE.getReason();

		/**
		 * The meta object literal for the '<em>List</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.util.List
		 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHSadGxPackageImpl#getList()
		 * @generated
		 */
		EDataType LIST = eINSTANCE.getList();

		/**
		 * The meta object literal for the '<em>Sad Component Instantiation</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mil.jpeojtrs.sca.sad.SadComponentInstantiation
		 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHSadGxPackageImpl#getSadComponentInstantiation()
		 * @generated
		 */
		EDataType SAD_COMPONENT_INSTANTIATION = eINSTANCE.getSadComponentInstantiation();

		/**
		 * The meta object literal for the '<em>IAdd Context</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.graphiti.features.context.IAddContext
		 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHSadGxPackageImpl#getIAddContext()
		 * @generated
		 */
		EDataType IADD_CONTEXT = eINSTANCE.getIAddContext();

		/**
		 * The meta object literal for the '<em>Component Pattern</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.ComponentPattern
		 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHSadGxPackageImpl#getComponentPattern()
		 * @generated
		 */
		EDataType COMPONENT_PATTERN = eINSTANCE.getComponentPattern();

		/**
		 * The meta object literal for the '<em>IUpdate Context</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.graphiti.features.context.IUpdateContext
		 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHSadGxPackageImpl#getIUpdateContext()
		 * @generated
		 */
		EDataType IUPDATE_CONTEXT = eINSTANCE.getIUpdateContext();

	}

} //RHSadGxPackage
