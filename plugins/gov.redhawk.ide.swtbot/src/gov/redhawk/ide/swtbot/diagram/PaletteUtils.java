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
package gov.redhawk.ide.swtbot.diagram;

import java.util.List;

import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.junit.Assert;

import gov.redhawk.ide.swtbot.matchers.PaletteFilterMatcher;

public class PaletteUtils {

	private PaletteUtils() {
	}

	/**
	 * Determines if a tool is present (i.e. visible) in the palette.
	 * @param editor
	 * @param label The tool label (can be namespaced, e.g. 'a.b.foo')
	 * @return True if present
	 */
	public static boolean toolIsPresent(RHBotGefEditor editor, String label) {
		return getTool(editor, label) != null;
	}

	protected static ToolEntry getTool(RHBotGefEditor editor, final String label) {
		try {
			editor.activateNamespacedTool(label.split("\\."));
		} catch (WidgetNotFoundException e) {
			return null;
		}
		return editor.getActiveTool();
	}

	/**
	 * Determines if a tool is present in the palette and has multiple implementations.
	 * @param editor
	 * @param label The tool label (can be namespaced, e.g. 'a.b.foo')
	 * @return
	 */
	public static boolean hasMultipleImplementations(RHBotGefEditor editor, String label) {
		try {
			editor.activateNamespacedTool(label.split("\\."), 1);
			return true;
		} catch (IndexOutOfBoundsException e) {
			return false;
		}
	}

	public static void setFilter(RHBotGefEditor editor, String filterString) {
		List<SWTBotGefEditPart> editParts = editor.getDragViewer().editPartsPalette(new PaletteFilterMatcher());
		Assert.assertTrue("Couldn't find palette filter", editParts.size() == 1);
		editParts.get(0).activateDirectEdit();
		editor.bot().text().setText(filterString);
	}

}
