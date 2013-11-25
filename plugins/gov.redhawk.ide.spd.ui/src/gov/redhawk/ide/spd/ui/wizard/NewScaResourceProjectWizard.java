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
package gov.redhawk.ide.spd.ui.wizard;

import gov.redhawk.ide.codegen.ICodeGeneratorDescriptor;
import gov.redhawk.ide.codegen.util.ImplementationAndSettings;
import gov.redhawk.ide.spd.generator.newcomponent.ComponentProjectCreator;
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
 * 
 * @since 4.0
 */
public class NewScaResourceProjectWizard extends NewScaResourceWizard implements IImportWizard {



	/**
	 * 
	 */
	public NewScaResourceProjectWizard() {
		super(ICodeGeneratorDescriptor.COMPONENT_TYPE_RESOURCE);
		this.setWindowTitle("New Component Project");
		this.setNeedsProgressMonitor(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void addPages() {
		setResourcePropertiesPage(new ScaResourceProjectPropertiesWizardPage("", "Component"));
		this.addPage(getResourcePropertiesPage());
		setImplPage(new ImplementationWizardPage("", ICodeGeneratorDescriptor.COMPONENT_TYPE_RESOURCE));
		getImplPage().setDescription("Choose the initial settings for the new implementation.");
		this.addPage(getImplPage());

		this.getImplList().add(new ImplementationAndSettings(getImplPage().getImplementation(), getImplPage().getImplSettings()));
		
		this.addPage(new ScaResourceProjectPropertiesWizardPage("Womp womp womp", "Component"));
		
		try {
			final Field field = Wizard.class.getDeclaredField("pages");
			field.getModifiers();
			if (!Modifier.isPublic(field.getModifiers())) {
				field.setAccessible(true);
			}
			this.setWizPages((List<IWizardPage>) field.get(this));
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
    protected IFile createComponentFiles(IProject project, String name, String spdId, String authorName, IProgressMonitor monitor) throws CoreException {
	    return ComponentProjectCreator.createComponentFiles(project, name, spdId, authorName, monitor);
    }

	@Override
    protected IProject createEmptyProject(String projectName, URI locationURI, IProgressMonitor monitor) throws CoreException {
		return ComponentProjectCreator.createEmptyProject(projectName, locationURI, monitor);
    }

}
