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
package gov.redhawk.ide.codegen.ui.internal;

import java.io.IOException;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.ResourceUtil;

import gov.redhawk.ide.codegen.ui.RedhawkCodegenUiActivator;
import mil.jpeojtrs.sca.prf.Properties;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.spd.SoftPkg;

public class SaveXmlUtils {

	private SaveXmlUtils() {
	}

	/**
	 * Save SPD, PRF and SCD files to disk. Any file may optionally be null. If from an editor, this method assumes
	 * they are all from the <b>same</b> editor. If the model objects come from an open editor, that editor's save
	 * functionality will be invoked (possibly requiring user interaction). Otherwise the files will be saved directly
	 * to disk.
	 * @param spd The SPD file (can be null)
	 * @param prf The PRF file (can be null)
	 * @param scd The SCD file (can be null)
	 * @throws CoreException
	 */
	public static void save(final SoftPkg spd, final Properties prf, final SoftwareComponent scd) throws CoreException {
		// Our model object may / most likely belongs to an editor
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();

		// Look for an SCD editor
		boolean scdSaved = false;
		if (scd != null) {
			scdSaved = saveViaEditor(scd, page, workspaceRoot);
		}

		// Look for a PRF editor
		boolean prfSaved = false;
		if (prf != null) {
			prfSaved = saveViaEditor(prf, page, workspaceRoot);
		}

		// Look for an SPD editor (this may save the PRF & SCD)
		boolean spdSaved = false;
		if (spd != null) {
			spdSaved = saveViaEditor(spd, page, workspaceRoot);
			prfSaved = prfSaved || (prf != null && spdSaved);
			scdSaved = scdSaved || (scd != null && spdSaved);
		}

		// If we were unable to save via editor, save the resource(s) directly
		if (spd != null && !spdSaved) {
			saveDirectly(spd);
		}
		if (prf != null && !prfSaved) {
			saveDirectly(prf);
		}
		if (scd != null && !scdSaved) {
			saveDirectly(scd);
		}
	}

	/**
	 * See {@link #save(SoftPkg, Properties, SoftwareComponent)} for description. This method is intended to be generic
	 * for use in saving either a SAD or DCD file.
	 * @param eObject The SAD or DCD file
	 * @throws CoreException
	 */
	public static void save(final EObject eObject) throws CoreException {
		// Our model object may / most likely belongs to an editor
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();

		// Try to invoke the editor's save functionality
		if (!saveViaEditor(eObject, page, workspaceRoot)) {
			// Otherwise just save directly to disk
			saveDirectly(eObject);
		}
	}

	private static boolean saveViaEditor(final EObject eObject, IWorkbenchPage page, IWorkspaceRoot workspaceRoot) {
		String workspaceRelativeStr = eObject.eResource().getURI().toPlatformString(false);
		IPath workspaceRelativePath = Path.fromPortableString(workspaceRelativeStr);
		if (workspaceRelativePath != null) {
			IEditorPart editorPart = ResourceUtil.findEditor(page, workspaceRoot.getFile(workspaceRelativePath));
			if (editorPart != null) {
				if (editorPart.isDirty()) {
					editorPart.doSave(new NullProgressMonitor());
				}
				return true;
			}
		}
		return false;
	}

	private static void saveDirectly(EObject eObject) throws CoreException {
		try {
			eObject.eResource().save(null);
		} catch (IOException e) {
			String errorMsg = Messages.bind(Messages.Error_CantSave, eObject.eResource().getURI().lastSegment());
			throw new CoreException(new Status(IStatus.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID, errorMsg, e));
		}
	}
}
