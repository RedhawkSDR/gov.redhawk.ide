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
package gov.redhawk.datareader.ui.controlPanels;

import gov.redhawk.datareader.ui.Activator;
import gov.redhawk.ide.debug.LocalScaComponent;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaSimpleProperty;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.sca.observables.SCAObservables;

import java.io.File;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import CF.ResourcePackage.StartError;
import CF.ResourcePackage.StopError;

/**
 *
 */
public class DataReaderComposite extends Composite {

	private ScaComponent input;
	private Text sampleRateText;
	private Text streamIdText;
	private EMFDataBindingContext context;
	private Text frontEndRfText;
	private Text inputFileText;
	private Button inputFileButton;
	private Text speedFactorText;
	private Button playButton;
	private Text yDeltaText;
	private Text subsizeText;
	private Combo complexCombo;
	private Combo loopCombo;
	private Button stopButton;
	private FileDialog inputFileDialog;
	private Button pauseButton;

	public DataReaderComposite(Composite parent, int style) {
		super(parent, style);
		createPropertyComposite(this);
	}

	@Override
	public void dispose() {
		super.dispose();
		context.dispose();
	}

	private void createPropertyComposite(Composite parent) {
		Composite propertyComposite = new Composite(parent, SWT.BORDER);
		parent.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).margins(25, 25).create());
		createControls(propertyComposite);
	}

	private void createControls(final Composite parent) {
		parent.setLayout(GridLayoutFactory.fillDefaults().numColumns(1).margins(10, 10).create());
		Composite textInput = new Composite(parent, SWT.None);
		
		Composite comboInput = new Composite(parent, SWT.None);

		GridData buttonGridData = new GridData();
		buttonGridData.horizontalAlignment = GridData.CENTER;
		Composite buttonInput = new Composite(parent, SWT.None);
		buttonInput.setLayoutData(buttonGridData);

		createText(textInput);
		createCombos(comboInput);
		createButtons(buttonInput);
	}

	public void createText(final Composite parent) {
		parent.setLayout(GridLayoutFactory.fillDefaults().numColumns(6).margins(0, 0).create());
		
		GridData textGridData = new GridData();
		textGridData.horizontalAlignment = GridData.FILL;
		textGridData.horizontalSpan = 2;
		textGridData.widthHint = 200;

		Label label = new Label(parent, SWT.None);
		label.setText("Sample Rate (Hz):");
		sampleRateText = new Text(parent, SWT.BORDER);
		sampleRateText.setLayoutData(textGridData);

		label = new Label(parent, SWT.None);
		label.setText("Stream ID:");
		streamIdText = new Text(parent, SWT.BORDER);
		streamIdText.setLayoutData(textGridData);

		label = new Label(parent, SWT.None);
		label.setText("Frontend RF (Hz):");
		frontEndRfText = new Text(parent, SWT.BORDER);
		frontEndRfText.setLayoutData(textGridData);

		label = new Label(parent, SWT.None);
		label.setText("Speed Factor:");
		speedFactorText = new Text(parent, SWT.BORDER);
		speedFactorText.setLayoutData(textGridData);

		label = new Label(parent, SWT.None);
		label.setText("Y Delta:");
		yDeltaText = new Text(parent, SWT.BORDER);
		yDeltaText.setLayoutData(textGridData);

		label = new Label(parent, SWT.None);
		label.setText("Subsize:");
		subsizeText = new Text(parent, SWT.BORDER);
		subsizeText.setLayoutData(textGridData);
		
		label = new Label(parent, SWT.None);
		label.setText("Input File:");
		inputFileText = new Text(parent, SWT.BORDER);
		inputFileText.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).span(4, 1).create());
		inputFileButton = new Button(parent, SWT.PUSH);
		inputFileButton.setText("Browse");
		inputFileButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				Shell shell = parent.getShell();

				if (input instanceof LocalScaComponent) {
					openLocalInputDialog(shell);
				}
				//TODO
				//else this is a Domain/Remote component
				//openScaInputDialog
			}

			private void openLocalInputDialog(Shell shell) {
				inputFileDialog = new FileDialog(shell, SWT.NULL);
				inputFileDialog.setText("Select Input File");
				String path = inputFileDialog.open();
				if (path != null) {
					File file = new File(path);
					if (file.isFile()) {
						displayFiles(new String[] { file.toString() });
					} else {
						displayFiles(file.list());
					}
				}
			}
		});
	}

	public void createCombos(Composite parent) {
		parent.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).margins(0, 0).spacing(64, 10).create());
		
		GridData comboGridData = new GridData();
		comboGridData.widthHint = 100;

		Label label = new Label(parent, SWT.None);
		label.setText("Complex");
		complexCombo = new Combo(parent, SWT.READ_ONLY);
		complexCombo.add("true");
		complexCombo.add("false");
		complexCombo.setToolTipText("Flag to indicate data is complex.  If true, data values assumed to be alternating real and complex float values.");
		complexCombo.setLayoutData(comboGridData);

		label = new Label(parent, SWT.None);
		label.setText("Loop");
		loopCombo = new Combo(parent, SWT.READ_ONLY);
		loopCombo.add("true");
		loopCombo.add("false");
//		loopCombo.setEnabled(false);
		loopCombo.setToolTipText("Do we continue to replay and loop over the input file when we are done or not");
		loopCombo.setLayoutData(comboGridData);
	}

	public void createButtons(Composite parent) {
//		GridLayout gridLayout = new GridLayout(3, true);
//		gridLayout.marginLeft = 85;
//		parent.setLayout(gridLayout);
		
		parent.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).margins(0, 0).create());
		
		GridData buttonGridData = new GridData();
		buttonGridData.horizontalSpan = 1;
		buttonGridData.horizontalAlignment = GridData.CENTER;
		buttonGridData.widthHint = 150;

		playButton = new Button(parent, SWT.PUSH);
		playButton.setText("Play");
		playButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ScaModelCommand.execute(input, new ScaModelCommand() {
					@Override
					public void execute() {
						((ScaSimpleProperty) input.getProperty("Play")).setValue(true);
					}
				});
				Job job = new Job("Start...") {
					@Override
					protected IStatus run(IProgressMonitor monitor) {
						try {
							input.start();
						} catch (StartError e) {
							return new Status(Status.ERROR, Activator.PLUGIN_ID, "Failed to start data reader.", e);
						}
						return Status.OK_STATUS;
					}
				};
				job.schedule();
			}
		});
		playButton.setLayoutData(buttonGridData);

		pauseButton = new Button(parent, SWT.PUSH);
		pauseButton.setText("Pause");
		pauseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ScaModelCommand.execute(input, new ScaModelCommand() {
					@Override
					public void execute() {
						((ScaSimpleProperty) input.getProperty("Play")).setValue(false);
					}
				});
			}
		});
		pauseButton.setLayoutData(buttonGridData);

		stopButton = new Button(parent, SWT.PUSH);
		stopButton.setText("Stop");
		stopButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ScaModelCommand.execute(input, new ScaModelCommand() {
					@Override
					public void execute() {
						((ScaSimpleProperty) input.getProperty("Play")).setValue(false);
					}
				});
				Job job = new Job("Stop...") {
					@Override
					protected IStatus run(IProgressMonitor monitor) {
						try {
							input.stop();
						} catch (StopError e) {
							return new Status(Status.ERROR, Activator.PLUGIN_ID, "Failed to stop data reader.", e);
						}
						return Status.OK_STATUS;
					}
				};
				job.schedule();
			}
		});
		stopButton.setLayoutData(buttonGridData);
	}

	public void setInput(ScaComponent input) {
		this.input = input;
		if (context != null) {
			context.dispose();
			context = null;
		}
		if (input == null) {
			return;
		}
		context = new EMFDataBindingContext();

		setTextBindings(input, "SampleRate", sampleRateText);
		setTextBindings(input, "StreamID", streamIdText);
		setTextBindings(input, "FrontendRF", frontEndRfText);
		setTextBindings(input, "InputFile", inputFileText);
		setTextBindings(input, "SpeedFactor", speedFactorText);
		setTextBindings(input, "ydelta", yDeltaText);
		setTextBindings(input, "subsize", subsizeText);

		setComboBindings(input, "complex", complexCombo);
		setComboBindings(input, "Loop", loopCombo);
	}

	public void setTextBindings(ScaComponent input, String id, Widget value) {
		IObservableValue ScaProperty = SCAObservables.observeSimpleProperty(input, id);
		IObservableValue CPanelProperty = WidgetProperties.text(SWT.Modify).observeDelayed(500, value);
		context.bindValue(CPanelProperty, ScaProperty);
	}

	public void setComboBindings(ScaComponent input, String id, Widget value) {
		IObservableValue ScaProperty = SCAObservables.observeSimpleProperty(input, id);
		IObservableValue CPanelProperty = WidgetProperties.selection().observeDelayed(500, value);
		context.bindValue(CPanelProperty, ScaProperty);
	}

	public void displayFiles(String[] files) {
		for (int i = 0; files != null && i < files.length; i++) {
			inputFileText.setText(files[i]);
		}
	}
}
