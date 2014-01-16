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
package gov.redhawk.ide.debug.internal.ui.diagram;

import gov.redhawk.model.sca.ScaConnection;
import mil.jpeojtrs.sca.sad.SadConnectInterface;

import org.eclipse.jdt.annotation.Nullable;

class ConnectionMapEntry {
	private String key;
	private ScaConnection conn;
	private SadConnectInterface profile;

	private void setKey(String key) {
		if (this.key == null) {
			this.key = key;
		}
	}

	public ScaConnection getScaConnection() {
		return conn;
	}

	public void setScaConnection(ScaConnection conn) {
		this.conn = conn;
		if (conn != null) {
			setKey(conn.getId());
		}
	}

	public SadConnectInterface getProfile() {
		return profile;
	}

	public void setProfile(SadConnectInterface profile) {
		this.profile = profile;
		setKey(profile.getId());
	}

	@Nullable
	String getKey() {
		return key;
	}

	public static String getKey(ScaConnection obj) {
		return obj.getId();
	}

	public static String getKey(SadConnectInterface obj) {
		return obj.getId();
	}
}
