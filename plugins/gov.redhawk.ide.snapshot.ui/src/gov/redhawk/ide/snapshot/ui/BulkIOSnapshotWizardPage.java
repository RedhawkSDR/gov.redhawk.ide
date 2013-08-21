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

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
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

	private BulkIOSnapshotSettings bulkIOsettings = new BulkIOSnapshotSettings();

	public BulkIOSnapshotWizardPage(String pageName, ImageDescriptor titleImage) {
		super(pageName, "Port Snapshot", titleImage);
		setDescription("Write a stream of samples from the port to the given file.");
	}

	public BulkIOSnapshotSettings getBulkIOsettings() {
		return bulkIOsettings;
	}

	@Override
	public void createControl(Composite main) {
		final Composite parent = new Composite(main, SWT.None);
		parent.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).create());

		DataBindingContext context = getContext();

		//Add Combo Box and text field to input how to capture samples
		Label label;
		final ComboViewer captureCombo = new ComboViewer(parent, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.SIMPLE);
		captureCombo.setLabelProvider(new LabelProvider());
		captureCombo.setContentProvider(new ArrayContentProvider());
		captureCombo.setInput(CaptureMethod.values());
		context.bindValue(ViewerProperties.singleSelection().observeDelayed(500, captureCombo), PojoObservables.observeValue(bulkIOsettings, "captureMethod"));

		final Text samplesTxt = new Text(parent, SWT.BORDER);
		samplesTxt.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(1, 1).create());
		//ensure that invalid number of samples are caught and displayed
		UpdateValueStrategy validateSamples = new UpdateValueStrategy();
		validateSamples.setBeforeSetValidator(new IValidator() {
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
		final Binding samplesBinding = context.bindValue(WidgetProperties.text(SWT.Modify).observeDelayed(500, samplesTxt),
			PojoObservables.observeValue(bulkIOsettings, "samples"), validateSamples, null);

		final Label unitsLabel = new Label(parent, SWT.None);
		unitsLabel.setText("");
		GridData unitsLayout = new GridData();
		unitsLayout.widthHint = 20;
		unitsLabel.setLayoutData(unitsLayout);
		//update validator, set text field enable, and units as needed
		captureCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				CaptureMethod method = bulkIOsettings.getCaptureMethod();
				if (method == CaptureMethod.INDEFINITELY) {
					samplesTxt.setText("1");
					unitsLabel.setText("");
					samplesBinding.updateTargetToModel();
					samplesTxt.setEnabled(false);
					return;
				} else if (method == CaptureMethod.CLOCK_TIME || method == CaptureMethod.SAMPLE_TIME) {
					unitsLabel.setText("(s)");
					samplesTxt.setEnabled(true);
					samplesBinding.updateTargetToModel();
				} else {
					unitsLabel.setText("");
					samplesTxt.setEnabled(true);
					samplesBinding.updateTargetToModel();
				}
			}
		});

		createOutputControls(parent);

		setPageComplete(false);
		setPageSupport(WizardPageSupport.create(this, context));
		setControl(parent);
	}
}
