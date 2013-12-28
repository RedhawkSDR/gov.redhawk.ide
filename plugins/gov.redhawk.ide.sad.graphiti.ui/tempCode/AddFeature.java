package gov.redhawk.ide.sad.graphiti.ui.diagram.features.connection;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

/**
 * @author Jack Chi
 *
 */
public class AddFeature extends AbstractAddFeature {

	public AddFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public PictogramElement add(IAddContext arg0) {
		return null;
	}

	@Override
	public boolean canAdd(IAddContext arg0) {
		// TODO This is left as an exercise for user
		
		//Create CENTER, LEFT, RIGHT, TOP, BOTTOM anchors
		
//		FixPointAnchor leftFpAnchor = peCreateService.createFixPointAnchor(containerShape);
//		Graphiti.getPeService().setPropertyValue(leftFpAnchor, AnchorUtil.BOUNDARY_FIXPOINT_ANCHOR, AnchorLocation.LEFT.getKey());
//		Point loc = leftFpAnchor.getLocation();
//		if (loc == null){
//			loc = StylesFactory.eINSTANCE.createPoint();
//			leftFpAnchor.setLocation(loc);
//		}
//		loc.setX(0);
//		loc.setY(0);
//		
//		leftFpAnchor.setReferencedGraphicsAlgorithm(roundedRectangle);
//		leftFpAnchor.setUseAnchorLocationAsConnectionEndpoint(true);
		
		return false;
	}

}
