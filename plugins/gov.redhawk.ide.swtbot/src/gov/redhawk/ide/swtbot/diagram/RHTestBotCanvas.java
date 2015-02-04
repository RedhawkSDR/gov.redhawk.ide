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

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefFigureCanvas;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.VoidResult;

/**
 * This class exists to help make dragging connections in the Graphiti diagram 
 * work properly.
 */
public class RHTestBotCanvas extends SWTBotGefFigureCanvas {

	/**
	 * @param canvas
	 * @throws WidgetNotFoundException
	 */
	public RHTestBotCanvas(FigureCanvas canvas) throws WidgetNotFoundException {
		super(canvas);
	}

	/**
	 * @param canvas
	 * @param lightweightSystem
	 * @throws WidgetNotFoundException
	 */
	public RHTestBotCanvas(Canvas canvas, LightweightSystem lightweightSystem) throws WidgetNotFoundException {
		super(canvas, lightweightSystem);
	}

	/**
	 * As defined in SWTBotGefFigureCanvas, but adds another move event, to the 
	 * midpoint between the two ends, after pressing the button and before moving 
	 * to the end.
	 */
	@Override
	public void mouseDrag(final int fromXPosition, final int fromYPosition, final int toXPosition, final int toYPosition) {
		final int middleXPosition = (fromXPosition + toXPosition) / 2;
		final int middleYPosition = (fromYPosition + toYPosition) / 2;
        UIThreadRunnable.asyncExec(new VoidResult() {
            public void run() {
            	org.eclipse.swt.events.MouseEvent meMove = new MouseEvent(createMouseEvent(fromXPosition, 
            				fromYPosition, 0, 0, 0));
            	eventDispatcher.dispatchMouseMoved(meMove);
            	org.eclipse.swt.events.MouseEvent meDown = new MouseEvent(createMouseEvent(fromXPosition, 
            				fromYPosition, 1, SWT.BUTTON1, 1));
            	eventDispatcher.dispatchMousePressed(meDown);
            	org.eclipse.swt.events.MouseEvent meMoveMiddle = new MouseEvent(createMouseEvent(middleXPosition, 
            				middleYPosition, 1, SWT.BUTTON1, 0));
            	eventDispatcher.dispatchMouseMoved(meMoveMiddle);
            	org.eclipse.swt.events.MouseEvent meMoveTarget = new MouseEvent(createMouseEvent(toXPosition, 
            				toYPosition, 1, SWT.BUTTON1, 0));
            	eventDispatcher.dispatchMouseMoved(meMoveTarget);
            	org.eclipse.swt.events.MouseEvent meUp = new MouseEvent(createMouseEvent(toXPosition, 
            				toYPosition, 1 , SWT.BUTTON1, 1));
            	eventDispatcher.dispatchMouseReleased(meUp);
            }
        });
	}
}
