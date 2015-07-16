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
package gov.redhawk.ide.sad.internal.ui.properties.model;

import java.util.Collection;

import org.eclipse.jface.viewers.ILabelProvider;

import mil.jpeojtrs.sca.prf.AbstractProperty;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;

public interface SadProperty {

	AbstractProperty getDefinition();

	boolean isAssemblyControllerProperty();

	String getExternalID();

	boolean canSetExternalId();

	void setExternalID(String newExternalID);

	Object getSadValue();

	void setSadValue(Object value);

	String getPrfValue();

	String getID();

	Collection< ? > getKinds();

	SadComponentInstantiation getComponentInstantiation();

	ILabelProvider getLabelProvider();
}
