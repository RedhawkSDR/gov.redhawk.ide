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
package gov.redhawk.ide.graphiti.ui.diagram.patterns;

import java.util.List;

import mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.pattern.AbstractPattern;
import org.eclipse.graphiti.pattern.config.IPatternConfiguration;

public abstract class AbstractContainerPattern extends AbstractPattern {
	
	public AbstractContainerPattern(IPatternConfiguration patternConfiguration) {
		super(null);
	}
	
	/**
	 * Unless overridden, this method ensures consistency of the validation logic 
	 * and associated error messages for Component, FindBy, and HostCollocation names.
	 */
	@Override
	public String checkValueValid(String value, IDirectEditingContext context) {
		return validate(getCheckValueValidName(), value);
	}
	
	/**
	 * If <code>getCreateName</code> does not return the desired name for an error
	 * message, this method can be overridden.
	 */
	protected String getCheckValueValidName() {
		return getCreateName();
	}
	
	@Override
	public abstract void setValue(final String value, IDirectEditingContext context);
	
	/**
	 * Checks to see if the given String <code>value</code> is valid. Returns an error
	 * message if invalid and <code>null</code> if valid. 
	 * @param valueType - should be capitalized (e.g. Component, Host Collocation, etc.)
	 * @param value
	 * @return error message
	 */
	public static String validate(String valueType, String value) {
		if (value.length() < 1) {
			return valueType + " Name must not be empty";
		}
		if (value.contains(" ")) {
			return valueType + " Name must not include spaces";
		}
		if (value.contains("\n")) {
			return valueType + " Name must not include line breaks";
		}
		// null means, that the value is valid
		return null;
	}

	/**
	 * Provides the title of the outer shape
	 * @param findByStub
	 * @return
	 */
	public String getOuterTitle(EObject obj) {
		return null;
	}
	
	/**
	 * Provides the title of the inner shape
	 * @param findByStub
	 * @return
	 */
	public abstract String getInnerTitle(EObject obj);

	/**
	 * Provides interface (if applicable)
	 * @param obj
	 * @return
	 */
	public ComponentSupportedInterfaceStub getInterface(EObject obj) {
		return null;
	}
	
	/**
	 * Provides outer image ID for graphical representation
	 * @param
	 * @return
	 */
	public String getOuterImageId() {
		return null;
	}
	
	/**
	 * Provides inner image ID for graphical representation
	 * @param
	 * @return
	 */
	public String getInnerImageId() {
		return null;
	}
	
	/**
	 * Provides outer container style for graphical representation
	 * @param
	 * @return
	 */
	public String getStyleForOuter() {
		return null;
	}
	
	/**
	 * Provides inner container style for graphical representation
	 * @param
	 * @return
	 */
	public String getStyleForInner() {
		return null;
	}
	
	/**
	 * Returns business objects that should be linked to shape
	 */
	public List<EObject> getBusinessObjectsToLink(EObject obj) {
		return null;
	}
	
}
