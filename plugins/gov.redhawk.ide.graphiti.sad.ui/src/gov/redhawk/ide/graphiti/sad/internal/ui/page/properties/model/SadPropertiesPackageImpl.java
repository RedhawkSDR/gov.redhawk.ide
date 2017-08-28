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
package gov.redhawk.ide.graphiti.sad.internal.ui.page.properties.model;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.EPackageImpl;

public class SadPropertiesPackageImpl extends EPackageImpl implements SadPropertiesPackage {
	private EClass sadPropertyEClass = null;

	public static SadPropertiesPackage init() {
		return new SadPropertiesPackageImpl();
	}

	private SadPropertiesPackageImpl() {
		sadPropertyEClass = createEClass(SadPropertiesPackage.SAD_PROPERTY);
		sadPropertyEClass.setName("SadProperty");

		createEAttribute(sadPropertyEClass, SadPropertiesPackage.SAD_PROPERTY__EXTERNAL_ID);
		createEAttribute(sadPropertyEClass, SadPropertiesPackage.SAD_PROPERTY__VALUE);
	}

	@Override
	public EClass getSadProperty() {
		return sadPropertyEClass;
	}

	@Override
	public EAttribute getSadProperty_ExternalId() {
		return (EAttribute) getSadProperty().getEStructuralFeatures().get(0);
	}

	@Override
	public EAttribute getSadProperty_Value() {
		return (EAttribute) getSadProperty().getEStructuralFeatures().get(1);
	}
}
