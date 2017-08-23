/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.sdr.internal.ui.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import gov.redhawk.sca.properties.Category;
import gov.redhawk.sca.properties.IPropertiesProvider;
import mil.jpeojtrs.sca.spd.SoftPkg;

public class WorkspacePropertiesProvider implements IPropertiesProvider {

	private ResourceSet resourceSet = new ResourceSetImpl();
	private List<SoftPkg> pathToSpd = new ArrayList<>();

	public WorkspacePropertiesProvider() {
		findSpds();
	}

	private void findSpds() {
		for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
			try {
				for (IResource resource : project.members()) {
					if (resource.getType() == IResource.FILE && resource.getName().endsWith(".spd.xml")) {
						SoftPkg spd = attemptLoad((IFile) resource);
						pathToSpd.add(spd);
					}
				}
			} catch (CoreException e) {
				SdrUiPlugin.getDefault().logError("Unable to enumerate members of project " + project.getName(), e);
			}
		}
	}

	private SoftPkg attemptLoad(IFile file) {
		Resource emfResource = resourceSet.getResource(URI.createURI(file.getLocationURI().toString()), true);
		SoftPkg spd = SoftPkg.Util.getSoftPkg(emfResource);
		if (spd != null && spd.getPropertyFile() != null && spd.getPropertyFile().getProperties() != null) {
			return spd;
		}
		if (emfResource != null) {
			resourceSet.getResources().remove(emfResource);
		}
		return null;
	}

	@Override
	public String getName() {
		return "Workspace";
	}

	@Override
	public String getDescription() {
		return "Properties from projects in the workspace";
	}

	@Override
	public String getIconPluginId() {
		return "org.eclipse.ui.ide";
	}

	@Override
	public String getIconPath() {
		return "icons/full/obj16/prj_obj.png";
	}

	@Override
	public List<Category> getCategories() {
		List<Category> categories = new ArrayList<Category>();
		for (SoftPkg spd : pathToSpd) {
			categories.add(new SpdCategory(spd));
		}
		return categories;
	}
}
