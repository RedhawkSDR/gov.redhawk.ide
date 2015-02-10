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
package gov.redhawk.ide.graphiti.dcd.ui.adapters;

import gov.redhawk.ide.graphiti.dcd.ext.impl.DeviceShapeImpl;
import gov.redhawk.ide.graphiti.ui.adapters.GraphitiAdapterUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaDevice;
import gov.redhawk.model.sca.ScaDeviceManager;
import gov.redhawk.model.sca.ScaModelPlugin;

import java.util.Map;

import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;
import mil.jpeojtrs.sca.util.QueryParser;
import mil.jpeojtrs.sca.util.ScaFileSystemConstants;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

/**
 * 
 */
public class DeviceShapeAdapterFactory implements IAdapterFactory {

	private static final Class< ? >[] LIST = new Class< ? >[] { ScaDevice.class };

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		Object object = adaptableObject;
		if (object instanceof AbstractGraphicalEditPart) {
			object = ((AbstractGraphicalEditPart) object).getModel();
		}
		DcdComponentInstantiation ci = null;
		if (object instanceof DeviceShapeImpl) {
			ci = (DcdComponentInstantiation) DUtil.getBusinessObject((DeviceShapeImpl) object);
		}

		if (ci != null && ci.eResource() != null) {
			if (adapterType.isAssignableFrom(ScaComponent.class)) {
				final URI uri = ci.eResource().getURI();
				final Map<String, String> query = QueryParser.parseQuery(uri.query());
				final String wfRef = query.get(ScaFileSystemConstants.QUERY_PARAM_WF);
				final ScaDeviceManager devMgr = ScaModelPlugin.getDefault().findEObject(ScaDeviceManager.class, wfRef);
				final String myId = ci.getId();
				if (devMgr != null) {
					for (final ScaDevice dev : GraphitiAdapterUtil.safeFetchComponents(devMgr)) {
						final String scaComponentId = dev.getIdentifier();
						if (scaComponentId.startsWith(myId)) {
							return dev;
						}
					}
				}
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
	 */
	@Override
	public Class< ? >[] getAdapterList() {
		return DeviceShapeAdapterFactory.LIST;
	}

}
