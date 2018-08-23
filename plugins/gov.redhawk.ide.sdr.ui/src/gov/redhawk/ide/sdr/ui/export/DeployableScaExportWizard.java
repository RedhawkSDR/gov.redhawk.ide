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
package gov.redhawk.ide.sdr.ui.export;

import gov.redhawk.ide.natures.ScaComponentProjectNature;
import gov.redhawk.ide.natures.ScaNodeProjectNature;
import gov.redhawk.ide.natures.ScaProjectNature;
import gov.redhawk.ide.natures.ScaWaveformProjectNature;
import gov.redhawk.ide.sdr.TargetSdrRoot;
import gov.redhawk.ide.sdr.preferences.IdeSdrPreferences;
import gov.redhawk.ide.ui.RedhawkIDEUiPlugin;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.set.WritableSet;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * @since 3.1
 */
public class DeployableScaExportWizard extends Wizard implements IExportWizard {

	private DeployableScaExportWizardPage exportPage = null;
	@SuppressWarnings("unused")
	private IStructuredSelection selection;

	protected class DeployableScaExportWizardModel {

		// CHECKSTYLE:OFF Keep these public so it's easy to connect databindings
		public IObservableValue<Boolean> directoryExport = new WritableValue<Boolean>(false, Boolean.class);
		// CHECKSTYLE:ON

		// CHECKSTYLE:OFF Keep these public so it's easy to connect databindings
		public IObservableValue<String> directoryDestination = new WritableValue<String>("", String.class);
		// CHECKSTYLE:ON

		// CHECKSTYLE:OFF Keep these public so it's easy to connect databindings
		public IObservableValue<Boolean> archiveExport = new WritableValue<Boolean>(false, Boolean.class);
		// CHECKSTYLE:ON

		// CHECKSTYLE:OFF Keep these public so it's easy to connect databindings
		public IObservableValue<String> archiveDestination = new WritableValue<String>("", String.class);
		// CHECKSTYLE:ON

		// CHECKSTYLE:OFF Keep these public so it's easy to connect databindings
		public IObservableSet<IProject> projectsToExport = new WritableSet<IProject>(new ArrayList<IProject>(), IProject[].class);
		// CHECKSTYLE:ON
	}

	/** The model. */
	private DeployableScaExportWizardModel model;

	private IScaExporter exporter;

	public DeployableScaExportWizard() {
		this.setWindowTitle("Export");
	}

	/**
	 * Gets the model.
	 * 
	 * @return the model
	 */
	protected DeployableScaExportWizardModel getModel() {
		return this.model;
	}

	@Override
	public boolean needsProgressMonitor() {
		return true;
	}

	@Override
	public void addPages() {
		this.exportPage = new DeployableScaExportWizardPage();
		this.addPage(this.exportPage);
	}

	@Override
	public boolean performFinish() {

		if (!PlatformUI.getWorkbench().getActiveWorkbenchWindow().getWorkbench().saveAllEditors(true)) {
			return true;
		}

		final Object[] toExport = this.model.projectsToExport.toArray();
		if (this.model.directoryExport.getValue()) {
			final IPath destDir = new Path(this.model.directoryDestination.getValue());
			if (destDir.toFile().isFile()) {
				return false;
			}
			if (!destDir.toFile().exists()) {
				destDir.toFile().mkdirs();
			}
			this.exporter = new FileStoreExporter(destDir);
		} else if (this.model.archiveExport.getValue()) {
			try {
				IPath zippath = new Path(this.model.archiveDestination.getValue());
				if (zippath.getFileExtension() == null) {
					zippath = zippath.addFileExtension("zip");
				}
				this.exporter = new ZipExporter(zippath);
			} catch (final IOException e) {
				RedhawkIDEUiPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, RedhawkIDEUiPlugin.PLUGIN_ID, "Unexpected error exporting projects", e));
				return true;
			}
		}

		try {

			getContainer().run(true, true, new IRunnableWithProgress() {

				@Override
				public void run(final IProgressMonitor m) throws InvocationTargetException, InterruptedException {
					final SubMonitor subMonitor = SubMonitor.convert(m, "Exporting...", 5);
					for (final Object obj : toExport) {
						final IProject proj = (IProject) obj;
						// The order of checking natures is important because
						// nodes and devices also are components
						try {
							if (proj.hasNature(ScaNodeProjectNature.ID)) {
								ExportUtils.exportNode(proj, DeployableScaExportWizard.this.exporter, subMonitor.newChild(1));
							} else if (proj.hasNature(ScaComponentProjectNature.ID)) {
								ExportUtils.exportComponent(proj, DeployableScaExportWizard.this.exporter, subMonitor.newChild(1));
							} else if (proj.hasNature(ScaWaveformProjectNature.ID)) {
								ExportUtils.exportWaveform(proj, DeployableScaExportWizard.this.exporter, subMonitor.newChild(1));
							}
						} catch (final CoreException e) {
							throw new InvocationTargetException(e);
						} catch (final IOException e) {
							throw new InvocationTargetException(e);
						}
					}

					try {
						DeployableScaExportWizard.this.exporter.finished();
						if (DeployableScaExportWizard.this.exporter.getExportLocation().equals(IdeSdrPreferences.getTargetSdrPath())) {
							TargetSdrRoot.getSdrRoot().reload(subMonitor.newChild(1));
						}

					} catch (final IOException e) {
						throw new InvocationTargetException(e);
					}
				}

			});
		} catch (final InvocationTargetException e) {
			final IStatus status = new Status(IStatus.ERROR, RedhawkIDEUiPlugin.PLUGIN_ID, "Error exporting SCA projects", e.getCause());
			RedhawkIDEUiPlugin.getDefault().getLog().log(status);
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {

					ErrorDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Export error",
					        "There was an error exporting the REDHAWK projects", status);
				}

			});
		} catch (final InterruptedException e) {
			final IStatus status = new Status(IStatus.ERROR, RedhawkIDEUiPlugin.PLUGIN_ID, "Exporting REDHAWK projects interrupted", e);
			RedhawkIDEUiPlugin.getDefault().getLog().log(status);
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {
					ErrorDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Export error",
					        "There was an error exporting the REDHAWK projects", status);
				}

			});
		}

		return true;
	}

	@Override
	public boolean canFinish() {
		if (this.model.projectsToExport.size() > 0) {
			return true;
		}
		return false;
	}

	@Override
	public void init(final IWorkbench workbench, final IStructuredSelection initialSelection) {
		this.model = new DeployableScaExportWizardModel();
		this.model.directoryDestination.setValue(IdeSdrPreferences.getTargetSdrPath().toOSString());
		this.model.directoryExport.setValue(Boolean.TRUE);

		if (initialSelection != null) {
			for (final Object item : initialSelection.toArray()) {
				if (item instanceof IProject) {
					final IProject proj = (IProject) item;
					try {
						if (proj.hasNature(ScaProjectNature.ID)) {
							this.model.projectsToExport.add(proj);
						}
					} catch (final CoreException e) {
						RedhawkIDEUiPlugin.getDefault().getLog()
						        .log(new Status(IStatus.ERROR, RedhawkIDEUiPlugin.PLUGIN_ID, "Unexpected error loading projects", e));
					}
				}
			}
		}
		this.selection = initialSelection;
	}

}
