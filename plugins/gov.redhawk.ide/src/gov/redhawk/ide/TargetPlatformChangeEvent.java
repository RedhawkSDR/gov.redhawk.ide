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
package gov.redhawk.ide;

// TODO: Auto-generated Javadoc
/**
 * The Class TargetPlatformChangeEvent.
 */
public class TargetPlatformChangeEvent {

	/** The old location. */
	private final String oldLocation;

	/** The new location. */
	private final String newLocation;

	/**
	 * Instantiates a new target platform change event.
	 * 
	 * @param oldLocation
	 *            the old location
	 * @param newLocation
	 *            the new location
	 */
	public TargetPlatformChangeEvent(final String oldLocation,
			final String newLocation) {
		super();
		this.oldLocation = oldLocation;
		this.newLocation = newLocation;
	}

	/**
	 * Gets the old location.
	 * 
	 * @return the old location
	 */
	public String getOldLocation() {
		return this.oldLocation;
	}

	/**
	 * Gets the new location.
	 * 
	 * @return the new location
	 */
	public String getNewLocation() {
		return this.newLocation;
	}

}
