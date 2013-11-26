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

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.internal.wizards.datatransfer.DataTransferMessages;

/**
 * @since 9.1
 */
@SuppressWarnings("restriction")
public class RedhawkImportLabelProvider implements ITableLabelProvider {
	//CHECKSTYLE:OFF

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if(element instanceof ProjectRecord) {
			ProjectRecord project = (ProjectRecord) element;
			String dir = project.projectSystemFile.getParentFile().getAbsolutePath();
			switch(columnIndex) {
			case 0:
				return NLS.bind(DataTransferMessages.WizardProjectsImportPage_projectLabel, project.getProjectName(), dir);
			}
			return null;
		}
		
		if(element instanceof ImplWrapper) {
			ImplWrapper implWrapper = (ImplWrapper) element;
			switch(columnIndex) {
			case 0:
				return implWrapper.getImpl().getId() + " (implementation)";
			case 1:
				return implWrapper.getTemplate();
			}
		}
		return null;
	}



}
