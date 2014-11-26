/*******************************************************************************
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package gov.redhawk.ide.sad.graphiti.ext.impl;

import gov.redhawk.ide.sad.graphiti.ext.ComponentShape;
import gov.redhawk.ide.sad.graphiti.ext.Event;
import gov.redhawk.ide.sad.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.sad.graphiti.ext.RHGxFactory;
import gov.redhawk.ide.sad.graphiti.ext.RHGxPackage;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.AbstractContainerPattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.ComponentPattern;
import java.util.List;
import mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.AssemblyController;
import mil.jpeojtrs.sca.sad.ExternalPorts;
import mil.jpeojtrs.sca.sad.Port;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.MmPackage;
import org.eclipse.graphiti.mm.algorithms.AlgorithmsPackage;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramsPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class RHGxPackageImpl extends EPackageImpl implements RHGxPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass containerShapeImplEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass rhContainerShapeEClass = null;

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
	private EEnum eventEEnum = null;

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
	private EDataType abstractContainerPatternEDataType = null;

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
	 * @see gov.redhawk.ide.sad.graphiti.ext.RHGxPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private RHGxPackageImpl() {
		super(eNS_URI, RHGxFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link RHGxPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static RHGxPackage init() {
		if (isInited)
			return (RHGxPackage) EPackage.Registry.INSTANCE.getEPackage(RHGxPackage.eNS_URI);

		// Obtain or create and register package
		RHGxPackageImpl theRHGxPackage = (RHGxPackageImpl) (EPackage.Registry.INSTANCE.get(eNS_URI) instanceof RHGxPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI)
			: new RHGxPackageImpl());

		isInited = true;

		// Initialize simple dependencies
		AlgorithmsPackage.eINSTANCE.eClass();
		MmPackage.eINSTANCE.eClass();
		EcorePackage.eINSTANCE.eClass();

		// Create package meta-data objects
		theRHGxPackage.createPackageContents();

		// Initialize created meta-data
		theRHGxPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theRHGxPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(RHGxPackage.eNS_URI, theRHGxPackage);
		return theRHGxPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getContainerShapeImpl() {
		return containerShapeImplEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getRHContainerShape() {
		return rhContainerShapeEClass;
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
	public EAttribute getComponentShape_Started() {
		return (EAttribute) componentShapeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getComponentShape_Event() {
		return (EAttribute) componentShapeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getEvent() {
		return eventEEnum;
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
	public EDataType getAbstractContainerPattern() {
		return abstractContainerPatternEDataType;
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
	public RHGxFactory getRHGxFactory() {
		return (RHGxFactory) getEFactoryInstance();
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
		containerShapeImplEClass = createEClass(CONTAINER_SHAPE_IMPL);

		rhContainerShapeEClass = createEClass(RH_CONTAINER_SHAPE);

		componentShapeEClass = createEClass(COMPONENT_SHAPE);
		createEAttribute(componentShapeEClass, COMPONENT_SHAPE__STARTED);
		createEAttribute(componentShapeEClass, COMPONENT_SHAPE__EVENT);

		// Create enums
		eventEEnum = createEEnum(EVENT);

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
		abstractContainerPatternEDataType = createEDataType(ABSTRACT_CONTAINER_PATTERN);
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
		PictogramsPackage thePictogramsPackage = (PictogramsPackage) EPackage.Registry.INSTANCE.getEPackage(PictogramsPackage.eNS_URI);
		EcorePackage theEcorePackage = (EcorePackage) EPackage.Registry.INSTANCE.getEPackage(EcorePackage.eNS_URI);

		// Create type parameters
		addETypeParameter(listEDataType, "Port");

		// Set bounds for type parameters

		// Add supertypes to classes
		rhContainerShapeEClass.getESuperTypes().add(thePictogramsPackage.getContainerShape());
		componentShapeEClass.getESuperTypes().add(this.getRHContainerShape());

		// Initialize classes and features; add operations and parameters
		initEClass(containerShapeImplEClass, ContainerShape.class, "ContainerShapeImpl", IS_ABSTRACT, IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);

		initEClass(rhContainerShapeEClass, RHContainerShape.class, "RHContainerShape", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		EOperation op = addEOperation(rhContainerShapeEClass, null, "init", 0, 1, !IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getIAddContext(), "context", 0, 1, !IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getAbstractContainerPattern(), "pattern", 0, 1, !IS_UNIQUE, IS_ORDERED);

		op = addEOperation(rhContainerShapeEClass, null, "init", 0, 1, !IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getIAddContext(), "context", 0, 1, !IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getAbstractContainerPattern(), "pattern", 0, 1, !IS_UNIQUE, IS_ORDERED);
		EGenericType g1 = createEGenericType(this.getList());
		EGenericType g2 = createEGenericType(this.getPort());
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "externalPorts", 0, 1, !IS_UNIQUE, IS_ORDERED);

		op = addEOperation(rhContainerShapeEClass, null, "getProvidesPortStubs", 0, 1, !IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(theEcorePackage.getEEList());
		g2 = createEGenericType(this.getProvidesPortStub());
		g1.getETypeArguments().add(g2);
		initEOperation(op, g1);

		addEOperation(rhContainerShapeEClass, null, "layout", 0, 1, !IS_UNIQUE, IS_ORDERED);

		op = addEOperation(rhContainerShapeEClass, this.getReason(), "update", 0, 1, !IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getIUpdateContext(), "context", 0, 1, !IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getAbstractContainerPattern(), "pattern", 0, 1, !IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getList());
		g2 = createEGenericType(this.getPort());
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "externalPorts", 0, 1, !IS_UNIQUE, IS_ORDERED);

		op = addEOperation(rhContainerShapeEClass, this.getReason(), "update", 0, 1, !IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getIUpdateContext(), "context", 0, 1, !IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getAbstractContainerPattern(), "pattern", 0, 1, !IS_UNIQUE, IS_ORDERED);

		op = addEOperation(rhContainerShapeEClass, this.getReason(), "updateNeeded", 0, 1, !IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getIUpdateContext(), "context", 0, 1, !IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getAbstractContainerPattern(), "pattern", 0, 1, !IS_UNIQUE, IS_ORDERED);
		g1 = createEGenericType(this.getList());
		g2 = createEGenericType(this.getPort());
		g1.getETypeArguments().add(g2);
		addEParameter(op, g1, "externalPorts", 0, 1, !IS_UNIQUE, IS_ORDERED);

		op = addEOperation(rhContainerShapeEClass, this.getReason(), "updateNeeded", 0, 1, !IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getIUpdateContext(), "context", 0, 1, !IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getAbstractContainerPattern(), "pattern", 0, 1, !IS_UNIQUE, IS_ORDERED);

		initEClass(componentShapeEClass, ComponentShape.class, "ComponentShape", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getComponentShape_Started(), theEcorePackage.getEBoolean(), "started", null, 0, 1, ComponentShape.class, !IS_TRANSIENT, !IS_VOLATILE,
			IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getComponentShape_Event(), this.getEvent(), "event", null, 0, 1, ComponentShape.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
			!IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		op = addEOperation(componentShapeEClass, null, "init", 0, 1, !IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getIAddContext(), "context", 0, 1, !IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getComponentPattern(), "pattern", 0, 1, !IS_UNIQUE, IS_ORDERED);

		addEOperation(componentShapeEClass, null, "layout", 0, 1, !IS_UNIQUE, IS_ORDERED);

		op = addEOperation(componentShapeEClass, this.getReason(), "update", 0, 1, !IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getIUpdateContext(), "context", 0, 1, !IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getComponentPattern(), "pattern", 0, 1, !IS_UNIQUE, IS_ORDERED);

		op = addEOperation(componentShapeEClass, this.getReason(), "updateNeeded", 0, 1, !IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getIUpdateContext(), "context", 0, 1, !IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getComponentPattern(), "pattern", 0, 1, !IS_UNIQUE, IS_ORDERED);

		// Initialize enums and add enum literals
		initEEnum(eventEEnum, Event.class, "Event");
		addEEnumLiteral(eventEEnum, Event.RELEASE);
		addEEnumLiteral(eventEEnum, Event.TERMINATE);

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
		initEDataType(abstractContainerPatternEDataType, AbstractContainerPattern.class, "AbstractContainerPattern", IS_SERIALIZABLE,
			!IS_GENERATED_INSTANCE_CLASS);
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

} // RHGxPackageImpl
