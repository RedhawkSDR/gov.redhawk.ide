package gov.redhawk.ide.sad.graphiti.ui.diagram.palette;

import java.text.MessageFormat;

import mil.jpeojtrs.sca.sad.diagram.providers.SadElementTypes;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.palette.impl.ObjectCreationToolEntry;
import org.eclipse.jface.resource.ImageDescriptor;

public class SpdToolEntry extends ObjectCreationToolEntry{

	public static final String TOOL_PREFIX = "spdTool.";

	private final IElementType elementType;
	private final URI spdUri;

	private String spdId;

	private String implID;
	
	/**
	 * @since 5.0
	 */
	public static ImageDescriptor getDefaultIcon() {
		return SadElementTypes.getImageDescriptor(SadElementTypes.SadComponentPlacement_3001);
	}
	
	/**
	 * @since 5.0
	 */
	public SpdToolEntry(String label, String description, URI spdURI, String id, String implID, ImageDescriptor icon, ICreateFeature createFeature) {
		
		super(label, description, null, null, createFeature);
		if (description == null) {
			description = MessageFormat.format("Create a new instance of the component \"{0}\".", label);
		}
		
		this.elementType = SadElementTypes.SadComponentPlacement_3001;
		this.spdUri = spdURI;
		

		this.spdId = id;
		this.implID = implID;
//		if (implID == null) {
//			setId(SpdToolEntry.TOOL_PREFIX + id);
//		} else {
//			setId(SpdToolEntry.TOOL_PREFIX + id + ":" + implID);
//		}
	}

	/**
	 * @since 5.0
	 */
	public SpdToolEntry(final SoftPkg spd, ImageDescriptor icon) {
		this(spd.getName(), spd.getDescription(), EcoreUtil.getURI(spd), spd.getId(), null, icon, null);
	}
	
	public SpdToolEntry(final SoftPkg spd, ICreateFeature createFeature) {
		this(spd.getName(), spd.getDescription(), EcoreUtil.getURI(spd), spd.getId(), null, getDefaultIcon(), createFeature);
	}

	/**
	 * @since 5.0
	 */
	public String getImplID() {
		return implID;
	}

	/**
	 * @since 5.0
	 */
	public String getSpdID() {
		return spdId;
	}

}
