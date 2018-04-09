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
package gov.redhawk.ide.graphiti.sad.ui.properties;

import org.eclipse.core.runtime.Platform;
import org.eclipse.graphiti.ui.platform.GraphitiShapeEditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AdvancedPropertySection;

import mil.jpeojtrs.sca.sad.SoftwareAssembly;

/**
 * @since 2.1
 */
public class SoftwareAssemblySection extends AdvancedPropertySection {

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		ISelection newSelection = selection;
		if (selection instanceof StructuredSelection) {
			StructuredSelection ss = (StructuredSelection) selection;
			Object obj = ss.getFirstElement();
			if (obj instanceof GraphitiShapeEditPart) {
				obj = Platform.getAdapterManager().getAdapter(obj, SoftwareAssembly.class);
			}
			newSelection = new StructuredSelection(new Object[] { obj });
		}
		super.setInput(part, newSelection);
	}
}
