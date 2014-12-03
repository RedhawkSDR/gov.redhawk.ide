package gov.redhawk.ide.graphiti.ui.diagram.providers;

import org.eclipse.graphiti.ui.platform.AbstractImageProvider;
import org.eclipse.graphiti.ui.platform.IImageProvider;

public class ImageProvider extends AbstractImageProvider implements IImageProvider {

	// The prefix for all identifiers of this image provider
	protected static final String PREFIX = "gov.redhawk.ide.graphiti.ui.diagram.providers.imageProvider.";
		
	public static final String IMG_FIND_BY = PREFIX + "findBy";
	
	@Override
	protected void addAvailableImages() {
		
		addImageFilePath(IMG_FIND_BY, "icons/full/obj16/FindBy.gif");
		
	}

}
