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
// BEGIN GENERATED CODE
package gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl;

import gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.CFDataType;
import gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.KeywordsType;
import gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Model;
import gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SnapshotMetadataFactory;
import gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SnapshotMetadataPackage;
import gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Time;
import gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Value;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.emf.ecore.xml.type.XMLTypePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class SnapshotMetadataPackageImpl extends EPackageImpl implements SnapshotMetadataPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass cfDataTypeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass keywordsTypeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass modelEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass sriEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass timeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass valueEClass = null;

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
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SnapshotMetadataPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private SnapshotMetadataPackageImpl() {
		super(eNS_URI, SnapshotMetadataFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link SnapshotMetadataPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static SnapshotMetadataPackage init() {
		if (isInited) return (SnapshotMetadataPackage)EPackage.Registry.INSTANCE.getEPackage(SnapshotMetadataPackage.eNS_URI);

		// Obtain or create and register package
		SnapshotMetadataPackageImpl theSnapshotMetadataPackage = (SnapshotMetadataPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof SnapshotMetadataPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new SnapshotMetadataPackageImpl());

		isInited = true;

		// Initialize simple dependencies
		XMLTypePackage.eINSTANCE.eClass();

		// Create package meta-data objects
		theSnapshotMetadataPackage.createPackageContents();

		// Initialize created meta-data
		theSnapshotMetadataPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theSnapshotMetadataPackage.freeze();

  
		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(SnapshotMetadataPackage.eNS_URI, theSnapshotMetadataPackage);
		return theSnapshotMetadataPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getCFDataType() {
		return cfDataTypeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getCFDataType_Mixed() {
		return (EAttribute)cfDataTypeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getCFDataType_Value() {
		return (EReference)cfDataTypeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getCFDataType_Id() {
		return (EAttribute)cfDataTypeEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getKeywordsType() {
		return keywordsTypeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getKeywordsType_Mixed() {
		return (EAttribute)keywordsTypeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getKeywordsType_CFDataType() {
		return (EReference)keywordsTypeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getModel() {
		return modelEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getModel_Mixed() {
		return (EAttribute)modelEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getModel_NumberOfSamples() {
		return (EAttribute)modelEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getModel_Time() {
		return (EReference)modelEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getModel_BulkIOType() {
		return (EAttribute)modelEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getModel_StreamSRI() {
		return (EReference)modelEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getModel_DataByteOrder()
	{
		return (EAttribute)modelEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getSRI() {
		return sriEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSRI_Mixed() {
		return (EAttribute)sriEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSRI_Hversion() {
		return (EAttribute)sriEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSRI_Xstart() {
		return (EAttribute)sriEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSRI_Xdelta() {
		return (EAttribute)sriEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSRI_Xunits() {
		return (EAttribute)sriEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSRI_Subsize() {
		return (EAttribute)sriEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSRI_Ystart() {
		return (EAttribute)sriEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSRI_Ydelta() {
		return (EAttribute)sriEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSRI_Yunits() {
		return (EAttribute)sriEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSRI_Mode() {
		return (EAttribute)sriEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSRI_StreamID() {
		return (EAttribute)sriEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSRI_Blocking() {
		return (EAttribute)sriEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSRI_Keywords() {
		return (EReference)sriEClass.getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getTime() {
		return timeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTime_StartTime() {
		return (EAttribute)timeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTime_EndTime() {
		return (EAttribute)timeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getValue() {
		return valueEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getValue_Mixed() {
		return (EAttribute)valueEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getValue_Value() {
		return (EAttribute)valueEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getValue_JavaType() {
		return (EAttribute)valueEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public SnapshotMetadataFactory getSnapshotMetadataFactory() {
		return (SnapshotMetadataFactory)getEFactoryInstance();
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
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		cfDataTypeEClass = createEClass(CF_DATA_TYPE);
		createEAttribute(cfDataTypeEClass, CF_DATA_TYPE__MIXED);
		createEReference(cfDataTypeEClass, CF_DATA_TYPE__VALUE);
		createEAttribute(cfDataTypeEClass, CF_DATA_TYPE__ID);

		keywordsTypeEClass = createEClass(KEYWORDS_TYPE);
		createEAttribute(keywordsTypeEClass, KEYWORDS_TYPE__MIXED);
		createEReference(keywordsTypeEClass, KEYWORDS_TYPE__CF_DATA_TYPE);

		modelEClass = createEClass(MODEL);
		createEAttribute(modelEClass, MODEL__MIXED);
		createEAttribute(modelEClass, MODEL__NUMBER_OF_SAMPLES);
		createEAttribute(modelEClass, MODEL__DATA_BYTE_ORDER);
		createEReference(modelEClass, MODEL__TIME);
		createEAttribute(modelEClass, MODEL__BULK_IO_TYPE);
		createEReference(modelEClass, MODEL__STREAM_SRI);

		sriEClass = createEClass(SRI);
		createEAttribute(sriEClass, SRI__MIXED);
		createEAttribute(sriEClass, SRI__HVERSION);
		createEAttribute(sriEClass, SRI__XSTART);
		createEAttribute(sriEClass, SRI__XDELTA);
		createEAttribute(sriEClass, SRI__XUNITS);
		createEAttribute(sriEClass, SRI__SUBSIZE);
		createEAttribute(sriEClass, SRI__YSTART);
		createEAttribute(sriEClass, SRI__YDELTA);
		createEAttribute(sriEClass, SRI__YUNITS);
		createEAttribute(sriEClass, SRI__MODE);
		createEAttribute(sriEClass, SRI__STREAM_ID);
		createEAttribute(sriEClass, SRI__BLOCKING);
		createEReference(sriEClass, SRI__KEYWORDS);

		timeEClass = createEClass(TIME);
		createEAttribute(timeEClass, TIME__START_TIME);
		createEAttribute(timeEClass, TIME__END_TIME);

		valueEClass = createEClass(VALUE);
		createEAttribute(valueEClass, VALUE__MIXED);
		createEAttribute(valueEClass, VALUE__VALUE);
		createEAttribute(valueEClass, VALUE__JAVA_TYPE);
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
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Obtain other dependent packages
		XMLTypePackage theXMLTypePackage = (XMLTypePackage)EPackage.Registry.INSTANCE.getEPackage(XMLTypePackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes

		// Initialize classes and features; add operations and parameters
		initEClass(cfDataTypeEClass, CFDataType.class, "CFDataType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getCFDataType_Mixed(), ecorePackage.getEFeatureMapEntry(), "mixed", null, 0, -1, CFDataType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getCFDataType_Value(), ecorePackage.getEObject(), null, "value", null, 1, 1, CFDataType.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getCFDataType_Id(), theXMLTypePackage.getString(), "id", null, 0, 1, CFDataType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(keywordsTypeEClass, KeywordsType.class, "KeywordsType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getKeywordsType_Mixed(), ecorePackage.getEFeatureMapEntry(), "mixed", null, 0, -1, KeywordsType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getKeywordsType_CFDataType(), this.getCFDataType(), null, "cFDataType", null, 0, -1, KeywordsType.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

		initEClass(modelEClass, Model.class, "Model", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getModel_Mixed(), ecorePackage.getEFeatureMapEntry(), "mixed", null, 0, -1, Model.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getModel_NumberOfSamples(), theXMLTypePackage.getLong(), "numberOfSamples", null, 1, 1, Model.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getModel_DataByteOrder(), theXMLTypePackage.getString(), "dataByteOrder", "", 1, 1, Model.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getModel_Time(), this.getTime(), null, "time", null, 1, 1, Model.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getModel_BulkIOType(), theXMLTypePackage.getString(), "bulkIOType", null, 1, 1, Model.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getModel_StreamSRI(), this.getSRI(), null, "streamSRI", null, 1, 1, Model.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

		initEClass(sriEClass, gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI.class, "SRI", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSRI_Mixed(), ecorePackage.getEFeatureMapEntry(), "mixed", null, 0, -1, gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSRI_Hversion(), theXMLTypePackage.getInt(), "hversion", null, 1, 1, gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getSRI_Xstart(), theXMLTypePackage.getDouble(), "xstart", null, 1, 1, gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getSRI_Xdelta(), theXMLTypePackage.getDouble(), "xdelta", null, 1, 1, gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getSRI_Xunits(), theXMLTypePackage.getShort(), "xunits", null, 1, 1, gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getSRI_Subsize(), theXMLTypePackage.getDouble(), "subsize", null, 1, 1, gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getSRI_Ystart(), theXMLTypePackage.getDouble(), "ystart", null, 1, 1, gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getSRI_Ydelta(), theXMLTypePackage.getDouble(), "ydelta", null, 1, 1, gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getSRI_Yunits(), theXMLTypePackage.getShort(), "yunits", null, 1, 1, gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getSRI_Mode(), theXMLTypePackage.getShort(), "mode", null, 1, 1, gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getSRI_StreamID(), theXMLTypePackage.getString(), "streamID", null, 1, 1, gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getSRI_Blocking(), theXMLTypePackage.getBoolean(), "blocking", null, 1, 1, gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getSRI_Keywords(), this.getKeywordsType(), null, "keywords", null, 1, 1, gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

		initEClass(timeEClass, Time.class, "Time", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getTime_StartTime(), theXMLTypePackage.getString(), "startTime", "0", 1, 1, Time.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getTime_EndTime(), theXMLTypePackage.getString(), "endTime", "0", 1, 1, Time.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(valueEClass, Value.class, "Value", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getValue_Mixed(), ecorePackage.getEFeatureMapEntry(), "mixed", null, 0, -1, Value.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getValue_Value(), theXMLTypePackage.getString(), "value", null, 1, 1, Value.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getValue_JavaType(), theXMLTypePackage.getString(), "javaType", null, 1, 1, Value.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

		// Create resource
		createResource(eNS_URI);

		// Create annotations
		// http://www.omg.org/XMI
		createXMIAnnotations();
		// http:///org/eclipse/emf/ecore/util/ExtendedMetaData
		createExtendedMetaDataAnnotations();
	}

	/**
	 * Initializes the annotations for <b>http://www.omg.org/XMI</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createXMIAnnotations() {
		String source = "http://www.omg.org/XMI";		
		addAnnotation
		  (this, 
		   source, 
		   new String[] 
		   {
			 "version", "2.0"
		   });																																				
	}

	/**
	 * Initializes the annotations for <b>http:///org/eclipse/emf/ecore/util/ExtendedMetaData</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createExtendedMetaDataAnnotations() {
		String source = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";			
		addAnnotation
		  (this, 
		   source, 
		   new String[] 
		   {
			 "qualified", "false"
		   });		
		addAnnotation
		  (cfDataTypeEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "CFDataType",
			 "kind", "mixed"
		   });		
		addAnnotation
		  (getCFDataType_Mixed(), 
		   source, 
		   new String[] 
		   {
			 "kind", "elementWildcard",
			 "name", ":mixed"
		   });		
		addAnnotation
		  (getCFDataType_Value(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "value",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getCFDataType_Id(), 
		   source, 
		   new String[] 
		   {
			 "kind", "attribute",
			 "name", "id",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (keywordsTypeEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "keywords_._type",
			 "kind", "mixed"
		   });		
		addAnnotation
		  (getKeywordsType_Mixed(), 
		   source, 
		   new String[] 
		   {
			 "kind", "elementWildcard",
			 "name", ":mixed"
		   });		
		addAnnotation
		  (getKeywordsType_CFDataType(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "CFDataType",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (modelEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "Model",
			 "kind", "mixed"
		   });		
		addAnnotation
		  (getModel_Mixed(), 
		   source, 
		   new String[] 
		   {
			 "kind", "elementWildcard",
			 "name", ":mixed"
		   });		
		addAnnotation
		  (getModel_NumberOfSamples(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "numberOfSamples",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getModel_DataByteOrder(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "dataByteOrder",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getModel_Time(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "time",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getModel_BulkIOType(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "BulkIOType",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getModel_StreamSRI(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "StreamSRI",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (sriEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "SRI",
			 "kind", "mixed"
		   });		
		addAnnotation
		  (getSRI_Mixed(), 
		   source, 
		   new String[] 
		   {
			 "kind", "elementWildcard",
			 "name", ":mixed"
		   });		
		addAnnotation
		  (getSRI_Hversion(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "hversion",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getSRI_Xstart(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "xstart",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getSRI_Xdelta(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "xdelta",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getSRI_Xunits(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "xunits",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getSRI_Subsize(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "subsize",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getSRI_Ystart(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "ystart",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getSRI_Ydelta(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "ydelta",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getSRI_Yunits(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "yunits",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getSRI_Mode(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "mode",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getSRI_StreamID(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "streamID",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getSRI_Blocking(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "blocking",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getSRI_Keywords(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "keywords",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (timeEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "Time",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getTime_StartTime(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "startTime",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getTime_EndTime(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "endTime",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (valueEClass, 
		   source, 
		   new String[] 
		   {
			 "name", "Value",
			 "kind", "mixed"
		   });		
		addAnnotation
		  (getValue_Mixed(), 
		   source, 
		   new String[] 
		   {
			 "kind", "elementWildcard",
			 "name", ":mixed"
		   });		
		addAnnotation
		  (getValue_Value(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "value",
			 "namespace", "##targetNamespace"
		   });		
		addAnnotation
		  (getValue_JavaType(), 
		   source, 
		   new String[] 
		   {
			 "kind", "element",
			 "name", "javaType",
			 "namespace", "##targetNamespace"
		   });
	}

} //SnapshotMetadataPackageImpl
