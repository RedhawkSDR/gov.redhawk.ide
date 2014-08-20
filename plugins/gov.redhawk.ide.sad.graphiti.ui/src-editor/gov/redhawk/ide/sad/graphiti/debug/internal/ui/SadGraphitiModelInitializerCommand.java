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
package gov.redhawk.ide.sad.graphiti.debug.internal.ui;

import gov.redhawk.ide.debug.LocalScaComponent;
import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.sad.graphiti.ui.SADUIGraphitiPlugin;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaConnection;
import gov.redhawk.model.sca.ScaPort;
import gov.redhawk.model.sca.ScaPortContainer;
import gov.redhawk.model.sca.ScaProvidesPort;
import gov.redhawk.model.sca.ScaUsesPort;
import gov.redhawk.sca.util.PluginUtil;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import mil.jpeojtrs.sca.partitioning.ComponentFile;
import mil.jpeojtrs.sca.partitioning.ComponentFileRef;
import mil.jpeojtrs.sca.partitioning.ComponentSupportedInterface;
import mil.jpeojtrs.sca.partitioning.NamingService;
import mil.jpeojtrs.sca.partitioning.PartitioningFactory;
import mil.jpeojtrs.sca.sad.FindComponent;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadComponentInstantiationRef;
import mil.jpeojtrs.sca.sad.SadComponentPlacement;
import mil.jpeojtrs.sca.sad.SadConnectInterface;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SadPartitioning;
import mil.jpeojtrs.sca.sad.SadProvidesPort;
import mil.jpeojtrs.sca.sad.SadUsesPort;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.ProtectedThreadExecutor;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.command.AbstractCommand;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jdt.annotation.NonNull;

import ExtendedCF.UsesConnection;

/**
 * 
 */
public class SadGraphitiModelInitializerCommand extends AbstractCommand {

	private final GraphitiModelMap modelMap;
	private final LocalScaWaveform waveform;
	private final SoftwareAssembly sad;

	public SadGraphitiModelInitializerCommand(final GraphitiModelMap map, final SoftwareAssembly sad, final LocalScaWaveform waveform) {
		this.modelMap = map;
		this.waveform = waveform;
		this.sad = sad;
	}

	private void initConnection(@NonNull final ScaConnection con) {
		final ScaUsesPort uses = con.getPort();
		final ScaPortContainer container = uses.getPortContainer();
		if (!(container instanceof ScaComponent)) {
			// Can only add connections within Components
			return;
		}
		final LocalScaComponent comp = (LocalScaComponent) container;

		// Initialize the connection ID and the USES side of the connection
		final SadConnectInterface sadCon = SadFactory.eINSTANCE.createSadConnectInterface();
		final SadUsesPort usesPort = SadFactory.eINSTANCE.createSadUsesPort();
		final SadComponentInstantiationRef usesCompRef = SadFactory.eINSTANCE.createSadComponentInstantiationRef();
		usesCompRef.setInstantiation(this.modelMap.get(comp));
		usesPort.setComponentInstantiationRef(usesCompRef);
		usesPort.setUsesIndentifier(uses.getName());
		sadCon.setUsesPort(usesPort);
		sadCon.setId(con.getId());

		// Initialize the Target side of the connection
		boolean foundTarget = false;
		final UsesConnection conData = con.getData();
		final org.omg.CORBA.Object target = conData.port;
		outC: for (final ScaComponent c : this.waveform.getComponents()) {
			if (target._is_equivalent(c.getObj())) {
				final ComponentSupportedInterface csi = PartitioningFactory.eINSTANCE.createComponentSupportedInterface();
				final SadComponentInstantiationRef ref = SadFactory.eINSTANCE.createSadComponentInstantiationRef();
				ref.setInstantiation(this.modelMap.get((LocalScaComponent) c));
				csi.setComponentInstantiationRef(ref);
				csi.setSupportedIdentifier(uses.getProfileObj().getRepID());
				sadCon.setComponentSupportedInterface(csi);
				foundTarget = true;
				break outC;
			} else {
				for (final ScaPort< ? , ? > cPort : c.fetchPorts(null)) {
					if (cPort instanceof ScaProvidesPort && target._is_equivalent(cPort.getObj())) {
						final SadProvidesPort sadProvidesPort = SadFactory.eINSTANCE.createSadProvidesPort();
						final SadComponentInstantiationRef ref = SadFactory.eINSTANCE.createSadComponentInstantiationRef();
						ref.setInstantiation(this.modelMap.get((LocalScaComponent) c));
						sadProvidesPort.setComponentInstantiationRef(ref);
						sadProvidesPort.setProvidesIdentifier(cPort.getName());
						sadCon.setProvidesPort(sadProvidesPort);
						foundTarget = true;
						break outC;
					}
				}
			}
		}
		// We were unable to find the target side of the connection, so ignore it
		if (foundTarget) {
			if (this.sad.getConnections() == null) {
				this.sad.setConnections(SadFactory.eINSTANCE.createSadConnections());
			}
			this.sad.getConnections().getConnectInterface().add(sadCon);
			if (con == null) {
				// Should be dead code, but outC block above apparently messes up null analysis
				return;
			}
			this.modelMap.put(con, sadCon);
		}
	}

	private void initComponent(@NonNull final LocalScaComponent comp) {
		final SoftPkg spd = comp.fetchProfileObject(null);
		if (spd == null) {
			// For some reason we couldn't find the SPD Abort.
			PluginUtil.logError(SADUIGraphitiPlugin.getDefault(), "Failed to find Soft Pkg for comp: " + comp.getInstantiationIdentifier(), null);
			return;
		}

		ComponentFile spdFile = null;
		for (final ComponentFile file : this.sad.getComponentFiles().getComponentFile()) {
			if (file.getSoftPkg() != null) {
				if (PluginUtil.equals(file.getSoftPkg().getId(), spd.getId())) {
					spdFile = file;
					break;
				}
			}
		}
		if (spdFile == null) {
			spdFile = SadFactory.eINSTANCE.createComponentFile();
			spdFile.setSoftPkg(spd);
			this.sad.getComponentFiles().getComponentFile().add(spdFile);
		}

		final SadPartitioning partitioning = this.sad.getPartitioning();

		final SadComponentPlacement placement = SadFactory.eINSTANCE.createSadComponentPlacement();
		partitioning.getComponentPlacement().add(placement);

		final SadComponentInstantiation inst = SadFactory.eINSTANCE.createSadComponentInstantiation();
		if (inst == null) {
			return;
		}
		inst.setUsageName(comp.getName());
		final FindComponent findComponent = SadFactory.eINSTANCE.createFindComponent();
		final NamingService namingService = PartitioningFactory.eINSTANCE.createNamingService();
		namingService.setName(comp.getName());
		findComponent.setNamingService(namingService);
		inst.setFindComponent(findComponent);
		inst.setId(comp.getName());

		final ComponentFileRef fileRef = PartitioningFactory.eINSTANCE.createComponentFileRef();
		fileRef.setFile(spdFile);

		inst.setPlacement(placement);
		placement.setComponentFileRef(fileRef);

		this.modelMap.put(comp, inst);
	}

	@Override
	public void execute() {
		this.sad.setComponentFiles(PartitioningFactory.eINSTANCE.createComponentFiles());
		this.sad.setPartitioning(SadFactory.eINSTANCE.createSadPartitioning());
		this.sad.setConnections(SadFactory.eINSTANCE.createSadConnections());
		this.sad.setAssemblyController(null);
		this.sad.setExternalPorts(null);
		if (waveform != null) {
			for (final ScaComponent comp : this.waveform.getComponents()) {
				if (comp != null) {
					initComponent((LocalScaComponent) comp);
				}
			}

			for (final ScaComponent comp : this.waveform.getComponents()) {
				if (comp == null) {
					continue;
				}
				List<ScaPort< ? , ? >> ports = Collections.emptyList();
				try {
					ports = ProtectedThreadExecutor.submit(new Callable<EList<ScaPort< ? , ? >>>() {

						@Override
						public EList<ScaPort< ? , ? >> call() throws Exception {
							return comp.fetchPorts(null);
						}

					});
				} catch (InterruptedException e) {
					SADUIGraphitiPlugin.getDefault().getLog().log(
						new Status(IStatus.ERROR, SADUIGraphitiPlugin.PLUGIN_ID, "Failed to fetch ports to initialize diagram.", e));
				} catch (ExecutionException e) {
					SADUIGraphitiPlugin.getDefault().getLog().log(
						new Status(IStatus.ERROR, SADUIGraphitiPlugin.PLUGIN_ID, "Failed to fetch ports to initialize diagram.", e));
				} catch (TimeoutException e) {
					SADUIGraphitiPlugin.getDefault().getLog().log(
						new Status(IStatus.ERROR, SADUIGraphitiPlugin.PLUGIN_ID, "Failed to fetch ports to initialize diagram.", e));
				}
				for (final ScaPort< ? , ? > port : ports) {
					if (port instanceof ScaUsesPort) {
						final ScaUsesPort uses = (ScaUsesPort) port;
						List<ScaConnection> connections = Collections.emptyList();
						try {
							connections = ProtectedThreadExecutor.submit(new Callable<List<ScaConnection>>() {

								@Override
								public List<ScaConnection> call() throws Exception {
									return uses.fetchConnections(null);
								}
							});
						} catch (InterruptedException e) {
							SADUIGraphitiPlugin.getDefault().getLog().log(
								new Status(IStatus.ERROR, SADUIGraphitiPlugin.PLUGIN_ID, "Failed to fetch connections to initialize diagram.", e));
						} catch (ExecutionException e) {
							SADUIGraphitiPlugin.getDefault().getLog().log(
								new Status(IStatus.ERROR, SADUIGraphitiPlugin.PLUGIN_ID, "Failed to fetch connections to initialize diagram.", e));
						} catch (TimeoutException e) {
							SADUIGraphitiPlugin.getDefault().getLog().log(
								new Status(IStatus.ERROR, SADUIGraphitiPlugin.PLUGIN_ID, "Failed to fetch connections to initialize diagram.", e));
						}

						for (final ScaConnection con : connections) {
							if (con != null) {
								initConnection(con);
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
