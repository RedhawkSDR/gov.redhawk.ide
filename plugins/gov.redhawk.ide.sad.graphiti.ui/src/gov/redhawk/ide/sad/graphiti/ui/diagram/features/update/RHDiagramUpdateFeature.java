package gov.redhawk.ide.sad.graphiti.ui.diagram.features.update;

import gov.redhawk.ide.sad.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.sad.graphiti.ui.SADUIGraphitiPlugin;
import gov.redhawk.ide.sad.graphiti.ui.diagram.features.layout.ZestLayoutDiagramFeature;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.AbstractFindByPattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.ComponentPattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.HostCollocationPattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mil.jpeojtrs.sca.partitioning.ComponentInstantiation;
import mil.jpeojtrs.sca.partitioning.FindBy;
import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.HostCollocation;
import mil.jpeojtrs.sca.sad.SadComponentPlacement;
import mil.jpeojtrs.sca.sad.SadConnectInterface;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.DefaultUpdateDiagramFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.ui.services.GraphitiUi;

public class RHDiagramUpdateFeature extends DefaultUpdateDiagramFeature {

	public RHDiagramUpdateFeature(IFeatureProvider fp) {
	    super(fp);
    }
	
	

	/**
	 * Updates the Diagram to reflect the underlying business model
	 * Make sure all elements in sad model (hosts/components/findby) are accounted for as 
	 * children of diagram, if they aren't then add them, if they are then check to see if
	 * they need to be updated, if they exist in the diagram yet not in the model, remove them
	 * @param context
	 * @param performUpdate
	 * @return
	 * @throws CoreException 
	 */
	@SuppressWarnings("unchecked")
    public Reason internalUpdate(IUpdateContext context, boolean performUpdate) throws CoreException{
		
		boolean updateStatus = false;
		
		PictogramElement pe = context.getPictogramElement();
		if (pe instanceof Diagram) {
			Diagram d = (Diagram) pe;
			
			//get sad from diagram
			final SoftwareAssembly sad = DUtil.getDiagramSAD(getFeatureProvider(), getDiagram());
			
			//TODO: ensure our SAD has an assembly controller
			//set one if necessary, why bother the user?
			
			//HostCollocation update shapes, remove old shapes, add new shapes
			List<HostCollocation> hostCollocations = new ArrayList<HostCollocation>();
			if(sad != null && sad.getPartitioning() != null && sad.getPartitioning().getHostCollocation() != null){
				//Elist -> List
				Collections.addAll(hostCollocations, (HostCollocation[])sad.getPartitioning().getHostCollocation().toArray( new HostCollocation[0]));
			}
			Reason updateShapesReason = 
					DUtil.addRemoveUpdateShapes((List<Shape>)(List<?>)HostCollocationPattern.getHostCollocationContainerShapes(d), 
							(List<EObject>)(List<?>)hostCollocations, (Class)HostCollocation.class,
							"Host", getDiagram(), getFeatureProvider(), performUpdate);
			if(!performUpdate && updateShapesReason.toBoolean()){
				return updateShapesReason;
			}else if(updateShapesReason.toBoolean() == true){
				updateStatus = true;
			}


			//Component update shapes, remove old shapes, add new shapes
			List<EObject> componentInstatiations = new ArrayList<EObject>();
			if(sad != null && sad.getPartitioning() != null && sad.getPartitioning().getComponentPlacement() != null){
				//Get list of componentInstantiations from model
				for(SadComponentPlacement p: sad.getPartitioning().getComponentPlacement()){
					Collections.addAll(componentInstatiations, (EObject[]) p.getComponentInstantiation().toArray(new EObject[0]));
				}
			}
			updateShapesReason = 
					DUtil.addRemoveUpdateShapes((List<Shape>)(List<?>)ComponentPattern.getAllComponentShapes(d), 
							componentInstatiations, (Class)ComponentInstantiation.class,
							"Host", getDiagram(), getFeatureProvider(), performUpdate);
			if(!performUpdate && updateShapesReason.toBoolean()){
				return updateShapesReason;
			}else if(updateShapesReason.toBoolean() == true){
				updateStatus = true;
			}

			
			//Update connections, remove old connections, add new connections
			List<SadConnectInterface> sadConnectInterfaces = new ArrayList<SadConnectInterface>();
			if(sad != null && sad.getConnections() != null && sad.getConnections().getConnectInterface() != null){
				//Get list of SadConnectInterfaces from model
				Collections.addAll(sadConnectInterfaces, (SadConnectInterface[]) sad.getConnections().getConnectInterface().toArray(new SadConnectInterface[0]));
			}
			//diagram connections EList->List
			List<Connection> connections = new ArrayList<Connection>();
			Collections.addAll(connections, (Connection[])d.getConnections().toArray(new Connection[0]));
			updateShapesReason = 
					addRemoveUpdateConnections(connections, 
							sadConnectInterfaces, (Class)SadConnectInterface.class,
							"Connection", getDiagram(), getFeatureProvider(), performUpdate);
			if(!performUpdate){
				return updateShapesReason;
			}else if(updateShapesReason.toBoolean() == true){
				updateStatus = true;
			}
		
		}
		
		if(updateStatus && performUpdate){
			return new Reason(true, "Update successful");
		}
		  
		return new Reason(false, "No updates required");
	}
	
	
	@Override
	public Reason updateNeeded(IUpdateContext context) {
		try {
	        return internalUpdate(context, false);
        } catch (CoreException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
		return null;
	}
	
	@Override
	public boolean update(IUpdateContext context) {
		Reason reason;
        try {
	        reason = internalUpdate(context, true);
	        reason.toBoolean();
	        
	        if(reason.toBoolean()){
	        	//if we changed something lets layout the diagram
	        	//TODO: THIS IS NOT THE RIGHT WAY, JUST DO IT TO GET SOMETHING WORKING
//	        	ZestLayoutDiagramFeature layoutFeature = new ZestLayoutDiagramFeature(getFeatureProvider());
//	        	layoutFeature.execute(null);
	        }
	        
        } catch (CoreException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
		
		
		return false;
	}
	
	/**
	 * Inspect all findby shapes that must be present due to their use in connections
	 * @return
	 */
	public static Reason addRemoveUpdateFindBy(List<Connection> connections, List<SadConnectInterface> sadConnectInterfaces, 
			Diagram diagram, IFeatureProvider featureProvider, boolean performUpdate){
		boolean updateStatus = false;

		//contains all the findByStubs that should exist in the diagram
		ArrayList<FindByStub> findByStubs = new ArrayList<FindByStub>();
		
		//populate list of findByStubs with existing instances from diagram
		List<RHContainerShape> findByShapes = AbstractFindByPattern.getAllFindByShapes(diagram);
		for(RHContainerShape findByShape: findByShapes){
			findByStubs.add((FindByStub)DUtil.getBusinessObject(findByShape));
		}

		//look for findby in connections to add
		for(SadConnectInterface sadConnectInterface: sadConnectInterfaces){

			//FindBy is always used inside usesPort
			if(sadConnectInterface.getUsesPort() != null &&
					sadConnectInterface.getUsesPort().getFindBy() != null)
			{

				//get FindBy model object
				FindBy findBy = (FindBy)sadConnectInterface.getUsesPort().getFindBy();

				//search for findByStub in the list
				FindByStub findByStub = findFindByStub(findBy, findByStubs);

				//does findBy exist in diagram already?
				if(findByStub == null){
					if(performUpdate){

						updateStatus = true;

						//create FindBy Shape for Source
						findByStub = AbstractFindByPattern.createFindByStub(findBy, featureProvider, diagram);

						//add to list
						findByStubs.add(findByStub);

					}else{
						return new Reason(true, "Add FindBy Shape");
					}
				}
				//add provides port to stub if doesn't already exist
				boolean uPFound = false;
				for(UsesPortStub p: findByStub.getUses()){
					if(p.equals(sadConnectInterface.getUsesPort())){
						uPFound = true;
					}
				}
				if(!uPFound){
					if(performUpdate){
						updateStatus = true;
						//add the required usesPort
						AbstractFindByPattern.addUsesPortStubToFindByStub(findByStub, sadConnectInterface.getUsesPort(), featureProvider);
					}else{
						return new Reason(true, "Add Uses Port to FindBy");
					}
				}
			}

			//lookup Target Anchor
			//sadConnectInterface.getComponentSupportedInterface().getFindBy()
			if(sadConnectInterface.getComponentSupportedInterface() != null &&
					sadConnectInterface.getComponentSupportedInterface().getSupportedIdentifier() != null &&
					sadConnectInterface.getComponentSupportedInterface().getFindBy() != null){

				//The model provides us with interface information for the FindBy we are connecting to
				FindBy findBy = (FindBy)sadConnectInterface.getComponentSupportedInterface().getFindBy();

				//search for findByStub in the list
				FindByStub findByStub = findFindByStub(findBy, findByStubs);

				if(findByStub == null){
					if(performUpdate){

						updateStatus = true;

						//create findByStub
						findByStub = AbstractFindByPattern.createFindByStub(findBy, featureProvider, diagram);

						//add to list
						findByStubs.add(findByStub);
					}else{
						return new Reason(true, "Add FindBy Shape");
					}
				}

				//findBy nested in ProvidesPort
			}else if(sadConnectInterface.getProvidesPort() != null &&
					sadConnectInterface.getProvidesPort().getFindBy() != null){


				FindBy findBy = (FindBy)sadConnectInterface.getProvidesPort().getFindBy();

				//search for findByStub in the list
				FindByStub findByStub = findFindByStub(findBy, findByStubs);

				//does findBy exist in diagram already?
				if(findByStub == null){
					if(performUpdate){
						updateStatus = true;
						
						//create findByStub
						findByStub = AbstractFindByPattern.createFindByStub(findBy, featureProvider, diagram);
						
						//add to list
						findByStubs.add(findByStub);
					}else{
						return new Reason(true, "Add FindBy Shape");
					}
				}
				
				//add provides port to stub if doesn't already exist
				boolean ppFound = false;
				for(ProvidesPortStub p: findByStub.getProvides()){
					if(p.equals(sadConnectInterface.getProvidesPort())){
						ppFound = true;
					}
				}
				if(!ppFound){
					if(performUpdate){
						updateStatus = true;
					//add the required providesPort
					AbstractFindByPattern.addProvidesPortStubToFindByStub(findByStub, sadConnectInterface.getProvidesPort(), featureProvider);
					}else{
						return new Reason(true, "Add Provides Port to FindBy");
					}
				}
			}
		}
		
		//TODO: Should we do this, I don't think it hurts to leave them.
		//get all FindByStub(s) in diagram and remove any that connections aren't using
		
		//TODO: Should we be removing the ports on the FindBy automatically if they arent's used
		//I don't see the harm in leaving them.  User could "edit" the FindByShape if they really wanted to remove them
		
		//add new FindByStub(s), update existing FindByStub
		for(FindByStub fbs: findByStubs){
			List<PictogramElement> elements = GraphitiUi.getLinkService().getPictogramElements(diagram, fbs);
			if(elements == null || elements.size() < 1){
				DUtil.addShapeViaFeature(featureProvider, diagram, fbs);
			}else{
				DUtil.updateShapeViaFeature(featureProvider, diagram, elements.get(0));
			}
		}
		
		if(updateStatus && performUpdate){
			return new Reason(true, "Update successful");
		}
		
		return new Reason(false, "No updates required");
	}
	
	/**
	 * Examines a list of Connections  and ensures there is an associated object in the model (objects).
	 * If the Connection has an associated object than it is updated (if necessary) otherwise the Connection is removed
	 * Next if there are new objects that do not have Connections, then a new Connection is added using
	 * the features associated the provided object type.
	 * @param connections
	 * @param sadConnectInterfaces
	 * @param pictogramLabel
	 * @param featureProvider
	 * @param performUpdate
	 * @return
	 * @throws CoreException 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
    public static Reason addRemoveUpdateConnections(List<Connection> connections, List<SadConnectInterface> sadConnectInterfaces, Class objectClass, 
    		String pictogramLabel, Diagram diagram, IFeatureProvider featureProvider, boolean performUpdate) throws CoreException{
		
		boolean updateStatus = false;
		
		//remove Connections on diagram if no longer in model, update all other Connections if necessary
		Reason updateConnectionsReason = DUtil.removeUpdatePictogramElement((List<PictogramElement>)(List<?>)connections, (List<EObject>)(List<?>)sadConnectInterfaces, objectClass, 
	    		pictogramLabel, featureProvider, performUpdate);
		if(!performUpdate && updateConnectionsReason.toBoolean()){
			return updateConnectionsReason;
		}else if(updateConnectionsReason.toBoolean() == true){
			updateStatus = true;
		}
		
		//correct unfriendly findByConnections
		updateConnectionsReason = DUtil.replaceDirectFindByConnection(sadConnectInterfaces, objectClass, pictogramLabel, diagram, featureProvider, performUpdate);
		if(!performUpdate && updateConnectionsReason.toBoolean()){
			return updateConnectionsReason;
		}else if(updateConnectionsReason.toBoolean() == true){
			updateStatus = true;
		}
		
		//add findByStub shapes
		updateConnectionsReason = addRemoveUpdateFindBy(connections, sadConnectInterfaces, diagram, featureProvider, performUpdate);
		if(!performUpdate && updateConnectionsReason.toBoolean()){
			return updateConnectionsReason;
		}else if(updateConnectionsReason.toBoolean() == true){
			updateStatus = true;
		}
		
		//add Connections found in model, but not in diagram
		for(SadConnectInterface sadConnectInterface: sadConnectInterfaces){
			//in diagram
			  boolean found = false;
			  for(Connection pe: connections){
				  if(sadConnectInterface.equals(DUtil.getBusinessObject(pe, objectClass))){
					  found = true;
				  }
			  }
			  if(!found){
				  // wasn't found, add Connection
				  if(performUpdate){
					  updateStatus = true;

					  //lookup sourceAnchor
					  Anchor sourceAnchor = DUtil.lookupSourceAnchor(sadConnectInterface, diagram);

					  //if sourceAnchor wasn't found its because the findBy needs to be added to the diagram
					  if(sourceAnchor == null){

						  //FindBy is always used inside usesPort
						  if(sadConnectInterface.getUsesPort() != null &&
								  sadConnectInterface.getUsesPort().getFindBy() != null)
						  {

							  FindBy findBy = (FindBy)sadConnectInterface.getUsesPort().getFindBy();
							  
							  //search for findByStub in diagram
							  FindByStub findByStub = DUtil.findFindByStub(findBy, diagram);
							  
							  if(findByStub == null){
								  //should never occur, addRemoveUpdateFindBy() takes care of this
								  throw new CoreException(new Status(IStatus.ERROR, SADUIGraphitiPlugin.PLUGIN_ID, "Unable to locate FindBy Shape in Diagram"));
							  }
							  
							  //determine which usesPortStub
							  UsesPortStub usesPortStub = null;
							  for(UsesPortStub p: findByStub.getUses()){
								  if(p != null && sadConnectInterface.getUsesPort().getUsesIndentifier() != null &&
										  p.getName().equals(sadConnectInterface.getUsesPort().getUsesIndentifier())){
									  usesPortStub = p;
								  }
							  }
							  //determine port anchor for FindByMatch
							  if(usesPortStub != null){
								  PictogramElement pe = DUtil.getPictogramElementForBusinessObject(diagram, (EObject)usesPortStub, Anchor.class);
								  sourceAnchor = (Anchor)pe;
							  }else{
								  System.out.println("our source port is not getting set");
								  //TODO: this means the provides port didn't exist in the existing findByStub..we need to add it
							  }
						  }
					  }
						  
					  //lookup Target Anchor
					  Anchor targetAnchor = null;
					  PictogramElement targetAnchorPe = DUtil.getPictogramElementForBusinessObject(diagram, sadConnectInterface.getTarget(), Anchor.class);
					  if(targetAnchorPe != null){
						  targetAnchor = (Anchor)targetAnchorPe;
					  }else{
						  
						  //sadConnectInterface.getComponentSupportedInterface().getFindBy()
						  if(sadConnectInterface.getComponentSupportedInterface() != null &&
								  sadConnectInterface.getComponentSupportedInterface().getSupportedIdentifier() != null &&
								  sadConnectInterface.getComponentSupportedInterface().getFindBy() != null){

							  //The model provides us with interface information for the FindBy we are connecting to
							  FindBy findBy = (FindBy)sadConnectInterface.getComponentSupportedInterface().getFindBy();

							  //iterate through FindByStubs in diagram
							  FindByStub findByStub = DUtil.findFindByStub(findBy, diagram);

							  if(findByStub == null){
								  //should never occur, addRemoveUpdateFindBy() takes care of this
								  throw new CoreException(new Status(IStatus.ERROR, SADUIGraphitiPlugin.PLUGIN_ID, "Unable to locate FindBy Shape in Diagram"));
							  }

							  //determine port anchor for FindByMatch
							  if(findByStub.getInterface() != null){
								  PictogramElement pe = DUtil.getPictogramElementForBusinessObject(diagram, findByStub.getInterface(), Anchor.class);
								  targetAnchor = (Anchor)pe;
							  }

					      //findBy nested in ProvidesPort
						  }else if(sadConnectInterface.getProvidesPort() != null &&
								  sadConnectInterface.getProvidesPort().getFindBy() != null){
							  

							  FindBy findBy = (FindBy)sadConnectInterface.getProvidesPort().getFindBy();

							  //iterate through FindByStubs in diagram
							  FindByStub findByStub = DUtil.findFindByStub(findBy, diagram);
							  
							  if(findByStub == null){
								  //should never occur, addRemoveUpdateFindBy() takes care of this
								  throw new CoreException(new Status(IStatus.ERROR, SADUIGraphitiPlugin.PLUGIN_ID, "Unable to locate FindBy Shape in Diagram"));
							  }
							  
							  //ensure the providesPort exists in FindByStub that already exists in diagram
							  boolean foundProvidesPortStub = false;
							  for(ProvidesPortStub p: findByStub.getProvides()){
								  if(p.getName().equals(sadConnectInterface.getProvidesPort().getProvidesIdentifier())){
									  foundProvidesPortStub = true;
								  }
							  }
							  if(!foundProvidesPortStub){
								  //add the required providesPort
								  AbstractFindByPattern.addProvidesPortStubToFindByStub(findByStub, sadConnectInterface.getProvidesPort(), featureProvider);
								  //Update on FindByStub PE
								  DUtil.updateShapeViaFeature(featureProvider, diagram, 
										  DUtil.getPictogramElementForBusinessObject(diagram, findByStub, RHContainerShape.class));

								  //maybe call layout?

							  }
							  
							  //determine which providesPortStub we are targeting
							  ProvidesPortStub providesPortStub = null;
							  for(ProvidesPortStub p: findByStub.getProvides()){
								  if(p != null && sadConnectInterface.getProvidesPort().getProvidesIdentifier() != null &&
										  p.getName().equals(sadConnectInterface.getProvidesPort().getProvidesIdentifier())){
									  providesPortStub = p;
									  break;
								  }
							  }

							  //determine port anchor for FindByMatch
							  if(providesPortStub != null){
								  PictogramElement pe = DUtil.getPictogramElementForBusinessObject(diagram, (EObject)providesPortStub, Anchor.class);
								  targetAnchor = (Anchor)pe;
							  }else{
								  //TODO: this means the provides port didn't exist in the existing findByStub..we need to add it
							  }
						  }
					  }

					  //add Connection if anchors
					  if(sourceAnchor != null && targetAnchor != null){
						  DUtil.addConnectionViaFeature(featureProvider, sadConnectInterface, sourceAnchor, targetAnchor);
					  }else{
						  //TODO: how do we handle this?
					  }
					  
				  }else{
					  return new Reason(true, "A " + pictogramLabel + " in model isn't displayed in diagram");
				  }
			  }
		}
		
		if(updateStatus && performUpdate){
			return new Reason(true, "Update successful");
		}
		  
		return new Reason(false, "No updates required");

	}
	
	/**
	 * Search for the FindByStub in the diagram given the findBy object
	 * @param findBy
	 * @param diagram
	 * @return
	 */
	public static FindByStub findFindByStub(FindBy findBy, List<FindByStub> findByStubs){
		  for(FindByStub findByStub: findByStubs){
			  if(AbstractFindByPattern.doFindByObjectsMatch(findBy, findByStub)){
				  //it matches
				  return findByStub;
			  }
		  }	
		  return null;
	}

}
