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

import gov.redhawk.ide.debug.ui.diagram.LocalScaDiagramPlugin;
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

//	private static final Debug DEBUG = new Debug(LocalScaDiagramPlugin.PLUGIN_ID, "init");

	private final GraphitiDcdModelMap modelMap;
	private final ScaDeviceManager deviceManager;
	private final DeviceConfiguration dcd;

	public GraphitiDcdModelMapInitializerCommand(final GraphitiDcdModelMap modelMap, final DeviceConfiguration dcd, final ScaDeviceManager deviceManager) {
		this.modelMap = modelMap;
		this.deviceManager = deviceManager;
		this.dcd = dcd;
	}

//	public static void initConnection(@NonNull final ScaConnection con, SoftwareAssembly sad, ScaWaveform waveform, GraphitiModelMap modelMap) {
//		final ScaUsesPort uses = con.getPort();
//		final ScaPortContainer container = uses.getPortContainer();
//		if (!(container instanceof ScaComponent)) {
//			// Can only add connections within Components
//			return;
//		}
//		final LocalScaComponent comp = (LocalScaComponent) container;
//
//		// Initialize the connection ID and the USES side of the connection
//		final SadConnectInterface sadCon = SadFactory.eINSTANCE.createSadConnectInterface();
//		final SadUsesPort usesPort = SadFactory.eINSTANCE.createSadUsesPort();
//		final SadComponentInstantiationRef usesCompRef = SadFactory.eINSTANCE.createSadComponentInstantiationRef();
//		usesCompRef.setInstantiation(modelMap.get(comp));
//		usesPort.setComponentInstantiationRef(usesCompRef);
//		usesPort.setUsesIndentifier(uses.getName());
//		sadCon.setUsesPort(usesPort);
//		sadCon.setId(con.getId());
//
//		// Initialize the Target side of the connection
//		boolean foundTarget = false;
//		final UsesConnection conData = con.getData();
//		final org.omg.CORBA.Object target = conData.port;
//		outC: for (final ScaComponent c : waveform.getComponents()) {
//			if (is_equivalent(target, c.getObj())) {
//				final ComponentSupportedInterface csi = PartitioningFactory.eINSTANCE.createComponentSupportedInterface();
//				final SadComponentInstantiationRef ref = SadFactory.eINSTANCE.createSadComponentInstantiationRef();
//				ref.setInstantiation(modelMap.get((LocalScaComponent) c));
//				csi.setComponentInstantiationRef(ref);
//				csi.setSupportedIdentifier(uses.getProfileObj().getRepID());
//				sadCon.setComponentSupportedInterface(csi);
//				foundTarget = true;
//				break outC;
//			} else {
//				for (final ScaPort< ? , ? > cPort : c.getPorts()) {
//					if (cPort instanceof ScaProvidesPort && is_equivalent(target, cPort.getObj())) {
//						final SadProvidesPort sadProvidesPort = SadFactory.eINSTANCE.createSadProvidesPort();
//						final SadComponentInstantiationRef ref = SadFactory.eINSTANCE.createSadComponentInstantiationRef();
//						ref.setInstantiation(modelMap.get((LocalScaComponent) c));
//						sadProvidesPort.setComponentInstantiationRef(ref);
//						sadProvidesPort.setProvidesIdentifier(cPort.getName());
//						sadCon.setProvidesPort(sadProvidesPort);
//						foundTarget = true;
//						break outC;
//					}
//				}
//			}
//		}
//		// We were unable to find the target side of the connection, so ignore it
//		if (foundTarget) {
//			if (sad.getConnections() == null) {
//				sad.setConnections(SadFactory.eINSTANCE.createSadConnections());
//			}
//			sad.getConnections().getConnectInterface().add(sadCon);
//			modelMap.put(con, sadCon);
//		} else {
//			if (GraphitiNodeModelMapInitializerCommand.DEBUG.enabled) {
//				GraphitiNodeModelMapInitializerCommand.DEBUG.trace("Failed to initialize connection " + con.getId());
//			}
//		}
//	}
//
//	private static boolean is_equivalent(final org.omg.CORBA.Object obj1, final org.omg.CORBA.Object obj2) {
//		if (obj1 == null || obj2 == null) {
//			return false;
//		}
//		if (obj1 == obj2) {
//			return true;
//		}
//		try {
//			return ProtectedThreadExecutor.submit(new Callable<Boolean>() {
//
//				@Override
//				public Boolean call() throws Exception {
//					return obj1._is_equivalent(obj2);
//				}
//
//			});
//		} catch (InterruptedException e) {
//			return false;
//		} catch (ExecutionException e) {
//			return false;
//		} catch (TimeoutException e) {
//			return false;
//		}
//
//	}
//
	public static void initDevice(final ScaDevice< ? > device, DeviceConfiguration dcd, GraphitiDcdModelMap modelMap) {
		final SoftPkg spd = (SoftPkg) device.getProfileObj();
		if (spd == null) {
			// For some reason we couldn't find the SPD Abort.
			PluginUtil.logError(LocalScaDiagramPlugin.getDefault(), "Failed to find Soft Pkg for comp: " + device.getIdentifier(), null);
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
//				List<ScaPort< ? , ? >> ports = Collections.emptyList();
//				ports = comp.getPorts();
//				for (final ScaPort< ? , ? > port : ports) {
//					if (port instanceof ScaUsesPort) {
//						final ScaUsesPort uses = (ScaUsesPort) port;
//						List<ScaConnection> connections = Collections.emptyList();
//						connections = uses.getConnections();
//
//						for (final ScaConnection con : connections) {
//							if (con != null) {
//								initConnection(con, sad, waveform, modelMap);
//							}
//						}
//					}
//				}
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
