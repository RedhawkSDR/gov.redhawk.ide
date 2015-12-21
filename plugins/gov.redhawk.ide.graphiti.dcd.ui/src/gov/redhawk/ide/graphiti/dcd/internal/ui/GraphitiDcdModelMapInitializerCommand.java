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
package gov.redhawk.ide.graphiti.dcd.internal.ui;

import gov.redhawk.ide.graphiti.dcd.ui.DCDUIGraphitiPlugin;
import gov.redhawk.model.sca.ScaDevice;
import gov.redhawk.model.sca.ScaDeviceManager;
import gov.redhawk.sca.util.PluginUtil;
import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;
import mil.jpeojtrs.sca.dcd.DcdComponentPlacement;
import mil.jpeojtrs.sca.dcd.DcdFactory;
import mil.jpeojtrs.sca.dcd.DcdPartitioning;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.partitioning.ComponentFile;
import mil.jpeojtrs.sca.partitioning.ComponentFileRef;
import mil.jpeojtrs.sca.partitioning.NamingService;
import mil.jpeojtrs.sca.partitioning.PartitioningFactory;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.emf.common.command.AbstractCommand;

public class GraphitiDcdModelMapInitializerCommand extends AbstractCommand {

	private final GraphitiDcdModelMap modelMap;
	private final ScaDeviceManager deviceManager;
	private final DeviceConfiguration dcd;

	public GraphitiDcdModelMapInitializerCommand(final GraphitiDcdModelMap modelMap, final DeviceConfiguration dcd, final ScaDeviceManager deviceManager) {
		this.modelMap = modelMap;
		this.deviceManager = deviceManager;
		this.dcd = dcd;
	}

	public static void initDevice(final ScaDevice< ? > device, DeviceConfiguration dcd, GraphitiDcdModelMap modelMap) {
		final SoftPkg spd = (SoftPkg) device.getProfileObj();
		if (spd == null) {
			// For some reason we couldn't find the SPD Abort.
			PluginUtil.logError(DCDUIGraphitiPlugin.getDefault(), "Failed to find profile object (SPD) for device: " + device.getIdentifier(), null);
			return;
		}

		ComponentFile spdFile = null;
		for (final ComponentFile file : dcd.getComponentFiles().getComponentFile()) {
			if (file.getSoftPkg() != null) {
				if (PluginUtil.equals(file.getSoftPkg().getId(), spd.getId())) {
					spdFile = file;
					break;
				}
			}
		}
		
		if (spdFile == null) {
			spdFile = DcdFactory.eINSTANCE.createComponentFile();
			spdFile.setSoftPkg(spd);
			dcd.getComponentFiles().getComponentFile().add(spdFile);
		}

		final DcdPartitioning partitioning = dcd.getPartitioning();

		final DcdComponentPlacement placement = DcdFactory.eINSTANCE.createDcdComponentPlacement();
		partitioning.getComponentPlacement().add(placement);

		final DcdComponentInstantiation inst = DcdFactory.eINSTANCE.createDcdComponentInstantiation();
		if (inst == null) {
			return;
		}
		inst.setUsageName(device.getLabel());
		final NamingService namingService = PartitioningFactory.eINSTANCE.createNamingService();
		namingService.setName(device.getLabel());
		inst.setId(device.getIdentifier());
		inst.setImplID(device.getIdentifier());

		final ComponentFileRef fileRef = PartitioningFactory.eINSTANCE.createComponentFileRef();
		fileRef.setFile(spdFile);

		inst.setPlacement(placement);
		placement.setComponentFileRef(fileRef);

		modelMap.put(device, inst);
	}

	@Override
	public void execute() {
		this.dcd.setComponentFiles(PartitioningFactory.eINSTANCE.createComponentFiles());
		this.dcd.setPartitioning(DcdFactory.eINSTANCE.createDcdPartitioning());
		this.dcd.setConnections(DcdFactory.eINSTANCE.createDcdConnections());

		if (deviceManager != null) {
			for (ScaDevice< ? > device : this.deviceManager.getAllDevices()) {
				if (device != null) {
					initDevice((ScaDevice< ? >) device, dcd, modelMap);
				}
			}

			for (final ScaDevice < ? > device : this.deviceManager.getAllDevices()) {
				if (device == null) {
					continue;
				}
			}
		}
	}

	@Override
	protected boolean prepare() {
		return true;
	}

	@Override
	public boolean canUndo() {
		return false;
	}

	@Override
	public void redo() {
		throw new UnsupportedOperationException();
	}
}
