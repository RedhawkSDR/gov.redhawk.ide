/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.swtbot.condition;

import org.eclipse.swt.graphics.RGBA;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;

/**
 * Tests for a specific color ({@link RGBA}).
 */
public abstract class WaitForColor extends DefaultCondition {

	private RGBA desiredColor;
	private RGBA lastSeenColor;

	/**
	 * @param desiredColor The color that indicates a successful test
	 */
	public WaitForColor(RGBA desiredColor) {
		this.desiredColor = desiredColor;
		this.lastSeenColor = null;
	}

	@Override
	public boolean test() throws Exception {
		lastSeenColor = getColor();
		return desiredColor.equals(lastSeenColor);
	}

	/**
	 * @return The color being tested
	 */
	protected abstract RGBA getColor();

	@Override
	public String getFailureMessage() {
		return "Expected color " + desiredColor + " but saw " + lastSeenColor;
	}

}
