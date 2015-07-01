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
package gov.redhawk.ide.sad.internal.ui.properties.model;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;

public interface ViewerPackage {
	public static ViewerPackage eINSTANCE = ViewerPackageImpl.init();

	public EClass getSadProperty();

	public EAttribute getSadProperty_Value();

	public EAttribute getSadProperty_ExternalId();

	public static int SAD_PROPERTY = 0;
	public static int SAD_PROPERTY__EXTERNAL_ID = 0;
	public static int SAD_PROPERTY__VALUE = 1;

	public interface Literals {
		public static EClass SAD_PROPERTY = ViewerPackage.eINSTANCE.getSadProperty();
		public static EAttribute SAD_PROPERTY__VALUE = ViewerPackage.eINSTANCE.getSadProperty_Value();
		public static EAttribute SAD_PROPERTY__EXTERNAL_ID = ViewerPackage.eINSTANCE.getSadProperty_ExternalId();
	}
}
