/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.graphiti.ui.adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gov.redhawk.core.graphiti.ui.adapters.ContainerShapeAdapterFactory;
import gov.redhawk.ide.debug.LocalLaunch;

/**
 * Extends {@link ContainerShapeAdapterFactory} to also adapt to {@link LocalLaunch}.
 */
public class ContainerShapeAdapterFactory2 extends ContainerShapeAdapterFactory {

	private static final Class< ? >[] ADAPTER_TYPES = new Class< ? >[] { LocalLaunch.class };

	@Override
	public Class< ? >[] getAdapterList() {
		List<Class< ? >> adapterList = new ArrayList<>();
		Collections.addAll(adapterList, super.getAdapterList());
		Collections.addAll(adapterList, ADAPTER_TYPES);
		return adapterList.toArray(new Class< ? >[adapterList.size()]);
	}

}
