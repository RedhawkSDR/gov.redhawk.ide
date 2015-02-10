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

import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;

/**
 * 
 */
public class BusinessObjectFilter extends AbstractPropertySectionFilter {

	private Class< ? > objectClass;
	
	private boolean shouldAllow;
	
	/**
	 * 
	 * @param filterClass
	 * @param allow Whether filterClass should be filtered in or out
	 */
	public BusinessObjectFilter(Class< ? > filterClass, boolean allow) {
		objectClass = filterClass;
		shouldAllow = allow;
	}

	/**
	 * Positive filter by default
	 * @param filterClass
	 */
	public BusinessObjectFilter(Class< ? > filterClass) {
		this(filterClass, true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter#accept(org.eclipse.graphiti.mm.pictograms.PictogramElement)
	 */
	@Override
	protected boolean accept(PictogramElement pictogramElement) {
		if (objectClass.isInstance(pictogramElement.getLink().getBusinessObjects().get(0))) {
			return shouldAllow;
		}
		return !shouldAllow;
	}

}
