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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;

import gov.redhawk.ide.graphiti.ui.properties.AbstractRequirementsPropertySection;
import mil.jpeojtrs.sca.partitioning.Requirements;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

/**
 * @since 2.1
 */
public class SadRequirementsPropertySection extends AbstractRequirementsPropertySection {

	@Override
	protected Requirements getSelectionRequirements() {
		EObject eObj = getEObject();
		if (!(eObj instanceof SadComponentInstantiation)) {
			return null;
		}

		return ((SadComponentInstantiation) eObj).getDeviceRequires();
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		EObject eObj = getEObject();
		if (eObj instanceof SadComponentInstantiation) {
			SadComponentInstantiation compInst = (SadComponentInstantiation) eObj;
			getTreeViewer().setInput(compInst);
		}
	}
}
