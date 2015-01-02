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
package gov.redhawk.ide.graphiti.dcd.internal.ui;

import gov.redhawk.model.sca.ScaDevice;
import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;

public class DcdNodeMapEntry {
	private String key;
	private ScaDevice< ? > device;
	private DcdComponentInstantiation profile;

	/**
	 * @param comp the comp to set
	 */
	public void setScaDevice(ScaDevice< ? > device) {
		this.device = device;
		setKey(device.getIdentifier());
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
	public ScaDevice< ? > getScaDevice() {
		return device;
	}

	/**
	 * @param profile the profile to set
	 */
	public void setProfile(DcdComponentInstantiation profile) {
		this.profile = profile;
		setKey(profile.getId());
	}

	/**
	 * @return the profile
	 */
	public DcdComponentInstantiation getProfile() {
		return profile;
	}

	public String getKey() {
		if (device != null) {
			return device.getIdentifier();
		} else if (profile != null) {
			return profile.getId();
		}
		return null;
	}

	public static String getKey(ScaDevice< ? > obj) {
		return obj.getIdentifier();
	}

	public static String getKey(DcdComponentInstantiation obj) {
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
		DcdNodeMapEntry other = (DcdNodeMapEntry) obj;
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
