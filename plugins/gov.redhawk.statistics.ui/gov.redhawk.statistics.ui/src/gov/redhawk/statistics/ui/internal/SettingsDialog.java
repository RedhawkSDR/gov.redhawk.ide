/******************************************************************************
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package gov.redhawk.statistics.ui.internal;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class SettingsDialog extends TitleAreaDialog {

	private Text barText;
	private int numBars; 
	private int numDims, selIndex;

	public SettingsDialog(Shell parentShell, int dimensions, int currentIndex, int currentBars) {
		super(parentShell);
		numDims = dimensions; 
		selIndex = currentIndex;
		numBars = currentBars;
	}

	@Override
	public void create() {
		super.create();
		setDialogHelpAvailable(false);

		setTitle("Chart Options"); 

		String messageText = "Please set the number of bars "
				+ ((numDims > 1) ? "and select dimension(s) " : "") 
				+ "to be shown.";
		setMessage(messageText, IMessageProvider.NONE); 

		//resize dialog window to ideal size
		this.getShell().pack();
	}

	@Override
	protected Control createDialogArea(Composite parent) {  

		Composite dialogComp = new Composite(parent, SWT.None);
		dialogComp.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		dialogComp.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());

		Composite barComposite = new Composite(dialogComp, SWT.None);
		barComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		barComposite.setLayout(GridLayoutFactory.fillDefaults().margins(10, 10).numColumns(2).create());

		Label label = new Label(barComposite, SWT.NONE);
		label.setText("Number of bars:");

		barText = new Text(barComposite, SWT.BORDER);
		barText.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		barText.setText(String.valueOf(numBars)); 
		barText.setSelection(0, barText.getText().length());
		barText.addModifyListener(new ModifyListener() { 
			@Override
			public void modifyText(ModifyEvent e) {
				getButton(OK).setEnabled(isValidInput());
			}
		});

		if (numDims > 1) {
			Composite buttons = new Composite(dialogComp, SWT.None);
			buttons.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
			buttons.setLayout(GridLayoutFactory.fillDefaults().margins(10, 10).create());

			for (int i = -1; i < numDims; i++) { 
				final Button button = new Button(buttons, SWT.RADIO);
				button.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
				button.setSelection(i == selIndex);
				button.setData(i);
				button.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						selIndex = (Integer) button.getData();

					} 
				});

				if (i == -1) {
					button.setText("Complex");
				} else if (numDims == 2) {
					if (i == 0) {
						button.setText("Real");
					} else {
						button.setText("Imaginary");
					}
				} else {
					button.setText("Dimension " + i);
				}
			}
		}

		return parent;
	}

	private boolean isValidInput() {
		try { 
			if (Integer.parseInt(barText.getText()) < 1) { 
				setMessage("Please enter a positive integer.", IMessageProvider.ERROR);
				return false;
			}
		} catch (NumberFormatException e) { 
			setMessage("Please enter a positive integer.", IMessageProvider.ERROR);
			return false; 
		}
		setMessage("Please set the number of bars " 
				+ ((numDims > 1) ? "and select dimension(s) " : "") 
				+ "to be shown.", IMessageProvider.NONE); 
		return true;
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	// We need to have the textFields into Strings because the UI gets disposed
	// and the Text Fields are not accessible any more.
	private void saveInput() {
		numBars = Integer.parseInt(barText.getText());  
	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	} 

	public int getNumBars() {
		return numBars;
	} 

	public int getSelectedIndex() {
		return selIndex;
	}
} 