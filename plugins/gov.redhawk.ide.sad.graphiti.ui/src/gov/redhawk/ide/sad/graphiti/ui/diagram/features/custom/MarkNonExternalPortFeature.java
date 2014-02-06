package gov.redhawk.ide.sad.graphiti.ui.diagram.features.custom;

import gov.redhawk.ide.sad.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.StyleUtil;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.Port;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.emf.common.util.EList;
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

public class MarkNonExternalPortFeature extends AbstractCustomFeature{

	public MarkNonExternalPortFeature(IFeatureProvider fp) {
	    super(fp);
    }

	public final static String NAME = "Mark Non-External Port";
	public final static String DESCRIPTION = "Mark this port non-external to waveform";
	
	@Override
	public String getName(){
		return NAME;
	}
	
	@Override
	public String getDescription(){
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
		FixPointAnchor fixPointAnchor = (FixPointAnchor)context.getPictogramElements()[0];
	    final Object obj = DUtil.getBusinessObject(fixPointAnchor);
	    
	    ContainerShape providesPortRectangleShape = (ContainerShape)Graphiti.getPeService().getActiveContainerPe(fixPointAnchor);
	    
	    //editing domain for our transaction
	    TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramEditor().getEditingDomain();
//kepler	    TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

	    //get sad from diagram
	    final SoftwareAssembly sad = DUtil.getDiagramSAD(getFeatureProvider(), getDiagram());
	    final EList<Port> externalPortList = sad.getExternalPorts().getPort();
	    
	    //get container outerContainerShape, which will be linked to SadComponentInstantiation
	    ContainerShape outerContainerShape = DUtil.findContainerShapeParentWithProperty(providesPortRectangleShape, RHContainerShapeImpl.SHAPE_outerContainerShape);

	    //Perform business object manipulation in a Command
	    TransactionalCommandStack stack = (TransactionalCommandStack)editingDomain.getCommandStack();
	    stack.execute(new RecordingCommand(editingDomain){
	    	@Override
	    	protected void doExecute() {
	    		
	    		Port portToRemove = null;
	    		//set port identifier
	    		if(obj instanceof ProvidesPortStub){
	    			for(Port p: externalPortList){
			    		if(p.getProvidesIndentifier().equals(((ProvidesPortStub)obj).getName())){
			    			portToRemove = p;
			    		}
			    	}
	    		}else if(obj instanceof UsesPortStub){
	    			for(Port p: externalPortList){
			    		if(p.getUsesIdentifier().equals(((UsesPortStub)obj).getName())){
			    			portToRemove = p;
			    		}
			    	}
	    		}
	    		//remove external port
	    		externalPortList.remove(portToRemove);
	    	}
	    });


	    //change style of port
	    Rectangle fixPointAnchorRectangle = (Rectangle)fixPointAnchor.getGraphicsAlgorithm();
	    fixPointAnchorRectangle.setStyle(StyleUtil.getStyleForUsesPort(DUtil.findDiagram(outerContainerShape)));
	    updatePictogramElement(fixPointAnchor);
	    

    }
	
}
