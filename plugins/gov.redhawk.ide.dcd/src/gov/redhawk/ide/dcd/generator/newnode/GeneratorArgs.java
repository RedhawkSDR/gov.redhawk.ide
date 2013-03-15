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
package gov.redhawk.ide.dcd.generator.newnode;

import gov.redhawk.ide.codegen.args.GeneratorArgsBase;
import mil.jpeojtrs.sca.spd.SoftPkg;

/**
 * The properties that can be set for the New Node generator.
 */
public class GeneratorArgs extends GeneratorArgsBase {

	private String domainManagerName;
	private String nodeName;
	private String nodeId;
	
	private SoftPkg[] devices;

	public String getDomainManagerName() {
		return this.domainManagerName;
	}

	public void setDomainManagerName(final String domainManagerName) {
		this.domainManagerName = domainManagerName;
	}

	public SoftPkg[] getDevices() {
		return this.devices;
	}

	public void setDevices(final SoftPkg[] devices) {
		this.devices = devices;
	}

	public String getNodeName() {
	    return nodeName;
    }

	public void setNodeName(String nodeName) {
	    this.nodeName = nodeName;
    }

	public String getNodeId() {
	    return nodeId;
    }

	public void setNodeId(String nodeId) {
	    this.nodeId = nodeId;
    }
}
