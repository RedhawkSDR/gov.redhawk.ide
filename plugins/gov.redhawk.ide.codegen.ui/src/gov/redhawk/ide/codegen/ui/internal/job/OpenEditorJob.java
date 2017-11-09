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
package gov.redhawk.ide.codegen.ui.internal.job;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.progress.WorkbenchJob;

import gov.redhawk.ide.codegen.ui.RedhawkCodegenUiActivator;

public class OpenEditorJob extends WorkbenchJob {

	private IFile mainFile;

	public OpenEditorJob(String name, IFile mainFile) {
		super(name);
		
		this.mainFile = mainFile;
	}

	@Override
	public IStatus runInUIThread(IProgressMonitor monitor) {
		try {
			IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), mainFile, true);
		} catch (final PartInitException p) {
			return new Status(IStatus.WARNING, RedhawkCodegenUiActivator.PLUGIN_ID, "Unable to open main file for editing.");
		}
		return new Status(IStatus.OK, RedhawkCodegenUiActivator.PLUGIN_ID, "");
	}


}
