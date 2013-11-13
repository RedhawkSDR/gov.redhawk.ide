package gov.redhawk.ide.sad.graphiti.ui.diagram.providers;

import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.ComponentPattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.FindByNamingServicePattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.SADConnectInterfacePattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DiagramUtil;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.IResizeShapeFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;
import org.eclipse.graphiti.features.impl.DefaultRemoveFeature;
import org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature;
import org.eclipse.graphiti.pattern.DefaultFeatureProviderWithPatterns;
import org.eclipse.graphiti.ui.features.DefaultDeleteFeature;



public class SADDiagramFeatureProvider extends DefaultFeatureProviderWithPatterns {

	
	public SADDiagramFeatureProvider(IDiagramTypeProvider diagramTypeProvider){
		super(diagramTypeProvider);
		
		//Add Patterns for Domain Objects
		addPattern(new ComponentPattern());
		addConnectionPattern(new SADConnectInterfacePattern());
		addPattern(new FindByNamingServicePattern());

	}
	
	@Override
	public IAddFeature getAddFeature(IAddContext context){
		
//		if(context.getNewObject() instanceof myObject1){
//			return new Feature1(this);
//		}
//		else if(context.getNewObject() instanceof myObject2){
//			return new Feature2(this);
//		}
		
		return super.getAddFeature(context);
	}
	
	@Override
	public IMoveShapeFeature getMoveShapeFeature(IMoveShapeContext context) {
		
		//Search for shapes that we don't want the user to have remove capability
		if(DiagramUtil.doesPictogramContainProperty(context, 
			  new String[] {ComponentPattern.COMPONENT_SHAPE_usesPortRectangleShape,
								ComponentPattern.COMPONENT_SHAPE_providesPortRectangleShape}))
		{
			return new DefaultMoveShapeFeature(this) {
				public boolean canMove(IContext context) {
					return false;
				}
			};
		}
		
		return super.getMoveShapeFeature(context);
	}
	
	@Override
	public ICreateFeature[] getCreateFeatures(){
		
		ICreateFeature[] defaultCreateFeatures = super.getCreateFeatures();
		
		//features added via pattern
		return defaultCreateFeatures;
	}
	
	@Override
	public IUpdateFeature getUpdateFeature(IUpdateContext context){
//		PictogramElement pictogramElement = context.getPictorgramElement();
//		if(pictogramElement instanceof ContainerShape){
//			Object bo = getBusinessObjectForPictogramElement(pictogramElement);
//			if(bo instanceof Object1){
//				return new UpdateObjec1Feature(this);
//			}
//		}
		
		//TODO: how do we hide these
//		//Search for shapes that we don't want the user to have remove capability
//		PictogramElement pe = context.getPictogramElement();
//		if(pe != null && pe.getProperties() != null){
//			for(Property p: pe.getProperties()){
//				if(ComponentPattern.PROVIDES_PORT_TYPE_CONTAINER.equals(p.getKey()) ||
//						ComponentPattern.USES_PORT_TYPE_CONTAINER.equals(p.getKey()) ||
//						ComponentPattern.PROVIDES_PORT_TYPE.equals(p.getKey()) ||
//						ComponentPattern.USES_PORT_TYPE.equals(p.getKey()))
//				{
//					return new DefaultUpdateFeature(this) {
//						public boolean isAvailable(IContext context) {
//							return false;
//						}
//					};
//				}
//			}
//		}
		
		return super.getUpdateFeature(context);
	}
	

	
	@Override
	public IDeleteFeature getDeleteFeature(IDeleteContext context){
		
		//Search for shapes that we don't want the user to have delete capability
		if(DiagramUtil.doesPictogramContainProperty(context, 
			  new String[] {ComponentPattern.COMPONENT_SHAPE_providesPortsContainerShape,
								ComponentPattern.COMPONENT_SHAPE_usesPortsContainerShape,
								ComponentPattern.COMPONENT_SHAPE_providesPortContainerShape,
								ComponentPattern.COMPONENT_SHAPE_usesPortContainerShape,
								ComponentPattern.COMPONENT_SHAPE_providesPortRectangleShape,
								ComponentPattern.COMPONENT_SHAPE_usesPortRectangleShape}))
		{
			return new DefaultDeleteFeature(this) {
				public boolean isAvailable(IContext context) {
					return false;
				}
			};
		}
		
		return super.getDeleteFeature(context);
	}
	
	@Override
	public IRemoveFeature getRemoveFeature(IRemoveContext context){
		
		//Search for shapes that we don't want the user to have remove capability
		if(DiagramUtil.doesPictogramContainProperty(context, 
				  new String[] {ComponentPattern.COMPONENT_SHAPE_providesPortsContainerShape,
									ComponentPattern.COMPONENT_SHAPE_usesPortsContainerShape,
									ComponentPattern.COMPONENT_SHAPE_providesPortContainerShape,
									ComponentPattern.COMPONENT_SHAPE_usesPortContainerShape,
									ComponentPattern.COMPONENT_SHAPE_providesPortRectangleShape,
									ComponentPattern.COMPONENT_SHAPE_usesPortRectangleShape}))
			{
			return new DefaultRemoveFeature(this) {
				public boolean isAvailable(IContext context) {
					return false;
				}
			};
		}
		
		return super.getRemoveFeature(context);
		
	}
	
	@Override
	public IFeature[] getDragAndDropFeatures(IPictogramElementContext context){
		
		ICreateConnectionFeature[] connectionFeatures = getCreateConnectionFeatures();
	
		return connectionFeatures;
	}
	
	
	@Override
	public IResizeShapeFeature getResizeShapeFeature(IResizeShapeContext context){
		
		//Search for shapes that we don't want the user to have resize capability
		if(DiagramUtil.doesPictogramContainProperty(context, 
				new String[] {ComponentPattern.COMPONENT_SHAPE_providesPortRectangleShape,
				ComponentPattern.COMPONENT_SHAPE_usesPortRectangleShape}))
		{
			return new DefaultResizeShapeFeature(this) {
				public boolean canResizeShape(IResizeShapeContext context) {
					return false;
				}
			};
		}
				
		return super.getResizeShapeFeature(context);
	}
}
