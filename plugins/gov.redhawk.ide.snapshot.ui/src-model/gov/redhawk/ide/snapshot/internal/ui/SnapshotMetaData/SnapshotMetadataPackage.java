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
package gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

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
 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SnapshotMetadataFactory
 * @model kind="package"
 *        annotation="http://www.omg.org/XMI version='2.0'"
 *        extendedMetaData="qualified='false'"
 * @generated
 */
public interface SnapshotMetadataPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "SnapshotMetaData";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "platform:/resource/gov.redhawk.ide.snapshot.ui/src/snapshotMetaData.xsd";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "SnapshotMetaData";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	SnapshotMetadataPackage eINSTANCE = gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.SnapshotMetadataPackageImpl.init();

	/**
	 * The meta object id for the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.CFDataTypeImpl <em>CF Data Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.CFDataTypeImpl
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.SnapshotMetadataPackageImpl#getCFDataType()
	 * @generated
	 */
	int CF_DATA_TYPE = 0;

	/**
	 * The feature id for the '<em><b>Mixed</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CF_DATA_TYPE__MIXED = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CF_DATA_TYPE__VALUE = 1;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CF_DATA_TYPE__ID = 2;

	/**
	 * The number of structural features of the '<em>CF Data Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CF_DATA_TYPE_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.KeywordsTypeImpl <em>Keywords Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.KeywordsTypeImpl
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.SnapshotMetadataPackageImpl#getKeywordsType()
	 * @generated
	 */
	int KEYWORDS_TYPE = 1;

	/**
	 * The feature id for the '<em><b>Mixed</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int KEYWORDS_TYPE__MIXED = 0;

	/**
	 * The feature id for the '<em><b>CF Data Type</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int KEYWORDS_TYPE__CF_DATA_TYPE = 1;

	/**
	 * The number of structural features of the '<em>Keywords Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int KEYWORDS_TYPE_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.ModelImpl <em>Model</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.ModelImpl
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.SnapshotMetadataPackageImpl#getModel()
	 * @generated
	 */
	int MODEL = 2;

	/**
	 * The feature id for the '<em><b>Mixed</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL__MIXED = 0;

	/**
	 * The feature id for the '<em><b>Number Of Samples</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL__NUMBER_OF_SAMPLES = 1;

	/**
	 * The feature id for the '<em><b>Data Byte Order</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL__DATA_BYTE_ORDER = 2;

	/**
	 * The feature id for the '<em><b>Time</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL__TIME = 3;

	/**
	 * The feature id for the '<em><b>Bulk IO Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL__BULK_IO_TYPE = 4;

	/**
	 * The feature id for the '<em><b>Stream SRI</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL__STREAM_SRI = 5;

	/**
	 * The number of structural features of the '<em>Model</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODEL_FEATURE_COUNT = 6;

	/**
	 * The meta object id for the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.SRIImpl <em>SRI</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.SRIImpl
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.SnapshotMetadataPackageImpl#getSRI()
	 * @generated
	 */
	int SRI = 3;

	/**
	 * The feature id for the '<em><b>Mixed</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SRI__MIXED = 0;

	/**
	 * The feature id for the '<em><b>Hversion</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SRI__HVERSION = 1;

	/**
	 * The feature id for the '<em><b>Xstart</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SRI__XSTART = 2;

	/**
	 * The feature id for the '<em><b>Xdelta</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SRI__XDELTA = 3;

	/**
	 * The feature id for the '<em><b>Xunits</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SRI__XUNITS = 4;

	/**
	 * The feature id for the '<em><b>Subsize</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SRI__SUBSIZE = 5;

	/**
	 * The feature id for the '<em><b>Ystart</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SRI__YSTART = 6;

	/**
	 * The feature id for the '<em><b>Ydelta</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SRI__YDELTA = 7;

	/**
	 * The feature id for the '<em><b>Yunits</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SRI__YUNITS = 8;

	/**
	 * The feature id for the '<em><b>Mode</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SRI__MODE = 9;

	/**
	 * The feature id for the '<em><b>Stream ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SRI__STREAM_ID = 10;

	/**
	 * The feature id for the '<em><b>Blocking</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SRI__BLOCKING = 11;

	/**
	 * The feature id for the '<em><b>Keywords</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SRI__KEYWORDS = 12;

	/**
	 * The number of structural features of the '<em>SRI</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SRI_FEATURE_COUNT = 13;

	/**
	 * The meta object id for the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.TimeImpl <em>Time</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.TimeImpl
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.SnapshotMetadataPackageImpl#getTime()
	 * @generated
	 */
	int TIME = 4;

	/**
	 * The feature id for the '<em><b>Start Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIME__START_TIME = 0;

	/**
	 * The feature id for the '<em><b>End Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIME__END_TIME = 1;

	/**
	 * The number of structural features of the '<em>Time</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIME_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.ValueImpl <em>Value</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.ValueImpl
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.SnapshotMetadataPackageImpl#getValue()
	 * @generated
	 */
	int VALUE = 5;

	/**
	 * The feature id for the '<em><b>Mixed</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALUE__MIXED = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALUE__VALUE = 1;

	/**
	 * The feature id for the '<em><b>Java Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALUE__JAVA_TYPE = 2;

	/**
	 * The number of structural features of the '<em>Value</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALUE_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.StructSeqImpl <em>Struct Seq</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.StructSeqImpl
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.SnapshotMetadataPackageImpl#getStructSeq()
	 * @generated
	 */
	int STRUCT_SEQ = 6;

	/**
	 * The feature id for the '<em><b>Mixed</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRUCT_SEQ__MIXED = 0;

	/**
	 * The feature id for the '<em><b>Struct</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRUCT_SEQ__STRUCT = 1;

	/**
	 * The number of structural features of the '<em>Struct Seq</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRUCT_SEQ_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.StructImpl <em>Struct</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.StructImpl
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.SnapshotMetadataPackageImpl#getStruct()
	 * @generated
	 */
	int STRUCT = 7;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRUCT__ID = 0;

	/**
	 * The feature id for the '<em><b>Mixed</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRUCT__MIXED = 1;

	/**
	 * The feature id for the '<em><b>Simple</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRUCT__SIMPLE = 2;

	/**
	 * The number of structural features of the '<em>Struct</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRUCT_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.SimpleImpl <em>Simple</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.SimpleImpl
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.SnapshotMetadataPackageImpl#getSimple()
	 * @generated
	 */
	int SIMPLE = 8;

	/**
	 * The feature id for the '<em><b>Mixed</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SIMPLE__MIXED = 0;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SIMPLE__ID = 1;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SIMPLE__VALUE = 2;

	/**
	 * The feature id for the '<em><b>Java Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SIMPLE__JAVA_TYPE = 3;

	/**
	 * The number of structural features of the '<em>Simple</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SIMPLE_FEATURE_COUNT = 4;

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.CFDataType <em>CF Data Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>CF Data Type</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.CFDataType
	 * @generated
	 */
	EClass getCFDataType();

	/**
	 * Returns the meta object for the attribute list '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.CFDataType#getMixed <em>Mixed</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Mixed</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.CFDataType#getMixed()
	 * @see #getCFDataType()
	 * @generated
	 */
	EAttribute getCFDataType_Mixed();

	/**
	 * Returns the meta object for the containment reference '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.CFDataType#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Value</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.CFDataType#getValue()
	 * @see #getCFDataType()
	 * @generated
	 */
	EReference getCFDataType_Value();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.CFDataType#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.CFDataType#getId()
	 * @see #getCFDataType()
	 * @generated
	 */
	EAttribute getCFDataType_Id();

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.KeywordsType <em>Keywords Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Keywords Type</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.KeywordsType
	 * @generated
	 */
	EClass getKeywordsType();

	/**
	 * Returns the meta object for the attribute list '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.KeywordsType#getMixed <em>Mixed</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Mixed</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.KeywordsType#getMixed()
	 * @see #getKeywordsType()
	 * @generated
	 */
	EAttribute getKeywordsType_Mixed();

	/**
	 * Returns the meta object for the containment reference list '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.KeywordsType#getCFDataType <em>CF Data Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>CF Data Type</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.KeywordsType#getCFDataType()
	 * @see #getKeywordsType()
	 * @generated
	 */
	EReference getKeywordsType_CFDataType();

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Model <em>Model</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Model</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Model
	 * @generated
	 */
	EClass getModel();

	/**
	 * Returns the meta object for the attribute list '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Model#getMixed <em>Mixed</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Mixed</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Model#getMixed()
	 * @see #getModel()
	 * @generated
	 */
	EAttribute getModel_Mixed();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Model#getNumberOfSamples <em>Number Of Samples</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Number Of Samples</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Model#getNumberOfSamples()
	 * @see #getModel()
	 * @generated
	 */
	EAttribute getModel_NumberOfSamples();

	/**
	 * Returns the meta object for the containment reference '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Model#getTime <em>Time</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Time</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Model#getTime()
	 * @see #getModel()
	 * @generated
	 */
	EReference getModel_Time();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Model#getBulkIOType <em>Bulk IO Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Bulk IO Type</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Model#getBulkIOType()
	 * @see #getModel()
	 * @generated
	 */
	EAttribute getModel_BulkIOType();

	/**
	 * Returns the meta object for the containment reference '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Model#getStreamSRI <em>Stream SRI</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Stream SRI</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Model#getStreamSRI()
	 * @see #getModel()
	 * @generated
	 */
	EReference getModel_StreamSRI();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Model#getDataByteOrder <em>Data Byte Order</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Data Byte Order</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Model#getDataByteOrder()
	 * @see #getModel()
	 * @generated
	 */
	EAttribute getModel_DataByteOrder();

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI <em>SRI</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>SRI</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI
	 * @generated
	 */
	EClass getSRI();

	/**
	 * Returns the meta object for the attribute list '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getMixed <em>Mixed</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Mixed</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getMixed()
	 * @see #getSRI()
	 * @generated
	 */
	EAttribute getSRI_Mixed();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getHversion <em>Hversion</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Hversion</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getHversion()
	 * @see #getSRI()
	 * @generated
	 */
	EAttribute getSRI_Hversion();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getXstart <em>Xstart</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Xstart</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getXstart()
	 * @see #getSRI()
	 * @generated
	 */
	EAttribute getSRI_Xstart();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getXdelta <em>Xdelta</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Xdelta</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getXdelta()
	 * @see #getSRI()
	 * @generated
	 */
	EAttribute getSRI_Xdelta();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getXunits <em>Xunits</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Xunits</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getXunits()
	 * @see #getSRI()
	 * @generated
	 */
	EAttribute getSRI_Xunits();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getSubsize <em>Subsize</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Subsize</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getSubsize()
	 * @see #getSRI()
	 * @generated
	 */
	EAttribute getSRI_Subsize();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getYstart <em>Ystart</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ystart</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getYstart()
	 * @see #getSRI()
	 * @generated
	 */
	EAttribute getSRI_Ystart();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getYdelta <em>Ydelta</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ydelta</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getYdelta()
	 * @see #getSRI()
	 * @generated
	 */
	EAttribute getSRI_Ydelta();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getYunits <em>Yunits</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Yunits</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getYunits()
	 * @see #getSRI()
	 * @generated
	 */
	EAttribute getSRI_Yunits();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getMode <em>Mode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Mode</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getMode()
	 * @see #getSRI()
	 * @generated
	 */
	EAttribute getSRI_Mode();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getStreamID <em>Stream ID</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Stream ID</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getStreamID()
	 * @see #getSRI()
	 * @generated
	 */
	EAttribute getSRI_StreamID();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#isBlocking <em>Blocking</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Blocking</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#isBlocking()
	 * @see #getSRI()
	 * @generated
	 */
	EAttribute getSRI_Blocking();

	/**
	 * Returns the meta object for the containment reference '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getKeywords <em>Keywords</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Keywords</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getKeywords()
	 * @see #getSRI()
	 * @generated
	 */
	EReference getSRI_Keywords();

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Time <em>Time</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Time</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Time
	 * @generated
	 */
	EClass getTime();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Time#getStartTime <em>Start Time</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Start Time</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Time#getStartTime()
	 * @see #getTime()
	 * @generated
	 */
	EAttribute getTime_StartTime();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Time#getEndTime <em>End Time</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>End Time</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Time#getEndTime()
	 * @see #getTime()
	 * @generated
	 */
	EAttribute getTime_EndTime();

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Value <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Value</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Value
	 * @generated
	 */
	EClass getValue();

	/**
	 * Returns the meta object for the attribute list '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Value#getMixed <em>Mixed</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Mixed</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Value#getMixed()
	 * @see #getValue()
	 * @generated
	 */
	EAttribute getValue_Mixed();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Value#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Value#getValue()
	 * @see #getValue()
	 * @generated
	 */
	EAttribute getValue_Value();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Value#getJavaType <em>Java Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Java Type</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Value#getJavaType()
	 * @see #getValue()
	 * @generated
	 */
	EAttribute getValue_JavaType();

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.StructSeq <em>Struct Seq</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Struct Seq</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.StructSeq
	 * @generated
	 */
	EClass getStructSeq();

	/**
	 * Returns the meta object for the attribute list '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.StructSeq#getMixed <em>Mixed</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Mixed</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.StructSeq#getMixed()
	 * @see #getStructSeq()
	 * @generated
	 */
	EAttribute getStructSeq_Mixed();

	/**
	 * Returns the meta object for the containment reference list '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.StructSeq#getStruct <em>Struct</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Struct</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.StructSeq#getStruct()
	 * @see #getStructSeq()
	 * @generated
	 */
	EReference getStructSeq_Struct();

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Struct <em>Struct</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Struct</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Struct
	 * @generated
	 */
	EClass getStruct();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Struct#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Struct#getId()
	 * @see #getStruct()
	 * @generated
	 */
	EAttribute getStruct_Id();

	/**
	 * Returns the meta object for the attribute list '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Struct#getMixed <em>Mixed</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Mixed</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Struct#getMixed()
	 * @see #getStruct()
	 * @generated
	 */
	EAttribute getStruct_Mixed();

	/**
	 * Returns the meta object for the containment reference list '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Struct#getSimple <em>Simple</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Simple</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Struct#getSimple()
	 * @see #getStruct()
	 * @generated
	 */
	EReference getStruct_Simple();

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Simple <em>Simple</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Simple</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Simple
	 * @generated
	 */
	EClass getSimple();

	/**
	 * Returns the meta object for the attribute list '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Simple#getMixed <em>Mixed</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Mixed</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Simple#getMixed()
	 * @see #getSimple()
	 * @generated
	 */
	EAttribute getSimple_Mixed();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Simple#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Simple#getId()
	 * @see #getSimple()
	 * @generated
	 */
	EAttribute getSimple_Id();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Simple#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Simple#getValue()
	 * @see #getSimple()
	 * @generated
	 */
	EAttribute getSimple_Value();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Simple#getJavaType <em>Java Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Java Type</em>'.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Simple#getJavaType()
	 * @see #getSimple()
	 * @generated
	 */
	EAttribute getSimple_JavaType();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	SnapshotMetadataFactory getSnapshotMetadataFactory();

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
		 * The meta object literal for the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.CFDataTypeImpl <em>CF Data Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.CFDataTypeImpl
		 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.SnapshotMetadataPackageImpl#getCFDataType()
		 * @generated
		 */
		EClass CF_DATA_TYPE = eINSTANCE.getCFDataType();

		/**
		 * The meta object literal for the '<em><b>Mixed</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CF_DATA_TYPE__MIXED = eINSTANCE.getCFDataType_Mixed();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CF_DATA_TYPE__VALUE = eINSTANCE.getCFDataType_Value();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CF_DATA_TYPE__ID = eINSTANCE.getCFDataType_Id();

		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.KeywordsTypeImpl <em>Keywords Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.KeywordsTypeImpl
		 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.SnapshotMetadataPackageImpl#getKeywordsType()
		 * @generated
		 */
		EClass KEYWORDS_TYPE = eINSTANCE.getKeywordsType();

		/**
		 * The meta object literal for the '<em><b>Mixed</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute KEYWORDS_TYPE__MIXED = eINSTANCE.getKeywordsType_Mixed();

		/**
		 * The meta object literal for the '<em><b>CF Data Type</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference KEYWORDS_TYPE__CF_DATA_TYPE = eINSTANCE.getKeywordsType_CFDataType();

		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.ModelImpl <em>Model</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.ModelImpl
		 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.SnapshotMetadataPackageImpl#getModel()
		 * @generated
		 */
		EClass MODEL = eINSTANCE.getModel();

		/**
		 * The meta object literal for the '<em><b>Mixed</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MODEL__MIXED = eINSTANCE.getModel_Mixed();

		/**
		 * The meta object literal for the '<em><b>Number Of Samples</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MODEL__NUMBER_OF_SAMPLES = eINSTANCE.getModel_NumberOfSamples();

		/**
		 * The meta object literal for the '<em><b>Time</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MODEL__TIME = eINSTANCE.getModel_Time();

		/**
		 * The meta object literal for the '<em><b>Bulk IO Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MODEL__BULK_IO_TYPE = eINSTANCE.getModel_BulkIOType();

		/**
		 * The meta object literal for the '<em><b>Stream SRI</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MODEL__STREAM_SRI = eINSTANCE.getModel_StreamSRI();

		/**
		 * The meta object literal for the '<em><b>Data Byte Order</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MODEL__DATA_BYTE_ORDER = eINSTANCE.getModel_DataByteOrder();

		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.SRIImpl <em>SRI</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.SRIImpl
		 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.SnapshotMetadataPackageImpl#getSRI()
		 * @generated
		 */
		EClass SRI = eINSTANCE.getSRI();

		/**
		 * The meta object literal for the '<em><b>Mixed</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SRI__MIXED = eINSTANCE.getSRI_Mixed();

		/**
		 * The meta object literal for the '<em><b>Hversion</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SRI__HVERSION = eINSTANCE.getSRI_Hversion();

		/**
		 * The meta object literal for the '<em><b>Xstart</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SRI__XSTART = eINSTANCE.getSRI_Xstart();

		/**
		 * The meta object literal for the '<em><b>Xdelta</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SRI__XDELTA = eINSTANCE.getSRI_Xdelta();

		/**
		 * The meta object literal for the '<em><b>Xunits</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SRI__XUNITS = eINSTANCE.getSRI_Xunits();

		/**
		 * The meta object literal for the '<em><b>Subsize</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SRI__SUBSIZE = eINSTANCE.getSRI_Subsize();

		/**
		 * The meta object literal for the '<em><b>Ystart</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SRI__YSTART = eINSTANCE.getSRI_Ystart();

		/**
		 * The meta object literal for the '<em><b>Ydelta</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SRI__YDELTA = eINSTANCE.getSRI_Ydelta();

		/**
		 * The meta object literal for the '<em><b>Yunits</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SRI__YUNITS = eINSTANCE.getSRI_Yunits();

		/**
		 * The meta object literal for the '<em><b>Mode</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SRI__MODE = eINSTANCE.getSRI_Mode();

		/**
		 * The meta object literal for the '<em><b>Stream ID</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SRI__STREAM_ID = eINSTANCE.getSRI_StreamID();

		/**
		 * The meta object literal for the '<em><b>Blocking</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SRI__BLOCKING = eINSTANCE.getSRI_Blocking();

		/**
		 * The meta object literal for the '<em><b>Keywords</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SRI__KEYWORDS = eINSTANCE.getSRI_Keywords();

		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.TimeImpl <em>Time</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.TimeImpl
		 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.SnapshotMetadataPackageImpl#getTime()
		 * @generated
		 */
		EClass TIME = eINSTANCE.getTime();

		/**
		 * The meta object literal for the '<em><b>Start Time</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TIME__START_TIME = eINSTANCE.getTime_StartTime();

		/**
		 * The meta object literal for the '<em><b>End Time</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TIME__END_TIME = eINSTANCE.getTime_EndTime();

		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.ValueImpl <em>Value</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.ValueImpl
		 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.SnapshotMetadataPackageImpl#getValue()
		 * @generated
		 */
		EClass VALUE = eINSTANCE.getValue();

		/**
		 * The meta object literal for the '<em><b>Mixed</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VALUE__MIXED = eINSTANCE.getValue_Mixed();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VALUE__VALUE = eINSTANCE.getValue_Value();

		/**
		 * The meta object literal for the '<em><b>Java Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VALUE__JAVA_TYPE = eINSTANCE.getValue_JavaType();

		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.StructSeqImpl <em>Struct Seq</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.StructSeqImpl
		 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.SnapshotMetadataPackageImpl#getStructSeq()
		 * @generated
		 */
		EClass STRUCT_SEQ = eINSTANCE.getStructSeq();

		/**
		 * The meta object literal for the '<em><b>Mixed</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute STRUCT_SEQ__MIXED = eINSTANCE.getStructSeq_Mixed();

		/**
		 * The meta object literal for the '<em><b>Struct</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference STRUCT_SEQ__STRUCT = eINSTANCE.getStructSeq_Struct();

		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.StructImpl <em>Struct</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.StructImpl
		 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.SnapshotMetadataPackageImpl#getStruct()
		 * @generated
		 */
		EClass STRUCT = eINSTANCE.getStruct();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute STRUCT__ID = eINSTANCE.getStruct_Id();

		/**
		 * The meta object literal for the '<em><b>Mixed</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute STRUCT__MIXED = eINSTANCE.getStruct_Mixed();

		/**
		 * The meta object literal for the '<em><b>Simple</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference STRUCT__SIMPLE = eINSTANCE.getStruct_Simple();

		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.SimpleImpl <em>Simple</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.SimpleImpl
		 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.SnapshotMetadataPackageImpl#getSimple()
		 * @generated
		 */
		EClass SIMPLE = eINSTANCE.getSimple();

		/**
		 * The meta object literal for the '<em><b>Mixed</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SIMPLE__MIXED = eINSTANCE.getSimple_Mixed();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SIMPLE__ID = eINSTANCE.getSimple_Id();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SIMPLE__VALUE = eINSTANCE.getSimple_Value();

		/**
		 * The meta object literal for the '<em><b>Java Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SIMPLE__JAVA_TYPE = eINSTANCE.getSimple_JavaType();

	}

} //SnapshotMetadataPackage
