package gov.redhawk.ide.sad.graphiti.ui.diagram.features.connection;

import org.eclipse.bpmn2.modeler.core.features.IConnectionRouter;
//import org.eclipse.bpmn2.modeler.core.features.ManhattanConnectionRouter;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.impl.AbstractLayoutFeature;
import org.eclipse.graphiti.mm.pictograms.Connection;

/**
 * @author Jack Chi
 *
 *	
 */
public class LayoutConnectionFeature extends
		AbstractLayoutFeature {

	boolean hasDoneChanges = false;

	public LayoutConnectionFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canLayout(ILayoutContext context) {
		return (context.getPictogramElement() instanceof Connection);
		// Connection connection = (Connection) context.getPictogramElement();
		// implement your logic here
//		return true;
	}

	@Override
	public boolean hasDoneChanges() {
		return hasDoneChanges;
	}

	@Override
	public boolean layout(ILayoutContext context) {
		if (canLayout(context)) {
			Connection connection = (Connection) context.getPictogramElement();
			IFeatureProvider fp = getFeatureProvider();
			IConnectionRouter router = null;
			router = new ExtendedManhattanConnectionRouter(fp);
			if (router != null) {
				hasDoneChanges = router.route(connection);
				router.dispose();
			}
		}
		return hasDoneChanges;
	}
}
