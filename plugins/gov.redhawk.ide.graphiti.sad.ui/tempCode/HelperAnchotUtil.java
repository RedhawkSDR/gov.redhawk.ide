package gov.redhawk.ide.sad.graphiti.ui.diagram.features.connection;

import org.eclipse.bpmn2.modeler.core.utils.AnchorUtil;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;

public class HelperAnchotUtil extends AnchorUtil {
	public static void addLeftRightFixedPointAnchors(Shape shape, GraphicsAlgorithm ga) {
		IDimension size = Graphiti.getGaService().calculateSize(ga);
		int w = size.getWidth();
		int h = size.getHeight();
//		createBoundaryAnchor(shape, AnchorLocation.TOP, w / 2, 0);
		createBoundaryAnchor(shape, AnchorLocation.RIGHT, w, h / 2);
//		createBoundaryAnchor(shape, AnchorLocation.BOTTOM, w / 2, h);
		createBoundaryAnchor(shape, AnchorLocation.LEFT, 0, h / 2);
	}
}
