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

import gov.redhawk.ide.ui.templates.TemplatesActivator;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.Platform;
import org.eclipse.pde.ui.templates.OptionTemplateSection;
import org.osgi.framework.Bundle;

public abstract class ScaTemplateSection extends OptionTemplateSection {

	private static final String[] EMPTY_STRINGS_ARRAY = new String[0];

	@Override
	protected ResourceBundle getPluginResourceBundle() {
		final Bundle bundle = Platform.getBundle(TemplatesActivator.getPluginId());
		return Platform.getResourceBundle(bundle);
	}

	@Override
	protected URL getInstallURL() {
		return TemplatesActivator.getDefault().getInstallURL();
	}

	@Override
	public URL getTemplateLocation() {
		try {
			final String[] candidates = getDirectoryCandidates();
			for (int i = 0; i < candidates.length; i++) {
				if (TemplatesActivator.getDefault().getBundle().getEntry(candidates[i]) != null) {
					final URL candidate = new URL(getInstallURL(), candidates[i]);
					return candidate;
				}
			}
		} catch (final MalformedURLException e) {
			// PASS do nothing
		}
		return null;
	}

	private String[] getDirectoryCandidates() {
		final double version = getTargetVersion();
		final ArrayList<String> result = new ArrayList<String>();
		if (version >= 3.3) { // SUPPRESS CHECKSTYLE MagicNumber
			result.add("templates_3.3" + "/" + getSectionId() + "/"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		if (version >= 3.2) { // SUPPRESS CHECKSTYLE MagicNumber
			result.add("templates_3.2" + "/" + getSectionId() + "/"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		if (version >= 3.1) { // SUPPRESS CHECKSTYLE MagicNumber
			result.add("templates_3.1" + "/" + getSectionId() + "/"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		if (version >= 3.0) { // SUPPRESS CHECKSTYLE MagicNumber
			result.add("templates_3.0" + "/" + getSectionId() + "/"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		return result.toArray(new String[result.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.pde.ui.templates.ITemplateSection#getFoldersToInclude()
	 */
	@Override
	public String[] getNewFiles() {
		return EMPTY_STRINGS_ARRAY;
	}

	protected String getFormattedPackageName(final String id) {
		final StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < id.length(); i++) {
			final char ch = id.charAt(i);
			if (buffer.length() == 0) {
				if (Character.isJavaIdentifierStart(ch)) {
					buffer.append(Character.toLowerCase(ch));
				}
			} else {
				if (Character.isJavaIdentifierPart(ch) || ch == '.') {
					buffer.append(ch);
				}
			}
		}
		return buffer.toString().toLowerCase(Locale.ENGLISH);
	}

}
