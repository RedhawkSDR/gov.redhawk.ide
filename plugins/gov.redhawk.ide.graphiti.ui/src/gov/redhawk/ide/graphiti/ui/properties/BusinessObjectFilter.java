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

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.platform.AbstractPropertySectionFilter;

/**
 * 
 */
public class BusinessObjectFilter extends AbstractPropertySectionFilter {

	private final Class<?>[] boFilterClasses;
	
	private boolean shouldAllow;
	
	/**
	 * @param allow Whether filterClasses should be filtered in or out
	 */
	public BusinessObjectFilter(boolean allow, Class<?>... filterClasses) {
		Assert.isTrue(filterClasses.length > 0, "Must specify at least one Business Object class to filter");
		this.boFilterClasses = filterClasses;
		this.shouldAllow = allow;
	}

	/**
	 * Positive filter by default
	 */
	public BusinessObjectFilter(Class<?>... filterClass) {
		this(true, filterClass);
	}

	@Override
	protected boolean accept(PictogramElement pictogramElement) {
		EObject eObject =  Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pictogramElement);
		for (Class< ? > filterClass : this.boFilterClasses) {
			if (filterClass.isInstance(eObject)) {
				return this.shouldAllow;
			}
		}
		return !this.shouldAllow;
	}

}
