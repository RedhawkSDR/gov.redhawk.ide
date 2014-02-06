package gov.redhawk.ide.sad.graphiti.ui.diagram.patterns;

import gov.redhawk.ide.sad.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.sad.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.sad.graphiti.ui.diagram.providers.ImageProvider;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;
import mil.jpeojtrs.sca.partitioning.DomainFinder;
import mil.jpeojtrs.sca.partitioning.DomainFinderType;
import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.PartitioningFactory;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.examples.common.ExampleUtil;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.pattern.IPattern;

public class FindByEventChannelPattern extends AbstractFindByPattern implements IPattern{

	
	public static final String NAME = "Event Channel";
			
	public FindByEventChannelPattern(){
		super();
	}
	
	@Override
	public String getCreateName(){
		return NAME;
	}
	
	@Override
	public String getCreateDescription() {
		return "";
	}
	
	@Override
	public String getCreateImageId() {
		return ImageProvider.IMG_FIND_BY_DOMAIN_MANAGER;
	}
	
	
	//THE FOLLOWING METHOD DETERMINE IF PATTERN IS APPLICABLE TO OBJECT
	@Override
	public boolean isMainBusinessObjectApplicable(Object mainBusinessObject) {
		if(mainBusinessObject instanceof FindByStub){
			FindByStub findByStub = (FindByStub)mainBusinessObject;
			if(findByStub.getDomainFinder() != null && findByStub.getDomainFinder().getType().equals(DomainFinderType.EVENTCHANNEL)){
				return true;
			}
		}
		return false;
	}
	
	//DIAGRAM FEATURES
	
	@Override
	public boolean canCreate(ICreateContext context) {
		return context.getTargetContainer() instanceof Diagram;
	}
	@Override
	public Object[] create(ICreateContext context) {
		
		//prompt user for Event Channel
		final String eventChannel = ExampleUtil.askString("Find By Event Channel", "Enter Event Channel", "");
		
		final FindByStub[] findByStubs = new FindByStub[1];
		
		//editing domain for our transaction
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();
		
		//Create Component Related objects in SAD model
		TransactionalCommandStack stack = (TransactionalCommandStack)editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain){
			@Override
            protected void doExecute() {
				
				findByStubs[0] = PartitioningFactory.eINSTANCE.createFindByStub();
				
				//interface stub (lollipop)
				findByStubs[0].setInterface(PartitioningFactory.eINSTANCE.createComponentSupportedInterfaceStub());
				
				//domain finder service of type domain manager
				DomainFinder domainFinder = PartitioningFactory.eINSTANCE.createDomainFinder();
				domainFinder.setType(DomainFinderType.EVENTCHANNEL);
				domainFinder.setName(eventChannel);
				findByStubs[0].setDomainFinder(domainFinder);
				
				//add to diagram resource file
				getDiagram().eResource().getContents().add(findByStubs[0]);
				
			}
		});
		
		addGraphicalRepresentation(context, findByStubs[0]);
		
		return new Object[] { findByStubs[0] };
	}


	
	@Override
	public String checkValueValid(String value, IDirectEditingContext context){
		if (value.length() < 1){
			return "Please enter any text as event channel.";
		}
		if (value.contains(" ")){
			return "Spaces are not allowed in event channels.";
		}
		if (value.contains("\n")){
			return "Line breakes are not allowed in event channels.";
		}
		// null means, that the value is valid
		return null;
	}
	
	@Override
	public void setValue(final String value, IDirectEditingContext context){
		PictogramElement pe = context.getPictogramElement();
		RHContainerShape rhContainerShape = (RHContainerShape)DUtil.findContainerShapeParentWithProperty(
				pe, RHContainerShapeImpl.SHAPE_outerContainerShape);
		final FindByStub findBy = (FindByStub) getBusinessObjectForPictogramElement(rhContainerShape);
		
		//editing domain for our transaction
	    TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();
		
	    //Perform business object manipulation in a Command
	    TransactionalCommandStack stack = (TransactionalCommandStack)editingDomain.getCommandStack();
	    stack.execute(new RecordingCommand(editingDomain){
	    	@Override
	    	protected void doExecute() {
	    		//set event name
	    		findBy.getDomainFinder().setName(value);
	    	}
	    });
	    
	    //perform update, redraw
	    updatePictogramElement(rhContainerShape);
	}

	@Override
    public String getInnerTitle(FindByStub findByStub) {
		return findByStub.getDomainFinder().getName();
    }

}
