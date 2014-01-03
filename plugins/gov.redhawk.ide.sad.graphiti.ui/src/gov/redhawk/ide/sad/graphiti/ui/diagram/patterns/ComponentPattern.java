package gov.redhawk.ide.sad.graphiti.ui.diagram.patterns;

import gov.redhawk.ide.sad.graphiti.ui.diagram.providers.ImageProvider;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DiagramUtil;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.StyleUtil;

import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.partitioning.ComponentFile;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadComponentPlacement;
import mil.jpeojtrs.sca.sad.SadConnectInterface;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.impl.AreaContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.pattern.AbstractPattern;
import org.eclipse.graphiti.pattern.IPattern;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.internal.GraphitiUIPlugin;
import org.eclipse.graphiti.ui.services.GraphitiUi;

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
	
	
	public static UpdateValueStrategy floatingPointRangeValidator(final String fieldName, final float minValue, final float maxValue, final boolean allowDefault){
	    return new UpdateValueStrategy().setAfterGetValidator(new IValidator() {

	    	@Override
	    	public IStatus validate(Object value){
	    		if(value != null && value instanceof String){
	    			
	    			String s = (String)value;
	    			
	    			//empty test
	    			if(s.trim().length() <=0){
	    				return ValidationStatus.error(fieldName + "must be non-empty");
	    			}
	    				
	    			//if "default" acceptable, test for it
	    			if(allowDefault && "default".equals(s)){
	    				return ValidationStatus.ok();
	    			}
	    			
	    			//float test
	    			Float fl;
	    			try{
	    				fl = Float.parseFloat(s);
	    			}catch(NumberFormatException nfe){
	    				return ValidationStatus.error(fieldName + "must be a floating point number");
	    			}
	    			
	    			//min test
	    			if(fl < minValue){
	    				return ValidationStatus.error(fieldName + "must be larger than or equal to " + String.valueOf(minValue));
	    			}
	    			
	    			//max test
	    			if(fl > maxValue){
	    				return ValidationStatus.error(fieldName + "must be less than or equal to " + String.valueOf(minValue));
	    			}
	    				
	    			return ValidationStatus.ok();
	    		}
				return null;
	    	}
	    });
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
					DiagramUtil.getHostCollocation(context.getTargetContainer()) != null){
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
	
//	@Override
//	public void remove(IRemoveContext context) {
//		if (wrappedRemoveFeature == null) {
//			wrappedRemoveFeature = createRemoveFeature(context);
//		}
//		wrappedRemoveFeature.remove(context);
//	}
	
	/**
	 * Return true if the user has selected a pictogram element that is linked with
	 * a SADComponentInstantiation instance
	 */
	@Override
	public boolean canDelete(IDeleteContext context) {
		Object obj = DiagramUtil.getBusinessObject(context.getPictogramElement());
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
						(SadComponentInstantiation)DiagramUtil.getBusinessObject(context.getPictogramElement());
		
		//editing domain for our transaction
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();
		
		//get sad from diagram
		final SoftwareAssembly sad = DiagramUtil.getDiagramSAD(getFeatureProvider(), getDiagram());
		
		//Perform business object manipulation in a Command
		TransactionalCommandStack stack = (TransactionalCommandStack)editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain){
			@Override
            protected void doExecute() {
				
				//delete component from SoftwareAssembly
				DiagramUtil.deleteComponentInstantiation(ciToDelete, sad);
            }
		});
		
		//delete the graphical component
		super.delete(context);
		
	}

	/**
	 * Resize Component
	 */
	@Override
	public void resizeShape(IResizeShapeContext context) {
		
		SadComponentInstantiation sadComponentInstantiation = (SadComponentInstantiation)getFeatureProvider().getBusinessObjectForPictogramElement(context.getPictogramElement());
		if(sadComponentInstantiation == null){
			return;
		}
		
		//resize component
		DiagramUtil.resizeOuterContainerShape(context, context.getPictogramElement(), 
				sadComponentInstantiation.getPlacement().getComponentFileRef().getFile().getSoftPkg().getName(),
				sadComponentInstantiation.getUsageName(), sadComponentInstantiation.getProvides(), sadComponentInstantiation.getUses());
	}
	
	/**
	 * Resizing a Component shape is always allowed
	 */
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
		Diagram diagram = findDiagram(targetContainerShape);
		
		//OUTER RECTANGLE
		ContainerShape outerContainerShape = 
				DiagramUtil.addOuterRectangle(targetContainerShape, 
						sadComponentInstantiation.getPlacement().getComponentFileRef().getFile().getSoftPkg().getName(), 
						sadComponentInstantiation, getFeatureProvider(),
						ImageProvider.IMG_COMPONENT_PLACEMENT,
						StyleUtil.getStyleForComponentOuter(findDiagram(targetContainerShape)));
		
		//INNER RECTANGLE
		DiagramUtil.addInnerRectangle(diagram,
				outerContainerShape,
				sadComponentInstantiation.getUsageName(),
				getFeatureProvider(),ImageProvider.IMG_COMPONENT_INSTANCE,
				StyleUtil.getStyleForComponentInner(diagram));


		//add lollipop interface anchor to component.
		DiagramUtil.addLollipop(outerContainerShape, diagram, sadComponentInstantiation.getInterfaceStub(), getFeatureProvider());
		
		//add provides ports
		DiagramUtil.addProvidesPorts(outerContainerShape, diagram, sadComponentInstantiation.getProvides(), getFeatureProvider());

		//add uses ports
		DiagramUtil.addUsesPorts(outerContainerShape, diagram, sadComponentInstantiation.getUses(), getFeatureProvider());

		//Define size and location
		AreaContext areaContext = new AreaContext();
		areaContext.setLocation(context.getX(), context.getY());
		areaContext.setSize(DiagramUtil.getMinimumWidth(sadComponentInstantiation.getPlacement().getComponentFileRef().getFile().getSoftPkg().getName(),
				sadComponentInstantiation.getUsageName(), sadComponentInstantiation.getProvides(), sadComponentInstantiation.getUses(), diagram), DiagramUtil.getPreferredHeight(sadComponentInstantiation.getProvides(), sadComponentInstantiation.getUses()));
		
		//Size component (we are doing this so that we don't have to keep sizing/location information in both the add() and resize(), only resize())
		DiagramUtil.resizeOuterContainerShape(areaContext, outerContainerShape, 
				sadComponentInstantiation.getPlacement().getComponentFileRef().getFile().getSoftPkg().getName(),
				sadComponentInstantiation.getUsageName(), sadComponentInstantiation.getProvides(), sadComponentInstantiation.getUses());
		
		//layout
		layoutPictogramElement(outerContainerShape);

		return outerContainerShape;
	}
	
	@Override
	public boolean canLayout(ILayoutContext context){
		return super.canLayout(context);
	}
	
	@Override
	public boolean layout(ILayoutContext context){
		return super.layout(context);
	}
	
	public static Diagram findDiagram(ContainerShape containerShape){
		return Graphiti.getPeService().getDiagramForShape(containerShape);
	}
	
	public boolean canMoveShape(IMoveShapeContext context) {

		SadComponentInstantiation sadComponentInstantiation = 
				(SadComponentInstantiation)DiagramUtil.getBusinessObject(context.getPictogramElement());
		if(sadComponentInstantiation == null){
			return false;
		}
		
		//if moving to HostCollocation to Sad Partitioning
		if(context.getTargetContainer() instanceof Diagram ||
				DiagramUtil.getHostCollocation(context.getTargetContainer()) != null){
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
		SadComponentInstantiation sadComponentInstantiation = 
				(SadComponentInstantiation)DiagramUtil.getBusinessObject(context.getPictogramElement());

		final SoftwareAssembly sad = DiagramUtil.getDiagramSAD(getFeatureProvider(), getDiagram());
		
		//if moving inside the same container
		if(context.getSourceContainer() == context.getTargetContainer()){
			super.moveShape(context);
		}
		
		//if moving from HostCollocation to a different HostCollocation
		if(DiagramUtil.getHostCollocation(context.getSourceContainer()) != null &&
				DiagramUtil.getHostCollocation(context.getTargetContainer()) != null &&
				DiagramUtil.getHostCollocation(context.getSourceContainer()) != DiagramUtil.getHostCollocation(context.getTargetContainer())){
			//swap parents
			DiagramUtil.getHostCollocation(context.getSourceContainer()).getComponentPlacement().remove((SadComponentPlacement)sadComponentInstantiation.getPlacement());
			DiagramUtil.getHostCollocation(context.getTargetContainer()).getComponentPlacement().add((SadComponentPlacement)sadComponentInstantiation.getPlacement());
			super.moveShape(context);
		}
		
		//if moving to HostCollocation to Sad Partitioning
		if(DiagramUtil.getHostCollocation(context.getTargetContainer()) != null &&
				context.getSourceContainer() instanceof Diagram){
			//swap parents
			sad.getPartitioning().getComponentPlacement().remove(sadComponentInstantiation.getPlacement());
			DiagramUtil.getHostCollocation(context.getTargetContainer()).getComponentPlacement().add((SadComponentPlacement)sadComponentInstantiation.getPlacement());
			super.moveShape(context);
		}
		
		//if moving to Sad Partitioning from HostCollocation
		if(DiagramUtil.getHostCollocation(context.getSourceContainer()) != null &&
				context.getTargetContainer() instanceof Diagram){
			//swap parents
			sad.getPartitioning().getComponentPlacement().add((SadComponentPlacement)sadComponentInstantiation.getPlacement());
			DiagramUtil.getHostCollocation(context.getSourceContainer()).getComponentPlacement().remove((SadComponentPlacement)sadComponentInstantiation.getPlacement());
			super.moveShape(context);
		}

	}
}
