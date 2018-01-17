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

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import gov.redhawk.ide.sdr.ComponentsContainer;
import gov.redhawk.ide.sdr.DevicesContainer;
import gov.redhawk.ide.sdr.NodesContainer;
import gov.redhawk.ide.sdr.ServicesContainer;
import gov.redhawk.ide.sdr.SharedLibrariesContainer;
import gov.redhawk.ide.sdr.SoftPkgRegistry;
import gov.redhawk.ide.sdr.WaveformsContainer;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.spd.SoftPkg;

public class DeleteHandler extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (!(selection instanceof IStructuredSelection)) {
			return null;
		}
		final IStructuredSelection structuredSelection = (IStructuredSelection) selection;

		Map<String, URI> deleteMap = new HashMap<String, URI>();
		for (final Object obj : structuredSelection.toArray()) {
			add(obj, deleteMap);
		}
		if (deleteMap.size() == 0) {
			return null;
		}

		Job deleteJob = createDeleteJob(deleteMap, event);
		if (deleteJob != null) {
			deleteJob.schedule();
		}

		return null;
	}

	private void add(Object obj, Map<String, URI> deleteMap) {
		if (obj instanceof SoftPkg) {
			SoftPkg spd = (SoftPkg) obj;
			deleteMap.put(spd.getName(), spd.eResource().getURI());
		} else if (obj instanceof SoftwareAssembly) {
			SoftwareAssembly sad = (SoftwareAssembly) obj;
			deleteMap.put(sad.getName(), sad.eResource().getURI());
		} else if (obj instanceof DeviceConfiguration) {
			DeviceConfiguration dcd = (DeviceConfiguration) obj;
			deleteMap.put(dcd.getName(), dcd.eResource().getURI());
		} else if (obj instanceof SoftPkgRegistry) {
			SoftPkgRegistry registry = (SoftPkgRegistry) obj;
			for (SoftPkg spd : registry.getComponents()) {
				add(spd, deleteMap);
			}
			if (obj instanceof ComponentsContainer) {
				ComponentsContainer container = (ComponentsContainer) obj;
				for (ComponentsContainer childContainer : container.getChildContainers()) {
					add(childContainer, deleteMap);
				}
			} else if (obj instanceof DevicesContainer) {
				DevicesContainer container = (DevicesContainer) obj;
				for (DevicesContainer childContainer : container.getChildContainers()) {
					add(childContainer, deleteMap);
				}
			} else if (obj instanceof ServicesContainer) {
				ServicesContainer container = (ServicesContainer) obj;
				for (ServicesContainer childContainer : container.getChildContainers()) {
					add(childContainer, deleteMap);
				}
			} else if (obj instanceof SharedLibrariesContainer) {
				SharedLibrariesContainer container = (SharedLibrariesContainer) obj;
				for (SharedLibrariesContainer childContainer : container.getChildContainers()) {
					add(childContainer, deleteMap);
				}
			}
		} else if (obj instanceof WaveformsContainer) {
			WaveformsContainer container = (WaveformsContainer) obj;
			for (SoftwareAssembly sad : container.getWaveforms()) {
				add(sad, deleteMap);
			}
			for (WaveformsContainer childContainer : container.getChildContainers()) {
				add(childContainer, deleteMap);
			}
		} else if (obj instanceof NodesContainer) {
			NodesContainer container = (NodesContainer) obj;
			for (DeviceConfiguration sad : container.getNodes()) {
				add(sad, deleteMap);
			}
			for (NodesContainer childContainer : container.getChildContainers()) {
				add(childContainer, deleteMap);
			}
		}
	}

	private Job createDeleteJob(Map<String, URI> deleteMap, ExecutionEvent event) {
		String deleteDescription;
		if (deleteMap.size() > 5) {
			deleteDescription = String.format("%d resources", deleteMap.size());
		} else {
			deleteDescription = deleteMap.keySet().stream().collect(Collectors.joining(", "));
		}

		String deleteQuestion = String.format("Are you sure you want to delete %s?", deleteDescription);
		if (!MessageDialog.openQuestion(HandlerUtil.getActiveShell(event), "Delete", deleteQuestion)) {
			return null;
		}

		final Job job = Job.create("Deleting: " + deleteDescription, monitor -> {
			try {
				for (URI uri : deleteMap.values()) {
					final IFileStore efsStore = EFS.getStore(java.net.URI.create(uri.toString()));
					final IFileStore parent = efsStore.getParent();
					parent.delete(EFS.NONE, monitor);
				}
			} catch (final CoreException e) {
				return new Status(e.getStatus().getSeverity(), SdrUiPlugin.PLUGIN_ID, "Failed to delete: " + deleteDescription, e);
			}
			SdrUiPlugin.getDefault().scheduleSdrRootRefresh();
			return Status.OK_STATUS;
		});
		job.setUser(false);
		return job;
	}
}
