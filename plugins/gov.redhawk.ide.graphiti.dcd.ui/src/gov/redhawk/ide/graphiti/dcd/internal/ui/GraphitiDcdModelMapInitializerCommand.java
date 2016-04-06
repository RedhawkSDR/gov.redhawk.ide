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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.emf.common.command.AbstractCommand;

import ExtendedCF.UsesConnection;
import gov.redhawk.ide.graphiti.dcd.ui.DCDUIGraphitiPlugin;
import gov.redhawk.model.sca.ScaConnection;
import gov.redhawk.model.sca.ScaDevice;
import gov.redhawk.model.sca.ScaDeviceManager;
import gov.redhawk.model.sca.ScaPort;
import gov.redhawk.model.sca.ScaPortContainer;
import gov.redhawk.model.sca.ScaProvidesPort;
import gov.redhawk.model.sca.ScaUsesPort;
import gov.redhawk.sca.util.Debug;
import gov.redhawk.sca.util.PluginUtil;
import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;
import mil.jpeojtrs.sca.dcd.DcdComponentInstantiationRef;
import mil.jpeojtrs.sca.dcd.DcdComponentPlacement;
import mil.jpeojtrs.sca.dcd.DcdConnectInterface;
import mil.jpeojtrs.sca.dcd.DcdFactory;
import mil.jpeojtrs.sca.dcd.DcdPartitioning;
import mil.jpeojtrs.sca.dcd.DcdProvidesPort;
import mil.jpeojtrs.sca.dcd.DcdUsesPort;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.partitioning.ComponentFile;
import mil.jpeojtrs.sca.partitioning.ComponentFileRef;
import mil.jpeojtrs.sca.partitioning.ComponentSupportedInterface;
import mil.jpeojtrs.sca.partitioning.NamingService;
import mil.jpeojtrs.sca.partitioning.PartitioningFactory;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.ProtectedThreadExecutor;

public class GraphitiDcdModelMapInitializerCommand extends AbstractCommand {
	private static final Debug DEBUG = new Debug(DCDUIGraphitiPlugin.PLUGIN_ID, "init");

	private final GraphitiDcdModelMap modelMap;
	private final ScaDeviceManager deviceManager;
	private final DeviceConfiguration dcd;

	public GraphitiDcdModelMapInitializerCommand(final GraphitiDcdModelMap modelMap, final DeviceConfiguration dcd, final ScaDeviceManager deviceManager) {
		this.modelMap = modelMap;
		this.deviceManager = deviceManager;
		this.dcd = dcd;
	}

	public static void initConnection(final ScaConnection con, DeviceConfiguration dcd, ScaDeviceManager deviceManager, GraphitiDcdModelMap modelMap) {
		final ScaUsesPort uses = con.getPort();
		final ScaPortContainer container = uses.getPortContainer();
		if (!(container instanceof ScaDevice< ? >)) {
			// Can only add connections within Components
			return;
		}
		final ScaDevice< ? > device = (ScaDevice< ? >) container;

		// Initialize the connection ID and the USES side of the connection
		final DcdConnectInterface dcdCon = DcdFactory.eINSTANCE.createDcdConnectInterface();
		final DcdUsesPort usesPort = DcdFactory.eINSTANCE.createDcdUsesPort();
		final DcdComponentInstantiationRef usesCompRef = DcdFactory.eINSTANCE.createDcdComponentInstantiationRef();
		usesCompRef.setInstantiation(modelMap.get(device));
		usesPort.setComponentInstantiationRef(usesCompRef);
		usesPort.setUsesIdentifier(uses.getName());
		dcdCon.setUsesPort(usesPort);
		dcdCon.setId(con.getId());

		// Initialize the Target side of the connection
		boolean foundTarget = false;
		final UsesConnection conData = con.getData();
		final org.omg.CORBA.Object target = conData.port;
		outC: for (final ScaDevice< ? > d : deviceManager.getAllDevices()) {
			if (is_equivalent(target, d.getObj())) {
				final ComponentSupportedInterface csi = PartitioningFactory.eINSTANCE.createComponentSupportedInterface();
				final DcdComponentInstantiationRef ref = DcdFactory.eINSTANCE.createDcdComponentInstantiationRef();
				ref.setInstantiation(modelMap.get((ScaDevice< ? >) d));
				csi.setComponentInstantiationRef(ref);
				csi.setSupportedIdentifier(uses.getProfileObj().getRepID());
				dcdCon.setComponentSupportedInterface(csi);
				foundTarget = true;
				break outC;
			} else {
				for (final ScaPort< ? , ? > cPort : d.getPorts()) {
					if (cPort instanceof ScaProvidesPort && is_equivalent(target, cPort.getObj())) {
						final DcdProvidesPort dcdProvidesPort = DcdFactory.eINSTANCE.createDcdProvidesPort();
						final DcdComponentInstantiationRef ref = DcdFactory.eINSTANCE.createDcdComponentInstantiationRef();
						ref.setInstantiation(modelMap.get((ScaDevice< ? >) d));
						dcdProvidesPort.setComponentInstantiationRef(ref);
						dcdProvidesPort.setProvidesIdentifier(cPort.getName());
						dcdCon.setProvidesPort(dcdProvidesPort);
						foundTarget = true;
						break outC;
					}
				}
			}
		}
		// We were unable to find the target side of the connection, so ignore it
		if (foundTarget) {
			if (dcd.getConnections() == null) {
				dcd.setConnections(DcdFactory.eINSTANCE.createDcdConnections());
			}
			dcd.getConnections().getConnectInterface().add(dcdCon);
			modelMap.put(con, dcdCon);
		} else {
			if (GraphitiDcdModelMapInitializerCommand.DEBUG.enabled) {
				GraphitiDcdModelMapInitializerCommand.DEBUG.trace("Failed to initialize connection " + con.getId());
			}
		}
	}

	private static boolean is_equivalent(final org.omg.CORBA.Object obj1, final org.omg.CORBA.Object obj2) {
		if (obj1 == null || obj2 == null) {
			return false;
		}
		if (obj1 == obj2) {
			return true;
		}
		try {
			return ProtectedThreadExecutor.submit(new Callable<Boolean>() {

				@Override
				public Boolean call() throws Exception {
					return obj1._is_equivalent(obj2);
				}

			});
		} catch (InterruptedException e) {
			return false;
		} catch (ExecutionException e) {
			return false;
		} catch (TimeoutException e) {
			return false;
		}

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

			for (final ScaDevice< ? > device : this.deviceManager.getAllDevices()) {
				if (device == null) {
					continue;
				}
				List<ScaPort< ? , ? >> ports = Collections.emptyList();
				ports = device.getPorts();
				for (final ScaPort< ? , ? > port : ports) {
					if (port instanceof ScaUsesPort) {
						final ScaUsesPort uses = (ScaUsesPort) port;
						List<ScaConnection> connections = Collections.emptyList();
						connections = uses.getConnections();

						for (final ScaConnection con : connections) {
							if (con != null) {
								initConnection(con, dcd, deviceManager, modelMap);
							}
						}
					}
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
