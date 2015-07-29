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
package gov.redhawk.ide.graphiti.ui.adapters;

import java.util.Map;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import gov.redhawk.ide.debug.LocalLaunch;
import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaDevice;
import gov.redhawk.model.sca.ScaDeviceManager;
import gov.redhawk.model.sca.ScaModelPlugin;
import gov.redhawk.model.sca.ScaWaveform;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;
import mil.jpeojtrs.sca.partitioning.ComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.util.QueryParser;
import mil.jpeojtrs.sca.util.ScaFileSystemConstants;

/**
 * Can adapt either:
 * <ul>
 * <li>{@link AbstractGraphicalEditPart} (Graphiti UI part)</li>
 * <li>{@link RHContainerShape} (our Graphiti model object)</li>
 * </ul>
 * from the diagrams to the following types (and a few of their super types):
 * <ul>
 * <li>{@link ScaComponent}</li>
 * <li>{@link ScaDevice}</li>
 * <li>{@link LocalLaunch}</li>
 * </ul>
 */
public class ContainerShapeAdapterFactory implements IAdapterFactory {

	private static final Class< ? >[] ADAPTER_TYPES = new Class< ? >[] { ScaComponent.class, ScaDevice.class, LocalLaunch.class };

	@Override
	@SuppressWarnings("unchecked")
	public < T > T getAdapter(Object adaptableObject, Class<T> adapterType) {
		// We convert the Graphiti UI part -> Graphiti model object, if not already done for us
		Object model;
		if (adaptableObject instanceof AbstractGraphicalEditPart) {
			model = ((AbstractGraphicalEditPart) adaptableObject).getModel();
		} else {
			model = adaptableObject;
		}
		if (!(model instanceof RHContainerShape)) {
			return null;
		}

		// Go from the Graphiti model object to the Redhawk model object
		// The object must be a ComponentInstantiation (super-type of instantiations in both SAD & DCD)
		Object redhawkModelObj = DUtil.getBusinessObject((RHContainerShape) model);
		if (!(redhawkModelObj instanceof ComponentInstantiation)) {
			return null;
		}
		ComponentInstantiation ci = (ComponentInstantiation) redhawkModelObj;
		if (ci.eResource() == null) {
			return null;
		}

		final String myId = ci.getId();
		final URI uri = ci.eResource().getURI();
		final Map<String, String> query = QueryParser.parseQuery(uri.query());
		final String ref = query.get(ScaFileSystemConstants.QUERY_PARAM_WF);

		// Check the type of the instantiation as well as the adapter type to determine the conversion
		if (ci instanceof SadComponentInstantiation) {
			final ScaWaveform waveform = ScaModelPlugin.getDefault().findEObject(ScaWaveform.class, ref);
			if (waveform != null) {
				for (final ScaComponent component : GraphitiAdapterUtil.safeFetchComponents(waveform)) {
					final String scaComponentId = component.identifier();
					if (scaComponentId.startsWith(myId)) {
						if (adapterType.isAssignableFrom(ScaComponent.class)
							|| (adapterType.isAssignableFrom(LocalLaunch.class) && component instanceof LocalLaunch)) {
							return (T) component;
						} else {
							return null;
						}
					}
				}
			}
		} else if (ci instanceof DcdComponentInstantiation) {
			final ScaDeviceManager devMgr = ScaModelPlugin.getDefault().findEObject(ScaDeviceManager.class, ref);
			if (devMgr != null) {
				try {
					ScaDevice<?> device = ScaModelCommand.runExclusive(devMgr, new RunnableWithResult.Impl<ScaDevice<?>>() {
						@Override
						public void run() {
							setResult(devMgr.getDevice(myId));
						}
					});
					if (adapterType.isAssignableFrom(ScaDevice.class) || (adapterType.isAssignableFrom(LocalLaunch.class) && device instanceof LocalLaunch)) {
						return (T) device;
					} else {
						return null;
					}
				} catch (InterruptedException e) {
					return null;
				}
			}
		}

		return null;
	}

	@Override
	public Class< ? >[] getAdapterList() {
		return ADAPTER_TYPES;
	}

}
