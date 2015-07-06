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

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

public class SadPropertiesAdapterFactory extends AdapterFactoryImpl {

	@Override
	public boolean isFactoryForType(Object type) {
		if (type instanceof SoftwareAssembly) {
			return true;
		} else if (type instanceof SadComponentInstantiation) {
			return true;
		}
		return super.isFactoryForType(type);
	}

	@Override
	protected Adapter createAdapter(Notifier target) {
		if (target instanceof SoftwareAssembly) {
			return new ViewerApplication(this);
		} else if (target instanceof SadComponentInstantiation) {
			return new ViewerComponent(this, (SadComponentInstantiation) target);
		}
		return super.createAdapter(target);
	}

}
