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
package gov.redhawk.ide.graphiti.dcd.internal.ui;

import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.emf.common.util.URI;

import CF.DataType;
import gov.redhawk.core.graphiti.ui.editor.AbstractGraphitiMultiPageEditor;
import gov.redhawk.ide.debug.LocalAbstractComponent;
import gov.redhawk.ide.debug.LocalScaDeviceManager;
import gov.redhawk.ide.graphiti.dcd.ui.DCDUIGraphitiPlugin;
import gov.redhawk.ide.graphiti.internal.ui.AbstractGraphitiModelMap;
import gov.redhawk.ide.graphiti.ui.GraphitiUIPlugin;
import gov.redhawk.model.sca.ScaDevice;
import gov.redhawk.model.sca.ScaDeviceManager;
import gov.redhawk.model.sca.ScaService;
import gov.redhawk.sca.util.SubMonitor;
import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;
import mil.jpeojtrs.sca.dcd.impl.DcdComponentInstantiationImpl;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

public class GraphitiDCDLocalModelMap extends GraphitiDcdModelMap {

	public GraphitiDCDLocalModelMap(AbstractGraphitiMultiPageEditor editor, ScaDeviceManager deviceManager) {
		super(editor, deviceManager);
		Assert.isTrue(deviceManager instanceof LocalScaDeviceManager, "This model map only supports local (sandbox) device managers");
	}

	@Override
	public void add(final DcdComponentInstantiation compInst) {
		final DcdNodeMapEntry nodeMapEntry = new DcdNodeMapEntry();
		nodeMapEntry.setProfile(compInst);
		final Map<String, DcdNodeMapEntry> nodes = getNodes();
		synchronized (nodes) {
			if (nodes.get(nodeMapEntry.getKey()) != null) {
				return;
			} else {
				nodes.put(nodeMapEntry.getKey(), nodeMapEntry);
			}
		}

		final String implID = ((DcdComponentInstantiationImpl) compInst).getImplID();
		Job job = new Job("Launching " + compInst.getUsageName()) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				SubMonitor subMonitor = SubMonitor.convert(monitor, "Launching " + compInst.getUsageName(), IProgressMonitor.UNKNOWN);
				LocalAbstractComponent newComp = null;
				try {
					newComp = GraphitiDCDLocalModelMap.this.launch(compInst, implID);
					if (newComp instanceof ScaDevice< ? >) {
						nodeMapEntry.setScaDevice((ScaDevice< ? >) newComp);
					} else if (newComp instanceof ScaService) {
						nodeMapEntry.setScaService((ScaService) newComp);
					}
					updateEnabledState(compInst, true);
					getEditor().refreshSelectedObject(compInst);
					return Status.OK_STATUS;
				} catch (final CoreException e) {
					nodes.remove(nodeMapEntry.getKey());
					return new Status(e.getStatus().getSeverity(), DCDUIGraphitiPlugin.PLUGIN_ID, e.getStatus().getMessage(), e);
				} finally {
					if (nodes.get(nodeMapEntry.getKey()) == null) {
						delete(compInst);
					}
					subMonitor.done();
				}
			}

		};
		job.schedule();
	}

	/**
	 * Launch an instance of the specified SPD.
	 * @param compInst The instantiation as per the DCD
	 * @param implID The implementation to launch, or null for any
	 * @throws CoreException
	 */
	protected LocalAbstractComponent launch(final DcdComponentInstantiation compInst, final String implID) throws CoreException {
		LocalScaDeviceManager localDeviceManager = (LocalScaDeviceManager) getDeviceManager();
		DataType[] initConfiguration = getInitialProperties(compInst);
		final SoftPkg spd = ScaEcoreUtils.getFeature(compInst, AbstractGraphitiModelMap.COMP_INST_TO_SPD_PATH);
		if (spd == null) {
			throw new CoreException(new Status(IStatus.ERROR, GraphitiUIPlugin.PLUGIN_ID, "Failed to resolve SPD.", null));
		}
		final URI spdURI = spd.eResource().getURI();

		return localDeviceManager.launch(compInst.getId(), initConfiguration, spdURI, implID, ILaunchManager.RUN_MODE);
	}
}
