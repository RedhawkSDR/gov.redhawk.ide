package gov.redhawk.ide.graphiti.internal.ui.resource;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.graphiti.mm.algorithms.styles.Style;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramsFactory;

import gov.redhawk.ide.graphiti.ui.GraphitiUIPlugin;
import gov.redhawk.ide.graphiti.ui.diagram.util.StyleUtil;

class StyleResource extends ResourceImpl {

	private Diagram diagram;

	public StyleResource() {
		this(URI.createPlatformPluginURI(GraphitiUIPlugin.PLUGIN_ID + "/style", false));
	}

	public StyleResource(URI uri) {
		super(uri);
		diagram = PictogramsFactory.eINSTANCE.createDiagram();
		StyleUtil.createAllStyles(diagram);
		getContents().add(diagram);
	}

	@Override
	public String getURIFragment(EObject eObject) {
		if (eObject instanceof Style) {
			return ((Style) eObject).getId();
		}
		return super.getURIFragment(eObject);
	}

	@Override
	public EObject getEObject(String uriFragment) {
		return StyleUtil.findStyle(diagram, uriFragment);
	}
}