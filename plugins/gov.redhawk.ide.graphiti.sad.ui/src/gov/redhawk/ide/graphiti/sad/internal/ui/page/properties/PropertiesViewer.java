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
package gov.redhawk.ide.graphiti.sad.internal.ui.page.properties;

import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerTextFilter;
import org.eclipse.swt.widgets.Composite;

public class PropertiesViewer extends XViewer {

	/**
	 * @param parent
	 * @param style
	 * @param xViewerFactory
	 */
	public PropertiesViewer(Composite parent, int style) {
		super(parent, style, new PropertiesViewerFactory());
		setAutoExpandLevel(2);
	}
	
	@Override
	public XViewerTextFilter getXViewerTextFilter() {
		return new PropertiesViewerFilter(this);
	}

}
