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
package gov.redhawk.ide.debug.internal.ui.diagram;

import java.util.ArrayList;
import java.util.List;

import gov.redhawk.ide.debug.LocalScaComponent;
import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaConnection;
import gov.redhawk.model.sca.ScaPort;
import gov.redhawk.model.sca.ScaUsesPort;
import gov.redhawk.sca.util.PluginUtil;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadConnectInterface;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.emf.common.command.AbstractCommand;
import org.eclipse.emf.common.util.EList;

/**
 * 
 */
public class ModelMapInitializerCommand extends AbstractCommand {
	private final ModelMap modelMap;
	private final LocalScaWaveform waveform;
	private final SoftwareAssembly sad;

	public ModelMapInitializerCommand(final ModelMap map, final SoftwareAssembly sad, final LocalScaWaveform waveform) {
		this.modelMap = map;
		this.waveform = waveform;
		this.sad = sad;
	}

	@Override
	public void execute() {
		if (waveform != null) {
			EList<SadComponentInstantiation> allInsts = sad.getAllComponentInstantiations();
			List<ScaComponent> nonMatchedComponents = new ArrayList<ScaComponent>();
			out : for (ScaComponent comp : waveform.getComponents()) {
				boolean found = false;
				for (SadComponentInstantiation inst : allInsts) {
					if (PluginUtil.equals(comp.getInstantiationIdentifier(), inst.getId())) {
						modelMap.put((LocalScaComponent) comp, inst);
						found = true;
						continue out;
					}
				}
				if (!found) {
					nonMatchedComponents.add(comp);
				}
			}

			if (sad.getConnections() != null) {
				out : for (SadConnectInterface con : sad.getConnections().getConnectInterface()) {
					for (ScaComponent comp : waveform.getComponents()) {
						for (ScaPort< ? , ? > port : comp.getPorts()) {
							if (port instanceof ScaUsesPort) {
								ScaUsesPort uses = (ScaUsesPort) port;
								for (ScaConnection scaCon : uses.getConnections()) {
									if (PluginUtil.equals(scaCon.getId(), con.getId())) {
										modelMap.put(scaCon, con);
										continue out;
									}
								}
							}
						}
					}
				}
			}
			
			for (ScaComponent comp : nonMatchedComponents) {
				if (comp instanceof LocalScaComponent) {
					SadModelInitializerCommand.initComponent((LocalScaComponent) comp, sad, modelMap);
				}
			}
			
			for (ScaComponent comp : nonMatchedComponents) {
				if (comp instanceof LocalScaComponent) {
					for (ScaPort<?, ?> port : comp.getPorts()) {
						if (port instanceof ScaUsesPort) {
							for (ScaConnection conn : ((ScaUsesPort) port).getConnections()) {
								SadModelInitializerCommand.initConnection(conn, sad, waveform, modelMap);
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
