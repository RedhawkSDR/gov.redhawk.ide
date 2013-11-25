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
package gov.redhawk.ide.snapshot.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * 
 */
public class BulkIOSnapshotWizardPage extends SnapshotWizardPage {

	private static final int UPDATE_DELAY_MS = 200;

	// === BEGIN: dialog page settings storage keys === 
	private static final String BSS_NUM_SAMPLES = "numberSamples";
	private static final String BSS_CAPTURE_METHOD = "captureMethod";
	// === END: dialog page settings storage keys === 

	private BulkIOSnapshotSettings bulkIOsettings = new BulkIOSnapshotSettings();
	private Text samplesTxt;
	private Label unitsLabel;
	private Binding samplesBinding;

	public BulkIOSnapshotWizardPage(String pageName, ImageDescriptor titleImage) {
		super(pageName, "Port Snapshot", titleImage);
		setDescription("Write a stream of samples from the port to the given file.");
	}

	public BulkIOSnapshotSettings getBulkIOsettings() {
		return bulkIOsettings;
	}

	@Override
	public void createControl(Composite main) {
		setupDialogSettingsStorage(); // for saving wizard page settings

		final Composite parent = new Composite(main, SWT.None);
		parent.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).create());

		DataBindingContext context = getContext();

		// Add Combo Box and text field to input how to capture samples
		final ComboViewer captureCombo = new ComboViewer(parent, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.SIMPLE);
		captureCombo.setLabelProvider(new LabelProvider());
		captureCombo.setContentProvider(new ArrayContentProvider());
		captureCombo.setInput(CaptureMethod.values());
		context.bindValue(ViewerProperties.singleSelection().observe(captureCombo), BeansObservables.observeValue(bulkIOsettings, "captureMethod"));

		samplesTxt = new Text(parent, SWT.BORDER);
		samplesTxt.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(1, 1).create());
		// ensure that invalid number of samples are caught and displayed
		UpdateValueStrategy validateSamples = createSamplesValidatorStrategy();
		samplesBinding = context.bindValue(WidgetProperties.text(SWT.Modify).observeDelayed(UPDATE_DELAY_MS, samplesTxt),
			BeansObservables.observeValue(bulkIOsettings, "samples"), validateSamples, null);

		unitsLabel = new Label(parent, SWT.None);
		unitsLabel.setText("");
		GridData unitsLayout = new GridData();
		unitsLayout.widthHint = 20;
		unitsLabel.setLayoutData(unitsLayout);
		// update validator, set text field enable, and units as needed
		captureCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				CaptureMethod method = bulkIOsettings.getCaptureMethod();
				updateControls(method);
			}
		});

		createOutputControls(parent);

		bulkIOsettings.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ("captureMethod".equals(evt.getPropertyName())) {
					updateControls((CaptureMethod) evt.getNewValue());
				}
			}
		});

		setPageComplete(false);
		setPageSupport(WizardPageSupport.create(this, context));
		setControl(parent);

		restoreWidgetValues(bulkIOsettings);
	}

	protected void updateControls(CaptureMethod method) {
		switch (method) {
		case INDEFINITELY:
			samplesTxt.setText("-1");
			unitsLabel.setText("");
			samplesBinding.updateTargetToModel();
			samplesTxt.setEnabled(false);
			break;
		case CLOCK_TIME:
		case SAMPLE_TIME:
			if (bulkIOsettings.getSamples() < 0) {
				samplesTxt.setText("1024");
			}
			unitsLabel.setText("(s)");
			samplesTxt.setEnabled(true);
			samplesBinding.updateTargetToModel();
			break;
		case NUM_SAMPLES:
			if (bulkIOsettings.getSamples() < 0) {
				samplesTxt.setText("1024");
			}
			unitsLabel.setText("");
			samplesTxt.setEnabled(true);
			samplesBinding.updateTargetToModel();
			break;
		default:
			break;
		}
	}

	private UpdateValueStrategy createSamplesValidatorStrategy() {
		UpdateValueStrategy updateValueStrategy = new UpdateValueStrategy();
		updateValueStrategy.setBeforeSetValidator(new IValidator() {
			@Override
			public IStatus validate(Object value) {
				if (value instanceof Double) {
					CaptureMethod method = bulkIOsettings.getCaptureMethod();
					if (method == CaptureMethod.INDEFINITELY) {
						return ValidationStatus.ok();
					} else if (((Double) value).doubleValue() <= 0) {
						return ValidationStatus.error(method + " must be greater than 0");
					} else if (method == CaptureMethod.NUM_SAMPLES) {
						double val = ((Double) value).doubleValue();
						if (val > Long.MAX_VALUE) {
							return ValidationStatus.error(method + " must less than or equal to " + Long.MAX_VALUE);
						}
						if ((val - (long) val) > 0) {
							return ValidationStatus.error(method + " must be a whole number");
						}
						return ValidationStatus.ok();
					} else {
						return ValidationStatus.ok();
					}
				} else {
					return ValidationStatus.error("The Number of Samples must be a positive number");
				}
			}
		});

		return updateValueStrategy;
	}

	protected void saveWidgetValues(BulkIOSnapshotSettings bss) {
		IDialogSettings pageSettings = getPageSettingsSection();
		pageSettings.put(BSS_NUM_SAMPLES, bss.getSamples());
		pageSettings.put(BSS_CAPTURE_METHOD, bss.getCaptureMethod().name());
	}

	private void restoreWidgetValues(BulkIOSnapshotSettings bss) {
		IDialogSettings pageSettings = getPageSettingsSection();
		if (pageSettings != null && bss != null) {
			String tmp;
			tmp = pageSettings.get(BSS_NUM_SAMPLES);
			if (tmp != null) {
				try {
					bss.setSamples(Double.valueOf(tmp));
				} catch (NumberFormatException nfe) {
					// PASS - ignore
				}
			}
			tmp = pageSettings.get(BSS_CAPTURE_METHOD);
			if (tmp != null) {
				try {
					bss.setCaptureMethod(CaptureMethod.valueOf(tmp));
				} catch (IllegalArgumentException iae) {
					// PASS - ignore
				}
			}
		}
	}
}
