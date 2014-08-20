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
package gov.redhawk.ide.debug.internal.ui.diagram;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import gov.redhawk.ide.debug.LocalScaComponent;

public class NodeMapEntry {
	private String key;
	private LocalScaComponent comp;
	private SadComponentInstantiation profile;

	/**
	 * @param comp the comp to set
	 */
	public void setLocalScaComponent(@NonNull LocalScaComponent comp) {
		this.comp = comp;
		setKey(comp.getInstantiationIdentifier());
	}

	/**
	 * @param key
	 */
	private void setKey(String key) {
		if (this.key == null) {
			this.key = key;
		}
	}

	/**
	 * @return the comp
	 */
	public LocalScaComponent getLocalScaComponent() {
		return comp;
	}

	/**
	 * @param profile the profile to set
	 */
	public void setProfile(@NonNull SadComponentInstantiation profile) {
		this.profile = profile;
		setKey(profile.getId());
	}

	/**
	 * @return the profile
	 */
	public SadComponentInstantiation getProfile() {
		return profile;
	}

	@Nullable
	public String getKey() {
		if (comp != null) {
			return comp.getInstantiationIdentifier();
		} else if (profile != null) {
			return profile.getId();
		}
		return null;
	}

	public static String getKey(LocalScaComponent obj) {
		return obj.getInstantiationIdentifier();
	}

	public static String getKey(SadComponentInstantiation obj) {
		return obj.getId();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		NodeMapEntry other = (NodeMapEntry) obj;
		if (key == null) {
			if (other.key != null) {
				return false;
			}
		} else if (!key.equals(other.key)) {
			return false;
		}
		return true;
	}

}
