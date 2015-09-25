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
package gov.redhawk.ide.graphiti.ui.palette;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteToolbar;
import org.eclipse.gef.ui.palette.PaletteViewer;

/**
 * 
 */
public class RHGraphitiPaletteFilter {

	private String filter = "";
	private PaletteViewer viewer;
	private Pattern pattern;

	public RHGraphitiPaletteFilter(PaletteViewer viewer, GraphicalEditPart logicLabelEditPart) {
		this.viewer = viewer;
	}

	/**
	 * 
	 */
	private void refresh() {
		PaletteRoot root = viewer.getPaletteRoot();
		for (Object obj : root.getChildren()) {
			if (obj instanceof PaletteToolbar) {
				continue;
			}
			if (obj instanceof PaletteEntry) {
				updateVisible((PaletteEntry) obj);
			}
		}
	}

	/**
	 * @param obj
	 * @return
	 */
	private void updateVisible(PaletteEntry obj) {
		if (obj instanceof CombinedTemplateCreationEntry) {
			boolean visible = matches(obj);
			obj.setVisible(visible);
		} else if (obj instanceof PaletteContainer) {
			PaletteContainer container = (PaletteContainer) obj;
			for (Object child : container.getChildren()) {
				if (child instanceof PaletteEntry) {
					PaletteEntry entry = (PaletteEntry) child;
					updateVisible(entry);
				}
			}
		}
	}

	/**
	 * @param obj
	 * @return
	 */
	private boolean matches(PaletteEntry obj) {
		if (pattern == null) {
			return true;
		}
		Matcher matcher = pattern.matcher(obj.getLabel());
		return matcher.matches();
	}

	public boolean matches(String label) {
		if (pattern == null) {
			return true;
		}
		Matcher matcher = pattern.matcher(label);
		return matcher.matches();
	}
	
	/**
	 * @return the filter
	 */
	public String getFilter() {
		return filter;
	}
	
	public void setFilter(String filter) {
		if (this.filter.equals(filter)) {
			return;
		}
		this.filter = filter;
		if (filter == null || filter.isEmpty()) {
			this.pattern = null;
		} else {
			this.pattern = Pattern.compile(".*" + Pattern.quote(this.filter) + ".*", Pattern.CASE_INSENSITIVE);
		}
		refresh();
	}

}
