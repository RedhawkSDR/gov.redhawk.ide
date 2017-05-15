/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.graphiti.dcd.ui.diagram.providers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import gov.redhawk.core.graphiti.dcd.ui.diagram.providers.DevMgrExplorerFeatureProvider;
import gov.redhawk.core.graphiti.dcd.ui.ext.DeviceShape;
import gov.redhawk.core.graphiti.dcd.ui.ext.ServiceShape;
import gov.redhawk.core.graphiti.ui.ext.RHContainerShape;
import gov.redhawk.core.graphiti.ui.util.DUtil;
import gov.redhawk.ide.graphiti.dcd.ui.diagram.feature.delete.DeviceReleaseFeature;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.ShowConsoleFeature;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.TerminateFeature;
import mil.jpeojtrs.sca.partitioning.ComponentInstantiation;

public class DevMgrSandboxFeatureProvider extends DevMgrExplorerFeatureProvider {

	public DevMgrSandboxFeatureProvider(IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);
	}

	@Override
	public ICustomFeature[] getContextButtonPadFeatures(CustomContext context) {
		// Check the selection to make sure it's appropriate
		for (PictogramElement pe : context.getPictogramElements()) {
			if (!(pe instanceof RHContainerShape) || DUtil.getBusinessObject(pe, ComponentInstantiation.class) == null) {
				return super.getContextButtonPadFeatures(context);
			}
		}

		List<ICustomFeature> features = new ArrayList<ICustomFeature>(Arrays.asList(super.getContextButtonPadFeatures(context)));
		features.add(new ShowConsoleFeature(this));

		return features.toArray(new ICustomFeature[features.size()]);
	}

	@Override
	public IDeleteFeature getDeleteFeature(IDeleteContext context) {
		final PictogramElement pe = context.getPictogramElement();
		if (pe instanceof DeviceShape) {
			// Deleting a device should be handled by release
			return new DeviceReleaseFeature(this);
		} else if (pe instanceof ServiceShape) {
			// Deleting a service should be handled by terminate
			return new TerminateFeature(this);
		}

		return super.getDeleteFeature(context);
	}
}
