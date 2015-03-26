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
package gov.redhawk.ide.graphiti.sad.ui.tests;

import gov.redhawk.ide.graphiti.ui.palette.RHGraphitiPaletteFilterFigure;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.PaletteUtils;

import java.util.regex.Pattern;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteStack;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefFigureCanvas;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 */
public class DiagramPaletteFilterTest extends AbstractGraphitiTest {

	private String waveformName;

	/**
	 * 
	 */

	private class FilterRunnable implements Runnable {

		private final SWTBotGefEditor editor;
		private String filterString;
		private Rectangle rect = null;
		private SWTBotGefFigureCanvas paletteCanvas = null;
		
		FilterRunnable(SWTBotGefEditor editor) {
			this.editor = editor;
		}
		
		@Override
		public void run() {
			if (filterString == null) {
				return;
			}
			typeInFilter(editor.getWidget(), filterString);
		}
		
		public void setFilterString(String filterText) {
			this.filterString = filterText;
			Display.getDefault().syncExec(this);
		}
		
		public Rectangle getRectangle() {
			return rect;
		}
		
		private void clickAndType(String filterText, int x, int y) {
			paletteCanvas.mouseMoveLeftClick(x, y);
			Control text = editor.bot().getFocusedWidget();
			if (text instanceof Text) {
				((Text) text).setText(filterText);
				return;
			}
			if (text instanceof FigureCanvas && x == 0 && y == 0) {
				// Still not sure why this happens
				clickAndType(filterText, rect.x, rect.y);
				return;
			}
			Assert.assertNotNull(text);
		}
		
		private Rectangle typeInFilter(Widget w, String filterText) {
			if (paletteCanvas != null && rect != null) {
				clickAndType(filterText, 0, 0);
				return rect;
			}
			if (w instanceof FigureCanvas) {
				FigureCanvas canvas = (FigureCanvas) w;
				Rectangle filterArea = typeInFilter(canvas.getContents(), canvas, filterText);
				if (filterArea != null) {
					return filterArea;
				} else {
					return null;
				}
			}
			if (w instanceof Composite) {
				Composite comp = (Composite) w;
				for (Control c: comp.getChildren()) {
					Rectangle rectangle = typeInFilter(c, filterText);
					if (rectangle != null) {
						return rectangle;
					}
				}
			}
			return null;
		}
		
		private Rectangle typeInFilter(IFigure f, FigureCanvas canvas, String filterText) {
			if (f instanceof RHGraphitiPaletteFilterFigure) {
				paletteCanvas = new SWTBotGefFigureCanvas(canvas);
				RHGraphitiPaletteFilterFigure figure = (RHGraphitiPaletteFilterFigure) f;
				rect = figure.getBounds();
				clickAndType(filterText, rect.x, rect.y);
				return figure.getBounds();
			}
			for (Object obj: f.getChildren()) {
				Rectangle rectangle = typeInFilter((IFigure) obj, canvas, filterText);
				if (rectangle != null) {
					return rectangle;
				}
			}
			return null;
		}
		
	}
	
	/**
	 * Test filtering of the component list
	 */
	@Test
	public void testFilter() {
		waveformName = "IDE-962-Test";
		final String component1 = "SigGen";
		final String component2 = "HardLimit";
		// IDE-1112: test presence of namespaced component in palette
		final String component3 = "ide1112.test.name.spaced.comp1";

		final String errorMissing1 = "Component " + component1 + " is missing from the palette";
		final String errorMissing2 = "Component " + component2 + " is missing from the palette";
		final String errorMissing3 = "Component " + component3 + " is missing from the palette";
		final String errorShown1 = "Component " + component1 + " should be filtered out of the palette";
		final String errorShown2 = "Component " + component2 + " should be filtered out of the palette";
		final String errorShown3 = "Component " + component3 + " should be filtered out of the palette";

		WaveformUtils.createNewWaveform(gefBot, waveformName);
		final SWTBotGefEditor editor = gefBot.gefEditor(waveformName);

		Assert.assertTrue(errorMissing1, PaletteUtils.toolIsPresent(editor, component1));
		Assert.assertTrue(errorMissing2, PaletteUtils.toolIsPresent(editor, component2));
		Assert.assertTrue(errorMissing3, PaletteUtils.toolIsPresent(editor, component3));

		FilterRunnable filterer = new FilterRunnable(editor);
		filterer.setFilterString("s");
		Rectangle filterRect = filterer.getRectangle();
		Assert.assertNotNull(filterRect);

		Assert.assertTrue(errorMissing1, PaletteUtils.toolIsPresent(editor, component1));
		Assert.assertFalse(errorShown2, PaletteUtils.toolIsPresent(editor, component2));
		Assert.assertTrue(errorMissing3, PaletteUtils.toolIsPresent(editor, component3));
		
		filterer.setFilterString("sh");

		Assert.assertFalse(errorShown1, PaletteUtils.toolIsPresent(editor, component1));
		Assert.assertFalse(errorShown2, PaletteUtils.toolIsPresent(editor, component2));
		Assert.assertFalse(errorShown3, PaletteUtils.toolIsPresent(editor, component3));

		filterer.setFilterString("h");

		Assert.assertFalse(errorShown1, PaletteUtils.toolIsPresent(editor, component1));
		Assert.assertTrue(errorMissing2, PaletteUtils.toolIsPresent(editor, component2));
		Assert.assertFalse(errorShown3, PaletteUtils.toolIsPresent(editor, component3));

		filterer.setFilterString(".");

		Assert.assertFalse(errorShown1, PaletteUtils.toolIsPresent(editor, component1));
		Assert.assertFalse(errorShown2, PaletteUtils.toolIsPresent(editor, component2));
		Assert.assertTrue(errorMissing3, PaletteUtils.toolIsPresent(editor, component3));

		filterer.setFilterString("");

		Assert.assertTrue(errorMissing1, PaletteUtils.toolIsPresent(editor, component1));
		Assert.assertTrue(errorMissing2, PaletteUtils.toolIsPresent(editor, component2));
		Assert.assertTrue(errorMissing3, PaletteUtils.toolIsPresent(editor, component3));
	}
	
	@Test
	public void checkImplementations() {
		waveformName = "IDE-953-Test";
		final String component1 = "SigGen";
		final String component2 = "HardLimit";
		WaveformUtils.createNewWaveform(gefBot, waveformName);
		final SWTBotGefEditor editor = gefBot.gefEditor(waveformName);
		
		Assert.assertTrue(hasMultipleImplementations(editor, component1));
		Assert.assertTrue(hasMultipleImplementations(editor, component2));
	}
	
	private boolean hasMultipleImplementations(SWTBotGefEditor editor, String component) {
		Assert.assertTrue(PaletteUtils.toolIsPresent(editor, component));
		ToolEntry entry = editor.getActiveTool();
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
