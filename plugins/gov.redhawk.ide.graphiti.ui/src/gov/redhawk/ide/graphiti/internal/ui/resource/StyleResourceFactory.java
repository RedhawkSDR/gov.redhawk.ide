package gov.redhawk.ide.graphiti.internal.ui.resource;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Factory;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramsFactory;

import gov.redhawk.ide.graphiti.ui.GraphitiUIPlugin;
import gov.redhawk.ide.graphiti.ui.diagram.util.StyleUtil;

public class StyleResourceFactory implements Factory {

	@Override
	public Resource createResource(URI uri) {
		return StyleResourceFactory.create(uri);
	}

	public static Resource createResource() {
		return StyleResourceFactory.create(URI.createPlatformPluginURI(GraphitiUIPlugin.PLUGIN_ID + "/style", false));
	}

	private static Resource create(URI uri) {
		Resource styleResource = new ResourceImpl(uri);
		Diagram diagram = PictogramsFactory.eINSTANCE.createDiagram();
		StyleUtil.createAllStyles(diagram);
		styleResource.getContents().add(diagram);
		return styleResource;		
	}
}
