package gov.redhawk.ide.sad.graphiti.features.add;

import gov.redhawk.ide.sad.graphiti.util.PropertyUtil;
import gov.redhawk.ide.sad.graphiti.util.SaveUtil;
import gov.redhawk.ide.sad.graphiti.util.StyleUtil;
import gov.redhawk.ide.sad.ui.SadUiActivator;

import java.io.IOException;

import mil.jpeojtrs.sca.partitioning.ComponentFile;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddShapeFeature;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;

public class AddComponentShapeFeature extends AbstractAddShapeFeature{

	
	//TODO: bwhoff2 we might not really be adding a component, but instead were adding a SoftPkg
	
	public AddComponentShapeFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canAdd(IAddContext context) {
		
		//are we adding a ComponentFile
		if(context.getNewObject() instanceof ComponentFile){
			
			//are we adding to diagram
			if(context.getTargetContainer() instanceof Diagram){
				return true;
			}
		}
		
		return false;
	}

	@Override
	public PictogramElement add(IAddContext context) {
		
		ComponentFile newComponentFile = (ComponentFile)context.getNewObject();
		
		Diagram targetDiagram = (Diagram)context.getTargetContainer();
		
		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		ContainerShape componentContainerShape = (ContainerShape)peCreateService.createContainerShape(targetDiagram, true);
		
		//distinguish shape
		PropertyUtil.setComponentShape(componentContainerShape);
		
		
		IGaService gaService = Graphiti.getGaService();
		
		
		
		RoundedRectangle roundedRectangle = gaService.createRoundedRectangle(componentContainerShape, 5, 5);
		roundedRectangle.setStyle(StyleUtil.getStyleForComponent(getDiagram()));
		gaService.setLocationAndSize(roundedRectangle, context.getX(), context.getY(), 10, 10);
		
		//usually resource will be null, add it to diagram and model file
		if(newComponentFile.eResource() == null){
			try{
				SoftwareAssembly softwareAssembly = SaveUtil.getModelFileInStoredResource(getDiagram());
				softwareAssembly.getComponentFiles().getComponentFile().add(newComponentFile);
				softwareAssembly.eResource().save(null);
			} catch(IOException e){
				SadUiActivator.getDefault().getLog().log(new Status(IStatus.ERROR, SadUiActivator.PLUGIN_ID, 
						"Error saving changes to model file after adding component", e));
			}
		}
		
		//associate business object(s). Add hard coded proc if found
		
		return null;
	}

}
