package gov.redhawk.ide.graphiti.ui.diagram.providers;

import org.eclipse.graphiti.ui.platform.AbstractImageProvider;
import org.eclipse.graphiti.ui.platform.IImageProvider;

public class ImageProvider extends AbstractImageProvider implements IImageProvider {

	// The prefix for all identifiers of this image provider
	protected static final String PREFIX = "gov.redhawk.ide.graphiti.ui.diagram.providers.imageProvider.";

	public static final String IMG_FIND_BY_CORBA_NAME = PREFIX + "findByCORBAName";
	public static final String IMG_FIND_BY_SERVICE = PREFIX + "findByService";
	public static final String IMG_FIND_BY_DOMAIN_MANAGER = PREFIX + "findByDomainManager";
	public static final String IMG_FIND_BY_FILE_MANAGER = PREFIX + "findByFileManager";
	public static final String IMG_FIND_BY_DOMAIN = PREFIX + "findByDomain";
	public static final String IMG_FIND_BY = PREFIX + "findBy";
	
	@Override
	protected void addAvailableImages() {
		
		addImageFilePath(IMG_FIND_BY_CORBA_NAME, "icons/full/obj16/NamingService.gif");
		addImageFilePath(IMG_FIND_BY_DOMAIN_MANAGER, "icons/full/obj16/DomainFinder.gif");
		addImageFilePath(IMG_FIND_BY_FILE_MANAGER, "icons/full/obj16/DomainFinder.gif");
		addImageFilePath(IMG_FIND_BY_DOMAIN, "icons/full/obj16/DomainFinder.gif");
		addImageFilePath(IMG_FIND_BY_SERVICE, "icons/full/obj16/DomainFinder.gif");
		addImageFilePath(IMG_FIND_BY, "icons/full/obj16/FindBy.gif");
		
	}

}
