/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package gov.redhawk.ide.sdr.nodebooter;

public enum DebugLevel {
	Fatal("0"),
	Error("1"),
	Warn("2"),
	Info("3"),
	Debug("4"),
	Trace("5");

	private String nodeBooterString;

	private DebugLevel(String nodeBooterString) {
		this.nodeBooterString = nodeBooterString;
	}

	public String getNodeBooterString() {
		return this.nodeBooterString;
	}
}
