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
package gov.redhawk.ide.graphiti.sad.ui.internal.modelmap;

import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jdt.annotation.NonNull;

import CF.DataType;
import gov.redhawk.core.graphiti.sad.ui.modelmap.GraphitiSADModelMap;
import gov.redhawk.core.graphiti.sad.ui.modelmap.SADNodeMapEntry;
import gov.redhawk.core.graphiti.ui.editor.AbstractGraphitiMultiPageEditor;
import gov.redhawk.core.graphiti.ui.modelmap.AbstractGraphitiModelMap;
import gov.redhawk.ide.debug.LocalScaComponent;
import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.graphiti.sad.ui.SADUIGraphitiPlugin;
import gov.redhawk.ide.graphiti.ui.GraphitiUIPlugin;
import gov.redhawk.model.sca.ScaWaveform;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

public class GraphitiSADLocalModelMap extends GraphitiSADModelMap {

	public GraphitiSADLocalModelMap(AbstractGraphitiMultiPageEditor editor, ScaWaveform waveform) {
		super(editor, waveform);
		Assert.isTrue(waveform instanceof LocalScaWaveform, "This model map only supports local (sandbox) waveforms");
	}

	@Override
	public void add(@NonNull final SadComponentInstantiation compInst) {
		final SADNodeMapEntry nodeMapEntry = new SADNodeMapEntry();
		nodeMapEntry.setProfile(compInst);
		final Map<String, SADNodeMapEntry> nodes = getNodes();
		synchronized (nodes) {
			if (nodes.get(nodeMapEntry.getKey()) != null) {
				return;
			} else {
				nodes.put(nodeMapEntry.getKey(), nodeMapEntry);
			}
		}

		final String implID = ((SadComponentInstantiation) compInst).getImplID();
		Job job = Job.create("Launching " + compInst.getUsageName(), monitor -> {
			SubMonitor progress = SubMonitor.convert(monitor, "Launching " + compInst.getUsageName(), 100);
			LocalScaComponent newComp = null;
			try {
				newComp = GraphitiSADLocalModelMap.this.launch(compInst, implID, progress.newChild(90));
				nodeMapEntry.setScaComponent(newComp);

				updateEnabledState(compInst, true);
				progress.worked(5);

				getEditor().refreshSelectedObject(compInst);
				progress.worked(5);

				return Status.OK_STATUS;
			} catch (final CoreException e) {
				nodes.remove(nodeMapEntry.getKey());
				return new Status(e.getStatus().getSeverity(), SADUIGraphitiPlugin.PLUGIN_ID, e.getStatus().getMessage(), e);
			} finally {
				if (nodes.get(nodeMapEntry.getKey()) == null) {
					delete(compInst);
				}
				progress.done();
			}
		});
		job.schedule();
	}

	/**
	 * Launch an instance of the specified SPD.
	 * @param compInst The instantiation as per the SAD
	 * @param implID The implementation to launch, or null for any
	 * @param monitor The progress monitor
	 * @throws CoreException
	 */
	protected LocalScaComponent launch(final SadComponentInstantiation compInst, final String implID, IProgressMonitor monitor) throws CoreException {
		LocalScaWaveform localWaveform = (LocalScaWaveform) getWaveform();
		DataType[] initConfiguration = getInitialProperties(compInst);
		final SoftPkg spd = ScaEcoreUtils.getFeature(compInst, AbstractGraphitiModelMap.COMP_INST_TO_SPD_PATH);
		if (spd == null) {
			throw new CoreException(new Status(IStatus.ERROR, GraphitiUIPlugin.PLUGIN_ID, "Failed to resolve SPD.", null));
		}
		final URI spdURI = spd.eResource().getURI();

		return localWaveform.launch(compInst.getId(), initConfiguration, spdURI, implID, ILaunchManager.RUN_MODE, monitor);
	}
}
