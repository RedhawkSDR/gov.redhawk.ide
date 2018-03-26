/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.help;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.help.IToc;
import org.eclipse.help.ITocContribution;
import org.eclipse.help.ITopic;
import org.osgi.framework.Bundle;

/**
 * This class provides the table of contents (TOC) for Redhawk. The code ensures that the applicable HTML pages in the
 * documentation zip are shown as topics, and the sub-pages are indexed for search purposes.
 */
public class RedhawkTocContribution implements ITocContribution {

	private static final String DOC_ZIP = "/doc.zip";

	private List<String> paths;
	private IToc toc = new RedhawkToc();

	public RedhawkTocContribution() {
	}

	@Override
	public String getCategoryId() {
		return null;
	}

	@Override
	public String getContributorId() {
		return RedhawkHelpPlugin.PLUGIN_ID;
	}

	@Override
	public String[] getExtraDocuments() {
		if (paths == null) {
			getFilesForSearchIndex();
		}
		return paths.toArray(new String[paths.size()]);
	}

	@Override
	public String getId() {
		return RedhawkHelpPlugin.PLUGIN_ID + ".contribution";
	}

	@Override
	public String getLocale() {
		String locale = Platform.getNL();
		if (locale == null) {
			locale = Locale.getDefault().toString();
		}
		return locale;
	}

	@Override
	public String getLinkTo() {
		return null;
	}

	@Override
	public IToc getToc() {
		return toc;
	}

	@Override
	public boolean isPrimary() {
		return true;
	}

	/**
	 * Returns all HTML files in the documentation zip file which that start with the same prefix as one of the topics.
	 */
	private void getFilesForSearchIndex() {
		paths = new ArrayList<>();

		// Create a list of the prefix paths for our documentation
		List<String> prefixes = new ArrayList<>();
		for (ITopic topic : toc.getTopics()) {
			if (!(topic instanceof RedhawkTopic)) {
				continue;
			}
			String docPath = ((RedhawkTopic) topic).getDocPath();
			prefixes.add(docPath.substring(0, docPath.lastIndexOf('/') + 1));
		}

		// Read all entries in the documentation zip file
		try (ZipInputStream zis = new ZipInputStream(getClass().getResource(DOC_ZIP).openStream())) {
			for (ZipEntry entry = zis.getNextEntry(); entry != null; entry = zis.getNextEntry()) {
				// If this entry is an HTML file...
				String name = entry.getName();
				if (!name.endsWith(".html")) {
					continue;
				}

				// ... and it starts with one of our documentation prefixes
				for (String prefix : prefixes) {
					if (name.startsWith(prefix)) {
						// Add the file to the list of files to index for search purposes
						paths.add("/" + RedhawkHelpPlugin.PLUGIN_ID + "/" + name);
						break;
					}
				}
			}
		} catch (IOException e) {
			Bundle bundle = Platform.getBundle(RedhawkHelpPlugin.PLUGIN_ID);
			Platform.getLog(bundle).log(new Status(IStatus.ERROR, RedhawkHelpPlugin.PLUGIN_ID, "Unable to get help files for search indexing", e));
			return;
		}
	}

}
