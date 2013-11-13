package gov.redhawk.ide.sad.graphiti.ui.diagram.features.add;

import gov.redhawk.diagram.activator.PluginActivator;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DiagramUtil;
import gov.redhawk.sca.util.PluginUtil;
import mil.jpeojtrs.sca.partitioning.ComponentFile;
import mil.jpeojtrs.sca.partitioning.ComponentFileRef;
import mil.jpeojtrs.sca.partitioning.ComponentFiles;
import mil.jpeojtrs.sca.partitioning.NamingService;
import mil.jpeojtrs.sca.partitioning.PartitioningFactory;
import mil.jpeojtrs.sca.sad.FindComponent;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadComponentPlacement;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SadPartitioning;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.ui.statushandlers.StatusManager;

public class CreateComponentFeature extends AbstractCreateFeature{

	private SoftPkg spd = null;
	
	public CreateComponentFeature(IFeatureProvider fp, final SoftPkg spd) {
	    super(fp, spd.getName(), spd.getDescription());
	    this.spd = spd;
    }

	@Override
	public boolean canCreate(ICreateContext context) {
		return context.getTargetContainer() instanceof Diagram;
	}
	@Override
	public Object[] create(ICreateContext context) {
		
		//editing domain for our transaction
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();
		
		//get sad from diagram
		final SoftwareAssembly sad = DiagramUtil.getDiagramSAD(getFeatureProvider(), getDiagram());
		
		if (spd == null) {
			//TODO: return some kind of error
		}
		
		//container for new component instantiation, necessary for reference after command execution
		final SadComponentInstantiation[] componentInstantiations = new SadComponentInstantiation[1];
		
		//Create Component Related objects in SAD model
		TransactionalCommandStack stack = (TransactionalCommandStack)editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain){
			@Override
            protected void doExecute() {
				//add component file
				ComponentFile componentFile = createComponentFile(sad, spd);
				
				//component placement
				SadComponentPlacement componentPlacement = createComponentPlacement(sad);
				
				//component instantiation
				componentInstantiations[0] = createComponentInstantiation(sad, componentPlacement, spd);
				
				//create component file ref
				final ComponentFileRef ref = PartitioningFactory.eINSTANCE.createComponentFileRef();
				ref.setFile(componentFile);
				componentPlacement.setComponentFileRef(ref);
            }
		});

		//call add feature
		addGraphicalRepresentation(context, componentInstantiations[0]);
		
		return new Object[] { componentInstantiations[0]};
	}
	
	
	
	
	
	//adds corresponding component file to sad if not already present
	private ComponentFile createComponentFile(final SoftwareAssembly sad, final SoftPkg spd) {

		// See if we have to add a new <componentfile>
		ComponentFile file = null;
		//set component files is not already set
		ComponentFiles cFiles = sad.getComponentFiles();
		if(cFiles == null){
			cFiles = PartitioningFactory.eINSTANCE.createComponentFiles();
			sad.setComponentFiles(cFiles);
		}
		//search for existing compatible component file for spd
		for (final ComponentFile f : cFiles.getComponentFile()) {
			if (f == null) {
				continue;  //TODO: why would this happen
			}
			final SoftPkg fSpd = f.getSoftPkg();
			if (fSpd != null && PluginUtil.equals(spd.getId(), fSpd.getId())) {
				file = f;
				break;
			}
		}
		//add new component file if not found above
		if (file == null) {
			file = SadFactory.eINSTANCE.createComponentFile();
			cFiles.getComponentFile().add(file);
			file.setSoftPkg(spd);
		}

		return file;
	}
	
	//create ComponentInstantiation 
	private SadComponentInstantiation createComponentInstantiation(final SoftwareAssembly sad, final SadComponentPlacement componentPlacement, final SoftPkg spd) {
		
		SadComponentInstantiation sadComponentInstantiation = SadFactory.eINSTANCE.createSadComponentInstantiation();
	
		String compName = SoftwareAssembly.Util.createComponentUsageName(sad, spd.getName());
		String id = SoftwareAssembly.Util.createComponentIdentifier(sad, compName);
		
		sadComponentInstantiation.setUsageName(compName);
		sadComponentInstantiation.setId(id);
		
		final FindComponent findComponent = SadFactory.eINSTANCE.createFindComponent();
		final NamingService namingService = PartitioningFactory.eINSTANCE.createNamingService();
		namingService.setName(compName);
		findComponent.setNamingService(namingService);
		sadComponentInstantiation.setFindComponent(findComponent);
		
		String implId = null;
		if (!spd.getImplementation().isEmpty()) { // Panic! Just choose first implementation
			implId = spd.getImplementation().get(0).getId();
		} else {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, PluginActivator.ID,
					spd.getName() + " Component has no implementation. ID: " + spd.getId()),
					StatusManager.LOG | StatusManager.SHOW);
			//return CommandResult.newErrorCommandResult("No SPD implementation available for " + spd.getName());
		}
		sadComponentInstantiation.setImplID(implId);
		
		//add to placement
		componentPlacement.getComponentInstantiation().add(sadComponentInstantiation);
	
		return sadComponentInstantiation;
	}
	
	/**
	 * Creates SADComponentPlacement in the SoftwareAssembly.  This needs to also handle Collocation and currently doesn't...see comment in method
	 * @param sad
	 * @param spd
	 * @return
	 */
	private SadComponentPlacement createComponentPlacement(final SoftwareAssembly sad){

		final SadComponentPlacement componentPlacement = SadFactory.eINSTANCE.createSadComponentPlacement();

		SadPartitioning sadPartitioning = sad.getPartitioning();
		if (sadPartitioning == null) {
			sadPartitioning = SadFactory.eINSTANCE.createSadPartitioning();
			sad.setPartitioning(sadPartitioning);
		}
		sadPartitioning.getComponentPlacement().add(componentPlacement);
		//TODO: we need to handle Collocation (this involves looking at the AddContext targetContainer
		//RIght now for simplicity we are just assuming add directly to SAD
		//		} else if (element instanceof HostCollocation) {
		//			final HostCollocation owner = (HostCollocation) element;
		//			owner.getComponentPlacement().add(newElement);
		//		}

		return componentPlacement;
	}

}
