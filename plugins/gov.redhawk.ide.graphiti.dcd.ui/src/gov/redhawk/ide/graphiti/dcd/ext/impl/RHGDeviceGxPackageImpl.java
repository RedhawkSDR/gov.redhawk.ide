/**
 */
package gov.redhawk.ide.graphiti.dcd.ext.impl;

import gov.redhawk.ide.graphiti.dcd.ext.DeviceShape;
import gov.redhawk.ide.graphiti.dcd.ext.RHGDeviceGxFactory;
import gov.redhawk.ide.graphiti.dcd.ext.RHGDeviceGxPackage;
import gov.redhawk.ide.graphiti.dcd.ext.ServiceShape;

import gov.redhawk.ide.graphiti.ext.RHGxPackage;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class RHGDeviceGxPackageImpl extends EPackageImpl implements RHGDeviceGxPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass deviceShapeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass serviceShapeEClass = null;

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
	 * @see gov.redhawk.ide.graphiti.dcd.ext.RHGDeviceGxPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private RHGDeviceGxPackageImpl() {
		super(eNS_URI, RHGDeviceGxFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link RHGDeviceGxPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static RHGDeviceGxPackage init() {
		if (isInited)
			return (RHGDeviceGxPackage) EPackage.Registry.INSTANCE.getEPackage(RHGDeviceGxPackage.eNS_URI);

		// Obtain or create and register package
		RHGDeviceGxPackageImpl theRHGDeviceGxPackage = (RHGDeviceGxPackageImpl) (EPackage.Registry.INSTANCE.get(eNS_URI) instanceof RHGDeviceGxPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI)
			: new RHGDeviceGxPackageImpl());

		isInited = true;

		// Initialize simple dependencies
		RHGxPackage.eINSTANCE.eClass();

		// Create package meta-data objects
		theRHGDeviceGxPackage.createPackageContents();

		// Initialize created meta-data
		theRHGDeviceGxPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theRHGDeviceGxPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(RHGDeviceGxPackage.eNS_URI, theRHGDeviceGxPackage);
		return theRHGDeviceGxPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDeviceShape() {
		return deviceShapeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getServiceShape() {
		return serviceShapeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RHGDeviceGxFactory getRHGDeviceGxFactory() {
		return (RHGDeviceGxFactory) getEFactoryInstance();
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
		deviceShapeEClass = createEClass(DEVICE_SHAPE);

		serviceShapeEClass = createEClass(SERVICE_SHAPE);
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

		// Set bounds for type parameters

		// Add supertypes to classes
		deviceShapeEClass.getESuperTypes().add(theRHGxPackage.getRHContainerShape());
		serviceShapeEClass.getESuperTypes().add(theRHGxPackage.getRHContainerShape());

		// Initialize classes and features; add operations and parameters
		initEClass(deviceShapeEClass, DeviceShape.class, "DeviceShape", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(serviceShapeEClass, ServiceShape.class, "ServiceShape", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		// Create resource
		createResource(eNS_URI);
	}

} //RHGDeviceGxPackageImpl
