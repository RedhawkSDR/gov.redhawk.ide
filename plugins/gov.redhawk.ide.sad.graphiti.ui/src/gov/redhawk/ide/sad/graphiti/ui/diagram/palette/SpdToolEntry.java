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
package gov.redhawk.ide.sad.graphiti.ui.diagram.palette;

import gov.redhawk.ide.sad.graphiti.ui.diagram.providers.ImageProvider;

import java.text.MessageFormat;

import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.palette.impl.ObjectCreationToolEntry;

public class SpdToolEntry extends ObjectCreationToolEntry {

	public static final String TOOL_PREFIX = "spdTool.";

	// private final IElementType elementType;
	private final URI spdUri;

	private String spdId;

	private String implID;

	public SpdToolEntry(String label, String description, URI spdURI, String id, String implID, String iconId, ICreateFeature createFeature) {

		super(label, description, iconId, null, createFeature);
		if (description == null) {
			description = MessageFormat.format("Create a new instance of the component \"{0}\".", label);
		}

		// this.elementType = SadElementTypes.SadComponentPlacement_3001;
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
	public SpdToolEntry(final SoftPkg spd, String iconId) {
		this(spd.getName(), spd.getDescription(), EcoreUtil.getURI(spd), spd.getId(), null, iconId, null);
	}

	public SpdToolEntry(final SoftPkg spd, ICreateFeature createFeature) {
		this(spd.getName(), spd.getDescription(), EcoreUtil.getURI(spd), spd.getId(), null, ImageProvider.IMG_COMPONENT_PLACEMENT, createFeature);
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
