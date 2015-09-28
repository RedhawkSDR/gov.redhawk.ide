package gov.redhawk.ide.graphiti.internal.ui.resource;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Factory;

public class StyleResourceFactory implements Factory {

	@Override
	public Resource createResource(URI uri) {
		return new StyleResource(uri);
	}

	public static Resource createResource() {
		return new StyleResource();
	}
}
