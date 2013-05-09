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
package gov.redhawk.ide.sdr.internal.ui;

import gov.redhawk.ide.RedhawkIdeActivator;
import gov.redhawk.ide.natures.ScaComponentProjectNature;
import gov.redhawk.ide.natures.ScaNodeProjectNature;
import gov.redhawk.ide.natures.ScaWaveformProjectNature;
import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import gov.redhawk.ide.sdr.ui.export.ExportUtils;
import gov.redhawk.ide.sdr.ui.export.FileStoreExporter;
import gov.redhawk.ide.sdr.ui.export.IScaExporter;
import gov.redhawk.ide.sdr.ui.util.RefreshSdrJob;
import gov.redhawk.sca.util.PluginUtil;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonDropAdapter;
import org.eclipse.ui.navigator.CommonDropAdapterAssistant;

public class SdrRootDropAdapterAssistant extends CommonDropAdapterAssistant {

	public SdrRootDropAdapterAssistant() {
	}

	@Override
	public IStatus validateDrop(final Object target, final int operation, final TransferData transferType) {
		if (LocalSelectionTransfer.getTransfer().isSupportedType(transferType)) {
			// If you use nativeToJava() here instead of getSelection(), you will get 
			// "Received wrong transfer data." messages in your error log.
			// This is because on some platforms, the transfer information isn't available before the drop.
			final Object data = LocalSelectionTransfer.getTransfer().getSelection();
			if (data instanceof TreeSelection) {
				final TreeSelection sel = (TreeSelection) data;
				for (final Object item : sel.toArray()) {
					final IProject proj = PluginUtil.adapt(IProject.class, item);
					if (proj == null) {
						return new Status(IStatus.CANCEL, SdrUiPlugin.PLUGIN_ID, "Only projects can be exported");
					}
					try {
						if (!proj.hasNature(ScaNodeProjectNature.ID) 
							&& !proj.hasNature(ScaComponentProjectNature.ID)
							&& !proj.hasNature(ScaWaveformProjectNature.ID)) {
							return new Status(IStatus.CANCEL, SdrUiPlugin.PLUGIN_ID, "Project is not an SCA Waveform, Node, Component, or Device");
						}
					} catch (CoreException e) {
						return new Status(IStatus.CANCEL, SdrUiPlugin.PLUGIN_ID, "Drop is not supported transfer type");
					}
				}
				return Status.OK_STATUS;
			}
		}
		return new Status(IStatus.CANCEL, SdrUiPlugin.PLUGIN_ID, "Drop is not supported transfer type");
	}

	@Override
	public IStatus handleDrop(final CommonDropAdapter aDropAdapter, final DropTargetEvent aDropTargetEvent, final Object aTarget) {
		final MultiStatus status = new MultiStatus(SdrUiPlugin.PLUGIN_ID, IStatus.OK, "", null);
		final Object data = LocalSelectionTransfer.getTransfer().getSelection();
		if (data instanceof TreeSelection) {
			final TreeSelection sel = (TreeSelection) data;
			for (final Object item : sel.toArray()) {
				final IProject proj = PluginUtil.adapt(IProject.class, item);
				if (proj != null) {
					if (aTarget instanceof SdrRoot) {
						status.add(handleProjectDrop((SdrRoot) aTarget, proj));
					} else if (aTarget instanceof EObject) {
						status.add(handleProjectDrop(((SdrRoot) ((EObject) aTarget).eContainer()), proj));
					}
				}
			}
		}

		return status;
	}

	private IStatus handleProjectDrop(final SdrRoot root, final IProject proj) {
		if (!PlatformUI.getWorkbench().getActiveWorkbenchWindow().getWorkbench().saveAllEditors(true)) {
			return Status.OK_STATUS;
		}
		if (!proj.isOpen()) {
			return new Status(IStatus.ERROR, SdrUiPlugin.PLUGIN_ID, "Cannot export a closed project.");
		}

		final WorkspaceJob job = new WorkspaceJob("Exporting " + proj) {

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
				final int FETCH_INFO_WORK = 1;
				final int TO_LOCAL_FILE_WORK = 1;
				final int EXPORT_WORK = 98;
				final SubMonitor progress = SubMonitor.convert(monitor, FETCH_INFO_WORK + TO_LOCAL_FILE_WORK + EXPORT_WORK);

				try {
					IPath sdrPath = SdrUiPlugin.getDefault().getTargetSdrPath();
					if (sdrPath == null) {
						throw new CoreException(new Status(Status.ERROR, SdrUiPlugin.PLUGIN_ID, "The SDR root is undefined. Check the SDRROOT environment variable and your preference settings.", null));
					}
					final URI scaRoot = URI.createFileURI(sdrPath.toPortableString());
					final IFileStore store = EFS.getStore(new java.net.URI(scaRoot.toString()));

					if (!store.fetchInfo(EFS.NONE, progress.newChild(FETCH_INFO_WORK)).exists()) {
						return new Status(IStatus.ERROR, SdrUiPlugin.PLUGIN_ID,
						        "The defined SDR root path does not exist.  Check the SDRROOT environment variable and your preference settings.");
					}

					// Currently we only support local target SDR roots, although in the future
					// the SDR root
					final File destDir = store.toLocalFile(EFS.NONE, progress.newChild(TO_LOCAL_FILE_WORK));
					if (destDir == null) {
						return new Status(IStatus.ERROR, SdrUiPlugin.PLUGIN_ID, "Exporting is only supported for local SDR");
					}
					if (destDir.isFile()) {
						return new Status(IStatus.ERROR, SdrUiPlugin.PLUGIN_ID, "An existing file " + destDir + "is in the way of the export");
					}
					if (!destDir.exists()) {
						destDir.mkdirs();
					}
					IScaExporter exporter = null;

					// TODO This should be here!!
					if (proj.hasNature("gov.redhawk.ide.idl.natures.idllibrary")) {
						exporter = new FileStoreExporter(RedhawkIdeActivator.getDefault().getRuntimePath());
					} else {
						exporter = new FileStoreExporter(new Path(destDir.toString()));
					}

					// The order of checking natures is important because
					// nodes and devices also are components
					if (proj.hasNature(ScaNodeProjectNature.ID)) {
						ExportUtils.exportNode(proj, exporter, progress.newChild(EXPORT_WORK));
					} else if (proj.hasNature(ScaComponentProjectNature.ID)) {
						ExportUtils.exportComponent(proj, exporter, progress.newChild(EXPORT_WORK));
					} else if (proj.hasNature(ScaWaveformProjectNature.ID)) {
						ExportUtils.exportWaveform(proj, exporter, progress.newChild(EXPORT_WORK));
					}

					exporter.finished();

					final RefreshSdrJob refreshJob = new RefreshSdrJob(root);
					refreshJob.schedule();

				} catch (final CoreException e) {
					return new Status(IStatus.ERROR, SdrUiPlugin.PLUGIN_ID, "Error exporting project to SDR root", e);
				} catch (final URISyntaxException e) {
					return new Status(IStatus.ERROR, SdrUiPlugin.PLUGIN_ID, "Error exporting project to SDR root", e);
				} catch (final IOException e) {
					return new Status(IStatus.ERROR, SdrUiPlugin.PLUGIN_ID, "Error exporting project to SDR root", e);
				} finally {
					monitor.done();
				}

				return Status.OK_STATUS;
			}
		};

		job.setUser(true);
		job.setRule(proj);
		job.setPriority(Job.LONG);
		job.schedule();

		return Status.OK_STATUS;
	}
}
