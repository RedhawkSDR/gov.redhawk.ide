/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.datalist.ui.internal;

import org.eclipse.jface.viewers.IToolTipProvider;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * Provides tooltips for a SWT {@link Table}.
 */
public class TableToolTipSupport {

	private TableToolTipSupport() {
	}

	/**
	 * Enable tooltips for the table using the given provider
	 * @param table
	 * @param tooltipProvider
	 */
	public static void enableFor(Table table, IToolTipProvider tooltipProvider) {
		// Set to nothing initially
		table.setToolTipText("");

		// Adjust text based on hover
		table.addMouseTrackListener(new MouseTrackListener() {

			@Override
			public void mouseHover(MouseEvent event) {
				TableItem item = table.getItem(new Point(event.x, event.y));
				if (item == null) {
					table.setToolTipText("");
				} else {
					table.setToolTipText(tooltipProvider.getToolTipText(item));
				}
			}

			@Override
			public void mouseExit(MouseEvent event) {
			}

			@Override
			public void mouseEnter(MouseEvent event) {
			}
		});
	}

}
