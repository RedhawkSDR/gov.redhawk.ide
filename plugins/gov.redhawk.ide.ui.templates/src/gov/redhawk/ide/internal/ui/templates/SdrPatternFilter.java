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
package gov.redhawk.ide.internal.ui.templates;

import gov.redhawk.ide.sdr.SoftPkgRegistry;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * 
 */
public class SdrPatternFilter extends PatternFilter {
	private Pattern p = Pattern.compile(".*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	@Override
	public void setPattern(String patternString) {
		if (patternString == null) {
			patternString = "";
		}
		patternString = patternString.replaceAll("\\*", ".*");
		patternString = patternString.replaceAll("\\?", ".?");
		try {
			this.p = Pattern.compile(".*" + patternString + ".*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		} catch (final PatternSyntaxException e) {
			this.p = Pattern.compile(".*" + Pattern.quote(patternString) + ".*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		}
		super.setPattern(patternString);
	}

	/*
	 * @see org.eclipse.ui.dialogs.PatternFilter#isElementVisible(org.eclipse.jface.viewers.Viewer, java.lang.Object)
	 */
	@Override
	public boolean isElementVisible(Viewer viewer, Object element) {
		if (element instanceof SoftPkgRegistry) {
			return true;
		}
		return super.isElementVisible(viewer, element);
	}
}
