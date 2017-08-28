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

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.widgets.xviewer.XViewerTextFilter;

public class PropertiesViewerFilter extends XViewerTextFilter {

	public PropertiesViewerFilter(PropertiesViewer xViewer) {
		super(xViewer);
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		ITreeContentProvider cp = (ITreeContentProvider) this.xViewer.getContentProvider();
		boolean retVal = super.select(viewer, parentElement, element);
		if (retVal) {
			return true;
		}
		if (cp.hasChildren(element)) {
			for (Object child : cp.getChildren(element)) {
				retVal = select(viewer, element, child);
				if (retVal) {
					break;
				}
			}
		}
		return retVal;
	}

}
