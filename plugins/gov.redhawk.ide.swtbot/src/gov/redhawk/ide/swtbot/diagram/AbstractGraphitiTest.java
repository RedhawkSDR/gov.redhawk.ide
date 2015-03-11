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
package gov.redhawk.ide.swtbot.diagram;

import gov.redhawk.ide.swtbot.UITest;

import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.junit.Before;

/**
 * 
 */
public abstract class AbstractGraphitiTest extends UITest {

	protected SWTGefBot gefBot; // SUPPRESS CHECKSTYLE VisibilityModifier

	@Before
	public void beforeTest() throws Exception {
		gefBot = new RHTestBot();
	}

	protected boolean toolIsPresent(SWTBotGefEditor editor, final String label) {
		if (label.contains(".")) {
			String[] segments = label.split("\\.");
			int numFolders = segments.length - 1;
			try {
				editor.activateTool(segments[numFolders]);
			} catch (WidgetNotFoundException e) {
				return false;
			}
			PaletteEntry entry = editor.getActiveTool();
			for (int index = numFolders - 1; index >= 0; --index) {
				if (segments[index].equals(entry.getParent().getLabel())) {
					entry = entry.getParent();
				} else {
					return false;
				}
			}
			return true;
		}
		return simpleToolIsPresent(editor, label);
	}
	
	protected boolean simpleToolIsPresent(SWTBotGefEditor editor, final String label) {
		String[] impls = new String[] {"",  " (cpp)", " (java)", " (python)"};
		for (String impl: impls) {
			try {
				editor.activateTool(label + impl);
			} catch (WidgetNotFoundException e) {
				continue;
			}
			return true;
		}
		return false;
	}
	
}
