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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;

import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.PortStyleUtil;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.Port;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

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
		final Anchor anchor = (Anchor) context.getPictogramElements()[0];
		final Object obj = DUtil.getBusinessObject(anchor);
		final ContainerShape portShape;
		if (obj instanceof ProvidesPortStub) {
			portShape = DUtil.findContainerShapeParentWithProperty(anchor, RHContainerShapeImpl.SHAPE_PROVIDES_PORT_CONTAINER);
		} else if (obj instanceof UsesPortStub) {
			portShape = DUtil.findContainerShapeParentWithProperty(anchor, RHContainerShapeImpl.SHAPE_USES_PORT_CONTAINER);
		} else {
			return;
		}

		// editing domain for our transaction
		final Diagram diagram = getDiagram();
		final TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		// get sad from diagram
		final SoftwareAssembly sad = DUtil.getDiagramSAD(diagram);
		final EList<Port> externalPortList = sad.getExternalPorts().getPort();

		// Perform business object manipulation in a Command
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {
				Port portToRemove = null;
				// set port identifier
				if (obj instanceof ProvidesPortStub) {
					for (Port p : externalPortList) {
						if (((ProvidesPortStub) obj).getName().equals(p.getProvidesIdentifier())
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
				anchor.getLink().getBusinessObjects().remove(portToRemove);

				// remove external port
				externalPortList.remove(portToRemove);

				// if there are no more external ports, remove the external ports object
				// and dis-associate external ports object with all component shapes
				if (sad.getExternalPorts().getPort().isEmpty()) {
					DUtil.removeBusinessObjectFromAllPictogramElements(diagram, sad.getExternalPorts());
					EcoreUtil.delete(sad.getExternalPorts());
					sad.setExternalPorts(null);
				}
			}
		});

		PortStyleUtil.resetPortStyling(diagram, editingDomain, portShape);
	}

}
