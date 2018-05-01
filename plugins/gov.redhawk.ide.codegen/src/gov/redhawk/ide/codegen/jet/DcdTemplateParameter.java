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
package gov.redhawk.ide.codegen.jet;

import mil.jpeojtrs.sca.dcd.DeviceConfiguration;

public class DcdTemplateParameter extends CommonTemplateParameter<DeviceConfiguration> {

	public DcdTemplateParameter(DeviceConfiguration dcd, String headerContent) {
		super(dcd, headerContent);
	}

	public DeviceConfiguration getDcd() {
		return getModel();
	}

}
