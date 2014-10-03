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
package gov.redhawk.ide.swtbot.diagram;

import gov.redhawk.ide.swtbot.UITest;

import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.junit.Before;

/**
 * 
 */
public abstract class AbstractGraphitiTest extends UITest {

	protected SWTGefBot gefBot; // SUPPRESS CHECKSTYLE INLINE


	@Before
	public void beforeTest() throws Exception {
		gefBot = new SWTGefBot();
		super.before();
	}

}
