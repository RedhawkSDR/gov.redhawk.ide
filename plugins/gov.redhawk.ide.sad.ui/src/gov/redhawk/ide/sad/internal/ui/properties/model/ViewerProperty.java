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

import mil.jpeojtrs.sca.prf.AbstractProperty;
import mil.jpeojtrs.sca.sad.ExternalProperties;
import mil.jpeojtrs.sca.sad.ExternalProperty;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

/**
 * 
 */
public abstract class ViewerProperty< T extends AbstractProperty > {

	protected final T def;
	private Object parent;

	/**
	 * 
	 */
	public ViewerProperty(T def, Object parent) {
		this.def = def;
		this.parent = parent;
	}

	public Object getParent() {
		return parent;
	}

	public T getDefinition() {
		return this.def;
	}

	public abstract void setToDefault();

	public SadComponentInstantiation getComponentInstantiation() {
		Object element = getParent();
		while (element != null) {
			if (element instanceof ViewerComponent) {
				return ((ViewerComponent) element).getComponentInstantiation();
			} else if (element instanceof ViewerProperty< ? >) {
				element = ((ViewerProperty< ? >) element).getParent();
			}
		}
		return null;
	}

	public ExternalProperty getExternalProperty() {
		SadComponentInstantiation comp = getComponentInstantiation();
		SoftwareAssembly sad = ScaEcoreUtils.getEContainerOfType(comp, SoftwareAssembly.class);
		if (sad != null && comp != null) {
			ExternalProperties externalProps = sad.getExternalProperties();
			if (externalProps != null) {
				for (ExternalProperty eprop : externalProps.getProperties()) {
					if (eprop.getCompRefID().equals(comp.getId()) && eprop.getPropID().equals(getDefinition().getId())) {
						return eprop;
					}
				}
			}
		}
		return null;
	}
	
	public String getID() {
		return def.getId();
	}

}
