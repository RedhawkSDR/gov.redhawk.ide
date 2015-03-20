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

import gov.redhawk.ide.graphiti.ui.palette.PaletteNamespaceFolder;
import gov.redhawk.ide.graphiti.ui.palette.PaletteNamespaceFolderEditPart;
import gov.redhawk.ide.swtbot.UITest;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.internal.ui.palette.editparts.DrawerEditPart;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.ui.palette.editparts.PaletteEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.matchers.ToolEntryLabelMatcher;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;

/**
 * 
 */
@SuppressWarnings("restriction")
public abstract class AbstractGraphitiTest extends UITest {

	protected RHTestBot gefBot; // SUPPRESS CHECKSTYLE VisibilityModifier

	@Before
	public void beforeTest() throws Exception {
		gefBot = new RHTestBot();
	}

	private class FolderMatcher extends BaseMatcher<PaletteNamespaceFolderEditPart> {
		
		private String partLabel;
		
		FolderMatcher(String label) {
			partLabel = label;
		}
		
		@Override
		public boolean matches(Object item) {
			if (item instanceof PaletteNamespaceFolderEditPart) {
				PaletteNamespaceFolderEditPart part = (PaletteNamespaceFolderEditPart) item;
				return partLabel.equals(part.getFolder().getLabel());
			}
			return false;
		}

		@Override
		public void describeTo(Description description) {
		}
		
	}
	
	private class ToolMatcher extends BaseMatcher<PaletteEditPart> {

		private ToolEntryLabelMatcher innerMatcher;
		
		ToolMatcher(String label) {
			innerMatcher = new ToolEntryLabelMatcher(label);
		}
		
		@Override
		public boolean matches(Object item) {
			if (item instanceof PaletteEditPart) {
				PaletteEditPart part = (PaletteEditPart) item;
				return innerMatcher.matches(part.getModel());
			}
			return false;
		}

		@Override
		public void describeTo(Description description) {
		}
		
	}
	
	private void ensureExpanded(SWTBotGefEditPart gefPart) {
		EditPart part = gefPart.part();
		if (part instanceof DrawerEditPart && !((DrawerEditPart) part).isExpanded() 
				|| part instanceof PaletteNamespaceFolderEditPart && !((PaletteNamespaceFolderEditPart) part).isExpanded()) {
			gefPart.click();
		}
	}
	
	protected ToolEntry getToolEntry(RHTestBotEditor editor, final String label) {
		if (label.contains(".")) {
			String[] segments = label.split("\\.");
			int numFolders = segments.length - 1;
			SWTBotGefEditPart currentParent = null;
			RHTestBotViewer viewer = editor.getDragViewer();
			for (int index = 0; index < numFolders; ++index) {
				List<SWTBotGefEditPart> gefParts = viewer.paletteEditParts(new FolderMatcher(segments[index]));
				SWTBotGefEditPart foundParent = null;
				for (SWTBotGefEditPart gefPart: gefParts) {
					// Check parent node of each part in list to determine if it is the right path segment
					EditPart part = gefPart.part();
					if (part == null) {
						continue;
					}
					Object model = part.getModel();
					if (model instanceof PaletteEntry) {
						PaletteContainer parent = ((PaletteEntry) model).getParent();
						if (currentParent == null && parent instanceof PaletteDrawer && !(parent instanceof PaletteNamespaceFolder)) {
							// First namespace segment successfully found, move on to next one
							foundParent = gefPart;
							ensureExpanded(gefPart.parent());
							ensureExpanded(gefPart);
							break;
						}
						if (gefPart.parent().equals(currentParent)) {
							foundParent = gefPart;
							ensureExpanded(gefPart);
							break;
						}
					}
				}
				if (foundParent == null) {
					return null;
				}
				currentParent = foundParent;
			}
//			try {
//				editor.activateTool(segments[numFolders]);
//			} catch (WidgetNotFoundException e) {
//				return null;
//			}
//			PaletteEntry entry = editor.getActiveTool();
			// Resolved whole namespace, time to find tool itself
			List<SWTBotGefEditPart> gefParts = viewer.paletteEditParts(new ToolMatcher(segments[numFolders]));
			for (SWTBotGefEditPart gefPart: gefParts) {
				if (gefPart.parent().equals(currentParent)) {
					EditPart part = gefPart.part();
					if (part == null) {
						continue;
					}
					Object model = part.getModel();
					if (model instanceof ToolEntry) {
						return (ToolEntry) model;
					}
				}
			}
			return null;
		}
		return getSimpleTool(editor, label);
	}
	
	protected boolean toolIsPresent(SWTBotGefEditor editor, final String label) {
		if (editor instanceof RHTestBotEditor) {
			RHTestBotEditor realEditor = (RHTestBotEditor) editor;
			return (getToolEntry(realEditor, label) != null);
		}
		return (getSimpleTool(editor, label) != null);
	}
	
	protected ToolEntry getSimpleTool(SWTBotGefEditor editor, final String label) {
		String[] impls = new String[] {"",  " (cpp)", " (java)", " (python)"};
		for (String impl: impls) {
			try {
				editor.activateTool(label + impl);
			} catch (WidgetNotFoundException e) {
				continue;
			}
			return editor.getActiveTool();
		}
		return null;
	}
	
}
