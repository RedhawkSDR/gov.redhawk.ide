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
package gov.redhawk.ide.internal.ui.event;

import org.eclipse.nebula.widgets.xviewer.IXViewerFactory;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;

public class EventViewer extends XViewer {

	public EventViewer(Tree tree, IXViewerFactory xViewerFactory) {
		super(tree, xViewerFactory);
		// TODO Auto-generated constructor stub
	}

	public EventViewer(Composite parent, int style, IXViewerFactory xViewerFactory) {
		super(parent, style, xViewerFactory);
		// TODO Auto-generated constructor stub
	}

	public EventViewer(Tree tree, IXViewerFactory xViewerFactory, boolean filterRealTime, boolean searchRealTime) {
		super(tree, xViewerFactory, filterRealTime, searchRealTime);
		// TODO Auto-generated constructor stub
	}

	public EventViewer(Composite parent, int style, IXViewerFactory xViewerFactory, boolean filterRealTime, boolean searchRealTime) {
		super(parent, style, xViewerFactory, filterRealTime, searchRealTime);
		// TODO Auto-generated constructor stub
	}

}
