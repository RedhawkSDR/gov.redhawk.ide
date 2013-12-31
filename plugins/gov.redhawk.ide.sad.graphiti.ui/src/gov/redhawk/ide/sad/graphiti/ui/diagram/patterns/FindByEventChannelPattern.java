package gov.redhawk.ide.sad.graphiti.ui.diagram.patterns;

import gov.redhawk.ide.sad.graphiti.ui.diagram.providers.ImageProvider;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DiagramUtil;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.StyleUtil;
import mil.jpeojtrs.sca.partitioning.DomainFinder;
import mil.jpeojtrs.sca.partitioning.DomainFinderType;
import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.PartitioningFactory;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.examples.common.ExampleUtil;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.impl.AreaContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.pattern.AbstractPattern;
import org.eclipse.graphiti.pattern.IPattern;

public class FindByEventChannelPattern extends AbstractPattern implements IPattern{

	
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
	
	
	//THE FOLLOWING THREE METHODS DETERMINE IF PATTERN IS APPLICABLE TO OBJECT
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
		if (context.getNewObject() instanceof FindByStub) {
			if (context.getTargetContainer() instanceof Diagram) {
					return true;
			}
		}
		return false;
	}
	
	@Override
	public PictogramElement add(IAddContext context) {
		FindByStub findByStub = (FindByStub) context.getNewObject();
		Diagram diagram = (Diagram) context.getTargetContainer();
		
		String title = findByStub.getDomainFinder().getName();
		
		//OUTER RECTANGLE
		ContainerShape outerContainerShape = 
				DiagramUtil.addOuterRectangle(diagram, 
						NAME, 
						findByStub, getFeatureProvider(), ImageProvider.IMG_FIND_BY,
						StyleUtil.getStyleForFindByOuter(diagram));

		//INNER RECTANGLE
		DiagramUtil.addInnerRectangle(diagram,
				outerContainerShape,
				title,
				getFeatureProvider(), getCreateImageId(),
				StyleUtil.getStyleForFindByInner(diagram));
		

		//add lollipop interface anchor to shape.
		DiagramUtil.addLollipop(outerContainerShape, diagram, findByStub.getInterface(), getFeatureProvider());
	
		//Define size and location
		AreaContext areaContext = new AreaContext();
		areaContext.setLocation(context.getX(), context.getY());
		areaContext.setSize(DiagramUtil.getMinimumWidth(NAME, title, findByStub.getProvides(), findByStub.getUses(), diagram), DiagramUtil.getPreferredHeight(findByStub.getProvides(), findByStub.getUses()));
		
		//Size component (we are doing this so that we don't have to keep sizing/location information in both the add() and resize(), only resize())
		DiagramUtil.resizeOuterContainerShape(areaContext, outerContainerShape, NAME, title, findByStub.getProvides(), findByStub.getUses());
		
		//layout
		layoutPictogramElement(outerContainerShape);

		return outerContainerShape;
	}
	
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
	
	
	/**
	 * Resize Component
	 */
	@Override
	public void resizeShape(IResizeShapeContext context) {
		
		FindByStub findByStub = (FindByStub)getFeatureProvider().getBusinessObjectForPictogramElement(context.getPictogramElement());
		if(findByStub == null){
			return;
		}
		
		//resize component
		DiagramUtil.resizeOuterContainerShape(context, context.getPictogramElement(), NAME,
				findByStub.getDomainFinder().getName(),
				findByStub.getProvides(), findByStub.getUses());
	}
	
	/**
	 * Resizing a Component shape is always allowed
	 */
	@Override
	public boolean canResizeShape(IResizeShapeContext context){
		return true;
	}

}
