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

import java.util.Map;

import gov.redhawk.ide.snapshot.internal.capture.CorbaDataReceiver;
import gov.redhawk.ide.snapshot.internal.capture.CorbaNumSamplesReceiver;
import gov.redhawk.ide.snapshot.internal.capture.CorbaSignalTimeReceiver;
import gov.redhawk.ide.snapshot.internal.capture.CorbaSystemTimeReceiver;
import gov.redhawk.model.sca.ScaUsesPort;

/**
 *
 */
public class BulkIOSnapshotWizard extends SnapshotWizard {

	private CorbaDataReceiver corbaReceiver;
	private BulkIOSnapshotWizardPage bulkIOPage;
	private ScaUsesPort port;

	public void setPort(ScaUsesPort port) {
		this.port = port;
	}

	public ScaUsesPort getPort() {
		return port;
	}

	@Override
	public void addPages() {
		Map<String, Boolean> connectionIds = ScaUsesPort.Util.getConnectionIds(this.getPort());
		bulkIOPage = new BulkIOSnapshotWizardPage("snapshot", null, connectionIds);
		setSnapshotPage(bulkIOPage);

		addPage(bulkIOPage);
	}

	/**
	 * @since 3.0
	 */
	public CorbaDataReceiver getCorbaReceiver() {
		return this.corbaReceiver;
	}

	@Override
	public boolean performFinish() {
		boolean retVal = super.performFinish();
		if (retVal) {
			BulkIOSnapshotSettings bulkIOSettings = bulkIOPage.getBulkIOsettings();
			CaptureMethod method = bulkIOSettings.getCaptureMethod();
			bulkIOPage.saveWidgetValues(bulkIOSettings);
			switch (method) {
			case CLOCK_TIME:
				corbaReceiver = new CorbaSystemTimeReceiver();
				((CorbaSystemTimeReceiver) corbaReceiver).setTimeInSeconds(bulkIOSettings.getSamples());
				break;
			case INDEFINITELY:
				corbaReceiver = new CorbaDataReceiver();
				break;
			case NUM_SAMPLES:
				corbaReceiver = new CorbaNumSamplesReceiver();
				((CorbaNumSamplesReceiver) corbaReceiver).setSamples((long) bulkIOSettings.getSamples());
				break;
			case SAMPLE_TIME:
				corbaReceiver = new CorbaSignalTimeReceiver();
				((CorbaSignalTimeReceiver) corbaReceiver).setTimeInSeconds(bulkIOSettings.getSamples());
				break;
			default:
				throw new IllegalStateException("Unknown capture type: " + method);
			}
			corbaReceiver.setDataWriter(getDataWriter());
			corbaReceiver.setConnectionID(bulkIOSettings.getConnectionID());
		}
		return retVal;
	}

}
