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
import gov.redhawk.ide.dcd.generator.newservice.ServiceProjectCreator;
import gov.redhawk.ide.spd.ui.wizard.ImplementationWizardPage;
import gov.redhawk.ide.spd.ui.wizard.NewScaResourceWizard;
import gov.redhawk.ide.ui.wizard.IImportWizard;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import mil.jpeojtrs.sca.spd.Implementation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

public class NewScaServiceCreationProjectWizard extends NewScaResourceWizard implements IImportWizard {

	public NewScaServiceCreationProjectWizard() {
		super(ICodeGeneratorDescriptor.COMPONENT_TYPE_SERVICE);
		this.setWindowTitle("New Service Project");
		this.setNeedsProgressMonitor(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void addPages() {
		setResourcePropertiesPage(new ScaServiceProjectPropertiesWizardPage("", "Service"));
		addPage(getResourcePropertiesPage());
		setImplPage(new ImplementationWizardPage("", ICodeGeneratorDescriptor.COMPONENT_TYPE_SERVICE));
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
    protected Implementation getImplementation() {
	    return getImplPage().getImplementation();
    }

	@Override
    protected String getID() {
		return getResourcePropertiesPage().getIdGroup().getId();
    }

	@Override
    protected String getProjectName() {
	    return getResourcePropertiesPage().getProjectName();
    }
	
	@Override
    protected String getType() {
		return ICodeGeneratorDescriptor.COMPONENT_TYPE_RESOURCE;
    }

	@Override
    protected IFile createComponentFiles(IProject project, String name, String spdId, String author, IProgressMonitor monitor) throws CoreException {
		final String serviceRepId = ((ScaServiceProjectPropertiesWizardPage) this.getResourcePropertiesPage()).getRepId();
	    return ServiceProjectCreator.createServiceFiles(project, name, spdId, author, serviceRepId, monitor);
    }

	@Override
    protected IProject createEmptyProject(String projectName, java.net.URI locationURI, IProgressMonitor monitor) throws CoreException {
	    return ServiceProjectCreator.createEmptyProject(projectName, locationURI, monitor);
    }
}
