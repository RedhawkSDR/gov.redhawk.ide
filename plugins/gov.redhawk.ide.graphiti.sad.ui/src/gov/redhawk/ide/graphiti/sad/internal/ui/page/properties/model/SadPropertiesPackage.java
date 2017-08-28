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

public interface SadPropertiesPackage {
	public static SadPropertiesPackage eINSTANCE = SadPropertiesPackageImpl.init(); // SUPPRESS CHECKSTYLE for now

	public EClass getSadProperty();

	public EAttribute getSadProperty_Value();

	public EAttribute getSadProperty_ExternalId();

	public static int SAD_PROPERTY = 0;
	public static int SAD_PROPERTY__EXTERNAL_ID = 0; // SUPPRESS CHECKSTYLE for now
	public static int SAD_PROPERTY__VALUE = 1; // SUPPRESS CHECKSTYLE for now

	public interface Literals { // SUPPRESS CHECKSTYLE for now
		public static EClass SAD_PROPERTY = SadPropertiesPackage.eINSTANCE.getSadProperty();
		public static EAttribute SAD_PROPERTY__VALUE = SadPropertiesPackage.eINSTANCE.getSadProperty_Value(); // SUPPRESS CHECKSTYLE for now
		public static EAttribute SAD_PROPERTY__EXTERNAL_ID = SadPropertiesPackage.eINSTANCE.getSadProperty_ExternalId(); // SUPPRESS CHECKSTYLE for now
	}
}
