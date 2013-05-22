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

package gov.redhawk.ide.dcd.ui.wizard;

import gov.redhawk.ide.codegen.ICodeGeneratorDescriptor;
import gov.redhawk.ide.codegen.util.ImplementationAndSettings;
import gov.redhawk.ide.dcd.generator.newdevice.DeviceProjectCreator;
import gov.redhawk.ide.spd.ui.wizard.ImplementationWizardPage;
import gov.redhawk.ide.spd.ui.wizard.NewScaResourceWizard;
import gov.redhawk.ide.ui.wizard.IImportWizard;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

/**
 * The Class NewScaResourceProjectWizard.
 */
public class NewScaDeviceCreationProjectWizard extends NewScaResourceWizard implements IImportWizard {


	/**
	 * 
	 */
	public NewScaDeviceCreationProjectWizard() {
		super(ICodeGeneratorDescriptor.COMPONENT_TYPE_DEVICE);
		this.setWindowTitle("New Device Project");
		this.setNeedsProgressMonitor(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void addPages() {
		setResourcePropertiesPage(new ScaDeviceProjectPropertiesWizardPage("", "Device"));
		addPage(getResourcePropertiesPage());
		setImplPage(new ImplementationWizardPage("", ICodeGeneratorDescriptor.COMPONENT_TYPE_DEVICE));
		getImplPage().setDescription("Choose the initial settings for the new implementation.");
		addPage(getImplPage());

		getImplList().add(new ImplementationAndSettings(getImplPage().getImplementation(), getImplPage().getImplSettings()));

		try {
			final Field field = Wizard.class.getDeclaredField("pages");
			field.getModifiers();
			if (!Modifier.isPublic(field.getModifiers())) {
				field.setAccessible(true);
			}
			setWizPages((List<IWizardPage>) field.get(this));
		} catch (final SecurityException e1) {
			// PASS
		} catch (final NoSuchFieldException e1) {
			// PASS
		} catch (final IllegalArgumentException e) {
			// PASS
		} catch (final IllegalAccessException e) {
			// PASS
		}
	}

	@Override
    protected IFile createComponentFiles(IProject project, String name, String spdId, String author, IProgressMonitor monitor) throws CoreException {
		String deviceType = ((ScaDeviceProjectPropertiesWizardPage) NewScaDeviceCreationProjectWizard.this.getResourcePropertiesPage()).getDeviceType();
		boolean aggregateDevice = ((ScaDeviceProjectPropertiesWizardPage) NewScaDeviceCreationProjectWizard.this.getResourcePropertiesPage()).getAggregateDeviceType();
	    return DeviceProjectCreator.createDeviceFiles(project, name, spdId, author, deviceType, aggregateDevice, monitor);
    }

	@Override
    protected IProject createEmptyProject(String projectName, URI locationURI, IProgressMonitor monitor) throws CoreException {
	    return DeviceProjectCreator.createEmptyProject(projectName, locationURI, monitor);
    }
}
