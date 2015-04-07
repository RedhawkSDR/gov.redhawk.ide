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

import gov.redhawk.ide.graphiti.ui.palette.PaletteNamespaceFolder;
import gov.redhawk.ide.graphiti.ui.palette.PaletteNamespaceFolderEditPart;

import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.internal.ui.palette.editparts.DrawerEditPart;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteStack;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.ui.progress.UIJob;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Assert;

@SuppressWarnings("restriction")
public class PaletteUtils {

	private PaletteUtils() {
	}

	private static class FolderMatcher extends BaseMatcher<PaletteNamespaceFolderEditPart> {

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

	private static void ensureExpanded(SWTBot bot, SWTBotGefEditPart gefPart) {
		final EditPart part = gefPart.part();
		if (part instanceof DrawerEditPart && !((DrawerEditPart) part).isExpanded() || part instanceof PaletteNamespaceFolderEditPart
			&& !((PaletteNamespaceFolderEditPart) part).isExpanded()) {
			if (part instanceof PaletteNamespaceFolderEditPart) {
				final PaletteNamespaceFolderEditPart partFolder = ((PaletteNamespaceFolderEditPart) part);
				UIJob uiJob = new UIJob("Expanding palette node") {

					@Override
					public IStatus runInUIThread(IProgressMonitor monitor) {
						partFolder.setExpanded(true);
						return Status.OK_STATUS;
					}
				};

				uiJob.schedule();
				bot.sleep(500);
			}

		}
	}

	public static ToolEntry getToolEntry(RHTestBotEditor editor, final String label) {
		if (label.contains(".")) {
			String[] segments = label.split("\\.");
			int numFolders = segments.length - 1;
			SWTBotGefEditPart currentParent = null;
			RHTestBotViewer viewer = editor.getDragViewer();
			for (int index = 0; index < numFolders; ++index) {
				List<SWTBotGefEditPart> gefParts = viewer.paletteEditParts(new FolderMatcher(segments[index]));
				SWTBotGefEditPart foundParent = null;
				for (SWTBotGefEditPart gefPart : gefParts) {
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
							ensureExpanded(editor.bot(), gefPart.parent());
							ensureExpanded(editor.bot(), gefPart);
							break;
						}
						if (gefPart.parent().equals(currentParent)) {
							foundParent = gefPart;
							ensureExpanded(editor.bot(), gefPart);
							break;
						}
					}
				}
				if (foundParent == null) {
					return null;
				}
				currentParent = foundParent;
			}

			// Resolved whole namespace, time to find tool itself
//			List<SWTBotGefEditPart> gefParts = viewer.paletteEditParts(new FolderMatcher(segments[numFolders - 1]));
			List<SWTBotGefEditPart> gefParts = currentParent.children();
			for (SWTBotGefEditPart gefPart : gefParts) {
				EditPart part = gefPart.part();
				if (part == null) {
					continue;
				}
				Object model = part.getModel();
				if (model instanceof ToolEntry) {
					return (ToolEntry) model;
				}
			}
			return null;
		}
		return getSimpleTool(editor, label);
	}

	public static boolean toolIsPresent(SWTBotGefEditor editor, final String label) {
		if (editor instanceof RHTestBotEditor) {
			RHTestBotEditor realEditor = (RHTestBotEditor) editor;
			return (getToolEntry(realEditor, label) != null);
		}
		return (getSimpleTool(editor, label) != null);
	}

	protected static ToolEntry getSimpleTool(SWTBotGefEditor editor, final String label) {
		String[] impls = new String[] { "", " (cpp)", " (java)", " (python)" };
		for (String impl : impls) {
			try {
				editor.activateTool(label + impl);
			} catch (WidgetNotFoundException e) {
				continue;
			}
			return editor.getActiveTool();
		}
		return null;
	}
	
	public static boolean hasMultipleImplementations(SWTBotGefEditor editor, String component) {
		Assert.assertTrue(PaletteUtils.toolIsPresent(editor, component));
		ToolEntry entry;
		if (editor instanceof RHTestBotEditor) {
			entry = PaletteUtils.getToolEntry((RHTestBotEditor) editor, component);
		} else {
			entry = editor.getActiveTool();
		}
		Assert.assertNotNull(entry);
		PaletteContainer container = entry.getParent();
		Assert.assertTrue(container instanceof PaletteStack);
		Assert.assertTrue(container.getChildren().size() > 1);
		Pattern match = Pattern.compile(component + " \\(\\w+\\)");
		for (Object obj: container.getChildren()) {
			if (obj instanceof ToolEntry) {
				Assert.assertTrue(match.matcher(((ToolEntry) obj).getLabel()).matches());
			} else {
				return false;
			}
		}
		return true;
	}

}
