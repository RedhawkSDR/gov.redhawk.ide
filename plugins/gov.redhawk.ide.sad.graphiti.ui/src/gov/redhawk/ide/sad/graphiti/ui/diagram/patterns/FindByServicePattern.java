package gov.redhawk.ide.sad.graphiti.ui.diagram.patterns;

import gov.redhawk.ide.sad.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.sad.graphiti.ext.RHGxFactory;
import gov.redhawk.ide.sad.graphiti.ui.diagram.providers.ImageProvider;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;
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
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.pattern.AbstractPattern;
import org.eclipse.graphiti.pattern.IPattern;
import org.eclipse.graphiti.services.Graphiti;
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
		ContainerShape targetContainerShape = (ContainerShape) context.getTargetContainer();
		Diagram diagram = (Diagram) context.getTargetContainer();
		
		//service name/type
		String displayInnerText = findByStub.getDomainFinder().getName();
		String displayOuterText = getOuterText(findByStub);
		
		//create shape
		RHContainerShape rhContainerShape = RHGxFactory.eINSTANCE.createRHContainerShape();

		//initialize shape contents
		rhContainerShape.init(targetContainerShape, displayOuterText, 
				findByStub, getFeatureProvider(), ImageProvider.IMG_FIND_BY,
				StyleUtil.getStyleForFindByOuter(diagram), displayInnerText,
				getCreateImageId(), StyleUtil.getStyleForFindByInner(diagram), 
				findByStub.getInterface(), findByStub.getUses(), findByStub.getProvides(), null);

		//set shape location to user's selection
		Graphiti.getGaLayoutService().setLocation(rhContainerShape.getGraphicsAlgorithm(), 
				context.getX(), context.getY());

		//layout
		layoutPictogramElement(rhContainerShape);

		return rhContainerShape;

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
	
	@Override
	public boolean canResizeShape(IResizeShapeContext context){
		return true;
	}
	
	@Override
	public boolean canLayout(ILayoutContext context){
		ContainerShape containerShape = (ContainerShape) context.getPictogramElement();
		Object obj = DUtil.getBusinessObject(containerShape);
		if(obj instanceof FindByStub){
			return true;
		}
		return false;
	}
	
	/**
	 * Layout children of component
	 */
	@Override
	public boolean layout(ILayoutContext context){
		((RHContainerShape)context.getPictogramElement()).layout();
		
		//something is always changing.
        return true;
	}
	
	private String getOuterText(FindByStub findByStub){
		//service name/type
		String displayOuterText = "";
		if(findByStub.getDomainFinder().getType().equals(DomainFinderType.SERVICENAME)){
			displayOuterText = NAME + " Name";
		}else if(findByStub.getDomainFinder().getType().equals(DomainFinderType.SERVICETYPE)){
			displayOuterText = NAME + " Type";
		}
		return displayOuterText;
	}
}
