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

import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteStack;
import org.eclipse.gef.palette.ToolEntry;

/**
 * IDE-1112: Class created to support a tree structure in the palette
 */
public class PaletteNamespaceFolder extends PaletteDrawer {

	private static final String TYPE_STRING = "PALETTE_NAMESPACE_NODE";
	/**
	 * @param name
	 * @param desc
	 * @param icon
	 */
	public PaletteNamespaceFolder(String name) {
		super(name);
		setType(TYPE_STRING);
	}

	@Override
	public boolean acceptsType(Object type) {
		if (ToolEntry.PALETTE_TYPE_TOOL.equals(type) 
				|| PaletteStack.PALETTE_TYPE_STACK.equals(type)
				|| super.getType().equals(type)
				|| TYPE_STRING.equals(type)) {
			return true;
		}
		return super.acceptsType(type);
	}

}
