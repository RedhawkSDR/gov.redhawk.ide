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
package gov.redhawk.ide.spd.generator.newcomponent;

import gov.redhawk.ide.codegen.args.GeneratorArgsBase;

/**
 * The properties that can be set for the New Component generator.
 */
public class GeneratorArgs extends GeneratorArgsBase {
	public static final String SOFTPKG_NAME_KEY = "softpkg_name";
	public static final String SOFTPKG_ID_KEY = "softpkg_id";
	public static final String SOFTPKG_FILE_KEY = "softpkg_file";
	
	public void setSoftPkgName(final String softPkgName) {
		this.setProperty(SOFTPKG_NAME_KEY, softPkgName);
	}
	
	public String getSoftPkgName() {
		return this.getProperty(SOFTPKG_NAME_KEY);
	}

	public void setSoftPkgFile(final String softPkgFile) {
		this.setProperty(SOFTPKG_FILE_KEY, softPkgFile);
	}

	public String getSoftPkgFile() {
		return this.getProperty(SOFTPKG_FILE_KEY);
	}
	
	@Override
	public void setSoftPkgId(final String softPkgFile) {
		this.setProperty(SOFTPKG_ID_KEY, softPkgFile);
	}

	@Override
	public String getSoftPkgId() {
		return this.getProperty(SOFTPKG_ID_KEY);
	}

}
