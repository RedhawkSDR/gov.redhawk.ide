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

import java.util.Arrays;
import java.util.List;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IReconnectionFeature;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.pattern.IPattern;

import gov.redhawk.core.graphiti.dcd.ui.diagram.providers.DCDGraphitiFeatureProvider;
import gov.redhawk.core.graphiti.ui.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.dcd.ui.diagram.feature.reconnect.DCDReconnectFeature;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.DialogEditingFeatureForPattern;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.IDialogEditingPattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByCORBANamePattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByDomainManagerPattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByEventChannelPattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByFileManagerPattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByServicePattern;
import mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;

public class DCDEditorFeatureProvider extends DCDGraphitiFeatureProvider {

	public DCDEditorFeatureProvider(IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);

		// Add find by patterns
		addPattern(new FindByDomainManagerPattern());
		addPattern(new FindByFileManagerPattern());
		addPattern(new FindByEventChannelPattern());
		addPattern(new FindByServicePattern());
		addPattern(new FindByCORBANamePattern());
	}

	@Override
	public ICustomFeature[] getCustomFeatures(ICustomContext context) {
		List<ICustomFeature> features = Arrays.asList(super.getCustomFeatures(context));

		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			PictogramElement pictogramElement = pes[0];
			if (pictogramElement instanceof RHContainerShape) {
				IPattern pattern = getPatternForPictogramElement(pictogramElement);
				if (pattern instanceof IDialogEditingPattern) {
					IDialogEditingPattern dialogEditing = (IDialogEditingPattern) pattern;
					if (dialogEditing.canDialogEdit(context)) {
						features.add(new DialogEditingFeatureForPattern(this, dialogEditing));
					}
				}
			}
		}

		return features.toArray(new ICustomFeature[features.size()]);
	}

	@Override
	public IReconnectionFeature getReconnectionFeature(IReconnectionContext context) {
		Object businessObject = getBusinessObjectForPictogramElement(context.getOldAnchor());
		if (businessObject instanceof UsesPortStub || businessObject instanceof ProvidesPortStub || businessObject instanceof ComponentSupportedInterfaceStub) {
			return new DCDReconnectFeature(this);
		}

		return super.getReconnectionFeature(context);
	}
}
