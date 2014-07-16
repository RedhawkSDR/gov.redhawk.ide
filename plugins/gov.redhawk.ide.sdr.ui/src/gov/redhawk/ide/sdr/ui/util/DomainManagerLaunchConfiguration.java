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
package gov.redhawk.ide.sdr.ui.util;

/**
 * @since 3.3
 */
public class DomainManagerLaunchConfiguration {

	public static final String PROP_DOMAIN_NAME = "domainName";
	public static final String PROP_DEBUG_LEVEL = "debugLevel";
	public static final String PROP_ARGUMENTS = "arguments";
	
	private String localDomainName = null;
	private String domainName = "";
	private DebugLevel debugLevel = DebugLevel.Info;
	private String arguments = "";

	/**
	 * @return the domainName
	 */
	public String getDomainName() {
		return domainName;
	}

	/**
	 * @param domainName the domainName to set
	 */
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	/**
	 * @return the debugLevel
	 */
	public DebugLevel getDebugLevel() {
		return debugLevel;
	}

	/**
	 * @param debugLevel the debugLevel to set
	 */
	public void setDebugLevel(DebugLevel debugLevel) {
		if (debugLevel == null) {
			debugLevel = DebugLevel.Info;
		}
		this.debugLevel = debugLevel;
	}

	public String getArguments() {
		return arguments;
	}

	public void setArguments(String arguments) {
		this.arguments = arguments;
	}
	
	public String getLocalDomainName() {
		return localDomainName;
	}
	
	public void setLocalDomainName(String localDomainName) {
		this.localDomainName = localDomainName;
	}

}
