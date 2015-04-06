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
package gov.redhawk.ide.graphiti.ui.diagram.util;

import java.util.HashMap;
import java.util.Map;

import gov.redhawk.ide.graphiti.ui.palette.RHGraphitiPaletteFilterFigure;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefFigureCanvas;
import org.junit.Assert;
/**
 * 
 */
public class PaletteFilterUtil {

	/**
	 * 
	 */
	private static Map <SWTBotGefEditor, FilterRunnable> RUNNABLES;
	private static PaletteFilterUtil INSTANCE;
	
	static {
		INSTANCE = new PaletteFilterUtil();
		RUNNABLES = new HashMap<SWTBotGefEditor, FilterRunnable>();
	}
	
	public static void setFilter(SWTBotGefEditor editor, String filterString) {
		FilterRunnable runnable = RUNNABLES.get(editor);
		if (runnable == null) {
			runnable = INSTANCE.new FilterRunnable(editor);
			RUNNABLES.put(editor, runnable);
		}
		runnable.setFilterString(filterString);
	}
	
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
	
}

