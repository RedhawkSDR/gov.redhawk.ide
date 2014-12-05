/**
 */
package gov.redhawk.ide.graphiti.dcd.ext.impl;

import gov.redhawk.ide.graphiti.dcd.ext.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class RHGDeviceGxFactoryImpl extends EFactoryImpl implements RHGDeviceGxFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static RHGDeviceGxFactory init() {
		try {
			RHGDeviceGxFactory theRHGDeviceGxFactory = (RHGDeviceGxFactory) EPackage.Registry.INSTANCE.getEFactory(RHGDeviceGxPackage.eNS_URI);
			if (theRHGDeviceGxFactory != null) {
				return theRHGDeviceGxFactory;
			}
		} catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new RHGDeviceGxFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RHGDeviceGxFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
		case RHGDeviceGxPackage.DEVICE_SHAPE:
			return createDeviceShape();
		case RHGDeviceGxPackage.SERVICE_SHAPE:
			return createServiceShape();
		default:
			throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DeviceShape createDeviceShape() {
		DeviceShapeImpl deviceShape = new DeviceShapeImpl();
		return deviceShape;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ServiceShape createServiceShape() {
		ServiceShapeImpl serviceShape = new ServiceShapeImpl();
		return serviceShape;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RHGDeviceGxPackage getRHGDeviceGxPackage() {
		return (RHGDeviceGxPackage) getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static RHGDeviceGxPackage getPackage() {
		return RHGDeviceGxPackage.eINSTANCE;
	}

} //RHGDeviceGxFactoryImpl
