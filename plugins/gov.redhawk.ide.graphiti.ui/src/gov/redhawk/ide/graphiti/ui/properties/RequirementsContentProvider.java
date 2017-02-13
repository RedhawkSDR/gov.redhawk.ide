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
package gov.redhawk.ide.graphiti.ui.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;

import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;
import mil.jpeojtrs.sca.partitioning.Requirements;
import mil.jpeojtrs.sca.partitioning.Requires;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

public class RequirementsContentProvider implements ITreeContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		Requirements requirements = null;
		if (inputElement instanceof SadComponentInstantiation) {
			requirements = ((SadComponentInstantiation) inputElement).getDeviceRequires();
		} else if (inputElement instanceof DcdComponentInstantiation) {
			requirements = ((DcdComponentInstantiation) inputElement).getDeployerRequires();
		}

		if (requirements == null) {
			return new Object[0];
		}

		List<Object> elements = new ArrayList<>();
		for (Requires requires : requirements.getRequires()) {
			elements.add(requires);
		}
		return elements.toArray(new Object[0]);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return false;
	}

}
