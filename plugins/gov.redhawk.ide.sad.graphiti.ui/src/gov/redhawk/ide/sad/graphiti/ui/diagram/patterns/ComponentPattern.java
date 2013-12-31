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
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.impl.AreaContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.pattern.AbstractPattern;
import org.eclipse.graphiti.pattern.IPattern;

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
			if (context.getTargetContainer() instanceof Diagram) {
				return true;
			}
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

		if(context.getPictogramElement() != null && 
				context.getPictogramElement().getLink() != null && 
				context.getPictogramElement().getLink().getBusinessObjects() != null)
		{
			for(EObject eObject: context.getPictogramElement().getLink().getBusinessObjects()){
				if(eObject instanceof SadComponentInstantiation){
					return true;
				}
			}
		}

		return false;
	}
	
	/**
	 * Delete the SadComponentInstantiation linked to the PictogramElement.  
	 */
	@Override
	public void delete(IDeleteContext context){
		
		//start working here, this doesn't work yet
		
		//set componentToDelete
		SadComponentInstantiation ciNonFinal = null;
		for(EObject eObject: context.getPictogramElement().getLink().getBusinessObjects()){
			if(eObject instanceof SadComponentInstantiation){
				ciNonFinal = (SadComponentInstantiation)eObject;
				break;
			}
		}
		final SadComponentInstantiation ciToDelete = ciNonFinal; //needs to be "final"
		
		//editing domain for our transaction
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();
		
		//get sad from diagram
		final SoftwareAssembly sad = DiagramUtil.getDiagramSAD(getFeatureProvider(), getDiagram());
		
		//Create Component Related objects in SAD model
		TransactionalCommandStack stack = (TransactionalCommandStack)editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain){
			@Override
            protected void doExecute() {
				
				//assembly controller may reference componentInstantiation
				//delete reference if applicable
				if(sad.getAssemblyController() != null &&
						sad.getAssemblyController().getComponentInstantiationRef() != null &&
						sad.getAssemblyController().getComponentInstantiationRef().getInstantiation().equals(ciToDelete)){
					//TODO: how should this be handled? We need to test this out
					EcoreUtil.delete(sad.getAssemblyController().getComponentInstantiationRef());
					sad.getAssemblyController().setComponentInstantiationRef(null);
				}
				
				//get placement for instantiation and delete it from sad partitioning after we look at removing the component file ref.
				SadComponentPlacement placement = (SadComponentPlacement)ciToDelete.getPlacement();
				
				//find and remove any attached connections
				//gather connections
				List<SadConnectInterface> connectionsToRemove = new ArrayList<SadConnectInterface>();
				if(sad.getConnections() != null){
					for(SadConnectInterface connectionInterface: sad.getConnections().getConnectInterface()){
						//we need to do thorough null checks here because of the many connection possibilities.  Firstly a connection requires only a usesPort and either (providesPort || componentSupportedInterface) 
						//and therefore null checks need to be performed.
						//FindBy connections don't have ComponentInstantiationRefs and so they can also be null
						if((connectionInterface.getComponentSupportedInterface() != null && connectionInterface.getComponentSupportedInterface().getComponentInstantiationRef() != null && ciToDelete.getId().equals(connectionInterface.getComponentSupportedInterface().getComponentInstantiationRef().getRefid())) ||
								(connectionInterface.getUsesPort() != null && connectionInterface.getUsesPort().getComponentInstantiationRef() != null && ciToDelete.getId().equals(connectionInterface.getUsesPort().getComponentInstantiationRef().getRefid())) ||
								(connectionInterface.getProvidesPort() != null && connectionInterface.getProvidesPort().getComponentInstantiationRef() != null && ciToDelete.getId().equals(connectionInterface.getProvidesPort().getComponentInstantiationRef().getRefid()))){
							connectionsToRemove.add(connectionInterface);
						}
					}
				}
				//remove gathered connections
				if(sad.getConnections() != null){
					sad.getConnections().getConnectInterface().removeAll(connectionsToRemove);
				}
				
				//delete component file if applicable
				//figure out which component file we are using and if no other component placements using it then remove it.
				ComponentFile componentFileToRemove = placement.getComponentFileRef().getFile();
				for(SadComponentPlacement p: sad.getPartitioning().getComponentPlacement()){
					if(p != placement && p.getComponentFileRef().getRefid().equals(placement.getComponentFileRef().getRefid())){
						componentFileToRemove = null;
					}
				}
				if(componentFileToRemove != null){
					sad.getComponentFiles().getComponentFile().remove(componentFileToRemove);
				}
				
				//delete component placement
				sad.getPartitioning().getComponentPlacement().remove(placement);
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
		Diagram diagram = (Diagram) context.getTargetContainer();
		
		//OUTER RECTANGLE
		ContainerShape outerContainerShape = 
				DiagramUtil.addOuterRectangle(diagram, 
						sadComponentInstantiation.getPlacement().getComponentFileRef().getFile().getSoftPkg().getName(), 
						sadComponentInstantiation, getFeatureProvider(),
						ImageProvider.IMG_COMPONENT_PLACEMENT,
						StyleUtil.getStyleForComponentOuter(diagram));

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
	
}
