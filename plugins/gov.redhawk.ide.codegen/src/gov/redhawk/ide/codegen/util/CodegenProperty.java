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
package gov.redhawk.ide.codegen.util;

/**
 * Provides language-specific access to property attributes that need to be written to file during code generation.
 * 
 * @since 9.0
 */
public interface CodegenProperty {

	/**
	 * Returns the property id.
	 * 
	 * @return the String representation of the Id in the implementing language
	 */
	String getId();

	/**
	 * Returns the property name.
	 * 	 
	 * @return the String representation of the name in the implementing language
	 */
	String getName();

	/**
	 * Returns the property type.
	 *  	 
	 * @return the String representation of the type in the implementing language
	 */
	String getType();

	/**
	 * Returns the property value.
	 * 
	 * @return the String representation of the value in the implementing language
	 */
	String getValue();

	/**
	 * Returns the property description.
	 * 
	 * @return the String representation of the description in the implementing language	 
	 */
	String getDescription();

	/**
	 * Returns the property mode.
	 * 
	 * @return the String representation of the mode in the implementing language	 
	 */
	String getMode();

	/**
	 * Returns the property action.
	 * 
	 * @return the String representation of the action in the implementing language	 
	 */
	String getAction();

	/**
	 * Returns the kind.
	 * 
	 * @return the String representation of the kind for the implementing language	 
	 */
	String getKind();

	/**
	 * Returns the kind values.
	 * 
	 * @return the String representation of the kinds for the implementing language	 
	 */
	String [] getKindValues();
}
