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
import gov.redhawk.model.sca.ScaPort;
import gov.redhawk.model.sca.ScaPortContainer;
import gov.redhawk.model.sca.ScaProvidesPort;
import gov.redhawk.model.sca.ScaUsesPort;

import mil.jpeojtrs.sca.partitioning.ComponentInstantiation;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.scd.AbstractPort;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;

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

			PictogramElement pe = (PictogramElement) object;
			EObject port = DUtil.getBusinessObject(pe);
			String portName;
			if (port instanceof UsesPortStub) {
				portName = ((UsesPortStub) port).getName();
			} else if (port instanceof ProvidesPortStub) {
				portName = ((ProvidesPortStub) port).getName();
			} else {
				return null;
			}

			Diagram diagram = Graphiti.getPeService().getDiagramForPictogramElement(pe);
			ScaPort< ? , ? > scaPort = getScaPort(diagram, port, portName);
			if (scaPort != null && AbstractPort.class.isAssignableFrom(adapterType)) {
				return adapterType.cast(scaPort.getProfileObj());
			} else {
				return adapterType.cast(scaPort);
			}
		}
		return null;
	}

	private ScaPort< ? , ? > getScaPort(Diagram diagram, EObject port, String name) {
		if (!(port.eContainer() instanceof ComponentInstantiation)) {
			return null;
		}

		ScaPortContainer component = GraphitiAdapterUtil.safeFetchResource(diagram, (ComponentInstantiation) port.eContainer());
		if (component != null) {
			return GraphitiAdapterUtil.safeFetchPort(component, name);
		}
		return null;
	}

	@Override
	public Class< ? >[] getAdapterList() {
		return PortEditPartAdapterFactory.LIST;
	}

}
