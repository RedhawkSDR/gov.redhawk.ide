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
package gov.redhawk.ide.spd.ui.tests;

import gov.redhawk.ide.spd.internal.ui.handlers.AddPortHandlerTests;
import gov.redhawk.ide.spd.internal.ui.handlers.EditPortHandlerTests;
import gov.redhawk.ide.spd.internal.ui.handlers.RemovePortsHandlerTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ AddPortHandlerTests.class, RemovePortsHandlerTests.class, EditPortHandlerTests.class })
public class SpdUiTestSuite {
}
