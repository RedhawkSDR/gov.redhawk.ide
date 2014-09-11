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
package gov.redhawk.ide.debug.internal.ui;

import gov.redhawk.model.sca.ScaAbstractProperty;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.swt.widgets.Composite;

/**
 * 
 */
public class LocalComponentPropertyEditWizardPage extends AbstractPropertyEditWizardPage<ScaComponent> {

	private LaunchComponentWizard wizard;

	protected LocalComponentPropertyEditWizardPage(LaunchComponentWizard wizard) {
		super("propPage");
		setDescription("Set the intial value of properties.");
		this.wizard = wizard;
	}

	@Override
	public void createControl(final Composite parent) {
		super.createControl(parent);
		this.wizard.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("softPkg")) {
					init((SoftPkg) evt.getNewValue());
				}
			}
		});
		init(wizard.getSoftPkg());
	}

	private void init(final SoftPkg softPkg) {

		if (softPkg == null) {
			setPropertyContainer(null);
			return;
		}

		final ScaComponent newComponent = ScaFactory.eINSTANCE.createScaComponent();
		newComponent.setDataProvidersEnabled(false);
		newComponent.setProfileObj(softPkg);

		for (final ScaAbstractProperty< ? > prop : newComponent.fetchProperties(null)) {
			prop.setIgnoreRemoteSet(true);
		}
		setPropertyContainer(newComponent);
	}

}
