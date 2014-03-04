package gov.redhawk.ide.sad.graphiti.ui.diagram.features.update;

import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.ComponentPattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.HostCollocationPattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mil.jpeojtrs.sca.partitioning.ComponentInstantiation;
import mil.jpeojtrs.sca.sad.HostCollocation;
import mil.jpeojtrs.sca.sad.SadComponentPlacement;
import mil.jpeojtrs.sca.sad.SadConnectInterface;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.DefaultUpdateDiagramFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

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
	 */
	@SuppressWarnings("unchecked")
    public Reason internalUpdate(IUpdateContext context, boolean performUpdate){
		
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
					DUtil.addRemoveUpdateConnections(connections, 
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
		return internalUpdate(context, false);
	}
	
	@Override
	public boolean update(IUpdateContext context) {
		Reason reason =  internalUpdate(context, true);
		
		
		return reason.toBoolean();
	}

}
