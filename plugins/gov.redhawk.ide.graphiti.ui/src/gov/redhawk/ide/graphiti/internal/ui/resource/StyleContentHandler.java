package gov.redhawk.ide.graphiti.internal.ui.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ContentHandlerImpl;

import gov.redhawk.ide.graphiti.ui.GraphitiUIPlugin;

public class StyleContentHandler extends ContentHandlerImpl {

	public static final String CONTENT_TYPE = "gov.redhawk.ide.graphiti.ui.style";

	@Override
	public boolean canHandle(URI uri) {
		return URI.createPlatformPluginURI(GraphitiUIPlugin.PLUGIN_ID + "/style", false).equals(uri);
	}

	@Override
	public Map<String, Object> contentDescription(URI uri, InputStream inputStream, Map< ? , ? > options, Map<Object, Object> context) throws IOException {
		Map<String, Object> description = super.contentDescription(uri, inputStream, options, context);
		description.put(CONTENT_TYPE_PROPERTY, CONTENT_TYPE);
		return description;
	}

}
