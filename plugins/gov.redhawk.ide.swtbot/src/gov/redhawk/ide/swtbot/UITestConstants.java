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
package gov.redhawk.ide.swtbot;

/**
 * The Class UITestConstants.
 * 
 */
public final class UITestConstants {

	/** Cell Editor activation type (single mouseClick activation). */
	public static final int MOUSE_CLICK_ACTIVATION = 0;

	/** Cell Editor activation type (mouse doubleClick activation). */
	public static final int MOUSE_DOUBLE_CLICK_ACTIVATION = 1;

	/** Cell Editor activation type (activation by pressing "F2" key). */
	public static final int F2_KEY_ACTIVATION = 2;

	/** Cell Editor. */
	public static final int DEFAULT_CELL_EDITOR_TIMEOUT = 1000;

	private UITestConstants() {
	}

}
