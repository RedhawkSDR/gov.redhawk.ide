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
package gov.redhawk.ide.ui.wizard;

import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

/**
 * @since 9.1
 */
public abstract class RedhawkImportUtil {
	//CHECKSTYLE:OFF
	public static String sadExtension = ".+\\.sad.xml";
	public static String spdExtension = ".+\\.spd.xml";
	public static String dcdExtension = ".+\\.dcd.xml";

	protected abstract int findMissingFiles();

	protected abstract IProject createDotProjectFile(String projectType);

	protected abstract void createWaveDevFile() throws CoreException;

	public abstract SoftPkg getSoftPkg(String path);
}
