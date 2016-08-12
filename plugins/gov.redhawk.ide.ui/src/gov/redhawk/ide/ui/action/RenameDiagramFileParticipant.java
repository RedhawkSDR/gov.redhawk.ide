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
package gov.redhawk.ide.ui.action;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.search.ui.text.FileTextSearchScope;

import gov.redhawk.ide.natures.ScaNodeProjectNature;
import gov.redhawk.ide.natures.ScaWaveformProjectNature;
import mil.jpeojtrs.sca.dcd.DcdPackage;
import mil.jpeojtrs.sca.sad.SadPackage;

/**
 * @since 10.0
 */
public class RenameDiagramFileParticipant extends RenameFileParticipant {

	@Override
	protected Map<FileTextSearchScope, Pattern> createNamePatternMap() {
		final String oldProjectName = getCurrentProject().getName();

		/* A Map where:
		 * - Key is the name of the files we want to look in
		 * - Value is the pattern we are using to find the text we want to replace
		 */
		Map<String[], String> filePatterns = new HashMap<String[], String>();
		filePatterns.put(new String[] { "*" + SadPackage.FILE_EXTENSION }, "(?:softwareassembly.*)" + oldProjectName);
		filePatterns.put(new String[] { "*" + DcdPackage.FILE_EXTENSION }, "(?:deviceconfiguration.*)" + oldProjectName);

		Map<FileTextSearchScope, Pattern> namePatternMap = super.createNamePatternMap();
		for (Entry<String[], String> filePattern : filePatterns.entrySet()) {
			FileTextSearchScope scope = FileTextSearchScope.newSearchScope(new IResource[] { getCurrentProject() }, filePattern.getKey(), true);
			Pattern pattern = Pattern.compile(filePattern.getValue());
			namePatternMap.put(scope, pattern);
		}

		return namePatternMap;
	}

	@Override
	public String getName() {
		return "Refactoring associated Diagram file.";
	}

	@Override
	protected boolean hasCorrectNature(IProject project) throws CoreException {
		return (project.hasNature(ScaNodeProjectNature.ID) || project.hasNature(ScaWaveformProjectNature.ID));
	}
}
