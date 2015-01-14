/**
 */
package gov.redhawk.ide.sad.graphiti.ext.impl;

import gov.redhawk.ide.graphiti.ext.RHGxPackage;

import gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.ComponentPattern;

import gov.redhawk.ide.sad.graphiti.ext.ComponentShape;
import gov.redhawk.ide.sad.graphiti.ext.RHSadGxFactory;
import gov.redhawk.ide.sad.graphiti.ext.RHSadGxPackage;

import java.util.List;

import mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;

import mil.jpeojtrs.sca.sad.AssemblyController;
import mil.jpeojtrs.sca.sad.ExternalPorts;
import mil.jpeojtrs.sca.sad.Port;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.graphiti.features.IFeatureProvider;

import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IUpdateContext;

import org.eclipse.graphiti.features.impl.Reason;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class RHSadGxPackageImpl extends EPackageImpl implements RHSadGxPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass componentShapeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType componentSupportedInterfaceStubEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType iFeatureProviderEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType usesPortStubEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType providesPortStubEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType portEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType assemblyControllerEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType externalPortsEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType reasonEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType listEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType sadComponentInstantiationEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType iAddContextEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType componentPatternEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType iUpdateContextEDataType = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see gov.redhawk.ide.sad.graphiti.ext.RHSadGxPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private RHSadGxPackageImpl() {
		super(eNS_URI, RHSadGxFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 * 
	 * <p>This method is used to initialize {@link RHSadGxPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static RHSadGxPackage init() {
		if (isInited)
			return (RHSadGxPackage) EPackage.Registry.INSTANCE.getEPackage(RHSadGxPackage.eNS_URI);

		// Obtain or create and register package
		RHSadGxPackageImpl theRHSadGxPackage = (RHSadGxPackageImpl) (EPackage.Registry.INSTANCE.get(eNS_URI) instanceof RHSadGxPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI)
			: new RHSadGxPackageImpl());

		isInited = true;

		// Initialize simple dependencies
		RHGxPackage.eINSTANCE.eClass();

		// Create package meta-data objects
		theRHSadGxPackage.createPackageContents();

		// Initialize created meta-data
		theRHSadGxPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theRHSadGxPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(RHSadGxPackage.eNS_URI, theRHSadGxPackage);
		return theRHSadGxPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getComponentShape() {
		return componentShapeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getComponentSupportedInterfaceStub() {
		return componentSupportedInterfaceStubEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getIFeatureProvider() {
		return iFeatureProviderEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getUsesPortStub() {
		return usesPortStubEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getProvidesPortStub() {
		return providesPortStubEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getPort() {
		return portEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getAssemblyController() {
		return assemblyControllerEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getExternalPorts() {
		return externalPortsEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getReason() {
		return reasonEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getList() {
		return listEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getSadComponentInstantiation() {
		return sadComponentInstantiationEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getIAddContext() {
		return iAddContextEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getComponentPattern() {
		return componentPatternEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getIUpdateContext() {
		return iUpdateContextEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RHSadGxFactory getRHSadGxFactory() {
		return (RHSadGxFactory) getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated)
			return;
		isCreated = true;

		// Create classes and their features
		componentShapeEClass = createEClass(COMPONENT_SHAPE);

		// Create data types
		componentSupportedInterfaceStubEDataType = createEDataType(COMPONENT_SUPPORTED_INTERFACE_STUB);
		iFeatureProviderEDataType = createEDataType(IFEATURE_PROVIDER);
		usesPortStubEDataType = createEDataType(USES_PORT_STUB);
		providesPortStubEDataType = createEDataType(PROVIDES_PORT_STUB);
		portEDataType = createEDataType(PORT);
		assemblyControllerEDataType = createEDataType(ASSEMBLY_CONTROLLER);
		externalPortsEDataType = createEDataType(EXTERNAL_PORTS);
		reasonEDataType = createEDataType(REASON);
		listEDataType = createEDataType(LIST);
		sadComponentInstantiationEDataType = createEDataType(SAD_COMPONENT_INSTANTIATION);
		iAddContextEDataType = createEDataType(IADD_CONTEXT);
		componentPatternEDataType = createEDataType(COMPONENT_PATTERN);
		iUpdateContextEDataType = createEDataType(IUPDATE_CONTEXT);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized)
			return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Obtain other dependent packages
		RHGxPackage theRHGxPackage = (RHGxPackage) EPackage.Registry.INSTANCE.getEPackage(RHGxPackage.eNS_URI);

		// Create type parameters
		addETypeParameter(listEDataType, "Port");

		// Set bounds for type parameters

		// Add supertypes to classes
		componentShapeEClass.getESuperTypes().add(theRHGxPackage.getRHContainerShape());

		// Initialize classes and features; add operations and parameters
		initEClass(componentShapeEClass, ComponentShape.class, "ComponentShape", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		EOperation op = addEOperation(componentShapeEClass, null, "init", 0, 1, !IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getIAddContext(), "context", 0, 1, !IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getComponentPattern(), "pattern", 0, 1, !IS_UNIQUE, IS_ORDERED);

		addEOperation(componentShapeEClass, null, "layout", 0, 1, !IS_UNIQUE, IS_ORDERED);

		op = addEOperation(componentShapeEClass, this.getReason(), "update", 0, 1, !IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getIUpdateContext(), "context", 0, 1, !IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getComponentPattern(), "pattern", 0, 1, !IS_UNIQUE, IS_ORDERED);

		op = addEOperation(componentShapeEClass, this.getReason(), "updateNeeded", 0, 1, !IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getIUpdateContext(), "context", 0, 1, !IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getComponentPattern(), "pattern", 0, 1, !IS_UNIQUE, IS_ORDERED);

		// Initialize data types
		initEDataType(componentSupportedInterfaceStubEDataType, ComponentSupportedInterfaceStub.class, "ComponentSupportedInterfaceStub", IS_SERIALIZABLE,
			!IS_GENERATED_INSTANCE_CLASS);
		initEDataType(iFeatureProviderEDataType, IFeatureProvider.class, "IFeatureProvider", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(usesPortStubEDataType, UsesPortStub.class, "UsesPortStub", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(providesPortStubEDataType, ProvidesPortStub.class, "ProvidesPortStub", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(portEDataType, Port.class, "Port", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(assemblyControllerEDataType, AssemblyController.class, "AssemblyController", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(externalPortsEDataType, ExternalPorts.class, "ExternalPorts", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(reasonEDataType, Reason.class, "Reason", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(listEDataType, List.class, "List", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(sadComponentInstantiationEDataType, SadComponentInstantiation.class, "SadComponentInstantiation", IS_SERIALIZABLE,
			!IS_GENERATED_INSTANCE_CLASS);
		initEDataType(iAddContextEDataType, IAddContext.class, "IAddContext", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(componentPatternEDataType, ComponentPattern.class, "ComponentPattern", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(iUpdateContextEDataType, IUpdateContext.class, "IUpdateContext", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

		// Create resource
		createResource(eNS_URI);

		// Create annotations
		// http://www.eclipse.org/emf/2011/Xcore
		createXcoreAnnotations();
	}

	/**
	 * Initializes the annotations for <b>http://www.eclipse.org/emf/2011/Xcore</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createXcoreAnnotations() {
		String source = "http://www.eclipse.org/emf/2011/Xcore";
		addAnnotation(this, source, new String[] { "GenModel", "http://www.eclipse.org/emf/2002/GenModel", "Ecore", "http://www.eclipse.org/emf/2002/Ecore" });
	}

} //RHSadGxPackageImpl
