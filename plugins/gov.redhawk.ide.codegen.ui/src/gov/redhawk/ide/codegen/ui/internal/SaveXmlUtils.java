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
	 * Saves SPD, PRF and SCD files to disk. If possible, an open editor will be located for the file(s) and saving
	 * will be done via the UI. This method makes the assumption that the objects are related (i.e. the PRF or SCD, if
	 * provided, belong to the SPD).
	 * @param spd The SPD file
	 * @param prf The PRF file (can be null)
	 * @param scd The SCD file (can be null)
	 * @throws CoreException
	 */
	public static void save(final SoftPkg spd, final Properties prf, final SoftwareComponent scd) throws CoreException {
		if (spd == null) {
			throw new IllegalArgumentException("SPD cannot be null");
		}

		// Our model object may / most likely belongs to an editor
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();

		// Look for an SCD editor
		boolean saved = false;
		if (scd != null) {
			saved = saveViaEditor(scd, page, workspaceRoot);
		}

		// Look for a PRF editor
		if (!saved && prf != null) {
			saved = saveViaEditor(prf, page, workspaceRoot);
		}

		// Look for an SPD editor (this may save the PRF & SCD)
		if (!saved) {
			saved = saveViaEditor(spd, page, workspaceRoot);
		}

		// If we were unable to save via editor, save the resource(s) directly
		if (!saved) {
			if (spd != null) {
				saveDirectly(spd);
			}
			if (prf != null) {
				saveDirectly(prf);
			}
			if (scd != null) {
				saveDirectly(scd);
			}
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

	/**
	 * @param eObject The object will be translated to a file, and the open editor for that file will be located
	 * @param page
	 * @param workspaceRoot
	 * @return
	 */
	private static boolean saveViaEditor(final EObject eObject, IWorkbenchPage page, IWorkspaceRoot workspaceRoot) {
		String workspaceRelativeStr = eObject.eResource().getURI().toPlatformString(false);
		IPath workspaceRelativePath = Path.fromPortableString(workspaceRelativeStr);
		if (workspaceRelativePath != null) {
			IEditorPart editorPart = ResourceUtil.findEditor(page, workspaceRoot.getFile(workspaceRelativePath));
			if (editorPart != null && editorPart.isDirty()) {
				editorPart.doSave(new NullProgressMonitor());
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
