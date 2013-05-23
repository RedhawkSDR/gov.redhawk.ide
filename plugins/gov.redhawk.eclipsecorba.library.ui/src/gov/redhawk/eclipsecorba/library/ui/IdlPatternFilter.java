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
package gov.redhawk.eclipsecorba.library.ui;

import gov.redhawk.eclipsecorba.idl.IdlInterfaceDcl;
import gov.redhawk.eclipsecorba.idl.Module;
import gov.redhawk.eclipsecorba.library.RepositoryModule;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * Filter for IDL repIds.
 * @since 1.1
 */
public class IdlPatternFilter extends PatternFilter {
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

	@Override
	public boolean isElementVisible(final Viewer viewer, final Object element) {
		String myName = "";
		// Always show the PendingUpdateAdapter
		if (element instanceof IdlRepositoryPendingUpdateAdapter) {
			return true;
		} else if (element instanceof Module || element instanceof RepositoryModule) {
			if (element instanceof Module) {
				myName = ((Module) element).getRepId();
			} else if (element instanceof RepositoryModule) {
				myName = ((RepositoryModule) element).getRepId();
			}
			return isParentMatch(viewer, element) || wordMatches(myName);
		} else if (element instanceof IdlInterfaceDcl) {
			final IdlInterfaceDcl inter = (IdlInterfaceDcl) element;

			if (inter.eContainer() instanceof Module) {
				myName = ((Module) inter.eContainer()).getRepId();
			} else if (inter.eContainer() instanceof RepositoryModule) {
				myName = ((RepositoryModule) inter.eContainer()).getRepId();
			}
			return isLeafMatch(viewer, element) || wordMatches(myName);
		}
		return false;
	}

	// Match the repId instead of the label if it is an interface
	@Override
	protected boolean isLeafMatch(final Viewer viewer, final Object element) {
		if (element instanceof IdlInterfaceDcl) {
			final IdlInterfaceDcl inter = (IdlInterfaceDcl) element;
			return wordMatches(inter.getRepId());
		}
		return super.isLeafMatch(viewer, element);

	}

	// Match the filter text against the whole input rather than
	// splitting into words
	@Override
	protected boolean wordMatches(final String text) {
		if (text.contains("/")) {
			return this.p.matcher(text).matches();
		}
		return super.wordMatches(text);
	}
}
