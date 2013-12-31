package gov.redhawk.ide.sad.graphiti.ui.diagram.patterns;

import gov.redhawk.ide.sad.graphiti.ui.diagram.providers.ImageProvider;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DiagramUtil;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.StyleUtil;
import gov.redhawk.ide.sad.graphiti.ui.diagram.wizards.FindByServiceWizardPage;
import mil.jpeojtrs.sca.partitioning.DomainFinder;
import mil.jpeojtrs.sca.partitioning.DomainFinderType;
import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.PartitioningFactory;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.impl.AreaContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.pattern.AbstractPattern;
import org.eclipse.graphiti.pattern.IPattern;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;

public class FindByServicePattern extends AbstractPattern implements IPattern{

	
	public static final String NAME = "Service";
	public static final String FIND_BY_SERVICE_NAME = "Service Name";
			
	public FindByServicePattern(){
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
		return ImageProvider.IMG_FIND_BY_SERVICE;
	}
	
	
	//THE FOLLOWING THREE METHODS DETERMINE IF PATTERN IS APPLICABLE TO OBJECT
	@Override
	public boolean isMainBusinessObjectApplicable(Object mainBusinessObject) {
		if(mainBusinessObject instanceof FindByStub){
			FindByStub findByStub = (FindByStub)mainBusinessObject;
			if(findByStub.getDomainFinder() != null && 
					(findByStub.getDomainFinder().getType().equals(DomainFinderType.SERVICENAME) || 
							findByStub.getDomainFinder().getType().equals(DomainFinderType.SERVICETYPE))){
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
		
		//service name/type
		String displayInnerText = findByStub.getDomainFinder().getName();
		String displayOuterText = "";
		if(findByStub.getDomainFinder().getType().equals(DomainFinderType.SERVICENAME)){
			displayOuterText = NAME + " Name";
		}else if(findByStub.getDomainFinder().getType().equals(DomainFinderType.SERVICETYPE)){
			displayOuterText = NAME + " Type";
		}
		
		
		
		//OUTER RECTANGLE
		ContainerShape outerContainerShape = 
				DiagramUtil.addOuterRectangle(diagram, 
						displayOuterText, 
						findByStub, getFeatureProvider(), ImageProvider.IMG_FIND_BY,
						StyleUtil.getStyleForFindByOuter(diagram));

		//INNER RECTANGLE
		DiagramUtil.addInnerRectangle(diagram,
				outerContainerShape,
				displayInnerText,
				getFeatureProvider(), getCreateImageId(),
				StyleUtil.getStyleForFindByInner(diagram));
		

		//add lollipop interface anchor to shape.
		DiagramUtil.addLollipop(outerContainerShape, diagram, findByStub.getInterface(), getFeatureProvider());
		
		//add provides ports
		DiagramUtil.addProvidesPorts(outerContainerShape, diagram, findByStub.getProvides(), getFeatureProvider());

		//add uses ports
		DiagramUtil.addUsesPorts(outerContainerShape, diagram, findByStub.getUses(), getFeatureProvider());

		//Define size and location
		AreaContext areaContext = new AreaContext();
		areaContext.setLocation(context.getX(), context.getY());
		areaContext.setSize(DiagramUtil.getMinimumWidth(FIND_BY_SERVICE_NAME, displayInnerText, findByStub.getProvides(), findByStub.getUses(), diagram), DiagramUtil.getPreferredHeight(findByStub.getProvides(), findByStub.getUses()));
		
		//Size component (we are doing this so that we don't have to keep sizing/location information in both the add() and resize(), only resize())
		DiagramUtil.resizeOuterContainerShape(areaContext, outerContainerShape, displayOuterText, displayInnerText, findByStub.getProvides(), findByStub.getUses());
		
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
		
		//prompt user for Service information
		Wizard myWizard = new Wizard(){
            public boolean performFinish() { return true; }
		};
		FindByServiceWizardPage page = new FindByServiceWizardPage();
		myWizard.addPage(page);
		WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), myWizard);
		if(dialog.open() == WizardDialog.CANCEL){
			return null;
		}
		
		//get user selections
		final String serviceNameText = page.getModel().getEnableServiceName() ? page.getModel().getServiceName() : null;
		final String serviceTypeText = page.getModel().getEnableServiceType() ? page.getModel().getServiceType() : null;
		final String usesPortName = page.getModel().getEnableUsesPort() ? page.getModel().getUsesPortName() : null;
		final String providesPortName = page.getModel().getEnableProvidesPort() ? page.getModel().getProvidesPortName() : null; 
		
		//create new business object
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
				findByStubs[0].setDomainFinder(domainFinder);
				if(serviceNameText != null && !serviceNameText.isEmpty()){
					domainFinder.setType(DomainFinderType.SERVICENAME);
					domainFinder.setName(serviceNameText);
				}else if(serviceTypeText != null && !serviceTypeText.isEmpty()){
					domainFinder.setType(DomainFinderType.SERVICETYPE);
					domainFinder.setName(serviceTypeText);
				}
				
				//if applicable add uses port stub
				if(usesPortName != null && !usesPortName.isEmpty()){
					UsesPortStub usesPortStub = PartitioningFactory.eINSTANCE.createUsesPortStub();
					usesPortStub.setName(usesPortName);
					findByStubs[0].getUses().add(usesPortStub);
				}
				
				//if applicable add provides port stub
				if(providesPortName != null && !providesPortName.isEmpty()){
					ProvidesPortStub providesPortStub = PartitioningFactory.eINSTANCE.createProvidesPortStub();
					providesPortStub.setName(providesPortName);
					findByStubs[0].getProvides().add(providesPortStub);
				}
				
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
		DiagramUtil.resizeOuterContainerShape(context, context.getPictogramElement(), FIND_BY_SERVICE_NAME,
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
