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
package gov.redhawk.ide.codegen.util;

/**
 * A class with helper functions for Automake.
 * 
 * @since 8.0
 */
public class AutoMakeUtil {

	private AutoMakeUtil() {
	}

	/**
	 * Takes a file name and creates the Automake derived variable name used in a Makefile.
	 * <p />
	 * For example, suppose you are creating a library like so:
	 * <p />
	 * <code>lib_LIBRARIES = libmumble++.a</code>
	 * <p />
	 * The library's sources need to be specified, and for that the derived variable name is needed to create the
	 * appropriate rule. That rule might look something like:
	 * <p />
	 * <code>libmumble___a_SOURCES = a.cpp b.cpp</code>
	 * <p />
	 * This method transforms the input "<code>libmumble++.a</code>" into "<code>libmumble___a</code>" for use as a
	 * prefix.
	 * <p />
	 * See also <a href="http://www.gnu.org/software/automake/manual/automake.html#Canonicalization">http://www.gnu.org/software/automake/manual/automake.html#Canonicalization</a>
	 *  
	 * @param fileName The file name to transform
	 * @return The Automake-derived variable name
	 */
	public static String createDerivedVariableName(String fileName) {
		StringBuffer buffer = new StringBuffer(fileName);
		for (int i = 0; i < buffer.length(); i++) {
			char c = buffer.charAt(i);
			// letters, numbers and the strudel (@) are ok - all others become an underscore 
			if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || (c == '@'))) {
				buffer.setCharAt(i, '_');
			}
		}
		return buffer.toString();
	}

}
