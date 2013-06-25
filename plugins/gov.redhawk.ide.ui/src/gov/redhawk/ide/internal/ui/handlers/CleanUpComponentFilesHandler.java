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
package gov.redhawk.ide.internal.ui.handlers;

import gov.redhawk.model.sca.util.ModelUtil;

import java.io.IOException;

import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.progress.WorkbenchJob;

/**
 * @since 6.0
 */
public class CleanUpComponentFilesHandler extends AbstractHandler implements IHandler {

	private IStructuredSelection fSelection;

	private Resource resource;

	public CleanUpComponentFilesHandler() {
		// TODO Auto-generated constructor stub
	}

	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final WorkbenchJob cleanJob = new WorkbenchJob("Clean unused Component Files") {

			@Override
			public IStatus runInUIThread(final IProgressMonitor monitor) {
				final ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getSelectionService().getSelection();

				if (selection instanceof IStructuredSelection) {
					CleanUpComponentFilesHandler.this.fSelection = (IStructuredSelection) selection;
				}

				if (CleanUpComponentFilesHandler.this.fSelection != null && !CleanUpComponentFilesHandler.this.fSelection.isEmpty()) {
					final EObject eObject = getModel();

					final CleanUpComponentFilesAction cleanAction = new CleanUpComponentFilesAction();

					if (eObject != null) {
						cleanAction.setRoot(eObject);
					}

					cleanAction.run();

					if (cleanAction.isResourceDirty()) {
						try {
							CleanUpComponentFilesHandler.this.resource.save(null);
						} catch (final IOException e) {
							// PASS
						}
					}
				}
				return Status.OK_STATUS;
			}
		};

		cleanJob.setUser(true);
		cleanJob.setPriority(Job.LONG);
		cleanJob.schedule();

		return null;
	}

	private EObject getModel() {
		final Object obj = this.fSelection.getFirstElement();

		if (obj instanceof IFile) {
			final IFile file = (IFile) obj;
			final ResourceSet set = ScaResourceFactoryUtil.createResourceSet();
			this.resource = set.getResource(URI.createPlatformResourceURI(file.getFullPath().toString(), true), true);

			try {
				this.resource.load(null);
			} catch (final IOException e1) {
				return null;
			}

			if (this.resource.getContents().get(0) != null) {
				if (this.resource.getContents().get(0) instanceof SoftwareAssembly) {
					return ModelUtil.getSoftwareAssembly(this.resource);
				} else if (this.resource.getContents().get(0) instanceof DeviceConfiguration) {
					return ModelUtil.getDeviceConfiguration(this.resource);
				} else {
					return null;
				}
			}
		}
		return null;
	}

}
