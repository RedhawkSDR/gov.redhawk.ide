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
package gov.redhawk.ide.codegen.impl;

import gov.redhawk.ide.codegen.CodegenPackage;
import gov.redhawk.ide.codegen.FileToCRCMap;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.PortRepToGeneratorMap;
import gov.redhawk.ide.codegen.Property;

import java.util.Collection;
import java.util.Date;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Implementation Settings</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gov.redhawk.ide.codegen.impl.ImplementationSettingsImpl#getName <em>Name</em>}</li>
 *   <li>{@link gov.redhawk.ide.codegen.impl.ImplementationSettingsImpl#getOutputDir <em>Output Dir</em>}</li>
 *   <li>{@link gov.redhawk.ide.codegen.impl.ImplementationSettingsImpl#getTemplate <em>Template</em>}</li>
 *   <li>{@link gov.redhawk.ide.codegen.impl.ImplementationSettingsImpl#getProperties <em>Properties</em>}</li>
 *   <li>{@link gov.redhawk.ide.codegen.impl.ImplementationSettingsImpl#getGeneratorId <em>Generator Id</em>}</li>
 *   <li>{@link gov.redhawk.ide.codegen.impl.ImplementationSettingsImpl#getGeneratedOn <em>Generated On</em>}</li>
 *   <li>{@link gov.redhawk.ide.codegen.impl.ImplementationSettingsImpl#getId <em>Id</em>}</li>
 *   <li>{@link gov.redhawk.ide.codegen.impl.ImplementationSettingsImpl#getGeneratedFileCRCs <em>Generated File CR Cs</em>}</li>
 *   <li>{@link gov.redhawk.ide.codegen.impl.ImplementationSettingsImpl#getPortGenerators <em>Port Generators</em>}</li>
 *   <li>{@link gov.redhawk.ide.codegen.impl.ImplementationSettingsImpl#isPrimary <em>Primary</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ImplementationSettingsImpl extends EObjectImpl implements ImplementationSettings {
	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;
	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;
	/**
	 * The default value of the '{@link #getOutputDir() <em>Output Dir</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOutputDir()
	 * @generated
	 * @ordered
	 */
	protected static final String OUTPUT_DIR_EDEFAULT = null;
	/**
	 * The cached value of the '{@link #getOutputDir() <em>Output Dir</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOutputDir()
	 * @generated
	 * @ordered
	 */
	protected String outputDir = OUTPUT_DIR_EDEFAULT;
	/**
	 * The default value of the '{@link #getTemplate() <em>Template</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTemplate()
	 * @generated
	 * @ordered
	 */
	protected static final String TEMPLATE_EDEFAULT = null;
	/**
	 * The cached value of the '{@link #getTemplate() <em>Template</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTemplate()
	 * @generated
	 * @ordered
	 */
	protected String template = TEMPLATE_EDEFAULT;
	/**
	 * The cached value of the '{@link #getProperties() <em>Properties</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProperties()
	 * @generated
	 * @ordered
	 */
	protected EList<Property> properties;
	/**
	 * The default value of the '{@link #getGeneratorId() <em>Generator Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGeneratorId()
	 * @generated
	 * @ordered
	 */
	protected static final String GENERATOR_ID_EDEFAULT = null;
	/**
	 * The cached value of the '{@link #getGeneratorId() <em>Generator Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGeneratorId()
	 * @generated
	 * @ordered
	 */
	protected String generatorId = GENERATOR_ID_EDEFAULT;
	/**
	 * The default value of the '{@link #getGeneratedOn() <em>Generated On</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGeneratedOn()
	 * @generated
	 * @ordered
	 */
	protected static final Date GENERATED_ON_EDEFAULT = null;
	/**
	 * The cached value of the '{@link #getGeneratedOn() <em>Generated On</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGeneratedOn()
	 * @generated
	 * @ordered
	 */
	protected Date generatedOn = GENERATED_ON_EDEFAULT;
	/**
	 * The default value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_EDEFAULT = null;
	/**
	 * The cached value of the '{@link #getGeneratedFileCRCs() <em>Generated File CR Cs</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGeneratedFileCRCs()
	 * @generated
	 * @ordered
	 */
	protected EList<FileToCRCMap> generatedFileCRCs;
	/**
	 * The cached value of the '{@link #getPortGenerators() <em>Port Generators</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPortGenerators()
	 * @generated
	 * @ordered
	 */
	protected EList<PortRepToGeneratorMap> portGenerators;
	/**
	 * The default value of the '{@link #isPrimary() <em>Primary</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isPrimary()
	 * @generated
	 * @ordered
	 */
	protected static final boolean PRIMARY_EDEFAULT = false;
	/**
	 * The cached value of the '{@link #isPrimary() <em>Primary</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isPrimary()
	 * @generated
	 * @ordered
	 */
	protected boolean primary = PRIMARY_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ImplementationSettingsImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CodegenPackage.Literals.IMPLEMENTATION_SETTINGS;
	}

	/**
	 * <!-- begin-user-doc -->
	 * @deprecated
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Deprecated
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * @deprecated
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Deprecated
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.IMPLEMENTATION_SETTINGS__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getOutputDir() {
		return outputDir;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setOutputDir(String newOutputDir) {
		String oldOutputDir = outputDir;
		outputDir = newOutputDir;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.IMPLEMENTATION_SETTINGS__OUTPUT_DIR, oldOutputDir, outputDir));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getTemplate() {
		return template;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTemplate(String newTemplate) {
		String oldTemplate = template;
		template = newTemplate;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.IMPLEMENTATION_SETTINGS__TEMPLATE, oldTemplate, template));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Property> getProperties() {
		if (properties == null) {
			properties = new EObjectContainmentEList<Property>(Property.class, this, CodegenPackage.IMPLEMENTATION_SETTINGS__PROPERTIES);
		}
		return properties;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getGeneratorId() {
		return generatorId;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setGeneratorId(String newGeneratorId) {
		String oldGeneratorId = generatorId;
		generatorId = newGeneratorId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.IMPLEMENTATION_SETTINGS__GENERATOR_ID, oldGeneratorId, generatorId));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Date getGeneratedOn() {
		return generatedOn;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setGeneratedOn(Date newGeneratedOn) {
		Date oldGeneratedOn = generatedOn;
		generatedOn = newGeneratedOn;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.IMPLEMENTATION_SETTINGS__GENERATED_ON, oldGeneratedOn, generatedOn));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public String getId() {
		// END GENERATED CODE
		final ImplIdToSettingsMapImpl entry = (ImplIdToSettingsMapImpl) this.eContainer;
		if (entry == null) {
			return null;
		}
		return entry.key;
		// BEGIN GENERATED CODE
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<FileToCRCMap> getGeneratedFileCRCs() {
		if (generatedFileCRCs == null) {
			generatedFileCRCs = new EObjectContainmentEList<FileToCRCMap>(FileToCRCMap.class, this, CodegenPackage.IMPLEMENTATION_SETTINGS__GENERATED_FILE_CR_CS);
		}
		return generatedFileCRCs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<PortRepToGeneratorMap> getPortGenerators() {
		if (portGenerators == null) {
			portGenerators = new EObjectContainmentEList<PortRepToGeneratorMap>(PortRepToGeneratorMap.class, this, CodegenPackage.IMPLEMENTATION_SETTINGS__PORT_GENERATORS);
		}
		return portGenerators;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isPrimary() {
		return primary;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPrimary(boolean newPrimary) {
		boolean oldPrimary = primary;
		primary = newPrimary;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.IMPLEMENTATION_SETTINGS__PRIMARY, oldPrimary, primary));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case CodegenPackage.IMPLEMENTATION_SETTINGS__PROPERTIES:
				return ((InternalEList<?>)getProperties()).basicRemove(otherEnd, msgs);
			case CodegenPackage.IMPLEMENTATION_SETTINGS__GENERATED_FILE_CR_CS:
				return ((InternalEList<?>)getGeneratedFileCRCs()).basicRemove(otherEnd, msgs);
			case CodegenPackage.IMPLEMENTATION_SETTINGS__PORT_GENERATORS:
				return ((InternalEList<?>)getPortGenerators()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case CodegenPackage.IMPLEMENTATION_SETTINGS__NAME:
				return getName();
			case CodegenPackage.IMPLEMENTATION_SETTINGS__OUTPUT_DIR:
				return getOutputDir();
			case CodegenPackage.IMPLEMENTATION_SETTINGS__TEMPLATE:
				return getTemplate();
			case CodegenPackage.IMPLEMENTATION_SETTINGS__PROPERTIES:
				return getProperties();
			case CodegenPackage.IMPLEMENTATION_SETTINGS__GENERATOR_ID:
				return getGeneratorId();
			case CodegenPackage.IMPLEMENTATION_SETTINGS__GENERATED_ON:
				return getGeneratedOn();
			case CodegenPackage.IMPLEMENTATION_SETTINGS__ID:
				return getId();
			case CodegenPackage.IMPLEMENTATION_SETTINGS__GENERATED_FILE_CR_CS:
				return getGeneratedFileCRCs();
			case CodegenPackage.IMPLEMENTATION_SETTINGS__PORT_GENERATORS:
				return getPortGenerators();
			case CodegenPackage.IMPLEMENTATION_SETTINGS__PRIMARY:
				return isPrimary();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case CodegenPackage.IMPLEMENTATION_SETTINGS__NAME:
				setName((String)newValue);
				return;
			case CodegenPackage.IMPLEMENTATION_SETTINGS__OUTPUT_DIR:
				setOutputDir((String)newValue);
				return;
			case CodegenPackage.IMPLEMENTATION_SETTINGS__TEMPLATE:
				setTemplate((String)newValue);
				return;
			case CodegenPackage.IMPLEMENTATION_SETTINGS__PROPERTIES:
				getProperties().clear();
				getProperties().addAll((Collection<? extends Property>)newValue);
				return;
			case CodegenPackage.IMPLEMENTATION_SETTINGS__GENERATOR_ID:
				setGeneratorId((String)newValue);
				return;
			case CodegenPackage.IMPLEMENTATION_SETTINGS__GENERATED_ON:
				setGeneratedOn((Date)newValue);
				return;
			case CodegenPackage.IMPLEMENTATION_SETTINGS__GENERATED_FILE_CR_CS:
				getGeneratedFileCRCs().clear();
				getGeneratedFileCRCs().addAll((Collection<? extends FileToCRCMap>)newValue);
				return;
			case CodegenPackage.IMPLEMENTATION_SETTINGS__PORT_GENERATORS:
				getPortGenerators().clear();
				getPortGenerators().addAll((Collection<? extends PortRepToGeneratorMap>)newValue);
				return;
			case CodegenPackage.IMPLEMENTATION_SETTINGS__PRIMARY:
				setPrimary((Boolean)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case CodegenPackage.IMPLEMENTATION_SETTINGS__NAME:
				setName(NAME_EDEFAULT);
				return;
			case CodegenPackage.IMPLEMENTATION_SETTINGS__OUTPUT_DIR:
				setOutputDir(OUTPUT_DIR_EDEFAULT);
				return;
			case CodegenPackage.IMPLEMENTATION_SETTINGS__TEMPLATE:
				setTemplate(TEMPLATE_EDEFAULT);
				return;
			case CodegenPackage.IMPLEMENTATION_SETTINGS__PROPERTIES:
				getProperties().clear();
				return;
			case CodegenPackage.IMPLEMENTATION_SETTINGS__GENERATOR_ID:
				setGeneratorId(GENERATOR_ID_EDEFAULT);
				return;
			case CodegenPackage.IMPLEMENTATION_SETTINGS__GENERATED_ON:
				setGeneratedOn(GENERATED_ON_EDEFAULT);
				return;
			case CodegenPackage.IMPLEMENTATION_SETTINGS__GENERATED_FILE_CR_CS:
				getGeneratedFileCRCs().clear();
				return;
			case CodegenPackage.IMPLEMENTATION_SETTINGS__PORT_GENERATORS:
				getPortGenerators().clear();
				return;
			case CodegenPackage.IMPLEMENTATION_SETTINGS__PRIMARY:
				setPrimary(PRIMARY_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case CodegenPackage.IMPLEMENTATION_SETTINGS__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case CodegenPackage.IMPLEMENTATION_SETTINGS__OUTPUT_DIR:
				return OUTPUT_DIR_EDEFAULT == null ? outputDir != null : !OUTPUT_DIR_EDEFAULT.equals(outputDir);
			case CodegenPackage.IMPLEMENTATION_SETTINGS__TEMPLATE:
				return TEMPLATE_EDEFAULT == null ? template != null : !TEMPLATE_EDEFAULT.equals(template);
			case CodegenPackage.IMPLEMENTATION_SETTINGS__PROPERTIES:
				return properties != null && !properties.isEmpty();
			case CodegenPackage.IMPLEMENTATION_SETTINGS__GENERATOR_ID:
				return GENERATOR_ID_EDEFAULT == null ? generatorId != null : !GENERATOR_ID_EDEFAULT.equals(generatorId);
			case CodegenPackage.IMPLEMENTATION_SETTINGS__GENERATED_ON:
				return GENERATED_ON_EDEFAULT == null ? generatedOn != null : !GENERATED_ON_EDEFAULT.equals(generatedOn);
			case CodegenPackage.IMPLEMENTATION_SETTINGS__ID:
				return ID_EDEFAULT == null ? getId() != null : !ID_EDEFAULT.equals(getId());
			case CodegenPackage.IMPLEMENTATION_SETTINGS__GENERATED_FILE_CR_CS:
				return generatedFileCRCs != null && !generatedFileCRCs.isEmpty();
			case CodegenPackage.IMPLEMENTATION_SETTINGS__PORT_GENERATORS:
				return portGenerators != null && !portGenerators.isEmpty();
			case CodegenPackage.IMPLEMENTATION_SETTINGS__PRIMARY:
				return primary != PRIMARY_EDEFAULT;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (name: ");
		result.append(name);
		result.append(", outputDir: ");
		result.append(outputDir);
		result.append(", template: ");
		result.append(template);
		result.append(", generatorId: ");
		result.append(generatorId);
		result.append(", generatedOn: ");
		result.append(generatedOn);
		result.append(", primary: ");
		result.append(primary);
		result.append(')');
		return result.toString();
	}

} // ImplementationSettingsImpl
