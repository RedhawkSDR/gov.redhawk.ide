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
package gov.redhawk.ide.graphiti.dcd.internal.ui.editor;

import gov.redhawk.ide.graphiti.dcd.ui.DCDUIGraphitiPlugin;

import org.eclipse.core.runtime.QualifiedName;

public final class ScaIdeConstants {

	public static final String PLUGIN_DOC_ROOT = "/gov.redhawk.ide.dcd.doc.user/"; //$NON-NLS-1$
	public static final QualifiedName PROPERTY_EDITOR_PAGE_KEY = new QualifiedName(DCDUIGraphitiPlugin.PLUGIN_ID, "editor-page-key"); //$NON-NLS-1$

	private ScaIdeConstants() {

	}
}
