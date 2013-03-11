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

/**
 * The listener interface for receiving ITargetPlatformChange events. The class
 * that is interested in processing a ITargetPlatformChange event implements
 * this interface, and the object created with that class is registered with a
 * component using the component's <code>addTargetPlatformChangeListener</code>
 * method. When the ITargetPlatformChange event occurs, that object's
 * appropriate method is invoked.
 * 
 * @see ITargetPlatformChangeEvent
 */
public interface ITargetPlatformChangeListener {

	/**
	 * Fired when the target platform changes.
	 * 
	 * @param event
	 *            the event.
	 */
	void onTargetPlatformChange(TargetPlatformChangeEvent event);
}
