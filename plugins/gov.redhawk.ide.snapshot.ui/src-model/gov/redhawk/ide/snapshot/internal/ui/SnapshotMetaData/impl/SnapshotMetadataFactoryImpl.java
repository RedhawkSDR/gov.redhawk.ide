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
//BEGIN GENERATED CODE
package gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl;

import gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.*;

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
public class SnapshotMetadataFactoryImpl extends EFactoryImpl implements SnapshotMetadataFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static SnapshotMetadataFactory init() {
		try {
			SnapshotMetadataFactory theSnapshotMetadataFactory = (SnapshotMetadataFactory) EPackage.Registry.INSTANCE.getEFactory(SnapshotMetadataPackage.eNS_URI);
			if (theSnapshotMetadataFactory != null) {
				return theSnapshotMetadataFactory;
			}
		} catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new SnapshotMetadataFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SnapshotMetadataFactoryImpl() {
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
		case SnapshotMetadataPackage.CF_DATA_TYPE:
			return createCFDataType();
		case SnapshotMetadataPackage.KEYWORDS_TYPE:
			return createKeywordsType();
		case SnapshotMetadataPackage.MODEL:
			return createModel();
		case SnapshotMetadataPackage.SRI:
			return createSRI();
		case SnapshotMetadataPackage.TIME:
			return createTime();
		case SnapshotMetadataPackage.VALUE:
			return createValue();
		default:
			throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CFDataType createCFDataType() {
		CFDataTypeImpl cfDataType = new CFDataTypeImpl();
		return cfDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public KeywordsType createKeywordsType() {
		KeywordsTypeImpl keywordsType = new KeywordsTypeImpl();
		return keywordsType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Model createModel() {
		ModelImpl model = new ModelImpl();
		return model;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SRI createSRI() {
		SRIImpl sri = new SRIImpl();
		return sri;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Time createTime() {
		TimeImpl time = new TimeImpl();
		return time;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Value createValue() {
		ValueImpl value = new ValueImpl();
		return value;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SnapshotMetadataPackage getSnapshotMetadataPackage() {
		return (SnapshotMetadataPackage) getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static SnapshotMetadataPackage getPackage() {
		return SnapshotMetadataPackage.eINSTANCE;
	}

} //SnapshotMetadataFactoryImpl
