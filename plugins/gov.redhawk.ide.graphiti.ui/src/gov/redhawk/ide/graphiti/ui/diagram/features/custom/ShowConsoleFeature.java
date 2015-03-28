/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.graphiti.ui.diagram.features.custom;

import gov.redhawk.ide.debug.LocalLaunch;
import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.ui.adapters.GraphitiAdapterUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaDevice;
import gov.redhawk.model.sca.ScaDeviceManager;
import gov.redhawk.model.sca.ScaModelPlugin;
import gov.redhawk.model.sca.ScaService;
import gov.redhawk.model.sca.ScaWaveform;

import java.util.Map;

import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;
import mil.jpeojtrs.sca.partitioning.ComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.util.QueryParser;
import mil.jpeojtrs.sca.util.ScaFileSystemConstants;

import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;

@SuppressWarnings("restriction")
public class ShowConsoleFeature extends AbstractCustomFeature {

	public ShowConsoleFeature(IFeatureProvider fp) {
		super(fp);
	}

	public static final String NAME = "Show Console";
	public static final String DESCRIPTION = "Bring up this component's Console View";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		if (context.getPictogramElements()[0] instanceof RHContainerShapeImpl && DUtil.isDiagramLocal(getDiagram())) {
			return true;
		}
		return super.canExecute(context);
	}

	@Override
	public void execute(ICustomContext context) {
		RHContainerShapeImpl shape = (RHContainerShapeImpl) context.getPictogramElements()[0];
		ComponentInstantiation ci = (ComponentInstantiation) DUtil.getBusinessObject(shape);
		LocalLaunch localLaunch = null;

		if (ci != null && ci.eResource() != null) {
			if (ci instanceof SadComponentInstantiation) {
				final URI uri = ci.eResource().getURI();
				final Map<String, String> query = QueryParser.parseQuery(uri.query());
				final String wfRef = query.get(ScaFileSystemConstants.QUERY_PARAM_WF);
				final ScaWaveform waveform = ScaModelPlugin.getDefault().findEObject(ScaWaveform.class, wfRef);
				final String myId = ci.getId();
				if (waveform != null) {
					for (final ScaComponent component : GraphitiAdapterUtil.safeFetchComponents(waveform)) {
						final String scaComponentId = component.identifier();
						if (scaComponentId.startsWith(myId)) {
							if (component instanceof LocalLaunch) {
								localLaunch = (LocalLaunch) component;
							}
						}
					}
				}
			} else if (ci instanceof DcdComponentInstantiation) {
				final URI uri = ci.eResource().getURI();
				final Map<String, String> query = QueryParser.parseQuery(uri.query());
				final String dmRef = query.get(ScaFileSystemConstants.QUERY_PARAM_WF);
				ScaDeviceManager deviceManager = ScaModelPlugin.getDefault().findEObject(ScaDeviceManager.class, dmRef);
				final String myId = ci.getId();
				if (deviceManager != null) {
					for (final ScaDevice< ? > device : GraphitiAdapterUtil.safeFetchComponents(deviceManager)) {
						final String scaDeviceId = device.identifier();
						if (scaDeviceId.startsWith(myId)) {
							if (device instanceof LocalLaunch) {
								localLaunch = (LocalLaunch) device;
								break;
							}
						}
					}
					if (localLaunch == null) {
						for (ScaService service : deviceManager.getServices()) {
							final String scaServiceId = service.getName();
							if (scaServiceId.startsWith(myId)) {
								if (service instanceof LocalLaunch) {
									localLaunch = (LocalLaunch) service;
									break;
								}
							}
						}
					}
				}
			}

			if (localLaunch != null && localLaunch.getLaunch() != null && localLaunch.getLaunch().getProcesses().length > 0) {
				final IConsole console = DebugUIPlugin.getDefault().getProcessConsoleManager().getConsole(localLaunch.getLaunch().getProcesses()[0]);
				final IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();
				consoleManager.showConsoleView(console);
			}
		}
	}

	@Override
	public String getImageId() {
		// IDE-1021: Overridden to return non-null so it will show up in button pad
		return gov.redhawk.ide.graphiti.ui.diagram.providers.ImageProvider.IMG_CONSOLE_VIEW;
	}

	@Override
	public String getDescription() {
		return DESCRIPTION;
	}
}
