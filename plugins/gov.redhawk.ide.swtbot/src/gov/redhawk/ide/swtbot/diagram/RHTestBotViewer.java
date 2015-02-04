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

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefViewer;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.VoidResult;

/**
 * Viewer class to use RHTestBotCanvas for better dragging functionality
 */
public class RHTestBotViewer extends SWTBotGefViewer {

	/**
	 * @param graphicalViewer
	 * @throws WidgetNotFoundException
	 */
	public RHTestBotViewer(GraphicalViewer graphicalViewer) throws WidgetNotFoundException {
		super(graphicalViewer);
	}

	@Override
	protected void init() throws WidgetNotFoundException {
		UIThreadRunnable.syncExec(new VoidResult() {
			public void run() {
				final Control control = graphicalViewer.getControl();
				if (control instanceof FigureCanvas) {
					canvas = new RHTestBotCanvas((FigureCanvas) control);
				}  else if (control instanceof Canvas) {
					if (control instanceof IAdaptable) {
						IAdaptable adaptable = (IAdaptable) control;
						Object adapter = adaptable.getAdapter(LightweightSystem.class);
						if (adapter instanceof LightweightSystem) {
							canvas = new RHTestBotCanvas((Canvas) control, (LightweightSystem) adapter);
						}
					}
                }	
				editDomain = graphicalViewer.getEditDomain();
			}
		});

		if (graphicalViewer == null) {
			throw new WidgetNotFoundException("Editor does not adapt to a GraphicalViewer");
		}
	}
}
