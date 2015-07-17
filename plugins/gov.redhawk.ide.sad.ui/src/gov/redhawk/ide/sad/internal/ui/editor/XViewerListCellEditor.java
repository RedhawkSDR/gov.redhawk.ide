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
package gov.redhawk.ide.sad.internal.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;

public class XViewerListCellEditor extends XViewerCellEditor {

	private Shell popup = null;
	private List list = null;
	private String[] items;
	private SelectionListener selectionListener;

	public XViewerListCellEditor(Composite parent, String[] items) {
		super(parent);
		this.items = items;

		createPopup(parent);
	}

	@Override
	public void dispose() {
		popup.dispose();
		super.dispose();
	}

	@Override
	public boolean setFocus() {
		// Determine the bounds of the list--since it is going to pop over the cell, we can use whatever size we want.
		// Use the width of the cell, but for height try to determine how large it should be to show a reasonable
		// number of items (logic borrowed from SWT's CCombo).
		int itemCount = Math.min(list.getItemCount(), 5);
		Point listSize = list.computeSize(SWT.DEFAULT, itemCount * list.getItemHeight(), false);
		int width = getSize().x;
		int height = listSize.y;
		// Also borrowed from CCombo: if the horizontal scroll bar isn't shown, offset the size so as not to waste
		// vertical real estate
		ScrollBar hBar = list.getHorizontalBar();
		if (!hBar.isVisible()) {
			height -= hBar.getSize().y;
		}

		// Pop up the Shell directly on top of the cell, sized to exactly match our computed list size
		Display display = getDisplay();
		Point position = display.map(getParent(), null, getLocation());
		popup.setBounds(position.x, position.y, width, height);
		list.setBounds(0, 0, width, height);
		popup.setVisible(true);
		return list.setFocus();
	}

	@Override
	protected void doSetValue(Object value) {
		list.removeSelectionListener(selectionListener);
		if (value != null) {
			list.setSelection(new String[] { (String) value });
		} else {
			list.setSelection(0);
		}
		list.addSelectionListener(selectionListener);
		setValueValid(true);
	}

	@Override
	protected Object doGetValue() {
		int index = list.getSelectionIndex();
		if (index >= 0) {
			return items[index];
		} else {
			return null;
		}
	}

	protected void createPopup(Control parent) {
		popup = new Shell(getShell(), SWT.NO_TRIM | SWT.ON_TOP);
		list = new List(popup, SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
		list.setBackground(parent.getBackground());
		list.setForeground(parent.getForeground());
		list.setFont(parent.getFont());
		list.setItems(items);

		selectionListener = new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				acceptEditor();
			}
		};
		list.addSelectionListener(selectionListener);

		list.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {
				acceptEditor();
			}

		});

		list.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				cancelEditor();
			}

			@Override
			public void focusGained(FocusEvent e) {
			}
		});

		list.addTraverseListener(new TraverseListener() {

			@Override
			public void keyTraversed(TraverseEvent e) {
				if (e.character == SWT.ESC) {
					cancelEditor();
				}
			}
		});
	}

	protected void cancelEditor() {
		setValueValid(false);
		focusLost();
	}

	protected void acceptEditor() {
		setValueValid(true);
		focusLost();
	}
}
