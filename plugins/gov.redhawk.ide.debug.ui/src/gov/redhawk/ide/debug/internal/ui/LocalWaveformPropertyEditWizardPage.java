/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.debug.internal.ui;

import gov.redhawk.model.sca.ScaAbstractProperty;
import gov.redhawk.model.sca.ScaFactory;
import gov.redhawk.model.sca.ScaWaveform;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.swt.widgets.Composite;

public class LocalWaveformPropertyEditWizardPage extends AbstractPropertyEditWizardPage<ScaWaveform> {
	private LaunchLocalWaveformWizard wizard;

	public LocalWaveformPropertyEditWizardPage(final String pageName, LaunchLocalWaveformWizard wizard) {
		super(pageName);
		this.setDescription("Provide the initial configuration for the Waveform");
		this.wizard = wizard;
	}

	@Override
	public void createControl(final Composite parent) {
		super.createControl(parent);
		this.wizard.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("softwareAssembly")) {
					init((SoftwareAssembly) evt.getNewValue());
				}
			}
		});
		init(wizard.getSoftwareAssembly());
	}

	private void init(final SoftwareAssembly sad) {

		if (sad == null) {
			setPropertyContainer(null);
			return;
		}

		final ScaWaveform newWaveform = ScaFactory.eINSTANCE.createScaWaveform();
		newWaveform.setDataProvidersEnabled(false);
		newWaveform.setProfileObj(sad);

		for (final ScaAbstractProperty< ? > prop : newWaveform.fetchProperties(null)) {
			prop.setIgnoreRemoteSet(true);
		}
		setPropertyContainer(newWaveform);
	}

}
