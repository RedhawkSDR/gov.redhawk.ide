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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IReconnectionFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.pattern.IPattern;

import gov.redhawk.core.graphiti.sad.ui.diagram.providers.SADGraphitiFeatureProvider;
import gov.redhawk.core.graphiti.ui.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.sad.ui.diagram.features.custom.DecrementStartOrderFeature;
import gov.redhawk.ide.graphiti.sad.ui.diagram.features.custom.IncrementStartOrderFeature;
import gov.redhawk.ide.graphiti.sad.ui.diagram.features.custom.MarkExternalPortFeature;
import gov.redhawk.ide.graphiti.sad.ui.diagram.features.custom.MarkNonExternalPortFeature;
import gov.redhawk.ide.graphiti.sad.ui.diagram.features.custom.SetAsAssemblyControllerFeature;
import gov.redhawk.ide.graphiti.sad.ui.diagram.features.reconnect.SADReconnectFeature;
import gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.HostCollocationPattern;
import gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.UsesDeviceFrontEndTunerPattern;
import gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.UsesDevicePattern;
import gov.redhawk.ide.graphiti.sad.ui.internal.diagram.features.SADUpdateDiagramFeature;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.DialogEditingFeatureForPattern;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.IDialogEditingPattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByCORBANamePattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByDomainManagerPattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByEventChannelPattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByFileManagerPattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByServicePattern;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.Port;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

public class SADEditorFeatureProvider extends SADGraphitiFeatureProvider {

	public SADEditorFeatureProvider(IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);

		// Add host collocation pattern
		addPattern(new HostCollocationPattern());

		// Add find by patterns
		addPattern(new FindByDomainManagerPattern());
		addPattern(new FindByFileManagerPattern());
		addPattern(new FindByEventChannelPattern());
		addPattern(new FindByServicePattern());
		addPattern(new FindByCORBANamePattern());

		// Add uses device patterns
		addPattern(new UsesDeviceFrontEndTunerPattern());
		addPattern(new UsesDevicePattern());
	}

	@Override
	public IUpdateFeature getUpdateFeature(IUpdateContext context) {
		if (context.getPictogramElement() instanceof Diagram) {
			return new SADUpdateDiagramFeature(this);
		}

		return super.getUpdateFeature(context);
	}

	@Override
	public ICustomFeature[] getCustomFeatures(ICustomContext context) {
		List<ICustomFeature> features = new ArrayList<>();
		Collections.addAll(features, super.getCustomFeatures(context));

		Diagram diagram = getDiagramTypeProvider().getDiagram();
		PictogramElement[] pes = context.getPictogramElements();
		if (pes == null || pes.length == 0) {
			return features.toArray(new ICustomFeature[features.size()]);
		}
		EObject businessObject = DUtil.getBusinessObject(pes[0]);

		if (pes[0] instanceof RHContainerShape) {
			if (businessObject instanceof SadComponentInstantiation) {
				// Design-time-only component features
				features.add(new SetAsAssemblyControllerFeature(this));
				features.add(new IncrementStartOrderFeature(this));
				features.add(new DecrementStartOrderFeature(this));
			}

			IPattern pattern = getPatternForPictogramElement(pes[0]);
			if (pattern instanceof IDialogEditingPattern) {
				IDialogEditingPattern dialogEditing = (IDialogEditingPattern) pattern;
				if (dialogEditing.canDialogEdit(context)) {
					features.add(new DialogEditingFeatureForPattern(this, dialogEditing));
				}
			}
		}

		// Design-time-only port features
		if ((businessObject instanceof ProvidesPortStub || businessObject instanceof UsesPortStub)
			&& businessObject.eContainer() instanceof SadComponentInstantiation) {
			boolean mark = true;

			final SoftwareAssembly sad = DUtil.getDiagramSAD(diagram);
			if (sad.getExternalPorts() != null) {
				// Get existing external ports
				EList<Port> externalPortList = sad.getExternalPorts().getPort();

				// Determine if this port is already external
				if (businessObject instanceof ProvidesPortStub) {
					String portName = ((ProvidesPortStub) businessObject).getName();
					EObject portContainer = ((ProvidesPortStub) businessObject).eContainer();
					for (Port p : externalPortList) {
						if (portName.equals(p.getProvidesIdentifier()) && portContainer.equals(p.getComponentInstantiationRef().getInstantiation())) {
							mark = false;
							break;
						}
					}
				} else if (businessObject instanceof UsesPortStub) {
					String portName = ((UsesPortStub) businessObject).getName();
					EObject portContainer = ((UsesPortStub) businessObject).eContainer();
					for (Port p : externalPortList) {
						if (portName.equals(p.getUsesIdentifier()) && portContainer.equals(p.getComponentInstantiationRef().getInstantiation())) {
							mark = false;
							break;
						}
					}
				}
			}

			// Add the mark external feature
			if (mark) {
				features.add(new MarkExternalPortFeature(this));
			} else {
				features.add(new MarkNonExternalPortFeature(this));
			}
		}

		return features.toArray(new ICustomFeature[features.size()]);
	}

	@Override
	public IReconnectionFeature getReconnectionFeature(IReconnectionContext context) {
		Object businessObject = getBusinessObjectForPictogramElement(context.getOldAnchor());
		if (businessObject instanceof UsesPortStub || businessObject instanceof ProvidesPortStub || businessObject instanceof ComponentSupportedInterfaceStub) {
			return new SADReconnectFeature(this);
		}

		return super.getReconnectionFeature(context);
	}
}
