package gov.redhawk.ide.sad.graphiti.ui.properties;

import gov.redhawk.ide.sad.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.provider.SadItemProviderAdapterFactory;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.platform.GraphitiShapeEditPart;
import org.eclipse.ui.views.properties.IPropertySource;

public class GraphitiEditPartToIPropertySourceAdapterFactory implements IAdapterFactory{
	
    public GraphitiEditPartToIPropertySourceAdapterFactory() {
        super();
}

    public Object getAdapter(Object adaptableObject, @SuppressWarnings("rawtypes") Class adapterType) {
    	if (IPropertySource.class.equals(adapterType)) {
    		if (adaptableObject instanceof GraphitiShapeEditPart) {
    			GraphitiShapeEditPart editPart = (GraphitiShapeEditPart) adaptableObject;
    			PictogramElement pictogramElement = editPart.getPictogramElement();
    			ContainerShape containerShape = (ContainerShape)DUtil.findContainerShapeParentWithProperty(
    					pictogramElement, RHContainerShapeImpl.SHAPE_outerContainerShape);
    			Object obj = DUtil.getBusinessObject(containerShape);
    			if(containerShape != null && obj != null && obj instanceof SadComponentInstantiation){
    				
    				//get sca property source
    				final SadItemProviderAdapterFactory factory = new SadItemProviderAdapterFactory();
    				IItemPropertySource obj2 = (IItemPropertySource) factory.adapt(obj, IItemPropertySource.class);
    				return new gov.redhawk.sca.ui.RedhawkUiAdapterFactory.ScaPropertySource(obj, obj2);
    			}
    		}
    	}
    	return null;
    }

    @SuppressWarnings("rawtypes")
    public Class[] getAdapterList() {
    	return new Class[] { IPropertySource.class };
    }
}
