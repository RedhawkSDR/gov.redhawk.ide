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
import gov.redhawk.model.sca.ScaModelPlugin;
import gov.redhawk.model.sca.ScaPort;
import gov.redhawk.model.sca.ScaProvidesPort;
import gov.redhawk.model.sca.ScaUsesPort;
import gov.redhawk.model.sca.ScaWaveform;

import java.util.Map;

import mil.jpeojtrs.sca.partitioning.ComponentInstantiation;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.util.QueryParser;
import mil.jpeojtrs.sca.util.ScaFileSystemConstants;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.ui.internal.parts.AdvancedAnchorEditPart;

@SuppressWarnings({ "restriction" })
public class PortEditPartAdapterFactory implements IAdapterFactory {

	private static final Class< ? >[] LIST = new Class< ? >[] { ScaProvidesPort.class, ScaUsesPort.class, ScaPort.class };

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {

		if (adaptableObject instanceof AdvancedAnchorEditPart) {
			Object portObject = null;

			EObject object = (EObject) ((AdvancedAnchorEditPart) adaptableObject).getModel();
			
			// Disallow context menu options for super ports
			if (DUtil.isSuperPort((ContainerShape)object.eContainer())) {
				return null;
			}
			
			if (object instanceof Anchor) {
				portObject = DUtil.getBusinessObject((Anchor) object);
			}

			if (portObject instanceof UsesPortStub) {
				UsesPortStub uses = (UsesPortStub) portObject;

				if (uses.eResource() == null || !(uses.eContainer() instanceof ComponentInstantiation)) {
					return null;
				}

				if (ScaPort.class.isAssignableFrom(adapterType)) {
					final URI uri = uses.eResource().getURI();
					final Map<String, String> query = QueryParser.parseQuery(uri.query());
					final String wfRef = query.get(ScaFileSystemConstants.QUERY_PARAM_WF);
					final ScaWaveform waveform = ScaModelPlugin.getDefault().findEObject(ScaWaveform.class, wfRef);
					final String myId = ((ComponentInstantiation) uses.eContainer()).getId();
					for (final ScaComponent component : GraphitiAdapterUtil.safeFetchComponents(waveform)) {
						final String scaComponentId = component.identifier();
						if (scaComponentId.startsWith(myId)) {
							for (final ScaPort< ? , ? > port : GraphitiAdapterUtil.safeFetchPorts(component)) {
								if (port != null && (port instanceof ScaUsesPort)) {
									final String name = port.getName();
									if (name != null && name.equals(uses.getName())) {
										return port;
									}

								}
							}
						}
					}
				}
			}

			if (portObject instanceof ProvidesPortStub) {
				ProvidesPortStub provides = (ProvidesPortStub) portObject;
				if (provides == null || provides.eResource() == null || !(provides.eContainer() instanceof ComponentInstantiation)) {
					return null;
				}
				if (ScaPort.class.isAssignableFrom(adapterType)) {
					final URI uri = provides.eResource().getURI();
					final Map<String, String> query = QueryParser.parseQuery(uri.query());
					final String wfRef = query.get(ScaFileSystemConstants.QUERY_PARAM_WF);
					final ScaWaveform waveform = ScaModelPlugin.getDefault().findEObject(ScaWaveform.class, wfRef);
					final String myId = ((ComponentInstantiation) provides.eContainer()).getId();
					final String providesName = provides.getName();
					for (final ScaComponent component : GraphitiAdapterUtil.safeFetchComponents(waveform)) {
						final String scaComponentId = component.identifier();
						if (scaComponentId != null && scaComponentId.startsWith(myId)) {
							for (final ScaPort< ? , ? > port : GraphitiAdapterUtil.safeFetchPorts(component)) {
								if (port != null && (port instanceof ScaProvidesPort)) {
									final String name = port.getName();
									if (name != null && name.equals(providesName)) {
										return port;
									}
								}
							}
						}
					}
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
