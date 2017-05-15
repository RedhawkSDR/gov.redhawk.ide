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
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.tb.ContextButtonEntry;
import org.eclipse.graphiti.tb.ContextEntryHelper;
import org.eclipse.graphiti.tb.IContextButtonEntry;
import org.eclipse.graphiti.tb.IContextButtonPadData;
import org.eclipse.graphiti.tb.IContextEntry;

import gov.redhawk.core.graphiti.dcd.ui.ext.ServiceShape;

public class DevMgrSandboxToolBehaviorProvider extends DCDPaletteToolBehaviorProvider {

	public DevMgrSandboxToolBehaviorProvider(IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);
	}

	@Override
	protected void setGenericContextButtons(IContextButtonPadData data, PictogramElement pe, int identifiers) {
		// update button
		if ((identifiers & CONTEXT_BUTTON_UPDATE) != 0) {
			IContextButtonEntry updateButton = ContextEntryHelper.createDefaultUpdateContextButton(getFeatureProvider(), pe);
			if (updateButton != null) {
				data.getGenericContextButtons().add(updateButton);
			}
		}

		// remove button
		if ((identifiers & CONTEXT_BUTTON_REMOVE) != 0) {
			IContextButtonEntry removeButton = ContextEntryHelper.createDefaultRemoveContextButton(getFeatureProvider(), pe);
			if (removeButton != null) {
				data.getGenericContextButtons().add(removeButton);
			}
		}

		// delete button
		IContextButtonEntry deleteButton = null;
		if (pe instanceof ServiceShape) {
			String iconId = gov.redhawk.ide.graphiti.ui.diagram.providers.ImageProvider.IMG_TERMINATE;
			deleteButton = createDeleteContextButton(getFeatureProvider(), pe, iconId);
		} else if ((identifiers & CONTEXT_BUTTON_DELETE) != 0) {
			deleteButton = ContextEntryHelper.createDefaultDeleteContextButton(getFeatureProvider(), pe);
		}
		if (deleteButton != null) {
			data.getGenericContextButtons().add(deleteButton);
		}
	}

	private IContextButtonEntry createDeleteContextButton(IFeatureProvider featureProvider, PictogramElement pe, String iconId) {
		IDeleteContext deleteContext = new DeleteContext(pe);
		IDeleteFeature deleteFeature = featureProvider.getDeleteFeature(deleteContext);
		IContextButtonEntry ret = null;
		if (deleteFeature != null && deleteFeature.isAvailable(deleteContext)) {
			ret = new ContextButtonEntry(deleteFeature, deleteContext);
			markAsDeleteContextEntry(ret, iconId);
		}
		return ret;
	}

	private void markAsDeleteContextEntry(IContextEntry entry, String iconId) {
		if (entry != null) {
			entry.setText(entry.getFeature().getName());
			entry.setDescription(entry.getFeature().getDescription());
			entry.setIconId(iconId);
		}
	}

}
