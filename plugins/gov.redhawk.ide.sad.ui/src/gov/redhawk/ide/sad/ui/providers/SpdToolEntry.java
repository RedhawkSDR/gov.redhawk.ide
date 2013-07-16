/*******************************************************************************
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package gov.redhawk.ide.sad.ui.providers;

import gov.redhawk.diagram.edit.helpers.ComponentPlacementEditHelperAdvice;

import java.text.MessageFormat;
import java.util.HashMap;

import mil.jpeojtrs.sca.sad.diagram.providers.SadElementTypes;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.Request;
import org.eclipse.gef.Tool;
import org.eclipse.gmf.runtime.diagram.ui.internal.services.palette.PaletteToolEntry;
import org.eclipse.gmf.runtime.diagram.ui.tools.CreationTool;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * @since 4.1
 */
public class SpdToolEntry extends PaletteToolEntry {

	public static final String TOOL_PREFIX = "spdTool.";

	private final IElementType elementType;
	private final URI spdUri;

	private String spdId;

	private String implID;

	/**
	 * @since 5.0
	 */
	public SpdToolEntry(String name, String description, URI spdURI, String id, String implID) {
		super(null, name, null);
		if (description == null) {
			description = MessageFormat.format("Create a new instance of the component \"{0}\".", name);
		}
		this.setDescription(description);
		this.elementType = SadElementTypes.SadComponentPlacement_3001;
		this.spdUri = spdURI;
		if (Display.getCurrent() == null) { 
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

				@Override
				public void run() {
					setSmallIcon(SadElementTypes.getImageDescriptor(SadElementTypes.SadComponentPlacement_3001));
				}
				
			});
		} else {
			setSmallIcon(SadElementTypes.getImageDescriptor(SadElementTypes.SadComponentPlacement_3001));
		}
		
		setLargeIcon(getSmallIcon());

		this.spdId = id;
		this.implID = implID;
		if (implID == null) {
			setId(SpdToolEntry.TOOL_PREFIX + id);
		} else {
			setId(SpdToolEntry.TOOL_PREFIX + id + ":" + implID);
		}
	}

	public SpdToolEntry(final SoftPkg spd) {
		this(spd.getName(), spd.getDescription(), EcoreUtil.getURI(spd), spd.getId(), null);
	}

	@Override
	public Tool createTool() {
		final Tool tool = new CreationTool(this.elementType) {
			@SuppressWarnings("unchecked")
			@Override
			protected Request createTargetRequest() {
				final Request retVal = super.createTargetRequest();
				final HashMap<Object, Object> map = new HashMap<Object, Object>();
				map.putAll(retVal.getExtendedData());
				map.put(ComponentPlacementEditHelperAdvice.CONFIGURE_OPTIONS_SPD_URI, SpdToolEntry.this.spdUri);
				if (implID != null) {
					map.put(ComponentPlacementEditHelperAdvice.CONFIGURE_OPTIONS_IMPL_ID, SpdToolEntry.this.implID);
				}
				retVal.setExtendedData(map);
				return retVal;
			}
		};
		tool.setProperties(getToolProperties());
		return tool;
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
