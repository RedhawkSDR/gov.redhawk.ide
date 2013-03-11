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
package gov.redhawk.ide.codegen;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc --> The <b>Package</b> for the model. It contains
 * accessors for the meta objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see gov.redhawk.ide.codegen.CodegenFactory
 * @model kind="package"
 * @generated
 */
public interface CodegenPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "codegen";
	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://www.redhawk.gov/model/codegen";
	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "codegen";
	/**
	 * The package content type ID.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eCONTENT_TYPE = "gov.redhawk.ide.codegen";
	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	CodegenPackage eINSTANCE = gov.redhawk.ide.codegen.impl.CodegenPackageImpl.init();
	/**
	 * The meta object id for the '{@link gov.redhawk.ide.codegen.impl.ImplementationSettingsImpl <em>Implementation Settings</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.codegen.impl.ImplementationSettingsImpl
	 * @see gov.redhawk.ide.codegen.impl.CodegenPackageImpl#getImplementationSettings()
	 * @generated
	 */
	int IMPLEMENTATION_SETTINGS = 0;
	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPLEMENTATION_SETTINGS__NAME = 0;
	/**
	 * The feature id for the '<em><b>Output Dir</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPLEMENTATION_SETTINGS__OUTPUT_DIR = 1;
	/**
	 * The feature id for the '<em><b>Template</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPLEMENTATION_SETTINGS__TEMPLATE = 2;
	/**
	 * The feature id for the '<em><b>Properties</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPLEMENTATION_SETTINGS__PROPERTIES = 3;
	/**
	 * The feature id for the '<em><b>Generator Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPLEMENTATION_SETTINGS__GENERATOR_ID = 4;
	/**
	 * The feature id for the '<em><b>Generated On</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPLEMENTATION_SETTINGS__GENERATED_ON = 5;
	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPLEMENTATION_SETTINGS__ID = 6;
	/**
	 * The feature id for the '<em><b>Generated File CR Cs</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPLEMENTATION_SETTINGS__GENERATED_FILE_CR_CS = 7;
	/**
	 * The feature id for the '<em><b>Port Generators</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPLEMENTATION_SETTINGS__PORT_GENERATORS = 8;
	/**
	 * The feature id for the '<em><b>Primary</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPLEMENTATION_SETTINGS__PRIMARY = 9;
	/**
	 * The number of structural features of the '<em>Implementation Settings</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPLEMENTATION_SETTINGS_FEATURE_COUNT = 10;
	/**
	 * The meta object id for the '{@link gov.redhawk.ide.codegen.impl.PropertyImpl <em>Property</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.codegen.impl.PropertyImpl
	 * @see gov.redhawk.ide.codegen.impl.CodegenPackageImpl#getProperty()
	 * @generated
	 */
	int PROPERTY = 1;
	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY__ID = 0;
	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY__VALUE = 1;
	/**
	 * The number of structural features of the '<em>Property</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY_FEATURE_COUNT = 2;
	/**
	 * The meta object id for the '{@link gov.redhawk.ide.codegen.impl.WaveDevSettingsImpl <em>Wave Dev Settings</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.codegen.impl.WaveDevSettingsImpl
	 * @see gov.redhawk.ide.codegen.impl.CodegenPackageImpl#getWaveDevSettings()
	 * @generated
	 */
	int WAVE_DEV_SETTINGS = 2;
	/**
	 * The feature id for the '<em><b>Impl Settings</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WAVE_DEV_SETTINGS__IMPL_SETTINGS = 0;
	/**
	 * The number of structural features of the '<em>Wave Dev Settings</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WAVE_DEV_SETTINGS_FEATURE_COUNT = 1;
	/**
	 * The meta object id for the '{@link gov.redhawk.ide.codegen.impl.ImplIdToSettingsMapImpl <em>Impl Id To Settings Map</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.codegen.impl.ImplIdToSettingsMapImpl
	 * @see gov.redhawk.ide.codegen.impl.CodegenPackageImpl#getImplIdToSettingsMap()
	 * @generated
	 */
	int IMPL_ID_TO_SETTINGS_MAP = 3;
	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPL_ID_TO_SETTINGS_MAP__VALUE = 0;
	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPL_ID_TO_SETTINGS_MAP__KEY = 1;
	/**
	 * The number of structural features of the '<em>Impl Id To Settings Map</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int IMPL_ID_TO_SETTINGS_MAP_FEATURE_COUNT = 2;
	/**
	 * The meta object id for the '{@link gov.redhawk.ide.codegen.impl.FileToCRCMapImpl <em>File To CRC Map</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.codegen.impl.FileToCRCMapImpl
	 * @see gov.redhawk.ide.codegen.impl.CodegenPackageImpl#getFileToCRCMap()
	 * @generated
	 */
	int FILE_TO_CRC_MAP = 4;
	/**
	 * The feature id for the '<em><b>Crc</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FILE_TO_CRC_MAP__CRC = 0;
	/**
	 * The feature id for the '<em><b>File</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FILE_TO_CRC_MAP__FILE = 1;
	/**
	 * The number of structural features of the '<em>File To CRC Map</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FILE_TO_CRC_MAP_FEATURE_COUNT = 2;
	/**
	 * The meta object id for the '{@link gov.redhawk.ide.codegen.impl.PortRepToGeneratorMapImpl <em>Port Rep To Generator Map</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.codegen.impl.PortRepToGeneratorMapImpl
	 * @see gov.redhawk.ide.codegen.impl.CodegenPackageImpl#getPortRepToGeneratorMap()
	 * @generated
	 */
	int PORT_REP_TO_GENERATOR_MAP = 5;
	/**
	 * The feature id for the '<em><b>Generator</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PORT_REP_TO_GENERATOR_MAP__GENERATOR = 0;
	/**
	 * The feature id for the '<em><b>Rep Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PORT_REP_TO_GENERATOR_MAP__REP_ID = 1;
	/**
	 * The number of structural features of the '<em>Port Rep To Generator Map</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PORT_REP_TO_GENERATOR_MAP_FEATURE_COUNT = 2;

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.codegen.ImplementationSettings <em>Implementation Settings</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Implementation Settings</em>'.
	 * @see gov.redhawk.ide.codegen.ImplementationSettings
	 * @generated
	 */
	EClass getImplementationSettings();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.codegen.ImplementationSettings#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see gov.redhawk.ide.codegen.ImplementationSettings#getName()
	 * @see #getImplementationSettings()
	 * @generated
	 */
	EAttribute getImplementationSettings_Name();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.codegen.ImplementationSettings#getOutputDir <em>Output Dir</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Output Dir</em>'.
	 * @see gov.redhawk.ide.codegen.ImplementationSettings#getOutputDir()
	 * @see #getImplementationSettings()
	 * @generated
	 */
	EAttribute getImplementationSettings_OutputDir();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.codegen.ImplementationSettings#getTemplate <em>Template</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Template</em>'.
	 * @see gov.redhawk.ide.codegen.ImplementationSettings#getTemplate()
	 * @see #getImplementationSettings()
	 * @generated
	 */
	EAttribute getImplementationSettings_Template();

	/**
	 * Returns the meta object for the containment reference list '{@link gov.redhawk.ide.codegen.ImplementationSettings#getProperties <em>Properties</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Properties</em>'.
	 * @see gov.redhawk.ide.codegen.ImplementationSettings#getProperties()
	 * @see #getImplementationSettings()
	 * @generated
	 */
	EReference getImplementationSettings_Properties();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.codegen.ImplementationSettings#getGeneratorId <em>Generator Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Generator Id</em>'.
	 * @see gov.redhawk.ide.codegen.ImplementationSettings#getGeneratorId()
	 * @see #getImplementationSettings()
	 * @generated
	 */
	EAttribute getImplementationSettings_GeneratorId();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.codegen.ImplementationSettings#getGeneratedOn <em>Generated On</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Generated On</em>'.
	 * @see gov.redhawk.ide.codegen.ImplementationSettings#getGeneratedOn()
	 * @see #getImplementationSettings()
	 * @generated
	 */
	EAttribute getImplementationSettings_GeneratedOn();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.codegen.ImplementationSettings#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see gov.redhawk.ide.codegen.ImplementationSettings#getId()
	 * @see #getImplementationSettings()
	 * @generated
	 */
	EAttribute getImplementationSettings_Id();

	/**
	 * Returns the meta object for the containment reference list '{@link gov.redhawk.ide.codegen.ImplementationSettings#getGeneratedFileCRCs <em>Generated File CR Cs</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Generated File CR Cs</em>'.
	 * @see gov.redhawk.ide.codegen.ImplementationSettings#getGeneratedFileCRCs()
	 * @see #getImplementationSettings()
	 * @generated
	 */
	EReference getImplementationSettings_GeneratedFileCRCs();

	/**
	 * Returns the meta object for the containment reference list '{@link gov.redhawk.ide.codegen.ImplementationSettings#getPortGenerators <em>Port Generators</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Port Generators</em>'.
	 * @see gov.redhawk.ide.codegen.ImplementationSettings#getPortGenerators()
	 * @see #getImplementationSettings()
	 * @generated
	 */
	EReference getImplementationSettings_PortGenerators();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.codegen.ImplementationSettings#isPrimary <em>Primary</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Primary</em>'.
	 * @see gov.redhawk.ide.codegen.ImplementationSettings#isPrimary()
	 * @see #getImplementationSettings()
	 * @generated
	 */
	EAttribute getImplementationSettings_Primary();

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.codegen.Property <em>Property</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Property</em>'.
	 * @see gov.redhawk.ide.codegen.Property
	 * @generated
	 */
	EClass getProperty();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.codegen.Property#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see gov.redhawk.ide.codegen.Property#getId()
	 * @see #getProperty()
	 * @generated
	 */
	EAttribute getProperty_Id();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.codegen.Property#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see gov.redhawk.ide.codegen.Property#getValue()
	 * @see #getProperty()
	 * @generated
	 */
	EAttribute getProperty_Value();

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.codegen.WaveDevSettings <em>Wave Dev Settings</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Wave Dev Settings</em>'.
	 * @see gov.redhawk.ide.codegen.WaveDevSettings
	 * @generated
	 */
	EClass getWaveDevSettings();

	/**
	 * Returns the meta object for the map '{@link gov.redhawk.ide.codegen.WaveDevSettings#getImplSettings <em>Impl Settings</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Impl Settings</em>'.
	 * @see gov.redhawk.ide.codegen.WaveDevSettings#getImplSettings()
	 * @see #getWaveDevSettings()
	 * @generated
	 */
	EReference getWaveDevSettings_ImplSettings();

	/**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>Impl Id To Settings Map</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Impl Id To Settings Map</em>'.
	 * @see java.util.Map.Entry
	 * @model features="value key" 
	 *        valueType="gov.redhawk.ide.codegen.ImplementationSettings" valueContainment="true"
	 *        keyDataType="org.eclipse.emf.ecore.EString"
	 * @generated
	 */
	EClass getImplIdToSettingsMap();

	/**
	 * Returns the meta object for the containment reference '{@link java.util.Map.Entry <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getImplIdToSettingsMap()
	 * @generated
	 */
	EReference getImplIdToSettingsMap_Value();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getImplIdToSettingsMap()
	 * @generated
	 */
	EAttribute getImplIdToSettingsMap_Key();

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.codegen.FileToCRCMap <em>File To CRC Map</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>File To CRC Map</em>'.
	 * @see gov.redhawk.ide.codegen.FileToCRCMap
	 * @generated
	 */
	EClass getFileToCRCMap();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.codegen.FileToCRCMap#getCrc <em>Crc</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Crc</em>'.
	 * @see gov.redhawk.ide.codegen.FileToCRCMap#getCrc()
	 * @see #getFileToCRCMap()
	 * @generated
	 */
	EAttribute getFileToCRCMap_Crc();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.codegen.FileToCRCMap#getFile <em>File</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>File</em>'.
	 * @see gov.redhawk.ide.codegen.FileToCRCMap#getFile()
	 * @see #getFileToCRCMap()
	 * @generated
	 */
	EAttribute getFileToCRCMap_File();

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.codegen.PortRepToGeneratorMap <em>Port Rep To Generator Map</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Port Rep To Generator Map</em>'.
	 * @see gov.redhawk.ide.codegen.PortRepToGeneratorMap
	 * @generated
	 */
	EClass getPortRepToGeneratorMap();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.codegen.PortRepToGeneratorMap#getGenerator <em>Generator</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Generator</em>'.
	 * @see gov.redhawk.ide.codegen.PortRepToGeneratorMap#getGenerator()
	 * @see #getPortRepToGeneratorMap()
	 * @generated
	 */
	EAttribute getPortRepToGeneratorMap_Generator();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.codegen.PortRepToGeneratorMap#getRepId <em>Rep Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Rep Id</em>'.
	 * @see gov.redhawk.ide.codegen.PortRepToGeneratorMap#getRepId()
	 * @see #getPortRepToGeneratorMap()
	 * @generated
	 */
	EAttribute getPortRepToGeneratorMap_RepId();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	CodegenFactory getCodegenFactory();

	/**
	 * <!-- begin-user-doc --> Defines literals for the meta objects that
	 * represent
	 * <ul>
	 * <li>each class,</li>
	 * <li>each feature of each class,</li>
	 * <li>each enum,</li>
	 * <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {

		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.codegen.impl.ImplementationSettingsImpl <em>Implementation Settings</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.codegen.impl.ImplementationSettingsImpl
		 * @see gov.redhawk.ide.codegen.impl.CodegenPackageImpl#getImplementationSettings()
		 * @generated
		 */
		EClass IMPLEMENTATION_SETTINGS = eINSTANCE.getImplementationSettings();
		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IMPLEMENTATION_SETTINGS__NAME = eINSTANCE.getImplementationSettings_Name();
		/**
		 * The meta object literal for the '<em><b>Output Dir</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IMPLEMENTATION_SETTINGS__OUTPUT_DIR = eINSTANCE.getImplementationSettings_OutputDir();
		/**
		 * The meta object literal for the '<em><b>Template</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IMPLEMENTATION_SETTINGS__TEMPLATE = eINSTANCE.getImplementationSettings_Template();
		/**
		 * The meta object literal for the '<em><b>Properties</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference IMPLEMENTATION_SETTINGS__PROPERTIES = eINSTANCE.getImplementationSettings_Properties();
		/**
		 * The meta object literal for the '<em><b>Generator Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IMPLEMENTATION_SETTINGS__GENERATOR_ID = eINSTANCE.getImplementationSettings_GeneratorId();
		/**
		 * The meta object literal for the '<em><b>Generated On</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IMPLEMENTATION_SETTINGS__GENERATED_ON = eINSTANCE.getImplementationSettings_GeneratedOn();
		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IMPLEMENTATION_SETTINGS__ID = eINSTANCE.getImplementationSettings_Id();
		/**
		 * The meta object literal for the '<em><b>Generated File CR Cs</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference IMPLEMENTATION_SETTINGS__GENERATED_FILE_CR_CS = eINSTANCE.getImplementationSettings_GeneratedFileCRCs();
		/**
		 * The meta object literal for the '<em><b>Port Generators</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference IMPLEMENTATION_SETTINGS__PORT_GENERATORS = eINSTANCE.getImplementationSettings_PortGenerators();
		/**
		 * The meta object literal for the '<em><b>Primary</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IMPLEMENTATION_SETTINGS__PRIMARY = eINSTANCE.getImplementationSettings_Primary();
		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.codegen.impl.PropertyImpl <em>Property</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.codegen.impl.PropertyImpl
		 * @see gov.redhawk.ide.codegen.impl.CodegenPackageImpl#getProperty()
		 * @generated
		 */
		EClass PROPERTY = eINSTANCE.getProperty();
		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROPERTY__ID = eINSTANCE.getProperty_Id();
		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROPERTY__VALUE = eINSTANCE.getProperty_Value();
		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.codegen.impl.WaveDevSettingsImpl <em>Wave Dev Settings</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.codegen.impl.WaveDevSettingsImpl
		 * @see gov.redhawk.ide.codegen.impl.CodegenPackageImpl#getWaveDevSettings()
		 * @generated
		 */
		EClass WAVE_DEV_SETTINGS = eINSTANCE.getWaveDevSettings();
		/**
		 * The meta object literal for the '<em><b>Impl Settings</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference WAVE_DEV_SETTINGS__IMPL_SETTINGS = eINSTANCE.getWaveDevSettings_ImplSettings();
		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.codegen.impl.ImplIdToSettingsMapImpl <em>Impl Id To Settings Map</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.codegen.impl.ImplIdToSettingsMapImpl
		 * @see gov.redhawk.ide.codegen.impl.CodegenPackageImpl#getImplIdToSettingsMap()
		 * @generated
		 */
		EClass IMPL_ID_TO_SETTINGS_MAP = eINSTANCE.getImplIdToSettingsMap();
		/**
		 * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference IMPL_ID_TO_SETTINGS_MAP__VALUE = eINSTANCE.getImplIdToSettingsMap_Value();
		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute IMPL_ID_TO_SETTINGS_MAP__KEY = eINSTANCE.getImplIdToSettingsMap_Key();
		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.codegen.impl.FileToCRCMapImpl <em>File To CRC Map</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.codegen.impl.FileToCRCMapImpl
		 * @see gov.redhawk.ide.codegen.impl.CodegenPackageImpl#getFileToCRCMap()
		 * @generated
		 */
		EClass FILE_TO_CRC_MAP = eINSTANCE.getFileToCRCMap();
		/**
		 * The meta object literal for the '<em><b>Crc</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FILE_TO_CRC_MAP__CRC = eINSTANCE.getFileToCRCMap_Crc();
		/**
		 * The meta object literal for the '<em><b>File</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FILE_TO_CRC_MAP__FILE = eINSTANCE.getFileToCRCMap_File();
		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.codegen.impl.PortRepToGeneratorMapImpl <em>Port Rep To Generator Map</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.codegen.impl.PortRepToGeneratorMapImpl
		 * @see gov.redhawk.ide.codegen.impl.CodegenPackageImpl#getPortRepToGeneratorMap()
		 * @generated
		 */
		EClass PORT_REP_TO_GENERATOR_MAP = eINSTANCE.getPortRepToGeneratorMap();
		/**
		 * The meta object literal for the '<em><b>Generator</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PORT_REP_TO_GENERATOR_MAP__GENERATOR = eINSTANCE.getPortRepToGeneratorMap_Generator();
		/**
		 * The meta object literal for the '<em><b>Rep Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PORT_REP_TO_GENERATOR_MAP__REP_ID = eINSTANCE.getPortRepToGeneratorMap_RepId();

	}

} // CodegenPackage
