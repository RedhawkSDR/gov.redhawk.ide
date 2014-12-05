/**
 */
package gov.redhawk.ide.sad.graphiti.ext.impl;

import gov.redhawk.ide.sad.graphiti.ext.*;
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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class RHSadGxFactoryImpl extends EFactoryImpl implements RHSadGxFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static RHSadGxFactory init() {
		try {
			RHSadGxFactory theRHSadGxFactory = (RHSadGxFactory) EPackage.Registry.INSTANCE.getEFactory(RHSadGxPackage.eNS_URI);
			if (theRHSadGxFactory != null) {
				return theRHSadGxFactory;
			}
		} catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new RHSadGxFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RHSadGxFactoryImpl() {
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
		case RHSadGxPackage.COMPONENT_SHAPE:
			return createComponentShape();
		default:
			throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
		case RHSadGxPackage.EVENT:
			return createEventFromString(eDataType, initialValue);
		case RHSadGxPackage.COMPONENT_SUPPORTED_INTERFACE_STUB:
			return createComponentSupportedInterfaceStubFromString(eDataType, initialValue);
		case RHSadGxPackage.IFEATURE_PROVIDER:
			return createIFeatureProviderFromString(eDataType, initialValue);
		case RHSadGxPackage.USES_PORT_STUB:
			return createUsesPortStubFromString(eDataType, initialValue);
		case RHSadGxPackage.PROVIDES_PORT_STUB:
			return createProvidesPortStubFromString(eDataType, initialValue);
		case RHSadGxPackage.PORT:
			return createPortFromString(eDataType, initialValue);
		case RHSadGxPackage.ASSEMBLY_CONTROLLER:
			return createAssemblyControllerFromString(eDataType, initialValue);
		case RHSadGxPackage.EXTERNAL_PORTS:
			return createExternalPortsFromString(eDataType, initialValue);
		case RHSadGxPackage.REASON:
			return createReasonFromString(eDataType, initialValue);
		case RHSadGxPackage.LIST:
			return createListFromString(eDataType, initialValue);
		case RHSadGxPackage.SAD_COMPONENT_INSTANTIATION:
			return createSadComponentInstantiationFromString(eDataType, initialValue);
		case RHSadGxPackage.IADD_CONTEXT:
			return createIAddContextFromString(eDataType, initialValue);
		case RHSadGxPackage.COMPONENT_PATTERN:
			return createComponentPatternFromString(eDataType, initialValue);
		case RHSadGxPackage.IUPDATE_CONTEXT:
			return createIUpdateContextFromString(eDataType, initialValue);
		default:
			throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
		case RHSadGxPackage.EVENT:
			return convertEventToString(eDataType, instanceValue);
		case RHSadGxPackage.COMPONENT_SUPPORTED_INTERFACE_STUB:
			return convertComponentSupportedInterfaceStubToString(eDataType, instanceValue);
		case RHSadGxPackage.IFEATURE_PROVIDER:
			return convertIFeatureProviderToString(eDataType, instanceValue);
		case RHSadGxPackage.USES_PORT_STUB:
			return convertUsesPortStubToString(eDataType, instanceValue);
		case RHSadGxPackage.PROVIDES_PORT_STUB:
			return convertProvidesPortStubToString(eDataType, instanceValue);
		case RHSadGxPackage.PORT:
			return convertPortToString(eDataType, instanceValue);
		case RHSadGxPackage.ASSEMBLY_CONTROLLER:
			return convertAssemblyControllerToString(eDataType, instanceValue);
		case RHSadGxPackage.EXTERNAL_PORTS:
			return convertExternalPortsToString(eDataType, instanceValue);
		case RHSadGxPackage.REASON:
			return convertReasonToString(eDataType, instanceValue);
		case RHSadGxPackage.LIST:
			return convertListToString(eDataType, instanceValue);
		case RHSadGxPackage.SAD_COMPONENT_INSTANTIATION:
			return convertSadComponentInstantiationToString(eDataType, instanceValue);
		case RHSadGxPackage.IADD_CONTEXT:
			return convertIAddContextToString(eDataType, instanceValue);
		case RHSadGxPackage.COMPONENT_PATTERN:
			return convertComponentPatternToString(eDataType, instanceValue);
		case RHSadGxPackage.IUPDATE_CONTEXT:
			return convertIUpdateContextToString(eDataType, instanceValue);
		default:
			throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ComponentShape createComponentShape() {
		ComponentShapeImpl componentShape = new ComponentShapeImpl();
		return componentShape;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Event createEventFromString(EDataType eDataType, String initialValue) {
		Event result = Event.get(initialValue);
		if (result == null)
			throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertEventToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ComponentSupportedInterfaceStub createComponentSupportedInterfaceStubFromString(EDataType eDataType, String initialValue) {
		return (ComponentSupportedInterfaceStub) super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertComponentSupportedInterfaceStubToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IFeatureProvider createIFeatureProviderFromString(EDataType eDataType, String initialValue) {
		return (IFeatureProvider) super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertIFeatureProviderToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public UsesPortStub createUsesPortStubFromString(EDataType eDataType, String initialValue) {
		return (UsesPortStub) super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertUsesPortStubToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ProvidesPortStub createProvidesPortStubFromString(EDataType eDataType, String initialValue) {
		return (ProvidesPortStub) super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertProvidesPortStubToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Port createPortFromString(EDataType eDataType, String initialValue) {
		return (Port) super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertPortToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public AssemblyController createAssemblyControllerFromString(EDataType eDataType, String initialValue) {
		return (AssemblyController) super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertAssemblyControllerToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ExternalPorts createExternalPortsFromString(EDataType eDataType, String initialValue) {
		return (ExternalPorts) super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertExternalPortsToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Reason createReasonFromString(EDataType eDataType, String initialValue) {
		return (Reason) super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertReasonToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public List< ? > createListFromString(EDataType eDataType, String initialValue) {
		return (List< ? >) super.createFromString(initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertListToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SadComponentInstantiation createSadComponentInstantiationFromString(EDataType eDataType, String initialValue) {
		return (SadComponentInstantiation) super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertSadComponentInstantiationToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IAddContext createIAddContextFromString(EDataType eDataType, String initialValue) {
		return (IAddContext) super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertIAddContextToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.ComponentPattern createComponentPatternFromString(EDataType eDataType, String initialValue) {
		return (gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.ComponentPattern) super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertComponentPatternToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IUpdateContext createIUpdateContextFromString(EDataType eDataType, String initialValue) {
		return (IUpdateContext) super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertIUpdateContextToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RHSadGxPackage getRHSadGxPackage() {
		return (RHSadGxPackage) getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static RHSadGxPackage getPackage() {
		return RHSadGxPackage.eINSTANCE;
	}

} //RHSadGxFactoryImpl
