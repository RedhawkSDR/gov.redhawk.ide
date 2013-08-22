package gov.redhawk.ide.sad.graphiti.util;

import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;

public class PropertyUtil {

	
	public static final String SHAPE_TYPE_KEY = "shapeType";
	public static final String COMPONENT_SHAPE_TYPE = "component";
	
	public static final void setComponentShape(PictogramElement pe){
		Graphiti.getPeService().setPropertyValue(pe,  SHAPE_TYPE_KEY, COMPONENT_SHAPE_TYPE);
	}
	
	public static boolean isComponentShape(PictogramElement pe){
		return COMPONENT_SHAPE_TYPE.equals(Graphiti.getPeService().getPropertyValue(pe,  SHAPE_TYPE_KEY));
	}
}
