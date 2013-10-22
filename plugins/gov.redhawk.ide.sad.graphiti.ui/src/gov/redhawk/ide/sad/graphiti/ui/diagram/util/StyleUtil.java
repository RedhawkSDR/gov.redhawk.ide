package gov.redhawk.ide.sad.graphiti.ui.diagram.util;

import java.util.Collection;

import org.eclipse.graphiti.mm.StyleContainer;
import org.eclipse.graphiti.mm.algorithms.styles.Style;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.util.PredefinedColoredAreas;

public class StyleUtil {

	//returns component style
	public static Style getStyleForComponent(Diagram diagram){
		final String styleId = "Component";
		
		Style style = findStyle(diagram, styleId);
		IGaService gaService = Graphiti.getGaService();
		if(style == null){
			style = gaService.createStyle(diagram, styleId);
			gaService.setRenderingStyle(style, PredefinedColoredAreas.getBlueWhiteAdaptions());
			style.setLineWidth(2);
		}
		return style;
	}
	
	//find the style with given id in style-container
	private static Style findStyle(StyleContainer styleContainer, String id){
		//find and return style
		Collection<Style> styles = styleContainer.getStyles();
		if(styles != null){
			for(Style style: styles){
				if(id.equals(style.getId())){
					return style;
				}
			}
		}
		return null;
	}
}
