package gov.redhawk.ide.sad.graphiti.ui.diagram.util;

import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.mm.GraphicsAlgorithmContainer;
import org.eclipse.graphiti.mm.Property;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.util.IColorConstant;

public class DiagramUtil {

	/**
	 * Returns the SoftwareAssembly for the provided diagram
	 * @param featureProvider
	 * @param diagram
	 * @return
	 */
	public static SoftwareAssembly getDiagramSAD(IFeatureProvider featureProvider, Diagram diagram){
		
		//NOTE to developer
		//You must use the same transactionalEditingDomain and associated resourceSet if you want save/undo/redo to work
		//properly.  The Graphiti editor will try saving the resourceSet and therefore we want our model to be in the same resourceSet.
		//The editingDomain below isn't associated with Graphiti model and so it doesn't save the model when the diagram editor saves.
		//TransactionalEditingDomain editingDomain = TransactionalEditingDomain.Registry.INSTANCE.getEditingDomain(ScaPlugin.EDITING_DOMAIN_ID);
		TransactionalEditingDomain editingDomain = featureProvider.getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();
		ResourceSet resourceSet = editingDomain.getResourceSet();
		
		URI uri = diagram.eResource().getURI();
		uri = uri.trimFragment().trimFileExtension().appendFileExtension("sad.xml");
		
		SoftwareAssembly sad = SoftwareAssembly.Util.getSoftwareAssembly(resourceSet.getResource(uri, true));
		
		return sad;
	}

	public static Polyline createArrow(GraphicsAlgorithmContainer gaContainer, IFeatureProvider featureProvider, Color color){
		IGaService gaService = Graphiti.getGaService();
		Polyline polyline = gaService.createPolyline(gaContainer, new int[] {-15,10,0,0,-15,-10 });
		polyline.setForeground(Graphiti.getGaService().manageColor(featureProvider.getDiagramTypeProvider().getDiagram(), IColorConstant.BLACK));
		polyline.setLineWidth(2);
		polyline.setForeground(color);
		return polyline;
	}
	
	
	/**
	 * Returns true if the provided context contains a pictogram element with one of the provided property values.
	 * False otherwise.
	 * @param context
	 * @param propertyKeys
	 * @return
	 */
	public static boolean doesPictogramContainProperty(PictogramElement pe, String[] propertyValues){
		if(pe != null && pe.getProperties() != null){
			for(Property p: pe.getProperties()){
				for(String propValue: propertyValues){
					if(p.getValue().equals(propValue)){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Returns true if the provided context contains a pictogram element with one of the provided property values.
	 * False otherwise.
	 * @param context
	 * @param propertyKeys
	 * @return
	 */
	public static boolean doesPictogramContainProperty(IPictogramElementContext context, String[] propertyValues){
		PictogramElement pe = context.getPictogramElement();
		return doesPictogramContainProperty(pe, propertyValues);
	}
}
