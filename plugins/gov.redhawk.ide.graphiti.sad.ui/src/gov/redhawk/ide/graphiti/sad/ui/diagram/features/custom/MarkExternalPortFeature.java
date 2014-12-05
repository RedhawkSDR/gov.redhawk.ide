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
import gov.redhawk.ide.graphiti.sad.ext.ComponentShape;
import gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.ComponentPattern;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.StyleUtil;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.Port;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadComponentInstantiationRef;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

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
		return true;
	}

	/**
	 * Marks a ProvidesPortStub or UsesPortStub as an external port
	 */
	@Override
	public void execute(ICustomContext context) {
		final FixPointAnchor fixPointAnchor = (FixPointAnchor) context.getPictogramElements()[0];
		final Object obj = DUtil.getBusinessObject(fixPointAnchor);

		ContainerShape providesPortRectangleShape = (ContainerShape) Graphiti.getPeService().getActiveContainerPe(fixPointAnchor);

		// editing domain for our transaction
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		// get sad from diagram
		final SoftwareAssembly sad = DUtil.getDiagramSAD(getFeatureProvider(), getDiagram());

		// get container outerContainerShape, which will be linked to SadComponentInstantiation
		final ContainerShape outerContainerShape = DUtil.findContainerShapeParentWithProperty(providesPortRectangleShape,
			RHContainerShapeImpl.SHAPE_OUTER_CONTAINER);
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
					for (ComponentShape cShape : ComponentPattern.getAllComponentShapes(getDiagram())) {
						cShape.getLink().getBusinessObjects().add(sad.getExternalPorts());
					}
				}

				// add external port to model
				Port port = SadFactory.eINSTANCE.createPort();
				// set port sciref
				SadComponentInstantiationRef sciRef = SadFactory.eINSTANCE.createSadComponentInstantiationRef();
				sciRef.setInstantiation(ci);
				port.setComponentInstantiationRef(sciRef);

				// set port identifier
				if (obj instanceof ProvidesPortStub) {
					port.setProvidesIndentifier(((ProvidesPortStub) obj).getName());
				} else if (obj instanceof UsesPortStub) {
					port.setUsesIdentifier(((UsesPortStub) obj).getName());
				}
				sad.getExternalPorts().getPort().add(port);

				// change style of port
				Rectangle fixPointAnchorRectangle = (Rectangle) fixPointAnchor.getGraphicsAlgorithm();
				fixPointAnchorRectangle.setStyle(StyleUtil.createStyleForExternalUsesPort(DUtil.findDiagram(outerContainerShape)));
				fixPointAnchor.getLink().getBusinessObjects().add(port);
			}
		});

		updatePictogramElement(fixPointAnchor);
	}

//	/**
//	 * Marks a ProvidesPortStub or UsesPortStub as an external port
//	 */
//	@Override
//    public void execute(ICustomContext context) {
//		ContainerShape usesPortRectangleShape = (ContainerShape)context.getPictogramElements()[0];
//	    final Object obj = DiagramUtil.getBusinessObject(usesPortRectangleShape);
//	    	
//	    //editing domain for our transaction
//	    TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();
//
//	    //get sad from diagram
//	    final SoftwareAssembly sad = DiagramUtil.getDiagramSAD(getFeatureProvider(), getDiagram());
//
//	    //get container outerContainerShape, which will be linked to SadComponentInstantiation
//	    ContainerShape outerContainerShape = DiagramUtil.findContainerShapeParentWithProperty(usesPortRectangleShape, DiagramUtil.SHAPE_outerContainerShape);
//	    final SadComponentInstantiation ci = (SadComponentInstantiation)DiagramUtil.getBusinessObject(outerContainerShape);
//	    
//	    //Perform business object manipulation in a Command
//	    TransactionalCommandStack stack = (TransactionalCommandStack)editingDomain.getCommandStack();
//	    stack.execute(new RecordingCommand(editingDomain){
//	    	@Override
//	    	protected void doExecute() {
//
//	    		
//	    		//create external ports if necessary
//	    		if(sad.getExternalPorts() == null){
//	    			sad.setExternalPorts(SadFactory.eINSTANCE.createExternalPorts());
//	    		}
//	    		
//	    		//add external port to model
//	    		Port port = SadFactory.eINSTANCE.createPort();
//	    		//set port sciref
//	    		SadComponentInstantiationRef sciRef = SadFactory.eINSTANCE.createSadComponentInstantiationRef();
//	    		sciRef.setInstantiation(ci);
//	    		port.setComponentInstantiationRef(sciRef);
//	    		
//	    		//set port identifier
//	    		if(obj instanceof ProvidesPortStub){
//	    			port.setProvidesIndentifier(((ProvidesPortStub)obj).getName());
//	    		}else if(obj instanceof UsesPortStub){
//	    			port.setUsesIdentifier(((UsesPortStub)obj).getName());
//	    		}
//	    		sad.getExternalPorts().getPort().add(port);
//	    	}
//	    });
//
//
//	    //change style of port
//	    Rectangle usesPortRectangle = (Rectangle)usesPortRectangleShape.getGraphicsAlgorithm();
//	    usesPortRectangle.setStyle(StyleUtil.getStyleForExternalUsesPort(DiagramUtil.findDiagram(usesPortRectangleShape)));
//	    updatePictogramElement(usesPortRectangleShape);
//	    
//	    //we might need to work with the anchor isntad...is that why the box isn't turning blue and because its not selectable when the outerContainerBox isn't selected
//
//    }

}
