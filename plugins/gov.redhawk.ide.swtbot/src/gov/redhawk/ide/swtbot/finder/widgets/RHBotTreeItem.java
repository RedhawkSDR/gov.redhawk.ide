/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package gov.redhawk.ide.swtbot.finder.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.results.Result;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;

import gov.redhawk.sca.ui.ScaLabelProvider;

public class RHBotTreeItem extends SWTBotTreeItem {

	public RHBotTreeItem(SWTBotTreeItem botTreeItem) throws WidgetNotFoundException {
		super(botTreeItem.widget);
	}

	/**
	 * Allows getting the tooltip from a TreeViewer
	 * @see org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBot#getToolTipText()
	 */
	@Override
	public String getToolTipText() {
		SWTWorkbenchBot bot = new SWTWorkbenchBot();
		final SWTBotShell[] startingShells = bot.shells();

		// Move the mouse over the tree item
		final Point point = syncExec(new Result<Point>() {
			public Point run() {
				Rectangle bounds = widget.getBounds();
				return widget.getParent().toDisplay(bounds.x, bounds.y);
			}
		});
		asyncExec(new VoidResult() {
			public void run() {
				Event event = createMouseEvent(point.x + 1, point.y + 1, 0, SWT.NONE, 0);
				event.type = SWT.MouseMove;
				display.post(event);
			}
		});
		asyncExec(new VoidResult() {
			public void run() {
				Event event = createMouseEvent(point.x + 2, point.y + 2, 0, SWT.NONE, 0);
				event.type = SWT.MouseMove;
				display.post(event);
			}
		});

		// Wait for the context help shell to be created
		int delay = new ScaLabelProvider().getToolTipDisplayDelayTime(null);
		bot.waitUntil(new DefaultCondition() {
			@Override
			public boolean test() throws Exception {
				return bot.shells().length > startingShells.length;
			}

			@Override
			public String getFailureMessage() {
				return "No popup appeared";
			}
		}, SWTBotPreferences.TIMEOUT + delay);
		SWTBotShell tooltipShell = null;
		for (SWTBotShell shell : bot.shells()) {
			boolean existing = false;
			for (SWTBotShell startingShell : startingShells) {
				if (startingShell.widget == shell.widget) {
					existing = true;
					break;
				}
			}
			if (!existing) {
				tooltipShell = shell;
				break;
			}
		}
		Assert.assertNotNull(tooltipShell);

		// Return the tooltip text
		return tooltipShell.bot().clabel().getText();
	}

}
