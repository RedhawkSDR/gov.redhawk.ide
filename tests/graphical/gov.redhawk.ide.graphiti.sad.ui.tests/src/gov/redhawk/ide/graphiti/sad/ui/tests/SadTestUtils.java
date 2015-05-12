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
package gov.redhawk.ide.graphiti.sad.ui.tests;

import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import mil.jpeojtrs.sca.partitioning.UsesDeviceStub;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.junit.Assert;

public class SadTestUtils {
	
	public static final String USE_DEVICE = "Use Device";
	public static final String USE_FRONTEND_TUNER_DEVICE = "Use FrontEnd Tuner Device";

	private SadTestUtils() {
	}

	/**
	 * Assert UsesDevice
	 * @param gefEditPart
	 */
	public static void assertUsesDevice(SWTBotGefEditPart gefEditPart) {
		Assert.assertNotNull(gefEditPart);
		// Drill down to graphiti component shape
		RHContainerShapeImpl rhContainerShape = (RHContainerShapeImpl) gefEditPart.part().getModel();

		// Grab the associated business object and confirm it is a UsesDeviceStub
		Object bo = DUtil.getBusinessObject(rhContainerShape);
		Assert.assertTrue("business object should be of type UsesDeviceStub", bo instanceof UsesDeviceStub);
		UsesDeviceStub usesDeviceStub = (UsesDeviceStub) bo;

		// Run assertions on expected properties
		Assert.assertEquals("outer text should match shape type", USE_DEVICE, rhContainerShape.getOuterText().getValue());
		Assert.assertEquals("inner text should match usesdevice id", usesDeviceStub.getUsesDevice().getId(), rhContainerShape.getInnerText().getValue());
		Assert.assertNotNull("component supported interface graphic should not be null", rhContainerShape.getLollipop());

	}
	
}
