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

import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.StyleUtil;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.Port;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.services.Graphiti;

public class MarkNonExternalPortFeature extends AbstractCustomFeature {

	public MarkNonExternalPortFeature(IFeatureProvider fp) {
		super(fp);
	}

	public static final String NAME = "Mark Non-External Port";
	public static final String DESCRIPTION = "Mark this port non-external to waveform";

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
		return true;
	}

	/**
	 * Marks a ProvidesPortStub or UsesPortStub as an non-external port
	 */
	@Override
	public void execute(ICustomContext context) {
		final FixPointAnchor fixPointAnchor = (FixPointAnchor) context.getPictogramElements()[0];
		final Object obj = DUtil.getBusinessObject(fixPointAnchor);

		final ContainerShape providesPortRectangleShape = (ContainerShape) Graphiti.getPeService().getActiveContainerPe(fixPointAnchor);

		// editing domain for our transaction
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		// get sad from diagram
		final SoftwareAssembly sad = DUtil.getDiagramSAD(getFeatureProvider(), getDiagram());
		final EList<Port> externalPortList = sad.getExternalPorts().getPort();

		// get container outerContainerShape, which will be linked to SadComponentInstantiation
		final ContainerShape outerContainerShape = DUtil.findContainerShapeParentWithProperty(providesPortRectangleShape,
			RHContainerShapeImpl.SHAPE_OUTER_CONTAINER);

		// Perform business object manipulation in a Command
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {

				Port portToRemove = null;
				// set port identifier
				if (obj instanceof ProvidesPortStub) {
					for (Port p : externalPortList) {
						if (((ProvidesPortStub) obj).getName().equals(p.getProvidesIndentifier())
							&& ((ProvidesPortStub) obj).eContainer().equals(p.getComponentInstantiationRef().getInstantiation())) {
							portToRemove = p;
						}
					}
				} else if (obj instanceof UsesPortStub) {
					for (Port p : externalPortList) {
						if (((UsesPortStub) obj).getName().equals(p.getUsesIdentifier())
							&& ((UsesPortStub) obj).eContainer().equals(p.getComponentInstantiationRef().getInstantiation())) {
							portToRemove = p;
						}
					}
				}

				// remove link to external port
				fixPointAnchor.getLink().getBusinessObjects().remove(portToRemove);

				// remove external port
				externalPortList.remove(portToRemove);

				// if there are no more external ports, remove the external ports object
				// and dis-associate external ports object with all component shapes
				if (sad.getExternalPorts().getPort().size() < 1) {
					DUtil.removeBusinessObjectFromAllPictogramElements(getDiagram(), sad.getExternalPorts());
					EcoreUtil.delete(sad.getExternalPorts());
					sad.setExternalPorts(null);
				}

				// change style of port
				Rectangle fixPointAnchorRectangle = (Rectangle) fixPointAnchor.getGraphicsAlgorithm();
				fixPointAnchorRectangle.setStyle(StyleUtil.createStyleForUsesPort(DUtil.findDiagram(outerContainerShape)));
			}
		});

		updatePictogramElement(fixPointAnchor);
	}

}
