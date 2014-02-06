package gov.redhawk.ide.sad.graphiti.ui.diagram.patterns;

import gov.redhawk.ide.sad.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.sad.graphiti.ext.RHGxFactory;
import gov.redhawk.ide.sad.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.sad.graphiti.ui.diagram.providers.ImageProvider;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.StyleUtil;

import java.util.Arrays;

import mil.jpeojtrs.sca.partitioning.FindByStub;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.pattern.AbstractPattern;
import org.eclipse.graphiti.pattern.IPattern;
import org.eclipse.graphiti.services.Graphiti;

public abstract class AbstractFindByPattern extends AbstractPattern implements IPattern{

	
	public AbstractFindByPattern(){
		super(null);
	}
	
	//THE FOLLOWING THREE METHODS DETERMINE IF PATTERN IS APPLICABLE TO OBJECT
	public abstract boolean isMainBusinessObjectApplicable(Object mainBusinessObject);
	
	@Override
	protected boolean isPatternControlled(PictogramElement pictogramElement) {
		Object domainObject = getBusinessObjectForPictogramElement(pictogramElement);
		return isMainBusinessObjectApplicable(domainObject);
	}
	@Override
	protected boolean isPatternRoot(PictogramElement pictogramElement) {
		Object domainObject = getBusinessObjectForPictogramElement(pictogramElement);
		return isMainBusinessObjectApplicable(domainObject);
	}
	
	
	//DIAGRAM FEATURES
	@Override
	public boolean canAdd(IAddContext context) {
		if (context.getNewObject() instanceof FindByStub) {
			if (context.getTargetContainer() instanceof Diagram) {
					return true;
			}
		}
		return false;
	}
	
	/**
	 * Provides the title of the outer shape
	 * @param findByStub
	 * @return
	 */
	public String getOuterTitle(FindByStub findByStub){
		return getCreateName();
	}
	
	/**
	 * Provides the title of the inner shape
	 * @param findByStub
	 * @return
	 */
	public abstract String getInnerTitle(FindByStub findByStub);
	
	@Override
	public PictogramElement add(IAddContext context) {
		FindByStub findByStub = (FindByStub) context.getNewObject();
		ContainerShape targetContainerShape = (ContainerShape) context.getTargetContainer();
		Diagram diagram = (Diagram) context.getTargetContainer();

		String outerTitle = getOuterTitle(findByStub);
		String innerTitle = getInnerTitle(findByStub);
		
		//create shape
		RHContainerShape rhContainerShape = RHGxFactory.eINSTANCE.createRHContainerShape();

		//initialize shape contents
		rhContainerShape.init(targetContainerShape, outerTitle, 
				Arrays.asList((EObject)findByStub), getFeatureProvider(), ImageProvider.IMG_FIND_BY,
//				(EObject)findByStub, getFeatureProvider(), ImageProvider.IMG_FIND_BY,
				StyleUtil.getStyleForFindByOuter(diagram), innerTitle,
				getCreateImageId(), StyleUtil.getStyleForFindByInner(diagram), 
				findByStub.getInterface(), findByStub.getUses(), findByStub.getProvides(), null);

		//set shape location to user's selection
		Graphiti.getGaLayoutService().setLocation(rhContainerShape.getGraphicsAlgorithm(), 
				context.getX(), context.getY());

		//layout
		layoutPictogramElement(rhContainerShape);

		return rhContainerShape;
	}
	
	@Override
	public boolean canCreate(ICreateContext context) {
		return context.getTargetContainer() instanceof Diagram;
	}
	@Override
	public abstract Object[] create(ICreateContext context);
	
	@Override
	public boolean canResizeShape(IResizeShapeContext context){
		return true;
	}
	
	@Override
	public boolean canLayout(ILayoutContext context){
		ContainerShape containerShape = (ContainerShape) context.getPictogramElement();
		Object obj = DUtil.getBusinessObject(containerShape);
		if(obj instanceof FindByStub){
			return true;
		}
		return false;
	}
	
	/**
	 * Layout children of component
	 */
	@Override
	public boolean layout(ILayoutContext context){
		((RHContainerShape)context.getPictogramElement()).layout();
		
		//something is always changing.
        return true;
	}
	
	@Override
	public boolean update(IUpdateContext context) {
		
		//business object
		FindByStub findByStub = 
				(FindByStub)DUtil.getBusinessObject(context.getPictogramElement());
		
		String outerTitle = getOuterTitle(findByStub);
		String innerTitle = getInnerTitle(findByStub);
		
		Reason updated = ((RHContainerShape)context.getPictogramElement())
				.update(outerTitle, 
						findByStub, getFeatureProvider(), ImageProvider.IMG_FIND_BY,
						StyleUtil.getStyleForFindByOuter(getDiagram()), innerTitle,
						getCreateImageId(), StyleUtil.getStyleForFindByInner(getDiagram()), 
						findByStub.getInterface(), findByStub.getUses(), findByStub.getProvides(), null);
				
		//if we updated redraw
		if(updated.toBoolean()){
			layoutPictogramElement(context.getPictogramElement());
		}

		return updated.toBoolean();
	}
	
	/**
	 * Determines whether we need to update the diagram from the model.  
	 */
	@Override
	public IReason updateNeeded(IUpdateContext context) {
		
		//business object
		FindByStub findByStub = 
				(FindByStub)DUtil.getBusinessObject(context.getPictogramElement());
		
		String outerTitle = getOuterTitle(findByStub);
		String innerTitle = getInnerTitle(findByStub);;
		
		Reason requiresUpdate = ((RHContainerShape)context.getPictogramElement())
				.updateNeeded(outerTitle, 
						findByStub, getFeatureProvider(), ImageProvider.IMG_FIND_BY,
						StyleUtil.getStyleForFindByOuter(getDiagram()), innerTitle,
						getCreateImageId(), StyleUtil.getStyleForFindByInner(getDiagram()), 
						findByStub.getInterface(), findByStub.getUses(), findByStub.getProvides(), null);

		return requiresUpdate;
	}
	
	@Override
	public boolean canDirectEdit(IDirectEditingContext context){
		PictogramElement pe = context.getPictogramElement();
		RHContainerShape rhContainerShape = (RHContainerShape)DUtil.findContainerShapeParentWithProperty(
				pe, RHContainerShapeImpl.SHAPE_outerContainerShape);
		Object obj = getBusinessObjectForPictogramElement(rhContainerShape);
		GraphicsAlgorithm ga = context.getGraphicsAlgorithm();
		//allow if we've selected Text for the FindByStub
		if (obj instanceof FindByStub && ga instanceof Text) {
			return true;
		}
		return false;
	}
	
	@Override
    public int getEditingType() {
	    return TYPE_TEXT;
    }

	@Override
    public String getInitialValue(IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		RHContainerShape rhContainerShape = (RHContainerShape)DUtil.findContainerShapeParentWithProperty(
				pe, RHContainerShapeImpl.SHAPE_outerContainerShape);
		FindByStub findBy = (FindByStub) getBusinessObjectForPictogramElement(rhContainerShape);
		return getInnerTitle(findBy);
    }
	
	@Override
	public abstract String checkValueValid(String value, IDirectEditingContext context);
	
	@Override
	public abstract void setValue(final String value, IDirectEditingContext context);

}
