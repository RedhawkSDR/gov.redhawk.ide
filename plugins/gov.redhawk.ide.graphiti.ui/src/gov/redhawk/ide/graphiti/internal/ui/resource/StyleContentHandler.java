package gov.redhawk.ide.graphiti.internal.ui.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ContentHandlerImpl;

public class StyleContentHandler extends ContentHandlerImpl {

	@Override
	public boolean canHandle(URI uri) {
		return URI.createURI("http://www.redhawk.gov/model/rhstyle/1.0.0").equals(uri);
	}

	@Override
	public Map<String, Object> contentDescription(URI uri, InputStream inputStream, Map< ? , ? > options, Map<Object, Object> context) throws IOException {
		Map<String, Object> description = super.contentDescription(uri, inputStream, options, context);
		description.put(CONTENT_TYPE_PROPERTY, "http://www.redhawk.gov/model/rhstyle/1.0.0");
		return description;
	}

}
