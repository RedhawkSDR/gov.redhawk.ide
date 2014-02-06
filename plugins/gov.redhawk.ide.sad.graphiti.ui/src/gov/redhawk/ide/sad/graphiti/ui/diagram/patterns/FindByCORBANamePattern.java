package gov.redhawk.ide.sad.graphiti.ui.diagram.patterns;

import gov.redhawk.ide.sad.graphiti.ui.diagram.providers.ImageProvider;
import gov.redhawk.ide.sad.graphiti.ui.diagram.wizards.FindByCORBANameWizardPage;
import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.NamingService;
import mil.jpeojtrs.sca.partitioning.PartitioningFactory;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.pattern.IPattern;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;

public class FindByCORBANamePattern extends AbstractFindByPattern implements IPattern{

	
	public static final String NAME = "Find By Name";
			
	public FindByCORBANamePattern(){
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
		return ImageProvider.IMG_FIND_BY_CORBA_NAME;
	}
	
	
	//THE FOLLOWING METHOD DETERMINE IF PATTERN IS APPLICABLE TO OBJECT
	@Override
	public boolean isMainBusinessObjectApplicable(Object mainBusinessObject) {
		if(mainBusinessObject instanceof FindByStub){
			FindByStub findByStub = (FindByStub)mainBusinessObject;
			if(findByStub.getNamingService() != null){
				return true;
			}
		}
		return false;
	}

	
	//DIAGRAM FEATURES
	@Override
	public Object[] create(ICreateContext context) {
		
		//prompt user for CORBA Name
		Wizard myWizard = new Wizard(){
            public boolean performFinish() { return true; }
		};
		FindByCORBANameWizardPage page = new FindByCORBANameWizardPage();
		myWizard.addPage(page);
		WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), myWizard);
		if(dialog.open() == WizardDialog.CANCEL){
			return null;
		}
		
		final String corbaNameText = page.getModel().getCorbaName();
		final String usesPortName = page.getModel().getEnableUsesPort() ? page.getModel().getUsesPortName() : "";
		final String providesPortName = page.getModel().getEnableProvidesPort() ? page.getModel().getProvidesPortName() : ""; 
		
		final FindByStub[] findByStubs = new FindByStub[1];
		
		//editing domain for our transaction
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramEditor().getEditingDomain();
//kepler		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();
		
		//Create Component Related objects in SAD model
		TransactionalCommandStack stack = (TransactionalCommandStack)editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain){
			@Override
            protected void doExecute() {
				
				findByStubs[0] = PartitioningFactory.eINSTANCE.createFindByStub();
				
				//interface stub (lollipop)
				findByStubs[0].setInterface(PartitioningFactory.eINSTANCE.createComponentSupportedInterfaceStub());
				
				//naming service (corba name)
				NamingService namingService = PartitioningFactory.eINSTANCE.createNamingService();
				namingService.setName(corbaNameText);
				findByStubs[0].setNamingService(namingService);
				
				//if applicable add uses port stub
				if(!usesPortName.isEmpty()){
					UsesPortStub usesPortStub = PartitioningFactory.eINSTANCE.createUsesPortStub();
					usesPortStub.setName(usesPortName);
					findByStubs[0].getUses().add(usesPortStub);
				}
				
				//if applicable add provides port stub
				if(!providesPortName.isEmpty()){
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
    public String getInnerTitle(FindByStub findByStub) {
	    return findByStub.getNamingService().getName();
    }

	@Override
    public String checkValueValid(String value, IDirectEditingContext context) {
	    return null;
    }

	@Override
    public void setValue(String value, IDirectEditingContext context) {
    }
}
