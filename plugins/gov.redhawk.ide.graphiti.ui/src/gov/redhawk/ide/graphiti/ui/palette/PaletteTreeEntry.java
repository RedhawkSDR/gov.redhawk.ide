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

import org.eclipse.core.runtime.Assert;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.palette.IToolEntry;
import org.eclipse.graphiti.palette.impl.PaletteCompartmentEntry;

/**
 * 
 */
public class PaletteTreeEntry extends PaletteCompartmentEntry implements IToolEntry {

	private IPaletteCompartmentEntry parent;
	
	public PaletteTreeEntry(String label, PaletteCompartmentEntry container) {
		super(label, null);
		setInitiallyOpen(false);
		if (container != null) {
			container.addToolEntry(this);
			parent = container;
		}
	}
	
	public PaletteTreeEntry(String label) {
		this(label, null);
	}
	
	public String getDescription() {
		if (parent != null && parent instanceof PaletteTreeEntry) {
			return ((PaletteTreeEntry) parent).getDescription() + "." + getLabel();
		}
		return "namespace " + getLabel();
	}
	
	@Override
	public void addToolEntry(IToolEntry toolEntry) {
		if (toolEntry instanceof PaletteTreeEntry) {
			String label = toolEntry.getLabel();
			Assert.isNotNull(label, "Namespace segment cannot be null");
			for (IToolEntry entry: this.getToolEntries()) {
				if (entry instanceof PaletteTreeEntry && label.equals(entry.getLabel())) {
					// Subfolder is already there, don't add it again
					return;
				}
			}
			((PaletteTreeEntry) toolEntry).parent = this;
		}
		super.addToolEntry(toolEntry);
	}
	
	public PaletteTreeEntry getSubfolder(String label) {
		if (label == null) {
			return null;
		}
		for (IToolEntry entry: getToolEntries()) {
			if (entry instanceof PaletteTreeEntry && label.equals(entry.getLabel())) {
				return (PaletteTreeEntry) entry;
			}
		}
		return new PaletteTreeEntry(label, this);
	}
}
