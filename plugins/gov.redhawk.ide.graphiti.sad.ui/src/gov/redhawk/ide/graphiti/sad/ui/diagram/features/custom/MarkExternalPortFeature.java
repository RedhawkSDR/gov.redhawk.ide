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
package gov.redhawk.ide.graphiti.sad.ui.diagram.features.custom;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;

import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.sad.ext.ComponentShape;
import gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.ComponentPattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.ProvidesPortPattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.UsesPortPattern;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.Port;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadComponentInstantiationRef;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

public class MarkExternalPortFeature extends AbstractCustomFeature {

	public MarkExternalPortFeature(IFeatureProvider fp) {
		super(fp);
	}

	public static final String NAME = "Mark External Port";
	public static final String DESCRIPTION = "Mark this port external to waveform";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return DESCRIPTION;
	}

	/**
	 * Always return true, we filter this specifically in the FeatureProvider
	 */
	@Override
	public boolean canExecute(ICustomContext context) {
		// Can't execute if the diagram is read-only
		if (DUtil.isDiagramReadOnly(getDiagram())) {
			return false;
		}

		return true;
	}

	/**
	 * Marks a ProvidesPortStub or UsesPortStub as an external port
	 */
	@Override
	public void execute(ICustomContext context) {
		final Anchor anchor = (Anchor) context.getPictogramElements()[0];
		final Object obj = DUtil.getBusinessObject(anchor);
		final ContainerShape portShape;
		if (obj instanceof ProvidesPortStub) {
			portShape = DUtil.findContainerShapeParentWithProperty(anchor, ProvidesPortPattern.SHAPE_PROVIDES_PORT_CONTAINER);
		} else if (obj instanceof UsesPortStub) {
			portShape = DUtil.findContainerShapeParentWithProperty(anchor, UsesPortPattern.SHAPE_USES_PORT_CONTAINER);
		} else {
			return;
		}
		final ContainerShape outerContainerShape = DUtil.findContainerShapeParentWithProperty(portShape, RHContainerShapeImpl.SHAPE_OUTER_CONTAINER);

		final Diagram diagram = getDiagram();
		final TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		final SoftwareAssembly sad = DUtil.getDiagramSAD(diagram);
		final SadComponentInstantiation ci = (SadComponentInstantiation) DUtil.getBusinessObject(outerContainerShape);

		// Perform business object manipulation in a Command
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {
				// initialize external ports if necessary, if we do this we must associate link it to all other
				// components in the diagram
				// this is necessary because the diagram needs to know which shapes to update when the externalPorts
				// object changes in some way.
				if (sad.getExternalPorts() == null) {
					sad.setExternalPorts(SadFactory.eINSTANCE.createExternalPorts());
					for (ComponentShape cShape : ComponentPattern.getAllComponentShapes(diagram)) {
						cShape.getLink().getBusinessObjects().add(sad.getExternalPorts());
					}
				}

				// Add external port to model
				Port port = SadFactory.eINSTANCE.createPort();
				SadComponentInstantiationRef sciRef = SadFactory.eINSTANCE.createSadComponentInstantiationRef();
				sciRef.setInstantiation(ci);
				port.setComponentInstantiationRef(sciRef);
				if (obj instanceof ProvidesPortStub) {
					port.setProvidesIdentifier(((ProvidesPortStub) obj).getName());
				} else if (obj instanceof UsesPortStub) {
					port.setUsesIdentifier(((UsesPortStub) obj).getName());
				}
				sad.getExternalPorts().getPort().add(port);

				// Add the external port as a business object on the anchor
				anchor.getLink().getBusinessObjects().add(port);
			}
		});

		// Update the containing component to reset the port's style
		RHContainerShape componentShape = ScaEcoreUtils.getEContainerOfType(portShape, RHContainerShape.class);
		updatePictogramElement(componentShape);
	}
}
