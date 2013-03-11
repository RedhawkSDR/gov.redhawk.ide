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
package gov.redhawk.ide.spd.internal.ui.handlers;

import gov.redhawk.ide.spd.ui.ComponentUiPlugin;
import gov.redhawk.ide.spd.ui.wizard.RecreateSettingsWizard;

import java.io.IOException;

import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

public class RecreateSettingsHandler extends AbstractHandler implements IHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		final ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getSelectionService().getSelection();

		if (selection instanceof IStructuredSelection) {
			for (final Object obj : ((IStructuredSelection) selection).toArray()) {
				if (obj instanceof IFile) {
					final IFile file = (IFile) obj;
					final ResourceSet set = new ResourceSetImpl();
					final Resource tempRes = set.getResource(URI.createPlatformResourceURI(file.getFullPath().toString(), true), true);

					// Load the resource and get the root object
					try {
						tempRes.load(null);
					} catch (final IOException e) {
						return new Status(IStatus.ERROR, ComponentUiPlugin.PLUGIN_ID, "Failed to recreate settings for " + file.getName(), e);
					}
					final EObject eObj = tempRes.getEObject("/");

					// Get the SPD
					if (eObj instanceof SoftPkg) {
						final SoftPkg spd = (SoftPkg) eObj;
						final RecreateSettingsWizard wizard = new RecreateSettingsWizard(spd);
						final WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
						if (dialog.open() == Window.OK) {
							// PASS
						}
						// TODO Launch the wizard
					} else {
						continue;
					}
				}
			}
		}
		return null;
	}

}
