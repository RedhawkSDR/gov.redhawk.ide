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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class BulkIOSnapshotWizardPage extends SnapshotWizardPage {

	private static final int UPDATE_DELAY_MS = 100;

	private BulkIOSnapshotSettings bulkIOsettings = new BulkIOSnapshotSettings();
	private Map<String, Boolean> connectionIds;
	private Text samplesTxt;
	private Label unitsLabel;
	private Binding samplesBinding;

	private Text connectionIDField;

	public BulkIOSnapshotWizardPage(String pageName, ImageDescriptor titleImage) {
		this(pageName, titleImage, new HashMap<String, Boolean>());
	}

	/**
	 * @since 2.0
	 */
	public BulkIOSnapshotWizardPage(String pageName, ImageDescriptor titleImage, Map<String, Boolean> connectionIds) {
		super(pageName, "Port Snapshot", titleImage);
		setDescription("Write a stream of samples from the Port to the given file.");
		this.connectionIds = connectionIds;
	}

	public BulkIOSnapshotSettings getBulkIOsettings() {
		return bulkIOsettings;
	}

	@Override
	public void createControl(Composite main) {
		setupDialogSettingsStorage(); // for saving wizard page settings

		final Composite parent = new Composite(main, SWT.None);
		parent.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).create());

		DataBindingContext dataBindingCtx = getContext();

		// === capture method (how to capture samples) ===
		final ComboViewer captureCombo = new ComboViewer(parent, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.SIMPLE);
		captureCombo.setLabelProvider(new LabelProvider());
		captureCombo.setContentProvider(ArrayContentProvider.getInstance()); // ArrayContentProvider does not store any state, therefore can re-use instances
		captureCombo.setInput(CaptureMethod.values());
		@SuppressWarnings("unchecked")
		IObservableValue< ? > captureMethodObservable = BeanProperties.value(bulkIOsettings.getClass(), BulkIOSnapshotSettings.PROP_CAPTURE_METHOD).observe(
			bulkIOsettings);
		dataBindingCtx.bindValue(ViewerProperties.singleSelection().observe(captureCombo), captureMethodObservable);

		// === number of samples ===
		samplesTxt = new Text(parent, SWT.BORDER);
		samplesTxt.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(1, 1).create());
		UpdateValueStrategy validateSamples = createSamplesValidatorStrategy(); // validator to ensure that invalid number of samples are caught and displayed
		@SuppressWarnings("unchecked")
		IObservableValue< ? > samplesObservable = BeanProperties.value(bulkIOsettings.getClass(), BulkIOSnapshotSettings.PROP_SAMPLES).observe(bulkIOsettings);
		samplesBinding = dataBindingCtx.bindValue(WidgetProperties.text(SWT.Modify).observeDelayed(UPDATE_DELAY_MS, samplesTxt), samplesObservable,
			validateSamples, null);

		// === units for number samples field ===
		unitsLabel = new Label(parent, SWT.None);
		unitsLabel.setText("");
		unitsLabel.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(1, 1).hint(20, SWT.DEFAULT).create());
		// update validator, set text field enable, and units as needed
		captureCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				CaptureMethod method = bulkIOsettings.getCaptureMethod();
				updateControls(method);
			}
		});

		// === connection ID ==
		// Widget is dependent on if port is multi-out or not (if connectionIds is empty, it is not).
		Label label = new Label(parent, SWT.None);
		if (connectionIds.isEmpty()) {
			label.setText("Connection ID (Optional):");
			connectionIDField = new Text(parent, SWT.BORDER);
			connectionIDField.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(2, 1).create());
			connectionIDField.setToolTipText("Custom Port connection ID to use vs a generated one.");
			@SuppressWarnings("unchecked")
			IObservableValue< ? > connectionIdObservable = BeanProperties.value(bulkIOsettings.getClass(), BulkIOSnapshotSettings.PROP_CONNECTION_ID).observe(
				bulkIOsettings);
			dataBindingCtx.bindValue(WidgetProperties.text(SWT.Modify).observe(connectionIDField), connectionIdObservable);
		} else {
			label.setText("Connection ID:");
			final ComboViewer connectionIDComboField = new ComboViewer(parent, SWT.BORDER | SWT.READ_ONLY);
			connectionIDComboField.getCombo().setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(2, 1).create());
			connectionIDComboField.getCombo().setToolTipText("Available mulit-out port connection IDs");
			connectionIDComboField.setContentProvider(ArrayContentProvider.getInstance());
			connectionIDComboField.setLabelProvider(new LabelProvider() {
				@Override
				public String getText(Object element) {
					if (element instanceof Entry) {
						return ((Entry< ? , ? >) element).getKey().toString();
					}
					return super.getText(element);
				}
			});
			connectionIDComboField.setInput(connectionIds.entrySet());

			ISWTObservableValue targetObservableValue = WidgetProperties.selection().observe(connectionIDComboField.getCombo());
			@SuppressWarnings("unchecked")
			IObservableValue< ? > modelObservableValue = BeanProperties.value(bulkIOsettings.getClass(), BulkIOSnapshotSettings.PROP_CONNECTION_ID).observe(
				bulkIOsettings);
			UpdateValueStrategy targetToModel = new UpdateValueStrategy();
			targetToModel.setAfterGetValidator(new IValidator() {

				@Override
				public IStatus validate(Object value) {
					Object element = connectionIDComboField.getStructuredSelection().getFirstElement();
					if (element instanceof Entry) {
						Boolean isValid = (Boolean) ((Entry< ? , ? >) element).getValue();
						if (!isValid) {
							return new Status(Status.ERROR, SnapshotActivator.PLUGIN_ID, "Selected connection ID is already in use");
						}
					}
					return Status.OK_STATUS;
				}
			});
			dataBindingCtx.bindValue(targetObservableValue, modelObservableValue, targetToModel, null);

			// Set selection at first available connectionId
			// This needs to happen after the data binding has been initialized
			if (connectionIDComboField != null) {
				for (Entry<String, Boolean> entry : connectionIds.entrySet()) {
					if (entry.getValue()) {
						connectionIDComboField.setSelection(new StructuredSelection(entry));
						break;
					}
				}
			}
		}

		// === create output control widgets ==
		createOutputControls(parent);

		bulkIOsettings.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (BulkIOSnapshotSettings.PROP_CAPTURE_METHOD.equals(evt.getPropertyName())) {
					updateControls((CaptureMethod) evt.getNewValue());
				}
			}
		});

		setPageComplete(false);
		setPageSupport(WizardPageSupport.create(this, dataBindingCtx));
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
		pageSettings.put(BulkIOSnapshotSettings.PROP_SAMPLES, bss.getSamples());
		pageSettings.put(BulkIOSnapshotSettings.PROP_CAPTURE_METHOD, bss.getCaptureMethod().name());
		pageSettings.put(BulkIOSnapshotSettings.PROP_CONNECTION_ID, bss.getConnectionID());
	}

	private void restoreWidgetValues(BulkIOSnapshotSettings bss) {
		IDialogSettings pageSettings = getPageSettingsSection();
		if (pageSettings != null && bss != null) {
			String tmp;
			tmp = pageSettings.get(BulkIOSnapshotSettings.PROP_SAMPLES);
			if (tmp != null) {
				try {
					bss.setSamples(Double.valueOf(tmp));
				} catch (NumberFormatException nfe) {
					// PASS - ignore
				}
			}
			tmp = pageSettings.get(BulkIOSnapshotSettings.PROP_CAPTURE_METHOD);
			if (tmp != null) {
				try {
					bss.setCaptureMethod(CaptureMethod.valueOf(tmp));
				} catch (IllegalArgumentException iae) {
					// PASS - ignore
				}
			}

			// Only preserve connection ID when using the Text widget
			tmp = pageSettings.get(BulkIOSnapshotSettings.PROP_CONNECTION_ID);
			if (tmp != null && connectionIDField != null) {
				bss.setConnectionID(tmp);
			}
		}
	}
}
