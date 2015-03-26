/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.graphiti.dcd.ui.runtime.domain.tests;

import gov.redhawk.ide.swtbot.NodeUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.junit.Assert;
import org.junit.Test;

public class DomainNodeTest extends AbstractGraphitiDomainNodeRuntimeTest {

	/**
	 * Test launching a node in a running domain.
	 * IDE-1187 - Node should include namespaced devices
	 */
	@Test
	public void launchNodeInDomain() {
		String device1 = "device_1";

		NodeUtils.launchNodeInDomain(bot, DOMAIN, NAMESPACE_DEVICE_MANAGER);
		setNodeFullName(ScaExplorerTestUtils.getFullNameFromScaExplorer(gefBot, DOMAIN_NODE_PARENT_PATH, NAMESPACE_DEVICE_MANAGER));
		SWTBotGefEditor editor = gefBot.gefEditor(getNodeFullName());

		Assert.assertNotNull("GPP should be displayed in diagram", editor.getEditPart("GPP"));
		Assert.assertNotNull("Namespaced device should be displayed in diagram", editor.getEditPart(device1));
	}

}
