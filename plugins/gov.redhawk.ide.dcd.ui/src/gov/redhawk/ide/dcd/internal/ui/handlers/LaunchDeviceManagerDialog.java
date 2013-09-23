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
package gov.redhawk.ide.dcd.internal.ui.handlers;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;

public class LaunchDeviceManagerDialog extends ListDialog {
	private static final int DEFAULT_DEBUG_LEVEL = 3;
	
	private final String[] debugLevels = new String[] { "Fatal", "Error", "Warn", "Info", "Debug", "Trace" };
	private Label label;
	private ComboViewer debugViewer;
	private int debugLevel;
	public LaunchDeviceManagerDialog(Shell parent) {
		super(parent);
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		
		this.label = new Label(parent, SWT.NULL);
		this.label.setText("Debug Level: ");
		this.debugViewer = new ComboViewer(parent, SWT.READ_ONLY | SWT.SINGLE | SWT.DROP_DOWN | SWT.BORDER);
		this.debugViewer.setContentProvider(new ArrayContentProvider());
		this.debugViewer.setInput(this.debugLevels);
		this.debugLevel = DEFAULT_DEBUG_LEVEL;
		this.debugViewer.setSelection(new StructuredSelection(debugLevels[DEFAULT_DEBUG_LEVEL]));
		
		this.debugViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				LaunchDeviceManagerDialog.this.debugLevel = LaunchDeviceManagerDialog.this.debugViewer.getCombo().getSelectionIndex();
			}
		});
		
		super.createButtonsForButtonBar(parent);
	}
	
	/**
	 * Provides the users selection of the debug message level.  The user is given the options
	 * "Fatal", "Error", "Warn", "Info", "Debug", "Trace" which correspond to a returned value here of
	 * 0, 1, 2, 3, 4, 5
	 * @return The selected debug level.
	 */
	public int getDebugLevel() {
		return this.debugLevel;
	}
}
