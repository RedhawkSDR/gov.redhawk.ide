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
package gov.redhawk.ide.codegen.args;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 9.0
 */
public class GeneratorArgsBase {
	private Map<String, String> m = new HashMap<String, String>();
	
	public static final String PROJECT_NAME_KEY = "project_name";
	public static final String SOFTPKG_FILE_KEY = "softpkg_file";
	public static final String AUTHOR_NAME_KEY = "author_name";
	public static final String PRF_FILE_KEY = "prf_file";
	public static final String SCD_FILE_KEY = "scd_file";
	
	/**
	 * @since 10.0
	 */
	public static final String SOFT_PKG_ID_KEY = "soft_pkg_id";
	/**
	 * @deprecated Use {@link #SOFT_PKG_ID_KEY} instead
	 */
	@Deprecated
	public static final String PROJECT_ID_KEY = SOFT_PKG_ID_KEY;

	public void setProperty(String k, String v) {
		m.put(k, v);
	}
	
	public String getProperty(String k) {
		return m.get(k);
	}
	
	public void setProjectName(final String projectName) {
		this.setProperty(PROJECT_NAME_KEY, projectName);
	}

	public String getProjectName() {
		return this.getProperty(PROJECT_NAME_KEY);
	}
	
	/**
	 * @deprecated Use {@link #setProperty(SOFTPKG_FILE_KEY, softPkgFile)}
	 */
	@Deprecated
	public void setSoftPkgFile(final String softPkgFile) {
		this.setProperty(SOFTPKG_FILE_KEY, softPkgFile);
	}
	/**
	 * @deprecated Use {@link #getProperty(SOFTPKG_FILE_KEY)}
	 */
	@Deprecated
	public String getSoftPkgFile() {
		return this.getProperty(SOFTPKG_FILE_KEY);
	}
	
	/**
	 * @deprecated Use setSoftPkgId instead.
	 */
	@Deprecated
	public void setProjectId(final String projectId) {
		this.setProperty(SOFT_PKG_ID_KEY, projectId);
	}
	
	/**
	 * @deprecated Use getSoftPkgId instead.
	 */
	@Deprecated
	public String getProjectId() {
		return this.getProperty(SOFT_PKG_ID_KEY);
	}
	
	public void setAuthorName(final String authorName) {
		this.setProperty(AUTHOR_NAME_KEY, authorName);
	}

	public String getAuthorName() {
		return this.getProperty(AUTHOR_NAME_KEY);
	}
	
	/**
	 * @since 10.0
	 */
	public void setSoftPkgId(String pkgID) {
		this.setProperty(SOFT_PKG_ID_KEY, pkgID);
	}
	/**
	 * @since 10.0
	 */
	public String getSoftPkgId() {
		return this.getProperty(SOFT_PKG_ID_KEY);
	}
	
	public void setPrfFile(String prfFile) {
		this.setProperty(PRF_FILE_KEY, prfFile);
	}

	public String getPrfFile() {
		return this.getProperty(PRF_FILE_KEY);
	}
	
	public void setScdFile(String scdFile) {
		this.setProperty(SCD_FILE_KEY, scdFile);
	}

	public String getScdFile() {
		return this.getProperty(SCD_FILE_KEY);
	}
	
}
