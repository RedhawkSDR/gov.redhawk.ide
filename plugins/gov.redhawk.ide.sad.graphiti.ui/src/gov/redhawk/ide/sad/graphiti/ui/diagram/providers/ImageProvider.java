package gov.redhawk.ide.sad.graphiti.ui.diagram.providers;

import org.eclipse.graphiti.ui.platform.AbstractImageProvider;
import org.eclipse.graphiti.ui.platform.IImageProvider;

public class ImageProvider extends AbstractImageProvider implements IImageProvider {

	// The prefix for all identifiers of this image provider
    protected static final String PREFIX =
              "gov.redhawk.ide.sad.graphiti.ui.diagram.providers.imageProvider.";
 
    
    public static final String IMG_COMPONENT_PLACEMENT = PREFIX + "componentPlacement";
    public static final String IMG_CONNECTION = PREFIX + "connection";
    public static final String IMG_FIND_BY_NAMING_SERVICE = PREFIX + "findByNamingService";
    public static final String IMG_FIND_BY_DOMAIN = PREFIX + "findByDomain";
	
	public ImageProvider() {
	}

	// register the path for each image identifier
	@Override
	protected void addAvailableImages() {
		addImageFilePath(IMG_COMPONENT_PLACEMENT, "icons/full/obj16/ComponentPlacement.gif");
        addImageFilePath(IMG_CONNECTION, "icons/full/obj16/ConnectInterface.gif");
        addImageFilePath(IMG_FIND_BY_NAMING_SERVICE, "icons/full/obj16/NamingService.gif");
        addImageFilePath(IMG_FIND_BY_DOMAIN, "icons/full/obj16/DomainFinder.gif");
	}

}
