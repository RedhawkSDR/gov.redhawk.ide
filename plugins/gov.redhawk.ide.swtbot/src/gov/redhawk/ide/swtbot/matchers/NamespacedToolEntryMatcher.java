/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.swtbot.matchers;

import java.util.regex.Pattern;

import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteStack;
import org.eclipse.gef.palette.ToolEntry;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import gov.redhawk.ide.graphiti.ui.palette.PaletteNamespaceFolder;

public class NamespacedToolEntryMatcher extends BaseMatcher<ToolEntry> {

	private String[] partLabels;

	private Pattern pattern;

	public NamespacedToolEntryMatcher(String[] labels) {
		partLabels = labels;

		String lastSegment = Pattern.quote(labels[labels.length - 1]);
		pattern = Pattern.compile(lastSegment + "(?: \\(.*\\))");
	}

	@Override
	public boolean matches(Object item) {
		// Must be a ToolEntry
		if (!(item instanceof ToolEntry)) {
			return false;
		}
		ToolEntry toolEntry = (ToolEntry) item;

		// See if it's the ToolEntry's label is what we're looking for
		if (!pattern.matcher(toolEntry.getLabel()).matches()) {
			return false;
		}

		// Verify the parents (i.e. namespace) are correct.
		PaletteContainer parent = toolEntry.getParent();
		if (parent instanceof PaletteStack) {
			// Skip over the PaletteStack (contains the multiple instances of a single component)
			parent = parent.getParent();
		}
		for (int i = partLabels.length - 2; i >= 0; i--) {
			if (!parent.getLabel().equals(partLabels[i])) {
				return false;
			}
			parent = parent.getParent();
		}

		// The parent should not be a namepsace folder
		if (parent instanceof PaletteNamespaceFolder) {
			return false;
		}

		return true;
	}

	@Override
	public void describeTo(Description description) {
	}
}
