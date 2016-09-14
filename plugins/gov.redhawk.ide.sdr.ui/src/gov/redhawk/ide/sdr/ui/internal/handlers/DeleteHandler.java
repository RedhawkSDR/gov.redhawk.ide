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
package gov.redhawk.ide.sdr.ui.internal.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import gov.redhawk.ide.sdr.ComponentsSubContainer;
import gov.redhawk.ide.sdr.NodesSubContainer;
import gov.redhawk.ide.sdr.WaveformsSubContainer;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.spd.SoftPkg;

public class DeleteHandler extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final ISelection selection = HandlerUtil.getCurrentSelection(event);
		Map<String, URI> deleteMap = new HashMap<String, URI>();

		if (selection instanceof IStructuredSelection) {
			final IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			for (final Object obj : structuredSelection.toArray()) {
				if (obj instanceof SoftPkg) {
					SoftPkg softPkg = (SoftPkg) obj;
					deleteMap.put(softPkg.getName(), softPkg.eResource().getURI());
				} else if (obj instanceof SoftwareAssembly) {
					SoftwareAssembly sad = (SoftwareAssembly) obj;
					deleteMap.put(sad.getName(), sad.eResource().getURI());
				} else if (obj instanceof DeviceConfiguration) {
					DeviceConfiguration dcd = (DeviceConfiguration) obj;
					deleteMap.put(dcd.getName(), dcd.eResource().getURI());
				} else if (obj instanceof ComponentsSubContainer) {
					recursiveAddToDeleteMap((ComponentsSubContainer) obj, deleteMap);
				} else if (obj instanceof WaveformsSubContainer) {
					recursiveAddToDeleteMap((WaveformsSubContainer) obj, deleteMap);
				} else if (obj instanceof NodesSubContainer) {
					recursiveAddToDeleteMap((NodesSubContainer) obj, deleteMap);
				}
			}
			Job deleteJob = createDeleteJob(deleteMap, event);
			if (deleteJob != null) {
				deleteJob.schedule();
			}
		}
		return null;
	}

	private void recursiveAddToDeleteMap(ComponentsSubContainer container, Map<String, URI> deleteMap) {
		for (ComponentsSubContainer subContainer : container.getSubContainers()) {
			recursiveAddToDeleteMap(subContainer, deleteMap);
		}
		for (SoftPkg softPkg : container.getComponents()) {
			deleteMap.put(softPkg.getName(), softPkg.eResource().getURI());
		}
	}

	private void recursiveAddToDeleteMap(WaveformsSubContainer container, Map<String, URI> deleteMap) {
		for (WaveformsSubContainer subContainer : container.getSubContainers()) {
			recursiveAddToDeleteMap(subContainer, deleteMap);
		}
		for (SoftwareAssembly sad : container.getWaveforms()) {
			deleteMap.put(sad.getName(), sad.eResource().getURI());
		}
	}

	private void recursiveAddToDeleteMap(NodesSubContainer container, Map<String, URI> deleteMap) {
		for (NodesSubContainer subContainer : container.getSubContainers()) {
			recursiveAddToDeleteMap(subContainer, deleteMap);
		}
		for (DeviceConfiguration dcd : container.getNodes()) {
			deleteMap.put(dcd.getName(), dcd.eResource().getURI());
		}
	}

	private Job createDeleteJob(Map<String, URI> deleteMap, ExecutionEvent event) {
		String objectNames = "";
		final List<URI> objectURIs = new ArrayList<URI>();
		Iterator<Entry<String, URI>> it = deleteMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, URI> entry = (Map.Entry<String, URI>) it.next();
			if (!it.hasNext()) {
				objectNames += entry.getKey();
			} else {
				objectNames += entry.getKey() + "\n";
			}
			objectURIs.add(entry.getValue());
		}
		final String deleteMessageString = objectNames.trim();

		if (!MessageDialog.openQuestion(HandlerUtil.getActiveShell(event), "Delete",
			"Are you sure you want to delete the following:\n" + deleteMessageString)) {
			return null;
		}

		final Job job = new Job("Deleting " + deleteMessageString) {
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				try {
					for (URI uri : objectURIs) {
						final IFileStore efsStore = EFS.getStore(java.net.URI.create(uri.toString()));
						final IFileStore parent = efsStore.getParent();
						parent.delete(EFS.NONE, monitor);
					}
				} catch (final CoreException e) {
					return new Status(e.getStatus().getSeverity(), SdrUiPlugin.PLUGIN_ID, "Failed to delete " + deleteMessageString, e);
				}
				SdrUiPlugin.getDefault().scheduleSdrRootRefresh();
				return Status.OK_STATUS;
			}
		};
		job.setUser(false);
		return job;
	}
}
