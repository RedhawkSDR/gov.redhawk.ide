/** 
 * REDHAWK HEADER
 *
 * Identification: $Revision: 9207 $
 */
package gov.redhawk.ide.debug.internal.ui;

import gov.redhawk.ide.debug.ui.LaunchUtil;
import gov.redhawk.ide.debug.ui.ScaDebugUiPlugin;
import gov.redhawk.ui.editor.SCAFormEditor;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.spd.util.SpdResourceImpl;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * 
 */
public class ComponentLaunchShortcut implements ILaunchShortcut {

	/**
	 * {@inheritDoc}
	 */
	public void launch(final ISelection selection, final String mode) {
		if (selection instanceof IStructuredSelection) {
			final IStructuredSelection ss = (IStructuredSelection) selection;
			final Object element = ss.getFirstElement();
			if (element instanceof IFile) {
				final IFile file = (IFile) element;
				if (file.getName().endsWith(SpdPackage.FILE_EXTENSION)) {
					launch(mode, file);
				}
			} else if (element instanceof IProject) {
				final IProject project = (IProject) element;
				final IFile file = project.getFile(new Path(project.getName() + SpdPackage.FILE_EXTENSION));
				if (file.exists()) {
					launch(mode, file);
				}
			}
		}

	}

	/**
	 * @param mode
	 * @param file
	 */
	private void launch(final String mode, final IFile file) {
		final ResourceSet resourceSet = new ResourceSetImpl();
		try {
			final Resource resource = resourceSet.getResource(URI.createPlatformResourceURI(file.getFullPath().toPortableString(), true), true);
			final SoftPkg spd = SoftPkg.Util.getSoftPkg(resource);
			launch(spd, mode);
		} catch (final Exception e) {
			final Status status = new Status(IStatus.ERROR, ScaDebugUiPlugin.PLUGIN_ID, "Failed to load SPD: " + file, e);
			StatusManager.getManager().handle(status, StatusManager.LOG | StatusManager.SHOW);
		}
	}

	private void launch(final SoftPkg spd, final String mode) {
		final Shell shell = Display.getCurrent().getActiveShell();
		try {
			LaunchUtil.launch(spd, mode, shell);
		} catch (final CoreException e) {
			final Status status = new Status(IStatus.ERROR, ScaDebugUiPlugin.PLUGIN_ID, e.getStatus().getMessage(), e.getStatus().getException());
			StatusManager.getManager().handle(status, StatusManager.LOG | StatusManager.SHOW);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void launch(final IEditorPart editor, final String mode) {
		if (editor instanceof SCAFormEditor) {
			final SCAFormEditor formEditor = (SCAFormEditor) editor;
			if (formEditor.getMainResource() instanceof SpdResourceImpl) {
				launch(SoftPkg.Util.getSoftPkg(formEditor.getMainResource()), mode);
			}
		}
	}

}
