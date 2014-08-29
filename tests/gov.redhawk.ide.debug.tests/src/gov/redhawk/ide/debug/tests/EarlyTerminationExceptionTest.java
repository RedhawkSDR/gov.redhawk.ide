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
package gov.redhawk.ide.debug.tests;

import java.util.regex.Pattern;

import gov.redhawk.ide.debug.EarlyTerminationException;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 */
public class EarlyTerminationExceptionTest {

	@Test
	public void testMessage() {
		Pattern unixCodeMsgPattern = Pattern.compile("Terminated with exit code \\w+ \\(\\d+\\)");
		Pattern codeMsgPattern = Pattern.compile("Terminated with exit code \\d+");
		for (int i = 1; i < 255; i++) {
			String msg = EarlyTerminationException.getExitCodeMessage(i);
			Assert.assertTrue("Invalid returned message " + msg, unixCodeMsgPattern.matcher(msg).matches() || codeMsgPattern.matcher(msg).matches());
		}
	}

}
