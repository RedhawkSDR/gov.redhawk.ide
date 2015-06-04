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
package gov.redhawk.ide.graphiti.sad.ui.adapters;

import gov.redhawk.ide.graphiti.sad.ext.impl.ComponentShapeImpl;
import gov.redhawk.ide.graphiti.ui.adapters.GraphitiAdapterUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaModelPlugin;
import gov.redhawk.model.sca.ScaWaveform;

import java.util.Map;

import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.util.QueryParser;
import mil.jpeojtrs.sca.util.ScaFileSystemConstants;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

/**
 * An adapter from {@link AbstractGraphicalEditPart} to the corresponding {@link ScaComponent}.
 */
public class ComponentShapeAdapterFactory implements IAdapterFactory {
	private static final Class< ? >[] LIST = new Class< ? >[] { ScaComponent.class };

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		// Get the model object for the edit part
		if (!(adaptableObject instanceof AbstractGraphicalEditPart)) {
			return null;
		}
		Object modelObject = ((AbstractGraphicalEditPart) adaptableObject).getModel();

		// Get the business object for the model object
		if (!(modelObject instanceof ComponentShapeImpl)) {
			return null;
		}
		SadComponentInstantiation ci = (SadComponentInstantiation) DUtil.getBusinessObject((ComponentShapeImpl) modelObject);

		if (ci == null || ci.eResource() == null) {
			return null;
		}

		if (adapterType.isAssignableFrom(ScaComponent.class)) {
			final URI uri = ci.eResource().getURI();
			final Map<String, String> query = QueryParser.parseQuery(uri.query());
			final String wfRef = query.get(ScaFileSystemConstants.QUERY_PARAM_WF);
			final ScaWaveform waveform = ScaModelPlugin.getDefault().findEObject(ScaWaveform.class, wfRef);
			final String myId = ci.getId();
			if (waveform != null) {
				for (final ScaComponent component : GraphitiAdapterUtil.safeFetchComponents(waveform)) {
					final String scaComponentId = component.identifier();
					if (scaComponentId.startsWith(myId)) {
						return component;
					}
				}
			}
		}

		return null;
	}

	@Override
	public Class< ? >[] getAdapterList() {
		return ComponentShapeAdapterFactory.LIST;
	}
}
