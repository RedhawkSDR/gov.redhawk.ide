package gov.redhawk.ide.sad.graphiti.ui.diagram.patterns;

import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DiagramUtil;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.StyleUtil;
import gov.redhawk.sca.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub;
import mil.jpeojtrs.sca.partitioning.ConnectInterface;
import mil.jpeojtrs.sca.partitioning.ConnectionTarget;
import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.SadConnectInterface;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.context.IAddConnectionContext;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.mm.Property;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.pattern.AbstractConnectionPattern;
import org.eclipse.graphiti.pattern.IConnectionPattern;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;

public class SADConnectInterfacePattern extends AbstractConnectionPattern implements IConnectionPattern{

	
	@Override
	public String getCreateName(){
		return "Connection";
	}
	
	
	/**
	 * Return true if use selected 
	 */
	@Override
	public boolean canStartConnection(ICreateConnectionContext context) {
		
		//get sad from diagram
		final SoftwareAssembly sad = DiagramUtil.getDiagramSAD(getFeatureProvider(), getDiagram());

		//source and destination targets
		UsesPortStub source = getUsesPortStub(context.getSourceAnchor());
		
		if(sad != null && source != null){
			return true;
		}

		return false;
	}
	
	/**
	 * Determines if a connection can be made.
	 */
	@Override
    public boolean canAdd(IAddContext context) {
		
		if(context instanceof IAddConnectionContext && context.getNewObject() instanceof SadConnectInterface){
			return true;
		}
	    return false;
    }
	
	/**
	 * Adds the connection to the diagram and associates the source/target port with the line
	 */
	@Override
	public PictogramElement add(IAddContext context) {
	    
		IAddConnectionContext addContext = (IAddConnectionContext)context;
		SadConnectInterface connectInterface = (SadConnectInterface)addContext.getNewObject();
		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		
		
		//Create connection
		Connection connection = peCreateService.createFreeFormConnection(getFeatureProvider().getDiagramTypeProvider().getDiagram());
		connection.setStart(addContext.getSourceAnchor());
		connection.setEnd(addContext.getTargetAnchor());
		
		//create line
		IGaService gaService = Graphiti.getGaService();
		Polyline line = gaService.createPolyline(connection);
		line.setLineWidth(2);
		line.setForeground(gaService.manageColor(getFeatureProvider().getDiagramTypeProvider().getDiagram(),  StyleUtil.BLACK));
		
		//add static graphical arrow
		ConnectionDecorator cd;
		cd = peCreateService.createConnectionDecorator(connection, false, 1.0, true);
		DiagramUtil.createArrow(cd, getFeatureProvider(), gaService.manageColor(getFeatureProvider().getDiagramTypeProvider().getDiagram(),  StyleUtil.BLACK));
		
		//link ports to connection
		getFeatureProvider().link(connection, new Object[] { connectInterface.getSource(), connectInterface.getTarget()});
		
		return connection;
	}
	
	@Override
	public boolean canCreate(ICreateConnectionContext context) {
		//get sad from diagram
		final SoftwareAssembly sad = DiagramUtil.getDiagramSAD(getFeatureProvider(), getDiagram());
				
		//source and destination targets
		UsesPortStub source = getUsesPortStub(context.getSourceAnchor());
		ConnectionTarget target = getConnectionTarget(context.getTargetAnchor());
		
		
		if(sad == null){
			return false;
		}
		if (source == null && target == null) {
			return false;
		}
		//TODO: What is this about? taken from gov.redhawk.diagram.edit.commands.ConnectInterfaceCreateCommand
//		if (getSource() == null) {
//			return true; // link creation is in progress; source is not defined yet
//		}
		if (target != null && source instanceof UsesPortStub) {
			//			Relax constraint on what constitutes a connection			
			//			TODO: Determine ultimately what defines a valid connection between two end points
			//			return InterfacesUtil.areCompatible(this.source, this.target);
			return (target instanceof ProvidesPortStub || target instanceof ComponentSupportedInterfaceStub || target instanceof FindByStub);
		}
		return true;
		
	}

	/**
	 * Creates a new connection between the selected usesPortStub and ConnectionTarget
	 */
	@Override
	public Connection create(ICreateConnectionContext context) {
		
		Connection newConnection = null;

		//source and destination targets
		final UsesPortStub source = getUsesPortStub(context.getSourceAnchor());
		final ConnectionTarget target = getConnectionTarget(context.getTargetAnchor());

		//TODO: handle bad situations
		if (source == null || target == null) {
			return null;
		}
		
		//editing domain for our transaction
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();
		
		//get sad from diagram
		final SoftwareAssembly sad = DiagramUtil.getDiagramSAD(getFeatureProvider(), getDiagram());
		
		//container for new SadConnectInterface, necessary for reference after command execution
		final SadConnectInterface[] sadConnectInterfaces = new SadConnectInterface[1];
		
		//Create Connect Interface & related objects
		TransactionalCommandStack stack = (TransactionalCommandStack)editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain){
			@Override
            protected void doExecute() {
				
				//create connections if necessary
				if(sad.getConnections() == null){
					sad.setConnections(SadFactory.eINSTANCE.createSadConnections());
				}
				
				//create connect interface
				sadConnectInterfaces[0] = SadFactory.eINSTANCE.createSadConnectInterface();
				
				//add to connections
				sad.getConnections().getConnectInterface().add(sadConnectInterfaces[0]);
				
				//set connection id
				sadConnectInterfaces[0].setId(createConnectionId(sad));
				//source
				sadConnectInterfaces[0].setSource(source);
				//target
				sadConnectInterfaces[0].setTarget(target);
				
				//TODO: evaluate when and where these should be set
//				sadConnectInterfaces[0].setProvidesPort(value);
//				sadConnectInterfaces[0].setTarget(value);
//				sadConnectInterfaces[0].setFindBy(value);
//				sadConnectInterfaces[0].setComponentSupportedInterface(value);
				

				
				
            }
		});
		
		// add connection for business object
		AddConnectionContext addContext = new AddConnectionContext(context.getSourceAnchor(), context.getTargetAnchor());
		addContext.setNewObject(sadConnectInterfaces[0]);
		newConnection = (Connection) getFeatureProvider().addIfPossible(addContext);
		
		return newConnection;
	}
	
	
	
	private UsesPortStub getUsesPortStub(Anchor anchor) {
		if (anchor != null) {
			Object object = getBusinessObjectForPictogramElement(anchor.getParent());
			if(object instanceof UsesPortStub){
				return (UsesPortStub)object;
			}
		}
		return null;
	}
	
	private ConnectionTarget getConnectionTarget(Anchor anchor) {
		if (anchor != null) {
			Object object = getBusinessObjectForPictogramElement(anchor.getParent());
			if (object instanceof ConnectionTarget) {
				return (ConnectionTarget)object;
			}
		}
		return null;
	}
	
	/**
	 * Returns the next available connection id
	 * @param sad
	 * @return
	 */
	private String createConnectionId(SoftwareAssembly sad) {
		final List<String> ids = new ArrayList<String>();
		final List< ? extends ConnectInterface< ? , ? , ? >> connections = sad.getConnections().getConnectInterface();
		for (final ConnectInterface< ? , ? , ? > connection : connections) {
			ids.add(connection.getId());
		}
		return StringUtil.defaultCreateUniqueString("connection_1", ids);
	}
	
	
}
