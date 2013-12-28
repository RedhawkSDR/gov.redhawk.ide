package gov.redhawk.ide.sad.graphiti.ui.diagram.features.connection;

import org.eclipse.bpmn2.modeler.core.features.BendpointConnectionRouter;
import org.eclipse.bpmn2.modeler.core.features.ConnectionFeatureContainer;
import org.eclipse.bpmn2.modeler.core.utils.AnchorUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IMoveBendpointContext;
import org.eclipse.graphiti.features.impl.DefaultMoveBendpointFeature;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.osgi.framework.debug.Debug;

public class MoveBendPointFeature extends DefaultMoveBendpointFeature {

	public MoveBendPointFeature(IFeatureProvider fp) {
		super(fp);
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	public boolean canExecute(IContext context) {
		// TODO Auto-generated method stub
		return super.canExecute(context);
	}
	
	@Override
	public boolean moveBendpoint(IMoveBendpointContext context) {
		boolean moved = super.moveBendpoint(context);
		try {
			FreeFormConnection connection = context.getConnection();
//			SomeInterface element = (SomeInterface) BusinessObjectUtil.getFirstElementOfType(connection, StepReference.class);
//			BPMNEdge edge = DIUtils.findBPMNEdge(element);
//			if (edge!=null) {
				int index = context.getBendpointIndex() + 1;
//				Point p = edge.getWaypoint().get(index);
//				p.setX(context.getX());
//				p.setY(context.getY());
				
				// also need to move the connection point if there is one at this bendpoint
				Shape connectionPointShape = AnchorUtil.getConnectionPointAt(connection, context.getBendpoint());
				if (connectionPointShape!=null)
					AnchorUtil.setConnectionPointLocation(connectionPointShape, context.getX(), context.getY());
	
				BendpointConnectionRouter.setMovedBendpoint(connection, context.getBendpointIndex());
				ConnectionFeatureContainer.updateConnection(getFeatureProvider(), connection);
//			}
			
		} catch (Exception e) {
//			Activator.logError(e);
			Debug.println(e);
		}
		return moved;
	}
	
	
	

}
