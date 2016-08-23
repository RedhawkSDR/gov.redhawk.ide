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
package gov.redhawk.ide.graphiti.sad.ui.diagram.providers;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import gov.redhawk.core.graphiti.sad.ui.diagram.providers.WaveformExplorerFeatureProvider;
import gov.redhawk.core.graphiti.sad.ui.ext.ComponentShape;
import gov.redhawk.ide.graphiti.sad.ui.diagram.features.delete.ComponentReleaseFeature;

public class WaveformSandboxFeatureProvider extends WaveformExplorerFeatureProvider {

	public WaveformSandboxFeatureProvider(IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);
	}

	@Override
	public IDeleteFeature getDeleteFeature(IDeleteContext context) {
		// Deleting a component should be handled by release
		final PictogramElement pe = context.getPictogramElement();
		if (pe instanceof ComponentShape) {
			return new ComponentReleaseFeature(this);
		}

		return super.getDeleteFeature(context);
	}
}
