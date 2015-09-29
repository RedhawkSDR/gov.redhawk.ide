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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonDropAdapter;
import org.eclipse.ui.navigator.CommonDropAdapterAssistant;

import gov.redhawk.ide.natures.ScaComponentProjectNature;
import gov.redhawk.ide.natures.ScaNodeProjectNature;
import gov.redhawk.ide.natures.ScaWaveformProjectNature;
import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import gov.redhawk.ide.sdr.ui.export.FileStoreExporter;
import gov.redhawk.ide.sdr.ui.export.IScaExporter;
import gov.redhawk.ide.sdr.ui.util.ExportToSdrRootJob;
import gov.redhawk.ide.sdr.ui.util.RefreshSdrJob;
import gov.redhawk.sca.util.PluginUtil;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

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
						if (!proj.hasNature(ScaNodeProjectNature.ID) && !proj.hasNature(ScaComponentProjectNature.ID)
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
		// Verify source(s) and target types
		final Object data = LocalSelectionTransfer.getTransfer().getSelection();
		if (!(data instanceof TreeSelection) || !(aTarget instanceof EObject)) {
			return Status.OK_STATUS;
		}
		if (!(aTarget instanceof SdrRoot) && ScaEcoreUtils.getEContainerOfType((EObject) aTarget, SdrRoot.class) == null) {
			return Status.OK_STATUS;
		}

		// Ensure everything is saved
		if (!PlatformUI.getWorkbench().getActiveWorkbenchWindow().getWorkbench().saveAllEditors(true)) {
			return Status.CANCEL_STATUS;
		}

		// Find all IProject(s) in the drop
		final TreeSelection sel = (TreeSelection) data;
		final List<IProject> projects = new ArrayList<IProject>();
		for (final Object item : sel.toArray()) {
			final IProject proj = PluginUtil.adapt(IProject.class, item);
			if (proj != null && proj.exists() && proj.isOpen()) {
				projects.add(proj);
			}
		}

		// Export, then refresh the SDRROOT
		final IScaExporter exporter = new FileStoreExporter(SdrUiPlugin.getDefault().getTargetSdrPath());
		final ExportToSdrRootJob exportJob = new ExportToSdrRootJob(exporter, projects);
		final RefreshSdrJob refreshJob = new RefreshSdrJob();
		exportJob.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				refreshJob.schedule();
			}
		});
		exportJob.setUser(true);
		exportJob.schedule();

		return Status.OK_STATUS;
	}
}
