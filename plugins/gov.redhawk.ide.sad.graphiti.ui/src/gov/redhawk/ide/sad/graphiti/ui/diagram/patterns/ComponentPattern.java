package gov.redhawk.ide.sad.graphiti.ui.diagram.patterns;

import gov.redhawk.ide.sad.graphiti.ext.ComponentShape;
import gov.redhawk.ide.sad.graphiti.ext.RHGxFactory;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.sad.HostCollocation;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadComponentPlacement;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.pattern.AbstractPattern;
import org.eclipse.graphiti.pattern.IPattern;
import org.eclipse.graphiti.services.Graphiti;

public class ComponentPattern extends AbstractPattern implements IPattern{

	
	private URI spdUri = null;
	
	public ComponentPattern(){
		super(null);
	}
	
	public URI getSpdUri() {
		return spdUri;
	}
	public void setSpdUri(URI spdUri){
		this.spdUri=spdUri;
	}
	
	@Override
	public String getCreateName(){
		return "Component";
	}
	
	
	//THE FOLLOWING THREE METHODS DETERMINE IF PATTERN IS APPLICABLE TO OBJECT
	@Override
	public boolean isMainBusinessObjectApplicable(Object mainBusinessObject) {
		return mainBusinessObject instanceof SadComponentInstantiation;
	}
	@Override
	protected boolean isPatternControlled(PictogramElement pictogramElement) {
		Object domainObject = getBusinessObjectForPictogramElement(pictogramElement);
		return isMainBusinessObjectApplicable(domainObject);
	}
	@Override
	protected boolean isPatternRoot(PictogramElement pictogramElement) {
		Object domainObject = getBusinessObjectForPictogramElement(pictogramElement);
		return isMainBusinessObjectApplicable(domainObject);
	}
	
	
	//DIAGRAM FEATURES
	
	@Override
	public boolean canAdd(IAddContext context) {
		if (context.getNewObject() instanceof SadComponentInstantiation) {
			if(context.getTargetContainer() instanceof Diagram ||
					DUtil.getHostCollocation(context.getTargetContainer()) != null){
				return true;
			}
			return false;
		}
		return false;
	}
	
	@Override
	public boolean canRemove(IRemoveContext context) {
		return false;
	}
	
	
	/**
	 * Return true if the user has selected a pictogram element that is linked with
	 * a SADComponentInstantiation instance
	 */
	@Override
	public boolean canDelete(IDeleteContext context) {
		Object obj = DUtil.getBusinessObject(context.getPictogramElement());
		if(obj instanceof SadComponentInstantiation){
			return true;
		}
		return false;
	}
	
	/**
	 * Delete the SadComponentInstantiation linked to the PictogramElement.  
	 */
	@Override
	public void delete(IDeleteContext context){
		
		//set componentToDelete
		final SadComponentInstantiation ciToDelete = 
						(SadComponentInstantiation)DUtil.getBusinessObject(context.getPictogramElement());
		
		Diagram diagram = DUtil.findDiagram((ContainerShape)context.getPictogramElement());
		
		//editing domain for our transaction
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();
		
		//get sad from diagram
		final SoftwareAssembly sad = DUtil.getDiagramSAD(getFeatureProvider(), getDiagram());
		
		//Perform business object manipulation in a Command
		TransactionalCommandStack stack = (TransactionalCommandStack)editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain){
			@Override
            protected void doExecute() {
				
				//delete component from SoftwareAssembly
				DUtil.deleteComponentInstantiation(ciToDelete, sad);
				
				//re-organize start order
				organizeStartOrder(sad);
            }
		});
		
		//delete the graphical component
		super.delete(context);
		
		//redraw start order 
		//DUtil.organizeDiagramStartOrder(diagram);
	}
	
	@Override
	public boolean canResizeShape(IResizeShapeContext context){
		return true;
	}
	
	
	/**
	 * Adds a Component to the diagram.  Immediately calls resize at the end to keep sizing and location in one place.
	 */
	public PictogramElement add(IAddContext context) {
		
		SadComponentInstantiation sadComponentInstantiation = (SadComponentInstantiation) context.getNewObject();
		ContainerShape targetContainerShape = (ContainerShape) context.getTargetContainer();
		
		//create shape
		ComponentShape componentShape = RHGxFactory.eINSTANCE.createComponentShape();
		
		//initialize shape contents
		componentShape.init(targetContainerShape, sadComponentInstantiation, getFeatureProvider());
		
		//set shape location to user's selection
		Graphiti.getGaLayoutService().setLocation(componentShape.getGraphicsAlgorithm(), 
				context.getX(), context.getY());
		
		//layout
		layoutPictogramElement(componentShape);
		
		return componentShape;

	}
	

	@Override
	public boolean canLayout(ILayoutContext context){
		ContainerShape containerShape = (ContainerShape) context.getPictogramElement();
		Object obj = DUtil.getBusinessObject(containerShape);
		if(obj instanceof SadComponentInstantiation){
			return true;
		}
		return false;
	}
	
	/**
	 * Layout children of component
	 */
	@Override
	public boolean layout(ILayoutContext context){

		((ComponentShape)context.getPictogramElement()).layout();
		
		//something is always changing.
        return true;
	}
	
	public boolean canMoveShape(IMoveShapeContext context) {

		SadComponentInstantiation sadComponentInstantiation = 
				(SadComponentInstantiation)DUtil.getBusinessObject(context.getPictogramElement());
		if(sadComponentInstantiation == null){
			return false;
		}
		
		//if moving to HostCollocation to Sad Partitioning
		if(context.getTargetContainer() instanceof Diagram ||
				DUtil.getHostCollocation(context.getTargetContainer()) != null){
			return true;
		}
		return false;
		
	}
	
	/**
	 * Moves Component shape.
	 * if moving to HostCollocation or away from one modify underlying model and allow parent class to perform graphical move
	 * if moving within the same container allow parent class to perform graphical move
	 */
	public void moveShape(IMoveShapeContext context) {
		SadComponentInstantiation ci = 
				(SadComponentInstantiation)DUtil.getBusinessObject(context.getPictogramElement());

		final SoftwareAssembly sad = DUtil.getDiagramSAD(getFeatureProvider(), getDiagram());
		
		//if moving inside the same container
		if(context.getSourceContainer() == context.getTargetContainer()){
			super.moveShape(context);
		}
		
		//if moving from HostCollocation to a different HostCollocation
		if(DUtil.getHostCollocation(context.getSourceContainer()) != null &&
				DUtil.getHostCollocation(context.getTargetContainer()) != null &&
				DUtil.getHostCollocation(context.getSourceContainer()) != DUtil.getHostCollocation(context.getTargetContainer())){
			//swap parents
			DUtil.getHostCollocation(context.getSourceContainer()).getComponentPlacement().remove((SadComponentPlacement)ci.getPlacement());
			DUtil.getHostCollocation(context.getTargetContainer()).getComponentPlacement().add((SadComponentPlacement)ci.getPlacement());
			super.moveShape(context);
		}
		
		//if moving to HostCollocation from Sad Partitioning
		if(DUtil.getHostCollocation(context.getTargetContainer()) != null &&
				context.getSourceContainer() instanceof Diagram){
			//swap parents
			sad.getPartitioning().getComponentPlacement().remove(ci.getPlacement());
			DUtil.getHostCollocation(context.getTargetContainer()).getComponentPlacement().add((SadComponentPlacement)ci.getPlacement());
			super.moveShape(context);
		}
		
		//if moving to Sad Partitioning from HostCollocation
		if(DUtil.getHostCollocation(context.getSourceContainer()) != null &&
				context.getTargetContainer() instanceof Diagram){
			//swap parents
			sad.getPartitioning().getComponentPlacement().add((SadComponentPlacement)ci.getPlacement());
			DUtil.getHostCollocation(context.getSourceContainer()).getComponentPlacement().remove((SadComponentPlacement)ci.getPlacement());
			super.moveShape(context);
		}

	}
	
	/**
	 * Return the highest start order for all components in the SAD.
	 * Returns null if no start order found
	 * @param sad
	 * @return
	 */
	public static BigInteger determineHighestStartOrder(final SoftwareAssembly sad){
		BigInteger highestStartOrder = null;
		for(SadComponentInstantiation c: getAllComponents(sad)){
			if(c.getStartOrder() != null && c.getStartOrder().compareTo(BigInteger.ZERO) >= 0){
				highestStartOrder = c.getStartOrder();
			}
		}
		return highestStartOrder;
	}
	
	/**
	 * Get all components in sad
	 * @param sad
	 * @return
	 */
	public static List<SadComponentInstantiation> getAllComponents(final SoftwareAssembly sad) {
		final List<SadComponentInstantiation> retVal = new ArrayList<SadComponentInstantiation>();
		if (sad.getPartitioning() != null) {
			for (final SadComponentPlacement cp : sad.getPartitioning().getComponentPlacement()) {
				retVal.addAll(cp.getComponentInstantiation());
			}
			for (final HostCollocation h : sad.getPartitioning().getHostCollocation()) {
				for (final SadComponentPlacement cp : h.getComponentPlacement()) {
					retVal.addAll(cp.getComponentInstantiation());
				}
			}
		}

		return retVal;
	}
	
	//adjust the start order for a component
	public static void organizeStartOrder(final SoftwareAssembly sad){
		BigInteger startOrder = BigInteger.ZERO;
		for(SadComponentInstantiation c: sad.getComponentInstantiationsInStartOrder()){
			c.setStartOrder(startOrder);
			startOrder = startOrder.add(BigInteger.ONE);
		}
	}
	
//	@Override
//	public boolean canUpdate(IUpdateContext context) {
//		PictogramElement pictogramElement = context.getPictogramElement();
//		return isPatternControlled(pictogramElement);
//	}

	@Override
	public boolean update(IUpdateContext context) {
		
		//business object
		SadComponentInstantiation ci = 
				(SadComponentInstantiation)DUtil.getBusinessObject(context.getPictogramElement());
				
		Reason updated = ((ComponentShape)context.getPictogramElement()).update(ci, getFeatureProvider());
		
		//if we updated redraw
		if(updated.toBoolean()){
			layoutPictogramElement(context.getPictogramElement());
		}

		return updated.toBoolean();
	}
	
	/**
	 * Determines whether we need to update the diagram from the model.  
	 */
	@Override
	public IReason updateNeeded(IUpdateContext context) {
		
		//business object
		SadComponentInstantiation ci = 
				(SadComponentInstantiation)DUtil.getBusinessObject(context.getPictogramElement());
				
		Reason requiresUpdate = ((ComponentShape)context.getPictogramElement()).updateNeeded(ci, getFeatureProvider());

		return requiresUpdate;
	}

}
