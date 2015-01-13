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
package gov.redhawk.ide.graphiti.dcd.ui.diagram.feature.custom.runtime;

import gov.redhawk.ide.debug.LocalLaunch;
import gov.redhawk.ide.debug.LocalScaDeviceManager;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.SpdLauncherUtil;
import gov.redhawk.ide.graphiti.dcd.ext.DeviceShape;
import gov.redhawk.ide.graphiti.dcd.ext.ServiceShape;
import gov.redhawk.ide.graphiti.dcd.ui.diagram.patterns.DevicePattern;
import gov.redhawk.ide.graphiti.dcd.ui.diagram.patterns.ServicePattern;
import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.model.sca.ScaDevice;
import gov.redhawk.model.sca.ScaService;
import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.impl.RemoveContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;

public class TerminateShapeFeature extends AbstractCustomFeature {

	public TerminateShapeFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return "Terminate";
	}

	@Override
	public String getDescription() {
		return "Hard terminate of the component from the model";
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		if (context.getPictogramElements()[0] instanceof DeviceShape || context.getPictogramElements()[0] instanceof ServiceShape) {
			return true;
		}
		return false;
	}

	@Override
	public void execute(ICustomContext context) {
		// Manually terminate the component process
		final RHContainerShapeImpl shape = (RHContainerShapeImpl) context.getPictogramElements()[0];
		DcdComponentInstantiation ci = (DcdComponentInstantiation) DUtil.getBusinessObject(shape);
		LocalLaunch localLaunch = null;
		if (ci != null) {
			LocalScaDeviceManager deviceManager = ScaDebugPlugin.getInstance().getLocalSca().getSandboxDeviceManager();
			final String ciId = ci.getId();
			if (deviceManager != null && shape instanceof DeviceShape) {
				for (ScaDevice< ? > device : deviceManager.getAllDevices()) {
					final String deviceId = device.identifier();
					if (deviceId.startsWith(ciId)) {
						if (device instanceof LocalLaunch) {
							localLaunch = (LocalLaunch) device;
						}
					}
				}
			} else if (deviceManager != null && shape instanceof ServiceShape) {
				for (ScaService service : deviceManager.getServices()) {
					// TODO: What is the primary identifier for a service?
					final String deviceId = service.getName();
					if (deviceId.startsWith(ciId)) {
						if (service instanceof LocalLaunch) {
							localLaunch = (LocalLaunch) service;
						}
					}
				}
			}

			if (localLaunch != null && localLaunch.getLaunch() != null && localLaunch.getLaunch().getProcesses().length > 0) {
				SpdLauncherUtil.terminate(localLaunch);
			}
		}

		// We need to remove the component from the sad.xml
		final DeviceConfiguration dcd = DUtil.getDiagramDCD(getDiagram());
		if (shape instanceof DeviceShape) {
			DevicePattern.deleteComponentInstantiation(ci, dcd);
		} else {
			ServicePattern.deleteComponentInstantiation(ci, dcd);
		}

		// We need to manually remove the graphical representation of the component
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {

			@Override
			protected void doExecute() {
				IRemoveContext rc = new RemoveContext(shape);
				IFeatureProvider featureProvider = getFeatureProvider();
				IRemoveFeature removeFeature = featureProvider.getRemoveFeature(rc);
				if (removeFeature != null) {
					removeFeature.remove(rc);
				}
			}
		});
	}

	@Override
	public String getImageId() {
		// Decided not to include this feature in the component button pad, but
		// leaving code in place in case it becomes relevant later
//		return gov.redhawk.ide.graphiti.ui.diagram.providers.ImageProvider.IMG_TERMINATE;
		return null;
	}
}
