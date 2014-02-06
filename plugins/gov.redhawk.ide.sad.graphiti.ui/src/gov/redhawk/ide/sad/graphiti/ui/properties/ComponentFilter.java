package gov.redhawk.ide.sad.graphiti.ui.properties;

import gov.redhawk.ide.sad.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;

public class ComponentFilter extends AbstractPropertySectionFilter{

	@Override
    protected boolean accept(PictogramElement pictogramElement) {
		ContainerShape containerShape = (ContainerShape)DUtil.findContainerShapeParentWithProperty(
				pictogramElement, RHContainerShapeImpl.SHAPE_outerContainerShape);
		Object obj = DUtil.getBusinessObject(containerShape);
		if(containerShape != null && obj != null && obj instanceof SadComponentInstantiation){
			return true;
		}
		return false;
    }

}
