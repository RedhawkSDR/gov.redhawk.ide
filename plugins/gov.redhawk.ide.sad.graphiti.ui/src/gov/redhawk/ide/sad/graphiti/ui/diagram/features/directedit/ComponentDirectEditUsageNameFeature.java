package gov.redhawk.ide.sad.graphiti.ui.diagram.features.directedit;

import gov.redhawk.ide.sad.graphiti.ext.ComponentShape;
import gov.redhawk.ide.sad.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class ComponentDirectEditUsageNameFeature extends AbstractDirectEditingFeature{

	public ComponentDirectEditUsageNameFeature(IFeatureProvider fp) {
	    super(fp);
    }

	@Override
	public boolean canDirectEdit(IDirectEditingContext context){
		PictogramElement pe = context.getPictogramElement();
		ComponentShape componentShape = (ComponentShape)DUtil.findContainerShapeParentWithProperty(
				pe, RHContainerShapeImpl.SHAPE_outerContainerShape);
		Object obj = getBusinessObjectForPictogramElement(componentShape);
		GraphicsAlgorithm ga = context.getGraphicsAlgorithm();
		//allow if we've selected Text for the component
		if (obj instanceof SadComponentInstantiation && ga instanceof Text) {
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
		ComponentShape componentShape = (ComponentShape)DUtil.findContainerShapeParentWithProperty(
				pe, RHContainerShapeImpl.SHAPE_outerContainerShape);
		SadComponentInstantiation ci = (SadComponentInstantiation) getBusinessObjectForPictogramElement(componentShape);
		return ci.getUsageName();
    }
	
	@Override
	public String checkValueValid(String value, IDirectEditingContext context){
		if (value.length() < 1){
			return "Please enter any text as class name.";
		}
		if (value.contains(" ")){
			return "Spaces are not allowed in class names.";
		}
		if (value.contains("\n")){
			return "Line breakes are not allowed in class names.";
		}
		// null means, that the value is valid
		return null;
	}
	
	@Override
	public void setValue(final String value, IDirectEditingContext context){
		PictogramElement pe = context.getPictogramElement();
		ComponentShape componentShape = (ComponentShape)DUtil.findContainerShapeParentWithProperty(
				pe, RHContainerShapeImpl.SHAPE_outerContainerShape);
		final SadComponentInstantiation ci = (SadComponentInstantiation) getBusinessObjectForPictogramElement(componentShape);
		
		//editing domain for our transaction
	    TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();
		
	    //Perform business object manipulation in a Command
	    TransactionalCommandStack stack = (TransactionalCommandStack)editingDomain.getCommandStack();
	    stack.execute(new RecordingCommand(editingDomain){
	    	@Override
	    	protected void doExecute() {
	    		//set usage name
	    		ci.setUsageName(value);
	    	}
	    });
	    
	    //perform update, redraw
	    updatePictogramElement(componentShape);
	    
	}

}
