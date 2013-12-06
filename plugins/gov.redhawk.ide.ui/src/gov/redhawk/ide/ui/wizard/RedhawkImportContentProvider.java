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
package gov.redhawk.ide.ui.wizard;

import gov.redhawk.ide.ui.wizard.RedhawkImportWizardPage1.ProjectRecord;

import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @since 9.1
 */
public class RedhawkImportContentProvider implements ITreeContentProvider {
	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof ProjectRecord[]) {
			ProjectRecord[] records = (ProjectRecord[]) inputElement;

			List<ProjectRecord> projects = new ArrayList<ProjectRecord>();

			// Only grab projects with an spd as projectSystemFile
			for (ProjectRecord project : records) {
				if (project.projectSystemFile.getName().matches(".+\\.spd.xml")) {
					projects.add(project);
				}
			}
			return projects.toArray();
		}
		return new Object[0];
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof ProjectRecord) {
			ProjectRecord project = (ProjectRecord) parentElement;
			SoftPkg softpkg = new RedhawkImportUtil().getSoftPkg(project.projectSystemFile.getAbsolutePath());

			List<ImplWrapper> implList = new ArrayList<ImplWrapper>();
			EList<Implementation> implementations = softpkg.getImplementation();
			for (Implementation impl : implementations) {
				ImplWrapper implWrapper = new ImplWrapper(project, impl);
				implList.add(implWrapper);
			}
			return implList.toArray();
		}
		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return element instanceof ProjectRecord;
	}
}
