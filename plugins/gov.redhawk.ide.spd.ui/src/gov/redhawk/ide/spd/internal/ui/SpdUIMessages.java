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
package gov.redhawk.ide.spd.internal.ui;

import org.eclipse.osgi.util.NLS;

/**
 * 
 */
public class SpdUIMessages extends NLS {
	private static final String BUNDLE_NAME = "gov.redhawk.ide.spd.internal.ui.messages"; //$NON-NLS-1$
	public static String ExportingSection_FORM; // SUPPRESS CHECKSTYLE StaticVariableNames
	public static String ExportingSection_SECTION_TITLE; // SUPPRESS CHECKSTYLE StaticVariableNames
	static {
		// initialize resource bundle
		NLS.initializeMessages(SpdUIMessages.BUNDLE_NAME, SpdUIMessages.class);
	}

	private SpdUIMessages() {
	}
}
