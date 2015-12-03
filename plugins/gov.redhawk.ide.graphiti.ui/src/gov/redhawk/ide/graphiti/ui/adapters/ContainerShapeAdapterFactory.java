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
import org.eclipse.graphiti.ui.platform.GraphitiShapeEditPart;

import gov.redhawk.ide.debug.LocalLaunch;
import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.model.sca.ScaAbstractComponent;
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
 * <li>{@link GraphitiShapeEditPart} (Graphiti UI part)</li>
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
	public < T > T getAdapter(Object adaptableObject, Class<T> adapterType) {
		// We convert the Graphiti UI part -> Graphiti model object, if not already done for us
		Object model;
		if (adaptableObject instanceof GraphitiShapeEditPart) {
			model = ((GraphitiShapeEditPart) adaptableObject).getPictogramElement();
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

		ScaAbstractComponent< ? > component = getComponent(ci);
		if (adapterType.isInstance(component)) {
			return adapterType.cast(component);
		} else {
			return null;
		}
	}

	private ScaAbstractComponent< ? > getComponent(ComponentInstantiation componentInstantiation) {
		final String instantiationId = componentInstantiation.getId();
		final URI uri = componentInstantiation.eResource().getURI();
		final Map<String, String> query = QueryParser.parseQuery(uri.query());
		final String ref = query.get(ScaFileSystemConstants.QUERY_PARAM_WF);

		if (componentInstantiation instanceof SadComponentInstantiation) {
			final ScaWaveform waveform = ScaModelPlugin.getDefault().findEObject(ScaWaveform.class, ref);
			return GraphitiAdapterUtil.safeFetchComponent(waveform, instantiationId);
		} else if (componentInstantiation instanceof DcdComponentInstantiation) {
			final ScaDeviceManager devMgr = ScaModelPlugin.getDefault().findEObject(ScaDeviceManager.class, ref);
			if (devMgr != null) {
				try {
					return ScaModelCommand.runExclusive(devMgr, new RunnableWithResult.Impl<ScaDevice<?>>() {
						@Override
						public void run() {
							setResult(devMgr.getDevice(instantiationId));
						}
					});
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
