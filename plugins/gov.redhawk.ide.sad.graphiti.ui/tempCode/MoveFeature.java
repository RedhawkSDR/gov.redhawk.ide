package gov.redhawk.ide.sad.graphiti.ui.diagram.features.connection;

import org.eclipse.bpmn2.modeler.core.features.ConnectionFeatureContainer;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;

/**
 * @author Jack Chi
 *
 */
public class MoveFeature extends DefaultMoveShapeFeature {

	public MoveFeature(IFeatureProvider fp) {
		super(fp);
	}
	
	@Override
    public boolean canMoveShape(IMoveShapeContext context) {
        return super.canMoveShape(context);
    }
	
	@Override
	protected void postMoveShape(IMoveShapeContext context) {
		super.postMoveShape(context);
		ConnectionFeatureContainer.updateConnections(getFeatureProvider(), getDiagram());
	}


}
