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
package gov.redhawk.ide.graphiti.sad.ui.diagram.wizards;

import gov.redhawk.frontend.util.TunerProperties;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class UsesDeviceFrontEndTunerWizardPage extends WizardPage {

	// inner class model used to store user selections
	public static class Model {

		public static final String TUNER_TYPE = "tunerType";
		public static final String ALLOCATION_ID = "allocationId";
		public static final String CENTER_FREQUENCY = "centerFrequency";
		public static final String BANDWIDTH = "bandwidth";
		public static final String BANDWIDTH_TOLERANCE = "bandwidthTolerance";
		public static final String SAMPLE_RATE = "sampleRate";
		public static final String SAMPLE_RATE_TOLERANCE = "sampleRateTolerance";
		public static final String DEVICE_CONTROL = "deviceControl";
		public static final String GROUP_ID = "groupId";
		public static final String RF_FLOW_ID = "rfFlowId";

		private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

		private String tunerType;
		private String allocationId;
		private Double centerFrequency;
		private Double bandwidth;
		private Double bandwidthTolerance;
		private Double sampleRate;
		private Double sampleRateTolerance;
		private Boolean deviceControl;
		private String groupId;
		private String rfFlowId;
		

		public Model() {
		}

		public String getTunerType() {
			return tunerType;
		}

		public void setTunerType(String tunerType) {
			final String oldValue = this.tunerType;
			this.tunerType = tunerType;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, Model.TUNER_TYPE, oldValue, tunerType));
		}
		
		
		public String getAllocationId() {
			return allocationId;
		}

		public void setAllocationId(String allocationId) {
			final String oldValue = this.allocationId;
			this.allocationId = allocationId;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, Model.ALLOCATION_ID, oldValue, allocationId));
		}

		
		public Double getCenterFrequency() {
			return centerFrequency;
		}

		public void setCenterFrequency(Double centerFrequency) {
			final Double oldValue = this.centerFrequency;
			this.centerFrequency = centerFrequency;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, Model.CENTER_FREQUENCY, oldValue, centerFrequency));
		}

		public Double getBandwidth() {
			return bandwidth;
		}

		public void setBandwidth(Double bandwidth) {
			final Double oldValue = this.bandwidth;
			this.bandwidth = bandwidth;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, Model.BANDWIDTH, oldValue, bandwidth));
		}

		public Double getBandwidthTolerance() {
			return bandwidthTolerance;
		}

		public void setBandwidthTolerance(Double bandwidthTolerance) {
			final Double oldValue = this.bandwidthTolerance;
			this.bandwidthTolerance = bandwidthTolerance;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, Model.BANDWIDTH_TOLERANCE, oldValue, bandwidthTolerance));
		}
		
		public Double getSampleRate() {
			return sampleRate;
		}

		public void setSampleRate(Double sampleRate) {
			final Double oldValue = this.sampleRate;
			this.sampleRate = sampleRate;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, Model.SAMPLE_RATE, oldValue, sampleRate));
		}

		public Double getSampleRateTolerance() {
			return sampleRateTolerance;
		}

		public void setSampleRateTolerance(Double sampleRateTolerance) {
			final Double oldValue = this.sampleRateTolerance;
			this.sampleRateTolerance = sampleRateTolerance;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, Model.SAMPLE_RATE_TOLERANCE, oldValue, sampleRateTolerance));
		}
		
		public Boolean getDeviceControl() {
			return deviceControl;
		}

		public void setDeviceControl(Boolean deviceControl) {
			final Boolean oldValue = this.deviceControl;
			this.deviceControl = deviceControl;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, Model.DEVICE_CONTROL, oldValue, deviceControl));
		}	
		
		public String getGroupId() {
			return groupId;
		}

		public void setGroupId(String groupId) {
			final String oldValue = this.groupId;
			this.groupId = groupId;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, Model.GROUP_ID, oldValue, groupId));
		}
		
		public String getRfFlowId() {
			return rfFlowId;
		}

		public void setRfFlowId(String rfFlowId) {
			final String oldValue = this.rfFlowId;
			this.rfFlowId = rfFlowId;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, Model.RF_FLOW_ID, oldValue, rfFlowId));
		}
		

		public void addPropertyChangeListener(final PropertyChangeListener listener) {
			this.pcs.addPropertyChangeListener(listener);
		}

		public void removePropertyChangeListener(final PropertyChangeListener listener) {
			this.pcs.removePropertyChangeListener(listener);
		}

		public boolean isComplete() {
			if (this.tunerType.length() == 0) {
				return false;
			}
			return true;
		}
	};

	private static final ImageDescriptor TITLE_IMAGE = null;

	private Model model;
	private DataBindingContext dbc;

	private Combo tunerTypeCombo;
	private Text allocationIdText;
	private Text centerFrequencyText;
	private Text bandwidthText;
	private Text bandwidthToleranceText;
	private Text sampleRateText;
	private Text sampleRateToleranceText;
	private Button deviceControlCheckbox;
	private Text groupIdText;
	private Text rfFlowIdText;
	
	public UsesDeviceFrontEndTunerWizardPage() {
		super("findByCorbaName", "Uses FrontEnd Tuner Device", TITLE_IMAGE);
		this.setDescription("Enter the Tuner Allocation specifications for the Front End Device");

		model = new Model();
		dbc = new DataBindingContext();
	
	}

	
	@Override
	public void createControl(Composite parent) {

		
		WizardPageSupport.create(this, dbc);

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(new GridLayout(1, false));

		// Tuner Type
		Label tunerTypeLabel = new Label(composite, SWT.NONE);
		tunerTypeLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		tunerTypeLabel.setText(TunerProperties.TunerAllocationProperties.TUNER_TYPE.getName() + ":");

		tunerTypeCombo = new Combo(composite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		ComboViewer comboViewer = new ComboViewer(tunerTypeCombo);
		tunerTypeCombo.setItems(new String[] {"TX", "RX", "CHANNELIZER", "DDC", "RX_DIGITIZER", "RX_DIGTIZIER_CHANNELIZER"});
		tunerTypeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		tunerTypeCombo.setToolTipText(TunerProperties.TunerAllocationProperties.TUNER_TYPE.getDescription());
		dbc.bindValue(ViewersObservables.observeSingleSelection(comboViewer), 
			BeansObservables.observeValue(model, Model.TUNER_TYPE));

		// AllocationId
		Label allocationIdLabel = new Label(composite, SWT.NONE);
		allocationIdLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		allocationIdLabel.setText(TunerProperties.TunerAllocationProperties.ALLOCATION_ID.getName() + ":");

		allocationIdText = new Text(composite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		allocationIdText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		allocationIdText.setToolTipText(TunerProperties.TunerAllocationProperties.ALLOCATION_ID.getDescription());
		dbc.bindValue(SWTObservables.observeText(allocationIdText, SWT.Modify), 
			BeansObservables.observeValue(model, Model.ALLOCATION_ID));
		
		// CenterFrequency
		Label centerFrequencyLabel = new Label(composite, SWT.NONE);
		centerFrequencyLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		centerFrequencyLabel.setText(TunerProperties.TunerAllocationProperties.CENTER_FREQUENCY.getName() + ":");

		centerFrequencyText = new Text(composite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		centerFrequencyText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		centerFrequencyText.setToolTipText(TunerProperties.TunerAllocationProperties.CENTER_FREQUENCY.getDescription());
		dbc.bindValue(SWTObservables.observeText(centerFrequencyText, SWT.Modify), 
			BeansObservables.observeValue(model, Model.CENTER_FREQUENCY));
		
		// Bandwidth
		Label bandwidthLabel = new Label(composite, SWT.NONE);
		bandwidthLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		bandwidthLabel.setText(TunerProperties.TunerAllocationProperties.BANDWIDTH.getName() + ":");

		bandwidthText = new Text(composite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		bandwidthText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		bandwidthText.setToolTipText(TunerProperties.TunerAllocationProperties.BANDWIDTH.getDescription());
		dbc.bindValue(SWTObservables.observeText(bandwidthText, SWT.Modify), 
			BeansObservables.observeValue(model, Model.BANDWIDTH));
		
		// Bandwidth Tolerance
		Label bandwidthToleranceLabel = new Label(composite, SWT.NONE);
		bandwidthToleranceLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		bandwidthToleranceLabel.setText(TunerProperties.TunerAllocationProperties.BANDWIDTH_TOLERANCE.getName() + ":");

		bandwidthToleranceText = new Text(composite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		bandwidthToleranceText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		bandwidthToleranceText.setToolTipText(TunerProperties.TunerAllocationProperties.BANDWIDTH_TOLERANCE.getDescription());
		dbc.bindValue(SWTObservables.observeText(bandwidthToleranceText, SWT.Modify), 
			BeansObservables.observeValue(model, Model.BANDWIDTH_TOLERANCE));
		
		// Sample Rate
		Label sampleRateLabel = new Label(composite, SWT.NONE);
		sampleRateLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		sampleRateLabel.setText(TunerProperties.TunerAllocationProperties.SAMPLE_RATE.getName() + ":");

		sampleRateText = new Text(composite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		sampleRateText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		sampleRateText.setToolTipText(TunerProperties.TunerAllocationProperties.SAMPLE_RATE.getDescription());
		dbc.bindValue(SWTObservables.observeText(sampleRateText, SWT.Modify), 
			BeansObservables.observeValue(model, Model.SAMPLE_RATE));
		
		// Sample Rate Tolerance
		Label sampleRateToleranceLabel = new Label(composite, SWT.NONE);
		sampleRateToleranceLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		sampleRateToleranceLabel.setText(TunerProperties.TunerAllocationProperties.SAMPLE_RATE_TOLERANCE.getName() + ":");

		sampleRateToleranceText = new Text(composite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		sampleRateToleranceText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		sampleRateToleranceText.setToolTipText(TunerProperties.TunerAllocationProperties.SAMPLE_RATE_TOLERANCE.getDescription());
		dbc.bindValue(SWTObservables.observeText(sampleRateToleranceText, SWT.Modify), 
			BeansObservables.observeValue(model, Model.SAMPLE_RATE_TOLERANCE));
		
		// Device Control
		Composite deviceComposite = new Composite(composite, SWT.NONE);
		deviceComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		deviceComposite.setLayout(new GridLayout(2, false));
		
		Label deviceControlLabel = new Label(deviceComposite, SWT.NONE);
		deviceControlLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		deviceControlLabel.setText(TunerProperties.TunerAllocationProperties.DEVICE_CONTROL.getName() + ":");

		deviceControlCheckbox = new Button(deviceComposite, SWT.CHECK | SWT.LEAD | SWT.BORDER);
		deviceControlCheckbox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		deviceControlCheckbox.setToolTipText(TunerProperties.TunerAllocationProperties.DEVICE_CONTROL.getDescription());
		dbc.bindValue(WidgetProperties.selection().observe(deviceControlCheckbox), 
			BeansObservables.observeValue(model, Model.DEVICE_CONTROL));
		
		// Group Id
		Label groupIdLabel = new Label(composite, SWT.NONE);
		groupIdLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		groupIdLabel.setText(TunerProperties.TunerAllocationProperties.GROUP_ID.getName() + ":");

		groupIdText = new Text(composite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		groupIdText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		groupIdText.setToolTipText(TunerProperties.TunerAllocationProperties.GROUP_ID.getDescription());
		dbc.bindValue(SWTObservables.observeText(groupIdText, SWT.Modify), 
			BeansObservables.observeValue(model, Model.GROUP_ID));
		
		// RF Flow Id
		Label rfFlowIdLabel = new Label(composite, SWT.NONE);
		rfFlowIdLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		rfFlowIdLabel.setText(TunerProperties.TunerAllocationProperties.RF_FLOW_ID.getName() + ":");

		rfFlowIdText = new Text(composite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		rfFlowIdText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		rfFlowIdText.setToolTipText(TunerProperties.TunerAllocationProperties.RF_FLOW_ID.getDescription());
		dbc.bindValue(SWTObservables.observeText(rfFlowIdText, SWT.Modify), 
			BeansObservables.observeValue(model, Model.RF_FLOW_ID));
		
		setControl(composite);

		dbc.updateModels();

	}


	public Model getModel() {
		return model;
	}

}