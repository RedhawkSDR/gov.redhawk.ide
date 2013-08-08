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
package gov.redhawk.datalist.ui.views;

import gov.redhawk.datalist.ui.DataListPlugin;
import gov.redhawk.datalist.ui.internal.DataCollectionSettings;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public abstract class OptionsComposite {

	private final DataCollectionSettings settings = new DataCollectionSettings(); 

	private Button button;
	private boolean isRunning = false;
	private Text samplesTxt;
	public static final String REAL = "Real", IMAGINARY = "Imaginary", COMPLEX = "Complex"; 
	public static enum CaptureMethod {
		NUMBER("Number of Samples"),
		INDEFINITELY("Indefinitely"),
		CLOCK_TIME("Clock Time"),
		SAMPLE_TIME("Sample Time");

		/** the description of the enum, outputted by toString()*/
		private String description; // DO NOT SET to null!


		CaptureMethod(String description) { 
			this.description = description;
		}

		@Override
		public String toString() {
			return description;
		}

		public static String[] toStringArray() {
			CaptureMethod[] methods = values();
			String[] captureMethods = new String[methods.length];
			for (int i = 0; i < methods.length; i++) {
				captureMethods[i] = methods[i].toString();
			}
			return captureMethods;
		}

		/**
		 * 
		 * @param arg0 the name or the description of the enum to be returned
		 * @return a CaptureMethod corresponding to the name or description provided
		 */
		public static CaptureMethod stringToValue(String s) {
			if (s.equals(NUMBER.name()) || s.equals(NUMBER.toString())) {
				return NUMBER;
			} else if (s.equals(INDEFINITELY.name()) || s.equals(INDEFINITELY.toString())) {
				return INDEFINITELY;
			} else if (s.equals(CLOCK_TIME.name()) || s.equals(CLOCK_TIME.toString())) {
				return CLOCK_TIME;
			} else if (s.equals(SAMPLE_TIME.name()) || s.equals(SAMPLE_TIME.toString())) {
				return SAMPLE_TIME;
			} else {
				return CaptureMethod.valueOf(s);
			}
		}
	}

	public OptionsComposite(Composite main) {
		this.settings.setProcessingTypes(CaptureMethod.toStringArray());
		createControl(main);
	}

	public void createControl(Composite main) {
		final Composite parent = new Composite(main, SWT.None);
		parent.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		parent.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).create()); 

		final Combo captureCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.SIMPLE);
		// TODO captureCombo.setItems(settings.getProcessingTypes());
		String[] a = {CaptureMethod.NUMBER.toString(), CaptureMethod.INDEFINITELY.toString()};
		captureCombo.setItems(a);
		captureCombo.select(0);

		samplesTxt = new Text(parent, SWT.BORDER);
		samplesTxt.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		final ControlDecoration dec = new ControlDecoration(samplesTxt, SWT.TOP | SWT.LEFT);
		dec.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_DEC_FIELD_ERROR));
		dec.setShowOnlyOnFocus(true);
		dec.hide(); 

		final Label unitsLabel = new Label(parent, SWT.None);
		unitsLabel.setText("");
		GridData unitsLayout = new GridData();
		unitsLayout.widthHint = 20;
		unitsLabel.setLayoutData(unitsLayout);

		captureCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				settings.setProcessType(captureCombo.getText());
				CaptureMethod method = 
						CaptureMethod.stringToValue(captureCombo.getText());
				if (method == CaptureMethod.INDEFINITELY) {
					samplesTxt.setText("1");
					unitsLabel.setText("");
					samplesTxt.setEnabled(false);
					isRunning = false;
					return;
				} else if (method == CaptureMethod.CLOCK_TIME 
						|| method == CaptureMethod.SAMPLE_TIME) {
					unitsLabel.setText("(s)");
					samplesTxt.setEnabled(true);
				} else {
					unitsLabel.setText("");
					samplesTxt.setEnabled(true);
				}
			}
		});

		Label label = new Label(parent, SWT.None);
		label.setText("Number of Dimensions:");

		final Combo columnsCombo = new Combo(parent, SWT.BORDER | SWT.SINGLE | SWT.DROP_DOWN);
		columnsCombo.setText(REAL);
		columnsCombo.add(REAL, 0);
		columnsCombo.add(COMPLEX, 1);  
		columnsCombo.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		final ControlDecoration colDec = new ControlDecoration(columnsCombo, SWT.TOP | SWT.LEFT);
		colDec.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_DEC_FIELD_ERROR));
		colDec.setDescriptionText("Please enter a positive integer or choose one of the options below.");
		colDec.setShowOnlyOnFocus(true);
		colDec.hide();

		button = new Button(parent, SWT.None);
		button.setLayoutData(GridDataFactory.fillDefaults().create());
		button.setImage(getImage("icons/start.gif"));
		button.setToolTipText("Start Acquire");
		button.setData(isRunning);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (isIndefinite()) {
					buttonsEnable(isRunning);
					if (isRunning) {
						isRunning = !isRunning;
						stopAcquire();
					} else {
						setSettings(columnsCombo.getText(), 
								Double.valueOf(samplesTxt.getText()), 
								captureCombo.getText());
						startAcquire();
					}
				} else {
					isRunning = true;
					buttonsEnable(isRunning);
					setSettings(columnsCombo.getText(), 
							Double.valueOf(samplesTxt.getText()), 
							captureCombo.getText());
					startAcquire();
				}
			}
		});

		samplesTxt.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) { 

				String value = samplesTxt.getText(); 

				if (isDouble(value)) {
					CaptureMethod method = CaptureMethod.stringToValue(settings.getProcessType());
					if (method == CaptureMethod.INDEFINITELY) {
						dec.hide();
					} else if (Double.valueOf(value) <= 0) {
						showDecoration(dec, settings.getProcessType() + " must be greater than 0.");
					} else if (method == CaptureMethod.NUMBER) {
						double val = Double.valueOf(value);
						if (val > Integer.MAX_VALUE) {
							showDecoration(dec, settings.getProcessType() 
									+ " must be less than or equal to " + Integer.MAX_VALUE + ".");
						} else if ((val - (int) val) > 0) {
							showDecoration(dec, settings.getProcessType() + " must be a positive integer.");
						} else if ((val > 1000000)) {
							showDecoration(dec, "For this sample size, you may run out of heap space.");
							button.setEnabled(!colDec.isVisible());
							return;
						} else {
							dec.hide();
						}
					} else {
						dec.hide();
					}

				} else {
					showDecoration(dec, "The number of samples must be a positive integer.");
				} 
				button.setEnabled(!dec.isVisible() && !colDec.isVisible());
			}
		}); 

		columnsCombo.addModifyListener(new ModifyListener() { 
			@Override
			public void modifyText(ModifyEvent e) {
				String dims = columnsCombo.getText();

				if (isPositiveInteger(dims) || inArray(dims, columnsCombo.getItems())) {
					colDec.hide();
				} else {
					colDec.show();		
				}
				button.setEnabled(!(dec.isVisible() && colDec.isVisible()));
			}
		});

	}

	protected Image getImage(String filename) {
		return DataListPlugin.imageDescriptorFromPlugin(DataListPlugin.PLUGIN_ID, filename).createImage();
	}

	public abstract void startAcquire();

	public abstract void stopAcquire();

	public static void showDecoration(ControlDecoration dec, String errorText) {
		dec.setDescriptionText(errorText);
		dec.show();
	}

	public boolean isDouble(String str) {
		try {
			if (Double.valueOf(str) != null) {
				return true;
			}
		} catch (NumberFormatException e) {
			return false;
		}
		return false; 
	}

	private static boolean isPositiveInteger(String s) {
		try { 
			if (Integer.parseInt(s) < 1) {
				return false;
			}
		} catch (NumberFormatException e) { 
			return false; 
		}
		return true;
	}

	private boolean inArray(String item, String[] array) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(item)) {
				return true;
			}
		}
		return false;
	}

	public void buttonsEnable(boolean running) {
		isRunning = running;
		if (isIndefinite()) {
			if (running) {
				button.setImage(getImage("icons/stop.gif"));
				button.setToolTipText("Stop Acquire");
			} else {
				button.setImage(getImage("icons/start.gif"));
				button.setToolTipText("Start Acquire");
			}
			button.setData(!running);
			button.setEnabled(true);
		} else {
			button.setEnabled(!running);
		}
	}

	private void setSettings(String dims, Double samples, String method) {
		if (dims.equals(REAL)) {
			settings.setDimensions(1);
		} else if (dims.equals(COMPLEX)) {
			settings.setDimensions(2);
		} else {
			settings.setDimensions(Integer.parseInt(dims));
		}
		// samples = # samples OR length of time (in seconds)
		settings.setSamples(samples); 
		settings.setProcessType(method);

	}

	public void showSamples(int count) {
		switch(CaptureMethod.stringToValue(settings.getProcessType())) {
		case NUMBER: 
			return;
		default:
			samplesTxt.setText(String.valueOf(count));
		}
	}

	public boolean isIndefinite() {
		return CaptureMethod.stringToValue(
				settings.getProcessType()).equals(CaptureMethod.INDEFINITELY);
	}

	public DataCollectionSettings getSettings() {
		return settings;
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */ 
	public void setFocus() {
		if (this.samplesTxt != null && !this.samplesTxt.isDisposed()) {
			this.samplesTxt.setFocus();
		}
	}

}
