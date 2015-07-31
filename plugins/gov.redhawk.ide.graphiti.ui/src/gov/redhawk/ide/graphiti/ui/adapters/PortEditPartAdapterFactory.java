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
package gov.redhawk.ide.graphiti.ui.adapters;

import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaDevice;
import gov.redhawk.model.sca.ScaDeviceManager;
import gov.redhawk.model.sca.ScaModelPlugin;
import gov.redhawk.model.sca.ScaPort;
import gov.redhawk.model.sca.ScaProvidesPort;
import gov.redhawk.model.sca.ScaUsesPort;
import gov.redhawk.model.sca.ScaWaveform;

import java.util.Map;

import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;
import mil.jpeojtrs.sca.partitioning.ComponentInstantiation;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.scd.AbstractPort;
import mil.jpeojtrs.sca.util.QueryParser;
import mil.jpeojtrs.sca.util.ScaFileSystemConstants;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class PortEditPartAdapterFactory implements IAdapterFactory {

	private static final Class< ? >[] LIST = new Class< ? >[] { ScaProvidesPort.class, ScaUsesPort.class, ScaPort.class };

	@Override
	public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
		if (AbstractPort.class.isAssignableFrom(adapterType) || ScaPort.class.isAssignableFrom(adapterType)) {
			EObject object = (EObject) ((EditPart) adaptableObject).getModel();

			// Disallow context menu options for super ports
			if (DUtil.isSuperPort((ContainerShape) object.eContainer())) {
				return null;
			}

			Object port = DUtil.getBusinessObject((PictogramElement) object);
			ScaPort< ? , ? > scaPort = null;
			if (port instanceof UsesPortStub) {
				UsesPortStub uses = (UsesPortStub) port;
				scaPort = getScaPort(uses, uses.getName());
			} else if (port instanceof ProvidesPortStub) {
				ProvidesPortStub provides = (ProvidesPortStub) port;
				scaPort = getScaPort(provides, provides.getName());
			}

			if (scaPort != null && AbstractPort.class.isAssignableFrom(adapterType)) {
				return adapterType.cast(scaPort.getProfileObj());
			} else {
				return adapterType.cast(scaPort);
			}
		}
		return null;
	}

	private ScaPort< ? , ? > getScaPort(EObject port, String name) {
		if (port.eResource() == null || !(port.eContainer() instanceof ComponentInstantiation)) {
			return null;
		}

		final URI uri = port.eResource().getURI();
		final Map<String, String> query = QueryParser.parseQuery(uri.query());
		final String wfRef = query.get(ScaFileSystemConstants.QUERY_PARAM_WF);

		if (port.eContainer() instanceof SadComponentInstantiation) {
			final ScaWaveform waveform = ScaModelPlugin.getDefault().findEObject(ScaWaveform.class, wfRef);
			if (waveform != null) {
				final String instantiationId = ((ComponentInstantiation) port.eContainer()).getId();
				final ScaComponent component = GraphitiAdapterUtil.safeFetchComponent(waveform, instantiationId);
				if (component != null) {
					return GraphitiAdapterUtil.safeFetchPort(component, name);
				}
			}
		} else if (port.eContainer() instanceof DcdComponentInstantiation) {
			final ScaDeviceManager devMgr = ScaModelPlugin.getDefault().findEObject(ScaDeviceManager.class, wfRef);
			if (devMgr != null) {
				final String deviceId = ((ComponentInstantiation) port.eContainer()).getId();
				final ScaDevice< ? > device = GraphitiAdapterUtil.safeFetchDevice(devMgr, deviceId);
				if (device != null) {
					return GraphitiAdapterUtil.safeFetchPort(device, name);
				}
			}
		}
		return null;
	}

	@Override
	public Class< ? >[] getAdapterList() {
		return PortEditPartAdapterFactory.LIST;
	}

}
