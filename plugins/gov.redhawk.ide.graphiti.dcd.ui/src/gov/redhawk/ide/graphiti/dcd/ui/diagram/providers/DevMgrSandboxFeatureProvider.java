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

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import gov.redhawk.core.graphiti.dcd.ui.ext.DeviceShape;
import gov.redhawk.core.graphiti.dcd.ui.ext.ServiceShape;
import gov.redhawk.ide.graphiti.dcd.ui.diagram.feature.delete.DeviceReleaseFeature;

public class DevMgrSandboxFeatureProvider extends DevMgrExplorerFeatureProvider {

	public DevMgrSandboxFeatureProvider(IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);
	}

	@Override
	public IDeleteFeature getDeleteFeature(IDeleteContext context) {
		final PictogramElement pe = context.getPictogramElement();
		if (pe instanceof DeviceShape) {
			// Deleting a device should be handled by release
			return new DeviceReleaseFeature(this);
		} else if (pe instanceof ServiceShape) {
			// Can't release a service
			return null;
		}

		return super.getDeleteFeature(context);
	}
}
