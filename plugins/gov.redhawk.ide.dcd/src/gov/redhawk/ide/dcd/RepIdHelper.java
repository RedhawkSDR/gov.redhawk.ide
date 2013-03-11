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
package gov.redhawk.ide.dcd;

/**
 * @since 2.1
 */
public class RepIdHelper {
	private RepIdHelper() {
	}

	public static String getProperInterfaceName(final String repId) {
		if (repId == null) {
			throw new IllegalArgumentException("REPID must NOT be null");
		}
		final String[] split = repId.split(":");
		String idl;
		if (split.length >= 2) {
			idl = split[1];
		} else {
			throw new IllegalArgumentException(repId);
		}

		final String[] idlParts = idl.split("/");
		if (idlParts.length - 1 < 0) {
			throw new IllegalArgumentException(repId);
		}
		return idlParts[idlParts.length - 1];
	}
}
